package com.stiltfox.utilities.datatypes

import spock.lang.Specification

class SFColorTest extends Specification
{
    def "invert will return the inverted color"(int r, int g, int b, SFColor expected)
    {
        when: "We calculate the inversion of the colors"
        def actual = new SFColor(r,g,b).invert()

        then: "We get back the inverted color"
        actual == expected

        where:
        r << [233, 69, 10000]
        g << [228, 143, 300]
        b << [208, 37, 750]
        expected << [[22,27,47] as SFColor, [186,112,218] as SFColor, [0,0,0] as SFColor]
    }

    def "shiftBrightness will adjust all color values by the provided amount"(double shiftAmount, SFColor expected)
    {
        given: "We have a color"
        SFColor color = [0.35,0.33,0.1]

        when: "We shift the color brightness"
        def actual = color.shiftBrightness(shiftAmount)

        then: "We get back the shifted color"
        actual == expected

        where:
        shiftAmount << [0.1, -0.2]
        expected << [[0.45,0.43,0.2] as SFColor, [0.15,0.13,0.0] as SFColor]
    }

    def "shiftBrightness will not adjust a color value above 1"()
    {
        given: "We have a color"
        SFColor color = [0.47, 0.55, 0.25]

        when: "We shift the color brightness"
        def actual = color.shiftBrightness(0.8)

        then: "We do not shift the value over 1"
        actual == [1.0,1.0,1.0] as SFColor
    }

    def "shiftBrightness will not adjust a color below 0"()
    {
        given: "We have a color"
        SFColor color = [0.1, 0.1, 0.1]

        when: "We shift the color brightness"
        def actual = color.shiftBrightness(-0.99)

        then: "We do not shift below 0"
        actual == [0,0,0] as SFColor
    }

    def "convert will apply the desired type conversion to the color, but not modify the color"()
    {
        given: "We have a color"
        SFColor color = [0.47, 0.55, 0.25]

        when: "We attempt to convert the color"
        def actual = color.convert((r, g, b)-> [r-5, g-5, b-5])

        then: "We get back the converted color and the original color is not effected"
        actual == [-4.53, -4.45, -4.75] as List<Double>
        color == [0.47, 0.55, 0.25] as SFColor
    }

    def "getGreyScale will return the greyscale value of the color"()
    {
        given: "We have a color"
        SFColor color = [0,102,255]

        when: "We convert the color to greyscale"
        def actual = color.getGreyScale()

        then: "We get back the expected greyscale value"
        actual == [0.3488,0.3488,0.3488] as SFColor
    }

    def "creating a SFColor with no parameters will create a random color at max opacity"()
    {
        given: "We know what the math random function will output"
        SFColor.getRandom = () -> (double)0.5

        when: "We create a color with no parameters"
        SFColor actual = []

        then: "We get back a color with 'random' values"
        actual == [0.5,0.5,0.5] as SFColor
    }
}