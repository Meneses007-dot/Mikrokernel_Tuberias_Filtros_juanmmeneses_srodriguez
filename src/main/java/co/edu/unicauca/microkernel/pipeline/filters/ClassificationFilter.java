package co.edu.unicauca.microkernel.pipeline.filters;

import co.edu.unicauca.microkernel.common.entities.QuestionRequest;
import co.edu.unicauca.microkernel.pipeline.base.QuestionFilter;

/**
 * Valida y asigna la competencia o categoria de la pregunta
 * (p. ej. "Razonamiento Cuantitativo", "Arquitectura de software").
 */
public class ClassificationFilter implements QuestionFilter {

    @Override
    public boolean process(QuestionRequest request) {
        if (request == null) {
            return false;
        }
        String classification = request.getClassification();
        return classification != null && !classification.trim().isEmpty()
                && classification.trim().length() >= 2;
    }
}