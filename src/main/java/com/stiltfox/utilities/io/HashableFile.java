package com.stiltfox.utilities.io;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stiltfox.utilities.MiscOps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class HashableFile extends File implements HashableResource
{
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final MiscOps miscOps = new MiscOps();

    public HashableFile (File file)
    {
        this(file.getPath());
    }

    public HashableFile(String pathname)
    {
        super(pathname);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void writeData(Object o) throws IOException
    {
        if (o != null)
        {
            try (BufferedWriter writer = Files.newBufferedWriter(toPath()))
            {
                mapper.writeValue(writer,o);
            }
        }
    }

    public void writeData(byte[] input) throws IOException
    {
        if (input != null)
        {
            Files.write(toPath(), input);
        }
    }

    public <T> T readObject(Class<T> tClass) throws IOException
    {
        T output = null;

        if (exists())
        {
            String data = new String(Files.readAllBytes(toPath()));
            if (data.trim().isEmpty()) data = "{}";

            output = mapper.readValue(data, tClass);
        }

        return output;
    }

    public <T> T readObject(TypeReference<T> reference) throws IOException
    {
        T output = null;

        if (exists())
        {
            String data = new String(Files.readAllBytes(toPath()));
            if (data.trim().isEmpty()) data = "{}";

            output = mapper.readValue(data, reference);
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

    public String sha256() throws IOException, NoSuchAlgorithmException
    {
        return miscOps.hashBinaryValue(Files.readAllBytes(toPath()), "SHA-256");
    }

    public String md5() throws IOException, NoSuchAlgorithmException
    {
        return miscOps.hashBinaryValue(Files.readAllBytes(toPath()), "MD5");
    }

    public String getFullName()
    {
        return getAbsolutePath();
    }

    public void copyTo(File location) throws IOException
    {
        Files.copy(toPath(), location.toPath());
    }
}