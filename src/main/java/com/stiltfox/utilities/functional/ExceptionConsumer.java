package com.stiltfox.utilities.functional;

public interface ExceptionConsumer<IPT, THR extends Throwable>
{
    void accept(IPT input) throws THR;
}