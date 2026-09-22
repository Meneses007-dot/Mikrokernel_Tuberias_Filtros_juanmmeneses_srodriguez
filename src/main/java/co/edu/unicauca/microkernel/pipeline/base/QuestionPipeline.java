package co.edu.unicauca.microkernel.pipeline.base;

import co.edu.unicauca.microkernel.common.entities.QuestionRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Coordina la ejecucion secuencial de una lista de {@link QuestionFilter},
 * deteniendose (y reportando) en el primer filtro que falle.
 */
public class QuestionPipeline {

    private final List<QuestionFilter> filters;
    private final List<String> errors;

    public QuestionPipeline() {
        this.filters = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    public QuestionPipeline addFilter(QuestionFilter filter) {
        this.filters.add(filter);
        return this;
    }

    /**
     * Ejecuta los filtros en orden. Si alguno falla, detiene el procesamiento
     * y registra el error del filtro responsable.
     */
    public boolean process(QuestionRequest request) {
        this.errors.clear();
        for (QuestionFilter filter : this.filters) {
            if (!filter.process(request)) {
                this.errors.add("El filtro '" + filter.getClass().getSimpleName()
                        + "' rechazo la pregunta");
                return false;
            }
        }
        return true;
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public int size() {
        return filters.size();
    }

    public List<QuestionFilter> getFilters() {
        return Collections.unmodifiableList(filters);
    }
}