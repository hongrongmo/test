package org.ei.domain;

import java.util.Hashtable;
import java.util.Map;

public class DriverConfig
{
	private static Map driverTable = new Hashtable();
	private static Map lookupTable = new Hashtable();


	static
	{
		driverTable.put("cpx","org.ei.data.compendex.runtime.CPXDatabase");//1
		driverTable.put("c84","org.ei.data.c84.runtime.C84Database");//32
		driverTable.put("nti","org.ei.data.ntis.runtime.NTISDatabase");//4
		driverTable.put("ins","org.ei.data.inspec.runtime.InspecDatabase");//2
		driverTable.put("ibf","org.ei.data.insback.runtime.InsBackDatabase");//4096
		driverTable.put("usp","org.ei.data.uspto.runtime.USPTODatabase");//8
		driverTable.put("crc","org.ei.data.ENGnetBASE.runtime.ENGnetBASEDatabase");//16
		driverTable.put("ref","org.ei.data.upt.runtime.UPTRefDatabase");//65536
		driverTable.put("upt","org.ei.data.upt.runtime.UPTDatabase");//0
		driverTable.put("upa","org.ei.data.upt.runtime.UPADatabase");//32768
		driverTable.put("eup","org.ei.data.upt.runtime.EUPDatabase");//16384
		driverTable.put("geo","org.ei.data.geobase.runtime.GEODatabase");//8192
		driverTable.put("pag","org.ei.data.pag.runtime.PAGDatabase");//131072
		driverTable.put("zbf","org.ei.data.cbf.runtime.CBFDatabase");//262144
	}

	public static Map getDriverTable()
	{
		return driverTable;
	}
}