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

    public SFColor getTextVisibleTextColorFromInvert()
    {
        SFColor color = invert();
        double luminosity = calculateRelativeLumocity(color);
        double polarity = luminosity < 0 ? 1 : -1;
        double boost = 0.1 * polarity;

        while ((luminosity > -60 && luminosity < 60) && (color.r != 1.0 || color.g != 1.0 || color.b != 1.0) && (color.r != 0.0 || color.g != 0.0 || color.b != 0.0))
        {
            color.r = Math.max(Math.min(color.r + (boost)*0.2126729, 1), 0);
            color.g = Math.max(Math.min(color.g + (boost)*0.7151522, 1), 0);
            color.b = Math.max(Math.min(color.b + (boost)*0.0721750, 1), 0);
            luminosity = calculateRelativeLumocity(color);
            boost = polarity < 0 ? boost - 0.1 : boost + 0.1;
        }

        return color;
    }

    public double calculateRelativeLumocity(SFColor foregroundColor)
    {
        double outputContrast = 0.0;
        double SAPC;

        double lumocity = blackClamp(calculateLumosity());
        double foregroundLumocity = blackClamp(foregroundColor.calculateLumosity());

        if (!(Math.abs(lumocity - foregroundLumocity) < 0.0005d))
        {
            if (lumocity > foregroundLumocity)
            {
                SAPC = ( Math.pow(lumocity, 0.56d) - Math.pow(foregroundLumocity, 0.57d) ) * 1.14d;
                outputContrast = (SAPC < 0.1d) ? 0.0d : SAPC - 0.027d;
            }
            else
            {
                SAPC = (Math.pow(lumocity,0.65d) - Math.pow(foregroundLumocity,0.62d)) * 1.14d;
                outputContrast = (SAPC > -0.1d) ? 0.0d : SAPC + 0.027d;
            }
        }

        return BigDecimal.valueOf(outputContrast * 100.0d).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    private double calculateLumosity()
    {
        return Math.min(Math.max((0.2126729 * Math.pow(r, 2.4d)) + (0.7151522 * Math.pow(g, 2.4d)) + (0.0721750 * Math.pow(b, 2.4d)), 0d),1d);
    }

    private double blackClamp(double lumocity)
    {
        return lumocity > 0.022d ? lumocity  : lumocity + Math.pow(0.022d-lumocity, 1.414d);
    }

    public boolean equals(Object o)
    {
        return this == o || (o instanceof SFColor &&
                BigDecimal.valueOf(r).setScale(10, RoundingMode.HALF_UP).doubleValue() == BigDecimal.valueOf(((SFColor) o).r).setScale(10, RoundingMode.HALF_UP).doubleValue() &&
                BigDecimal.valueOf(g).setScale(10, RoundingMode.HALF_UP).doubleValue() == BigDecimal.valueOf(((SFColor) o).g).setScale(10, RoundingMode.HALF_UP).doubleValue() &&
                BigDecimal.valueOf(b).setScale(10, RoundingMode.HALF_UP).doubleValue() == BigDecimal.valueOf(((SFColor) o).b).setScale(10, RoundingMode.HALF_UP).doubleValue());
    }
}