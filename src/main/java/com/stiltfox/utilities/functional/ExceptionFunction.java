package com.stiltfox.utilities.functional;

public interface ExceptionFunction<IPT, OPT, THR extends Throwable>
{
    OPT accept(IPT input) throws THR;
}