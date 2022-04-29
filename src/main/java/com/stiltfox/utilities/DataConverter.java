package com.stiltfox.utilities;

public class DataConverter
{
    public String convertBinaryToHexString(byte[] dataToConvert)
    {
        char[] output =  new char[dataToConvert.length * 2];

        for (int x = 0; x< dataToConvert.length; x++)
        {
            output[x*2] = convertNibble((dataToConvert[x] >> 4) & 0x0f);
            output[(x*2)+1] = convertNibble(dataToConvert[x] & 0x0f);
        }

        return new String(output);
    }

    private char convertNibble(int data)
    {
        return (char)(data > 9 ? (data - 9) | 0x40 : data | 0x30);
    }
}
