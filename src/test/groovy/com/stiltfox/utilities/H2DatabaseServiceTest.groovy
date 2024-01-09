package com.stiltfox.utilities

import com.stiltfox.utilities.test_tools.StiltFoxTest
import java.sql.SQLException

class H2DatabaseServiceTest extends StiltFoxTest
{
    H2DatabaseService metaDataService = []

    def before() {}

    def "getMetaData will throw an SQLException when loading a non-database file"()
    {
        given: "We have a file that is not a database"
        def nonDatabase = tempFolder.newFile("not_a_database.mv.db")
        nonDatabase.write("test")

        when: "We try to get the file's meta data"
        metaDataService.getMetaData(nonDatabase)

        then: "We throw an error when trying to read the file as a database"
        thrown(SQLException.class)
    }

    def "getMetaData will throw a FileNotFoundException when loading a file that does not exist"()
    {
        given: "We have a file that is not existing"
        File nonExistingFile = ["badfile.mv.db"]

        when: "We try to get the file's meta data"
        metaDataService.getMetaData(nonExistingFile)

        then: "We throw an error that the file does not exist"
        def error = thrown(FileNotFoundException.class)
        error.getMessage() == nonExistingFile.getAbsolutePath()
    }

    def "getMetaData will load the meta data of the database"()
    {
        given: "We have a database with many tables of varying column size"
        def dbFile = tempFolder.newFile("database.mv.db")
        runCommandOnTestDb(["CREATE TABLE \"PUBLIC\".\"TABLE\"(A_COLUMN int, B_COLUMN int)", "CREATE TABLE \"PUBLIC\".\"TABLE_TWO\"(A_COLUMN int)", "CREATE TABLE \"PUBLIC\".\"TABLE_THREE\"(A_COLUMN int, B_COLUMN int, C_COLUMN int)"], "database")

        when: "We try to get the metadata of the database"
        def actual = metaDataService.getMetaData(dbFile)

        then: "We get back the correct columns for each table"
        actual == ["TABLE": ["A_COLUMN": "INTEGER", "B_COLUMN": "INTEGER"], "TABLE_TWO": ["A_COLUMN": "INTEGER"] , "TABLE_THREE":["A_COLUMN": "INTEGER", "B_COLUMN": "INTEGER", "C_COLUMN": "INTEGER"]]
    }

    def "validateDatabase returns an empty list when the meta data input matches the expected data exactly"()
    {
        given: "We have two sets of metadata that are the same"
        def metaData = ["table":["column": "int"]]
        def expectedData = ["table":["column": "int"]]

        when: "We try to compare the metadata"
        def actual = metaDataService.validateDatabase(metaData, expectedData)

        then: "We get back an empty list"
        actual == [] as Set
    }

    def "validateDatabase returns a list of differences between the metadata and expected data"()
    {
        given: "We have an expected database structure"
        def expectedData = ["TABLE": ["A_COLUMN": "INTEGER", "B_COLUMN": "VARCHAR"], "TABLE_TWO": ["A_COLUMN": "BOOLEAN"] , "TABLE_THREE":["A_COLUMN": "INTEGER", "B_COLUMN": "INTEGER", "C_COLUMN": "INTEGER"]]

        when: "We try to compare the metadata"
        def actual = metaDataService.validateDatabase(metaData, expectedData)

        then: "We get back a list of errors"
        actual == errors

        where:
        metaData << [[] as HashMap<String, Map<String,String>>,
                    ["TABLE":["A_COLUMN": "INTEGER", "B_COLUMN": "BOOLEAN"], "TABLE_TWO":["B_COLUMN":"VARCHAR"]],
                    ["TABLE_FIVE":["A_COLUMN": "BOOLEAN"]]]
        errors << [["Missing table TABLE", "Missing table TABLE_TWO", "Missing table TABLE_THREE"] as Set,
                   ["Missing table TABLE_THREE", "Column B_COLUMN in table TABLE is the wrong type; expected: VARCHAR actual: BOOLEAN", "Unwanted column in TABLE_TWO; B_COLUMN type VARCHAR", "Missing column in TABLE_TWO; A_COLUMN type BOOLEAN"] as Set,
                   ["Unwanted table TABLE_FIVE", "Missing table TABLE", "Missing table TABLE_TWO", "Missing table TABLE_THREE"] as Set]
    }

    def "getAllData gets all of the data from the database"()
    {
        given: "We have a database with some data in it"
        def dbFile = tempFolder.newFile("temp.mv.db")
        runCommandOnTestDb(["create table TEST (A_COLUMN int, B_COLUMN int)", "create table TEST2 (V VARCHAR)", "insert into TEST2 values ('test')", "insert into TEST values (3,4)", "insert into TEST values (5,6)"], "temp")

        when: "We try to get all the data in the database"
        def actual = metaDataService.getAllData(dbFile)

        then: "We get back the metadata and row data"
        actual == [metadata:[TEST:[A_COLUMN:"INTEGER", B_COLUMN:"INTEGER"], TEST2:[V:"VARCHAR"]],rowdata:[TEST:[[A_COLUMN:5,B_COLUMN:6],[A_COLUMN:3, B_COLUMN:4]] as Set,TEST2:[[V:"test"]] as Set]]
    }
}