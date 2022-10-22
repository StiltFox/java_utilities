package com.stiltfox.utilities.io;

import com.stiltfox.utilities.MiscOps;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public abstract class HashableResource
{
    protected static final MiscOps miscOps = new MiscOps();

    public abstract byte[] getData() throws IOException;
    public abstract String getName();
    public abstract String getExtension();

    public String sha256() throws NoSuchAlgorithmException, IOException
    {
        return miscOps.hashBinaryValue(getData(), "SHA-256");
    }

    public String md5() throws NoSuchAlgorithmException, IOException
    {
        return miscOps.hashBinaryValue(getData(), "MD5");
    }
}