package com.stiltfox.utilities.datatypes;

import com.stiltfox.utilities.functional.TriFunction;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Supplier;

@ToString
public class SFColor
{
    private Supplier<Double> getRandom = Math::random;
    double r,g,b,a;

    public SFColor()
    {
        r = getRandom.get();
        g = getRandom.get();
        b = getRandom.get();
        a = 1;
    }

    public SFColor(int r, int g, int b)
    {
        this(r/255.0,g/255.0,b/255.0);
    }

    public SFColor(double red, double green, double blue)
    {
        this(red, green, blue, 1);
    }

    public SFColor(double red, double green, double blue, double opacity)
    {
        r = Math.max(Math.min(red, 1),0);
        g = Math.max(Math.min(green, 1),0);
        b = Math.max(Math.min(blue, 1),0);
        a = Math.max(Math.min(opacity, 1),0);
    }

    public SFColor shiftBrightness(double toShift)
    {
        double outR = Math.max(Math.min(r + toShift, 1), 0);
        double outG = Math.max(Math.min(g + toShift, 1), 0);
        double outB = Math.max(Math.min(b + toShift, 1), 0);

        return new SFColor(outR, outG, outB, a);
    }

    public SFColor invert()
    {
        return new SFColor(1-r,1-g,1-b);
    }

    public <T> T convert(TriFunction<Double, Double, Double, T> converter)
    {
        return converter.accept(r,g,b);
    }

    public SFColor getGreyScale()
    {
        double greyscale = (0.299 * r) + (0.587 * g) + (0.114 * b);
        return new SFColor(greyscale, greyscale, greyscale);
    }

    public boolean equals(Object o)
    {
        return this == o || (o instanceof SFColor &&
                BigDecimal.valueOf(r).setScale(10, RoundingMode.HALF_UP).doubleValue() == BigDecimal.valueOf(((SFColor) o).r).setScale(10, RoundingMode.HALF_UP).doubleValue() &&
                BigDecimal.valueOf(g).setScale(10, RoundingMode.HALF_UP).doubleValue() == BigDecimal.valueOf(((SFColor) o).g).setScale(10, RoundingMode.HALF_UP).doubleValue() &&
                BigDecimal.valueOf(b).setScale(10, RoundingMode.HALF_UP).doubleValue() == BigDecimal.valueOf(((SFColor) o).b).setScale(10, RoundingMode.HALF_UP).doubleValue());
    }
}