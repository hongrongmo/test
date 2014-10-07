<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/tlds/functions.tld" prefix="f"%>

<c:choose>
	<c:when test="${param.displaytype eq 'selectedrecords'}">
		<c:choose>
			<c:when test="${not empty actionBean.searchresults}">
				<c:set var="searchresultslink">CID=${actionBean.reruncid}&SEARCHID=${actionBean.searchid}&COUNT=${actionBean.pagenav.currentindex}&database=${actionBean.database}</c:set>
			</c:when>
			<c:otherwise>
				<c:set var="searchresultslink"></c:set>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<c:set var="searchresultslink">CID=${actionBean.reruncid}&SEARCHID=${actionBean.searchid}&COUNT=${actionBean.pagenav.currentindex}&database=${actionBean.database}</c:set>
	</c:otherwise>
</c:choose>

<c:set var="newsearchlink">CID=<c:choose>
		<c:when test="${actionBean.searchtype == 'Expert'}">expertSearch&database=${actionBean.database}</c:when>
		<c:when test="${actionBean.searchtype == 'Thesaurus'}">thesHome&database=${actionBean.database}#init</c:when>
		<c:when test="${actionBean.searchtype == 'Book'}">ebookSearch&database=131072</c:when>
		<c:otherwise>quickSearch&database=${actionBean.database}</c:otherwise>
	</c:choose>
</c:set>


<div id="resultsmanager" aria-label="Selected Records and Result action" role="navigation">
	<c:choose>
		<c:when test="${param.displaytype eq 'selectedrecords'}">
			<div id="pageselect" class="col" style="width: 70px;">
				<a id="removealllink"
					href="/selected/delete.url?CID=deleteFromSelectedSet&docid=all&SEARCHID=${actionBean.searchid}&searchtype=${actionBean.searchtype}&resultscount=${actionBean.resultscount}&database=${actionBean.database}&searchresults=${actionBean.searchresultsencoded}&newsearch=${actionBean.newsearchencoded}&backindex=${actionBean.pagenav.currentindex}&DATABASEID=${actionBean.database}"
					title="Remove all selections"> <jwr:img border="0" src="/static/images/Remove_All.png" alt="Remove all selections"/>
				</a>
			</div>
		</c:when>
		<c:when test="${param.displaytype eq 'viewfolders'}">
			<div id="pageselect" class="col" style="width: 70px;">
				<a id="removealllink"
					href="/selected/deleteallfolder.url?CID=deleteAllFromFolder&format=citation&folderid=${actionBean.folderid}&database=${actionBean.database}"
					title="Remove all selections"> <jwr:img border="0"
					src="/static/images/Remove_All.png" alt="Remove all selections"/>
				</a>
			</div>
		</c:when>
		<c:otherwise>
			<div id="pageselect_boss" class="col">
				<b>Select:</b>&nbsp;&nbsp;<a href="${actionBean.helpUrl}#selected_recs.htm"  title="Learn more about selecting records" class="helpurl" style="border:none; text-decoration:none"><jwr:img src="/static/images/i.png" border="0" styleClass="infoimg" alt="Learn more about selecting records"/></a>
				<br />
                <div id="pageselect" class="pageselect">
                  <input id="pageckbx" type="checkbox" name="page" class="pageselect_default" action="page" title="Select current page"/>
                  <jwr:img styleId="pageselect_toggle" title="Selection options" alt="Selection options" src="/static/images/down.jpg" styleClass="pageselect_toggle closed"/>
                </div>
                <ul class="pageselect_menu horizlist" id="pageselect_menu">
                    <li id="Page" class="pageselect_action" action="page"><span>Page</span></li>
                    <li id="Maximum" class="pageselect_action" action="maximum"><span>Maximum (up to 500)</span></li>
                </ul>
                <div id="basketdialog" style="display:none"></div>
            </div>
		</c:otherwise>
	</c:choose>

    <%-- ************* TOOLBAR ************* --%>
	<div id="toolbar" class="col">
