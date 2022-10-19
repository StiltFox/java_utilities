package com.stiltfox.utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MiscOps
{
    private static final DataConverter converter = new DataConverter();

    public String getVersionNumber(Class<?> clazz)
    {
        return clazz.getPackage().getImplementationVersion();
    }

    public <T> List<T> getUnion(List<T>... lists)
    {
        List<T> output = new ArrayList<>();
        List<T> shortest = lists[0];
        for (List<T> list : lists) shortest = list.size() < shortest.size() ? list : shortest;


        for (T item : shortest)
        {
            boolean union = true;
            for (List<T> list:lists) union &= list.contains(item);
            if (union) output.add(item);
        }

        return output;
    }

    public String hashBinaryValue(byte[] value, String algorithm) throws NoSuchAlgorithmException
    {
        String output;

        MessageDigest digest = MessageDigest.getInstance(algorithm);
        output = converter.binaryToHexString(digest.digest(value));

        if (output != null) output = output.toLowerCase(Locale.ROOT);

        return  output;
    }
}