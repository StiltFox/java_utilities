package com.stiltfox.utilities.io;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

public class HashableFile extends HashableResource
{
    private static final ObjectMapper mapper = new ObjectMapper();
    private final File sourceFile;

    public HashableFile (File file)
    {
        sourceFile = file;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public HashableFile(String pathname)
    {
        sourceFile = new File(pathname);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void write(Object o) throws IOException
    {
        if (o != null)
        {
            try (BufferedWriter writer = Files.newBufferedWriter(sourceFile.toPath()))
            {
                mapper.writeValue(writer,o);
            }
        }
    }

    public void write(byte[] input) throws IOException
    {
        if (input != null)
        {
            Files.write(sourceFile.toPath(), input);
        }
    }

    public <T> T readObject(Class<T> tClass) throws IOException
    {
        T output = null;

        if (sourceFile.exists())
        {
            try(BufferedReader reader = Files.newBufferedReader(sourceFile.toPath()))
            {
                output = mapper.readValue(reader, tClass);
            }
        }

        return output;
    }

    public <T> T readObject(TypeReference<T> reference) throws IOException
    {
        T output = null;

        if (sourceFile.exists())
        {
            try(BufferedReader reader = Files.newBufferedReader(sourceFile.toPath()))
            {
                output = mapper.readValue(reader, reference);
            }
        }

        return output;
    }

    public File getFile()
    {
        return sourceFile;
    }

    public HashableFile[] listFiles()
    {
        File[] files = sourceFile.listFiles();
        return new ArrayList<>(files == null ? new ArrayList<>() : Arrays.stream(files).map(HashableFile::new).toList()).toArray(new HashableFile[0]);
    }

    public byte[] getData() throws IOException
    {
        return Files.readAllBytes(sourceFile.toPath());
    }

    public String getName()
    {
        int endName = sourceFile.getName().lastIndexOf(".");
        return sourceFile.getName().substring(0, endName<0? sourceFile.getName().length(): endName);
    }

    public String getExtension()
    {
        return sourceFile.getName().substring(sourceFile.getName().lastIndexOf("."));
    }
}