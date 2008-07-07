package org.ei.data.books.tocs;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class BookCreator extends PdfProcessorStamper  {

  public boolean process(String xmlpath) throws IOException {
    boolean result = false;
    File xmlFile = new File(xmlpath + FILE_SEP + "main.xml");
    String isbn = new File(xmlFile.getParent()).getName();
    if (isbn != null) {
      if (isbn.length() == 10) {
        isbn = convertToIsbn13(isbn);
      }
      if (checkIsbn(isbn)) {
        log.info(isbn + ": " + xmlFile);

        List<IncludeItem> includeitems = IssueLoader.getIssue(xmlFile).getIncludeItems();
        Iterator<IncludeItem> piis = includeitems.iterator();

        createPDF(xmlFile, isbn, piis);
        stampPDF(xmlFile, isbn);
        result = true;
      }
    }
    return result;
  }
}