package org.ei.domain;

import java.util.Hashtable;
import java.util.Map;

public class DriverConfig
{
	private static Map driverTable = new Hashtable();
	private static Map lookupTable = new Hashtable();


	static
	{
		driverTable.put("cpx","org.ei.ev.driver.cpx.CPXDatabase");//1
		driverTable.put("c84","org.ei.ev.driver.c84.C84Database");//32
		driverTable.put("nti","org.ei.ev.driver.ntis.NTISDatabase");//4
		driverTable.put("ins","org.ei.ev.driver.inspec.InspecDatabase");//2
		driverTable.put("ibf","org.ei.ev.driver.insback.InsBackDatabase");//4096
		driverTable.put("usp","org.ei.ev.driver.uspto.USPTODatabase");//8
		driverTable.put("crc","org.ei.ev.driver.ENGnetBASE.ENGnetBASEDatabase");//16
		driverTable.put("ref","org.ei.ev.driver.upt.UPTRefDatabase");//65536
		driverTable.put("upt","org.ei.ev.driver.upt.UPTDatabase");//0
		driverTable.put("upa","org.ei.ev.driver.upt.UPADatabase");//32768
		driverTable.put("eup","org.ei.ev.driver.upt.EUPDatabase");//16384
		driverTable.put("geo","org.ei.ev.driver.geo.GEODatabase");//8192
		driverTable.put("pag","org.ei.ev.driver.pag.PAGDatabase");//131072
		driverTable.put("zbf","org.ei.ev.driver.cbf.CBFDatabase");//262144
	}

	public static Map getDriverTable()
	{
		return driverTable;
	}
}