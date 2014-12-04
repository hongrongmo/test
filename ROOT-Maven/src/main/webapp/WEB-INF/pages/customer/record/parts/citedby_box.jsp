<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

		<div id="citedby_box" class="toolbox shadowbox" style="display:none">
			<p class="title"><span style="padding: 0 7px">Tools in Scopus</span><a href="${actionBean.helpUrl}#Cited_by_count_from_scopus.htm" alt="Cited-by count from Scopus" title="Cited-by count from Scopus" class="helpurl"><img class="infoimg" src="/static/images/i.png" alt="" style="position:relative;" /></a></p>
			
			<div id="citedby_results_wrap" style="display:none">
			<p id="citedby_main" class="double"><b>Cited by: </b> This article has been cited <b><a id="countlink" href="#"></a></b>&nbsp;in Scopus since 1996.</p>
			
			<div id="citedby_results"></div>
			
			<div id="authordetail_separator" class="hr" style="color: #9b9b9b; background-color: #006370; height: 2px; margin: 7px;"><hr/></div>
			</div>
			
			<div id="authordetail_results" style="display:none">
				<p class="double"><b>Author details: </b> View Author Details in Scopus.</p> 
			</div>
						
			<p class="bottom"><span><a title="Learn more (opens in a new window)" href="javascript:newwindow=window.open('http://info.scopus.com','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable');%20void('');">Learn more about Scopus</a></span></p>
			
			<SCRIPT LANGUAGE="Javascript" SRC="/static/js/CitedBy.js?v=4"></script>
			
		</div> 