<c:choose>
          <%-- ************* PAGE FORMAT (view folders display) ************* --%>
	<c:when test="${param.displaytype eq 'viewfolders'}">
		<ul id="pageformat" class="horizlist">
		    <b>Page format:&nbsp;&nbsp;</b>
		    <input id="toprdCit"
			   name="selectoption"
  					<c:if test="${empty actionBean.selectoption || 'citation' eq actionBean.selectoption}"> checked='checked'</c:if>
			   title="Use citation format when emailing, printing, downloading, saving, or viewing"
			   value="citation" checked="checked" type="radio"
			   onclick="javascript:document.location='/selected/citationfolder.url?CID=viewCitationSavedRecords&EISESSION=${actionBean.sessionid}&database=${actionBean.database}&folderid=${actionBean.folderid}'">
			<label for="toprdCit">Citation</label>&nbsp;
			<input id="toprdAbs"
			   name="selectoption"
			   <c:if test="${'abstract' eq actionBean.selectoption}"> checked='checked'</c:if>
			   title="Use abstract format when emailing, printing, downloading, saving, or viewing"
			   value="abstract" type="radio" type="radio"
			   onclick="javascript:document.location='/selected/abstractfolder.url?CID=viewAbstractSavedRecords&EISESSION=${actionBean.sessionid}&database=${actionBean.database}&folderid=${actionBean.folderid}'">
			<label for="toprdAbs">Abstract</label>&nbsp;
			<input id="toprdDet"
			   name="selectoption"
			   <c:if test="${'detailed' eq actionBean.selectoption}"> checked='checked'</c:if>
			   title="Use detailed format when emailing, printing, downloading, saving, or viewing"
			   value="detailed" type="radio" type="radio"
			   onclick="javascript:document.location='/selected/detailedfolder.url?CID=viewDetailedSavedRecords&EISESSION=${actionBean.sessionid}&database=${actionBean.database}&folderid=${actionBean.folderid}'">
			<label for="toprdDet">Detailed record</label>
		</ul>
	</c:when>

    <%-- ************* PAGE FORMAT (selected records display) ************* --%>
	<c:when test="${param.displaytype eq 'selectedrecords'}">
        <ul id="pageformat" class="horizlist">
		    <li><b>Page format:&nbsp;&nbsp;</b>
		    <input id="toprdCit"
		        name="selectoption"
			<c:if test="${empty actionBean.selectoption || 'citation' eq actionBean.selectoption}"> checked='checked'</c:if>
  					title="Use citation format when emailing, printing, downloading, saving, or viewing"
  				    value="citation" checked="checked" type="radio"
  			    onclick="javascript:document.location='/selected/citation.url?CID=citationSelectedSet&EISESSION=${actionBean.sessionid}&DATABASEID=${actionBean.database}&SEARCHTYPE=${actionBean.searchtype}&BASKETCOUNT=${actionBean.pagenav.currentpage}&SEARCHID=${actionBean.searchid}&backIndex=${actionBean.pagenav.currentindex}&newsearch=${f:encode(newsearchlink)}&searchresults=${f:encode(searchresultslink)}'" />
			<label for="toprdCit">Citation</label>&nbsp;
			<input id="toprdAbs"
			   name="selectoption"
			   <c:if test="${'abstract' eq actionBean.selectoption}"> checked='checked'</c:if>
			   title="Use abstract format when emailing, printing, downloading, saving, or viewing"
			   value="abstract" type="radio" type="radio"
			   onclick="javascript:document.location='/selected/abstract.url?CID=abstractSelectedSet&EISESSION=${actionBean.sessionid}&DATABASEID=${actionBean.database}&SEARCHTYPE=${actionBean.searchtype}&BASKETCOUNT=${actionBean.pagenav.currentpage}&SEARCHID=${actionBean.searchid}&backIndex=${actionBean.pagenav.currentindex}&newsearch=${f:encode(newsearchlink)}&searchresults=${f:encode(searchresultslink)}'" />
			<label for="toprdAbs">Abstract</label>&nbsp;
			<input id="toprdDet"
			   name="selectoption"
			   <c:if test="${'detailed' eq actionBean.selectoption}"> checked='checked'</c:if>
			   title="Use detailed format when emailing, printing, downloading, saving, or viewing"
			   value="detailed" type="radio" type="radio"
			   onclick="javascript:document.location='/selected/detailed.url?CID=detailedSelectedSet&EISESSION=${actionBean.sessionid}&DATABASEID=${actionBean.database}&SEARCHTYPE=${actionBean.searchtype}&BASKETCOUNT=${actionBean.pagenav.currentpage}&SEARCHID=${actionBean.searchid}&backIndex=${actionBean.pagenav.currentindex}&newsearch=${f:encode(newsearchlink)}&searchresults=${f:encode(searchresultslink)}'" />
			<label for="toprdDet">Detailed record</label>
		  </li>
		</ul>
	</c:when>

    <%-- ************* OUTPUT TOOLS AND VIEW SELECTED RECORDS (results pages) ************* --%>
	<c:when	test="${param.displaytype ne 'viewfolders' and param.displaytype ne 'selectedrecords'}">
		<ul id="viewsel" class="horizlist">
			<li class="viewimage"><a
				id="viewlink" title="View your selected records"
				href="/selected/citation.url?CID=citationSelectedSet&EISESSION=${actionBean.sessionid}&DATABASEID=${actionBean.database}&SEARCHTYPE=${actionBean.searchtype}&SEARCHID=${actionBean.searchid}&backIndex=${actionBean.pagenav.currentindex}&newsearch=${f:encode(newsearchlink)}&searchresults=${f:encode(searchresultslink)}">Selected
					Records (<label id="lblbasketcount">${actionBean.basketCount}</label>)</a>
			</li>
			<li class="viewbtn">
			     | <a value="Delete All" class="clearbasket"  id="clearbasket" name="btnclear"  title="Delete all selected records" action="clearonnewsearch" href="#"><jwr:img border="0" src="/static/images/Delete.png"/> Delete All</a>
			</li>
		</ul>
	</c:when>
	<c:otherwise>
		<span><b>Page format:&nbsp;&nbsp;</b>
		<input id="toprdCit"
			name="selectoption"
			<c:if test="${empty actionBean.selectoption || 'citation' eq actionBean.selectoption}"> checked='checked'</c:if>
			title="Use citation format when emailing, printing, downloading, saving, or viewing"
			value="citation" checked="checked" type="radio">
		<label for="toprdCit">Citation</label>&nbsp;
		<input id="toprdAbs"
			name="selectoption"
			<c:if test="${'abstract' eq actionBean.selectoption}"> checked='checked'</c:if>
			title="Use abstract format when emailing, printing, downloading, saving, or viewing"
			value="abstract" type="radio" type="radio">
		<label for="toprdAbs">Abstract</label>&nbsp;
		<input id="toprdDet"
			name="selectoption"
			<c:if test="${'detailed' eq actionBean.selectoption}"> checked='checked'</c:if>
			title="Use detailed format when emailing, printing, downloading, saving, or viewing"
			value="detailed" type="radio" type="radio">
		<label for="toprdDet">Detailed record</label> </span>
	</c:otherwise>
