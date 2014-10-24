<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div id="part_askanengineer" class="showhide">
	<p align="left">&nbsp;</p>
	<table border="0" cellpadding="0" cellspacing="0">
		<tbody>
			<tr>
				<td><img id="Chemical" class="discipline" title="Chemical" 
						src="/static/images/ae/Chemical.gif" 
						width="108" height="128" border="0"/></td>
				<td><img id="Industrial" class="discipline" title="Industrial" 
						src="/static/images/ae/Industrial.gif" 
						width="108" height="128" border="0"/></td>
				<td><img id="Mechanical" class="discipline" title="Industrial"
						src="/static/images/ae/Mechanical.gif" 
						width="108" height="128" border="0"/></td>
				<td><img id="Electrical" class="discipline" title="Electrical"
						src="/static/images/ae/Electrical.gif" 
						width="108" height="128" border="0"/></td>
				<td><img id="SignalProcessing" class="discipline" title="Signal Processing" 
						src="/static/images/ae/SignalProcessing.gif" 
						width="108" height="128" border="0"/></td>
				<td><img id="Manufacturing" class="discipline" title="Manufacturing"
						src="/static/images/ae/Manufacturing.gif" 
						width="108" height="128" border="0"/></td>
				<td><img id="Materials" class="discipline" title="Materials"
						src="/static/images/ae/Materials.gif" 
						width="108" height="128" border="0"/></td>
				<td><img id="Management" class="discipline" title="Management"
						src="/static/images/ae/Management.gif" 
						width="108" height="128" border="0"/></td>
				<td><img id="Computer" class="discipline" title="Computer"
						src="/static/images/ae/Computer.gif"
						width="108" height="128" border="0"/></td>
			</tr>
		</tbody>
	</table>
	<a id="auto_scroller" href="auto_scroller"></a>
<%-- ********************************************************* --%>
<%-- MECHANICAL LAYER                                          --%>	
<%-- ********************************************************* --%>	
		    <div class="Layer" id="Mechanical_Layer" style="display: none;">
		      <p align="left" class="style1"><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, websites, and other resources that can help you answer your question.</span></p>
		
		      <table width="972" border="0" cellspacing="0" cellpadding="0">
<c:forEach items="${actionBean.disciplines['mechanical'].gurus}" var="guru" varStatus="guruStatus">
		        <tr>
<c:if test="${guruStatus.first}">
		          <td width="111" align="left" valign="top" rowspan="${fn:length(actionBean.disciplines['mechanical'].gurus)}"><img src="/static/images/ae/Mechanical.gif" alt="Mechanical" width="108" height="128" /></td>
</c:if>
		          <td width="25"><img src="/static/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
		          <td width="417" align="left" valign="top">
						  <p align="left" class="style1">
						 	<span class="style2"><a href="${guru.searchLink}" title="Search this expert">${guru.name}</a>
						 	</span> <br />
						  </p>
<c:if test="${guru.guruInfo != null && guru.guruInfo != ''}">
						  <p align="left" class="style1">
								${guru.guruInfo}
						  </p><br/>
</c:if>
						  <p align="left" class="style1">Areas of expertise:
							<c:forEach items="${guru.items}" var="search" varStatus="slCount">
								<a href="${search.href}" title="Search this expertise">${search.name}</a>			          	
								<c:if test="${slCount.count != fn:length(guru.items)}">
									<span>, </span>
								</c:if>
							</c:forEach>
						  </p>
						  <p align="left" class="style1">&nbsp;</p>
				  </td>		          
				  <td width="25" align="left" valign="top"><img src="/static/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
		          <td width="394" align="left" valign="top">
<c:if test="${guruStatus.first}">
		          	<div style="text-align:left; margin-bottom: 7px"><span class="style1"><span class="style2">Sample question(s):  &nbsp; &nbsp;</span></span></div>
</c:if>
<c:choose>
<c:when test="${guruStatus.count eq 1}">
		            <p align="left" class="style1">&ldquo;What type of steel rod/axle&nbsp;would you use for one way roller bearings.&rdquo;</p>
