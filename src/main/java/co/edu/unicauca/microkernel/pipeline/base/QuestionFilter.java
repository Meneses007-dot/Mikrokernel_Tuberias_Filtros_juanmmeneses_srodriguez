package co.edu.unicauca.microkernel.pipeline.base;

import co.edu.unicauca.microkernel.common.entities.QuestionRequest;

/**
 * Abstraccion de un filtro del patron Tuberias y Filtros (Pipes &amp; Filters).
 */
public interface QuestionFilter {

    boolean process(QuestionRequest request);
}