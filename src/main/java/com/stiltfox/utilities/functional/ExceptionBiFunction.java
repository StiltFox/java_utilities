package com.stiltfox.utilities.functional;

public interface ExceptionBiFunction<IPT, IPT1, OPT, THR extends Throwable>
{
    OPT accept(IPT input, IPT1 input1) throws THR;
}
