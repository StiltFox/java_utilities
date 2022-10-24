package com.stiltfox.utilities.io;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface HashableResource
{
    String getNameWithoutExtension();
    String getExtension();
    String sha256() throws IOException, NoSuchAlgorithmException;
    String md5() throws IOException, NoSuchAlgorithmException;
}
