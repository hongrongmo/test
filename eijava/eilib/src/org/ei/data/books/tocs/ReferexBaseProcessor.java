package org.ei.data.books.tocs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.data.books.tools.PDF_FileInfo;

public abstract class ReferexBaseProcessor  implements ArchiveProcessor {
  protected static Log log = LogFactory.getLog(ReferexBaseProcessor.class);

  public static final String FILE_SEP = System.getProperty("file.separator");
  public static final String BURST_AND_EXTRACTED = "V:\\EW\\BurstAndExtracted\\";

  private List<String> isbnList = new ArrayList<String>();

  public void setIsbnList(List<String> isbnlist) { this.isbnList = isbnlist; }
  public List<String> getIsbnList() { return this.isbnList; }
  
  public boolean checkIsbn(String isbn) {
    boolean result = false;
    if((isbn == null) || (!isbnList.isEmpty() && !isbnList.contains(isbn))) {
        log.debug("Unused ISBN: " + isbn);
    }
    else {
        log.debug("found isbn " + isbn);
        result = true;
    }    
    return result;
  }
  
  public String convertToIsbn13(String isbn) {
    String newisbn;
    String isbnroot = PDF_FileInfo.BN13_PREFIX + isbn.substring(0, 9);
    newisbn = isbnroot + PDF_FileInfo.getISBN13CheckDigit(isbnroot);
    return newisbn;
  }
  
  public String cleanIdentifier(String id) {
    return (id != null) ? id.replaceAll("\\p{Punct}", "") : null;
  }

}
