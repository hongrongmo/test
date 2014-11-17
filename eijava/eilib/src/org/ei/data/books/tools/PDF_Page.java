package org.ei.data.books.tools;

import java.io.File;
import java.text.DecimalFormat;

public class PDF_Page implements Visitable {

	private static DecimalFormat df = new DecimalFormat("0000");
	static {
		df.setMinimumIntegerDigits(4);
	}

	private long page = 1;
	private String documentpath = null;
	private Bookmark chapter = null;
    private Bookmark section = null;

	public PDF_Page(String docname, long pagenum, Bookmark chapterbkmk, Bookmark sectionmk) {
		documentpath = docname;
		page = pagenum;
		chapter = chapterbkmk;
        section = sectionmk;
	}

	public long getPageNum() {
		return page;
	}

	public Bookmark getChapter() {
		return chapter; 
	}

    public Bookmark getSection() {
        return section; 
    }
    
	public String getTextFilePath() {
		return (documentpath + System.getProperty("file.separator") + "pg_" 
				+ df.format(page) + ".txt"); 
	}

	public long getTextSizeBytes() {
		long page_txt_bytes = 0;
		File page_data_file = new File(this.getTextFilePath());
		if ((page_data_file != null) && (page_data_file.exists())){
			page_txt_bytes = page_data_file.length();
		}
		page_data_file = null;
		return page_txt_bytes;
	}
    
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }    
}