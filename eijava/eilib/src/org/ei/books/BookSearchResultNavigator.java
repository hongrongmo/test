package org.ei.books;

import java.io.IOException;
import java.io.Writer;

public class BookSearchResultNavigator
{

    public int docIndex;
    public int pageSize;
    public int resultCount;



    public BookSearchResultNavigator(int docIndex,
                        int pageSize,
                        int resultCount)
    {
        this.docIndex = docIndex;
        this.pageSize = pageSize;
        this.resultCount = resultCount;
    }


    public void toXML(Writer out)
        throws IOException
    {
        int nextPage = getNextPage();
        int previousPage = getPreviousPage();

        out.write("<NAV NEXT=\"");
        out.write(Integer.toString(nextPage));
        out.write("\" PREV=\"");
        out.write(Integer.toString(previousPage));
        out.write("\" INDEX=\"");
        out.write(Integer.toString(docIndex));
        out.write("\"/>");
    }

    private int getNextPage()
    {
        int offSet = ((getPageIndex(pageSize, docIndex)-1) * pageSize)+1;
        int nextPage  = offSet+pageSize;
        if(nextPage > resultCount)
        {
            nextPage = 0;
        }

        return nextPage;
    }

    private int getPreviousPage()
    {
        int offSet = ((getPageIndex(pageSize, docIndex)-1) * pageSize)+1;
        int previousPage  = offSet-pageSize;
        if(previousPage < 1)
        {
            previousPage = 0;
        }

        return previousPage;
    }

    private int getPageIndex(int pSize, int dIndex)
    {
        int pageIndex;

        if ( (dIndex%pSize) == 0 )
        {
            // if the remainder is zero the page index will find in the following way
            pageIndex  = (dIndex/pSize);
        }
        else
        {
                        // otherwise find the page index in the following way
            pageIndex  = (dIndex/pSize) + 1;
        }

        return pageIndex;

    }






}