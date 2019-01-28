<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Vertical TextBox</title>

<style type="text/css">
div.wrap{
    position: relative;
    display: block;
    text-align: center;
}
div.left{
border-right: 1px solid #189028;
    border-top: 1px solid #189028;
    border-left: 1px solid #189028;
    padding-right: 4px;
    padding-top: 2px;
    padding-left: 4px;
    background-color: #bbbbbb;
    overflow: hidden;
    position: relative;    
    float: left;
    -moz-transform: rotate(270deg);
    -moz-rotation-point: 0 0;
    -webkit-transform: rotate(270deg);
    -webkit-rotation-point: 0 0;
    -o-transform: rotate(270deg);
    -ms-transform: rotate(270deg);
    height: auto;
    top: 30px;
    width: auto;
}
h1{
font-weight:bold;
font-size:large;
    vertical-align: middle;
}


</style>
</head>
<body>
<div class="wrap">
    <div class="left">Flik 1</div>
    <div class="left">Flik 2</div>
    <div class="left">Flik 3</div>
    <div class="left">Flik 4</div>
    <div class="left"><input type="button" name="test" value="aignmnet"></div>
</div>

</body>
</html>