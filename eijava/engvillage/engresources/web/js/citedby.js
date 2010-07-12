function ajaxCitedByFunction1()
{

	var eid="";	
	var arr = new Array();
	arr = document.getElementsByTagName("citedby");
	var queryString = "citedby=";
	var count;
	for(var i=0; i < arr.length; i++)
	{
		var tagObj = arr[i];
		if(tagObj.getAttribute("ISSN")!=null)
		{
		   queryString = queryString+"ISSN:"+tagObj.getAttribute("ISSN")+"|";
		}

		if(tagObj.getAttribute("DOI")!=null)
		{
		   queryString=queryString+"DOI:"+tagObj.getAttribute("DOI")+"|";	
		}

		if(tagObj.getAttribute("PII")!=null)
		{
		   queryString=queryString+"PII:"+tagObj.getAttribute("PII")+"|";
		}
		if(tagObj.getAttribute("VOLUME")!=null)
		{
		   queryString=queryString+"VOLUME:"+tagObj.getAttribute("VOLUME")+"|";
		}
		if(tagObj.getAttribute("ISSUE")!=null)
		{
		   queryString=queryString+"ISSUE:"+tagObj.getAttribute("ISSUE")+"|";
		}
		if(tagObj.getAttribute("PAGE")!=null)
		{
		   queryString=queryString+"PAGE:"+tagObj.getAttribute("PAGE")+"|";
		}
		if(tagObj.getAttribute("AN")!=null)
		{
		   queryString=queryString+"AN:"+tagObj.getAttribute("AN")+"|";	
		}
		if(tagObj.getAttribute("SECURITY")!=null)
		{
			queryString=queryString+"S:"+tagObj.getAttribute("SECURITY")+"|";
		}
		if(tagObj.getAttribute("SESSION-ID")!=null)
		{
			var sessionID = tagObj.getAttribute("SESSION-ID");
			if(sessionID.indexOf("_")>-1)
			{
				sessionID = sessionID.substring(sessionID.indexOf("_")+1);
				queryString=queryString+"SID:"+sessionID+"|";
			}
		}

		queryString=queryString+"_";
	}
	params = queryString;
		
	$.getJSON("/engresources/redirect.jsp?"+params,getEIDResults);
	
	function getEIDResults(data)
	{
		
		for(i=0;i<data.result.length;i++)
		{			
		   eid = data.result[i].EID;
		 
		   scopusID = data.result[i].SID;
		   count = data.result[i].COUNT;
		   break;
		}
		
		if(eid.length>0)
		{
			var params = "eid="+eid;

			$.getJSON("/engresources/redirect.jsp?"+params,populateResults);		

		}
		else
		{
			document.getElementById("citedby_box").innerHTML = "";
		}


		function populateResults(data)
		{

			var citedby_content="";
			var year="";
			for(i=0;i<data.result.length;i++)
			{			  
			   year=data.result[i].YEAR;
			   
			   if(i<2)
			   {
				   citedby_content = citedby_content+" "+data.result[i].AUTHOR;
				   citedby_content = citedby_content+" <br/><b>"+data.result[i].TITLE+"</b>";
				   citedby_content = citedby_content+"<br/>("+data.result[i].YEAR+")";
				   citedby_content = citedby_content+" "+data.result[i].SOURCETITLE;
				   citedby_content = citedby_content+"<br/><br/>";
			   }
			  			  
			}
			
			var href ="javascript:newwindow=window.open('http://www.scopus.com/scopus/inward/citedby.url?partnerID=qRss3amk&eid="+eid+"','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable');%20void('');";
			var head ="<div class=\"b\"><div class=\"l\"><div class=\"rc\"><table border=\"0\" style=\"margin:0px; padding:0px; width:100%\"><tr><td bgcolor=\"#C3C8D1\" align=\"center\" padding=\"0\" border=\"0\" margin=\"0\"><a class=\"MedBlackText\"><font color=\"red\"><b>Cited by in Scopus</b></font></a></td></tr>";
			var linkDisplay = "This article has been cited "+count+" times in Scopus since "+year;
			var hoverText = "scopus found "+count+" citations for this article";
			if(count==1)
			{
				linkDisplay = "This article has been cited 1 time in Scopus since "+year;
				hoverText = "scopus found "+count+" citation for this article";
			}
			var content = head+"<tr><td><a title =\""+hoverText+"\" class=\"MedBlackText\" href="+href+">"+linkDisplay+"</a></td></tr><tr><td bgcolor=\"#F5F5F5\"><a class=\"BlueText\">"+citedby_content+"</a></td></tr><tr><td><a href=\"javascript:newwindow=window.open('http://www.scopus.com','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable');%20void('');\" class=\"BlueText\"><b>Learn more about Scopus</b></a></td></tr></table></div></div></div>";
			document.getElementById("citedby_box").innerHTML = content;
		
		}
	}
}
	