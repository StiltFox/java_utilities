package com.stiltfox.utilities;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class FileOps
{
    private static ObjectMapper mapper = new ObjectMapper();
    private ClassLoader classLoader = this.getClass().getClassLoader();

    public FileOps()
    {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public <T> T readJsonFile(Class<T> clazz, String path) throws IOException
    {
        T output;

        try (BufferedReader reader = Files.newBufferedReader(Path.of(path)))
        {
            output = mapper.readValue(reader, clazz);
        }

        return output;
    }

    public <T> T readJsonFile(Class<T> clazz, String path, Supplier<T> dflt)
    {
        T output;

        try
        {
            output = readJsonFile(clazz, path);
        }
        catch (IOException e)
        {
            output = dflt.get();
        }

        return output;
    }

    public String readResourceFile(String resourcePath, BiConsumer<String, Exception> onError)
    {
        String output = null;

        try
        {
            output = new String(classLoader.getResourceAsStream(resourcePath).readAllBytes());
        }
        catch (Exception e)
        {
            onError.accept(resourcePath, e);
        }

        return output;
    }

    public void saveJsonFile(Object data, String path, BiConsumer<String, Exception> onError)
    {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(path)))
        {
            mapper.writeValue(writer,data);
        }
        catch (IOException e)
        {
            onError.accept(path, e);
        }
    }

    public String readTextFile(String path, BiConsumer<String, Exception> onError)
    {
        String output = null;

        try
        {
            output = new String(Files.readAllBytes(Path.of(path)));
        }
        catch (IOException e)
        {
            onError.accept(path, e);
        }

        return output;
    }
}