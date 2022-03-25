package com.stiltfox.utilities

import com.google.gson.Gson
import com.stiltfox.utilities.test_tools.StiltFoxTest
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import java.nio.file.Files
import java.nio.file.Path
import java.util.function.BiConsumer
import java.util.function.Supplier

class FileOpsTest extends StiltFoxTest
{
    Gson gson
    Supplier supplier
    BiConsumer biConsumer
    ClassLoader classLoader
    FileOps fileOps

    def before()
    {
        gson = new Gson()
        supplier = Mock()
        classLoader = Mock()
        biConsumer = Mock()
        fileOps = [classLoader: classLoader]
    }

    def "readJsonFile returns default when file to read does not exist"()
    {
        given: "We have a path to a file that does not exist"
        def badPath = "I'm not even a file path"
        TestClass expected = [value: "gold", name: "pumpkin", description: "test tag"]

        when: "We try to read an object from the file"
        def actual = fileOps.readJsonFile(TestClass.class, badPath, supplier)

        then: "We call the supplier and return the result of it"
        1 * supplier.get() >> expected
        actual == expected
    }

    def "readJsonFile will fill in what it can if the file in question only has some of the required fields"()
    {
        given: "We have a file with partial data"
        def partialPath = this.class.getClassLoader().getResource("partial.json").getFile()
        TestClass expected = [name: "someJsonObject"]

        when: "We try to read an object from the file"
        def actual = fileOps.readJsonFile(TestClass, partialPath, supplier)

        then: "We return the partial result"
        0 * supplier.get() >> null
        actual == expected
    }

    def "readResourceFile returns the string contents of the requested resource"()
    {
        given: "We have a resource we want to load"
        def resourceUrl = "/someResource.txt"
        def expectedContents = "Lorem ipsum doler summett"

        when: "We load the resource"
        def actual = fileOps.readResourceFile(resourceUrl, biConsumer)

        then: "We get back the contents of the file and the error handler is not called"
        0 * biConsumer.accept(_,_)
        1 * classLoader.getResourceAsStream(resourceUrl) >> new ByteArrayInputStream(expectedContents.getBytes())
        actual == expectedContents
    }

    def "readResourceFile calls error handler if anything goes wrong while getting the resource"()
    {
        given: "We have a resource that throws an error when read"
        def errorResource = "/corrupt_file.txt"
        RuntimeException expectedException = new RuntimeException("OH NO!")

        when: "We load the resource"
        def actual = fileOps.readResourceFile(errorResource, biConsumer)

        then: "We call the error handler and get back a null"
        1 * classLoader.getResourceAsStream(errorResource) >> {throw expectedException}
        1 * biConsumer.accept(errorResource, expectedException)
        actual == null
    }

    def "saveJsonFile calls error handler if anything goes wrong while saving data"()
    {
        given: "We have an object to save, and a file location that causes an error"
        def nonExistingPath = tempFolder.getRoot().getAbsolutePath()
        def map = ["map":"map", "another_key":"key"]

        when: "We try to save the json to the file"
        fileOps.saveJsonFile(map, nonExistingPath, biConsumer)

        then: "We call the error handler and dont save anything"
        1 * biConsumer.accept(nonExistingPath, _)
    }

    def "saveJsonFile creates the file and puts expected data in the file"()
    {
        given: "We have an object to save and a location to put it"
        def path = tempFolder.getRoot().getAbsolutePath() + "/test.json"
        def map = ["map":"map", "another_key":"key"]

        when: "We try to save the json file"
        fileOps.saveJsonFile(map, path, biConsumer)

        then: "The file is saved with the proper data and the error handler is not called"
        0 * biConsumer.accept(_, _)
        new File(path).exists()
        gson.fromJson(Files.newBufferedReader(Path.of(path)), Map.class) == map
    }

    def "saveJsonFile overwrites existing file data with expected data"()
    {
        given: "We have an object to save and an existing file to put it in"
        def path = tempFolder.getRoot().getAbsolutePath() + "/test.json"
        def storedMap = ["map":"map", "another_key":"key"]
        def mapToSave = ["value":"asdf"]
        gson.toJson(storedMap, Files.newBufferedWriter(Path.of(path)))

        when: "We try to save the json file"
        fileOps.saveJsonFile(mapToSave, path, biConsumer)

        then: "The file is saved over the existing file"
        0 * biConsumer.accept(_, _)
        new File(path).exists()
        gson.fromJson(Files.newBufferedReader(Path.of(path)), Map.class) == mapToSave
    }

    def "readTextFile returns the text of the file if it exists"()
    {
        given: "Have a valid text file"
        def partialPath = this.class.getClassLoader().getResource("partial.json").getFile()

        when: "We try to read from the file"
        def actual = fileOps.readTextFile(partialPath, biConsumer)

        then: "We return the contents of the file"
        0 * biConsumer.accept(_,_)
        actual == "{\n" +
                "  \"name\": \"someJsonObject\",\n" +
                "  \"unused\": true\n" +
                "}"
    }

    def "readTextFile calls the error handler when there is a problem"()
    {
        given: "We have a bad file path"
        def path = tempFolder.getRoot().getAbsolutePath() + "/non-existing.txt"

        when: "We try to read the text file"
        def actual = fileOps.readTextFile(path, biConsumer)

        then: "We call the error handler"
        1 * biConsumer.accept(path, _ as Exception)
        actual == null
    }

    @EqualsAndHashCode
    @ToString
    static class TestClass
    {
        String value
        String name
        String description

        TestClass() {}
    }
}