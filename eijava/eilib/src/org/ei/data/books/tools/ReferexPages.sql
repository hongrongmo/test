
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
PII           - S300 Chapter ID
PAGE_NUM      - Page number (INDEX) in PDF file
PAGE_LABEL    - Label appearing on page in book
SECTION_TITLE - Title of containing section
SECTION_START - Start page of contaning section
CHAPTER_TITLE - Title of containing CHAPTER
CHAPTER_START - Start page of contaning CHAPTER
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
  PII            VARCHAR2(64 BYTE),
  PAGE_NUM       INTEGER,
  PAGE_LABEL     VARCHAR2(32 BYTE),
  SECTION_TITLE  VARCHAR2(256 BYTE),
  SECTION_START  INTEGER,
  CHAPTER_TITLE  VARCHAR2(256 BYTE),
  CHAPTER_START  INTEGER,
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

BOOKS SQL Creation script description
--------------------------------------------------------------------------------------
CREATE TABLE BOOKS
(
  BN     VARCHAR2(32 BYTE)                      NOT NULL,
  BN13   VARCHAR2(32 BYTE),
  PP     VARCHAR2(80 BYTE),
  YR     VARCHAR2(10 BYTE),
  TI     VARCHAR2(1280 BYTE),
  AUS    VARCHAR2(1500 BYTE),
  CVS    VARCHAR2(640 BYTE),
  AF     VARCHAR2(768 BYTE),
  PN     VARCHAR2(88 BYTE),
  NT     VARCHAR2(1500 BYTE),
  ST     VARCHAR2(192 BYTE),
  SP     VARCHAR2(768 BYTE),
  ISS    VARCHAR2(32 BYTE),
  VO     VARCHAR2(32 BYTE),
  AB     CLOB,
  SUB    NUMBER,
  PDFPP  VARCHAR2(16 BYTE)
)


SQLLDR Command
--------------------------------------------------------------------------------------
sqlldr.exe AP_PRO1/ei3it@NEPTUNE direct=false data=output.out control=ReferexPages.sqlldr.ctl log=ReferexPages.sqlldr.log bad=ReferexPages.sqlldr.bad silent=FEEDBACK

sqlldr.exe AP_PRO1/ei3it@NEPTUNE direct=false control=bookPages.ctl log=sqlldr.log bad=sqlldr.bad silent=FEEDBACK bindsize=512000 rows=10000

SQLLDR Control File
--------------------------------------------------------------------------------------
See PVCS file ReferexPages.sqlldr.ctl

Testing queries
--------------------------------------------------------------------------------------
SELECT * FROM book_pages_new where section_title is null
SELECT * FROM book_pages_new where page_num is null
SELECT * FROM book_pages_new where page_start is null or page_start <=0
SELECT * FROM book_pages_new where page_total is null or page_total <=0

Merge/Update queries

DO NOT RUN UNLESS TABLES ARE INDEXED ON WHERE/JOIN CONDITION FIELD(S)
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

update book_pages_S300 set (YR, PN,  ST, AB, ISS, VO, CVS, SUB ) = (
  select books_991.YR, books_991.PN,  books_991.ST, books_991.AB, books_991.ISS, books_991.VO, books_991.CVS, books_991.SUB  from books_991  where book_pages_s300.bn13=books_991.bn13
)

update book_pages_WOBL set (PP,YR,TI,AUS,CVS,AF,PN,NT,ST,SP,ISS,VO,AB,SUB,PDFPP) = (
  select PP,YR,TI,AUS,CVS,AF,PN,NT,ST,SP,ISS,VO,AB,SUB,PDFPP from books_991 where book_pages_WOBL.bn13=books_991.bn13
)


update book_pages_406 set (PP,YR,TI,AUS,CVS,AF,PN,NT,ST,SP,ISS,VO,AB,SUB,PDFPP) = (
  select PP,YR,TI,AUS,CVS,AF,PN,NT,ST,SP,ISS,VO,AB,SUB,PDFPP from books where book_pages_406.bn=books.bn
)

Count Books that have been loaded/not loaded so far
--------------------------------------------------------------------------------------
SELECT count(unique(BOOKS_992.BN13)) FROM BOOKS_992
  LEFT JOIN BOOK_PAGES_TEMP ON BOOKS_992.BN13=BOOK_PAGES_TEMP.BN13
  WHERE BOOK_PAGES_TEMP.BN13 IS NOT NULL;


Data loading command for Keywords files(s)
--------------------------------------------------------------------------------------
sqlplus AP_PRO1/ei3it@neptune @c:\baja\eijava\eilib\keywords.sql



Isolate S300 files from the original 406/7
--------------------------------------------------------------------------------------

delete from book_pages_s300 where BN13 IN (select unique(BOOK_PAGES_s300.BN13) from BOOK_PAGES_s300 inner join BOOKS on BOOKS.BN13=BOOK_PAGES_s300.BN13)


select count(unique(BN13)) from book_pages_s300rds.sql

Isolate the non-overlapping S300 files from the original WOBLS
--------------------------------------------------------------------------------------
/* This example finds all rows in book_pages_s300 with an id value that is not present in WOBL or 406/7 */
/* I added the SUBSTR to catch the books in the WOBLS that have bn13's with Suffixes) */
SELECT S300S.bn13 FROM S300S
  LEFT JOIN WOBLS ON SUBSTR(S300S.BN13,1,13)=SUBSTR(WOBLS.BN13,1,13)
  WHERE WOBLS.bn13 IS NULL;



