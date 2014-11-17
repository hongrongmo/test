/**
 * Simple function to truncate a search query
 * @param searchquery
 * @return
 */
function truncateQuery(searchquery) {
	if (searchquery.length > 300) {
		searchquery = searchquery.substring(0, 300);
		searchquery = searchquery + "...";
	}

	return searchquery;
}

/**
 * This function will be called when you want to add all documents to the selected set and when you
 * want to remove all the documents from selected set in the page .In this two
 * functions will be performed based on selectoption argument.If the selectoption value is markall
 * the documents which are not there in the selected set will be added and when the selectoption value
 * is unmarkall it will delete all the documents corresponding to that page from the selected set.
 */

function selectUnselectAllRecords(selectoption,checkCheckBox) {
	// Build URL to save selections 
	var basketurl = "/engresources/Basket.jsp?"
			+ "select="
			+ selectoption
			+ "&database="
			+ escape(document.forms.resultsform.database.value)
			+ "&resultscount="
			+ escape(document.forms.resultsform.resultscount.value)
			+ "&searchquery="
			+ escape(truncateQuery(document.forms.resultsform.searchquery.value))
			+ "&sessionid="
			+ document.forms.resultsform.sessionid.value
			+ "&searchid="
			+ escape(document.forms.resultsform.searchid.value)
			+ "&timestamp=" + new Date().getTime();
   
	// Add Doc ID, Handle values to url
	var docids=""; 
	var handles="";
	var cbresults = $("input[name='cbresult']");
	$(cbresults.get()).each(function() {
		docids += ($(this).attr('docid')+",");
		handles += ($(this).attr('handle')+",");
	});
 
	$.ajax({
	 	type: "POST",
	    cache: false,
	    dataType: "html",
		url:basketurl,
		data:{docid: docids, handle: handles},
		success: function(data, status, xhr) {
        	if($.trim(data) != null){
        		if('markall' == selectoption || 'markall500' == selectoption){
        			if(checkCheckBox){
        				cbresults.attr('checked', true);
        			}else{
        				cbresults.attr('checked',false);
        			}
        		}else{
        			cbresults.attr('checked',false);
        		}
        	}
		},
		error: function(data) {
			alert("Unable to save your Email alert!");
		}
	});
	
	//new Image().src = basketurl;

}

/*
  This function will be called when you want to delete all documents from the selected set
  in the corresponding page.
 */

function clearAllSelectedRecords(selectvalue, sessionid, resultscount) {
	var cbsize = 0;
	new Image().src = "/engresources/Basket.jsp?select=" + selectvalue
			+ "&sessionid=" + sessionid + "&timestamp=" + new Date().getTime();
	$("input[name='cbresult']").attr('checked', false);
	$("input[name='page']").attr('checked', false);
}

/*
  This function called when you want to add the document to the selected set and when
  you want to delete the document from the selected set.In this two
  functions will be performed based on checkbox checked value.If the checked value is true
  the document will add to the selected set otherwise the document will be removed from
  the selected set.
 */

function selectUnselectRecord(thebox, handle, docid, searchquery, database,
		sessionid, searchid, resultscount) {
	var now = new Date();
	var milli = now.getTime();
	var img = new Image();
	var cbcheck = false;
	
	var basketCount= document.forms.resultsform.basketCount.value;
	//alert("thry");
	if (thebox.checked) {
		//window.alert("entered into adding to the basket");
		if(Number(basketCount)+1 > 500){
			thebox.checked= false;
			alert("You have already selected a maximum of 500 records.  You can view all currently selected records by clicking \'Selected Records\'.");
			
		}
		else{
			document.forms.resultsform.basketCount.value=Number(basketCount)+1;
			var i = new Image();
			i.src = "/engresources/Basket.jsp?" + "select=mark&handle=" + handle
					+ "&docid=" + docid + "&database=" + escape(database)
					+ "&sessionid=" + sessionid + "&searchquery="
					+ escape(searchquery) + "&searchid=" + escape(searchid)
					+ "&timestamp=" + milli;
		}
	} else {
		//window.alert(" deleting from the basket");
		document.forms.resultsform.basketCount.value=basketCount-1;
		var e = new Image();
		e.src = "/engresources/Basket.jsp?" + "select=unmark&handle=" + handle
				+ "&docid=" + docid + "&database=" + escape(database)
				+ "&sessionid=" + sessionid + "&searchquery="
				+ escape(searchquery) + "&searchid=" + escape(searchid)
				+ "&timestamp=" + milli;
	}
}

/*
	Select all results in search
*/
function selectAllResults(resultsform, all) {
	if (all.is(":checked")) {
		// Validate range
		if (resultsform.resultscount.value > 500) {
			var doit = window.confirm("Only the first 500 records will be selected.  Do you want to proceed?");
			if (!doit) return false;
		}
	}
    var dedup = resultsform.DupFlag.value;
    var dbpref = resultsform.dbpref.value;
    var fieldpref = resultsform.fieldpref.value;
    var dbLink = resultsform.dbLink.value;
    var origin = resultsform.origin.value;
    var linkSet = resultsform.linkSet.value;
    var  searchquery = resultsform.searchquery.value;
    var  database = resultsform.database.value;
    var  sessionid = resultsform.sessionid.value;
    var  searchid = resultsform.searchid.value;
    var  pagesize = resultsform.pagesize.value;
    var  resultscount = resultsform.resultscount.value;
    var  tagSearchFlag = resultsform.tagSearchFlag.value;
    var  scope = resultsform.scope.value;
    var  databaseid = resultsform.databaseid.value;
    var  count = resultsform.currentpage.value;
    var  searchtype = resultsform.searchtype.value;
    var displaytype = $(resultsform).find("input[name='selectoption']").val();
    
    document.location = 
    	"/controller/servlet/Controller?EISESSION="+sessionid+
    	"&CID=addSelectedRange&searchid="+searchid+
    	"&searchquery="+searchquery+
    	"&searchtype="+searchtype+
    	"&database="+database+
    	"&databaseid="+databaseid+
    	"&startrange=1&endrange="+(resultscount<=500 ? resultscount : 500)+
    	"&pagesize="+pagesize+
    	"&resultscount="+resultscount+
    	"&count="+count+
    	"&displaytype="+displaytype+
    	"&DupFlag="+dedup+
    	"&dbpref="+dbpref+
    	"&fieldpref="+fieldpref+
    	"&origin="+origin+
    	"&dbLink="+dbLink+
    	"&linkSet="+linkSet+
    	"&tagSearchFlag="+tagSearchFlag+
    	"&scope="+scope;
	return true;
}
