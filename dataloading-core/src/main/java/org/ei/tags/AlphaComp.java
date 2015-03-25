package org.ei.tags;

import java.util.Comparator;

public class AlphaComp implements Comparator<Tag> {

    public int compare(Tag o1, Tag o2) {
        Tag tag1 = (Tag) o1;
        Tag tag2 = (Tag) o2;

        String tagname1 = tag1.getTagSearchValue();
        String tagname2 = tag2.getTagSearchValue();
        return tagname1.compareTo(tagname2);

    }
}