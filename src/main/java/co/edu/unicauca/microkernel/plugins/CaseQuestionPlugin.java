package co.edu.unicauca.microkernel.plugins;

import co.edu.unicauca.microkernel.common.entities.Question;
import co.edu.unicauca.microkernel.common.entities.QuestionRequest;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Plugin que genera preguntas de analisis de casos.
 */
public class CaseQuestionPlugin extends AbstractQuestionPlugin {

    public static final String TYPE = "CASO";
    private static final String NAME = "GeneradorPreguntaCaso";
    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean supports(String type) {
        return TYPE.equalsIgnoreCase(type);
    }

    @Override
    public Question generate(QuestionRequest request) {
        if (!runValidations(request)) {
            return null;
        }
        return buildQuestion(TYPE + "-" + SEQUENCE.getAndIncrement(), TYPE, request);
    }
}