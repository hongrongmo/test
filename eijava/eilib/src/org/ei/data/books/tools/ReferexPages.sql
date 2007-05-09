
File Contents
--------------------------------------------------------------------------------------
BOOK_PAGES description
BOOK_PAGES SQL Creation script description
SQLLDR Command
SQLLDR Control File
Testing queries
Merge/Update queries


BOOK_PAGES table fields and descriptions
--------------------------------------------------------------------------------------
DOCID         - 'pag_' + BN + '_' + PAGE_NUM
BN            - ISBN10
BN13          - ISBN13
PAGE_NUM      - Page number in PDF file
SECTION_TITLE - Title of containing section
PAGE_START    - Start page of contaning collection
PAGE_BYTES    - Byte count of raw text file
PAGE_TXT      - raw page text
PAGE_TOTAL    - PDF file page count
PAGE_KEYWORDS - Indexed thesaurus terms
PP            - Actual book page count
YR            - Year published
TI            - Book title
AUS           - Book author(s) or editor(s) - ';' separated
CVS           - Controlled terms
AF            - NULL/Not In Use
PN            - Publisher
NT            - NULL/Not In Use
ST            - Secondary title
SP            - Label(s) for content of AUS field - ';' separated
ISS           - NULL/Not In use
VO            - Shortname for containing collection
AB            - Book description
SUB           - Sub collection number - NULL/EMPTY for original books
PDFPP         - PDF file page count


BOOK_PAGES SQL Creation script description
--------------------------------------------------------------------------------------
CREATE TABLE BOOK_PAGES_NEW
(
  DOCID          VARCHAR2(64 BYTE)              NOT NULL,
  BN             VARCHAR2(16 BYTE),
  BN13           VARCHAR2(16 BYTE),
  PAGE_NUM       INTEGER,
  SECTION_TITLE  VARCHAR2(256 BYTE),
  PAGE_START     INTEGER,
  PAGE_BYTES     INTEGER,
  PAGE_TXT       CLOB,
  PAGE_TOTAL     INTEGER,
  PAGE_KEYWORDS  VARCHAR2(4000 BYTE),
  PP             VARCHAR2(80 BYTE),
  YR             VARCHAR2(10 BYTE),
  TI             VARCHAR2(1280 BYTE),
  AUS            VARCHAR2(1500 BYTE),
  CVS            VARCHAR2(640 BYTE),
  AF             VARCHAR2(768 BYTE),
  PN             VARCHAR2(88 BYTE),
  NT             VARCHAR2(1500 BYTE),
  ST             VARCHAR2(192 BYTE),
  SP             VARCHAR2(768 BYTE),
  ISS            VARCHAR2(32 BYTE),
  VO             VARCHAR2(32 BYTE),
  AB             CLOB,
  SUB            NUMBER,
  PDFPP          VARCHAR2(16 BYTE)
)


SQLLDR Command
--------------------------------------------------------------------------------------
sqlldr.exe AP_PRO1/ei3it@NEPTUNE direct=false data=output.out control=ReferexPages.sqlldr.ctl log=ReferexPages.sqlldr.log bad=ReferexPages.sqlldr.bad silent=FEEDBACK

SQLLDR Control File
--------------------------------------------------------------------------------------
LOAD DATA
APPEND
INTO TABLE BOOK_PAGES_NEW
FIELDS TERMINATED BY "\t"
TRAILING NULLCOLS
( DOCID       CHAR,
  BN          CHAR,
  PAGE_NUM    CHAR,
  SECTION_TITLE     CHAR,
  PAGE_START CHAR,
  PAGE_BYTES  CHAR,
  PAGE_FILENAME FILLER CHAR(100),
  PAGE_TXT    LOBFILE(PAGE_FILENAME) TERMINATED BY EOF,
  PAGE_TOTAL CHAR )

Testing queries
--------------------------------------------------------------------------------------
SELECT * FROM book_pages_new where section_title is null
SELECT * FROM book_pages_new where page_num is null
SELECT * FROM book_pages_new where page_start is null or page_start <=0
SELECT * FROM book_pages_new where page_total is null or page_total <=0

Merge/Update queries
--------------------------------------------------------------------------------------
MERGE INTO BOOK_PAGES E1
USING BOOK_PAGES2 E2
ON ( E2.DOCID = E1.DOCID )
WHEN MATCHED THEN
    UPDATE SET E1.PAGE_BYTES = E2.PAGE_BYTES
WHEN NOT MATCHED THEN
	 INSERT (DOCID) VALUES (E2.DOCID)

update book_pages set (PP,YR,TI,AUS,CVS,AF,PN,NT,ST,SP,ISS,VO,AB,SUB,PDFPP) = (
select PP,YR,TI,AUS,CVS,AF,PN,NT,ST,SP,ISS,VO,AB,SUB,PDFPP from books where book_pages.bn=books.bn
)
