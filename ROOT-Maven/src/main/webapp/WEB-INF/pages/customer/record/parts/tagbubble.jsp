<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>	
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tlds/functions.tld" prefix="f" %>
	<c:choose>
			<c:when test="${actionBean.view eq 'page'}">
				<c:set var="tagwidth" value="width:90%;min-width:215px" />
			</c:when>
			<c:when test="${actionBean.view eq 'book'}">
				<c:set var="tagwidth" value="width:306px" />
			</c:when>
			<c:otherwise>
				<c:set var="tagwidth" />
			</c:otherwise>
		</c:choose>
      <div id="tagsgroups" class="toolbox shadowbox" style="display:non;${tagwidth}">
      
	      <form style="margin:0px; padding:0px;" name="tagbubble" id="tagbubbleform" action="/tagsgroups/doc.url" METHOD="POST" >
          <input type = "hidden"  name="addtag"  value ="t" />
          <input type = "hidden"  name="backurl"  value ="${actionBean.backurl}" />
          <input type = "hidden"  name="tagdoc" value ="${actionBean.tagbubble.docid}" />
          <input type = "hidden"  name="database" value ="${actionBean.database}" />
      
			<p class="title"><b>Add a tag</b>&nbsp;&nbsp;<a href="${actionBean.helpUrl}#Tagging_a_record.htm" class="helpurl" title="Learn more about adding a tag to this record"><img
						src="/static/images/i.png" border="0" class="infoimg" align="top"  alt="Learn more about adding a tag to this record"/></a></p>

			<p class="taglocation">
				<label class="hidden" for="scope">Add a tag scope</label>
                <select style="vertical-align: middle; width: 150px" name="scope" id="scope">
                  <option value="1">Public</option>
                  <option value="2">Private</option>
                  <option value="4">My Institution</option>
<c:choose>
<c:when test="${actionBean.context.userLoggedIn}">
	<c:if test="${not empty actionBean.tagbubble.taggroups}"><c:forEach var="group" items="${actionBean.tagbubble.taggroups}"><option class='group' value='3:${group.groupID}'>${group.title}</option></c:forEach></c:if>
</c:when>
<c:otherwise>
			 	<option value="6">Login for groups</option>
</c:otherwise>
</c:choose>
                </select>
			</p>

			<p id="gpopup" style="display:none">
				<label class="hidden" for="notifygroup">Notify Group Checkbox</label>
				<input style="margin:0; padding:0; position:relative; top: 1px" type="checkbox" name="notifygroup" id="notifygroup" style="">
				<label name="notifylabel" for="notifygroup" style="">Email Group</label>
			</p>
	
			<p>
                <label class="hidden" for="searchgrouptags">Add a tag text box</label><input style="vertical-align: middle; width: 150px" name="searchgrouptags" id="searchgrouptags"/>
                <input style="vertical-align: middle; font-weight: bold" alt="Add Tag" type="submit" value="Add" name="addtag"/>
                <!-- AJAX tag 'suggestion' dropdown -->
				<div id="autocompletewrap" style="width:142px; position:absolute"></div>
			</p>
		  </form>
		  
			<%-- Check for existing tags on this record --%>
		  <div id="tagdisplay"<c:if test="${empty actionBean.tagbubble.tags}"> style="display:none"</c:if>>
				<div class="hr" style="color: #0156AA; background-color: #0156AA; height: 2px; margin-left: 7px;width:95%"><hr/></div>
				<p style="margin: 7px"><b>My tags</b></br></p>
				<p style="margin: 7px" id="tagdisplaylinks"><c:forEach var="tag" items="${actionBean.tagbubble.tags}" varStatus="status">
				<c:choose><c:when test="${not empty tag.groupID}"><c:set var="scope">${tag.scope}:${tag.groupID}</c:set></c:when><c:otherwise><c:set var="scope">${tag.scope}</c:set></c:otherwise></c:choose>
						<a
							href="/search/results/tags.url?CID=tagSearch&searchtype=TagSearch&SEARCHID=${f:encode(tag.tag)}&scope=${scope}"
							style="color:${tag.group.color}">${tag.tag}</a>&nbsp;</c:forEach>
				</p>			
						
		  </div>		  

			<form style="margin:0px; padding:0px;" name="tagedit" id="tagedit" action="/tagsgroups/doc.url" METHOD="POST">
                <input type="hidden"  name="tagdoc" value ="${actionBean.tagbubble.docid}" />
                <input type="hidden"  name="searchtype" value ="${actionBean.searchtype}" />
                <input type="hidden"  name="searchgrouptags" value ="${f:encode(actionBean.searchid)}" />
                <input type="hidden" name="editfield" id="editfield"/>
	            <input type = "hidden"  name="backurl"  value ="${actionBean.backurl}" /> 
                <div style="padding-left:7px; padding-top: 5px; padding-bottom: 7px;">
                  <c:if test="${actionBean.context.userLoggedIn}">
                    <c:if test="${actionBean.tagbubble.setEdit}">
                      <a href="#" id="tageditlink"><img style="position:relative; top:-2px" name="editlink" src="/static/images/Edit.png" border="0"/>&nbsp;Edit</a>
                    </c:if>
                  </c:if>
                  <table style="margin:0px; width:100%; padding:0px; display:none" cellpadding="0" cellspacing="0" border="0" id="tagedit_table">
                    <tbody id="tagedit_table_body">
<c:forEach  var="edittag" items="${actionBean.tagbubble.editTags}" varStatus="status" >
<c:set var="editscope">${edittag.editFieldName}<c:if test="${not empty edittag.editGroupID}">;${edittag.editGroupID}</c:if></c:set>
					<tr><td>
						<input type="hidden" name="${edittag.editHFieldName}" id="${edittag.editScope}" value="${edittag.editTagsName}"></input>
						<label for="txtEdit1" style="font-size: 11px; ">${edittag.editLable}</label>
					</td></tr>
					<tr><td>
						<input type="text" size="25" name="${editscope}" id="txt${edittag.editFieldName}" class="tageditinput" value="${edittag.editTagsName}" style="font-size: 11px; ">
					</td></tr>
</c:forEach>
					<tr><td><input type="submit" value="Save Changes" name="edittags" title="Save Changes"></td></tr>
                    </tbody>
                  </table>
                </div>
              </form>
			  <div class="hr" style="color: #0156AA; background-color: #0156AA; height: 2px; margin-left: 7px; width:95%"><hr/></div>
<%-- Delicious link --%>
<c:url var="location" value="http://${pageContext.request.serverName}:${pageContext.request.localPort}/controller/servlet/Controller?CID=blogDocument&amp;MID=${f:encode(actionBean.tagbubble.docid)}&DATABASE=${actionBean.database}"/>
 		  <p class="im" style="margin-bottom: 7px">
 		     <a href="#" onclick="window.open('https://delicious.com/save?v=5&provider=EngineeringVillage&noui&jump=close&url='+encodeURIComponent(location.href)+'&title='+encodeURIComponent(document.title), 'delicious','toolbar=no,width=550,height=550'); return false;">
                <img src="https://delicious.com/img/logo.png" height="16" width="16" alt="Delicious"> Save this on Delicious
             </a>
          </p>
		  
	</div>

<jwr:script src="/bundles/tagbubble.js"></jwr:script>
	


