package co.edu.unicauca.microkernel.plugins;

import co.edu.unicauca.microkernel.common.entities.Question;
import co.edu.unicauca.microkernel.common.entities.QuestionRequest;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Plugin que genera preguntas con recursos multimedia (imagen, audio o video).
 */
public class MultimediaQuestionPlugin extends AbstractQuestionPlugin {

    public static final String TYPE = "MULTIMEDIA";
    private static final String NAME = "GeneradorPreguntaMultimedia";
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