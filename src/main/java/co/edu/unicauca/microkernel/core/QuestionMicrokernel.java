package co.edu.unicauca.microkernel.core;

import co.edu.unicauca.microkernel.common.entities.Question;
import co.edu.unicauca.microkernel.common.entities.QuestionRequest;
import co.edu.unicauca.microkernel.common.interfaces.QuestionPlugin;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Nucleo central del patron Microkernel.
 *
 * Administra el banco de preguntas en un {@code Map<String, Question>} y gestiona
 * el ciclo de vida de los plugins: carga por reflexion, registro y ejecucion.
 */
public class QuestionMicrokernel {

    private final Map<String, Question> questionBank;
    private final Map<String, QuestionPlugin> registeredPlugins;

    public QuestionMicrokernel() {
        this("plugins.properties");
    }

    /**
     * @param propertiesPath ruta (classpath o sistema de archivos) del archivo
     *                       {@code plugins.properties} que mapea claves a nombres
     *                       completamente calificados de clases plugin.
     */
    public QuestionMicrokernel(String propertiesPath) {
        this.questionBank = new LinkedHashMap<>();
        this.registeredPlugins = new LinkedHashMap<>();
        loadPlugins(propertiesPath);
    }

    private void loadPlugins(String propertiesPath) {
        Properties properties = new Properties();
        try (InputStream input = openResource(propertiesPath)) {
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar el archivo de plugins: "
                    + propertiesPath, e);
        }

        for (Object value : properties.values()) {
            try {
                String className = ((String) value).trim();
                Class<?> pluginClass = Class.forName(className);
                QuestionPlugin plugin = (QuestionPlugin)
                        pluginClass.getDeclaredConstructor().newInstance();
                registeredPlugins.put(plugin.getName(), plugin);
            } catch (ReflectiveOperationException | ClassCastException e) {
                throw new IllegalStateException("Error al instanciar el plugin: " + value, e);
            }
        }
    }

    private InputStream openResource(String propertiesPath) throws IOException {
        InputStream classpath = getClass().getClassLoader().getResourceAsStream(propertiesPath);
        if (classpath != null) {
            return classpath;
        }
        return new FileInputStream(propertiesPath);
    }

    /**
     * Busca el primer plugin cuyo {@code supports(type)} sea verdadero, lo ejecuta
     * y, si genera una pregunta, la registra en el banco.
     *
     * @return la pregunta generada, o {@code null} si el pipeline del plugin la rechazo
     *         o no existe un plugin que soporte el tipo.
     */
    public Question executePlugin(String type, QuestionRequest request) {
        for (QuestionPlugin plugin : registeredPlugins.values()) {
            if (plugin.supports(type)) {
                Question question = plugin.generate(request);
                if (question != null) {
                    questionBank.put(question.getId(), question);
                    return question;
                }
                return null;
            }
        }
        return null;
    }

    public void registerPlugin(QuestionPlugin plugin) {
        registeredPlugins.put(plugin.getName(), plugin);
    }

    public Question getQuestion(String id) {
        return questionBank.get(id);
    }

    public Map<String, Question> getQuestionBank() {
        return Collections.unmodifiableMap(questionBank);
    }

    public Map<String, QuestionPlugin> getRegisteredPlugins() {
        return Collections.unmodifiableMap(registeredPlugins);
    }

    public int getPluginCount() {
        return registeredPlugins.size();
    }

    public int getQuestionCount() {
        return questionBank.size();
    }
}