package co.edu.unicauca.microkernel.pipeline.filters;

import co.edu.unicauca.microkernel.common.entities.QuestionRequest;
import co.edu.unicauca.microkernel.pipeline.base.QuestionFilter;

/**
 * Valida que el titulo y el contenido no esten vacios, cumplan una longitud
 * minima y tengan un formato correcto (sin espacios redundantes en los extremos).
 */
public class ContentValidationFilter implements QuestionFilter {

    private static final int MIN_TITLE_LENGTH = 3;
    private static final int MIN_CONTENT_LENGTH = 10;

    @Override
    public boolean process(QuestionRequest request) {
        if (request == null) {
            return false;
        }
        String title = request.getTitle();
        String content = request.getContent();

        return title != null
                && title.trim().length() >= MIN_TITLE_LENGTH
                && content != null
                && content.trim().length() >= MIN_CONTENT_LENGTH;
    }
}