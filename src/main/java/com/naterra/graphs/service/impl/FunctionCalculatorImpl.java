package com.naterra.graphs.service.impl;

import com.naterra.graphs.exception.GraphException;
import com.naterra.graphs.service.FunctionCalculator;
import com.naterra.graphs.util.GraphUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class FunctionCalculatorImpl implements FunctionCalculator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionCalculatorImpl.class);

    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");

    @Override
    public Object calculate(Object value, String function) throws GraphException {
        validateType(value.getClass());

        try {
            Object updatedValue = calcExpression(value, function);

            if (value.getClass().equals(Integer.class)) {
                return ((Double)updatedValue).intValue();
            }

            return calcExpression(value, function);
        } catch (ScriptException e) {
            GraphUtil.logAndThrowException(LOGGER, "Unable to calculate '%s' for '%s'", function, value.toString());
            return value;
        }
    }

    private void validateType(Class clazz) {
        if (!clazz.equals(String.class)
            && !clazz.equals(Integer.class)
            && !clazz.equals(Double.class)) {
            GraphUtil.logAndThrowException(LOGGER, "Type %s is not supported", clazz.getName());
        }
    }

    private Object calcExpression(Object value, String function) throws ScriptException {
        return engine.eval(getJavascriptFunction(value, function));
    }

    private String getJavascriptFunction(Object value, String function) {
        StringBuilder sb = new StringBuilder("var x = " + value.toString() + ";");
        sb.append("x = x ").append(function).append(";");

        return sb.toString();
    }
}
