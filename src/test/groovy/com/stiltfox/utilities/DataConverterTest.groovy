package com.stiltfox.utilities

import spock.lang.Specification

class DataConverterTest extends Specification
{
    DataConverter converter = []

    def "convertBinaryToHexString will convert the binary data to the correct hex"()
    {
        when: "I try to convert binary data to a hex string"
        def actual = converter.binaryToHexString(dataToConvert as byte[])

        then: "We get back the hex string"
        actual == expected

        where:
        dataToConvert << [[0x00], [0xAB, 0xCD, 0xEF, 0x12, 0x34, 0x56, 0x78, 0x09], [0x89, 0xd4]]
        expected << ["00", "ABCDEF1234567809", "89D4"]
    }
}