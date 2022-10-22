package com.stiltfox.utilities.io

import com.stiltfox.utilities.test_tools.StiltFoxTest

class HashableResourceTest extends StiltFoxTest
{
    def before (){}

    def "sha256 will return the hash of the data provided by the object"(String data, String expected)
    {
        given: "We have an object with some data"
        TestHashable testData = new TestHashable(data)

        when: "We get the sha256 hash"
        String hash = testData.sha256()

        then: "We get the hash of the contents"
        hash == expected

        where:
        data << ["this is a file", "some text here", "asdf"]
        expected << ["fc45acaffc35a3aa674f7c0d5a03d22350b4f2ff4bf45ccebad077e5af80e512", "72e8aed2d93a2cfe4f55c019e4a1862eb869c2c6080a183edbb6f0f6ab32bdf2", "f0e4c2f76c58916ec258f246851bea091d14d4247a2fc3e18694461b1816e13b"]
    }

    def "md5 will return the hash of the data provided by the object"(String data, String expected)
    {
        given: "We have an object with some data"
        TestHashable testData = new TestHashable(data)

        when: "We get the md5 hash"
        String hash = testData.md5()

        then: "We get the hash of the contents"
        hash == expected

        where:
        data << ["this is a file", "some text here", "asdf"]
        expected << ["139ec4f94a8c908e20e7c2dce5092af4", "9b21960e1acf245f1493527ce1d0bbea", "912ec803b2ce49e4a541068d495ab570"]
    }

    static class TestHashable extends HashableResource
    {
        byte[] data

        TestHashable(String input) {data = input.bytes}

        byte[] getData() throws IOException {return data}
        String getName() {return null}
        String getExtension() {return null}
    }
}