package org.ei.dataloading.inspec.loadtime;


public class InspecBaseTableRecord
{
	public static final String[] insbaseTableFields= 
	{
		"M_ID","ID","ANUM", "ADATE", "RTYPE","CPR",
		"TI","AB","CLS","CVS","FLS","TRMC","NDI","CHI","AOI","THLP","TTJ","FJT","FTTJ","LA","TC",
		"AC","AUS","EDS","TRS","ABNUM","CN","CNT","SN","SNT","CCCC","MATID","SBN","RNUM","UGCHN",
		"CNUM","PNUM","OPAN","SICI","SICIT","DNUM","DOI","VOLISS","VOLISST","PARTNO","AMDREF",
		"CLOC","PPUB","CPAT","COPA","CPUB","CPUBT","NPL1","NPL2","IPN","IPNT","XREFNO","UM",
		"URL","DCURL","AAFF","EAFF","PAS","PUB","IORG","SORG","AVAIL","PRICE","CDATE","PDATE",
		"TDATE","FDATE","PPDATE", "LOAD_NUMBER","IPC","CIT"
	};
	public static final String[] ibfbaseTableFields=
	{
		"M_ID","ANUM","RTYPE","CRT","SU","TI","AB","FIG","CLS","CVS","FLS","OCLS","OCVS","UDC",
		"OINFO","THLP","FJT","AJT","OJT","SOURCE","TFJT","TAJT","TOJT","TSOURCE","LA","TC","AUS",
		"EDS","ABNUM","RNUM","PNUM","DOI","TDOI","VOL","ISS","VOLISS","TVOL","TISS","TVOLISS",
		"PARTNO","SEC","CLOC","PPUB","CIORG","CCNF","CPAT","CPUB","TCPUB","NPL1","IPN","TIPN",
		"PAS","PUB","IORG","SORG","CDATE","CEDATE","CODATE","PDATE","PEDATE","PODATE","PYR",
		"TPDATE","TPEDATE","TPODATE","FDATE","FODATE","LOAD_NUMBER","IPC"
	};

	public static final String[] insxmlbaseTableFields=
	{
									"M_ID",
									"ANUM",
									"ASPDATE",
									"AOPDATE",
									"RTYPE",
									"NRTYPE",
									"CPR",
									"SU",
									"TI",
									"AB",
									"CLS",
									"CVS",
									"FLS",
									"TRMC",
									"NDI",
									"CHI",
									"AOI",
									"PFLAG",
									"PJID",
									"PFJT",
									"PAJT",
									"PVOL",
									"PISS",
									"PVOLISS",
									"PIPN",
									"PSPDATE",
									"PEPDATE",
									"POPDATE",
									"PCPUB",
									"PCDN",
									"PSN",
									"NPSN", // 07/2007 added, inspec provided issn and electronic issn
									"PSICI",
									"PPUB",
									"PCCCC",
									"PUM",
									"PDNUM",
									"PDOI",
									"PURL",
									"PDCURL",
									"SJID",
									"SFJT",
									"SAJT",
									"SVOL",
									"SISS",
									"SVOLISS",
									"SIPN",
									"SSPDATE",
									"SEPDATE",
									"SOPDATE",
									"SRTDATE", // 03/2007 added , inspec provided new field "sortdate"
									"SCPUB",
									"SCDN",
									"SSN",
									"NSSN", // 07/2007 added, inspec provided issn and electronic issn
									"SSICI",
									"LA",
									"TC",
									"AC",
									"AUS",
									"AUS2",
									"EDS",
									"TRS",
									"ABNUM",
									"MATID",
									"SBN",
									"RNUM",
									"VRN", // 03/2007  added for 'Standards' - new doc type, version
									"UGCHN",
									"CNUM",
									"PNUM",
									"OPAN",
									"PARTNO",
									"AMDREF",
									"CLOC",
									"BPPUB",
									"CIORG",
									"CCNF",
									"CPAT",
									"COPA",
									"PUBTI",
									"NPL1",
									"NPL2",
									"XREFNO",
									"AAFF",
									"AAFFMULTI1", // 03/2007 added , field for multi author affiliations
									"AAFFMULTI2", // 03/2007 added , field for overflow
									"AFC",
									"EAFF",
									"EAFFMULTI1", //  03/2007 added , field for multi editor affiliations
									"EAFFMULTI2", //  03/2007 added , field for overflow
									"EFC",
									"PAS",
									"IORG",
									"SORG",
									"AVAIL",
									"PRICE",
									"CSPDATE",
									"CEPDATE",
									"COPDATE",
									"PYR",
									"FDATE",
									"OFDATE",
									"PPDATE",
									"OPPDATE",
									"LOAD_NUMBER",
									"IPC",
									"CIT",
									"ACCESS",	// added on 20201222 by hmo for EVOPS-1068
									"MATID1",	// added on 20201222 by hmo for EVOPS-1068
									"RTNAME",	// added on 20201222 by hmo for EVOPS-1068
									"PAUTH",	// added on 20201222 by hmo for EVOPS-1068
									"CPC",		// added on 20201222 by hmo for EVOPS-1068
									"LINKG",	// added on 20201222 by hmo for EVOPS-1068
									"FUNDG",	// added on 20201222 by hmo for EVOPS-1068									
									"VIDEOG",	// added on 20201222 by hmo for EVOPS-1068
									"REPOSG"	// added on 20201222 by hmo for EVOPS-1068
									};


}