</c:when>
<c:when test="${guruStatus.count eq 2}">
		            <p align="left" class="style1">&nbsp;&ldquo;How can I calculate or at arrive the capacity of a mechanical press?&rdquo;</p>
</c:when>
<c:otherwise>
		            <p align="left" class="style1">No question available for this expert.</p>
</c:otherwise>
</c:choose>
		            <p><input type="button" value="Email this Engineer" title="Email this Engineer" class="emaillink button" href="/askanexpert/email/display.url?section=Ask%20an%20Engineer&sectionid=one&discipline=Mechanical&disciplineid=Layer1&guru=${guru.name}"/></p>
		            </td>
		        </tr>
</c:forEach>
		      </table>
		      <p><br />
		      </p>
		    </div>
<%-- ********************************************************* --%>
<%-- MANUFACTURING LAYER                                       --%>	
<%-- ********************************************************* --%>	
		    <div class="Layer" id="Manufacturing_Layer" style="display: none;">
		      <p align="left" class="style1"><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, websites, and other resources that can help you answer your question.</span></p>
		
		      <table width="972" border="0" cellspacing="0" cellpadding="0">
<c:forEach items="${actionBean.disciplines['manufacturing'].gurus}" var="guru" varStatus="guruStatus">
		        <tr>
<c:if test="${guruStatus.first}">
		          <td width="111" align="left" valign="top" rowspan="${fn:length(actionBean.disciplines['manufacturing'].gurus)}"><img src="/static/images/ae/Manufacturing.gif" alt="Manufacturing" width="108" height="128" /></td>
</c:if>
		          <td width="25"><img src="/static/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
		          <td width="417" align="left" valign="top">
						  <p align="left" class="style1">
						 	<span class="style2"><a href="${guru.searchLink}" title="Search this expert">${guru.name}</a>
						 	</span> <br />
						  </p>
<c:if test="${guru.guruInfo != null && guru.guruInfo != ''}">
						  <p align="left" class="style1">
								${guru.guruInfo}
						  </p><br/>
</c:if>
						  <p align="left" class="style1">Areas of expertise:
							<c:forEach items="${guru.items}" var="search" varStatus="slCount">
								<a href="${search.href}" title="Search this expertise">${search.name}</a>			          	
								<c:if test="${slCount.count != fn:length(guru.items)}">
									<span>, </span>
								</c:if>
							</c:forEach>
						  </p>
						  <p align="left" class="style1">&nbsp;</p>
				  </td>		        
				  
		          <td width="25" align="left" valign="top"><img src="/static/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
		          <td width="394" align="left" valign="top">
<c:if test="${guruStatus.first}">
		          	<div style="text-align:left; margin-bottom: 7px"><span class="style1"><span class="style2">Sample question(s):  &nbsp; &nbsp;</span></span></div>
</c:if>
<c:choose>
<c:when test="${guruStatus.count eq 1}">
		            <p align="left" class="style1">&ldquo;How do you organize the value engineering functions in a tractor manufacturing firm?&rdquo;</p>
</c:when>
<c:when test="${guruStatus.count eq 2}">
		            <p align="left" class="style1">&ldquo;What&rsquo;s the best individual fabrication system for Bulk-handling machines?&rdquo;</p>
</c:when>
<c:otherwise>
		            <p align="left" class="style1">No question available for this expert.</p>
</c:otherwise>
</c:choose>
		            <p><input type="button" value="Email this Engineer" title="Email this Engineer" class="emaillink button" href="/askanexpert/email/display.url?section=Ask an Engineer&sectionid=one&discipline=Manufacturing&disciplineid=Layer2&guru=${guru.name}"/></p>
<c:if test="${not guruStatus.last}">
		            <p class="style1">&nbsp;</p>
		            <p class="style1">&nbsp;</p>
</c:if>
				  </td>
		        </tr>
</c:forEach>
		      </table>
		      <p><br />
		      </p>
		    </div>
	
<%-- ********************************************************* --%>
<%-- SIGNAL PROCESSING LAYER                                   --%>	
<%-- ********************************************************* --%>	
		    <div class="Layer" id="SignalProcessing_Layer" style="display: none;">
		      <p align="left" class="style1"><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, websites, and other resources that can help you answer your question.</span></p>
		
		      <table width="972" border="0" cellspacing="0" cellpadding="0">
