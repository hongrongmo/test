package org.ei.domain.lookup;

import org.ei.domain.Database;


public class LookupPN extends CombinedBase
{

	public LookupPN(String sessionID,
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
		sql.append("SELECT publisher_name lookup_name, dbase, 'QQ' lookup_value " );
		sql.append("FROM CMB_PN_LOOKUP WHERE publisher_name LIKE '" + searchword+"%' " );
		sql.append(" AND dbase in (");
		sql.append(getSqlIN(databases));
		sql.append(") ORDER BY lookup_name" );
		return sql.toString();
	}


}