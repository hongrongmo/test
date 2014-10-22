<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
    
<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - TICURL">

    <stripes:layout-component name="csshead">
    <jsp:include page="include/customcss.jsp"></jsp:include>
    <style type="text/css">
        span.label {float: left;width: 150px;text-align: right;padding-right: 10px;padding-top: 5px;}
        div.row {clear: both;padding-top: 5px;}
        a#answerALink {float:left; padding-top: 5px}
    </style>
    </stripes:layout-component>
    
    <stripes:layout-component name="header">
        <jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
    </stripes:layout-component>

    <stripes:layout-component name="ssourls"/>

<%-- **************************************** --%>  
<%-- CONTENTS                                 --%>  
<%-- **************************************** --%>  
    <stripes:layout-component name="contents">
    <div id="container">
    
    <jsp:include page="include/tabs.jsp"/>
    
    <div class="marginL10 padding5" >

        
        <h2>TICURL generator</h2>

		<p>Usage: This tool will generate the query parameters to be used with EV to do a TICURL authentication.  It requires the following:</p>
		<ul>
		  <li>A Ticketed URL Origin ID configured in Customer System application.  Can be found on the main CS app home screen under "Gateways", then "Ticketed URL Origin IDs". </li>
		  <li>The "Target" should be a bookmarkable EV URL *without* the protocol or server information.  E.g. - /home.url or /search/quick.url</li>  
		  <li>Click on "Update" to get timestamp.  TICURL feature only lasts for a short while so this timestamp may need to be updated frequently if you are testing</li>
		  <li>Click on "GO" to generate URL parameters (Ticketed URL textarea) and a clickable URL.</li>
		</ul>
		
		<div class="contentMain" style="width:95%;">
		<div class="contentShadow">
            <br/>
			<div class="row"><span class="label">Target:</span><input id="target" type="text" size="60" value="/home.url"><input id="btnEncode" type="button" value="Encode please"></div>
			<div class="row"><span class="label">Origin:</span><input id="origin" type="text" size="60" value="ev"></div>
			<div class="row"><span class="label">Origin User:</span><input id="originUser" type="text" size="60" value=""></div>
			<div class="row"><span class="label">Time:</span><input id="time" type="text" size="60" value=""><input id="btnTime" type="button" value="Update"></div>
			<div class="row">
			  <span class="label">Salt/Version:</span>
			  <input id="salt" type="text" size="60" value="nttzzKMPL6-ANLY96(!DjtN-ofUIp-h;">
			  <input id="version" type="text" size="2" value="1">
			</div>
			<br>
			<div class="row">
			 <span class="label">Ticketed URL:</span>
			 <textarea id="answerA" cols="80" rows="3"></textarea>
			</div>
			
			<div class="row">
			 <span class="label">Clickable URL:</span>
			 <a id="answerALink" href=""></a>
			</div>
			
			<br/><br/>
			
			<input style="margin:0px 0px 0px 150px;width:250px" id="btnGo" type="button" value="Go">

            <div class="clear"></div>

            <br/>
		</div>
		</div>
		
    </div>    

</stripes:layout-component>
    
<stripes:layout-component name="jsbottom_custom">
    <script type="text/javascript" src="https://crypto-js.googlecode.com/files/2.0.0-crypto-md5.js"></script>
	<script type="text/javascript">
	var CARS_AUTH_URL = "/ticurl?";
	$(document).ready(function() {
		
	    $("#btnEncode").click( function(evt) {
	      var t = $("#target");
	      t.attr("value", escape(t.attr("value")).replace(/\//g, "%2F"));
	    });
	    
	    $("#btnTime").click( function(evt) {
	      var d = new Date();
	      var y = d.getUTCFullYear();
	      var m = (d.getUTCMonth()+1);
	      var day = d.getUTCDate();
	      var h   = d.getUTCHours();
	      var min = d.getUTCMinutes();
	      var s   = d.getUTCSeconds();
	      if(m<10) m = "0" + m;
	      if(day<10) day = "0" + day;
	      if(h<10) h = "0" + h;
	      if(min<10) min = "0" + min;
	      if(s<10) s = "0" + s;
	      var dStr = y+''+m+''+day+''+h+''+min+''+s;
	      $("#time").attr("value", dStr);
	    });
	    
	    $("#btnGo").click( function(evt) {
	      var val = "_ob=TicketedURL" +
	                "&_origin=" + $("#origin").val() +
	                "&_originUser=" + $("#originUser").val() +
	                "&_target=" + $("#target").val() +
	                "&_ts=" + $("#time").val() +
	                "&_version=" + $("#version").val();
	
	      var answerA = $("#answerA");
	      var saltA = $("#salt").val();
	      var md5A = Crypto.MD5(val+saltA);
	      var urlA = "" + val + "&md5=" + md5A;
	      $("#answerA").val(urlA);
	
	      urlA = CARS_AUTH_URL + urlA;
	      $("#answerALink").attr("href",urlA);
	      $("#answerALink").html(urlA);
	
	    });
	});
	
	</script>
</stripes:layout-component>
<stripes:layout-component name="sessionexpiryhandler"></stripes:layout-component>
</stripes:layout-render>