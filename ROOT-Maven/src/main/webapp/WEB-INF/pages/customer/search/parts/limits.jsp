<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
	
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>

			<div class="searchcomponentshortwrap">
				<div id="limittowrap" style="width:380px; float:left;">
				
                    <div class="searchcomponentlabel">				
					<h2 class="h2help">LIMIT TO</h2>
					<c:choose>
	      				<c:when test="${searchtab == 'expertsearch'}">
	      					<a href="${actionBean.helpUrl}#Date_Limits.htm" alt="Learn more about search limits" title="Learn more about search limits" class="helpurl">
	      				</c:when>
				      <c:otherwise>
				      	<a href="${actionBean.helpUrl}#search_limits_avail_by_database.htm" alt="Learn more about search limits" title="Learn more about search limits" class="helpurl">
				      </c:otherwise>
				    </c:choose>
					<img src="/static/images/i.png" border="0" class="infoimg" align="bottom" alt=""/></a></div>
					<div class="searchlimitbybox">
					<fieldset>
					<ul>
					<c:if test="${searchtab != 'expertsearch'}">
						<c:if test="${not empty actionBean.doctypeopts}">
						<li>
						<label class="hidden" for="limittodoc">Limit To Document Type</label>
                        <stripes:select name="doctype" id="limittodoc" value="${actionBean.doctype}" onchange="checkPatent(this.form);" class="limittype" title="Choose a Document Type">
                        <stripes:options-collection collection="${actionBean.doctypeopts}" label="value" value="name"/>
                        </stripes:select>
					    </li>
						</c:if>

						<c:if test="${not empty actionBean.treatmenttypeopts}">
						<li>
						<label class="hidden" for="limittotreat">Limit To Treatment Type</label>
                        <stripes:select name="treatmentType" id="limittotreat" class="limittype" title="Choose a Treatment Type">
                        <stripes:options-collection collection="${actionBean.treatmenttypeopts}" label="value" value="name"/>
                        </stripes:select>
					    </li>
						</c:if>
						
						<c:if test="${not empty actionBean.disciplinetypeopts}">
						<li>
						<label class="hidden" for="limittodisc">Limit To Discipline Type</label>
                        <stripes:select name="disciplinetype" class="limittype" id="limittodisc" title="Choose a Discipline Type">
                        <stripes:options-collection collection="${actionBean.disciplinetypeopts}" label="value" value="name"/>
                        </stripes:select>
					    </li>
						</c:if>
						
						<%-- Always output language type options --%>
						<li>
						<label class="hidden" for="limittolang">Limit To Language Type</label>
						<stripes:select name="language" class="limittype" id="limittolang" title="Choose a Language">
                        <stripes:options-collection collection="${actionBean.languageopts}" label="value" value="name"/>
						</stripes:select>
					    </li>
					</c:if>
						
						<li>
						  <label class="hidden" for="yearrangechx">Limit To Year Range</label>
						  <stripes:radio name="yearselect" value="yearrange" id="yearrangechx"/>
						  <label class="hidden" for="startyrrange">Start Year</label>
						  <stripes:select name="startYear" onchange="selectYearRange(0);" id="startyrrange" style="width:5em" title="Choose a date range">
                            ${actionBean.startyearopts}
						  </stripes:select>

						<div class="searchcomponentlabel" style="float:none; margin: 0 15px; display:inline"> TO </div>
						  <label class="hidden" for="endyearrange">End Year</label>
						  <stripes:select name="endYear" onchange="selectYearRange(0);" id="endyearrange" style="width:5em" title="Choose a date range">
                            ${actionBean.endyearopts}
						  </stripes:select>
						  <stripes:hidden name="stringYear"/>
						</li>

						<li>
							<label class="hidden" for="rdupdt">Limit to Database Updates</label>
	                        <stripes:radio name="yearselect" value="lastupdate" id="rdupdt" onclick="checkLastUpdates();"
	                        	title="Use this option to limit your search to the database updates from the last week, last 2 weeks, last 3 weeks, or last 4 weeks."/>
	                          <label class="hidden" for="limittoupdates">Number of Udates</label>
	                          <stripes:select name="updatesNo" id="limittoupdates" onchange="selectYearRange(1);" title="Use this option to limit your search to the database updates from the last week, last 2 weeks, last 3 weeks, or last 4 weeks.">
								<stripes:option value="1">1</stripes:option>
								<stripes:option value="2">2</stripes:option>
								<stripes:option value="3">3</stripes:option>
								<stripes:option value="4">4</stripes:option>
	                          </stripes:select>
                            <label for="rdupdt">Updates</label>
                        </li>
                       
                        </ul>
                        </fieldset> 
       				</div>
                           				
				</div>
				
				
				<div id="sortbywrap" style="float:left;"> 
				    <div>
					<h2  class="searchcomponentlabel" style="margin-bottom:5px; width: 65px;"><span>SORT BY</span></h2>
					<a href="${actionBean.helpUrl}#Sort_by.htm" alt="Learn more about choosing sort order" title="Learn more about choosing sort order" class="helpurl"><img
						src="/static/images/i.png" border="0" class="infoimg" align="bottom" alt=""/></a>
					</div>
					<div class="searchlimitbybox">
					<ul>
					<li>
					<fieldset>	
					  <span style="margin-right:15px;">
	                    <input type="radio" name="sort" id="chkrel" value="relevance" title="Sort your search by Relevance"<c:if test="${'relevance' == actionBean.sort or 'publicationYear' != actionBean.sort}"> checked="checked"</c:if>></input>
						<label for="chkrel">Relevance</label>
					  </span>
					  <span>
                        <input type="radio" name="sort" id="chkyr" value="yr" title="Sort your search by Year in descending order"<c:if test="${'yr' == actionBean.sort or 'publicationYear' == actionBean.sort}"> checked="checked"</c:if>></input>
                        <label for="chkyr">Publication year</label>
                      </span>
                      </fieldset>
					</li>

					<li>						
                        <stripes:checkbox name="autostem" id="chkstm" title="Autostemming determines the suffixes of words and allows you to search for the term as entered, the root word, and other words formed"/>
                        <label for="chkstm">Autostemming off</label>
                    </li>
                    </ul>
					</div>
					
				</div>
				
				<div class="clear"></div>
				
			</div>
			
 