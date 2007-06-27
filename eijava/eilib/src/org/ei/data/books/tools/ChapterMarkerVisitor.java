package org.ei.data.books.tools;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ChapterMarkerVisitor extends BookVisitor {

    private static Pattern p = Pattern.compile("^\\d(\\.)?[^\\d]");
    private static Pattern p2 = Pattern.compile("^\\w(\\.)");

    public void visit(Bookmark mark) {

        if(mark.getTitle().toLowerCase().replaceAll("\\s","").indexOf("chapter") >= 0) {
//            log.info("matched 'chapter' " + mark);
            mark.setChapter(true);
        } else {
            Matcher m = null;
            m = p.matcher(mark.getTitle());
            if (m.find()) {
//                log.info("matched 1 "  + mark);
                mark.setChapter(true);
            }
            else {
                m = p2.matcher(mark.getTitle());
                if (m.find()) {
//                    log.info("matched 2 "  + mark);
                    mark.setChapter(true);
                }
            }
        }
        if(!mark.isChapter()) {
            mark.getChildren().accept(this);
        }
    }

    public void visit(PDF_FileInfo pdffile) {
        pdffile.getBookmarks().accept(this);
    }

    public void visit(Bookmarks marks) {
        Bookmark bkmk = null;
        Iterator itrbkmks = marks.iterator();
        while (itrbkmks.hasNext()) {
            bkmk = (Bookmark) itrbkmks.next();
            bkmk.accept(this);
        }

    }


}
