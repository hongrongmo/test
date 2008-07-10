package org.ei.data.books.tocs;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class BookSeriesCreator extends PdfProcessorStamper {

  public boolean process(String xmlpath) throws IOException {
    boolean result = false;
    File xmlFile = new File(xmlpath + FILE_SEP + "issue.xml");
    Issue anissue = IssueLoader.getIssue(xmlFile);
    if (anissue != null) {
      String issn = cleanIdentifier(anissue.getIssn());
      String isbn = cleanIdentifier(anissue.getIsbn());
      if ((isbn != null) && checkIsbn(isbn)) {
        if (isbn.length() == 10) {
          isbn = convertToIsbn13(isbn);
        }
        log.info(isbn + "/" + issn + ": " + xmlFile);

        List<IncludeItem> issuearticles = anissue.getIncludeItems();
        Iterator<IncludeItem> includeitems = issuearticles.iterator();

        while (includeitems.hasNext()) {
          IncludeItem includeitem = includeitems.next();
          String pii = cleanIdentifier(includeitem.getPii());
          String mainxmlfolder = pii.replace("S" + issn, "");
          // article file is in folder which is article pii without the
          // "S<issn>" as the prefix
          File mainxmlFile = new File(xmlpath
              + FILE_SEP + mainxmlfolder
              + FILE_SEP + "main.xml");
          if (mainxmlFile.exists()) {
            Article article = ArticleLoader.getArticle(mainxmlFile);
            if (article == null) {
              log.info(mainxmlFile);
            } else {
              log.debug("\t\t" + article.getTitle());
              result = true;
            }
          } else {
            log.info("\t Cannot find " + mainxmlFile + "!");
          }

        } // while
      }
    }
    return result;
  }
}