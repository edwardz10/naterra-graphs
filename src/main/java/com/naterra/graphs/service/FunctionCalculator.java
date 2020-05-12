package com.naterra.graphs.service;

import com.naterra.graphs.exception.GraphException;

public interface FunctionCalculator<T>{
    T calculate(T value, String function) throws GraphException;
}
