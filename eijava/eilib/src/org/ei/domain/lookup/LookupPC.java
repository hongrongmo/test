package org.ei.domain.lookup;

import org.ei.domain.Database;


public class LookupPC extends CombinedBase
{

	public LookupPC(String sessionID,
					int indexesPerPage)
	{
		super(sessionID,
			  indexesPerPage,
			  true);
	}

	protected String getSQLHook(String searchword,
								Database[] databases)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT long_name lookup_name, dbase, 'QQ' lookup_value " );
		sql.append("FROM CMB_PC_LOOKUP WHERE long_name LIKE '" + searchword+"%' " );
		sql.append(" AND dbase in (");
		sql.append(getSqlIN(databases));
		sql.append(") ORDER BY lookup_name" );
		return sql.toString();
	}


}