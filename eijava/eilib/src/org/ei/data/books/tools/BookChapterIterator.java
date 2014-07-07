package org.ei.data.books.tools;

import java.util.Iterator;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BookChapterIterator implements Iterator {
    
    protected static Log log = LogFactory.getLog(BookChapterIterator.class);
 
    private Iterator children = null;
    private Bookmark last_mark = null;
    private Stack parents = new Stack();
    
    public BookChapterIterator(Bookmarks bookmarks) {
        children = bookmarks.iterator();
    }

    public boolean hasNext() {

        if(last_mark != null) {
            Bookmarks marks = last_mark.getChildren();
            if(!marks.isEmpty()) {
                parents.push(children);
                children = marks.iterator();
            }
            last_mark = null;
        }
        while(!children.hasNext() && !parents.empty()) {
            children = (Iterator) parents.pop();
        }
        last_mark = null;
        return children.hasNext();
    }

    public Object next() {
        last_mark = (Bookmark) children.next();
        return last_mark;
    }

    public void remove() {
        // TODO Auto-generated method stub

    }

}
