package org.ei.bulletins;

import java.io.IOException;
import java.io.Writer;

public class BulletinResultNavigator {

    public int docIndex;
    public int pageSize;
    public int hitCount;

    public BulletinResultNavigator(int docIndex, int pageSize, int hitCount) {
        this.docIndex = docIndex;
        this.pageSize = pageSize;
        this.hitCount = hitCount;
    }
    public int getHitCount() {

        return hitCount;
    }
    /** 
    	 * @return
    	 */
    public void toXML(Writer out) throws IOException {
        int nextPage = getNextPage();
        int previousPage = getPreviousPage();

        out.write("<NAV NEXT=\"");
        out.write(Integer.toString(nextPage));
        out.write("\" PREV=\"");
        out.write(Integer.toString(previousPage));
        out.write("\" INDEX=\"");
        out.write(Integer.toString(docIndex));
        out.write("\"/>");
    }
   
    /** 
    	 * @return
    	 */
    private int getNextPage() {

        int offSet = ((getPageIndex(pageSize, docIndex) - 1) * pageSize) + 1;
       
        int nextPage = offSet + pageSize;
        
        if (nextPage > hitCount) {
            nextPage = 0;
        }

        return nextPage;
    }
    /** 
    	 * @return
    	 */
    private int getPreviousPage() {

        int offSet = ((getPageIndex(pageSize, docIndex) - 1) * pageSize) + 1;
        int previousPage = offSet - pageSize;
        if (previousPage < 1) {
            previousPage = 0;
        }

        return previousPage;
    }
    private int getPageIndex(int pSize, int dIndex) {
        int pageIndex;

        if ((dIndex % pSize) == 0) {
            // if the remainder is zero the page index will find in the following way
            pageIndex = (dIndex / pSize);
        } else {
            // otherwise find the page index in the following way
            pageIndex = (dIndex / pSize) + 1;
        }

        return pageIndex;

    }
    public int getOffSet(){
		int offSet = ((getPageIndex(pageSize, docIndex) - 1) * pageSize) + 1;
		
		return offSet;
    }
    /** 
     * @return
     */
    public BulletinPage filter(BulletinPage page) {

        int hitCount = page.size();

        BulletinPage nPage = new BulletinPage();
        
       
        int offSet = getOffSet() - 1;

        for (int i = offSet; i < hitCount; i++) {
        	
            nPage.add((Bulletin) page.get(i));
            if (nPage.size() == pageSize)
                break;
        }

        return nPage;

    }
}