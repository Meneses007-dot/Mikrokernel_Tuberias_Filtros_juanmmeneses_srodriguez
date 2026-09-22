package co.edu.unicauca.microkernel.plugins;

import co.edu.unicauca.microkernel.common.entities.Question;
import co.edu.unicauca.microkernel.common.entities.QuestionRequest;
import co.edu.unicauca.microkernel.common.interfaces.QuestionPlugin;
import co.edu.unicauca.microkernel.pipeline.base.QuestionPipeline;
import co.edu.unicauca.microkernel.pipeline.filters.ClassificationFilter;
import co.edu.unicauca.microkernel.pipeline.filters.ContentValidationFilter;
import co.edu.unicauca.microkernel.pipeline.filters.CorrectAnswerValidationFilter;
import co.edu.unicauca.microkernel.pipeline.filters.OptionsValidationFilter;

/**
 * Clase base para los plugins del microkernel.
 *
 * Encapsula el pipeline de validacion (Tuberias y Filtros) que debe ejecutarse
 * dentro de cada plugin antes de generar y registrar la pregunta. Si el pipeline
 * falla, el plugin no genera la pregunta.
 */
public abstract class AbstractQuestionPlugin implements QuestionPlugin {

    private final QuestionPipeline validationPipeline;

    protected AbstractQuestionPlugin() {
        this.validationPipeline = new QuestionPipeline()
                .addFilter(new ContentValidationFilter())
                .addFilter(new OptionsValidationFilter())
                .addFilter(new ClassificationFilter())
                .addFilter(new CorrectAnswerValidationFilter());
    }

    /**
     * Ejecuta el pipeline de validacion sobre la solicitud.
     *
     * @return {@code true} si todos los filtros aprueban la solicitud.
     */
    protected boolean runValidations(QuestionRequest request) {
        if (request == null) {
            return false;
        }
        return validationPipeline.process(request);
    }

    /**
     * Errores reportados por el ultimo procesamiento del pipeline.
     */
    protected java.util.List<String> getValidationErrors() {
        return validationPipeline.getErrors();
    }

    protected QuestionPipeline getValidationPipeline() {
        return validationPipeline;
    }

    protected Question buildQuestion(String id, String type, QuestionRequest request) {
        return new Question(id, request.getTitle(), request.getContent(), type);
    }
}