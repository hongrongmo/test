package org.ei.domain.lookup;

import java.sql.ResultSet;

import org.ei.domain.Database;


public class LookupTR extends CombinedBase
{
	public LookupTR(String sessionID,
			int indexesPerPage)
	{
		super(sessionID,
		      indexesPerPage,
		      false);
	}


	protected String getDisplayNameHook(ResultSet rs)
				throws Exception
	{
		String displayName= rs.getString("lookup_name");
		String value= rs.getString("lookup_value");
		displayName = value+" - "+ displayName;
		return displayName;
	}


	protected String getSQLHook(String searchword,
				    			Database[] databases)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT long_name lookup_name, dbase, short_name lookup_value ");
		sql.append("FROM CMB_TR_LOOKUP WHERE ");
		sql.append(" dbase in (");
		sql.append(getSqlIN(databases));
		sql.append(") ORDER BY lookup_name" );
      	return sql.toString();
	}
}