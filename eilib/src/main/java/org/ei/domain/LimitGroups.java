package org.ei.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LimitGroups {
    private Map<String, ArrayList<String>> fieldMap = new HashMap<String, ArrayList<String>>();

    public void addLimiter(String field, String value) {
        if (fieldMap.containsKey(field)) {
            List<String> values = (List<String>) fieldMap.get(field);
            values.add(value);
        } else {
            ArrayList<String> values = new ArrayList<String>();
            values.add(value);
            fieldMap.put(field, values);
        }
    }

    public int size() {
        return this.fieldMap.size();
    }

    public String[] getFields() {
        String[] keys = new String[fieldMap.size()];
        Iterator<String> it = fieldMap.keySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            String field = (String) it.next();
            keys[i] = field;
            i++;
        }

        return keys;
    }

    public String[] getValues(String key) {
        ArrayList<?> values = (ArrayList<?>) fieldMap.get(key);
        return (String[]) values.toArray(new String[values.size()]);
    }
}