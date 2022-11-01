package com.stiltfox.utilities.io

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.stiltfox.utilities.test_tools.StiltFoxTest
import groovy.transform.EqualsAndHashCode

import java.nio.file.Files

class HashableFileTest extends StiltFoxTest
{
    ObjectMapper mapper = []

    def before()
    {}

    def "getName will get the file name without the extension"(String fileName, String expected)
    {
        given: "We have a hashable file with a name"
        HashableFile file = [tempFolder.newFile(fileName)]

        when: "We get the filename"
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

    def "writeData will create the file if it does not exist, then write the contents to it when provided an object"()
    {
        given: "We have file that does not exist"
        HashableFile file = [tempFolder.getRoot().getAbsolutePath() + "/testfile.txt"]

        when: "We try to write an object to the file"
        file.writeData(["test":"value"])

        then: "The object is written"
        mapper.readValue(file, Map.class) == ["test":"value"]
    }

    def "writeData will overwrite an existing file when provided an object"()
    {
        given: "We have a file that already exists and has a value"
        def existingFile = tempFolder.newFile("test.txt")
        existingFile.write("asdfsdfasggerfdlgkjdfgoirhdflkgjsdhgsdglkdhsgoirhds")
        HashableFile file = [existingFile]

        when: "We try to write an object to the file"
        file.writeData(["test":"testvalue"])

        then: "The object is written"
        mapper.readValue(file, Map.class) == ["test":"testvalue"]
        new String(Files.readAllBytes(file.toPath())) == "{\"test\":\"testvalue\"}"
    }

    def "writeData will create the file if it does not exist, then write the contents to it when provided binary"()
    {
        given: "We have file that does not exist"
        HashableFile file = [tempFolder.getRoot().getAbsolutePath() + "/testfile.txt"]

        when: "We try to write binary to the file"
        file.writeData("this is a test".bytes)

        then: "The binary is written"
        Files.readAllLines(file.toPath()) == ["this is a test"]
    }

    def "writeData will overwrite an existing file when provided binary"()
    {
        given: "We have a file that already exists and has a value"
        def existingFile = tempFolder.newFile("test.txt")
        existingFile.write("asdfsdfasggerfdlgkjdfgoirhdflkgjsdhgsdglkdhsgoirhds")
        HashableFile file = [existingFile]

        when: "We try to write binary to the file"
        file.writeData("this is a test".bytes)

        then: "The binary is written"
        Files.readAllLines(file.toPath()) == ["this is a test"]
    }

    def "readObject will read the json from the file"()
    {
        given: "We have a file that already exists"
        def existingFile = tempFolder.newFile("test.txt")
        existingFile.write("{\"testText\":\"text\",\"itemList\":[\"item_1\",\"item_3\",\"pickle\"]}")
        HashableFile file = [existingFile]

        when: "We try to read the file to an object"
        AClass actual = file.readObject(AClass.class)

        then: "We get back an object with the expected values"
        actual == ["text",["item_1","item_3","pickle"]] as AClass
    }

    def "readObject will read generic objects like lists from file"()
    {
        given: "We have a file that already exists with a list"
        def existingFile = tempFolder.newFile("test.txt")
        existingFile.write("[{\"testText\":\"text\",\"itemList\":[\"item_1\",\"item_3\",\"pickle\"]},{\"testText\":\"label\",\"itemList\":[\"value\"]}]")
        HashableFile file = [existingFile]

        when: "We try to read the file to an object"
        List<AClass> actual = file.readObject(new TypeReference<List<AClass>>() {})

        then: "We get back an object with the expected values"
        actual == [["text",["item_1","item_3","pickle"]] as AClass,["label",["value"]] as AClass]
    }

    def "sha256 will return the hash of the data provided by the object"(String data, String expected)
    {
        given: "We have a file with some data"
        def existingFile = tempFolder.newFile("test.txt")
        existingFile.write(data)
        HashableFile hashableFile = [existingFile]

        when: "We get the sha256 hash"
        String hash = hashableFile.sha256()

        then: "We get the hash of the contents"
        hash == expected

        where:
        data << ["this is a file", "some text here", "asdf"]
        expected << ["fc45acaffc35a3aa674f7c0d5a03d22350b4f2ff4bf45ccebad077e5af80e512", "72e8aed2d93a2cfe4f55c019e4a1862eb869c2c6080a183edbb6f0f6ab32bdf2", "f0e4c2f76c58916ec258f246851bea091d14d4247a2fc3e18694461b1816e13b"]
    }

    def "md5 will return the hash of the data provided by the object"(String data, String expected)
    {
        given: "We have a file with some data"
        def existingFile = tempFolder.newFile("test.txt")
        existingFile.write(data)
        HashableFile hashableFile = [existingFile]

        when: "We get the md5 hash"
        String hash = hashableFile.md5()

        then: "We get the hash of the contents"
        hash == expected

        where:
        data << ["this is a file", "some text here", "asdf"]
        expected << ["139ec4f94a8c908e20e7c2dce5092af4", "9b21960e1acf245f1493527ce1d0bbea", "912ec803b2ce49e4a541068d495ab570"]
    }

    @EqualsAndHashCode
    static class AClass
    {
        String testText
        List<String> itemList

        AClass(){}
        AClass(String testText, List<String> itemList)
        {
            this.testText = testText
            this.itemList = itemList
        }
    }
}