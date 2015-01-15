package org.ei.domain.lookup;

import java.sql.ResultSet;

import org.ei.domain.Database;


public class LookupST extends CombinedBase
{

	public LookupST(String sessionID,
					int indexesPerPage)
	{
		super(sessionID,
			  indexesPerPage,
			  true);
	}

	protected String getDisplayNameHook(ResultSet rs)
		throws Exception
	{
		String displayName= rs.getString("lookup_name");
		/*
		String issn = rs.getString("ISSN");
		if(issn != null && issn.length() > 0)
		{
			displayName = displayName+" ("+issn+")";
		}
		*/
		return displayName;

	}

	protected String getSQLHook(String searchword,
								Database[] databases)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT source_title lookup_name,dbase,issn, 'QQ' lookup_value " );
		sql.append("FROM CMB_ST_LOOKUP WHERE source_title LIKE '" + searchword+"%' " );
		sql.append(" AND dbase in (");
		sql.append(getSqlIN(databases));
		sql.append(") ORDER BY lookup_name" );
		return sql.toString();
	}

}