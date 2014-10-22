<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div id="part_askalibrarian" class="showhide">
	<p align="left">&nbsp;</p>
	<p align="left" class="style1">A librarian can help you formulate a search, identify and locate a source for a book or an article, or find additional
		resources on a specific topic.</p>
	<p align="left" class="style1">
		To learn more about the subject areas that are covered by the databases available to you, please visit <a target="_blank"
			href="http://www.ei.org/engineeringvillage" title="Learn more about Engineering Village (opens in a new window)">http://www.ei.org/engineeringvillage</a>.
        We also have some training materials available at <a href="http://www.ei.org/trainingpresentations" title="View training presentations (opens in a new window)">ei.org</a>.
	</p>
	<div align="left">
		<table width="972" border="0" cellspacing="0" cellpadding="0">
			<tbody>
				<tr>
					<td width="429" align="left" valign="top"><img
							src="/static/images/ae/New_Specialist.gif" title="" width="429" height="356" border="0"
							id="lib_Image1".></td>
					<td width="25" align="left" valign="top"><img src="/static/images/ae/spacer.gif" alt="spacer" width="25" height="1"></td>
					<td width="518" align="left" valign="middle"><span class="style2">Sample questions:</span><br>
						<ol>
							<li class="style1">"Can you help me find the Journal of Constructional Steel Research?"</li>
							<li class="style1">"I'd like to do a detailed search on coercive force and material composition but I need assistance."</li>
						</ol>
						<p>
						<c:choose>
						    <c:when test="${actionBean.mailto}">
						       <a name="Email a Librarian" title="Email a Librarian"  href="${actionBean.librarianEmail}">Email a Librarian</a>
						    </c:when>
						     <c:when test="${actionBean.javaScriptLink}">
                               <a name="Email a Librarian" title="Email a Librarian"  href="${actionBean.librarianEmail}">Email a Librarian</a>
                            </c:when>
							<c:otherwise>
							 <input type="button" value="Email a Librarian" title="Email a Librarian" class="emaillink button" href="${actionBean.librarianEmail}"/>
							</c:otherwise>
						</c:choose>	
						</p>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	<p align="left">&nbsp;</p>
</div>