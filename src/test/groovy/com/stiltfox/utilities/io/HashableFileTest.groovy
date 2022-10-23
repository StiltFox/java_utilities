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
        def actual = file.getName()

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

    def "write will create the file if it does not exist, then write the contents to it when provided an object"()
    {
        given: "We have file that does not exist"
        HashableFile file = [tempFolder.getRoot().getAbsolutePath() + "/testfile.txt"]

        when: "We try to write an object to the file"
        file.write(["test":"value"])

        then: "The object is written"
        mapper.readValue(file.sourceFile, Map.class) == ["test":"value"]
    }

    def "write will overwrite an existing file when provided an object"()
    {
        given: "We have a file that already exists and has a value"
        def existingFile = tempFolder.newFile("test.txt")
        existingFile.write("asdfsdfasggerfdlgkjdfgoirhdflkgjsdhgsdglkdhsgoirhds")
        HashableFile file = [existingFile]

        when: "We try to write an object to the file"
        file.write(["test":"testvalue"])

        then: "The object is written"
        mapper.readValue(file.sourceFile, Map.class) == ["test":"testvalue"]
        new String(Files.readAllBytes(file.getFile().toPath())) == "{\"test\":\"testvalue\"}"
    }

    def "write will create the file if it does not exist, then write the contents to it when provided binary"()
    {
        given: "We have file that does not exist"
        HashableFile file = [tempFolder.getRoot().getAbsolutePath() + "/testfile.txt"]

        when: "We try to write binary to the file"
        file.write("this is a test".bytes)

        then: "The binary is written"
        Files.readAllLines(file.getFile().toPath()) == ["this is a test"]
    }

    def "write will overwrite an existing file when provided binary"()
    {
        given: "We have a file that already exists and has a value"
        def existingFile = tempFolder.newFile("test.txt")
        existingFile.write("asdfsdfasggerfdlgkjdfgoirhdflkgjsdhgsdglkdhsgoirhds")
        HashableFile file = [existingFile]

        when: "We try to write binary to the file"
        file.write("this is a test".bytes)

        then: "The binary is written"
        Files.readAllLines(file.getFile().toPath()) == ["this is a test"]
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