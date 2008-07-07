package org.ei.data.books.tocs;

import java.io.IOException;

public interface ArchiveMapper {

  public int map(ArchiveProcessor processor) throws IOException;
}
