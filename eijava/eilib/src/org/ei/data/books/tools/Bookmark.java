package org.ei.data.books.tools;


public class Bookmark implements Comparable, Visitable {

	public static String BKMK_COVER_TITLE = "Cover";
	public static String BKMK_FRONTMATTER_TITLE = "Frontmatter";

	public static Bookmark BKMK_COVER = new Bookmark(1, Bookmark.BKMK_COVER_TITLE);
	public static Bookmark BKMK_FRONTMATTER = new Bookmark(1, Bookmark.BKMK_FRONTMATTER_TITLE);
	
	private long page = 1;
    private long endpage = 0;
	private int level = 1;
	private String title = "";
	private Bookmarks children = new Bookmarks();
	
	public Bookmark() {
	}
	
	public Bookmark(long bkmkpage, String strtitle) {
		if(strtitle != null) {
			title = strtitle.replaceAll("\\&\\#[^;](\\w)*;", "");
        }
		page = bkmkpage;
	}

	public Bookmark(long bkmkpage, String strtitle, int bkmklevel) {
		this(bkmkpage, strtitle);
		level = bkmklevel;
	}

	public String getTitle() {
		return title;
	}

	public long getPage() {
		return page;
	}

	public void setPage(long pagenum) {
		page = pagenum;
	}

	public int getLevel() {
		return level;
	}
	
	public Bookmarks getChildren() {
		return children;
	}

	public boolean equals(Object arg0) {
        if (!(arg0 instanceof Bookmark))
            return false;
        Bookmark bkmk = (Bookmark) arg0;
        
        return bkmk.getTitle().equals(this.getTitle()) &&
               bkmk.getPage() == this.getPage() &&
               bkmk.getLevel() == this.getLevel();
    }

	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
        if (!(arg0 instanceof Bookmark))
            return -1;

        Bookmark bkmk = (Bookmark) arg0;
        return (new Long(this.getPage())).compareTo(new Long(bkmk.getPage()));
	}

	public String toString() {
        String indent = "";
        for(int i = 1; i < level; i++)
        {
            indent += "    ";
        }
		return indent + " Level: " + level + " Page: " + page + "-" + endpage + " Title: " + title;
	}

    public long getEndpage() {
        return endpage;
    }

    public void setEndpage(long endpage) {
        this.endpage = endpage;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}