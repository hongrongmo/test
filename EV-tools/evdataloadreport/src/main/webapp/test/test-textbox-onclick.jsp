<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

<input type="hidden" value="0" id="theValue" /> 

<p><input type='button' onclick='addInputBox()' value='Add'/></p> 

<div id="myDiv"> </div> 
<script> 
function addInputBox() { 

var ni = document.getElementById('myDiv'); 

var numi = document.getElementById('theValue'); 

var num = (document.getElementById('theValue').value -1)+ 2; 

numi.value = num; 

var newdiv = document.createElement('div'); 

var divIdName = 'my'+num+'Div'; 

newdiv.setAttribute('id',divIdName); 

newdiv.innerHTML = "<input type=\"text\" /> <input type=\"button\" onclick=\"removeInputBox(\'"+divIdName+"\')\" value='Remove'/>"+divIdName; 

ni.appendChild(newdiv); 

} 

function removeInputBox(divNum) { 


var d = document.getElementById('myDiv'); 

var olddiv = document.getElementById(divNum); 

d.removeChild(olddiv); 

} 
</script>


</body>
</html>