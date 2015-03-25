package org.ei.tags;

import java.util.*;

public class GroupCreateComp implements Comparator<Object> {

    public int compare(Object o1, Object o2) {
        TagGroup group1 = (TagGroup) o1;
        TagGroup group2 = (TagGroup) o2;

        long date1 = group1.getDatefounded();
        long date2 = group2.getDatefounded();

        if (date1 == date2) {
            return 0;
        } else if (date1 < date2) {
            return -1;
        } else {
            return 1;
        }
    }
}