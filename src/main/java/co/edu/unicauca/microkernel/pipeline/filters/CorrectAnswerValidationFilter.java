package co.edu.unicauca.microkernel.pipeline.filters;

import co.edu.unicauca.microkernel.common.entities.QuestionRequest;
import co.edu.unicauca.microkernel.pipeline.base.QuestionFilter;

import java.util.List;

/**
 * Verifica que exista una respuesta correcta y que esta pertenezca
 * a las opciones disponibles (consistencia de datos).
 */
public class CorrectAnswerValidationFilter implements QuestionFilter {

    @Override
    public boolean process(QuestionRequest request) {
        if (request == null) {
            return false;
        }
        List<String> options = request.getOptions();
        String correctAnswer = request.getCorrectAnswer();

        if (correctAnswer == null || correctAnswer.trim().isEmpty() || options == null) {
            return false;
        }

        for (String option : options) {
            if (option != null && option.trim().equalsIgnoreCase(correctAnswer.trim())) {
                return true;
            }
        }
        return false;
    }
}