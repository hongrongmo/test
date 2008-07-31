package org.ei.data.books.tocs;

import java.util.HashMap;
import java.util.Map;

import org.ei.data.books.tools.PDF_FileInfo;


public abstract class ArchiveMapper {

  public abstract void createmap();
  private Map<String, String> archiveMap = new HashMap<String, String>();

  public String getArchviePath(String isbn) {
    return archiveMap.get(isbn);
  }

  public void addArchviePath(String isbn, String archivepath) {
    archiveMap.put(isbn, archivepath);
  }
  public String cleanIdentifier(String id) {
    return (id != null) ? id.replaceAll("\\p{Punct}", "") : null;
  }

  public String convertToIsbn13(String isbn) {
    String newisbn;
    String isbnroot = PDF_FileInfo.BN13_PREFIX + isbn.substring(0, 9);
    newisbn = isbnroot + PDF_FileInfo.getISBN13CheckDigit(isbnroot);
    return newisbn;
  }
}
