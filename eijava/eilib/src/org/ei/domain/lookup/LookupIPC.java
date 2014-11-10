package org.ei.domain.lookup;

import org.ei.domain.Database;


public class LookupIPC extends CombinedBase
{

	public LookupIPC(String sessionID,
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
		sql.append("SELECT replace(ipccode,'SLASH','/')||'[]'||description lookup_name, dbase, replace(ipccode,'SLASH','/') lookup_value " );
		sql.append("FROM CMB_IPC_LOOKUP WHERE ipccode LIKE replace('"+searchword+"','/','SLASH')||'%' " );
		sql.append(" AND dbase in (");
		sql.append(getSqlIN(databases));
		sql.append(") ORDER BY lookup_name" );

		return sql.toString();
	}


}