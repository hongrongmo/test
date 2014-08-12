<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script>
var highlightV1 = false;

</script>
<c:choose>
	<c:when test="${actionBean.context.userSession.user.getPreference('HIGHLIGHT_V1') && !actionBean.context.userSession.user.userPrefs.highlightBackground}">

	 	<script>highlightV1 = true;</script>
	 		<style>
	 		.hit{
					font-size:120%;
					font-style:italic;
					font-weight:bold;
					color:${actionBean.context.userSession.user.userPrefs.highlight};

				}
				span.bkhit {
					font-weight: bold;
					background-color: #FFFFAA;
				}
			</style>

	</c:when>
	<c:when test="${actionBean.context.userSession.user.userPrefs.highlightBackground}">

	 		<style>
	 		.hit{
					font-size:120%;
					font-style:italic;
					font-weight:bold;
					color:${actionBean.context.userSession.user.userPrefs.highlight};

				}
				span.hit {
					font-weight: bold;
					background-color: #FFFFAA;
				}
			</style>
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
