package org.ei.query.base;

import java.util.*;


public interface Highlightable
{

	public Map getHighlightData(String field);

	public void setHighlightData(Map data);

}