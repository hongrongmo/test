package org.ei.tags;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.validator.GenericValidator;
import org.ei.domain.DocID;

/**
 * This is a dual-purpose class - when used from the engvillage model layer it creates XML for the Tags & Groups functionality. When used with the controller
 * module it provides a bean for the XSL to transform the XML back into.
 *
 */
public class TagBubble {
    private String backURL;
    private String nextURL;
    private String addTagURL;
    private String editTagURL;
    private String puserID;
    private String customerID;
    private String js;
    private DocID docID;
    private List<Tag> tags;
    private List<TagGroup> taggroups;
    private TagGroupBroker groupBroker = null;
    private List<EditTag> editTags = new ArrayList<EditTag>();
    private boolean setEdit;

    public TagBubble() {
        // Empty ctor
    }

    public TagBubble(String addTagURL, String editTagURL, String nextURL, String puserID, String customerID, DocID docID, Comparator<Tag> comp) throws Exception {
        this.addTagURL = addTagURL;
        this.nextURL = nextURL;
        this.editTagURL = editTagURL;
        this.puserID = puserID;
        this.customerID = customerID;
        this.docID = docID;

        if ((puserID != null) && (puserID.trim().length() != 0)) {
            groupBroker = new TagGroupBroker();
        }

        TagBroker tagBroker = new TagBroker(groupBroker);

        tags = Arrays.asList(tagBroker.getTags(docID.getDocID(), puserID, customerID, comp));
    }

    public void toXML(Writer out) throws Exception {
        out.write("<TAG-BUBBLE>");
        if (!GenericValidator.isBlankOrNull(puserID)) {
            out.write("<LOGGED-IN>true</LOGGED-IN>");
            out.write("<PUSER>");
            out.write(this.puserID);
            out.write("</PUSER>");
            taggroups = Arrays.asList(groupBroker.getGroups(this.puserID, false));
            out.write("<TGROUPS>");
            for (int i = 0; i < taggroups.size(); i++) {
                // System.out.println("Group:"+groups[i].getTitle());
                taggroups.get(i).toXML(out);
            }
            out.write("</TGROUPS>");
        }
        out.write("<CUSTID>");
        out.write(this.customerID);
        out.write("</CUSTID>");
        out.write("<TAG-DOCID>");
        out.write(docID.getDocID());
        out.write(":");
        out.write(Integer.toString(docID.getDatabase().getMask()));
        String collection = docID.getCollection();
        if (collection != null) {
            out.write(":");
            out.write(collection);
        }
        out.write("</TAG-DOCID>");
        out.write("<URLS>");
        out.write("<NEXT-URL>");
        out.write(this.nextURL);
        out.write("</NEXT-URL>");
        out.write("<ADD-TAG-URL>");
        out.write(this.addTagURL);
        out.write("</ADD-TAG-URL>");
        out.write("<EDIT-TAG-URL>");
        // System.out.println("###############Edit URL:"+this.editTagURL);

        out.write(this.editTagURL);
        out.write("</EDIT-TAG-URL>");
        out.write("</URLS>");
        if (tags != null) {
            out.write("<TAGS>");
            for (int i = 0; i < tags.size(); i++) {
                tags.get(i).toXML(out);
            }
            out.write("</TAGS>");
        }

        out.write("</TAG-BUBBLE>");
    }

    //
    //
    // GETTERS/SETTERS
    //
    //

    public String getBackurl() {
        return backURL;
    }

    public void setBackurl(String backURL) {
        this.backURL = backURL;
    }

    public String getNexturl() {
        return nextURL;
    }

    public void setNexturl(String nextURL) {
        this.nextURL = nextURL;
    }

    public String getAddtagurl() {
        return addTagURL;
    }

    public void setAddtagurl(String addTagURL) {
        this.addTagURL = addTagURL;
    }

    public String getEdittagurl() {
        return editTagURL;
    }

    public void setEdittagurl(String editTagURL) {
        this.editTagURL = editTagURL;
    }

    public String getCustomerid() {
        return customerID;
    }

    public void setCustomerid(String customerID) {
        this.customerID = customerID;
    }

    public String getJs() {
        return js;
    }

    public void setJs(String js) {
        this.js = js;
    }

    public String getDocid() {
        return docID.getDocID();
    }

    public void setDocid(String docID) {
        // Create DocID object without database
        this.docID = new DocID(docID, null);
    }

    public Tag[] getTags() {
        if (this.tags == null) {
            return null;
        } else {
            Tag[] tagarr = new Tag[this.tags.size()];
            return (Tag[]) this.tags.toArray(tagarr);
        }
    }

    public void addTag(Tag tag) {
        if (this.tags == null)
            this.tags = new ArrayList<Tag>();
        this.tags.add(tag);
    }

    public void addTaggroup(TagGroup taggroup) {
        if (taggroups == null)
            taggroups = new ArrayList<TagGroup>();
        taggroups.add(taggroup);
    }

    public void addTaggroup(String id, String title) {
        TagGroup group = new TagGroup();
        group.setGroupID(id);
        group.setTitle(title);
        if (taggroups == null)
            taggroups = new ArrayList<TagGroup>();
        taggroups.add(group);
    }

    public List<TagGroup> getTaggroups() {
        return taggroups;
    }

    public String getPuserID() {
        return puserID;
    }

    public void setPuserID(String puserID) {
        this.puserID = puserID;
    }

    public List<EditTag> getEditTags() {
        return editTags;
    }

    public void setEditTags(List<EditTag> editTags) {
        this.editTags = editTags;
    }

    public void addEditTags(EditTag editTag) {
        this.editTags.add(editTag);
    }

    public boolean isSetEdit() {
        return setEdit;
    }

    public void setSetEdit(boolean setEdit) {
        this.setEdit = setEdit;
    }
}
