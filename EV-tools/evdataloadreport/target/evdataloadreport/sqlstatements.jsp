<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Useful SQL Statements</title>

 <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/main.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/tables.css"/>
    
    <style type="text/css">
    h2{
    text-decoration: underline;
    color: #00008B;
    text-shadow: navy;
    font-size: 18px;
    font-weight: bold;
    }
    
   #list1, #list2, #list3
    {
    	font-size: 14px;
    	font-family: Courier
    }
    
    
    </style>
    
</head>
<body bgcolor="#FFFAFA" topmargin="0" style="margin-left: 10px; margin-bottom: 10px; margin-right: 20px;">
<table border="0" cellspacing="0" cellpadding="0" width="99%">
  <tr><td valign="top" height="4"></td></tr>
  <tr><td valign="top" align="right"><a href="javascript:window.close();"><img src="${pageContext.servletContext.contextPath}/static/images/close.gif" border="0"/></a></td></tr>
</table>

<div id="section">
<h2>Useful Sql Statement:</h2>

<p style="font-style: italic; font-family: Courier; color: #4B0082; font-style: italic; font-size: 16px;" ><b>Note:</b> you can copy the sql statement shown and run it in sqlplus/sqldeveloper after <b>substituting any parameters</b> (if there any)
</p>

</br>


