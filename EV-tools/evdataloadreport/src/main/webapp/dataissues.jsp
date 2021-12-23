<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
     <%@ page import="java.util.ArrayList" %>
    <%@ page import="java.util.List" %>
    <%@ page import="java.util.Map" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Data Issues</title>
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
		<%if(session.getAttribute("ISSUESTITLELIST") !=null && ((ArrayList<String>)session.getAttribute("ISSUESTITLELIST")).size() >0){ %>
		
		<ul class="navlinks">
		
			 <c:forEach items="${ISSUESTITLELIST}" var="item">
  				<li>
  					<c:if test="${item ne null}"><a href="${pageContext.servletContext.contextPath}/DataIssues?title=${item}">${item}</a></c:if>
  				</li>
  			</c:forEach>
		</ul>
	 <%} else { %>
	 	<script type="text/javascript">alert("No Data available");
	 	window.location.href = "dataanalysis.jsp";
	 	</script>
	 
	 <%} %>
	</div>
		
<div id="content">

<%if(session.getAttribute("ISSUESTITLELIST") !=null && ((ArrayList<String>)session.getAttribute("ISSUESTITLELIST")).size() >0){ %>
	<p style="margin-top: 10px;"> to view/edit the issue, click on Issue Title on the left</p>
	<%} %>

<c:if test="${not empty param.status}">
	<c:if test="${param.status eq 1}">
  				   
  				   <script type="text/javascript">
				 	 alert('Record Updated Successfully, to view update content click issue title');
				  </script>
				  
				   </c:if>
				   
				   <c:if test="${param.status eq 0}">
  				   
  					<script type="text/javascript">
					alert('No Records Updated!');
					 </script>
				  
				</c:if>
</c:if>
				

