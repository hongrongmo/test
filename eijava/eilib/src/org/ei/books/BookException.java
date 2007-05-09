package org.ei.books;

import org.ei.exception.BasicException;

public class BookException
    extends BasicException
{

    public BookException(Exception e)
    {
        super(e);
    }

}