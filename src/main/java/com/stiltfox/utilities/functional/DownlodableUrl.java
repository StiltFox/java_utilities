package com.stiltfox.utilities.functional;

import com.stiltfox.utilities.HashableFile;
import com.stiltfox.utilities.MiscOps;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

public class DownlodableUrl
{
    private static MiscOps miscOps = new MiscOps();
    private URL url;
    private byte[] data = null;

    public DownlodableUrl(String spec) throws MalformedURLException
    {
        url = new URL(spec);
    }

    public String sha256() throws IOException, NoSuchAlgorithmException
    {
        downloadResource(false);
        return miscOps.hashBinaryValue(data, "SHA-256");
    }

    public void downloadTo(HashableFile location) throws IOException
    {
        downloadResource(false);
        if (location != null) location.writeBinary(data);
    }

    private void downloadResource(boolean clearCashe) throws IOException
    {
        if (data == null || clearCashe)
        {
            try (BufferedInputStream stream = new BufferedInputStream(url.openStream()))
            {
                data = stream.readAllBytes();
            }
        }
    }
}
