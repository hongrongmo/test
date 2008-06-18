package org.ei.data.books.tools;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PDF_FileInfo implements Visitable {
    protected static Log log = LogFactory.getLog(PDF_FileInfo.class);
  
    public static final String BN13_PREFIX = "978";


    private static DecimalFormat df;
	static {
        df = new DecimalFormat("0000");
		df.setMinimumIntegerDigits(4);
	}
	public static String formatPageNumber(long pagenum) {
		return df.format(pagenum);
	}

	private Bookmarks allbookmarks = null;
    public Iterator createIterator() {
        return new BookChapterIterator(allbookmarks);
    }
    
	private long page_count = 0;
	//private int chapterBookmarkLevel = 1;
   
	private String isbn = null;
	private String isbn13 = null;
	private String filepathname = null;

	public PDF_FileInfo(File fileobj) throws IOException {

		String subdirname = fileobj.getName();
        subdirname = subdirname.replaceAll("(\\.pdf|Folder)", "");
        
        String subdirpath = fileobj.getParent() + System.getProperty("file.separator") + subdirname;

        String isbn = subdirname.replaceAll("^e", "");
        String isbnSuffix = null;
        if(isbn.endsWith("a") || isbn.endsWith("b") || isbn.endsWith("c")) {
            isbnSuffix = isbn.substring(isbn.length()-1);
        }
        log.info(isbn);
        if(!isbn.startsWith(BN13_PREFIX)) {
            setIsbn(isbn);
            String isbnroot = PDF_FileInfo.BN13_PREFIX + getIsbn().substring(0, 9);
            setIsbn13(isbnroot + PDF_FileInfo.getISBN13CheckDigit(isbnroot) + ((isbnSuffix != null) ? isbnSuffix : ""));
        }
        else {
            setIsbn13(isbn);
            String isbnroot = getIsbn13().substring(3, 12);
            setIsbn(isbnroot + PDF_FileInfo.getISBN10CheckDigit(isbnroot) + ((isbnSuffix != null) ? isbnSuffix : ""));
        }
        setFilePathname(subdirpath);

        DocumentData dd = new DocumentData(subdirpath);
		setPageCount(dd.getPageCount());
		setBookmarks(dd.getBookmarks());
	}

	public PDF_Page getPage(long curpage) {

		Bookmark chapter = getContainingChapter(curpage);
        Bookmark section = getContainingSection(curpage);
		return new PDF_Page(filepathname, curpage, chapter, section);
	}

	public Bookmarks getBookmarks() {
		return allbookmarks;
	}

	public void setBookmarks(Bookmarks bookmarks) {
		allbookmarks = bookmarks;
	}
	
	public String getFilePathname() {
		return filepathname;
	}
    public void setFilePathname(String path) {
        filepathname = path;
    }
	public void setPageCount(long pages) {
		page_count = pages;
	}

	public long getPageCount() {
		return page_count;
	}

	public String getIsbn() {
		return isbn;
	}
    public void setIsbn(String anisbn) {
        isbn = anisbn;
    }
    
	public Bookmark getContainingChapter(long curpage) {
        Bookmark bookmark = null;
        Iterator itr = this.createIterator();
        while (itr.hasNext()) {
            bookmark = (Bookmark) itr.next();
            if(bookmark.isChapter()) {
              if ((curpage >= bookmark.getPage()) && (curpage < bookmark.getEndpage())) {
                  break;
              } 
            }
            bookmark = null;
        }
        // null is OK!! Some pages are NOT inside of chapters
		return bookmark;
	}
    
    public Bookmark getContainingSection(long curpage) {
        Bookmark bookmark = null, prevchapter = null;
        Iterator itr = this.createIterator();
        while (itr.hasNext()) {
            bookmark = (Bookmark) itr.next();

            if (bookmark.getPage() <= curpage) {
                prevchapter = bookmark;
            } else {
                bookmark = prevchapter;
                break;
            }
        }
        if(bookmark == null) {
            if(curpage == 1) {
                bookmark = Bookmark.BKMK_COVER;
            }
            else {
                bookmark = Bookmark.BKMK_FRONTMATTER;
            }
        }
        return bookmark;
    }
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    /**
     * Calculate the check digit for a 13 digit ISBN
     * @param isbn - the ISBN without dashes
     * @return the check digit
     */
    public static char getISBN13CheckDigit(final String isbn) {
        int len = isbn.length();
        int digitSum = 0;
        int calcValue = 0;

        // length of passed in isbn must be 12 or 
        // we will get a nullpointer here
        for (int i = 0; i < 12; i++) {
            int val = Integer.parseInt((isbn.substring(i, i + 1)));
            if (i % 2 == 1) {
                digitSum += val * 3;
            } else {
                digitSum += val;
            }
        }

        calcValue = (10 - (digitSum % 10)) % 10;

        return (char) (calcValue + '0');
    }

//    s = 0×10 + 3×9 + 0×8 + 6×7 + 4×6 + 0×5 + 6×4 + 1×3 + 5×2
//    =    0 +  27 +   0 +  42 +  24 +   0 +  24 +   3 +  10
//    = 130
//  (130 / 11 = 11 remainder 9) or (130 mod 11)
//  11 - 9 = 2
//  (2 / 11 = 0 remainder 2) or (2 mod 11)
    private static char getISBN10CheckDigit(final String isbn) {
        
        int digitSum = 0;
        int calcValue = 0;

        // length of passed in isbn must be 9 or 
        // we will get a nullpointer here
        for (int i = 0; i < 9; i++) {
            int val = Integer.parseInt((isbn.substring(i, i + 1)));
            int weight = 10 - (i % 10);
            digitSum += val * weight;
        }

        calcValue = (11 - (digitSum % 11)) % 11;
        if(calcValue == 10 ) {
            return (char) ('X');
        }
        else 
        {
            return (char) (calcValue + '0');
        }
    }
  
    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

//    public int getChapterBookmarkLevel() {
//        return chapterBookmarkLevel;
//    }
//
//    public void setChapterBookmarkLevel(int chapterBookmarkLevel) {
//        this.chapterBookmarkLevel = chapterBookmarkLevel;
//    }

} // private class PDF_Fileinfo

