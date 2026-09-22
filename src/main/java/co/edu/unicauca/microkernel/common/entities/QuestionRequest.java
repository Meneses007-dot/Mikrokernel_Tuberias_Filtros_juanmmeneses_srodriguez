package co.edu.unicauca.microkernel.common.entities;

import java.util.List;

/**
 * Transporta los datos de entrada hacia los plugins del microkernel.
 * Incluye la informacion sobre la que opera el pipeline de validacion.
 */
public class QuestionRequest {

    private String title;
    private String content;
    private String type;
    private String classification;
    private List<String> options;
    private String correctAnswer;

    public QuestionRequest(String title, String content, String type,
                           String classification, List<String> options, String correctAnswer) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.classification = classification;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

    public String getClassification() {
        return classification;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}