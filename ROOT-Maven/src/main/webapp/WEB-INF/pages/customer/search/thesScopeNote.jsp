<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
<%@ taglib uri="/WEB-INF/tlds/functions.tld" prefix="f" %>
    
<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Scope Notes">

<stripes:layout-component name="csshead">
		<style type="text/css">
		  body {font-family:arial,verdana,geneva; margin: 5; padding: 5; background-color: #CEEBFF;}
		</style>
</stripes:layout-component>

	<stripes:layout-component name="header"/> 

<%-- **************************************** --%>  
<%-- CONTENTS                                 --%>  
<%-- **************************************** --%>  
<stripes:layout-component name="contents">
    <div id="contents" style="padding: 0 5px">
    
    <p style="float:right; margin: 5px"><a href="javascript:window.close();"><img src="/static/images/close.gif" border="0"/></a></p>
    <div class="clear"></div>
    
    <p class="DBlueText"><b>${actionBean.scopenote.mainTerm}</b></p>
    
<c:if test="${not empty actionBean.scopenote.scopeNotes}">
    <br/>
    <p><b>Scope notes: </b>${f:decode(actionBean.scopenote.scopeNotes)}</p>
</c:if>
    
<c:if test="${not empty actionBean.scopenote.dateOfIntro}">
    <br/>
    <p><b>Introduced: </b>${actionBean.scopenote.dateOfIntro}</p>
</c:if>

<c:if test="${not empty actionBean.scopenote.historyScopeNotes}">
    <br/>
    <p><b>History: </b>${f:decode(actionBean.scopenote.historyScopeNotes)}</p>
</c:if>
    
<c:if test="${not empty actionBean.scopenote.searchInfo}">
    <br/>
    <p><b>Search Info: </b>${f:decode(actionBean.scopenote.searchInfo)}</p>
</c:if>

<c:if test="${not empty actionBean.scopenote.classifications}">

    <br/>
    <p>
        <b>Related classification codes: </b>
<c:forEach var="classification" items="${actionBean.scopenote.classifications}" varStatus="status">
    <c:choose>
        <c:when test="${classification.classificationID.optional}">(${classification.classificationID.classCode}): ${classification.classTitle}<c:if test="${not status.last}">; </c:if></c:when>
        <c:otherwise>${classification.classificationID.classCode}: ${classification.classTitle}<c:if test="${not status.last}">; </c:if></c:otherwise>
    </c:choose>
</c:forEach>
    </p>
</c:if>

<c:if test="${not empty actionBean.scopenote.type}">
    <br/>
    <p><b>Type of term: </b>${f:decode(actionBean.scopenote.type)}</p>
</c:if>

<c:if test="${not empty actionBean.scopenote.coordinates}">
    <br/>
    <p><b>Coordinates: </b>${actionBean.scopenote.coordinates}</p>
    <c:if test="${actionBean.gmapDisplayable}">
    <br/>
    <img id="mapStaticImage" src="http://maps.googleapis.com/maps/api/staticmap?key=AIzaSyCfQ_HSbcET25jn-cuT2Lz0CVycFnoGgZQ&sensor=false&center=&zoom=5&size=250x250&maptype=roadmap&path=${actionBean.coordsAsGmapPath}"/>
    </c:if>
</c:if>

    <br/>
    <br/>
    <br/>
    
    </div>
</stripes:layout-component>

<stripes:layout-component name="jsbottom_custom">
  <script type="text/javascript">
  //<![CDATA[
	$(document).ready(function() {
	  var strdomain = document.domain;
	  var apikeys = { 
			  "www.engineeringvillage.com":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhTq1aVOHKz326Ly74ktyh0JEC50cxRcwqEG9_IZn1rf-kXlYHD0OAjeKw", 
			  "cert.engineeringvillage.com":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhTq1aVOHKz326Ly74ktyh0JEC50cxRcwqEG9_IZn1rf-kXlYHD0OAjeKw", 
			  "www.engineeringvillage2.com":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhQycePdRUCs6MMAMRXc5pXGU3hn3hRfr_zEZfLKIyUShHi94g2KWSfGEw",
			  "www.engineeringvillage.org":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhSAdZp0PxlTEhTFv3KMS9ULbNb5zBRqzELdPImli2KTwR6NaFokcBv4bA",
			  "www.engineeringvillage2.org":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhSQVDkHczPvGQBBH-1zyyQlxvW27BTYxtGf1JR2Az5uJMv5BiYUcHc0fg",
			  "www.chemvillage.org":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhRc2oirmkLapUOdAKcS-7VrLe4NmRRygHzNcODvNaPwfo8u7kjzm8R3Pg",
			  "www.papervillage2.org":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhRoQ9NpbmNyNRg_ZVHqDXsW5KVm9BQAIDpSj4LWpPYl79Ju3n8FsZYtiA",
			  "www.paperchem.com":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhSwBt7CUughPYSQRGOq0g9Cnw2rgRS09l1TiHYrU0IFHRsrrd0Ioscj6g",
			  "www.papervillage.org":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhR4KYQ73p3WzPF7syNfyrFgFZtwaxR5sJ7ySrsRekJV7hIExrrtRqD08g",
			  "www.papervillage2.com":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhS3XaAm7oOu6A7GrMVbYRe2u9FCpRSfk-An4_9sm-2YoETe1SnzwtWffg",
			  "www.apiencompass.org":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhRHcUpwnapUojBcsWU6PxPlD9oAeBR6FefYn5tiecY3qDiH2LruH550xw",
			  "www.eiencompass.com":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhR1px2Xrv_o7VDsm6kNUoHpHa-UvhQXJf3NTv0tT22yWiSZ3EogdInG7Q",
			  "www.encompassvillage.com":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhQyD6pGsbDZ_IMKl1sG3ErY3WRqNRTxQeViRtl5W6vKzuRQpp1iqSg8Ig",
			  "www.encompassvillage.org":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhQG36b3Ti2jJLY9hFCzqh5YnzGkahRkPw6Cz4ehvpyGlSKiP6_Kp_z9Ug"};
	  var domainkey = apikeys[strdomain];
	  var staticImage = null;
	  staticImage = document.getElementById("mapStaticImage");
	  if(staticImage != null)  {
	    var staticImageURL = staticImage.getAttribute("src") + domainkey;
	    staticImage.setAttribute("src",staticImageURL);
	  }
	}
  //]]>
  </script>
</stripes:layout-component>

<stripes:layout-component name="footer"/> 

</stripes:layout-render>