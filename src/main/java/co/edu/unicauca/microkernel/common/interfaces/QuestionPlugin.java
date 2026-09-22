package co.edu.unicauca.microkernel.common.interfaces;

import co.edu.unicauca.microkernel.common.entities.Question;
import co.edu.unicauca.microkernel.common.entities.QuestionRequest;

/**
 * Contrato que deben implementar todos los plugins del microkernel.
 */
public interface QuestionPlugin {

    String getName();

    boolean supports(String type);

    Question generate(QuestionRequest request);
}