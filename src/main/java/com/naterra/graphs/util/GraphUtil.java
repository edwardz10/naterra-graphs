package com.naterra.graphs.util;

import com.naterra.graphs.exception.GraphException;
import com.naterra.graphs.model.VertexDTO;
import com.naterra.graphs.service.impl.FunctionCalculatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GraphUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphUtil.class);

    public static final String JAVA_LANG = "java.lang";

    public static void logAndThrowException(Logger logger, String format, String ... arguments)
            throws GraphException {
        String errorMessage = String.format(format, arguments);
        logger.error(errorMessage);
        throw new GraphException(errorMessage);
    }

    public static void validateType(String className) throws GraphException {

        if (className.length() <= JAVA_LANG.length() || !className.startsWith(JAVA_LANG)) {
            logAndThrowException(LOGGER, "Only classes from the %s package are supported", JAVA_LANG);
        }

        try {
            Class.forName(className);
        } catch (Exception e) {
            logAndThrowException(LOGGER, "Class %s not found", className);
        }
    }

    public static String calculateValueCache(VertexDTO from, VertexDTO to) {
        String[] values = {from.getValue().toString(), to.getValue().toString()};
        Arrays.sort(values);
        return values[0] + values[1];
    }
}