<c:forEach items="${actionBean.disciplines['signal processing'].gurus}" var="guru" varStatus="guruStatus">
		        <tr>
<c:if test="${guruStatus.first}">
		          <td width="111" align="left" valign="top" rowspan="${fn:length(actionBean.disciplines['signal processing'].gurus)}"><img src="/static/images/ae/SignalProcessing.gif" alt="SignalProcessing" width="108" height="128" /></td>
</c:if>
		          <td width="25"><img src="/static/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
		          <td width="417" align="left" valign="top">
						  <p align="left" class="style1">
						 	<span class="style2"><a href="${guru.searchLink}" title="Search this expert">${guru.name}</a>
						 	</span> <br />
						  </p>
<c:if test="${guru.guruInfo != null && guru.guruInfo != ''}">
						  <p align="left" class="style1">
								${guru.guruInfo}
						  </p><br/>
</c:if>
						  <p align="left" class="style1">Areas of expertise:
							<c:forEach items="${guru.items}" var="search" varStatus="slCount">
								<a href="${search.href}" title="Search this expertise">${search.name}</a>			          	
								<c:if test="${slCount.count != fn:length(guru.items)}">
									<span>, </span>
								</c:if>
							</c:forEach>
						  </p>
						  <p align="left" class="style1">&nbsp;</p>
				  </td>		        
		          
		          <td width="25" align="left" valign="top"><img src="/static/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
		          <td width="394" align="left" valign="top">
<c:if test="${guruStatus.first}">
		          	<div style="text-align:left; margin-bottom: 7px"><span class="style1"><span class="style2">Sample question(s):  &nbsp; &nbsp;</span></span></div>
</c:if>
<c:choose>
<c:when test="${guruStatus.count eq 1}">
		            <p align="left" class="style1">&ldquo;Which images or signals inside the tunnel are best for object detection?&rdquo;</p>
</c:when>
<c:otherwise>
		            <p align="left" class="style1">No question available for this expert.</p>
</c:otherwise>
</c:choose>
		            <p><input type="button" value="Email this Engineer" title="Email this Engineer" class="emaillink button" href="/askanexpert/email/display.url?section=Ask an Engineer&sectionid=one&discipline=Signal Processing&disciplineid=Layer3&guru=${guru.name}"/></p>
<c:if test="${not guruStatus.last}">
		            <p class="style1">&nbsp;</p>
		            <p class="style1">&nbsp;</p>
</c:if>
		           </td>
		        </tr>
</c:forEach>
		      </table>
		      <p><br />
		      </p>
		    </div>
	
	
<%-- ********************************************************* --%>
<%-- ELECTRICAL LAYER                                          --%>	
<%-- ********************************************************* --%>	
		    <div class="Layer" id="Electrical_Layer" style="display: none;">
		      <p align="left" class="style1"><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, websites, and other resources that can help you answer your question.</span></p>
		
		      <table width="972" border="0" cellspacing="0" cellpadding="0">
<c:forEach items="${actionBean.disciplines['electrical'].gurus}" var="guru" varStatus="guruStatus">
		        <tr>
<c:if test="${guruStatus.first}">
		          <td width="111" align="left" valign="top" rowspan="${fn:length(actionBean.disciplines['electrical'].gurus)}"><img src="/static/images/ae/Electrical.gif" alt="Electrical" width="108" height="128" /></td>
</c:if>
		          <td width="25"><img src="/static/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>

		          <td width="417" align="left" valign="top">
						  <p align="left" class="style1">
						 	<span class="style2"><a href="${guru.searchLink}" title="Search this expert">${guru.name}</a>
						 	</span> <br />
						  </p>
<c:if test="${guru.guruInfo != null && guru.guruInfo != ''}">
						  <p align="left" class="style1">
								${guru.guruInfo}
						  </p><br/>
