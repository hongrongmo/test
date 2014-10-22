<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
	<script type="text/javascript">
	$(document).ready(function() {
		// Associate evpopup links with popup functionality
		$(".evpopup").click(function(ev){
			window.open(this.href, this.target, 'width=510,height=510, directories=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no');
			ev.preventDefault();
			return false;
		});

		// Search history functions
		$("#historytoggle").click(function(ev) {
			ev.preventDefault();
			$(this)[0].innerHTML = ($("#historyshow").is(":visible") ? "Show" : "Hide");
			$(this)[0].title = ($("#historyshow").is(":visible") ? "Show search history" : "Hide search history" );			
			$("#historyshow").slideToggle("slow");
			$("#historyhide").slideToggle("slow");

			$(this).toggleClass("historyplus");
			$(this).toggleClass("historyminus");
		});
		
		$("#searchsourcestoggle").click(function(ev) {
			ev.preventDefault();
			$(this)[0].innerHTML = ($("#searchSourcesLinks").is(":visible") ? "Show" : "Hide");
			$.cookie('ev_ss_shown', ($("#searchSourcesLinks").is(":visible") ? false : true),  { path: '/' });
			$(this)[0].title = ($("#searchSourcesLinks").is(":visible") ? "Show more search sources" : "Hide more searchs sources" );
			$("#searchSourcesLinks").slideToggle("slow");
			$(this).toggleClass("historyplus");
			$(this).toggleClass("historyminus");
			
		});
		
		if($.cookie("ev_ss_shown") && $.cookie("ev_ss_shown") == "true"){
			$("#searchsourcestoggle").trigger("click");
		}
		
	});
	</script>