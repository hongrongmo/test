package org.ei.query.base;

import java.util.Map;

import org.ei.domain.Key;

public interface Highlightable {

    public Map<Key, String[]> getHighlightData(String field);

    public void setHighlightData(Map<Key, String[]> data);

}