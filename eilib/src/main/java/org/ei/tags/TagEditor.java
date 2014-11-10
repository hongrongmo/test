package org.ei.tags;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class TagEditor {

    public static final int LENTGHT_LIMIT = 30;
    private String userID;
    private String custID;
    private String docID;
    private String collection;
    private int mask;
    private Map<String, String[]> requestMap;

    public TagEditor(Map<String, String[]> requestMap, String userID, String custID, String docID) throws Exception {
        this.requestMap = requestMap;
        this.userID = userID;
        this.custID = custID;
        String[] tdocParts = docID.split(":");
        this.docID = tdocParts[0];
        this.mask = Integer.parseInt(tdocParts[1]);
        if (tdocParts.length > 2) {
            this.collection = tdocParts[2];
        }

        parseRequest();
    }

    private String getHParamName(String paramName) {
        return paramName.concat(TagEditGroup.POSTFIX);
    }

    private void parseRequest() throws Exception {
        Iterator<?> ritr = this.requestMap.keySet().iterator();
        TagBroker broker = new TagBroker();

        while (ritr.hasNext()) {
            String paramName = (String) ritr.next();
            if (paramName.indexOf(TagEditGroup.PREFIX) == 0 && !paramName.endsWith(TagEditGroup.POSTFIX)) {
                int scope = parseScope(paramName);
                String group = parseGroup(paramName);

                String[] textParams = (String[]) requestMap.get(paramName);
                String[] hiddenParams = (String[]) requestMap.get(getHParamName(paramName));

                String[] newtags = parseTags(textParams[0]);
                String[] oldtags = parseTags(hiddenParams[0]);

                boolean edit = checkTagLength(newtags);

                String[] addtags = diff(newtags, oldtags);
                String[] deltags = diff(oldtags, newtags);

                if (edit) {
                    delTags(broker, deltags, scope, group, userID, custID, docID);

                    addTags(broker, addtags, scope, group, userID, custID, docID, mask, collection);
                }
            }
        }
    }

    private boolean checkTagLength(String[] newtags) {
        if (newtags != null && newtags.length > 0) {
            for (int i = 0; i < newtags.length; i++) {
                if (newtags[i].length() > LENTGHT_LIMIT) {
                    return false;
                }
            }
        }

        return true;
    }

    private String[] parseTags(String tagString) {
        if (tagString != null && tagString.length() > 0) {
            String[] tags = (String[]) tagString.split(",");
            for (int i = 0; i < tags.length; i++) {
                tags[i] = tags[i].trim();
            }
            return tags;
        }

        return new String[0];
    }

    private void addTags(TagBroker broker, String[] addtags, int scope, String groupID, String userID, String custID, String docID, int mask, String collection)
        throws Exception {
        for (int i = 0; i < addtags.length; i++) {
            // System.out.println("Adding tags:"+addtags[i]);
            Tag tag = new Tag();
            tag.setScope(scope);
            tag.setGroupID(groupID);
            tag.setDocID(docID);
            tag.setTag(addtags[i]);
            tag.setCustID(custID);
            tag.setUserID(userID);
            tag.setMask(mask);
            tag.setCollection(collection);
            broker.addTag(tag);
        }
    }

    private void delTags(TagBroker broker, String deltags[], int scope, String groupID, String userID, String custID, String docID) throws Exception {
        for (int i = 0; i < deltags.length; i++) {
            // System.out.println("Deleting tags:"+deltags[i]);
            broker.deleteTag(deltags[i], scope, userID, custID, groupID, docID);

        }
    }

    private String[] diff(String[] a1, String[] a2) {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < a1.length; i++) {
            if (!matches(a1[i], a2)) {
                list.add(a1[i]);
            }
        }

        return (String[]) list.toArray(new String[list.size()]);
    }

    private boolean matches(String test, String[] list) {
        boolean matches = false;
        for (int i = 0; i < list.length; i++) {
            if (test.equals(list[i])) {
                matches = true;
                break;
            }
        }

        return matches;
    }

    private int parseScope(String param) {
        char c = param.charAt(4);
        char[] carray = { c };
        String sc = new String(carray);
        return Integer.parseInt(sc);
    }

    private String parseGroup(String param) {
        if (param.indexOf(";") > -1) {
            String parts[] = param.split(";");
            return parts[1];
        }

        return null;
    }
}
