create or replace type STRARRAY as table of varchar2(4000);
create or replace type INTARRAY as table of number;
/
create or replace package ev2_StoredProcedure as
  
s STRARRAY;

procedure test;
PROCEDURE insertTrial(sqlQuery VARCHAR2);
PROCEDURE insertArray(sqlArray STRARRAY);
PROCEDURE insertS(	firstName 		VARCHAR2,
					lastName		VARCHAR2,
					jobTitle		VARCHAR2,
					company			VARCHAR2,
					address1		VARCHAR2,
					address2		VARCHAR2,
					webSite			VARCHAR2,
					city			VARCHAR2,
					state			VARCHAR2,
					zip				VARCHAR2,
					country			VARCHAR2,
					phone			VARCHAR2,
					emailaddress	VARCHAR2,
					howHear			VARCHAR2,
					howHearExplained	VARCHAR2,
					product			VARCHAR2,
					mail			VARCHAR2,
					email			VARCHAR2,
					referringURL	VARCHAR2,
					trialDate		Date,
					ID				VARCHAR2);
end;
/
create or replace package body ev2_StoredProcedure 
as
procedure test is
begin
   	s(1) := 'Apple';
   	s(2) := 'Banana';
   	s(3) := 'Pear';
   	insertArray(s);
EXCEPTION  
   WHEN OTHERS THEN
   		DBMS_OUTPUT.put_line('error in procedure '||sqlerrm);
end;

PROCEDURE insertTrial(sqlQuery VARCHAR2) IS
BEGIN
	EXECUTE IMMEDIATE sqlQuery;
	COMMIT;
EXCEPTION  
   WHEN OTHERS THEN
   		ROLLBACK;
      	RAISE_APPLICATION_ERROR(-20101, 'Error in procedure.');
END;


PROCEDURE insertArray(sqlArray in STRARRAY) IS

BEGIN
	for I in 1 .. sqlArray.count
	loop
		EXECUTE IMMEDIATE sqlArray(i);
		--DBMS_OUTPUT.put_line('query String '||sqlArray(i));
	end loop;
	commit;
EXCEPTION  
   WHEN OTHERS THEN
   		ROLLBACK;
   		--DBMS_OUTPUT.put_line('error in procedure '||sqlerrm);
      	RAISE_APPLICATION_ERROR(-20101, 'Error in procedure. count='||sqlArray.count||' first='||sqlArray(1)||' second='||sqlArray(2)||'error='||sqlerrm);
END;

PROCEDURE insertS(	firstName 		VARCHAR2,
					lastName		VARCHAR2,
					jobTitle		VARCHAR2,
					company			VARCHAR2,
					address1		VARCHAR2,
					address2		VARCHAR2,
					webSite			VARCHAR2,
					city			VARCHAR2,
					state			VARCHAR2,
					zip				VARCHAR2,
					country			VARCHAR2,
					phone			VARCHAR2,
					emailaddress	VARCHAR2,
					howHear			VARCHAR2,
					howHearExplained	VARCHAR2,
					product			VARCHAR2,
					mail			VARCHAR2,
					email			VARCHAR2,
					referringURL	VARCHAR2,
					trialDate		Date,
					ID				VARCHAR2) IS
BEGIN
	insert into trial_user values(firstName,lastName,jobTitle,company,address1,address2,webSite,city,state,zip,country,phone,emailaddress,howHear,howHearExplained,product,mail,email,referringURL,trialDate,ID);
	COMMIT;
EXCEPTION  
   WHEN OTHERS THEN
   		ROLLBACK;
      	RAISE_APPLICATION_ERROR(-20101, 'Error in procedure insert.');
END;

end ev2_StoredProcedure;
/
show error;


create or replace PROCEDURE insertS(firstName VARCHAR2,lastName VARCHAR2,jobTitle VARCHAR2,
company VARCHAR2,address1 VARCHAR2,address2 VARCHAR2,webSite	VARCHAR2,city VARCHAR2,
state VARCHAR2,zip VARCHAR2,country VARCHAR2,phone VARCHAR2,emailaddress VARCHAR2,howHear VARCHAR2,
howHearExplained	VARCHAR2,product VARCHAR2,mail VARCHAR2,email VARCHAR2,referringURL	VARCHAR2,
trialDate Date,ID VARCHAR2) IS
BEGIN
	insert into trial_user values(firstName,lastName,jobTitle,company,address1,address2,webSite,
	city,state,zip,country,phone,emailaddress,howHear,howHearExplained,product,mail,email,referringURL,trialDate,ID);
	COMMIT;
EXCEPTION  
   WHEN OTHERS THEN
   		ROLLBACK;
      	RAISE_APPLICATION_ERROR(-20101, 'Error in procedure insert.');
END;


declare
type queryArray is table of varchar2(32000) index by binary_integer;
sqlArray  	queryArray;
begin
sqlArray(1):='one';
sqlArray(2):='two';
ev2_StoredProcedure.insertArray(sqlArray);
end;
/


create or replace PROCEDURE insertArray(sqlArray vArray) AS

BEGIN
	for I in 1..sqlArray.count
	loop
		EXECUTE IMMEDIATE sqlArray(i);
	end loop;
	commit;
EXCEPTION  
   WHEN OTHERS THEN
   		ROLLBACK;
   		DBMS_OUTPUT.put_line('error in procedure '||sqlerrm);
      	RAISE_APPLICATION_ERROR(-20101, 'Error in procedure.');
END;

declare
type array is table of varchar2(30) index by binary_integer;
a array;
procedure p( array_in array )
is
begin
  for i in 1..array_in.count loop
    dbms_output.put_line( array_in(i) );
   end loop;
 end;
 begin
    a(1) := 'Apple';
    a(2) := 'Banana';
    a(3) := 'Pear';
    p( a );
end;
/


create or replace package ev2test as
	type array is table of varchar2(30) index by binary_integer;
	a array;
	procedure p( array_in array );
	procedure test;
end;
/
show error;

create or replace package body ev2test as
procedure p( array_in array )
is
begin
  for i in 1..array_in.count loop
    dbms_output.put_line( array_in(i) );
    end loop;
 end;
 procedure test is
 begin
   a(1) := 'Apple';
   a(2) := 'Banana';
   a(3) := 'Pear';
   p( a );
 end;
end ev2test;
/
show error;
