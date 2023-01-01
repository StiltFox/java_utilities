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
}