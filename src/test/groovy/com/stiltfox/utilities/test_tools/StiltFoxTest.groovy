package com.stiltfox.utilities.test_tools

import org.junit.ClassRule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.sql.*
import java.util.function.Function

abstract class StiltFoxTest extends Specification
{
    @ClassRule
    TemporaryFolder tempFolder = new TemporaryFolder()

    def setup()
    {
        tempFolder.create()
        before()
    }

    abstract def before();


    def runCommandOnTestDb(List<String> sqlCommands, String dbName) throws Exception
    {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:file:" + tempFolder.getRoot().getAbsolutePath() + "/" + dbName + ";TRACE_LEVEL_FILE=0", "sa", ""))
        {
            try(Statement statement = connection.createStatement())
            {
                for (String sqlCommand : sqlCommands)
                {
                    statement.execute(sqlCommand)
                }
            }
        }
    }

    def runCommandsOnTestStiltFoxDb(List<String> sqlCommands) throws Exception
    {
        runCommandOnTestDb(sqlCommands, "stiltfoxfsdb")
    }
}