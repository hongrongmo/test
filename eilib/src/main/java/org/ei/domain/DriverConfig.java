package org.ei.domain;

import java.util.Hashtable;
import java.util.Map;

public class DriverConfig
{
    private static Map<String, String> driverTable = new Hashtable<String, String>();
    
    static
    {
        driverTable.put(DatabaseConfig.CPX_PREF, "org.ei.data.compendex.runtime.CPXDatabase");// 1
        driverTable.put(DatabaseConfig.INS_PREF, "org.ei.data.inspec.runtime.InspecDatabase");// 2
        driverTable.put(DatabaseConfig.NTI_PREF, "org.ei.data.ntis.runtime.NTISDatabase");// 4
        driverTable.put(DatabaseConfig.USPTO_PREF, "org.ei.data.uspto.runtime.USPTODatabase");// 8
        driverTable.put(DatabaseConfig.CRC_PREF, "org.ei.data.ENGnetBASE.runtime.ENGnetBASEDatabase");// 16
        driverTable.put(DatabaseConfig.C84_PREF, "org.ei.data.c84.runtime.C84Database");// 32
        driverTable.put(DatabaseConfig.PCH_PREF, "org.ei.data.paper.runtime.PaperChemDatabase");// 64
        driverTable.put(DatabaseConfig.CHM_PREF, "org.ei.data.chem.runtime.ChemDatabase");// 128
        driverTable.put(DatabaseConfig.CBN_PREF, "org.ei.data.cbnb.runtime.CBNBDatabase");// 256
        driverTable.put(DatabaseConfig.ELT_PREF, "org.ei.data.encompasslit.runtime.EltDatabase");// 1024
        driverTable.put(DatabaseConfig.EPT_PREF, "org.ei.data.encompasspat.runtime.EptDatabase");// 2048
        driverTable.put(DatabaseConfig.IBF_PREF, "org.ei.data.insback.runtime.InsBackDatabase");// 4096
        driverTable.put(DatabaseConfig.GEO_PREF, "org.ei.data.geobase.runtime.GEODatabase");// 8192
        driverTable.put(DatabaseConfig.EUP_PREF, "org.ei.data.upt.runtime.EUPDatabase");// 16384
        driverTable.put(DatabaseConfig.UPA_PREF, "org.ei.data.upt.runtime.UPADatabase");// 32768
        driverTable.put(DatabaseConfig.REF_PREF, "org.ei.data.upt.runtime.UPTRefDatabase");// 65536
        driverTable.put(DatabaseConfig.PAG_PREF, "org.ei.data.pag.runtime.PAGDatabase");// 131072
        driverTable.put(DatabaseConfig.CBF_PREF, "org.ei.data.cbf.runtime.CBFDatabase");// 262144
        driverTable.put(DatabaseConfig.UPT_PREF, "org.ei.data.upt.runtime.UPTDatabase");// 524288
        driverTable.put(DatabaseConfig.IBS_PREF, "org.ei.data.ibs.runtime.IbsDatabase");// 1048576
        driverTable.put(DatabaseConfig.GRF_PREF, "org.ei.data.georef.runtime.GRFDatabase");// 2097152
    }
    
    public static Map<String, String> getDriverTable()
    {
        return driverTable;
    }
}