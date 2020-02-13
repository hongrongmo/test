<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@page import="java.util.ArrayList" %>
     <%@page import="java.util.HashMap" %>
     <%@page import="org.aws.DescribeEc2" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

<script type='text/javascript'>//<![CDATA[

var stateObject = {
    "California": {
        "Monterey": ["Salinas", "Gonzales"],
        "Alameda": ["Oakland", "Berkeley"]
    },
    "Oregon": {
        "Douglas": ["Roseburg", "Winston"],
        "Jackson": ["Medford", "Jacksonville"]
    }
}
window.onload = function () {
    var stateSel = document.getElementById("stateSel"),
        countySel = document.getElementById("countySel"),
        citySel = document.getElementById("citySel");
    for (var state in stateObject) {
        stateSel.options[stateSel.options.length] = new Option(state, state);
    }
    stateSel.onchange = function () {
        countySel.length = 1; // remove all options bar first
        citySel.length = 1; // remove all options bar first
        if (this.selectedIndex < 1) return; // done   
        for (var county in stateObject[this.value]) {
            countySel.options[countySel.options.length] = new Option(county, county);
        }
    }
    stateSel.onchange(); // reset in case page is reloaded
    countySel.onchange = function () {
        citySel.length = 1; // remove all options bar first
        if (this.selectedIndex < 1) return; // done   
        var cities = stateObject[stateSel.value][this.value];
        for (var i = 0; i < cities.length; i++) {
            citySel.options[citySel.options.length] = new Option(cities[i], cities[i]);
        }
    }
}
//]]> 

</script>

</head>

<body>
  <form name="myform" id="myForm">
    <select name="optone" id="stateSel" size="1">
        <option value="" selected="selected">Select state</option>
    </select>
    <br>
    <br>
    <select name="opttwo" id="countySel" size="1">
        <option value="" selected="selected">Please select state first</option>
    </select>
    <br>
    <br>
    <select name="optthree" id="citySel" size="1">
        <option value="" selected="selected">Please select county first</option>
    </select>
</form>
  
</body>

</html>
