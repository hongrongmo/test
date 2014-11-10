package org.ei.domain;

import java.io.IOException;
import java.io.Writer;

public class ClassificationID implements XMLSerializable {

    private String classCode;
    private Database database;
    private boolean optional = false;

    public ClassificationID(String classCode, Database database) {
        this.classCode = classCode;
        this.database = database;
        if (classCode.indexOf("(") == 0 && classCode.indexOf(")") == (classCode.length() - 1)) {
            this.optional = true;
            this.classCode = stripParens(classCode);
        }
    }

    private String stripParens(String s) {
        s = s.substring(1, s.length());
        s = s.substring(0, s.length() - 1);
        return s;
    }

    public String getClassCode() {
        return this.classCode;
    }

    public boolean isOptional() {
        return this.optional;
    }

    public Database getDatabase() {
        return this.database;
    }

    public boolean equals(Object o) {
        boolean b = false;
        Class<? extends ClassificationID> c = getClass();
        if (c.isInstance(o)) {
            ClassificationID id = (ClassificationID) o;
            if (id.getClassCode().equals(classCode) && id.getDatabase().getID().equals(database.getID())) {
                b = true;
            } else {
                b = false;
            }

        } else {
            b = false;
        }

        return b;
    }

    public void toXML(Writer out) throws IOException {

        out.write("<CID>");
        if (this.optional) {
            out.write("<OPT>");
        }

        out.write(getClassCode());

        if (this.optional) {
            out.write("</OPT>");
        }

        out.write("</CID>");

    }
}