<!-- Display Detailed Info of selected issue & edit -->		
  <div id="issues">		
		 <%if(session.getAttribute("DATAISSUESINFO") !=null && ((ArrayList<Map<String,String>>)session.getAttribute("DATAISSUESINFO")).size() >0)
  		 { %>
	 
  	<form name="f" action="${pageContext.servletContext.contextPath}/DataIssues" method="post"> 	 
  		 <table id="dataissue">
  			 <tbody>
  		 		 <c:forEach items="${DATAISSUESINFO}" var="item">
      			 
      			  <!-- title -->
					<c:if test="${item.TITLE ne null}">
						<tr>
							<td id="category">Issue: </td>
							<td><input type="text" name="txtboxtitle" value="${item.TITLE}" id="info"/><img class="editimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/edit.png" title=""></img>
							</td>
						</tr>
					</c:if>
					
      				 <!-- category -->
					<c:if test="${item.CATEGORY ne null}">
						<tr>
							<td id="category">Category: </td>
							<td>
								<select name="issuecategory" id="info">
									<option value="bd" <%if(session.getAttribute("DATAISSUECATEGORY") !=null && session.getAttribute("DATAISSUECATEGORY").toString().equalsIgnoreCase("bd")){ %> selected="selected"<%} %>>BD</option>
									<option value="cpx" <%if(session.getAttribute("DATAISSUECATEGORY") !=null && session.getAttribute("DATAISSUECATEGORY").toString().equalsIgnoreCase("cpx")){ %> selected="selected"<%} %>>CPX</option>
									<option value="aip"<%if(session.getAttribute("DATAISSUECATEGORY") !=null && session.getAttribute("DATAISSUECATEGORY").toString().equalsIgnoreCase("aip")){ %> selected="selected"<%} %>>AIP</option>
									<option value="c84" <%if(session.getAttribute("DATAISSUECATEGORY") !=null && session.getAttribute("DATAISSUECATEGORY").toString().equalsIgnoreCase("c84")){ %> selected="selected"<%} %>>C84</option>
									<option value="pch" <%if(session.getAttribute("DATAISSUECATEGORY") !=null && session.getAttribute("DATAISSUECATEGORY").toString().equalsIgnoreCase("pch")){ %> selected="selected"<%} %>>PCH</option>
									<option value="elt" <%if(session.getAttribute("DATAISSUECATEGORY") !=null && session.getAttribute("DATAISSUECATEGORY").toString().equalsIgnoreCase("elt")){ %> selected="selected"<%} %>>ELT</option>
									<option value="geo" <%if(session.getAttribute("DATAISSUECATEGORY") !=null && session.getAttribute("DATAISSUECATEGORY").toString().equalsIgnoreCase("geo")){ %> selected="selected"<%} %>>GEO</option>
									<option value="chm" <%if(session.getAttribute("DATAISSUECATEGORY") !=null && session.getAttribute("DATAISSUECATEGORY").toString().equalsIgnoreCase("chm")){ %> selected="selected"<%} %>>CHM</option>
									<option value="ins" <%if(session.getAttribute("DATAISSUECATEGORY") !=null && session.getAttribute("DATAISSUECATEGORY").toString().equalsIgnoreCase("ins")){ %> selected="selected"<%} %>>INS</option>
									<option value="ibf" <%if(session.getAttribute("DATAISSUECATEGORY") !=null && session.getAttribute("DATAISSUECATEGORY").toString().equalsIgnoreCase("ibf")){ %> selected="selected"<%} %>>IBF</option>
									<option value="ept" <%if(session.getAttribute("DATAISSUECATEGORY") !=null && session.getAttribute("DATAISSUECATEGORY").toString().equalsIgnoreCase("ept")){ %> selected="selected"<%} %>>EPT</option>
									<option value="nti" <%if(session.getAttribute("DATAISSUECATEGORY") !=null && session.getAttribute("DATAISSUECATEGORY").toString().equalsIgnoreCase("nti")){ %> selected="selected"<%} %>>NTI</option>
									<option value="cbn" <%if(session.getAttribute("DATAISSUECATEGORY") !=null && session.getAttribute("DATAISSUECATEGORY").toString().equalsIgnoreCase("cbn")){ %> selected="selected"<%} %>>CBN</option>
									<option value="grf" <%if(session.getAttribute("DATAISSUECATEGORY") !=null && session.getAttribute("DATAISSUECATEGORY").toString().equalsIgnoreCase("grf")){ %> selected="selected"<%} %>>GRF</option>
									<option value="gip" <%if(session.getAttribute("DATAISSUECATEGORY") !=null && session.getAttribute("DATAISSUECATEGORY").toString().equalsIgnoreCase("gip")){ %> selected="selected"<%} %>>GIP</option>
									<option value="upt" <%if(session.getAttribute("DATAISSUECATEGORY") !=null && session.getAttribute("DATAISSUECATEGORY").toString().equalsIgnoreCase("upt")){ %> selected="selected"<%} %>>UPT</option>
									<option value="eup" <%if(session.getAttribute("DATAISSUECATEGORY") !=null && session.getAttribute("DATAISSUECATEGORY").toString().equalsIgnoreCase("eup")){ %> selected="selected"<%} %>>EUP</option>
									<option value="other" <%if(session.getAttribute("DATAISSUECATEGORY") !=null && session.getAttribute("DATAISSUECATEGORY").toString().equalsIgnoreCase("other")){ %> selected="selected"<%} %>>OTHER</option>
								</select>
							<%-- <input type="text" name = "category" value="${item.CATEGORY}" id="info"/><img class="editimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/edit.png" title=""></img> --%>
							</td>
						</tr>
					</c:if>
				
					<!-- type -->
					<c:if test="${item.TYPE ne null}">
						<tr>
							<td id="category">Type: </td>
							<td><input type="text" name="type" value="${item.TYPE}" id="info"/><img class="editimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/edit.png" title=""></img>
							</td>
						</tr>
					</c:if>
				
					<!-- Priority -->
					<c:if test="${item.PRIORITY ne null}">
						<tr>
							<td id="category">Priority: </td>
							<td><input type="text" name="priority" value="${item.PRIORITY}" id="info"/><img class="editimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/edit.png" title=""></img>
							</td>
						</tr>
					</c:if>
					
					<!-- Lables -->
					<c:if test="${item.LABELS ne null}">
						<tr>
							<td id="category">Labels: </td>
							<td><input type="text" name="lables" value="${item.LABELS}" id="info"/><img class="editimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/edit.png" title=""></img>
							</td>
						</tr>
					</c:if>
					
					<!-- Epic Name -->
					<c:if test="${item.EPIC_NAME ne null}">
						<tr>
							<td id="category">Epic Name: </td>
							<td><input type="text" name="epic_name" value="${item.EPIC_NAME}" id="info"/><img class="editimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/edit.png" title=""></img>
							</td>
						</tr>
					</c:if>
					
					<!-- Sprint -->
					<c:if test="${item.SPRINT ne null}">
						<tr>
							<td id="category">Sprint: </td>
							<td><input type="text" name="sprint" value="${item.SPRINT}" id="info"/><img class="editimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/edit.png" title=""></img>
							</td>
						</tr>
					</c:if>
					
					<!-- Status -->
					<c:if test="${item.STATUS ne null}">
						<tr>
							<td id="category">Status: </td>
							<td><input type="text" name="status" value="${item.STATUS}" id="info"/><img class="editimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/edit.png" title=""></img>
							</td>
						</tr>
					</c:if>
					
					<!-- Resolution -->
					<c:if test="${item.RESOLUTION ne null}">
						<tr>
							<td id="category">Resolution: </td>
							<td><input type="text" name="resolution" value="${item.RESOLUTION}" id="info"/><img class="editimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/edit.png" title=""></img>
							</td>
						</tr>
					</c:if>
					
					<!-- Assignee -->
					<c:if test="${item.ASSIGNEE ne null}">
						<tr>
							<td id="category">Assignee: </td>
							<td><input type="text" name="assignee" value="${item.ASSIGNEE}" id="info"/><img class="editimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/edit.png" title=""></img>
							</td>
						</tr>
					</c:if>
					
					<!-- Reporter -->
					<c:if test="${item.REPORTER ne null}">
						<tr>
							<td id="category">Reporter: </td>
							<td><input type="text" name="reporter" value="${item.REPORTER}" id="info"/><img class="editimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/edit.png" title=""></img>
							</td>
						</tr>
					</c:if>
					
					<!-- Creation Date -->
					<c:if test="${item.CREATION_DATE ne null}">
						<tr>
							<td id="category">Created: </td>
							<td>${item.CREATION_DATE}</td>
							
						</tr>
					</c:if>
					
					<!-- Modification Date -->
					<c:if test="${item.UPDATE_DATE ne null}">
						<tr>
							<td id="category">Updated: </td>
							<td>${item.UPDATE_DATE}</td>
							
						</tr>
					</c:if>
					
					<!-- Description -->
					<c:if test="${item.DESCRIPTION ne null}">
						<tr>
							<td id="category">Description: </td>
							<td><textarea rows="10" cols="80" name="description">${item.DESCRIPTION}
							</textarea></td>
							
						</tr>
					</c:if>
					
					<!-- Comment -->
						<tr>
							<td id="category">Comment: </td>
							<td><textarea rows="10" cols="80" name="comment">${item.ADDED_COMMENT}
							</textarea></td>
							
						</tr>
					
					
				
				</c:forEach>
				
				<tr>
				<td ><input name="Update" type="submit" value="Update" /></td>
				</tr>
			</tbody>
		</table>
	</form>	
		
		</br>
		</br>		
  		 <%} %>
	</div>
	




</div>



<div id="footer">
<p id="copyright">Copyright &copy; &nbsp;<%= new java.util.Date().getYear() + 1900 %>&nbsp;<a
	title="Elsevier home page (opens in a new window)" target="_blank"
	href="http://www.elsevier.com">Elsevier B.V.</a> All rights reserved.
</p>
</div>


	</div>

<!-- set category default selected category is the one that the issue belongs to  -->
<script type="text/javascript"> 
<% if(session.getAttribute("DATAISSUECATEGORY") !=null)
{%>
	// it is not working, WHY???
	var e = document.getElementById("info").selectedIndex = session.getAttribute("DATAISSUECATEGORY");
   
<%}
 else{%>
document.getElementById("info").selectedValue="cpx";
<%}%> 

</script>

</body>
</html>