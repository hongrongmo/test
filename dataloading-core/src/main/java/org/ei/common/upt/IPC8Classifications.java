package org.ei.common.upt;

import java.util.*;
import java.io.*;

import org.ei.domain.*;

public class IPC8Classifications implements ElementData {

    protected List<IPC8Classification> classifications = new ArrayList<IPC8Classification>();
    protected Key key;
    protected boolean labels = false;

    public void setKey(Key akey) {
        this.key = akey;
    }

    public Key getKey() {
        return this.key;
    }

    public void exportLabels(boolean labels) {
        this.labels = labels;
    }

    public String[] getElementData() {
        String[] edata = new String[classifications.size()];

        for (int i = 0; i < classifications.size(); i++) {
            IPC8Classification c = (IPC8Classification) classifications.get(i);
            edata[i] = c.getClassTitle();
        }

        return edata;
    }

    public void setElementData(String[] elementData) {
        for (int i = 0; i < elementData.length; i++) {
            IPC8Classification c = (IPC8Classification) classifications.get(i);
            c.setClassTitle(elementData[i]);
        }
    }

    public IPC8Classifications(Key key, List<IPC8Classification> list) {
        this.key = key;
        this.classifications = list;
    }

    public void toXML(Writer out) throws IOException

    {
        out.write("<");
        out.write(key.getKey());
        if (labels && (this.key.getLabel() != null)) {
            out.write(" label=\"");
            out.write(this.key.getLabel());
            out.write("\"");
        }
        out.write(">");

        for (int i = 0; i < classifications.size(); i++) {
            IPC8Classification c = (IPC8Classification) classifications.get(i);
            c.toXML(out);
        }
        out.write("</");
        out.write(key.getKey());
        out.write(">");

    }
}
