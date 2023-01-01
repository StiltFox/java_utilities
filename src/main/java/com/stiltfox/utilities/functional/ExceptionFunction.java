package com.stiltfox.utilities.functional;

@FunctionalInterface
public interface ExceptionFunction<IPT, OPT, THR extends Throwable>
{
    OPT accept(IPT input) throws THR;
}