package com.stiltfox.utilities.functional;

public interface ExceptionConsumer<RET, IPT, THR extends Throwable>
{
    RET accept(IPT input) throws THR;
}