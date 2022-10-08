package com.stiltfox.utilities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class HashableFile extends File
{
    private static final DataConverter converter = new DataConverter();
    private static final ObjectMapper mapper = new ObjectMapper();

    public HashableFile (File file)
    {
        super(file.getPath());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public HashableFile(String pathname)
    {
        super(pathname);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public String sha256() throws NoSuchAlgorithmException, IOException
    {
        String output;

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        output = converter.binaryToHexString(digest.digest(Files.readAllBytes(toPath())));

        if (output != null) output = output.toLowerCase(Locale.ROOT);

        return  output;
    }

    public String md5() throws NoSuchAlgorithmException, IOException
    {
        String output;

        MessageDigest digest = MessageDigest.getInstance("MD5");
        output = converter.binaryToHexString(digest.digest(Files.readAllBytes(toPath())));

        if (output != null) output = output.toLowerCase(Locale.ROOT);

        return output;
    }

    public void writeObject(Object o) throws IOException
    {
        if (o != null)
        {
            try (BufferedWriter writer = Files.newBufferedWriter(toPath(), StandardOpenOption.CREATE))
            {
                mapper.writeValue(writer,o);
            }
        }
    }

    public <T> T readObject(Class<T> tClass) throws IOException
    {
        T output = null;

        if (exists())
        {
            try(BufferedReader reader = Files.newBufferedReader(toPath()))
            {
                output = mapper.readValue(reader, tClass);
            }
        }

        return output;
    }

    public <T> T readObject(TypeReference<T> reference) throws IOException
    {
        T output = null;

        if (exists())
        {
            try(BufferedReader reader = Files.newBufferedReader(toPath()))
            {
                output = mapper.readValue(reader, reference);
            }
        }

        return output;
    }

    public HashableFile[] listFiles()
    {
        File[] files = super.listFiles();
        return new ArrayList<>(files == null ? new ArrayList<>() : Arrays.stream(files).map(HashableFile::new).toList()).toArray(new HashableFile[0]);
    }

    public String getNameWithoutExtension()
    {
        int endName = getName().lastIndexOf(".");
        return getName().substring(0, endName<0? getName().length(): endName);
    }

    public String getExtension()
    {
        return getName().substring(getName().lastIndexOf("."));
    }
}