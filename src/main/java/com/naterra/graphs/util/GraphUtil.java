package com.naterra.graphs.util;

import com.naterra.graphs.exception.GraphException;
import com.naterra.graphs.model.VertexDTO;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

public class GraphUtil {

    public static void logAndThrowException(Logger logger, String format, String ... arguments)
            throws GraphException {
        String errorMessage = String.format(format, arguments);
        logger.error(errorMessage);
        throw new GraphException(errorMessage);
    }

    public static VertexDTO<?> getVertexByValue(Map<VertexDTO<?>, List<VertexDTO<?>>> matrix, String token) {
        return matrix.keySet().stream()
                .filter(vertex -> vertex.getValue().toString().equals(token))
                .findAny()
                .orElse(null);

    }

    public static boolean isTypeValid(String className) throws GraphException {
        if (!className.startsWith("java.lang.")) {
            throw new GraphException("Only classes from the 'java.lang' package are supported");
        }

        try {
            Class<?> myClass = Class.forName(className);
            Constructor<?> constructor = myClass.getConstructors()[0];
            constructor.newInstance();
        } catch (Exception e) {
            throw new GraphException("Class '" + className + "' not found");
        }

        return true;
    }
}
