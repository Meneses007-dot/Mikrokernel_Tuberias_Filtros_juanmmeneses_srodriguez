package co.edu.unicauca.microkernel.plugins;

import co.edu.unicauca.microkernel.common.entities.Question;
import co.edu.unicauca.microkernel.common.entities.QuestionRequest;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Plugin que genera preguntas de seleccion multiple con una unica respuesta correcta.
 */
public class MultipleChoiceQuestionPlugin extends AbstractQuestionPlugin {

    public static final String TYPE = "SELECCION_MULTIPLE";
    private static final String NAME = "GeneradorPreguntaSeleccionMultiple";
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