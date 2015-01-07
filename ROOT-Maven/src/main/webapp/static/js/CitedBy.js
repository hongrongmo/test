CitedByManager = {
		eid:"",
		doi:"",
		init: function() {
			if (getParameterByName('SYSTEM_PT') != "" ) return;
		    // The calling jsp must include one or more span elements of class 'citedbyspan'
			// These elements should contain attributes used to call the Scopus web service
			var arr = $("span[name='citedbyspan']");
			if((arr == null) || (arr == undefined) || (arr.length == 0)) {
			  return false;
			}
			if ($("#citedby_box").length == 0) {
				CitedByManager.showCitedByCount(arr);
			}else{
				$("#authordetail_separator").hide();
				CitedByManager.showCitedByDetails(arr);
			}
		},
		showCitedByCount: function(arr){
			
			var queryArray = CitedByManager.buildQueryForCount(arr);
			$.post("/abstract/citedby.url?CID=citedbyAbstract&abstract=t",{citedby:queryArray}, function(data) {
				if((data == null) || (data.result == null) || (data.result == undefined)) {
					  return false;
				}
				for(var i=0;i<data.result.length;i++)
				  {
					  if(data.result[i]!=null && data.result[i]!=undefined)
					  {
						  var id = data.result[i].ID;
						  var eid = data.result[i].EID;
						  var scopusID = data.result[i].SID;
						  var count = data.result[i].COUNT;
						  var dashID=id+"dash";
						  if(count>0) {
							  var linkspan = $("#"+id);
							  if(linkspan && linkspan.length > 0) {
								  var citedbylink = linkspan.find("a.externallink");
								  if (citedbylink && citedbylink.length>0) {}
								  linkspan.show();
								  citedbylink.text('Cited by in Scopus ('+count+')');
								  citedbylink.attr('title','Scopus found '+count+' citation' + (count > 1 ? 's' : '') + ' for this article');
								  citedbylink.attr('href','#');
								  citedbylink.attr('onclick',"GALIBRARY.createWebEvent('External Link','Cited By'); window.open('http://www.scopus.com/scopus/inward/citedby.url?partnerID=qRss3amk&eid="+eid+"','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable'); void('');return false;");
							  }
						  }

					  }
				  }
			},"json");
		},
		showCitedByDetails: function(arr){
			var queryArray = CitedByManager.buildQueryForCount(arr);
			$.post("/abstract/citedby.url?CID=citedbyAbstract&abstract=t&",{citedby:queryArray,authordetail:'yes'}, function(data) {
				
				if(data != null && data.authors != null){
					CitedByManager.populateAuthorDetails(data);
				}
				if(data != null && data.result != null){
					if(data.result[0]!=null && data.result[0]!=undefined)
				       {
				         eid = data.result[0].EID;
				         scopusID = data.result[0].SID;
				         count = data.result[0].COUNT;
				       }

					  // Start populating the citedby box
					  if (eid.length>0) {
						  var citedby_results_wrap = $("#citedby_results_wrap");
						  citedby_results_wrap.show();
						  citedby_results_wrap.find("#citedby_main #countlink")
						  	.attr("href","#")
						  	.attr("onclick","GALIBRARY.createWebEvent('External Link','Cited By'); window.open('http://www.scopus.com/scopus/inward/citedby.url?partnerID=qRss3amk&eid="+eid+"','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable'); void('');return false;")
						  	.attr("title","Scopus found "+count+" citation" + (count > 1 ? "s" : "") + " for this article")
						  	.text(count + " time" + (count>1?"s":""));
					  } else {
						  // Hide cited-by components
					  }

					  if (eid.length>0 || doi.length>0) {
						  // Retrieve more cited-by data using the Scopus EID
					      var params = "eid="+eid+(doi == null ? "" : "::"+doi);
					      $.post("/abstract/citedby.url?CID=citedbyAbstract&abstract=t",{eid:params},CitedByManager.populateCitedByDetails,"json");
					  }
				}
				
				
				
				  
			},"json");
		},
		populateAuthorDetails: function(data){
			if (!data.authors.PROFILE_AUTHOR || !data.authors.PROFILE_AUTHORID) return;
			var authordetail_results = $("#authordetail_results");
			var authors = data.authors.PROFILE_AUTHOR.split(' , ');
			var authorids = data.authors.PROFILE_AUTHORID.split(',');
			for (var i=0; i<authors.length; i++) {
				var authorid= $.trim(authorids[i]);
				var authorpara = $("<p class='authordetail'/>");
				var title = "View author details in Scopus";
				var authorlink = $("<a/>");
				authorlink.attr('title',title);
				authorlink.attr('href','http://www.scopus.com/authid/detail.url?authorId=' + authorid);
				if (i >=3) authorpara.css('display','none').addClass('hiddendetail');
				authorlink.text(authors[i]);
				authorlink.click(function(e) {
					e.preventDefault();
					//add some ga tracking
					GALIBRARY.createWebEventWithLabel('External Link','Author Details', escape($(this).attr('href')));
					window.open($(this).attr('href'),
							'newwindow',
							'width=800,height=600,toolbar=no,location=no,scrollbars,resizable');
				});
				authorpara.append(authorlink);
				authordetail_results.append(authorpara);
			}
			if (i > 3) {
				var togglelink1 = $("<a href='#' class='hiddendetail' style='float:right; position:relative; top:-17px; margin-right:5px' onclick=\"javascript:$('.hiddendetail').toggle()\" title=\"See all authors\">View All Authors</a>");
				var togglelink2 = $("<a href='#' class='hiddendetail' style='float:right; position:relative; top:-17px; margin-right:5px; display:none' onclick=\"javascript:$('.hiddendetail').toggle()\" title=\"Hide authors\">Hide Authors</a>");
				authordetail_results.append(togglelink1).append(togglelink2);
			}
			
			if($("#authordetail_results p.authordetail").length > 0){
				authordetail_results.show();
				$("#citedby_box").show();
			}
		},
		populateCitedByDetails: function(data){
			if ((data == null) || (data.result == null) || (data.result == undefined) || (data.result.length == 0)) {
				return;
			}
			var citedby_results = $("#citedby_results");
			if (citedby_results.length == 0) return;
			citedby_results.show().html('');
			$.each(data.result,function(index, value) {
				// Only show 2 records MAX!  Also stop if value is null/undefined
				if (index > 1 || value == null || value == undefined) return;
				if(value.AUTHOR != null){
					var authors = value.AUTHOR.split(' , ');
					var authorpara = $("<p class='authors'></p>");
					citedby_results.append();
					for (var i=0; i<authors.length; i++) {
						authorpara.append(
							(i>0 ? "; ":"") +
							authors[i]);
					}
					citedby_results.append(authorpara);
				}
				if(value.EID != null && value.TITLE != null ){
					var recordHref = "javascript:newwindow=window.open('http://www.scopus.com/scopus/inward/record.url?partnerID=qRss3amk&eid="
						+ value.EID
						+ "','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable');%20void('');";
					citedby_results.append("<p class='scopustitle'><a title=\"View record in Scopus\" href=\"" + recordHref + "\">" + value.TITLE + "</a></p>");
					if(value.YEAR != null && value.SOURCETITLE != null ){
						citedby_results.append("<p class='sourceinfo'><i>(" + value.YEAR + ") " + value.SOURCETITLE + "</i></p>");
					}
				}
			});
			
			if($('#citedby_results').children().length > 0){
				if($("#authordetail_results p.authordetail").length > 0){
					$("#authordetail_separator").show();
				}
				$("#citedby_box").show();
			}else{
				if($("#authordetail_results p.authordetail").length > 0){
					$("#citedby_box").show();
				}
			}
		},
		buildQueryForCount: function(arr){
			  var queryArray = "";
			  // Iterate over the elements to make one call with all data
			  for(var i=0; i < arr.length; i++)
			  {
				var queryString = "";
			    var tagObj = arr[i];
			    if(tagObj.getAttribute("ISSN")!=null)
			    {
			       queryString = queryString+"\"issn\":\""+tagObj.getAttribute("ISSN")+"\"";
			    }
			    if(tagObj.getAttribute("ISBN")!=null)
			    {
			    	if(queryString.length > 0){queryString += ",";}
			    	queryString=queryString+"\"isbn\":\""+tagObj.getAttribute("ISBN")+"\"";
			    }
			    
			    if(tagObj.getAttribute("ISBN13")!=null)
			    {
			    	if(queryString.length > 0){queryString += ",";}
			    	queryString=queryString+"\"isbn13\":\""+tagObj.getAttribute("ISBN13")+"\"";
			    }
	
			    if(tagObj.getAttribute("DOI")!=null)
			    {
			       doi = tagObj.getAttribute("DOI");
			       if(queryString.length > 0){queryString += ",";}
	
			       queryString=queryString+"\"doi\":\""+tagObj.getAttribute("DOI")+"\"";
			    }
	
			    if(tagObj.getAttribute("PII")!=null)
			    {
			    	if(queryString.length > 0){queryString += ",";}
			    	queryString=queryString+"\"pii\":\""+tagObj.getAttribute("PII")+"\"";
			    }
			    if(tagObj.getAttribute("VOLUME")!=null)
			    {
			    	if(queryString.length > 0){queryString += ",";}
			       queryString=queryString+"\"vol\":\""+tagObj.getAttribute("VOLUME")+"\"";
			    }
			    if(tagObj.getAttribute("ISSUE")!=null)
			    {
			    	if(queryString.length > 0){queryString += ",";}
			    	queryString=queryString+"\"issue\":\""+tagObj.getAttribute("ISSUE")+"\"";
			    }
			    if(tagObj.getAttribute("PAGE")!=null)
			    {
			    	if(queryString.length > 0){queryString += ",";}
			    	queryString=queryString+"\"page\":\""+tagObj.getAttribute("PAGE")+"\"";
			    }
			    if(tagObj.getAttribute("AN")!=null)
			    {
			    	if(queryString.length > 0){queryString += ",";}
			    	queryString=queryString+"\"an\":\""+tagObj.getAttribute("AN")+"\"";
			    }
			    if(tagObj.getAttribute("SECURITY")!=null)
			    {
			    	if(queryString.length > 0){queryString += ",";}
			    	queryString=queryString+"\"security\":\""+tagObj.getAttribute("SECURITY")+"\"";
			    }
			    if(tagObj.getAttribute("SESSION-ID")!=null)
			    {
			      var sessionID = tagObj.getAttribute("SESSION-ID");
			      if(queryString.length > 0){queryString += ",";}
			      if(sessionID.indexOf("_")>-1)
			      {
			        sessionID = sessionID.substring(sessionID.indexOf("_")+1);
			        queryString=queryString+"\"sid\":\""+sessionID+"\"";
			      }else if(sessionID.length > 0) {
			    	queryString=queryString+"\"sid\":\""+sessionID+"\"";
			      }
			    }
			    if(i<arr.length && i >= 1)
			    {
			    	queryArray += ",{" + queryString + "}";
			    }else{
			    	queryArray = "{" + queryString + "}";
			    }
			  }
			  queryArray = "[" + queryArray + "]";
			  return queryArray;
		}
		
};

$(document).ready(function() {
	CitedByManager.init();
});
