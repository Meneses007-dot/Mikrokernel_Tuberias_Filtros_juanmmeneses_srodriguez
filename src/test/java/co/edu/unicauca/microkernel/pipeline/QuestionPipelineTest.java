package co.edu.unicauca.microkernel.pipeline;

import co.edu.unicauca.microkernel.common.entities.Question;
import co.edu.unicauca.microkernel.common.entities.QuestionRequest;
import co.edu.unicauca.microkernel.pipeline.base.QuestionFilter;
import co.edu.unicauca.microkernel.pipeline.base.QuestionPipeline;
import co.edu.unicauca.microkernel.pipeline.filters.ClassificationFilter;
import co.edu.unicauca.microkernel.pipeline.filters.ContentValidationFilter;
import co.edu.unicauca.microkernel.pipeline.filters.CorrectAnswerValidationFilter;
import co.edu.unicauca.microkernel.pipeline.filters.OptionsValidationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de las clases de dominio, de cada filtro y del pipeline completo.
 */
class QuestionPipelineTest {

    private QuestionPipeline buildPipeline() {
        return new QuestionPipeline()
                .addFilter(new ContentValidationFilter())
                .addFilter(new OptionsValidationFilter())
                .addFilter(new ClassificationFilter())
                .addFilter(new CorrectAnswerValidationFilter());
    }

    private QuestionRequest validRequest() {
        return new QuestionRequest(
                "Pregunta de prueba",
                "Este es el contenido completo y suficiente de la pregunta.",
                "SELECCION_MULTIPLE",
                "Razonamiento Cuantitativo",
                Arrays.asList("Opcion A", "Opcion B", "Opcion C", "Opcion D"),
                "Opcion B");
    }

    // ---------- Clases de dominio ----------

    @Test
    @DisplayName("Question almacena y expone sus datos")
    void questionStoresAndExposesItsData() {
        Question q = new Question("Q1", "Titulo", "Contenido", "SELECCION_MULTIPLE");
        assertEquals("Q1", q.getId());
        assertEquals("Titulo", q.getTitle());
        assertEquals("Contenido", q.getContent());
        assertEquals("SELECCION_MULTIPLE", q.getType());
    }

    @Test
    @DisplayName("QuestionRequest almacena y expone sus datos")
    void questionRequestStoresAndExposesItsData() {
        List<String> options = Arrays.asList("A", "B");
        QuestionRequest r = new QuestionRequest(
                "T", "C", "CASO", "Clas", options, "B");
        assertEquals("T", r.getTitle());
        assertEquals("C", r.getContent());
        assertEquals("CASO", r.getType());
        assertEquals("Clas", r.getClassification());
        assertEquals(options, r.getOptions());
        assertEquals("B", r.getCorrectAnswer());
    }

    // ---------- Filtro de contenido ----------

    @Test
    @DisplayName("ContentValidationFilter acepta titulo y contenido validos")
    void contentFilterAcceptsValidContent() {
        assertTrue(new ContentValidationFilter().process(validRequest()));
    }

    @Test
    @DisplayName("ContentValidationFilter rechaza titulo vacio o demasiado corto")
    void contentFilterRejectsEmptyOrShortTitle() {
        QuestionRequest r1 = new QuestionRequest("", "Contenido largo valido aqui",
                "SELECCION_MULTIPLE", "Clas", Arrays.asList("A", "B"), "A");
        QuestionRequest r2 = new QuestionRequest("Ab", "Contenido largo valido aqui",
                "SELECCION_MULTIPLE", "Clas", Arrays.asList("A", "B"), "A");
        assertFalse(new ContentValidationFilter().process(r1));
        assertFalse(new ContentValidationFilter().process(r2));
    }

    @Test
    @DisplayName("ContentValidationFilter rechaza contenido vacio o demasiado corto")
    void contentFilterRejectsEmptyOrShortContent() {
        QuestionRequest r1 = new QuestionRequest("Titulo valido", "",
                "SELECCION_MULTIPLE", "Clas", Arrays.asList("A", "B"), "A");
        QuestionRequest r2 = new QuestionRequest("Titulo valido", "Corto",
                "SELECCION_MULTIPLE", "Clas", Arrays.asList("A", "B"), "A");
        assertFalse(new ContentValidationFilter().process(r1));
        assertFalse(new ContentValidationFilter().process(r2));
    }

    // ---------- Filtro de opciones ----------

    @Test
    @DisplayName("OptionsValidationFilter rechaza menos de 2 opciones")
    void optionsFilterRejectsFewerThanTwoOptions() {
        QuestionRequest r = new QuestionRequest("Titulo valido", "Contenido largo valido",
                "SELECCION_MULTIPLE", "Clas", Arrays.asList("Sola"), "Sola");
        assertFalse(new OptionsValidationFilter().process(r));
    }

