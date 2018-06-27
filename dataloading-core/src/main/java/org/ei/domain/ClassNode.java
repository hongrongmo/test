package org.ei.domain;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

import org.ei.util.DiskMap;

public class ClassNode {
    private ClassNode parent;
    private Stack<ClassNode> children = new Stack<ClassNode>();
    private String title;
    private String id;

    public ClassNode(String id, String title, Map<String, ClassNode> seekMap) {
        this.title = title;
        this.id = id;
        seekMap.put(id, this);
    }

    public void export(DiskMap map) throws Exception {
        LinkedList<String> ll = new LinkedList<String>();
        ll.addLast(getTitle());
        ClassNode cn = this;

        while ((cn = cn.getParent()) != null) {
            if (!cn.getTitle().equals("root")) {
                ll.addLast(cn.getTitle());
            }
        }

        StringBuffer buf = new StringBuffer();
        while (ll.size() > 0) {
            buf.append(ll.removeLast());
            if (ll.size() > 0) {
                buf.append(" (:) ");
            }
        }

        String t = buf.toString();
        map.put(getID(), t);

        Iterator<ClassNode> it = children.iterator();
        while (it.hasNext()) {
            ClassNode cnode = (ClassNode) it.next();
            cnode.export(map);
        }
    }

    public ClassNode(String id, Map<String, ClassNode> seekMap) {
        this.id = id;
        seekMap.put(id, this);
    }

    public String getTitle() {
        return this.title;
    }

    public String getID() {
        return this.id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ClassNode getParent() {
        return this.parent;
    }

    public void setParent(ClassNode parent) {
        this.parent = parent;
    }

    public ClassNode getCurrentChildNode() {

        ClassNode currentNode = (ClassNode) children.peek();

        return currentNode;
    }

    public void addChild(ClassNode child) {
        child.setParent(this);
        children.push(child);
    }

    public Stack<ClassNode> getChildren() {
        return children;
    }

}