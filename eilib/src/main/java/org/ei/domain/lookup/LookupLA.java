package org.ei.domain.lookup;

import org.ei.domain.Database;


public class LookupLA extends CombinedBase
{
	public LookupLA(String sessionID,
			int indexesPerPage)
	{
		super(sessionID,
		      indexesPerPage,
		      false);
	}


	protected String getSQLHook(String searchword,
								Database[] databases)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT long_name lookup_name, dbase, 'QQ' lookup_value ");
		sql.append("FROM CMB_LA_LOOKUP WHERE ");
		sql.append(" dbase in (");
		sql.append(getSqlIN(databases));
		sql.append(") ORDER BY lookup_name" );
      		return sql.toString();
	}
}