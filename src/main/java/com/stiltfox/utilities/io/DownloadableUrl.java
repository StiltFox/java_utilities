package com.stiltfox.utilities.io;

import com.stiltfox.utilities.MiscOps;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

public class DownloadableUrl implements HashableResource
{
    private static MiscOps miscOps = new MiscOps();
    private URL url;
    private byte[] data = null;

    public DownloadableUrl(String spec) throws MalformedURLException
    {
        url = new URL(spec);
    }

    public void copyTo(File file) throws IOException
    {
        copyTo(new HashableFile(file));
    }

    public void copyTo(HashableFile location) throws IOException
    {
        if (location != null) location.writeData(getData());
    }

    public String getFullName()
    {
        return url.toString();
    }

    public String getNameWithoutExtension()
    {
        return url.getHost() + (url.getPath().contains(".") ? url.getPath().substring(0, url.getPath().lastIndexOf(".")) : url.getPath());
    }

    public String getExtension()
    {
        return url.getPath().equals("") || !url.getPath().contains(".") ? ".html" : url.getPath().substring(url.getPath().lastIndexOf("."));
    }

    public String sha256() throws IOException, NoSuchAlgorithmException
    {
        return miscOps.hashBinaryValue(getData(), "SHA-256");
    }

    public String md5() throws IOException, NoSuchAlgorithmException
    {
        return miscOps.hashBinaryValue(getData(), "MD5");
    }

    public byte[] getData() throws IOException
    {
        if (data == null)
        {
            try (BufferedInputStream stream = new BufferedInputStream(url.openStream()))
            {
                data = stream.readAllBytes();
            }
        }

        return data;
    }
}