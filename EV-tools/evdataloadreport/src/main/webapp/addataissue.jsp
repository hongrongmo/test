<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add New Data Issue</title>

<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/reports.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/main.css"/>
     <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/tables.css"/>
   
  
   <style type="text/css">
   
   html,
body {
	margin:0;
	padding:0;
	height:100%;
}
#wrapper {
	min-height:100%;
	position:relative;
}
#header {
	background:#ededed;
	padding:10px;
}

#sidebar {
    position:absolute;
    top:0; bottom:0; left:0;
    width:180px;
    background:#148C75;
    margin-top:100px;
}



#content {
	padding-bottom:80px; /* Height of the footer element */
	padding-left: 200px;
}
#footer {
	background:#ffab62;
	width:100%;
	height:40px;
	position:absolute;
	bottom:0;
	left:0;
}
   
   
   </style>

</head>
<body>


<div id="wrapper">
		<%@ include file="/views/includes/header.jsp" %>

<!-- Display Issues List for the specified Category -->
	<div id="sidebar">
		<%@ include file="/views/includes/navlinks.jsp" %>
	</div>
		
<div id="content">


<p style="margin-top: 10px;"> Fill out necessary fields to add a new data issue</p>


<!--  Add New Issue -->	
<div id="addnewissue">

<%if(session !=null && session.getAttribute("ADDNEWDATAISSUE").toString().equalsIgnoreCase("true")) {%>

	<form name="f" action="${pageContext.servletContext.contextPath}/DataIssues" method="post"> 	 
  		 <table id="dataissue">
  			 <tbody>
  		 		
      			  <!-- title -->
						<tr>
							<td id="category">Issue: </td>
							<td><input type="text" name="txtboxtitle" id="info" required /><img class="editimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/edit.png" title=""></img>
							</td>
						</tr>
					
      				 <!-- category -->
						<tr>
							<td id="category">Category: </td>
							<td>
								<select name="issuecategory" id="info">
									<option value="bd">BD</option>
									<option value="cpx">CPX</option>
									<option value="aip">AIP</option>
									<option value="c84">C84</option>
									<option value="pch">PCH</option>
									<option value="elt">ELT</option>
									<option value="geo">GEO</option>
									<option value="chm">CHM</option>
									<option value="ins">INS</option>
									<option value="ibf">IBF</option>
									<option value="ept">EPT</option>
									<option value="nti">NTI</option>
									<option value="cbn">CBN</option>
									<option value="grf">GRF</option>
									<option value="gip">GIP</option>
									<option value="upt">UPT</option>
									<option value="eup">EUP</option>
									<option value="other">OTHER</option>
								</select>
							</td>
						</tr>
				
					<!-- type -->
						<tr>
							<td id="category">Type: </td>
							<td><input type="text" name="type" id="info"/><img class="editimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/edit.png" title=""></img>
							</td>
						</tr>
				
					<!-- Priority -->
					
						<tr>
							<td id="category">Priority: </td>
							<td><input type="text" name="priority" id="info"/><img class="editimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/edit.png" title=""></img>
							</td>
						</tr>

					
					<!-- Lables -->
						<tr>
							<td id="category">Labels: </td>
							<td><input type="text" name="lables" id="info"/><img class="editimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/edit.png" title=""></img>
							</td>
						</tr>
					
					<!-- Epic Name -->
						<tr>
							<td id="category">Epic Name: </td>
							<td><input type="text" name="epic_name" id="info"/><img class="editimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/edit.png" title=""></img>
							</td>
						</tr>
					
					<!-- Sprint -->
						<tr>
							<td id="category">Sprint: </td>
							<td><input type="text" name="sprint" id="info"/><img class="editimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/edit.png" title=""></img>
							</td>
						</tr>
					
					<!-- Status -->
						<tr>
							<td id="category">Status: </td>
							<td><input type="text" name="status" id="info"/><img class="editimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/edit.png" title=""></img>
							</td>
						</tr>
					
					<!-- Resolution -->
						<tr>
							<td id="category">Resolution: </td>
							<td><input type="text" name="resolution" id="info"/><img class="editimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/edit.png" title=""></img>
							</td>
						</tr>
					
					<!-- Assignee -->
						<tr>
							<td id="category">Assignee: </td>
							<td><input type="text" name="assignee" id="info"/><img class="editimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/edit.png" title=""></img>
							</td>
						</tr>
					
					<!-- Reporter -->
						<tr>
							<td id="category">Reporter: </td>
							<td><input type="text" name="reporter" id="info"/><img class="editimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/edit.png" title=""></img>
							</td>
						</tr>
					
					
					<!-- Description -->
						<tr>
							<td id="category">Description: </td>
							<td><textarea rows="10" cols="80" name="description">${item.DESCRIPTION}
							</textarea></td>
							
						</tr>
				
				<tr>
				<td ><input name="submit" type="submit" value="Add" /></td>
				</tr>
			</tbody>
		</table>
	</form>	
<%} %>

</br>
</br>
</div>



</div>



<div id="footer">
<p id="copyright">Copyright &copy; &nbsp;<%= new java.util.Date().getYear() + 1900 %>&nbsp;<a
	title="Elsevier home page (opens in a new window)" target="_blank"
	href="http://www.elsevier.com">Elsevier B.V.</a> All rights reserved.
</p>
</div>


</div>
	
	
</body>
</html>