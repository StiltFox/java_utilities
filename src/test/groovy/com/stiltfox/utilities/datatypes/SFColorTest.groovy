package com.stiltfox.utilities.datatypes

import spock.lang.Specification

class SFColorTest extends Specification
{
    def "calculateRelativeLumocity will calculate relative luminosity assuming the color provided is the foreground"(int r, int g, int b, int r0, int g0, int b0, double expected)
    {
        when: "We calculate the relative luminosity of two colors"
        def actual = new SFColor(r,g,b).calculateRelativeLumocity(new SFColor(r0, g0, b0))

        then: "We get back the expected lumosity difference"
        actual == expected

        where:
        r << [233, 69, 10000]
        g << [228, 143, 300]
        b << [208, 37, 750]
        r0 << [18, 245, -5]
        g0 << [52, 135, -1]
        b0 << [176, 161, -44]
        expected << [75.6, -20.6, 106.0]
    }

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
}