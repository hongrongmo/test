function ajaxCitedByFunction()
{
  var arr = $("span[name='citedbyspan']");
  if((arr == null) || (arr == undefined) || (arr.length == 0)) {
    return false;
  }
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
    if(i<arr.length-1)
    {
      queryString=queryString+"_";
    }
  }
  params = queryString;

  $.post("/engresources/redirect.jsp",{citedby:params},
      function(data)
      {
        if((data == null) || (data.result == null) || (data.result == undefined)) {
          return false;
        }

        for(i=0;i<data.result.length;i++)
        {
        if(data.result[i]!=null && data.result[i]!=undefined)
        {
          var id = data.result[i].ID;
          var eid = data.result[i].EID;
          var scopusID = data.result[i].SID;
          var count = data.result[i].COUNT;
          var dashID=id+"dash";
            if(count>0)
            {
		  if(document.getElementById(id)!=null && document.getElementById(dashID)!=null)
		  {
		    document.getElementById(dashID).innerHTML = "&#160; - &#160";
		    var countString="Cited by in Scopus ("+count+")";
		    document.getElementById(id).innerHTML = countString;
		    if(count>1)
		    {
		      document.getElementById(id).title="Scopus found "+count+" citations for this article";
		    }
		    else
		    {
		      document.getElementById(id).title="Scopus found "+count+" citation for this article";
		    }
		    document.getElementById(id).href = "javascript:newwindow=window.open('http://www.scopus.com/scopus/inward/citedby.url?partnerID=qRss3amk&eid="+eid+"','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable'); void('');";
		  }
		  else
		  {
		    alert("document.getElementById(id) is null");
		  }
	   }
        }

        }
      },"json");
}