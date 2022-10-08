package com.stiltfox.utilities

import com.fasterxml.jackson.databind.ObjectMapper
import com.stiltfox.utilities.test_tools.StiltFoxTest

class HashableFileTest extends StiltFoxTest
{
    ObjectMapper mapper = []

    def before()
    {}

    def "sha256 will return the sha256 code of the file represented"(String contents, String expected)
    {
        given: "We have a hashable file with some contents"
        HashableFile file = [tempFolder.newFile("file.txt")]
        file.write(contents)

        when: "We get the sha256"
        def actual = file.sha256()

        then: "We get back the hashcode of the contents"
        actual == expected

        where:
        contents << ["this is a file", "some text here", "asdf"]
        expected << ["fc45acaffc35a3aa674f7c0d5a03d22350b4f2ff4bf45ccebad077e5af80e512", "72e8aed2d93a2cfe4f55c019e4a1862eb869c2c6080a183edbb6f0f6ab32bdf2", "f0e4c2f76c58916ec258f246851bea091d14d4247a2fc3e18694461b1816e13b"]
    }

    def "md5 will return the md5 hash of the file represented"(String contents, String expected)
    {
        given: "We have a hashable file with some contents"
        HashableFile file = [tempFolder.newFile("file.txt")]
        file.write(contents)

        when: "We get the md5"
        def actual = file.md5()

        then: "We get back the hashcode of the contents"
        actual == expected

        where:
        contents << ["this is a file", "some text here", "asdf"]
        expected << ["139ec4f94a8c908e20e7c2dce5092af4", "9b21960e1acf245f1493527ce1d0bbea", "912ec803b2ce49e4a541068d495ab570"]
    }

    def "getNameWithoutExtension will get the file name without the extension"(String fileName, String expected)
    {
        given: "We have a hashable file with a name"
        HashableFile file = [tempFolder.newFile(fileName)]

        when: "We get the filename without extension"
        def actual = file.getNameWithoutExtension()

        then: "we get back the name"
        actual == expected

        where:
        fileName << ["test.txt", "scp-173.data", "jack.bright.personel"]
        expected << ["test", "scp-173", "jack.bright"]
    }

    def "getExtension gets the extension of the file"(String fileName, String expected)
    {
        given: "We have a hashable file with a name"
        HashableFile file = [tempFolder.newFile(fileName)]

        when: "We get the extension"
        def actual = file.getExtension()

        then: "we get back the extension"
        actual == expected

        where:
        fileName << ["test.txt", "scp-173.data", "jack.bright.personel"]
        expected << [".txt", ".data", ".personel"]
    }

    def "writeObject will create the file if it does not exist, then write the contents to it"()
    {
        given: "We have file that does not exist"
        HashableFile file = [tempFolder.getRoot().getAbsolutePath() + "/testfile.txt"]

        when: "We try to write an object to the file"
        file.writeObject(["test":"value"])

        then: "The object is written"
        mapper.readValue(file, Map.class) == ["test":"value"]
    }

    def "writeObject will overwrite an existing object"()
    {
        given: "We have a file that already exists and has a value"
        def existingFile = tempFolder.newFile("test.txt")
        existingFile.write("asdf")
        HashableFile file = [existingFile]

        when: "We try to write an object to the file"
        file.writeObject(["test":"testvalue"])

        then: "The object is written"
        mapper.readValue(file, Map.class) == ["test":"testvalue"]
    }
}