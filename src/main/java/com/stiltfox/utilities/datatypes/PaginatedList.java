package com.stiltfox.utilities.datatypes;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class PaginatedList<T> extends ArrayList<T>
{
    @Getter
    private int pageSize;

    public PaginatedList(List<T> list, int pageSize)
    {
        super();
        addAll(list);
        setPageSize(pageSize);
    }

    public PaginatedList(int pageSize)
    {
        super();
        setPageSize(pageSize);
    }

    public void setPageSize(int size)
    {
        pageSize = Math.max(size, 1);
    }

    public int getNumberOfPages()
    {
        int numPages = size() / pageSize;
        if ((size() % pageSize) > 0) numPages++;
        return numPages;
    }

    public List<T> getPage(int pageNumber)
    {
        ArrayList<T> output = new ArrayList<>();
        int pageToUse = Math.max(Math.min(pageNumber, getNumberOfPages()-1), 0);
        int startPos = (pageToUse) * pageSize;
        int endPos = Math.min(startPos + pageSize, size());

        for (int x=startPos; x<endPos; x++) output.add(get(x));

        return output;
    }
}