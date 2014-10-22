<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<div id="facet_overlay_${navigator.field}" name="facet_item" class="facetbox" field="${navigator.field}">
			<div class="facetheader">
				<h3 class="facettitle facetshowhide" field="${navigator.field}" title="${navopen ? 'Close' : 'Open'}"><b>${navigator.label}</b>&nbsp;&nbsp;</h3>
				<div class="facetheader_links"> 
				<c:if test="${actionBean.isnavchrt}">
					<a title="View chart" href=""
						onclick="window.open('/search/results/analyzenav.url?searchid=${actionBean.searchid}&database=${actionBean.database}&field=${navigator.field}','newwindow','width=600,height=630,toolbar=no,location=no,scrollbars,resizable');return false"><jwr:img
						src="/static/images/Graph.png" styleId="graph" border="0" alt="View Chart"/></a>
					<a title="Download data"
						href="/controller/servlet/Controller?CID=downloadNavigatorCSV&searchid=${actionBean.searchid}&database=${actionBean.database}&nav=${navigator.field}nav"><jwr:img
						src="/static/images/Data.png" styleId="data" border="0" alt="Download Data"/></a>
				</c:if>		
					<a class="facetshowhide" field="${navigator.field}"
						title="${navopen ? 'Close' : 'Open'}"> <c:choose>
							<c:when test="${!navopen}">
								<jwr:img src="/static/images/facet_down.png"
									styleClass="facetupdown" border="0" alt="facet down arrow"/>
							</c:when>
							<c:otherwise>
								<jwr:img src="/static/images/facet_up.png"
									styleClass="facetupdown" border="0" alt="facet up arrow"/>
							</c:otherwise>
						</c:choose>
					</a>
				</div>
				<div class="clear"></div>
			</div>			

			<div class="facetentries"<c:if test="${!navopen}"> style="display:none"</c:if>>
			<fieldset>
			<ul>
			
<c:forEach items="${navigator.items}" var="item" varStatus="itemstatus">
			<tr class="facetentry<c:if test="${itemstatus.count > 5}"> facetbottom5</c:if>"<c:if test="${(navigator.open <= 5 && navigator.open >= -5) && itemstatus.count > 5}"> style="</c:if>>
				<td class="facetentry_ck">
					<input type="checkbox" name="${navigator.name}"
                         value="${item.count}~${item.value}~${item.label}"
                         id="${navigator.name}${itemstatus.count}" />
                 </td>
				<td class="facetentry_label">
				<c:choose>
				<c:when test="${fn:length(item.label) le  80}"><label for="${navigator.name}${itemstatus.count}">${item.label}</label></c:when>
				<c:otherwise><label for="${navigator.name}${itemstatus.count}">${fn:substring(item.label, 0, 79)}
									<span style="cursor: pointer" title="${item.label}">...</span></label>
				</c:otherwise>
				</c:choose>
				</td>
				<td class="facetentry_count">(${item.count})</td>
			</tr>
</c:forEach>
			</ul>
			</fieldset>
<c:if test="${navcount > 5}">
			<div class="facet_moreless">
				<a class="showmore" aria-labelledby="facet_${navigator.field}" more="${navigator.more}" less="${navigator.less}" field="${navigator.field}"<c:if test="${((navigator.open > 5 or navigator.open < -5) and navcount > 5 and navigator.more < 10)}"> style="display:none"</c:if>
			          href="${rerunactionurl}?CID=${actionBean.CID}&navigator=MORE&FIELD=${navigator.field}:${navigator.more}&SEARCHID=${actionBean.searchid}&database=${actionBean.database}&docid=${actionBean.docid}">
		          View more
		        </a> 
		        <%-- Show separator link only when more & less both available --%>
		        <span class="showlessSeprator"<c:if test="${!((navigator.open > 5 or navigator.open < -5) and navigator.more > 10)}"> style="display:none"</c:if>>
		          &nbsp;&nbsp;|&nbsp;&nbsp;
		        </span>
		        <%-- Show "less" link when less count > 0 --%> 
		        <a class="showless" more="${navigator.more}" less="${navigator.less}" field="${navigator.field}"<c:if test="${(navigator.open < 10 and navigator.open > -10)}"> style="display:none"</c:if>
		         href="${rerunactionurl}?CID=${actionBean.CID}&navigator=MORE&FIELD=${navigator.field}:${navigator.less}&SEARCHID=${actionBean.searchid}&database=${actionBean.database}&docid=${actionBean.docid}">
		         View fewer
		        </a>
		    </div>                      
</c:if>
			</div>
		</div>