package com.stiltfox.utilities

import spock.lang.Specification

class DataConverterTest extends Specification
{
    DataConverter converter = []

    def "convertBinaryToHexString will convert the binary data to the correct hex"(byte[] dataToConvert, String expected)
    {
        when: "I try to convert binary data to a hex string"
        def actual = converter.binaryToHexString(dataToConvert)

        then: "We get back the hex string"
        actual == expected

        where:
        dataToConvert << [[0x00], [0xAB, 0xCD, 0xEF, 0x12, 0x34, 0x56, 0x78, 0x09], [0x89, 0xd4]]
        expected << ["00", "ABCDEF1234567809", "89D4"]
    }

    def "binaryToUUID will convert the binary data to the correct UUID"(byte[] data, UUID expected)
    {
        when: "I try to convert binary data to a UUID"
        def actual = converter.binaryToUUID(data)

        then: "we get back the UUID"
        actual == expected

        where:
        data << [[0xef,0x11,0x91,0xc3,0xcc,0x43,0x72,0xb8,0xf4,0x00,0x70,0xeb,0x88,0x26,0xff,0x5d],
                 [0x00,0x11,0x00,0x11,0x00,0x11,0x00,0xb8,0xf4,0x00,0x70,0xeb,0x88,0x26,0xff,0x5d],
                 [0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff]]
        expected << [[0xef1191c3cc4372b8 as long,0xf40070eb8826ff5d as long] as UUID,
                     [0x00110011001100b8 as long,0xf40070eb8826ff5d as long] as UUID,
                     [0xffffffffffffffff as long,0xffffffffffffffff as long] as UUID]
    }

    def "binaryToUUID will return a zero id on an error"()
    {
        given: "we have a bad stream"
        InputStream badStream = Mock()
        badStream.readAllBytes() >> {throw new IOException()}

        when: "I try to convert binary data to a UUID"
        def actual = converter.binaryToUUID(badStream)

        then: "we get back a zero UUID"
        actual == [0x0000000000000000 as long,0x0000000000000000 as long] as UUID
    }
}