--create runtime properties table for furture runtime properties access  

Create table RUNTIME_PROPERTIES(
NAME VARCHAR2(100) not null,
VALUE VARCHAR2(5000),
LAST_UPDATE timestamp Default SYSDATE
)   

insert into RUNTIME_PROPERTIES (name, value) VALUES ('SSO_CORE_REDIRECT_FLAG', 'true');

commit;


--Table for saving Tomcat seesion 

create table tomcat_sessions(
  session_id     varchar(100) not null primary key,
  valid_session  char(1) not null,
  max_inactive   NUMBER(10) not null,
  last_access    NUMBER(20)  not null,
  app_name       varchar(255),
  session_data   blob
);

commit;
