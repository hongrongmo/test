/*
 * Created on Aug 5, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.domain.navigators;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ResultsState
{
	//private static Log log = LogFactory.getLog(ResultsState.class);

	public static final int DEFAULT_STATE_COUNT = 10;
	public static final int DEFAULT_FACTOR = 2;

	private static final String STATE_SEP = ":";
	private static final String FIELD_SEP = ";";

	private Map rs = new Hashtable();

	public ResultsState()
	{
	}

	public ResultsState(String state)
	{
		if(state != null)
		{
			rs.clear();
			String[] states = state.split(FIELD_SEP);
			for(int i=0; i < states.length; i++)
			{
				modifyState(states[i]);
			}
		}

	}
	public Map getStateMap() {
		return rs;
	}

	public void modifyState(String state)
	{
		String term[] = state.split(STATE_SEP);
		try
		{
			rs.put(term[0], new Integer(term[1]));
		}
		catch(NumberFormatException nfe)
		{
			rs.put(term[0],new Integer(DEFAULT_STATE_COUNT));
		}
	}

	public String toString()
	{
		StringBuffer state = new StringBuffer();
		Iterator itrstates = rs.keySet().iterator();
		while(itrstates.hasNext())
		{
			String field = (String) itrstates.next();
			state
			.append(field)
			.append(STATE_SEP)
			.append((Integer) rs.get(field))
			.append(FIELD_SEP);
		}
		return state.toString();
	}

	public static String getPagers(int totalsize, int currentcount, String fieldname)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("<PAGERS FIELD=\"").append(fieldname).append("\">");
		//log.debug(" total size " + totalsize);
		//log.debug(" fieldname " + fieldname);
		//log.debug(" current count " + currentcount);
		// jam - added check to make sure we do not display '...less'
		// pager when there are fewer than the default size
		if((totalsize > ResultsState.DEFAULT_STATE_COUNT) && (currentcount > ResultsState.DEFAULT_STATE_COUNT))
		{
			sb.append("<LESS COUNT=\"").append(currentcount / ResultsState.DEFAULT_FACTOR).append("\"/>");
		}
		if(totalsize > currentcount)
		{
			sb.append("<MORE COUNT=\"").append(currentcount * ResultsState.DEFAULT_FACTOR).append("\"/>");
		}
		sb.append("<TOTALSIZE COUNT=\"").append(totalsize).append("\"/>");
		
		sb.append("</PAGERS>");

		return sb.toString();
	}
	
	public static JSONObject getPagersJSON(int totalsize, int currentcount, String fieldname) throws JSONException
	{
		JSONObject pagers = new JSONObject();
		StringBuffer sb = new StringBuffer();
		pagers.put("field", fieldname);
		
		if((totalsize > ResultsState.DEFAULT_STATE_COUNT) && (currentcount > ResultsState.DEFAULT_STATE_COUNT))
		{
			pagers.put("less", currentcount / ResultsState.DEFAULT_FACTOR);
			
		}
		if(totalsize > currentcount)
		{
			//pagers.put("more", currentcount * ResultsState.DEFAULT_FACTOR);
		}
		
		pagers.put("totalSize", totalsize);

		return pagers;
	}
}
