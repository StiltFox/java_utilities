package com.stiltfox.utilities.io;

import com.stiltfox.utilities.MiscOps;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadableUrl extends HashableResource
{
    private static MiscOps miscOps = new MiscOps();
    private URL url;
    private byte[] data = null;

    public DownloadableUrl(String spec) throws MalformedURLException
    {
        url = new URL(spec);
    }

    public void downloadTo(HashableFile location) throws IOException
    {
        if (location != null) location.write(getData());
    }

    public String getExtension()
    {
        return url.getPath().equals("") || ! url.getPath().contains(".") ? ".html" : url.getPath().substring(url.getPath().lastIndexOf("."));
    }

    public String getName()
    {
        return url.toString();
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