<table id="sql">
	<thead>
		<tr>
			<th>query type</th>
			<th>Sql Statement</th>
		</tr>
	</thead>
	<tbody>
	<tr>
			<td id="sqltype">Table Space (Total Space Usage)</td>
			<td>
				
				select (select sum(bytes)/1024/1024/1024 from dba_data_files) + (select BYTES/1024/1024/1024 from dba_temp_files) from dual; 
				
			</td>
		</tr>
		
		<tr>
			<td id="sqltype">Table Structure</td>
			<td>
				<% if(session.getAttribute("TABLENAME") !=null)
				{
					String query = "select COLUMN_ID, COLUMN_NAME, DATA_TYPE, DATA_LENGTH, NULLABLE, DATA_DEFAULT from user_TAB_COLUMNS where"+ 
					" table_name = '"+session.getAttribute("TABLENAME").toString().toUpperCase()+"' order by COLUMN_ID;";
					out.println(query);
				}
				else
				{
					%>
				select COLUMN_ID, COLUMN_NAME, DATA_TYPE, DATA_LENGTH, NULLABLE, DATA_DEFAULT from user_TAB_COLUMNS where 
				table_name ='[<b>Replace with actual table name</b>]' order by COLUMN_ID";
				<%} %>
			</td>
		</tr>
		
		<tr>
			<td id="sqltype"> Table Indexes</td>
			<td>
				<%if(session.getAttribute("TABLENAME") !=null)
					{
						String query = "select a.table_owner,a.index_name,a.uniqueness,a.status,a.index_type,a.temporary,a.partitioned,a.funcidx_status, a.join_index,b.col, c.column_expression "+
								"from user_indexes a, " +
								"(select index_name,listagg(COLUMN_NAME||',') WITHIN GROUP (ORDER BY column_position) col "+
								"from USER_IND_COLUMNS "+
								"where table_name='"+session.getAttribute("TABLENAME").toString().toUpperCase()+"' "+
								"group by index_name) b,USER_IND_EXPRESSIONS c  where "+
								"a.INDEX_NAME = b.INDEX_NAME and a.INDEX_NAME = c.INDEX_NAME (+) and a.table_name='"+session.getAttribute("TABLENAME").toString().toUpperCase()+"' order by a.index_name desc;";
						out.println(query);
					}
				else
				{%>
					select a.table_owner,a.index_name,a.uniqueness,a.status,a.index_type,a.partitioned,a.funcidx_status, a.join_index,b.column_name, c.column_expression 
					from user_indexes a, 
					(select index_name,listagg(COLUMN_NAME||',') WITHIN GROUP (ORDER BY column_position) column_name
					from USER_IND_COLUMNS
					where table_name='[<b>Replace with actual table name</b>]'
					group by index_name) b,USER_IND_EXPRESSIONS c  where 
					a.INDEX_NAME = b.INDEX_NAME and a.INDEX_NAME = c.INDEX_NAME (+) and a.table_name='[<b>Replace with actual table name</b>]' order by a.index_name desc;
				<%}
				%>
			</td>
		</tr>	
		
		<tr>
		<td id="sqltype"> Table Constraints</td>
		<td>
			<%if(session.getAttribute("TABLENAME") !=null) 
			{
				String query = "select owner,constraint_name,constraint_type,search_condition,status,generated,"+
						"last_change,index_name,index_owner from user_constraints where table_name='"+session.getAttribute("TABLENAME").toString().toUpperCase()+"' order by constraint_name;";
				out.println(query);
			}
			else
			{%>
				select owner,constraint_name,constraint_type,search_condition,status,generated,
				last_change,index_name,index_owner from user_constraints where table_name='[<b>Replace with actual table name</b>]' order by constraint_name";
			<%}
			%>
		</tr>	
		
		<!-- Session Queries -->
		
		<tr>
			<td id="sqltype"> All sessions count by users</td>
			<td>
				select nvl(username, 'Background Processes') as username, count(*) as count from v$session group by username order by username
			</td>
		</tr>
		
		<tr>
			<td id="sqltype"> All Sessions</td>
			<td>
				select sid,serial# as serial_num,username,osuser,program, machine, terminal, to_char(logon_time, 'mm-dd-yyyy HH24:MI:SS')  from v$session 
				order by sid;
			</td>
		</tr>	
		
		<tr>
			<td id="sqltype"> Sessions of a specific Oracle user</td>
			<td>
			<%if(session.getAttribute("SESSIONNAME") !=null) 
			{ %>
				select sid,serial# as serial_num,username,osuser,program, machine, terminal, to_char(logon_time, 'mm-dd-yyyy HH24:MI:SS') as datetime 
				from v$session where username='<%= session.getAttribute("SESSIONNAME")%>';
			<%}
			else
			{%>
				select sid,serial# as serial_num,username,osuser,program, machine, terminal, to_char(logon_time, 'mm-dd-yyyy HH24:MI:SS') as datetime 
				from v$session where username='[<b>Replace with actual Oracle User Name</b>]';
			<%} %>
			</td>
		</tr>
		
		
		<tr>
			<td id="sqltype"> Sessions of a specific oracle user and an OS user</td>
			<td>
			<%if(session.getAttribute("SESSIONNAME") !=null && session.getAttribute("OSUSER") !=null) 
			{ %>
				select sid,serial# as serial_num,username,osuser,program, machine, terminal, to_char(logon_time, 'mm-dd-yyyy HH24:MI:SS') as datetime 
				from v$session where username='<%= session.getAttribute("SESSIONNAME")%>' and upper(osuser)='<%= session.getAttribute("OSUSER").toString().toUpperCase() %>';
			<%}
			else
			{%>
				select sid,serial# as serial_num,username,osuser,program, machine, terminal, to_char(logon_time, 'mm-dd-yyyy HH24:MI:SS') as datetime 
				from v$session where username='[<b>Replace with actual Oracle User Name</b>]' and upper(osuser)='[<b>Replace with actual OsUser</b>]';
			<%} %>
			</td>
		</tr>
		
		
		<tr>
			<td id="sqltype"> Kill Session</td>
			<td>
				sid: parameter1, serial#:parameter2
				</br>
				exec rdsadmin.rdsadmin_util.kill(parameter1, parameter2, 'IMMEDIATE');
			</td>
		</tr>
		
		<tr>
			<td id="sqltype"> Opened Cursors</td>
			<td>
				select s.username, a.value, s.sid, s.serial# as serial_num, s.OSUSER, s.PROCESS, s.MACHINE, to_char(s.LOGON_TIME, 'mm-dd-yyyy HH24:MI:SS') as datetime </br> 
				from v$sesstat a, v$statname b, v$session s </br>
				where s.username not in ('SYSTEM','APPQOSSYS','DBSNMP','OUTLN','SYS','CTXSYS', 'RDSADMIN', 'SYSMAN') </br>
				and a.statistic# = b.statistic#  and s.sid=a.sid </br>
				and b.name = 'opened cursors current' order by s.username, a.value desc;
			</td>
		</tr>
		
		
		<tr>
			<td id="sqltype"> Show All Parameters</td>
			<td>
				select NUM, NAME,  VALUE , description, display_value, TYPE,ISSES_MODIFIABLE from v$parameter order by NUM;
			</td>
		</tr>
		
		
		<tr>
			<td id="sqltype"> Show a Specific Parameter</td>
			<td>
			<%if(session.getAttribute("SESSIONNAME") !=null && session.getAttribute("OSUSER") !=null && session.getAttribute("PARAMETERNAME") !=null) 
			{ %>
				select NUM, NAME,  VALUE , description, display_value, TYPE,ISSES_MODIFIABLE from v$parameter 
				where lower(NAME)='<%= session.getAttribute("PARAMETERNAME").toString().toLowerCase() %>'";
			<%} 
			else
			{%>
				select NUM, NAME,  VALUE , description, display_value, TYPE,ISSES_MODIFIABLE from v$parameter 
				where lower(NAME)='[<b>Replace with actual Parameter Name</b>]';
			<%} %>
			</td>
		</tr>
		
		<tr>
			<td id="sqltype"> Show Control File</td>
			<td>
				select name,block_size,file_size_blks,status,is_recovery_dest_file from v$controlfile;
			</td>
		</tr>
		
		<tr>
			<td id="sqltype"> Show Log File</td>
			<td>
				select member,group# as group_num,type,status,is_recovery_dest_file from v$logfile
			</td>
		</tr>
		
		<tr>
			<td id="sqltype"> Show Rollback Segment</td>
			<td>
				select d.segment_name, d.tablespace_name, s.waits, s.shrinks, s.wraps, s.status </br>
				from v$rollstat s, dba_rollback_segs d </br>
				where s.usn = d.segment_id </br>
				order by 1;
			</td>
		</tr>
		
	</tbody>
</table>

</div>
</body>
</html>