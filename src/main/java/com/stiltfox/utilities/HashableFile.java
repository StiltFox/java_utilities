package com.stiltfox.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class HashableFile extends File
{
    private static final DataConverter converter = new DataConverter();

    public HashableFile (File file)
    {
        super(file.getPath());
    }

    public HashableFile(String pathname)
    {
        super(pathname);
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