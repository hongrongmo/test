package org.ei.dataloading.ntis.loadtime;


public class NTISBaseTableRecord
{
	public static final String[] baseTableFields = {
		  "M_ID",
		  "AN",		//ACCESSION_NUMBER
		  "CAT",	//CLASSIFICATION_CODE
		  "IC",		//COUNTRY,AFFILIATION_LOCATION,LANGUAGE
		  "PR",		//AVAILABILITY
		  "SO",		//COUNTRY,AFFILIATION_LOCATION,PERFORMER
		  "TI",		//TITLE
		  "IDES",
		  "VI",		//VOLUME,ISSUE
		  "TN",		//DOCTYPE
		  "PA1",	//AUTHOR
		  "PA2",	//AUTHOR
		  "PA3",	//AUTHOR
		  "PA4",	//AUTHOR
		  "PA5",	//AUTHOR
		  "RD",		//PATENTAPPDATE,PATENTISSUEDATE,PUB_YEAR
		  "XP",		//STARTPAGE
		  "PDES",
		  "RN",		//REPORTNUMBER
		  "CT",		//ORDERNUMBER
		  "PN",		//PROJECTNUMBER
		  "TNUM",	//TASK_NUMBER
		  "MAA1",	//AGENCY
		  "MAN1",
		  "MAA2",	//AGENCY
		  "MAN2",
		  "CL",
		  "SU",		//NOTES,LANGUAGE
		  "AV",		//AVAILABILITY
		  "DES",	//CONTROLLED_TERMS
		  "NU1",
		  "IDE",	//UNCONTROLLED_TERMS
		  "HN",		//AUTHOR
		  "AB", 	//ABSTRACT
		  "NU2",
		  "IB",
		  "TA",		//TITLE_ANNOTATION
		  "NU3",
		  "NU4",
		  "LC",
		  "SE",
		  "CAC",	//CORPORATE_SOURCE_CODES
		  "DLC",	//DTICLimitationCode
		  "LOAD_NUMBER"

		};

		public static final String[] xmlbaseTableFields = {
				"M_ID",							//VARCHAR2(64)
				"AccessionNumber",				//VARCHAR2(53)
				"CategoryCode",					//Clob
				"SourceCode",					//Clob
				"CountryCode",					//Clob
				"LanguageCode",      			//Clob
				"Media",						//Clob
				"PrimaryAuthor",				//Varchar2(1000)
				"SecondaryAuthor",				//Clob
				"SponsorAuthor",				//Clob
				"Title",						//varchar2(4000)
				"GVVII",						//varchar2(500)
				"TitleNote",					//varchar2(4000)
				"PersonalAuthor",				//Clob
				"ReportMonth",					//varchar2(10)
				"ReportDay",					//varchar2(10)
				"ReportYear",					//varchar2(4)
				"PageCount",					//varchar2(20)
				"ReportNumber",					//Clob
				"ContractNumber",				//Clob
				"GrantNumber",					//Clob
				"OrderNumber",					//Clob
				"ProjectNumber",				//Clob
				"TaskNumber",					//Clob
				"MonitorAgencyNumber",			//Clob
				"SupplementaryNotes",			//varchar2(1000)
				"AvailabilityNote",				//varchar2(1000)
				"Descriptor",					//Clob
				"Identifier",					//Clob
				"Abstract",						//Clob
				"DTICLimitationCode",			//varchar2(2000)
				"PrimaryAuthorCode",			//varchar22(2000)
				"SecondaryAuthorCode",			//varchar2(2000)
				"LOAD_NUMBER",					//number
				"SponsorAuthorCode"			//varchar2(2000)

	};

/*
	create table ntis_master(
	M_ID					VARCHAR2(64),
	AccessionNumber			VARCHAR2(53),
	CategoryCode			Clob,
	SourceCode				Clob,
	CountryCode				Clob,
	LanguageCode      		Clob,
	Media					Clob,
	PrimaryAuthor			Varchar2(1000),
	SecondaryAuthor			Clob,
	SponsorAuthor			Clob,
	title					varchar2(4000),
	GVVII					varchar2(500),
	TitleNote				varchar2(4000),
	PersonalAuthor			Clob,
	ReportMonth				varchar2(10),
	ReportDay				varchar2(10),
	ReportYear				varchar2(4),
	PageCount				varchar2(20),
	ReportNumber			Clob,
	OrderNumber				Clob,
	GrantNumber				Clob,
	ContractNumber			Clob,
	ProjectNumber			Clob,
	TaskNumber				Clob,
	MonitorAgencyNumber		Clob,
	SupplementaryNotes		varchar2(1000),
	AvailabilityNote		varchar2(1000),
	Descriptor				Clob,
	Identifier				Clob,
	Abstract				Clob,
	DTICLimitationCode		varchar2(2000),
	PrimaryAuthorCode		varchar2(2000),
	SecondaryAuthorCode		varchar2(2000)
	SponsorAuthorCode		varchar2(2000),
	LOAD_NUMBER				number);
*/
}
