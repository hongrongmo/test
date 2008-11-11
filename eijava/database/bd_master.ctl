LOAD DATA
APPEND
INTO TABLE BD_MASTER
FIELDS TERMINATED BY '\t'
TRAILING NULLCOLS
(
M_ID                            CHAR (128),
ACCESSNUMBER                    CHAR(13),
ABSTRACTDATA                    CHAR(10000),
AUTHOR                          CHAR(4000),
CORRESPONDENCENAME              CHAR(4000),
CORRESPONDENCEEADDRESS          CHAR(2000),
CORRESPONDENCEAFFILIATION       CHAR(4000),
ISSUETITLE                      CHAR(2560),
SOURCETITLE                     CHAR(2560),
SOURCETITLEABBREV               CHAR(2560),
CONFNAME                        CHAR(1200),
ISBN                            CHAR(1000),
ISSN                            CHAR(9),
CODEN                           CHAR(10),
VOLUME                          CHAR(32),
ISSUE                           CHAR(65),
PUBLICATIONDATE                 CHAR(74),
PUBLICATIONYEAR                 CHAR(9),
CONFDATE                        CHAR(526),
CONFLOCATION                   	CHAR(1200),
CONFSPONSORS                    CHAR(2400),
MEDIA                           CHAR(26),
CSESS                           CHAR(140),
PATNO                           CHAR(22),
PLING                           CHAR(64),
APPLN                           CHAR(128),
PRIOR_NUM                       CHAR(1200),
CITTYPE                         CHAR(5),
ASSIG                           CHAR(300),
PCODE                           CHAR(22),
CLAIM                           CHAR(8),
SOURC                           CHAR(768),
NOFIG                           CHAR(20),
NOTAB                           CHAR(3),
SUB_INDEX                       CHAR(1300),
SPECN                           CHAR(1500),
SUPPL                           CHAR(200),
PDFIX                           CHAR(1583),
REPORTNUMBER                    CHAR(102),
DOI				CHAR(128),
PII				CHAR(128),
CONFCODE			CHAR(32),
ABSTRACTORIGINAL		CHAR(20),
CONTROLLEDTERM			CHAR(4000),
PUBLISHERNAME			CHAR(2400),
LOADNUMBER			CHAR(10),
PAGE				CHAR(768),
TREATMENTCODE			CHAR(16),
PAGECOUNT			CHAR(32),
CLASSIFICATIONCODE              CHAR(256),
CITATIONTITLE                   CHAR(1000),
REFCOUNT 	                CHAR(20),
CONFCATNUMBER			CHAR(64),
PUBLISHERADDRESS		CHAR(3200),
UNCONTROLLEDTERM		CHAR(4000),
EDITORS                         CHAR(4000),
CONFERENCEEDITOR		CHAR(2400),
CONFERENCEORGANIZATION		CHAR(1200),
CONFERENCEEDITORADDRESS    	CHAR(1200),
CITATIONLANGUAGE		CHAR(256),
DATABASE			CHAR(5)
)