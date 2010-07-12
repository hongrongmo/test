function ajaxCitedByFunction()
{
	var arr = new Array();
	arr = document.getElementsByTagName("citedby");
	var queryString = "citedby=";
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
	
	//$.getJSON("http://145.36.192.167/controller/servlet/Controller?CID=CITEDBY_REDIRECT&"+params,populateResults);
	$.getJSON("/engresources/redirect.jsp?"+params,populateResults);

	
	function populateResults(data)
	{
		//alert("count= "+data.result.length);
		for(i=0;i<data.result.length;i++)
		{			
		   var id = data.result[i].ID;

		   var eid = data.result[i].EID;
		   var scopusID = data.result[i].SID;
		   var count = data.result[i].COUNT;

		   if(document.getElementById(id)!=null)
		   {
			//alert("can you see me");
			var countString="Cited by in Scopus("+count+")";
			document.getElementById(id).innerHTML = countString;
			document.getElementById(id).alt="scopus found "+count+" citations for this article";
			document.getElementById(id).href = "javascript:newwindow=window.open('http://www.scopus.com/scopus/inward/citedby.url?partnerID=qRss3amk&eid="+eid+"','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable'); void('');";
			//document.getElementById(id+"Detail").href = document.getElementById(id+"Detail").href+"&eid="+eid+"&count="+count;
		   }
		   else
		   {
			alert("document.getElementById(id) is null");
		   }

		}
	}
	
}