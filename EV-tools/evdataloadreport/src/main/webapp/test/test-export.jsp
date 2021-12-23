<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
     <%@ page import="org.ei.Export" %>
     
     
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

<!-- 
<input type="button" class="test" onclick="Retrivetable()" value="Export to Pdf" align="bottom"/>

<table id="historyTable">
<tr>
<td> Test1</td>
<td> Test2</td>
<td> Test3</td>
</tr>
<tr>
<td> t1</td>
<td> t2</td>
<td> t3</td>
</tr>

<tr>
<td> te1</td>
<td> te2</td>
<td> te3</td>
</tr>

</table>
<script language="javascript" type="text/javascript">
                 function Retrivetable()
                {
                var table = document.getElementById("historyTable");

                if (table) {

                  // If outerHTML property available, use it
                  if (typeof table.outerHTML == 'string') {
                    $('#settable').val(table.outerHTML)
                  // Otherwise, emualte it
                  } else {
                    var div = document.createElement('div');
                    div.appendChild(table.cloneNode(true));
                    $('#settable').val(div.innerHTML);
                  }
                }
                } 
                </script> -->

<!--  WORKS well -->
<%-- <%new Export().createPdf("C:/ws/EV-tools/evdataloadreport/test_table.pdf");%> --%>



<h1></h1>

<%-- <input type="button" name="Export" onclick="<%new Export().createPdf("C:/ws/EV-tools/evdataloadreport/test_table.pdf");%>" value="TestPDF"/> --%>

<!--  Partially works  -->
<%-- <%
String filename = "newFileName.pdf";
String filepath = "C:/ws/EV-tools/evdataloadreport/";
response.setContentType("application/pdf");
response.setHeader("Content-Type", "application/pdf");
/* response.setContentType("APPLICATION/OCTET-STREAM"); */
response.setHeader("Content-Disposition","attachment; filename=\"" + filename + "\"");



java.io.FileInputStream fileInputStream = new java.io.FileInputStream(filepath + filename);

int i;
while ((i=fileInputStream.read()) != -1) {
out.write(i);
}
fileInputStream.close();
%>
 --%>
 ${pageContext.request.contextPath}/Resources
 
 <%=getServletContext().getRealPath("Report.pdf") %>
 
 <%=getServletContext().getContextPath() %>
 
 <object data="${pageContext.request.contextPath}/newFileName.pdf" type="application/pdf" width="500" height="300">
    <a href="${pageContext.request.contextPath}/PdfServlet">Download file.pdf</a>
</object>


<%out.println("here is the remote user" + "${pageContext.request.remoteUser}"); %>


<a
	title="Elsevier home page (opens in a new window)" target="_blank"
	href="http://www.elsevier.com">Elsevier B.V.</a> All rights reserved.
	
 
    <%= new java.util.Date().getYear() + 1900 %>
    
    
              
</body>
</html>