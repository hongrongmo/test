/**
 *
 */
package org.ei.books;

import java.io.IOException;

/**
 * @author harovetm
 *
 * Interface to fetch book parts.  Meant to be used within a Book document to fetch book
 * parts from an external source.
 */
public interface IBookPartFetcher {

    public String getBookPart(String isbn13, String part) throws IOException;

}
