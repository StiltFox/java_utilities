package com.stiltfox.utilities.functional;

public interface ExceptionSupplier<OPT, THR extends Throwable>
{
    OPT get() throws THR;
}