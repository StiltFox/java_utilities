package com.stiltfox.utilities.io

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.stiltfox.utilities.test_tools.StiltFoxTest
import groovy.transform.EqualsAndHashCode

import java.nio.file.Files
import java.util.stream.Collectors

class HashableFileTest extends StiltFoxTest
{
    ObjectMapper mapper = []

    def before()
    {}

    def "getNameWithoutExtension will get the file name without the extension"(String fileName, String expected)
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

    def "getFullName will return the absolute path of the file"()
    {
        given: "We have a file"
        File tempFile = tempFolder.newFile()
        HashableFile file = [tempFile]

        when: "We get the full name"
        def actual = file.getFullName()

        then: "We get back the absolute path"
        actual == tempFile.getAbsolutePath()
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
        def actual = file.readObject(AClass.class)

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

    def "readObject will read an empty object if a file is empty"()
    {
        given: "We have a file that already exists with nothing in it"
        def existingFile = tempFolder.newFile("test.txt")
        HashableFile file = [existingFile]

        when: "We try to read the file to an object"
        def actual = file.readObject(AClass.class)

        then: "We get back an empty instance of AClass"
        actual == [] as AClass
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

    def "listFiles will list all of the files contained within the directory"()
    {
        given: "We have a hashable file that points to a directory"
        HashableFile directoryToList = [tempFolder.newFolder("directory")]
        HashableFile file1 = [tempFolder.newFile("directory/test.txt")]
        HashableFile file2 = [tempFolder.newFile("directory/pickle.jpg")]
        HashableFile file3 = [tempFolder.newFile("directory/sandwich.cmd")]

        when: "We list the files"
        def actual = directoryToList.listFiles()

        then: "We get back a list of the contained files"
        actual == [file1, file2, file3] as HashableFile[]
    }

    def "listFiles will return an empty list if the file pointed to does not exist"()
    {
        given: "We have a hashable file that does not exist"
        HashableFile nonExistingFile = ["i/dont/exist"]

        when: "We attempt to list the files"
        def actual = nonExistingFile.listFiles()

        then: "We get back an empty list"
        actual == [] as HashableFile[]
    }

    def "listFiles will return an empty list if the file pointed to is not a directory"()
    {
        given: "We have a hashable file that is not a directory"
        HashableFile regularFile = [tempFolder.newFile()]

        when: "We attempt to list the files"
        def actual = regularFile.listFiles()

        then: "We get back an empty list"
        actual == [] as HashableFile[]
    }

    def "copyTo will copy the contents of the file to the new location"()
    {
        given: "We have a source file and a destination"
        HashableFile source = [tempFolder.newFile()]
        HashableFile destination = [tempFolder.getRoot().getAbsolutePath() + "/copy"]
        source.write("SCP-173 is to be kept in a concrete room.")

        when: "We copy the source to the destination"
        source.copyTo(destination)

        then: "The contents of the source will be in the destination"
        source.readLines() == ["SCP-173 is to be kept in a concrete room."]
        destination.readLines() == ["SCP-173 is to be kept in a concrete room."]
    }

    def "copyTo will overwrite the contents of an existing file"()
    {
        given: "We have a source file and a destination with data in it"
        HashableFile source = [tempFolder.newFile()]
        HashableFile destination = [tempFolder.getRoot().getAbsolutePath() + "/copy"]
        source.write("dr. bright has access to all facilities.")
        destination.write("dr. bright is not allowed to access the site 19 recreational facility.")

        when: "We copy the source to the destination"
        source.copyTo(destination)

        then: "The contents of the source will be in the destination"
        source.readLines() == ["dr. bright has access to all facilities."]
        destination.readLines() == ["dr. bright has access to all facilities."]
    }

    def "copyTo will recursively copy all flies to the destination if the source is a directory"()
    {
        given: "We have a directory with objects in it"
        HashableFile source = [tempFolder.newFolder("directory")]
        HashableFile destination = [tempFolder.getRoot().getAbsolutePath() + "/copy"]
        tempFolder.newFile("directory/test.txt")
        tempFolder.newFolder("directory/sandwich")
        tempFolder.newFile("directory/sandwich/pickle.jpg")
        def path = destination.getAbsolutePath()

        when: "We copy the directory to the new location"
        source.copyTo(destination)
        def actual = Files.walk(destination.toPath()).map(pth-> new HashableFile(pth.toString())).collect(Collectors.toSet())

        then: "All files are copied over"
        actual == [[path] as HashableFile,
                   [path+"/test.txt"] as HashableFile,
                   [path+"/sandwich"] as HashableFile,
                   [path+"/sandwich/pickle.jpg"] as HashableFile] as Set
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