package co.edu.unicauca.microkernel.app;

import co.edu.unicauca.microkernel.common.entities.Question;
import co.edu.unicauca.microkernel.common.entities.QuestionRequest;
import co.edu.unicauca.microkernel.core.QuestionMicrokernel;
import co.edu.unicauca.microkernel.plugins.CaseQuestionPlugin;
import co.edu.unicauca.microkernel.plugins.MultimediaQuestionPlugin;
import co.edu.unicauca.microkernel.plugins.MultipleChoiceQuestionPlugin;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.List;

/**
 * Punto de entrada de la aplicacion de escritorio (Swing).
 *
 * Las acciones de la interfaz (crear pregunta, elegir tipo, etc.) disparan la
 * ejecucion del microkernel y del pipeline de validacion de los plugins.
 */
public class Main extends JFrame {

    private static final String[] QUESTION_TYPES = {
            MultipleChoiceQuestionPlugin.TYPE,
            CaseQuestionPlugin.TYPE,
            MultimediaQuestionPlugin.TYPE
    };

    private final QuestionMicrokernel microkernel = new QuestionMicrokernel();

    private final JComboBox<String> typeCombo = new JComboBox<>(QUESTION_TYPES);
    private final JTextField titleField = new JTextField(24);
    private final JTextArea contentArea = new JTextArea(3, 24);
    private final JTextField classificationField = new JTextField(24);
    private final JTextArea optionsArea = new JTextArea(4, 24);
    private final JTextField correctAnswerField = new JTextField(24);
    private final JTextArea outputArea = new JTextArea(6, 24);

    private final DefaultTableModel tableModel =
            new DefaultTableModel(new String[]{"ID", "Titulo", "Contenido", "Tipo"}, 0);
    private final JTable table = new JTable(tableModel);

    public Main() {
        super("Banco de Preguntas Saber PRO — Taller 05 (Microkernel + Pipes & Filters)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel form = buildFormPanel();
        JPanel resultPanel = buildResultPanel();
        outputArea.setEditable(false);
        optionsArea.setLineWrap(true);
        contentArea.setLineWrap(true);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, form, resultPanel);
        split.setResizeWeight(0.55);
        add(split, BorderLayout.CENTER);

        setSize(980, 620);
        setLocationRelativeTo(null);
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Tipo de pregunta:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        panel.add(typeCombo, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Titulo:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        panel.add(titleField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Contenido:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        panel.add(new JScrollPane(contentArea), gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Clasificacion (competencia):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        panel.add(classificationField, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Opciones (una por linea):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        panel.add(new JScrollPane(optionsArea), gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Respuesta correcta:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        panel.add(correctAnswerField, gbc);

        JButton generateButton = new JButton("Generar pregunta");
        generateButton.addActionListener(e -> handleGenerate());
        gbc.gridy = 6;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(generateButton, gbc);

        return panel;
    }

    private JPanel buildResultPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Banco de preguntas "
                + "(" + microkernel.getPluginCount() + " plugins registrados)"));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(new JScrollPane(outputArea), BorderLayout.SOUTH);
        return panel;
    }

    private void handleGenerate() {
        String type = (String) typeCombo.getSelectedItem();
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();
        String classification = classificationField.getText().trim();
        List<String> options = Arrays.stream(optionsArea.getText().split("\\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        String correctAnswer = correctAnswerField.getText().trim();

        QuestionRequest request =
                new QuestionRequest(title, content, type, classification, options, correctAnswer);

        Question question = microkernel.executePlugin(type, request);
        if (question != null) {
            tableModel.addRow(new Object[]{
                    question.getId(), question.getTitle(), question.getContent(), question.getType()
            });
            outputArea.setText("Pregunta generada y registrada: " + question.getId()
                    + System.lineSeparator()
                    + "Tipo: " + question.getType()
                    + System.lineSeparator()
                    + "Banco: " + microkernel.getQuestionCount() + " pregunta(s).");
            clearForm();
        } else {
            outputArea.setText("La pregunta fue rechazada por el pipeline de validacion."
                    + System.lineSeparator()
                    + "Revise: titulo/contenido, minimo 2 opciones sin vacias ni duplicadas,"
                    + System.lineSeparator()
                    + "clasificacion y que la respuesta correcta este entre las opciones.");
        }
    }

    private void clearForm() {
        titleField.setText("");
        contentArea.setText("");
        classificationField.setText("");
        optionsArea.setText("");
        correctAnswerField.setText("");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // se mantiene el look & feel por defecto
        }
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}