</c:if>
						  <p align="left" class="style1">Areas of expertise:
							<c:forEach items="${guru.items}" var="search" varStatus="slCount">
								<a href="${search.href}" title="Search this expertise">${search.name}</a>			          	
								<c:if test="${slCount.count != fn:length(guru.items)}">
									<span>, </span>
								</c:if>
							</c:forEach>
						  </p>
						  <p align="left" class="style1">&nbsp;</p>
				  </td>		        

		          <td width="25" align="left" valign="top"><img src="/static/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
		          <td width="394" align="left" valign="top">
<c:if test="${guruStatus.first}">
		          	<div style="text-align:left; margin-bottom: 7px"><span class="style1"><span class="style2">Sample question(s):  &nbsp; &nbsp;</span></span></div>
</c:if>
<c:choose>
<c:when test="${guruStatus.count eq 1}">
		            <p class="style1">&ldquo;How can I calibrate a current transformer?&rdquo;</p>
</c:when>
<c:otherwise>
		            <p align="left" class="style1">No question available for this expert.</p>
</c:otherwise>
</c:choose>
		            <p><input type="button" value="Email this Engineer" title="Email this Engineer" class="emaillink button" href="/askanexpert/email/display.url?section=Ask an Engineer&sectionid=one&discipline=Electrical&disciplineid=Layer8&guru=${guru.name}"/></p>
<c:if test="${not guruStatus.last}">
		            <p class="style1">&nbsp;</p>
		            <p class="style1">&nbsp;</p>
</c:if>
				  </td>
		        </tr>
</c:forEach>
		      </table>
		      <p><br />
		      </p>
		    </div>
	
<%-- ********************************************************* --%>
<%-- ENGINEERING MANAGEMENT LAYER                              --%>	
<%-- ********************************************************* --%>	
		    <div class="Layer" id="Management_Layer" style="display: none;">
		      <p align="left" class="style1"><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, websites, and other resources that can help you answer your question.</span></p>
		
		      <table width="972" border="0" cellspacing="0" cellpadding="0">
<c:forEach items="${actionBean.disciplines['management'].gurus}" var="guru" varStatus="guruStatus">
		        <tr>
<c:if test="${guruStatus.first}">
		          <td width="111" align="left" valign="top" rowspan="${fn:length(actionBean.disciplines['management'].gurus)}"><img src="/static/images/ae/Management.gif" alt="Management" width="108" height="128" /></td>
</c:if>
		          <td width="25"><img src="/static/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>

		          <td width="417" align="left" valign="top">
						  <p align="left" class="style1">
						 	<span class="style2"><a href="${guru.searchLink}" title="Search this expert">${guru.name}</a>
						 	</span> <br />
						  </p>
<c:if test="${guru.guruInfo != null && guru.guruInfo != ''}">
						  <p align="left" class="style1">
								${guru.guruInfo}
						  </p><br/>
</c:if>
						  <p align="left" class="style1">Areas of expertise:
							<c:forEach items="${guru.items}" var="search" varStatus="slCount">
								<a href="${search.href}" title="Search this expertise">${search.name}</a>			          	
								<c:if test="${slCount.count != fn:length(guru.items)}">
									<span>, </span>
								</c:if>
							</c:forEach>
						  </p>
						  <p align="left" class="style1">&nbsp;</p>
				  </td>		        

		          <td width="25" align="left" valign="top"><img src="/static/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
		          <td width="394" align="left" valign="top">
<c:if test="${guruStatus.first}">
		          	<div style="text-align:left; margin-bottom: 7px"><span class="style1"><span class="style2">Sample question(s):  &nbsp; &nbsp;</span></span></div>
</c:if>
<c:choose>
<c:when test="${guruStatus.count eq 1}">
		            <p align="left" class="style1">&ldquo;During design, how is optimization achieved and what factors are considered?&rdquo;</p>
</c:when>
<c:otherwise>
		            <p align="left" class="style1">No question available for this expert.</p>
</c:otherwise>
</c:choose>
		            <p><input type="button" value="Email this Engineer" title="Email this Engineer" class="emaillink button" href="/askanexpert/email/display.url?section=Ask an Engineer&sectionid=one&discipline=Engineering Management&disciplineid=Layer9&guru=${guru.name}"/></p>
