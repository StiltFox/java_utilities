package com.stiltfox.utilities;

import com.stiltfox.utilities.functional.ExceptionConsumer;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.*;
import java.util.function.BiFunction;

public class H2DatabaseService
{

    public void executeQueryOnDatabase(File databaseFile, String query) throws Exception
    {
        executeQueryOnDatabase(databaseFile, query, new ArrayList<>(), null);
    }

    public void executeQueryOnDatabase(File databaseFile, String query, List<Map.Entry<Integer, Object>> parameters) throws Exception
    {
        executeQueryOnDatabase(databaseFile, query, parameters, null);
    }
    public void executeQueryOnDatabase(File databaseFile, String query, ExceptionConsumer<ResultSet, Exception> resultSetReader) throws Exception
    {
        executeQueryOnDatabase(databaseFile, query, new ArrayList<>(), resultSetReader);
    }

    public void executeQueryOnDatabase(File databaseFile, String query, List<Map.Entry<Integer, Object>> parameters, ExceptionConsumer<ResultSet, Exception> resultSetReader) throws Exception
    {
        if (!databaseFile.exists()) throw new FileNotFoundException(databaseFile.getAbsolutePath());
        try (Connection connection = connectToDatabase(databaseFile))
        {
            try (PreparedStatement statement = connection.prepareStatement(query))
            {
                for (int x=0; x<parameters.size(); x++)
                    statement.setObject(x+1, parameters.get(x).getValue(), parameters.get(x).getKey());
                if (resultSetReader == null)
                {
                    statement.execute();
                }
                else
                {
                    try (ResultSet resultSet = statement.executeQuery())
                    {
                        resultSetReader.accept(resultSet);
                    }
                }
            }
        }
    }

    public Set<String> validateDatabase(Map<String, Map<String, String>> metaData, Map<String, Map<String, String>> expectedDatabase)
    {
        Set<String> errors = new HashSet<>();
        metaData.keySet().forEach(table -> {
            if (expectedDatabase.containsKey(table))
            {
                metaData.get(table).forEach((column, type) ->{
                    if (expectedDatabase.get(table).containsKey(column))
                    {
                        if (!expectedDatabase.get(table).get(column).equals(type))
                            errors.add(String.format("Column %s in table %s is the wrong type; expected: %s actual: %s", column, table, expectedDatabase.get(table).get(column), type));
                    }
                    else
                    {
                        errors.add(String.format("Unwanted column in %s; %s type %s", table, column, metaData.get(table).get(column)));
                    }
                });
            }
            else
            {
                errors.add(String.format("Unwanted table %s", table));
            }
        });

        expectedDatabase.keySet().forEach(table -> {
            if (metaData.containsKey(table))
            {
                expectedDatabase.get(table).forEach((column, type)->{
                    if (!metaData.get(table).containsKey(column)) errors.add(String.format("Missing column in %s; %s type %s", table, column, type));
                });
            }
            else
            {
                errors.add(String.format("Missing table %s", table));
            }
        });

        return errors;
    }

    public Map<String, Map<String, String>> getMetaData(File databaseFile) throws Exception
    {
        Map<String, Map<String, String>> databaseStructure = new HashMap<>();
        executeQueryOnDatabase(databaseFile, "SELECT TABLE_NAME, COLUMN_NAME, TYPE_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'PUBLIC';", resultSet -> {
            while(resultSet.next())
            {
                String tableName = resultSet.getString("TABLE_NAME");
                if (!databaseStructure.containsKey(tableName)) databaseStructure.put(tableName, new HashMap<>());
                databaseStructure.get(tableName).put(resultSet.getString("COLUMN_NAME"), resultSet.getString("TYPE_NAME"));
            }
        });

        return databaseStructure;
    }

    private Connection connectToDatabase(File databaseFile) throws SQLException
    {
        return DriverManager.getConnection(String.format("jdbc:h2:file:%s;TRACE_LEVEL_FILE=0", databaseFile.getAbsolutePath().substring(0, databaseFile.getAbsolutePath().indexOf("."))), "sa", "");
    }
}