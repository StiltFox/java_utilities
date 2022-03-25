package com.stiltfox.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MiscOps
{
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
}