<c:if test="${not guruStatus.last}">
		            <p class="style1">&nbsp;</p>
		            <p class="style1">&nbsp;</p>
</c:if>
		          </td>
		        </tr>
</c:forEach>
		      </table>
		      <p><br />
		      </p>
		    </div>
	
<%-- ********************************************************* --%>
<%-- INDUSTRIAL LAYER                                          --%>	
<%-- ********************************************************* --%>	
		    <div class="Layer" id="Industrial_Layer" style="display: none;">
		      <p align="left" class="style1"><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, websites, and other resources that can help you answer your question.</span></p>
		
		      <table width="972" border="0" cellspacing="0" cellpadding="0">
<c:forEach items="${actionBean.disciplines['industrial'].gurus}" var="guru" varStatus="guruStatus">
		        <tr>
<c:if test="${guruStatus.first}">
		          <td width="111" align="left" valign="top" rowspan="${fn:length(actionBean.disciplines['industrial'].gurus)}"><img src="/static/images/ae/Industrial.gif" alt="Industrial" width="108" height="128" /></td>
</c:if>
		          <td width="25"><img src="/static/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>

		          <td width="417" align="left" valign="top">
						  <p align="left" class="style1">
						 	<span class="style2"><a href="${guru.searchLink}" title="Search this expert">${guru.name}</a>
						 	</span> <br />
						  </p>
<c:if test="${guru.guruInfo != null && guru.guruInfo != ''}">
						  <p align="left" class="style1">
								${guru.guruInfo}
						  </p><br/>
</c:if>
						  <p align="left" class="style1">Areas of expertise:
							<c:forEach items="${guru.items}" var="search" varStatus="slCount">
								<a href="${search.href}" title="Search this expertise">${search.name}</a>			          	
								<c:if test="${slCount.count != fn:length(guru.items)}">
									<span>, </span>
								</c:if>
							</c:forEach>
						  </p>
						  <p align="left" class="style1">&nbsp;</p>
				  </td>		        

		          <td width="25" align="left" valign="top"><img src="/static/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
		          <td width="394" align="left" valign="top">
<c:if test="${guruStatus.first}">
		          	<div style="text-align:left; margin-bottom: 7px"><span class="style1"><span class="style2">Sample question(s):  &nbsp; &nbsp;</span></span></div>
</c:if>
<c:choose>
<c:when test="${guruStatus.count eq 1}">
		            <p align="left" class="style1">&ldquo;To ensure a certain interval in a time study, how many samples should be done?&rdquo;</p>
</c:when>
<c:when test="${guruStatus.count eq 2}">
		            <p align="left" class="style1">&ldquo;How can I measure energy produced from water chilled air conditioning?&rdquo;</p>
</c:when>
<c:otherwise>
		            <p align="left" class="style1">No question available for this expert.</p>
</c:otherwise>
</c:choose>
		            <p><input type="button" value="Email this Engineer" title="Email this Engineer" class="emaillink button" href="/askanexpert/email/display.url?section=Ask an Engineer&sectionid=one&discipline=Industrial&disciplineid=Layer15&guru=${guru.name}"/></p>
<c:if test="${not guruStatus.last}">
		            <p class="style1">&nbsp;</p>
		            <p class="style1">&nbsp;</p>
</c:if>
				  </td>
		        </tr>
</c:forEach>
		      </table>
		      <p><br />
		      </p>
		    </div>
	
<%-- ********************************************************* --%>
<%-- MATERIALS LAYER                                          --%>	
<%-- ********************************************************* --%>	
		    <div class="Layer" id="Materials_Layer" style="display: none;">
		      <p align="left" class="style1"><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, websites, and other resources that can help you answer your question.</span></p>
		
		      <table width="972" border="0" cellspacing="0" cellpadding="0">
<c:forEach items="${actionBean.disciplines['materials'].gurus}" var="guru" varStatus="guruStatus">
		        <tr>
