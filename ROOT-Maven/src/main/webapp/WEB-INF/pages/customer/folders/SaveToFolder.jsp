<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
<%@ taglib uri="/WEB-INF/tlds/functions.tld" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


	
<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - My Folders">

	<stripes:layout-component name="csshead"> 
	<link href="/static/css/ev_folder.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	</stripes:layout-component>

	<stripes:layout-component name="contents">
	<div id="folders" class="paddingL15">
		<h1>View/Update Folders</h1>
   </div>
  
  <c:set var="backeventurl" scope="request">
		<c:choose>
			<c:when test="${fn:contains(actionBean.backurl,'citationSelectedSet')}">/selected/citation.url?</c:when>
			<c:when test="${fn:contains(actionBean.backurl,'quickSearchAbstractFormat')}">/search/doc/abstract.url?pageType=quickSearch&searchtype=Quick&</c:when>
			<c:when test="${fn:contains(actionBean.backurl,'quickSearchDetailedFormat')}">/search/doc/detailed.url?pageType=quickSearch&searchtype=Quick&</c:when>
			<c:when test="${fn:contains(actionBean.backurl,'expertSearchAbstractFormat')}">/search/doc/abstract.url?pageType=expertSearch&searchtype=Expert&</c:when>
			<c:when test="${fn:contains(actionBean.backurl,'expertSearchDetailedFormat')}">/search/doc/detailed.url?pageType=expertSearch&searchtype=Expert&</c:when>
			<c:when test="${fn:contains(actionBean.backurl,'pageDetailedFormat')}">/search/book/detailed.url?pageType=page&</c:when>
			<c:when test="${fn:contains(actionBean.backurl,'tagSearchAbstractFormat')}">/search/doc/abstract.url?pageType=tagSearch&searchtype=TagSearch&</c:when>
			<c:when test="${fn:contains(actionBean.backurl,'tagSearchDetailedFormat')}">/search/doc/detailed.url?pageType=tagSearch&searchtype=TagSearch&</c:when>
			<c:otherwise>/search/results/quick.url?</c:otherwise>
		</c:choose>
	</c:set>
  
  <c:if test="${actionBean.documentCount > 0}">
   <div id="folders" class="paddingL15">
		<hr class="divider">
   </div>	
  <div style="border: 2px solid #ababab;min-width: 500px;margin: 10px 0 10px 20px;width:70%">
    <FORM name="savetofolder" action="/personal/folders/save/add.url" METHOD="POST" onSubmit="return checkfolderforform('savetofolder','${actionBean.foldercount}')" >
      <input type="hidden" name="backurl" value="${actionBean.backurl}">
      <input type="hidden" name="maxfoldersize" value="${actionBean.maxFolderSize}">
      <input type="hidden" name="CID" value="${actionBean.hiddenCID}">
      <input type="hidden" name="database" value="${actionBean.database}">
      <input type="hidden" name="documentcount" value="${actionBean.documentCount}">
      <input type="hidden" name="docid" value="${actionBean.docid}">
      <input type="hidden" name="searchresults" value="${actionBean.searchresults}">
      <input type="hidden" name="newsearch" value="${actionBean.newsearch}">
      <input type="hidden" name="DOCINDEX" value="${actionBean.docindex}">
      <input type="hidden" name="format" value="${actionBean.format}">
      

        <!-- Outer Template Table / Grey Border / DO NOT CHANGE -->
        <table border="0" width="100%" cellspacing="0" cellpadding="0" id="savefolder">
          <!-- Spacer Row / Provides space below Navigation Bar / DO NOT CHANGE -->
          <tr>
            <!-- Content Cel / White (default) Background / DO NOT CHANGE -->
            <td valign="top">
              <!-- Inner Template Table / DO NOT CHANGE -->
              <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <!-- Title Row / Indented with Spacer / Bold White Text on Grey Background -->
                <!-- Spacer Row / White Background / DO NOT CHANGE -->
                <tr><td valign="top" height="5" colspan="4"><img src="/static/images/s.gif" border="0" height="5" /></td></tr>
                <!-- Content Row / Indented with Spacer / Content cell/should can contain table 
                  to acheive specific content layout within template -->
                <tr>
                  <!-- Indentation Cel / DO NOT CHANGE -->
                  <td width="10"><img src="/static/images/s.gif" width="10" /></td>
                  <!-- Content Goes Here -->
                  <td colspan="3">
                    <!-- Content Goes Here -->

    <table border="0" cellspacing="0" cellpadding="0" width="100%">
      
      
        <!-- Table below the logo area ,checks if the number of folders for this user == 0 -->
        <c:choose>
            <c:when test="${actionBean.foldercount == 0}">
                <tr>
                  <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
                        <td valign="top" colspan="2">
                        With your Personal Account, you can create up to three folders in which to save selected records.<br/> Each folder can contain up to 50 records.To create a new folder, please enter a folder name:
                        </td>
                        <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
                </tr>
            <tr><td valign="top" height="4" colspan="4"><img src="/static/images/s.gif" border="0" height="8"/></td></tr>
            <tr>
              <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
                        <td valign="top" colspan="2">
                        <input type="text" name="foldername" size="28" maxlength="32"/>
                        <input type="submit"  name="submit" value="Save" title="Save folder"/>
                <input type="submit" name="cancel" value="Cancel" title="Cancel action" onclick="javascript:document.location='${backeventurl}${actionBean.backurl}&DOCINDEX=${actionBean.docindex}&format=${actionBean.format}';return false;"> &#160; &#160;
                        </td>
                        <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
                </tr>
            </c:when>
            
            <%-- checks if the no of folders for this user is less than 3 and greater than zero  --%>
            <c:when test="${(actionBean.foldercount > 0)}">
            <tr>
              <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
              <td valign="top" colspan="2">
              With your Personal Account, you can create up to three folders in which to save selected records.<br/> Each folder can contain up to 50 records.Choose an existing folder or create a new folder.
              </td>
              <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
            </tr>
            <tr><td valign="top" height="2" colspan="4"><img src="/static/images/s.gif" border="0" height="2"/></td></tr>
            <tr>
              <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
              <td align="left" >My existing folders:
                <input type="radio" name="choose" checked="checked"/>&#160;
                <a CLASS="SmBlackText">
                <select name="folderid" onfocus="selectradiodrop('${actionBean.foldercount}')">
                 <c:forEach items="${actionBean.folderlist}" var="folder">
                    <option value="${folder.folderID},${folder.folderSize}">
                      ${folder.folderName}
                    </option>
                  </c:forEach>
                </select>
                </a>
              </td>
              <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
            </tr>
            <c:if test="${actionBean.foldercount <3}">
	            <tr><td valign="top" height="2" colspan="4"><img src="/static/images/s.gif" border="0" height="2"/></td></tr>
	            <tr>
	              <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
	              <td align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Create a folder:
	                <input type="radio" name="choose">&#160;
	                <input type="text" name="foldername" size="28"  maxlength="32" onfocus="selectradiotext('${actionBean.foldercount}')">
	              </td>
	              <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
	            </tr>
            </c:if>
            </c:when>
            <c:otherwise>
            </c:otherwise>
        </c:choose>
            
            
            <c:if test="${not (actionBean.foldercount == 0)}">
            <tr><td valign="top" height="1" colspan="4"><img src="/static/images/s.gif" border="0" height="1"/></td></tr>
             <tr>
              <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>   
                <td valign="top">
                <input type="submit"  name="submit" value="Save" title="Save folder"/>
                <input type="submit" name="cancel" value="Cancel" title="Cancel action" onclick="javascript:document.location='${backeventurl}${actionBean.backurl}';return false;"> &#160; &#160;
                </td>
                <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
            </tr>
            
            </c:if>
            <tr><td valign="top" height="7" colspan="4"><img src="/static/images/s.gif" border="0" height="7"/></td></tr>
          
    </table>
                    <!-- Content Goes Here -->
                  </td>
                </tr>
                <!-- Sopacer Row Below Content / DO NOT CHANGE -->
                
              </table>
            </td>
            <!-- Spacer Cel / Grey Border / DO NOT CHANGE -->
          
          </tr>
          <!-- Spacer Row / Grey Border / DO NOT CHANGE -->
          
        </table>
      

    </FORM>
  </div>
  </c:if> <!-- $DOCUMENT-COUNT &gt; 0 -->

  

  <c:if test="${actionBean.documentCount == 0}">
     <table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
        <tr><td valign="top" height="0" colspan="3"><img src="/static/images/s.gif" border="0"/></td></tr>
        <tr><td valign="top" width="20"><img src="/static/images/s.gif" border="0" width="20"/></td><td valign="top" colspan="2">You did not select any records to save to the folder.</td></tr>
        <tr><td valign="top" height="2" colspan="3"><img src="/static/images/s.gif" border="0"/></td></tr>
     </table>
    <div id="folders" class="paddingL15" style="margin: 0 0 15px 0;">		
		<hr class="divider">
   </div> 		  
     <table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
        <tr><td valign="top" width="20"><img src="/static/images/s.gif" border="0" width="20"/></td><td valign="top">
          <c:choose><c:when test="${not empty actionBean.searchresults and not empty actionBean.newsearch}">
      		<a  href="${backeventurl}${actionBean.backurl}&searchresults=${f:encode(actionBean.searchresults)}&newsearch=${f:encode(actionBean.newsearch)}">Return to previous page</a>
      	  </c:when><c:otherwise>
      		<a  href="${backeventurl}${actionBean.backurl}&DOCINDEX=${actionBean.docindex}&format=${actionBean.format}">Return to previous page</a>
      		</c:otherwise></c:choose>
        </td><td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td></tr>
     </table>
  
    <br/>
  </c:if>

	</stripes:layout-component>
	
	<stripes:layout-component name="jsbottom_custom">
	    <SCRIPT type="text/javascript" SRC="/static/js/SaveToFolder.js?v=${releaseversion}"></SCRIPT>
	</stripes:layout-component>
	
</stripes:layout-render>
