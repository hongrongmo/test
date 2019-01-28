<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Test Show Hid</title>
</head>
<body>


<div id="show">

<input type="text" name="uname"/>
<input type="submit" name="submit" value="dosomething" onclick="dosomething()"/>
</div>



<input type="submit" name="submit" value="hide" onclick="hide()"/>

<input type="submit" name="submit" value="show" onclick="show()"/>


<!-- test this part from sessions.jsp -->

</br>
</br>

<a href="#" onclick='addInputBox()'>User Session</a>
<a href="#" onclick='addInputBox2()'>User Session By UserName and OS User</a>
			
			
<div id="sessionByOracleUser" style="margin-top: 6px;"> </div> 
<div id="sessionByOracleAndOSUser" style="margin-top: 6px;"> </div> 


<div id="allsessioncount">

<table>
  <caption>All Sessions Count By Users</caption>
  
   
  <tbody>
  
  	<tr>
  		<td>Value1</td>
  		<td>Value2</td>
  	</tr>
  		
  </tbody>
 </table>
   
  </div>

 <!-- END -->
 </br>
</br>
 
 
 <div id="allsessions" style="display: block;"> 
  <table >
  <caption>All Sessions</caption>
  
   
  		<tbody>
        	<tr>
				<td>Value3</td>
				<td>Value4</td>
			</tr>
		</tbody>
	</table>
</div>
				
</br>
</br>

<div id="sessionbyoouser">	
		 <table >
 		 <caption style="width:600px;">Session Info for UserName: <%= session.getAttribute("SESSIONNAME") %> and OsUser: <%= session.getAttribute("OSUSER") %></caption>
  
  	 
 	 <tbody>
  			<tr>
   				<td>Value5</td>
				<td>Value6</td>
       		</tr>
       </tbody>
     </table>
 </div>
       
       				

</br>
</br>


<div id="openedcursor">	
		 <table>
 		 <caption style="width:400px;">Opened Cursors</caption>
  
  	<tbody>
       		<tr>
       			<td>Value7</td>
				<td>Value8</td>
			</tr>
		</tbody>
	</table>
</div>		
										
	

<script type="text/javascript">

function hide() {

	document.getElementById("show").style.display='none';
	document.getElementById("temp").innerHTML = "";
}


function show() {
	
	document.getElementById("show").style.display='block';
	
	
	
		
}


function dosomething()
{

	var ni = document.getElementById('show'); 


	var newdiv = document.createElement('div'); 

	var divIdName = 'Enter User Session'; 

	newdiv.setAttribute('id',divIdName); 

	newdiv.innerHTML = "<form name=\"ousersession\" class=\"getsessionbyuser\" action=\"${pageContext.servletContext.contextPath}/Session?op=oousersession\" method=\"post\">"+
						"<b>Enter Oracle UserName</b><input type=\"text\" name=\"oracleUserName\"/> "+
						"<b>Enter OS UserName</b><input type=\"text\" name=\"osUserName\"/> "+
						"<input name=\"submit\" type=\"submit\" value=\"View User Session\" /></form>";


	ni.appendChild(newdiv); 
	
	
	}


// FROM sessions.jsp

function addInputBox() { 

	var ni = document.getElementById('sessionByOracleUser'); 


	var newdiv = document.createElement('div'); 

	var divIdName = 'sessionbyuser'; 

	newdiv.setAttribute('id',divIdName); 

	newdiv.innerHTML = "<form name=\"usersession\" class=\"getsessionbyuser\" action=\"${pageContext.servletContext.contextPath}/Session?op=usersession\" method=\"post\">"+
						"<b>Enter the UserName</b><input type=\"text\" name=\"oracleUserName\"/> "+
						"<input name=\"submit\" type=\"submit\" value=\"View User Session\" /></form>";


	ni.appendChild(newdiv); 

	// set other session's INFO invisible
	 
	document.getElementById("allsessioncount").style.display='none';
	document.getElementById("allsessions").style.display='none';
	document.getElementById("sessionbyoouser").style.display='none';


	} 


	// Get Session using Oracle User Name and OS User
	function addInputBox2() { 

		var ni = document.getElementById('sessionByOracleAndOSUser'); 


		var newdiv = document.createElement('div'); 

		var divIdName = 'Enter User Session'; 

		newdiv.setAttribute('id',divIdName); 

		newdiv.innerHTML = "<form name=\"ousersession\" class=\"getsessionbyuser\" action=\"${pageContext.servletContext.contextPath}/Session?op=oousersession\" method=\"post\">"+
							"<b>Enter Oracle UserName</b><input type=\"text\" name=\"oracleUserName\"/> "+
							"<b>Enter OS UserName</b><input type=\"text\" name=\"osUserName\"/> "+
							"<input name=\"submit\" type=\"submit\" value=\"View User Session\" /></form>";


		ni.appendChild(newdiv); 

		// set other session's INFO invisible
		
		document.getElementById("allsessioncount").style.display='none';
		document.getElementById("allsessions").style.display='none';
		document.getElementById("sessionbyuser").style.display='none';
		
		} 
		
	

</script>
</body>
</html>