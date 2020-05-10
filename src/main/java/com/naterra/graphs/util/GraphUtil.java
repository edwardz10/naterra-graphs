package com.naterra.graphs.util;

import com.naterra.graphs.exception.GraphException;
import org.slf4j.Logger;

public class GraphUtil {

    public static void logAndThrowException(Logger logger, String format, String ... arguments)
            throws GraphException {
        String errorMessage = String.format(format, arguments);
        logger.error(errorMessage);
        throw new GraphException(errorMessage);
    }
}
