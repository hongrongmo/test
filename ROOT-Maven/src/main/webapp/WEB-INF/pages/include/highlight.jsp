<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script>
var highlightV1 = false;
var highlightV2 = false;
</script>	
<c:choose>
	<c:when test="${actionBean.context.userSession.user.getPreference('HIGHLIGHT_V1') or actionBean.context.userSession.user.getPreference('HIGHLIGHT_V2')}">
	 <c:choose>
	 	<c:when test="${actionBean.context.userSession.user.getPreference('HIGHLIGHT_V1')}">
	 	<script>highlightV1 = true;</script>
	 		<style>
	 		.hit{
					font-size:120%;
					font-style:italic;
					font-weight:bold;
					color:#148C75;
					
				}
			</style>
	 	</c:when>
	 	<c:when test="${actionBean.context.userSession.user.getPreference('HIGHLIGHT_V2')}">
	 	<script>highlightV2 = true;</script>
	 		<style>
	 		.hit{
					font-size:120%;
					font-style:italic;
					font-weight:bold;
					color:#FF9933;
					
				}
			</style>
	 	</c:when>
	 </c:choose>	
	</c:when>
	<c:when test="${fromResults != 'true'}">
		<style>
			 span.hit {
				font-weight: bold;
				background-color: #FFFFAA;
			}		
		</style>
	</c:when>
	
</c:choose>	
