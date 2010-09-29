function ajaxCitedByFunction1()
{
  var arr = $("span[name='citedbyspan']");
  if((arr == null) || (arr == undefined) || (arr.length == 0)) {
    return false;
  }
  var eid="";
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

  $.post("/engresources/redirect.jsp",{citedby:params},getEIDResults,"json");

  function getEIDResults(data)
  {
    if((data == null) || (data.result == null) || (data.result == undefined))
    {
      return false;
    }
    for(i=0;i<data.result.length;i++)
    {
       if(data.result[i]!=null && data.result[i]!=undefined)
       {
         eid = data.result[i].EID;
         scopusID = data.result[i].SID;
         count = data.result[i].COUNT;
       }
       break;
    }

    if(count>0 && eid.length>0)
    {
      var params = "eid="+eid;
      $.post("/engresources/redirect.jsp",{eid:params},populateResults,"json");
    }
    else
    {
      document.getElementById("citedby_box").innerHTML = "";
    }


    function populateResults(data)
    {
      if((data == null) || (data.result == null) || (data.result == undefined))
      {
        return false;
      }
      var citedby_content="";
      var year="";
      var recordEid;
      for(i=0;i<data.result.length;i++)
      {
         year=data.result[i].YEAR;
         if(data.result[i]!=null && data.result[i]!=undefined)
         {
           recordEid=data.result[i].EID;
           var recordHref="javascript:newwindow=window.open('http://www.scopus.com/scopus/inward/record.url?partnerID=qRss3amk&eid="+recordEid+"','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable');%20void('');";
           if(i>1)
           {
              break;
           }

           citedby_content = citedby_content+" <a class=citedbyBlueLink>"+data.result[i].AUTHOR+"</a>";
           citedby_content = citedby_content+" <br/><a class=citedbyBlueBoldLink href="+recordHref+"><b>"+data.result[i].TITLE+"</b></a>";
           citedby_content = citedby_content+"<br/><a class=italicCitedbyblack>("+year+")";
           citedby_content = citedby_content+" "+data.result[i].SOURCETITLE;
           citedby_content = citedby_content+"</a><br/><br/>";
          }
      }


      var href ="javascript:newwindow=window.open('http://www.scopus.com/scopus/inward/citedby.url?partnerID=qRss3amk&eid="+eid+"','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable');%20void('');";
      var head ="<div style=\"width:215px;\" class=\"t\"><div class=\"b\"><div class=\"l\"><div class=\"rc\"><div class=\"bl\"><div class=\"br\"><div class=\"tl\"><div class=\"trc\"><table border=0 ><tr><td><img src=/engresources/images/s.gif border=0 width=1></td><td valign=bottom padding=0 border=0 margin=0><a class=\"LgBlackText\"><b>Tools in Scopus</b></a></td><td><img src=/engresources/images/s.gif border=0 width=1></td></tr><tr><td colspan=3 align=right valign=top padding=0 border=0 margin=0 height=3><img src=/engresources/tagimages/line.jpg height=3 width=210></td></tr>";
      var preLinkText ="<a class=citedbyRedText><b>Cited by: </b></a><a class=citedbyBlackText>This article has been cited </a>";
      var linkDisplay = "<b>"+count+" times</b>";
      var proLinkText = "<a class=citedbyBlackText>since 1996</a><br/>";
      var hoverText = "Scopus found "+count+" citations for this article";
      if(count==1)
      {
        linkDisplay = "<b>1 time</b> "
        hoverText = "Scopus found "+count+" citation for this article";
      }
      var content = head+"<tr><td><img src=/engresources/images/s.gif border=0 width=1></td><td>"+preLinkText+"<a title ='"+hoverText+"' class=citedbyunderlineLink  href="+href+">"+linkDisplay+"</a><a class=citedbyBlackText> in Scopus </a>"+proLinkText+"</td><td><img src=/engresources/images/s.gif border=0 width=1></td></tr><tr><td colspan=3><img src=/engresources/images/s.gif border=0 width=1></td></tr><tr><td><img src=/engresources/images/s.gif border=0 width=1></td><td><a class=citedbyBlueText>"+citedby_content+"</a></td><td><img src=/engresources/images/s.gif border=0 width=1></td></tr><tr><td colspan=3 valign=top padding=0 border=0 margin=0 height=3><img src=/engresources/tagimages/line.jpg height=3 width=210></td></tr><tr><td><img src=/engresources/images/s.gif border=0 width=1></td><td><a href=\"javascript:newwindow=window.open('http://www.info.scopus.com','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable');%20void('');\" class=smCitedByBlueLink>Learn more about Scopus</a></td><td><img src=/engresources/images/s.gif border=0 width=1></td></tr></table></div></div></div></div></div></div></div></div>";
      document.getElementById("citedby_box").innerHTML = content;

    }
  }
}
