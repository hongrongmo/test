package org.ei.data.books.tools;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Bookmarks implements Visitable {

    private List marks = new LinkedList();
    
    public void add(Bookmark mark) {
        marks.add(mark);
    }
    
    public Iterator iterator() {
        return marks.iterator();
    }

    public boolean isEmpty() {
        return marks.isEmpty();
    }

    public void accept(Visitor visitor) {
        if(!marks.isEmpty()) {
            visitor.visit(this);
        }
    }
    
}
