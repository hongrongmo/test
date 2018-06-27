package org.ei.tags;

import java.io.Writer;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class EditGrouper {

    private String puserID;
    private String customerID;
    private String docID;
    private String scope;
    private StringBuffer jsebuf = new StringBuffer();
    private LinkedHashMap<String, TagEditGroup> editGroups = new LinkedHashMap<String, TagEditGroup>();

    public static final Hashtable<String, String> groupLabels = new Hashtable<String, String>();
    public static final String KEY_DELIM = ";";

    static {
        groupLabels.put("1", "Public:");
        groupLabels.put("2", "Private:");
        groupLabels.put("4", "My Institution:");
        groupLabels.put("3", "Group ");
    }

    public EditGrouper(String puserID, String customerID, String docID) throws Exception {
        this.puserID = puserID;
        this.customerID = customerID;
        this.docID = docID;
    }

    // generate edit-tags groups

    public void setEditGroups(Tag[] oTags) {
        for (int i = 0; i < oTags.length; i++) {
            Tag t = oTags[i];
            if (this.puserID != null && t.getUserID().equals(this.puserID)) {
                String scope = String.valueOf(t.getScope());
                String key = setKey(scope, t.getGroupID());
                if (!editGroups.containsKey(key)) {
                    TagEditGroup editGroup = new TagEditGroup(t, key);
                    editGroups.put(key, editGroup);
                } else {
                    TagEditGroup eGroup = (TagEditGroup) editGroups.get(key);
                    // System.out.println(eGroup.getGroupID());
                    eGroup.add(t);
                }
            }
        }

    }

    private String setKey(String scope, String groupID) {

        if (Integer.parseInt(scope) == Scope.SCOPE_GROUP) {
            return scope.concat(KEY_DELIM).concat(groupID);
        } else {
            return scope;
        }
    }

    public StringBuffer jsElemComposer(String inputLabel, String inputName, StringBuffer inputValue) {

        inputName = "Edit" + inputName;
        // start for loop for each text field
        jsebuf.append("\n var row = document.createElement(\"tr\");");
        jsebuf.append("\n var cell = document.createElement(\"td\");");
        // add input label
        jsebuf.append("\n var labelNode = document.createElement(\"label\");");
        jsebuf.append("\n labelNode.appendChild(document.createTextNode(\"").append(inputLabel).append("\"));");
        jsebuf.append("\n labelNode.htmlFor = \"txt").append(inputName).append("\";");
        jsebuf.append("\n labelNode.style.fontSize = \"11px\";");
        jsebuf.append("\n cell.appendChild(labelNode);");
        jsebuf.append("\n row.appendChild(cell);");
        jsebuf.append("\n tageditTableBody.appendChild(row);");

        jsebuf.append("\n row = document.createElement(\"tr\");");
        jsebuf.append("\n cell = document.createElement(\"td\");");

        // text input field
        jsebuf.append("\n var ip = document.createElement(\"input\");");
        jsebuf.append("\n ip.setAttribute(\"type\",\"text\");");
        jsebuf.append("\n ip.setAttribute(\"size\", \"25\");");
        // add input name
        jsebuf.append("\n ip.setAttribute(\"name\", \"").append(inputName).append("\");");
        jsebuf.append("\n ip.setAttribute(\"id\", \"txt").append(inputName).append("\");");
        // tag values
        jsebuf.append("\n ip.setAttribute(\"value\", \"").append(inputValue.toString().toLowerCase()).append("\");");
        jsebuf.append("\n ip.setAttribute(\"class\",\"SmBlackText\");");
        jsebuf.append("\n ip.style.fontSize = \"11px\";");

        jsebuf.append("\n cell.appendChild(ip);");
        jsebuf.append("\n row.appendChild(cell);");
        jsebuf.append("\n tageditTableBody.appendChild(row);");
        // end for loop

        return jsebuf;
    }

    public StringBuffer jsDrawSubmitBtn() {
        StringBuffer jsbtn = new StringBuffer();

        jsbtn.append("\n var btnrow = document.createElement(\"tr\");");
        jsbtn.append("\n var btncell = document.createElement(\"td\");");

        jsbtn.append("\n var btnimg = document.createElement(\"input\");");
        jsbtn.append("\n btnimg.setAttribute(\"type\", \"submit\");");
        jsbtn.append("\n btnimg.setAttribute(\"value\", \"Save Changes\");");
        jsbtn.append("\n btnimg.setAttribute(\"name\", \"edittags\");");
        jsbtn.append("\n btnimg.setAttribute(\"title\", \"Save Changes\");");

        jsbtn.append("\n btncell.appendChild(btnimg);");
        jsbtn.append("\n btnrow.appendChild(btncell);");
        jsbtn.append("\n tageditTableBody.appendChild(btnrow);");

        return jsbtn;
    }

    public String jsDhtmlComposer() {

        StringBuffer btn = jsDrawSubmitBtn();

        StringBuffer jsbuf = new StringBuffer();
        jsbuf.append("function drawEdit()");
        jsbuf.append("{");
        jsbuf.append("clearEdit();");
        jsbuf.append(jsebuf);
        jsbuf.append(btn);
        jsbuf.append("return false;");
        jsbuf.append("}");
        return jsbuf.toString();
    }

    public void editXML(Tag[] oTags, Writer out) throws Exception {
        StringBuffer jsElements = new StringBuffer();
        setEditGroups(oTags);
        out.write("<EDIT-TAGS>");

        Iterator<String> itr = editGroups.keySet().iterator();
        if (!editGroups.isEmpty()) {
            out.write("<SET-EDIT/>");
        }
        while (itr.hasNext()) {
            TagEditGroup egroup = (TagEditGroup) editGroups.get(itr.next());

            jsElemComposer(egroup.getLabel(), egroup.getKey(), egroup.getTagNames());

            out.write("<EDIT-TAG>");

            out.write("<EDIT-FIELD-NAME>");
            out.write(egroup.getInputName());
            out.write("</EDIT-FIELD-NAME>");

            out.write("<EDIT-HFIELD-NAME>");
            out.write(egroup.getHInputName());
            out.write("</EDIT-HFIELD-NAME>");

            out.write("<EDIT-LABEL>");
            out.write(egroup.getLabel());
            out.write("</EDIT-LABEL>");

            out.write("<EDIT-TAGS-NAMES>");
            out.write("<![CDATA[");
            out.write(egroup.getTagNames().toString().toLowerCase());
            out.write("]]>");
            out.write("</EDIT-TAGS-NAMES>");

            out.write("<EDIT-GROUPID>");
            out.write(egroup.getGroupID());
            out.write("</EDIT-GROUPID>");

            out.write("<EDIT-SCOPE>");
            out.write(egroup.getScope());
            out.write("</EDIT-SCOPE>");

            out.write("</EDIT-TAG>");

        }

        out.write("<JS>");
        out.write(jsDhtmlComposer());
        out.write("</JS>");
        out.write("</EDIT-TAGS>");
    }

}