    @Test
    @DisplayName("OptionsValidationFilter rechaza opciones vacias")
    void optionsFilterRejectsEmptyOptions() {
        QuestionRequest r = new QuestionRequest("Titulo valido", "Contenido largo valido",
                "SELECCION_MULTIPLE", "Clas", Arrays.asList("A", "  "), "A");
        assertFalse(new OptionsValidationFilter().process(r));
    }

    @Test
    @DisplayName("OptionsValidationFilter rechaza opciones duplicadas")
    void optionsFilterRejectsDuplicateOptions() {
        QuestionRequest r = new QuestionRequest("Titulo valido", "Contenido largo valido",
                "SELECCION_MULTIPLE", "Clas", Arrays.asList("A", "a", "B"), "A");
        assertFalse(new OptionsValidationFilter().process(r));
    }

    // ---------- Filtro de clasificacion ----------

    @Test
    @DisplayName("ClassificationFilter rechaza clasificacion vacia")
    void classificationFilterRejectsBlank() {
        QuestionRequest r = new QuestionRequest("Titulo valido", "Contenido largo valido",
                "SELECCION_MULTIPLE", "  ", Arrays.asList("A", "B"), "A");
        assertFalse(new ClassificationFilter().process(r));
        assertTrue(new ClassificationFilter().process(validRequest()));
    }

    // ---------- Filtro de respuesta correcta ----------

    @Test
    @DisplayName("CorrectAnswerValidationFilter rechaza respuesta fuera de las opciones")
    void correctAnswerFilterRejectsAnswerNotInOptions() {
        QuestionRequest r = new QuestionRequest("Titulo valido", "Contenido largo valido",
                "SELECCION_MULTIPLE", "Clas", Arrays.asList("A", "B", "C"), "Z");
        assertFalse(new CorrectAnswerValidationFilter().process(r));
    }

    @Test
    @DisplayName("CorrectAnswerValidationFilter rechaza respuesta vacia")
    void correctAnswerFilterRejectsBlankAnswer() {
        QuestionRequest r = new QuestionRequest("Titulo valido", "Contenido largo valido",
                "SELECCION_MULTIPLE", "Clas", Arrays.asList("A", "B"), "");
        assertFalse(new CorrectAnswerValidationFilter().process(r));
    }

    // ---------- Orquestacion del pipeline ----------

    @Test
    @DisplayName("Pipeline completo acepta una pregunta valida")
    void pipelineAcceptsValidRequest() {
        QuestionPipeline pipeline = buildPipeline();
        assertTrue(pipeline.process(validRequest()));
        assertTrue(pipeline.getErrors().isEmpty());
        assertEquals(4, pipeline.size());
    }

    @Test
    @DisplayName("Pipeline detiene el proceso en el primer filtro que falla y reporta el error")
    void pipelineStopsAtFirstFailingFilter() {
        QuestionPipeline pipeline = buildPipeline();
        QuestionRequest invalid = new QuestionRequest(
                "", "Contenido largo pero titulo invalido", "SELECCION_MULTIPLE",
                "Clas", Arrays.asList("A", "B"), "A");
        assertFalse(pipeline.process(invalid));
        assertEquals(1, pipeline.getErrors().size());
        assertTrue(pipeline.getErrors().get(0).contains("ContentValidationFilter"));
    }

    @Test
    @DisplayName("Pipeline rechaza la respuesta correcta que no pertenece a las opciones")
    void pipelineRejectsAnswerNotInOptions() {
        QuestionPipeline pipeline = buildPipeline();
        QuestionRequest invalid = new QuestionRequest(
                "Titulo valido", "Contenido largo valido para la pregunta", "CASO",
                "Clas", Arrays.asList("A", "B", "C"), "Respuesta inexistente");
        assertFalse(pipeline.process(invalid));
        assertFalse(pipeline.getErrors().isEmpty());
    }

    @Test
    @DisplayName("Los filtros registrados se conservan en el orden de adicion")
    void pipelineKeepsFilterOrder() {
        QuestionPipeline pipeline = buildPipeline();
        List<QuestionFilter> filters = pipeline.getFilters();
        assertTrue(filters.get(0) instanceof ContentValidationFilter);
        assertTrue(filters.get(1) instanceof OptionsValidationFilter);
        assertTrue(filters.get(2) instanceof ClassificationFilter);
        assertTrue(filters.get(3) instanceof CorrectAnswerValidationFilter);
        assertNotNull(pipeline);
    }
}