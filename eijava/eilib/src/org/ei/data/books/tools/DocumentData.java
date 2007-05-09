package org.ei.data.books.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DocumentData {

    private static Log log = LogFactory.getLog(DocumentData.class);
    
	private Bookmarks bookmarks = new Bookmarks();
	private long page_count = 0;
	
	public DocumentData(String filepathname) {
    	readDocumentData(filepathname);
	}
	public Bookmarks getBookmarks() {
		return bookmarks;
	}

	public void setBookmarks(Bookmarks bookmarks) {
		this.bookmarks = bookmarks;
	}

	public long getPageCount() {
		return page_count;
	}

	public void setPageCount(long page_count) {
		this.page_count = page_count;
	}

	private void readDocumentData(String filepathname) {

        Bookmark curr_bkmk = null;

        Stack parents = new Stack();
		String title = null;
		long page_num = 1;
        int level = 0;
		int curr_level = 1;
        Bookmarks curr_bookmarks = null; 
		int MAX_FIELDS = 2;

		String PAGES_TOKEN = "NumberOfPages";
		String BKMK_TITLE_TOKEN = "BookmarkTitle";
		String BKMK_LEVEL_TOKEN = "BookmarkLevel";
		String BKMK_PAGENUM_TOKEN = "BookmarkPageNumber";

        File doc_data = new File(filepathname + System.getProperty("file.separator") + "doc_data.txt");
		BufferedReader rdr = null;
        try {
            rdr = new BufferedReader(new FileReader(doc_data));

            curr_bookmarks = getBookmarks();
    		while (rdr.ready()) {
    			String aline = rdr.readLine();
    			String[] fields = aline.split("\\:", MAX_FIELDS);
    
    			if ((fields != null) && (fields.length >= MAX_FIELDS)) {
    				if (PAGES_TOKEN.equalsIgnoreCase(fields[0])) {
    					setPageCount(Long.parseLong(fields[1].trim()));
    					continue;
    				}
    				if (BKMK_TITLE_TOKEN.equalsIgnoreCase(fields[0])) {
    					title = fields[1].trim();
    					continue;
    				} else if (BKMK_LEVEL_TOKEN.equalsIgnoreCase(fields[0])) {
    					level = Integer.parseInt(fields[1].trim());
    					continue;
    				} else if (BKMK_PAGENUM_TOKEN.equalsIgnoreCase(fields[0])) {
    					page_num = Long.parseLong(fields[1].trim());
    					// fall through with title, level and page_num and create
    					// bookmark
    				} else {
    					continue;
    				}
    
    				Bookmark bkmk = new Bookmark(page_num, title, level);
    				if (curr_level == level) {
    					// do nothing
    				} else if (curr_level < level) { 
                        // deeper level
    					parents.push(curr_bookmarks);
    					curr_bookmarks = curr_bkmk.getChildren();
    				} else { 
                        // lower level
                        // we may be jumping more than one level back up 
                        // (i.e. from 3 back up to 1) that is why we loop
    					while (level < curr_level--) {
    						curr_bookmarks = (Bookmarks) parents.pop();
    					}
    				}
    				curr_bookmarks.add(bkmk);
    				curr_level = level;
    				curr_bkmk = bkmk;
    			}
    
    		} // while() file reader

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if(rdr != null) {
                try {
                    rdr.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
		
		// check to make sure the first bookmark starts at page 1
		// if not - add a bookmark for page 1
//		Bookmark firstmark = (Bookmark) bookmarks.get(0);
//		if(!bookmarks.contains(Bookmark.BKMK_COVER)  && (firstmark.getPage() != 1)) {
//			log.debug(filepathname + " Adding bookmark: [" + Bookmark.BKMK_COVER + "] First bookmark was: [" + firstmark + "]");
//			bookmarks.add(1, Bookmark.BKMK_COVER);
//		}

        
//		// loop through bookmarks fixing bookmarks with page number 0
//		Iterator itrmk = bookmarks.iterator();
//		int i = 1;
//		while(itrmk.hasNext()) {
//			Bookmark mark = (Bookmark) itrmk.next();
//			if(mark.getPage() == 0) {
//                log.error("Found bookark page 0!");
//				// try to find page number from sub-level bookmark(s)
//				long markPage = getBookmarkPage(mark.getChildren());
//				if(markPage == 0) {
//					// try to find page number from same level bookmark(s)
//					markPage = getBookmarkPage(bookmarks.subList(i,bookmarks.size()));
//				}
//				mark.setPage(markPage);
//
//				if(mark.getPage() == 0) {
//					log.error(filepathname + " Chapter page is 0! " + mark);
//					log.error(" Couldn't fix !!!! " + mark);
//				}
//				else {
//					//log.error(" FIXED with " + mark.getPage());
//				}
//			}
//			i++;
//		}
	} // readDocumentData()
//
//	private long getBookmarkPage(List bookmarks)
//	{
//		Iterator itrmk = bookmarks.iterator();
//		long newpage = 0;
//		while(itrmk.hasNext()) {
//			Bookmark mark = (Bookmark) itrmk.next();
//			if(mark.getPage() != 0) {
//				newpage = mark.getPage();
//			}
//			if(newpage != 0) {
//				break;
//			}
//		}
//		return newpage;
//	}


}
