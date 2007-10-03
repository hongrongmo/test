package org.ei.data.books.tocs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class PageRange implements Comparable  {
    protected static Log log = LogFactory.getLog(PageRange.class);

    private String start = null;
    private String end = null;

    public PageRange() {
    }

    public List sequence() {
        List sq = new ArrayList();
        if(length() == 1) {
            sq.add((isRoman()) ? Roman.roman(getFirst_page()) : getFirst_page());
        }
        else {
            for(int x=getFirst_page(); x <= getLast_page(); x++) {
                sq.add((isRoman()) ? Roman.roman(x) : String.valueOf(x));
            }
        }
        return sq;
    }
    
    public int length() {
        int len = -1;
        try {
            if((getLast_page() != 0) && (getLast_page() > getFirst_page())) {
                return getLast_page() - getFirst_page() + 1;
            }
            else {
                len = 1;
            }
        }
        catch(IllegalArgumentException e) {
            len = -1;
        }
        
        return len;
            
    }

    public int compareTo(Object o) {
        PageRange range = (PageRange) o;
        // TODO Auto-generated method stub
//        if(getStart() < range.getStart()) {
//            return -1;
//        }
//        else if(getStart() > range.getStart()) {
//            return 1;
//        }
        return 0;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String ed) {
        this.end = ed;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String st) {
        this.start = st;
    }

    public String toString() {
        // TODO Auto-generated method stub
        return " (" + (getStart()) + ((getEnd() != null) ? ", " + (getEnd()) : "") + ")";
    }
    
    public int getLast_page() {
        return format(getEnd());
    }

    public int getFirst_page() {
        return format(getStart());
    }

    private int format(String pageno) {
        int page = 0;
        if(pageno != null) {
            try {
                if(Roman.isRoman(pageno)) {
                    page = Roman.arabic(pageno);
                }
                else {
                    pageno = pageno.replaceAll("\\D", "");
                    page = Integer.parseInt(pageno);
                }
            } catch(NumberFormatException e) {
                log.error("error");
            } catch(IllegalArgumentException e) {
                log.error("error");
            }
        }
        return page;
    }

    public boolean isRoman() {
        return Roman.isRoman(start) || Roman.isRoman(end);
    }
}