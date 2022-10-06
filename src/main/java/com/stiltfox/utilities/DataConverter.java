package com.stiltfox.utilities;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.UUID;

public class DataConverter
{
    public String binaryToHexString(byte[] dataToConvert)
    {
        char[] output =  new char[dataToConvert.length * 2];

        for (int x = 0; x< dataToConvert.length; x++)
        {
            output[x*2] = convertNibble((dataToConvert[x] >> 4) & 0x0f);
            output[(x*2)+1] = convertNibble(dataToConvert[x] & 0x0f);
        }

        return new String(output);
    }

    public UUID binaryToUUID(byte[] bytes)
    {
        return binaryToUUID(new ByteArrayInputStream(bytes));
    }

    public UUID binaryToUUID(InputStream binaryStream)
    {
        UUID categoryId;

        try
        {
            ByteBuffer buffer = ByteBuffer.wrap(binaryStream.readAllBytes());
            categoryId = new UUID(buffer.getLong(), buffer.getLong());
        }
        catch (IOException e)
        {
            categoryId = new UUID(0x00, 0x00);
        }

        return categoryId;
    }

    private char convertNibble(int data)
    {
        return (char)(data > 9 ? (data - 9) | 0x40 : data | 0x30);
    }
}
