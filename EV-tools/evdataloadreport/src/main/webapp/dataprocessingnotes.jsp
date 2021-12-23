<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Data Processing Notes</title>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/main.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/layout.css"/>
    
    <style type="text/css">
    h2{
    text-decoration: underline;
    color: #00008B;
    text-shadow: navy;
    font-size: 14px;
    }
    
   #list1, #list2, #list3
    {
    	font-size: 14px;
    	font-family: Courier
    }
    
    #notes
    {
    	padding-left: 10px;
    }
    
    </style>
</head>
<body bgcolor="#FFFAFA" topmargin="0">
<table border="0" cellspacing="0" cellpadding="0" width="99%">
  <tr><td valign="top" height="4"></td></tr>
  <tr><td valign="top" align="right"><a href="javascript:window.close();"><img src="${pageContext.servletContext.contextPath}/static/images/close.gif" border="0"/></a></td></tr>
</table>

<div id="section">

<div id="notes">
<h2 style="font-style: ">Records were rejected by EV program:</h2>


<ol id= "list1">
<li>Records already exist in database (most common case)</br> 
<b style="font-style: italic; font-family: Courier; color: #191970; font-style: italic;" >Note: to find exact cause(s) of this type of rejection, we need to perform further investigation</b>
</br><b>or</b></li>

<li>BD sent TOC first and later sent indexed records in new files. They should be sent in correction files</br><b>or</b></li>
<li>Missing Accession Number</br><b>or</b></li>
<li>Different records with same Accession Number or same PUI</br><b>or</b></li>
<li>Records with NULL PUI or NULL Accession Number</br><b>or</b></li>
<li>Records with NULL PUI or NULL Accession Number</br><b>or</b></li>
<li>Bad Accession Number (<itemid idtype="CPX">ORA-01722: invalid number</itemid></br><b>or</b></li>
<li>Records blocked per the request of EV Content Management Team</br><b>or</b></li>
<li>Conten missing xml tag which causes field shifting (eg, <%= "<sup3&gt;" %> )</br><b>or</b></li>

</ol>
<h2>Duplicated records in same week's source files:</h2>
<ol id= "list2">
<li>During a given week, multiple copies of some records are distributed across multiple files.</li>
</ol>


<h2>No record is matched:</h2>
<ol id="list3">
<li>BD send us records to delete which never loaded before or already been deleted</li>
</ol>


</div>

</div>


</body>
</html>