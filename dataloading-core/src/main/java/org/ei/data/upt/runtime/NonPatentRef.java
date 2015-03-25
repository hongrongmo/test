/*
 * Created on Nov 1, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.data.upt.runtime;

import java.io.Writer;

/**
 * @author KFokuo
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NonPatentRef {

    String source;
    String cpxDocId;
    String insDocId;
    int index;

    public NonPatentRef() {

    }

    /**
     * @return
     */
    public String getSource() {
        return source;
    }

    /**
     * @param string
     */
    public void setSource(String string) {
        source = string;
    }

    /**
     * @return
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param string
     */
    public void setIndex(int index) {
        this.index = index;
    }
    public void toXML(Writer out) throws Exception {

        out.write("<NP-DOC>");
        out.write("<INDEX>");
        out.write(String.valueOf(index));
        out.write("</INDEX>");
        out.write("<SOURCE>");
        out.write(source);
        out.write("</SOURCE>");

        if (cpxDocId != null) {
            out.write("<CPX-DOCID>");
            out.write(cpxDocId);
            out.write("</CPX-DOCID>");
        }

        if (insDocId != null) {
            out.write("<INS-DOCID>");
            out.write(insDocId);
            out.write("</INS-DOCID>");
        }
        out.write("</NP-DOC>");

    }
    /**
     * @return
     */
    public String getCpxDocId() {
        return cpxDocId;
    }

    /**
     * @return
     */
    public String getInsDocId() {
        return insDocId;
    }

    /**
     * @param string
     */
    public void setCpxDocId(String string) {
        cpxDocId = string;
    }

    /**
     * @param string
     */
    public void setInsDocId(String string) {
        insDocId = string;
    }

}
