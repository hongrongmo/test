
package org.ei.domain;

import org.ei.exception.SearchException;
import org.ei.exception.SystemErrorCodes;


public class PageCacheException extends SearchException
{
    private static final long serialVersionUID = -893181600613340749L;

    public PageCacheException(String message, Exception e) {
        super(SystemErrorCodes.PAGE_CACHE_ERROR, message, e);
    }

    public PageCacheException(Exception e) {
        super(SystemErrorCodes.PAGE_CACHE_ERROR, e);
    }

}