</c:choose>

		<br/>

        <%-- ************* EMAIL/PRINT/DOWNLOAD ************* --%>
        <ul id="outputtools" class="horizlist">
            <li class="email"><a id="emaillink" title="Email selections"
                style="padding-left: 0" aria-labelledby="viewlink" href="">Email</a> |</li>
            <li class="print"><a id="printlink" title="Print selections"  aria-labelledby="viewlink"
                href="">Print</a> |</li>
            <li class="download" id="downloadli"><a id="oneclickDL" title="Download selections" style="display:none"></a><a id="downloadlink" aria-labelledby="viewlink" title="Download selections" href="">Download</a>
            <c:if test="${param.displaytype ne 'viewfolders' || actionBean.removeduplicates}">  | </c:if> </li>

            <c:if test="${param.displaytype ne 'viewfolders'}">
                <li class="save"><a
                    title="Save selections to a folder in Settings" aria-labelledby="viewlink"
                    href="/personal/folders/save/view.url?CID=viewSavedFolders&EISESSION=${actionBean.sessionid}&database=${actionBean.database}&count=${actionBean.pagenav.currentindex}&searchid=${actionBean.searchid}&source=selectedset&backurl=SAVETOFOLDER&searchresults=${f:encode(searchresultslink)}&newsearch=${f:encode(newsearchlink)}">Save to Folder</a>
                <c:if test="${param.displaytype ne '' && param.displaytype ne 'selectedrecords' && param.displaytype ne 'quickresults' || actionBean.removeduplicates}"> | </c:if>
                </li>
                <c:if test="${param.displaytype ne 'selectedrecords' and param.displaytype ne 'quickresults'} ">
                    <li class="view"><a id="viewlink" title="View selections" aria-labelledby="viewlink"
                        href="/selected/citation.url?CID=citationSelectedSet&EISESSION=${actionBean.sessionid}&DATABASEID=${actionBean.database}&SEARCHTYPE=${actionBean.searchtype}&SEARCHID=${actionBean.searchid}&backIndex=${actionBean.pagenav.currentindex}&newsearch=${f:encode(newsearchlink)}&searchresults=${f:encode(searchresultslink)}">View Selections</a> <c:if test="${actionBean.removeduplicates}">  | </c:if>
                    </li>
                </c:if>
            </c:if>
            <c:if test="${actionBean.removeduplicates}">
				<li class="removedups"><a class="removedupslink" href="/search/results/dedupForm.url?CID=dedupForm&database=${actionBean.database}&SEARCHID=${actionBean.searchid}&COUNT=1&SEARCHTYPE=${actionBean.searchtype}&RESULTSCOUNT=${actionBean.resultscount}" title="Remove duplicates from your search results by selecting a primary database">Remove Duplicates</a></li>
			</c:if>
        </ul>

	</div>

    <%-- ************* SORT BY ************* --%>
	<div id="sortby" class="col">
	<br/>
	<c:if test="${param.displaytype ne 'selectedrecords' and param.displaytype ne 'viewfolders' and param.displaytype ne 'tagresults' and (not(actionBean.dedup))}">
		Sort by:
		 <select name="sort" id="sort" title="Sort results by">
				<c:forEach var="sortoption" items="${actionBean.sortoptions}">
					<option
						value="${rerunactionurl}?CID=${actionBean.reruncid}&database=${actionBean.database}&RERUN=${actionBean.searchid}&sort=${sortoption.field}&sortdir=${sortoption.direction}"
						<c:if test="${(actionBean.sort eq sortoption.field) and (actionBean.sortdir eq sortoption.direction)}"> selected="selected"</c:if>>${sortoption.label}</option>
				</c:forEach>
		</select>
		<noscript>
			<input type="button" class="button" value="Go" title="Go to Selected Order"></input>
		</noscript>

	</c:if>
    </div>


	</div>
	<div class="clear"></div>
</div>
</FORM>