<c:if test="${guruStatus.first}">
		          <td width="111" align="left" valign="top" rowspan="${fn:length(actionBean.disciplines['materials'].gurus)}"><img src="/static/images/ae/Materials.gif" alt="Materials" width="108" height="128" /></td>
</c:if>
		          <td width="25"><img src="/static/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>

		          <td width="417" align="left" valign="top">
						  <p align="left" class="style1">
						 	<span class="style2"><a href="${guru.searchLink}" title="Search this expert">${guru.name}</a>
						 	</span> <br />
						  </p>
<c:if test="${guru.guruInfo != null && guru.guruInfo != ''}">
						  <p align="left" class="style1">
								${guru.guruInfo}
						  </p><br/>
</c:if>
						  <p align="left" class="style1">Areas of expertise:
							<c:forEach items="${guru.items}" var="search" varStatus="slCount">
								<a href="${search.href}" title="Search this expertise">${search.name}</a>			          	
								<c:if test="${slCount.count != fn:length(guru.items)}">
									<span>, </span>
								</c:if>
							</c:forEach>
						  </p>
						  <p align="left" class="style1">&nbsp;</p>
				  </td>		        

		          <td width="25" align="left" valign="top"><img src="/static/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
		          <td width="394" align="left" valign="top">
<c:forEach items="${actionBean.disciplines['materials'].gurus}" var="guru" varStatus="guruStatus">
<c:if test="${guruStatus.first}">
		          	<div style="text-align:left; margin-bottom: 7px"><span class="style1"><span class="style2">Sample question(s):  &nbsp; &nbsp;</span></span></div>
</c:if>
<c:choose>
<c:when test="${guruStatus.count eq 1}">
		              <p class="style1">&ldquo;Is it possible to give heat treatment to copper?&rdquo; </p>
</c:when>
<c:otherwise>
		            <p align="left" class="style1">No question available for this expert.</p>
</c:otherwise>
</c:choose>
		            <p><input type="button" value="Email this Engineer" title="Email this Engineer" class="emaillink button" href="/askanexpert/email/display.url?section=Ask an Engineer&sectionid=one&discipline=Materials&disciplineid=Layer10&guru=${guru.name}"/></p>
<c:if test="${not guruStatus.last}">
		            <p class="style1">&nbsp;</p>
		            <p class="style1">&nbsp;</p>
</c:if>
</c:forEach>
				  </td>
		        </tr>
</c:forEach>
		      </table>
		      <p><br />
		      </p>
		    </div>
	
<%-- ********************************************************* --%>
<%-- COMPUTER LAYER                                          --%>	
<%-- ********************************************************* --%>	
		    <div class="Layer" id="Computer_Layer" style="display: none;">
		      <p align="left" class="style1"><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, websites, and other resources that can help you answer your question.</span></p>
		
		      <table width="972" border="0" cellspacing="0" cellpadding="0">
<c:forEach items="${actionBean.disciplines['computer'].gurus}" var="guru" varStatus="guruStatus">
		        <tr>
<c:if test="${guruStatus.first}">
		          <td width="111" align="left" valign="top" rowspan="${fn:length(actionBean.disciplines['computer'].gurus)}"><img src="/static/images/ae/Computer.gif" alt="Computer" width="108" height="128" /></td>
</c:if>
		          <td width="25"><img src="/static/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>

		          <td width="417" align="left" valign="top">
						  <p align="left" class="style1">
						 	<span class="style2"><a href="${guru.searchLink}" title="Search this expert">${guru.name}</a>
						 	</span> <br />
						  </p>
<c:if test="${guru.guruInfo != null && guru.guruInfo != ''}">
						  <p align="left" class="style1">
								${guru.guruInfo}
						  </p><br/>
</c:if>
						  <p align="left" class="style1">Areas of expertise:
							<c:forEach items="${guru.items}" var="search" varStatus="slCount">
								<a href="${search.href}" title="Search this expertise">${search.name}</a>			          	
								<c:if test="${slCount.count != fn:length(guru.items)}">
									<span>, </span>
								</c:if>
							</c:forEach>
						  </p>
						  <p align="left" class="style1">&nbsp;</p>
				  </td>		        

		          <td width="25" align="left" valign="top"><img src="/static/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
		          <td width="394" align="left" valign="top">
