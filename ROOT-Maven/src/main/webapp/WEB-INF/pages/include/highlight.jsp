<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<c:choose>
	<c:when test="${actionBean.context.userSession.user.highlightingEnabled  }">
	 	<script>
	 	<c:choose>
		<c:when test="${actionBean.context.userSession.user.userPrefs.highlightBackground}">
			$(document).ready(function(){
				if(!checkHighlightCookie()){
					$(".hit").addClass("bghit");
					$(".bghit").removeClass("hit");
				}

			});
 		</c:when>
 		<c:otherwise>
		$(document).ready(function(){
			checkHighlightCookie();
		});

 		</c:otherwise>
 		</c:choose>
 		function checkHighlightCookie(){
 			var found = false;
			if($.cookie('ev_highlight')){
				found = true;
				var hlOptions = JSON.parse($.cookie("ev_highlight"));
				if(!hlOptions.bg_highlight){
					if($("#ckbackhighlight")){
						$("#ckbackhighlight").prop("checked", false);
					}
					$(".hit").css("color", hlOptions.color);
					$("a span.hit").css("color", "inherit");
				}else{
					$(".hit").addClass("bghit");
					$(".bghit").removeClass("hit");
					$("#ckbackhighlight").prop("checked", true);
				}
				return found;
			}

 		}
	 	</script>
	 		<style>
	 		span.hit, td.hit{
					font-size:100%;
					font-weight:bold;
					color:${actionBean.context.userSession.user.userPrefs.highlight};

				}

				span.bghit, td.bghit {
					font-weight: bold;
					font-size:100%;
					color:black;
					background-color: #FFFFAA;
				}

			a span.hit{
				color:inherit;
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

