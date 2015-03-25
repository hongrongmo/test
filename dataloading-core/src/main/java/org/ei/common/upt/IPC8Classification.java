package org.ei.common.upt;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.ei.domain.XMLSerializable;

public class IPC8Classification implements XMLSerializable, Comparable<Object>

{
    private String section;
    private String ipcClass;
    private String subClass;
    private String mainGroup;
    private String sep;
    private String subGroup;
    private String versionDate;
    private String classLevel;
    private String position;
    private String value;
    private String actionDate;
    private String type;
    private String source;
    private String authority;
    private String ipcRaw;
    private String title;
    private String key;

    public IPC8Classification(String ipcRaw) {
        this.ipcRaw = ipcRaw;
        parseIPC8(ipcRaw);
    }

    private void parseIPC8(String ipcRaw) {

        this.section = ipcRaw.substring(0, 1);
        this.ipcClass = ipcRaw.substring(1, 3);
        this.subClass = ipcRaw.substring(3, 4);
        this.mainGroup = ipcRaw.substring(4, 8);
        this.sep = ipcRaw.substring(8, 9);
        this.subGroup = ipcRaw.substring(9, 15);
        this.versionDate = ipcRaw.substring(19, 27);
        this.classLevel = ipcRaw.substring(27, 28);
        this.position = ipcRaw.substring(28, 29);
        this.value = ipcRaw.substring(29, 30);
        this.actionDate = ipcRaw.substring(30, 38);
        this.type = ipcRaw.substring(38, 39);
        this.source = ipcRaw.substring(39, 40);
        this.authority = ipcRaw.substring(40, 42);
        this.key = section + ipcClass.replaceAll(" ", "0") + subClass + mainGroup.replaceAll(" ", "0") + subGroup.replaceAll(" ", "0");
    }

    public String getType() {

        StringBuffer type = new StringBuffer();
        if (position.equalsIgnoreCase("F"))
            type.append("F");
        else
            type.append("L");

        if (classLevel.equalsIgnoreCase("A"))
            type.append("A");
        else
            type.append("C");

        if (value.equalsIgnoreCase("I"))
            type.append("I");
        else
            type.append("N");

        return type.toString();
    }

    public void setClassTitle(String title) {
        this.title = title;
    }

    public void setCode(String raw) {
        parseIPC8(ipcRaw);
    }

    public String getCode() {
        return section + ipcClass + subClass + mainGroup.trim() + sep + subGroup.trim();
    }

    public String getCodeKey() {
        return this.key;
    }

    public boolean equals(Object anotherIPC8) {
        if (!(anotherIPC8 instanceof IPC8Classification))
            throw new ClassCastException("IPC8Classification object expected.");
        return this.getCodeKey().equalsIgnoreCase(((IPC8Classification) anotherIPC8).getCodeKey());
    }

    public int compareTo(Object anotherIPC8) {

        if (!(anotherIPC8 instanceof IPC8Classification))
            throw new ClassCastException("IPC8Classification object expected.");
        String str1 = this.getType();
        String str2 = ((IPC8Classification) anotherIPC8).getType();
        return str1.compareTo(str2);
    }

    public static List<IPC8Classification> build(String raw) {

        if ((raw.length() % 50) == 0) {
            int count = raw.length() / 50;
            List<IPC8Classification> ipcs = new ArrayList<IPC8Classification>();

            Hashtable<String, IPC8Classification> uniq = new Hashtable<String, IPC8Classification>();

            int start = 0;
            int end = 0;
            for (int i = 0; i < count; i++) {
                start = end;
                end = end + 50;
                IPC8Classification cl = new IPC8Classification(raw.substring(start, end));
                if (!uniq.containsKey(cl.getCodeKey())) {
                    uniq.put(cl.getCodeKey(), cl);
                    ipcs.add(cl);

                }

            }
            Collections.sort(ipcs);

            return ipcs;
        } else
            return null;
    }

    public String getClassTitle() {
        if (title == null)
            return "";
        else
            return title;
    }

    public void toXML(Writer out) throws IOException {

        out.write("<PID ");
        out.write("level=\"");
        out.write(this.classLevel);
        out.write("\" ");
        out.write("type=\"");
        out.write(this.value);
        out.write("\">");
        out.write("<CID>");
        out.write("<![CDATA[" + section + ipcClass + subClass.trim() + "&nbsp;" + mainGroup.trim() + sep + subGroup.trim() + "]]>");
        out.write("</CID>");
        out.write("<CTI><![CDATA[");
        out.write(getClassTitle());
        out.write("]]></CTI>");
        out.write("</PID>");

    }

}
