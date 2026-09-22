package co.edu.unicauca.microkernel.core;

import co.edu.unicauca.microkernel.common.entities.Question;
import co.edu.unicauca.microkernel.common.entities.QuestionRequest;
import co.edu.unicauca.microkernel.common.interfaces.QuestionPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de la carga de plugins por reflexion y de la ejecucion del nucleo.
 */
class QuestionMicrokernelTest {

    private QuestionMicrokernel microkernel;

    @BeforeEach
    void setUp() {
        microkernel = new QuestionMicrokernel();
    }

    private QuestionRequest validRequest(String type) {
        return new QuestionRequest(
                "Pregunta de prueba",
                "Este es el contenido completo y suficiente de la pregunta.",
                type,
                "Razonamiento Cuantitativo",
                Arrays.asList("Opcion A", "Opcion B", "Opcion C", "Opcion D"),
                "Opcion B");
    }

    @Test
    @DisplayName("plugins.properties se lee y los plugins se instancian por reflexion")
    void pluginsAreLoadedByReflection() {
        assertEquals(3, microkernel.getPluginCount());
        for (QuestionPlugin plugin : microkernel.getRegisteredPlugins().values()) {
            assertNotNull(plugin.getName());
        }
    }

    @Test
    @DisplayName("executePlugin genera y registra la pregunta para tipos soportados")
    void executePluginSucceedsForSupportedTypes() {
        String[] types = {"SELECCION_MULTIPLE", "CASO", "MULTIMEDIA"};
        for (int i = 0; i < types.length; i++) {
            Question question = microkernel.executePlugin(types[i], validRequest(types[i]));
            assertNotNull(question);
            assertEquals(types[i], question.getType());
            assertEquals(i + 1, microkernel.getQuestionCount());
        }
        assertEquals(3, microkernel.getQuestionCount());
    }

    @Test
    @DisplayName("executePlugin retorna null y no registra nada para un tipo no soportado")
    void executePluginFailsForUnsupportedType() {
        Question question = microkernel.executePlugin("ESSAY", validRequest("ESSAY"));
        assertNull(question);
        assertTrue(microkernel.getQuestionBank().isEmpty());
    }

    @Test
    @DisplayName("executePlugin retorna null si la solicitud no supera el pipeline")
    void executePluginFailsWhenPipelineRejectsRequest() {
        QuestionRequest invalid = new QuestionRequest(
                "", // titulo invalido
                "Contenido largo valido",
                "SELECCION_MULTIPLE",
                "Razonamiento Cuantitativo",
                Arrays.asList("A", "B"),
                "A");
        Question question = microkernel.executePlugin("SELECCION_MULTIPLE", invalid);
        assertNull(question);
        assertTrue(microkernel.getQuestionBank().isEmpty());
    }

    @Test
    @DisplayName("Las preguntas generadas quedan accesibles por id en el banco")
    void generatedQuestionIsStoredAndRetrievable() {
        Question generated = microkernel.executePlugin("CASO", validRequest("CASO"));
        assertNotNull(generated);
        Question stored = microkernel.getQuestion(generated.getId());
        assertNotNull(stored);
        assertEquals(generated.getTitle(), stored.getTitle());
    }

    @Test
    @DisplayName("El banco expone el mapa de preguntas")
    void questionBankIsExposedAsMap() {
        microkernel.executePlugin("SELECCION_MULTIPLE", validRequest("SELECCION_MULTIPLE"));
        Map<String, Question> bank = microkernel.getQuestionBank();
        assertFalse(bank.isEmpty());
        assertTrue(bank.containsKey(bank.keySet().iterator().next()));
    }

    @Test
    @DisplayName("El plugins.properties manual/adicional tambien puede registrarse en runtime")
    void additionalPluginCanBeRegisteredManually() {
        int before = microkernel.getPluginCount();
        microkernel.registerPlugin(new TestPlugin());
        assertEquals(before + 1, microkernel.getPluginCount());
    }

    private static class TestPlugin implements QuestionPlugin {
        @Override
        public String getName() {
            return "TestPlugin";
        }

        @Override
        public boolean supports(String type) {
            return "TEST".equalsIgnoreCase(type);
        }

        @Override
        public Question generate(QuestionRequest request) {
            return new Question("TEST-1", request.getTitle(), request.getContent(), "TEST");
        }
    }
}