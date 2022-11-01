package com.stiltfox.utilities.functional;

public interface TriFunction<IPT, IPT1, IPT2, OPT>
{
    OPT accept(IPT input, IPT1 input1, IPT2 input2);
}
