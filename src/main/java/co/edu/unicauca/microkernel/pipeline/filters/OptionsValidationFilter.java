package co.edu.unicauca.microkernel.pipeline.filters;

import co.edu.unicauca.microkernel.common.entities.QuestionRequest;
import co.edu.unicauca.microkernel.pipeline.base.QuestionFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Valida las opciones de respuesta: minimo 2 opciones, ninguna vacia y
 * ninguna duplicada (regla del taller oficial evaluado).
 */
public class OptionsValidationFilter implements QuestionFilter {

    @Override
    public boolean process(QuestionRequest request) {
        if (request == null) {
            return false;
        }
        List<String> options = request.getOptions();
        if (options == null || options.size() < 2) {
            return false;
        }

        Set<String> seen = new HashSet<>();
        for (String option : options) {
            if (option == null || option.trim().isEmpty()) {
                return false;
            }
            if (!seen.add(option.trim().toLowerCase())) {
                return false;
            }
        }
        return true;
    }
}