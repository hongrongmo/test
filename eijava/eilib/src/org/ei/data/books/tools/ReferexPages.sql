
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

update book_pages_S300 set (PP,YR,TI,AUS,CVS,AF,PN,ST,SP,ISS,VO,AB,SUB,PDFPP) = (  select PP,YR,TI,AUS,CVS,AF,PN,ST,SP,ISS,VO,AB,SUB,PDFPP from BOOKS_ALL where book_pages_S300.BN13= BOOKS_ALL.BN13)

/* Update Books Series 2 Metadata */
update book_pages_S300 set (YR,CVS,PN,ST,VO,AB,SUB,PDFPP) = (  select YR,CVS,PN,ST,VO,AB,SUB,PDFPP from BOOKS_ALL
  where book_pages_S300.BN13= BOOKS_ALL.BN13) where book_pages_S300.BN13 IN ('9780444898470', '9780444896131', '9780444882738', '9780444829962', '9780444829528', '9780444828231', '9780444825636', '9780444825629', '9780444824219', '9780444522030', '9780444521620', '9780444521194', '9780444519993', '9780444517852', '9780444516992', '9780444516879', '9780444516060', '9780444515575', '9780444514936', '9780444514653', '9780444513540', '9780444509697', '9780444505033', '9780444504982', '9780444503510', '9780444502063', '9780444500458', '9780444500359', '9780340652626', '9780340652619', '9780340652602', '9780125769600', '9780125443616', '9780125443609', '9780124605305', '9780124605299', '9780124605237', '9780123705822', '9780123705327', '9780121569327', '9780121569310', '9780120885152', '9780120883813', '9780120455409', '9780080447087', '9780080446721', '9780080446332', '9780080445540', '9780080444772', '9780080442129', '9780080441481', '9780080441290', '9780080439709', '9780080439532', '9780080439365', '9780080437903', '9780080437125', '9780080436999', '9780080436890', '9780080430225', '9780080429991', '9780080427034', '9780080426983', '9780080426792')
/* Update Books Series 1 Metadata */
update book_pages_S300 set (YR,CVS,PN,ST,VO,AB,SUB,PDFPP) = (  select YR,CVS,PN,ST,VO,AB,SUB,PDFPP from BOOKS_ALL
  where book_pages_S300.BN13= BOOKS_ALL.BN13) where book_pages_S300.BN13 IN ('9780120121540','9780120121564','9780120121588','9780120121595')


update book_pages_WOBL set (PP,YR,TI,AUS,CVS,AF,PN,NT,ST,SP,ISS,VO,AB,SUB,PDFPP) = (   select PP,YR,TI,AUS,CVS,AF,PN,NT,ST,SP,ISS,VO,AB,SUB,PDFPP from books_991 where book_pages_WOBL.bn13=books_991.bn13 )
update book_pages_406 set (PP,YR,TI,AUS,CVS,AF,PN,NT,ST,SP,ISS,VO,AB,SUB,PDFPP) = ( select PP,YR,TI,AUS,CVS,AF,PN,NT,ST,SP,ISS,VO,AB,SUB,PDFPP from books where book_pages_406.bn13=books.bn13)

/* ISBN 11 books 'a', 'b','c' ie  (BN13 LIKE '%c' OR BN13 LIKE '%b' OR BN13 LIKE '%a') */
update book_pages_406 set (PP,YR,TI,AUS,CVS,AF,PN,NT,ST,SP,ISS,VO,AB,SUB,PDFPP) = ( select PP,YR,TI,AUS,CVS,AF,PN,NT,ST,SP,ISS,VO,AB,SUB,PDFPP from books where book_pages_406.bn13=books.bn13)
  where  book_pages_406.BN13 IN ('9780080426990a','9780080426990b','9780080439501a','9780444511409a','9780444511409b','9780444898753a','9780444898753b','9780750610773a','9780750610773b','9780750615471a','9780750615471b','9780750615471c','9780750636056a','9780750636056b')


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

Check Collection and Sub Collection Counts
--------------------------------------------------------------------------------------
select COUNT(*), VO, SUB FROM BOOKS_ALL GROUP BY VO, SUB

See ISBNs
select BN13, VO||SUB FROM BOOKS_ALL GROUP BY SUB, VO, BN13




SQL> INSERT INTO BOOK_PAGES_ALL /*+ APPEND NOLOGGING */ (DOCID,BN,BN13,PII,PAGE_NUM,PAGE_LABEL,SECTION_TITLE,SECTION_START,CHAPTER_TITLE,CHAPTER_START,PAGE_BYTES,PAGE_TXT,PAGE_TOTAL,PAGE_KEYWORDS,PP,YR,TI,AUS,CVS,AF,PN,NT,ST,SP,ISS,VO,AB,SUB,PDFPP)
  2  select DOCID,BN,BN13,PII,PAGE_NUM,PAGE_LABEL,SECTION_TITLE,SECTION_START,CHAPTER_TITLE,CHAPTER_START,PAGE_BYTES,PAGE_TXT,PAGE_TOTAL,PAGE_KEYWORDS,PP,YR,TI,AUS,CVS,AF,PN,NT,ST,SP,ISS,VO,AB,SUB,PDFPP
  3  FROM BOOK_PAGES_S300
  4  WHERE BOOK_PAGES_S300.BN13 IN
  5  (SELECT S300S.bn13 FROM S300S LEFT JOIN WOBLS ON SUBSTR(S300S.BN13,1,13)=SUBSTR(WOBLS.BN13,1,13) WHERE WOBLS.bn13 IS NULL)
  6  ;


select 'titles.put("'||BN13||'","'||TI||DECODE(ST,NULL,'',': '||ST)||'");' from BOOKS_ALL ORDER BY TI, ST

select 'titles.put("'||BN13||'","'||TI||'");' from BOOKS_ALL ORDER BY TI






update book_pages_S300 set (PP,YR,TI,AUS,CVS,AF,PN,ST,SP,ISS,VO,AB,SUB,PDFPP) = (  select PP,YR,TI,AUS,CVS,AF,PN,ST,SP,ISS,VO,AB,SUB,PDFPP from BOOKS_991 where book_pages_S300.BN13= BOOKS_991.BN13)




/* Copy out 19 'Bad Books' into temp table to be replaced by S300 data */
INSERT INTO BOOK_PAGES_406_OLD /*+ APPEND NOLOGGING */ (DOCID,BN,BN13,PII,PAGE_NUM,PAGE_LABEL,SECTION_TITLE,SECTION_START,CHAPTER_TITLE,CHAPTER_START,PAGE_BYTES,PAGE_TXT,PAGE_TOTAL,PAGE_KEYWORDS,PP,YR,TI,AUS,CVS,AF,PN,NT,ST,SP,ISS,VO,AB,SUB,PDFPP)
  select DOCID,BN,BN13,PII,PAGE_NUM,PAGE_LABEL,SECTION_TITLE,SECTION_START,CHAPTER_TITLE,CHAPTER_START,PAGE_BYTES,PAGE_TXT,PAGE_TOTAL,PAGE_KEYWORDS,PP,YR,TI,AUS,CVS,AF,PN,NT,ST,SP,ISS,VO,AB,SUB,PDFPP
  FROM BOOK_PAGES_406
  WHERE BOOK_PAGES_406.BN13 IN ('9780750649322','9780750657976','9780750657730','9780750639965','9780750645683','9780750648332','9780750651004','9780750650038','9780122374616','9780750677011','9780120777907','9780750651318','9780750658089','9780750650762','9780750655200','9780750674959','9780750674027','9780750648851','9780123875822')