<c:if test="${guruStatus.first}">
		          	<div style="text-align:left; margin-bottom: 7px"><span class="style1"><span class="style2">Sample question(s):  &nbsp; &nbsp;</span></span></div>
</c:if>
<c:choose>
<c:when test="${guruStatus.count eq 1}">
		              <p class="style1">&ldquo;What is a "microprogrammed control unit" and what is the difference between "horizontal" and "vertical" microcode?&rdquo;</p>
</c:when>
<c:otherwise>
		            <p align="left" class="style1">No question available for this expert.</p>
</c:otherwise>
</c:choose>
		            <p><input type="button" value="Email this Engineer" title="Email this Engineer" class="emaillink button" href="/askanexpert/email/display.url?section=Ask an Engineer&sectionid=one&discipline=Computer&disciplineid=Layer11&guru=${guru.name}"/></p>
<c:if test="${not guruStatus.last}">
		            <p class="style1">&nbsp;</p>
		            <p class="style1">&nbsp;</p>
</c:if>
				  </td>
		        </tr>
</c:forEach>
		      </table>
		      <p><br />
		      </p>
		    </div>
	
	
<%-- ********************************************************* --%>
<%-- CHEMICAL LAYER                                            --%>	
<%-- ********************************************************* --%>	
		    <div class="Layer" id="Chemical_Layer" style="display: none;">
		      <p align="left" class="style1"><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, websites, and other resources that can help you answer your question.</span></p>
		
		      <table width="972" border="0" cellspacing="0" cellpadding="0">
<c:forEach items="${actionBean.disciplines['chemical'].gurus}" var="guru" varStatus="guruStatus">
		        <tr>
<c:if test="${guruStatus.first}">
		          <td width="111" align="left" valign="top" rowspan="${fn:length(actionBean.disciplines['chemical'].gurus)}"><img src="/static/images/ae/Chemical.gif" alt="Chemical" width="108" height="128" /></td>
</c:if>
		          <td width="25"><img src="/static/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>

		          <td width="417" align="left" valign="top">
						  <p align="left" class="style1">
						 	<span class="style2"><a href="${guru.searchLink}" title="Search this expert">${guru.name}</a>
						 	</span> <br />
						  </p>
<c:if test="${guru.guruInfo != null && guru.guruInfo != ''}">
						  <p align="left" class="style1">
								${guru.guruInfo}
						  </p><br/>
</c:if> 
						  <p align="left" class="style1">Areas of expertise:
							<c:forEach items="${guru.items}" var="search" varStatus="slCount">
								<a href="${search.href}" title="Search this expertise">${search.name}</a>			          	
								<c:if test="${slCount.count != fn:length(guru.items)}">
									<span>, </span>
								</c:if>
							</c:forEach>
						  </p>
						  <p align="left" class="style1">&nbsp;</p>
				  </td>		        

		          <td width="25" align="left" valign="top"><img src="/static/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
		          <td width="394" align="left" valign="top">
<c:if test="${guruStatus.first}">
		          	<div style="text-align:left; margin-bottom: 7px"><span class="style1"><span class="style2">Sample question(s):  &nbsp; &nbsp;</span></span></div>
</c:if>
<c:choose>
<c:when test="${guruStatus.count eq 1}">
		              <p class="style1">&ldquo;How can wet carbon dioxide be responsible for a corrosion problem in iron-containing metals?&rdquo; </p>
</c:when>
<c:otherwise>
		            <p align="left" class="style1">No question available for this expert.</p>
</c:otherwise>
</c:choose>
		            <p><input type="button" value="Email this Engineer" title="Email this Engineer" class="emaillink button" href="/askanexpert/email/display.url?section=Ask an Engineer&sectionid=one&discipline=Chemical&disciplineid=Layer12&guru=${guru.name}"/></p>
<c:if test="${not guruStatus.last}">
		            <p class="style1">&nbsp;</p>
		            <p class="style1">&nbsp;</p>
</c:if>
		          </td>
		        </tr>
</c:forEach>
		      </table>
		    </div>
</div>