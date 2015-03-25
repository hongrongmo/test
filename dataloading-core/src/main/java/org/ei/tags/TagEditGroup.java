package org.ei.tags;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class TagEditGroup {
    private String userID;
    private String custID;
    private String groupID = "";
    private String scope;
    private String docID;
    private String inputName;
    private String hinputName;
    private String key;
    private String label;
    private String[] oTags;
    private String[] nTags;
    private Hashtable<String, Tag> deleteTags;
    private Hashtable<String, Tag> addTags;
    private ArrayList<String> oTagArray = new ArrayList<String>();

    public static final String PREFIX = "Edit";
    public static final String IDELIM = ";";
    public static final String POSTFIX = "_h";
    public static final String DELIM = ", ";
    public static final String GTITLE_DELIM = ":";

    public TagEditGroup(String puserID, String customerID, String docID) {
        this.userID = puserID;
        this.custID = customerID;
        this.docID = docID;
    }

    public TagEditGroup(Tag t, String _key) {

        String scope = String.valueOf(t.getScope());
        this.setScope(scope);
        this.setGroupID(t.getGroupID());
        this.setLabel(t.getScope(), t.getGroup());
        this.setInputName(scope);
        this.setHInputName(scope, t.getGroupID());
        oTagArray.add(t.getTagSearchValue());
        this.setKey(_key);

    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getGroupID() {
        return this.groupID;
    }

    public String getScope() {
        return this.scope;
    }

    public String parseScope(String paramName) {
        return paramName.substring(4, 5);
    }

    public void setKey(String _key) {
        this.key = _key;
    }

    public String getKey() {
        return this.key;
    }

    public String[] getoTags() {
        return this.oTags;
    }

    public String[] getnTags() {
        return this.nTags;
    }

    public void fillGroup(String paramName, String[] paramValue) {
        if (paramName.endsWith(POSTFIX)) {
            this.oTags = paramValue[0].split(DELIM);
        } else {
            this.nTags = paramValue[0].split(DELIM);
        }
        this.inputName = paramName;
        this.scope = parseScope(paramName);
        this.groupID = parseGroupID(paramName);
    }

    public String parseGroupID(String paramName) {
        int gid = paramName.indexOf(EditGrouper.KEY_DELIM);
        if (gid > 0) {
            gid = gid + 1;
            return paramName.substring(gid);
        }
        return null;

    }

    public void setLabel(int scope, TagGroup tgroup) {
        String strScope = String.valueOf(scope);
        this.label = (String) EditGrouper.groupLabels.get(strScope);

        if (scope == Scope.SCOPE_GROUP && tgroup != null) {
            this.label = this.label.concat(tgroup.getTitle()).concat(GTITLE_DELIM);
        }
    }

    public String getLabel() {
        return this.label;
    }

    public void add(Tag t) {
        this.oTagArray.add(t.getTagSearchValue());
    }

    public void setInputName(String scope) {
        this.inputName = PREFIX.concat(scope);
    }

    public String getInputName() {
        return this.inputName;
    }

    public void setHInputName(String scope, String groupID) {
        if (scope.equalsIgnoreCase(String.valueOf(Scope.SCOPE_GROUP))) {
            this.hinputName = PREFIX.concat(scope).concat(IDELIM).concat(groupID).concat(POSTFIX);
        } else {
            this.hinputName = PREFIX.concat(scope).concat(POSTFIX);
        }
    }

    public String getHInputName() {
        return this.hinputName;
    }

    public StringBuffer getTagNames() {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < this.oTagArray.size(); i++) {
            if (i != 0) {
                buf.append(DELIM);
            }
            buf.append(this.oTagArray.get(i));

        }
        return buf;
    }

    public void setAddDeleteTags(Hashtable<String, Tag> addTags, Hashtable<String, Tag> deleteTags) {
        this.addTags = addTags;
        this.deleteTags = deleteTags;
    }

    public void edit(TagBroker broker) throws Exception {
        Iterator<String> aitr = this.addTags.keySet().iterator();
        while (aitr.hasNext()) {

            String anext = (String) aitr.next();
            editAdd(anext, broker);
        }
        Iterator<String> ditr = this.deleteTags.keySet().iterator();
        while (ditr.hasNext()) {

            String dnext = (String) ditr.next();
            editDelete(dnext, broker);
        }
    }

    public void editAdd(String tagName, TagBroker broker) throws Exception {
        Tag addtag = new Tag();
        fillTag(addtag, tagName);
        broker.addTag(addtag);
    }

    public void fillTag(Tag addtag, String tagName) {
        addtag.setTag(tagName);
        addtag.setCustID(this.custID);
        addtag.setGroupID(this.groupID);
        addtag.setScope(Integer.parseInt(this.scope));
        addtag.setUserID(this.userID);
        addtag.setDocID(this.docID);
    }

    public void editDelete(String tagName, TagBroker broker) throws Exception {
        // System.out.println("--DELETE seting groupID--"+this.groupID);
        broker.deleteTag(tagName, Integer.parseInt(this.scope), this.userID, this.custID, this.groupID, this.docID);
    }

}