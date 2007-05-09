package org.ei.data.books.tools;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;

public class PDF_FileInfo implements Visitable {
    
    private static final String BN13_PREFIX = "987";


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

   
	private String isbn = null;
	private String isbn13 = null;
	private String filepathname = null;

	public PDF_FileInfo(File fileobj) throws IOException {

		String subdirname = fileobj.getName();
        subdirname = subdirname.replaceAll("(\\.pdf|Folder)", "");
        
        String subdirpath = fileobj.getParent() + System.getProperty("file.separator") + subdirname;

        setIsbn(subdirname.replaceAll("^e", ""));
        String bn13root = PDF_FileInfo.BN13_PREFIX + getIsbn().substring(0, 9);
        setIsbn13(bn13root + getISBN13CheckDigit(bn13root));
        setFilePathname(subdirpath);

        DocumentData dd = new DocumentData(subdirpath);
		setPageCount(dd.getPageCount());
		setBookmarks(dd.getBookmarks());
	}

	public PDF_Page getPage(long curpage) {

		Bookmark chapter = getContainingChapter(curpage);
		return new PDF_Page(filepathname, curpage, chapter);
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
		Iterator itrbkmks = getBookmarks().iterator();
	
		Bookmark bookmark = null, prevchapter = null;
		while (itrbkmks.hasNext()) {
			bookmark = (Bookmark) itrbkmks.next();
			long chappage = bookmark.getPage(); 

			if (chappage <= curpage) {
				prevchapter = bookmark;
				continue;
			} else {
				bookmark = prevchapter;
				break;
			}
		}
		if (bookmark == null) {
			bookmark = prevchapter;
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
    private static char getISBN13CheckDigit(final String isbn) {
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

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

} // private class PDF_Fileinfo

