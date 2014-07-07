package org.ei.data.books.tocs;

import java.io.IOException;

public interface ArchiveProcessor {

  public boolean process(String xmlpath) throws IOException;

}
