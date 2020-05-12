package com.naterra.graphs.service.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FunctionCalculatorImplTest {

    @Test
    public void stringFunctionWorks() {
        String value = "'AAA'";
        String function = " + 'BBB'";

        String updatedValue = (String)(new FunctionCalculatorImpl().calculate(value, function));
        assertEquals(updatedValue, "AAABBB");
    }

    @Test
    public void integerFunctionWorks() {
        Integer value = 17;
        String function = " * 5";

        Integer updatedValue = (Integer)(new FunctionCalculatorImpl().calculate(value, function));
        assertEquals(updatedValue, 85);
    }

    @Test
    public void doubleFunctionWorks() {
        Double value = 28.0;
        String function = " / 7";

        Double updatedValue = (Double)(new FunctionCalculatorImpl().calculate(value, function));
        assertEquals(updatedValue, 4);
    }
}
