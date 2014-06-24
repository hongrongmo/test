/**
 * Run some code at document ready to prepare the
 * search results page for user interaction
 */
$(document).ready(function() {
	/**
	 * Handle the download link
	 */
	$("#downloadlink").click(function (e) {
		e.preventDefault();
		var form = $("#resultsform");
		var downloadurl = "/delivery/download/display.url?database="+form.find("input[name='database']").val()+
			"&displayformat="+form.find("input[name='selectoption']:checked").val()+
			"&allselected=true";
		
		var new_window = window.open(downloadurl,'NewWindow','status=yes,resizable,scrollbars,width=600,height=600');
		new_window.focus();
		return false;
	});
	
	$(".pageSizeVal").change(function(e) {
		e.preventDefault();
		var form = $("#gotopage_top");
		var reruncid = form.find("input[name='CID']").val(); 
		var databaseval= form.find("input[name='DATABASEID']").val();
		var searchid= form.find("input[name='SEARCHID']").val();
		var pagesizeval= $(this).find("option:selected").val();
		var databasetype= form.find("input[name='DATABASETYPE']").val();
		var searchtype= form.find("input[name='SEARCHTYPE']").val();
		var searchresults= form.find("input[name='searchresults']").val();
		var newsearch= form.find("input[name='newsearch']").val();
		var backIndex= form.find("input[name='backIndex']").val();
		
		var url="/selected/citation.url";
	
		if(reruncid!=null && reruncid.indexOf("abstract")!=-1){
			url = 	"/selected/abstract.url";
		}else if(reruncid!=null && reruncid.indexOf("citation")!=-1){
			url = 	"/selected/citation.url";
		}else{
			url = 	"/selected/detailed.url";
		}
		
		url= url + "?CID="+reruncid+"&DATABASEID="+databaseval+"&SEARCHID="+searchid+
		"&pageSizeVal="+pagesizeval+"&DATABASETYPE="+databasetype+"&SEARCHTYPE="+searchtype+"&searchresults="+searchresults+"&newsearch="+newsearch+"&backIndex"+backIndex;
   
		window.location.href = url;
	});
	
	/**
	 * Handle the print link
	 */
	$("#printlink").click(function(e) {
		e.preventDefault();
		var form = $("#resultsform");
		var printurl = "/delivery/print/display.url?timestamp="+new Date().getTime()+"&displayformat="+form.find("input[name='selectoption']:checked").val();

		var new_window = window.open(printurl,'NewWindow','status=yes,resizable,scrollbars,width=700,height=400');
		new_window.focus();
		return false;
	});


	// THIS FUNCTION BASICALLY CONSTRUCT ALL THE REQUIRED PARAMETERS FOR EMAIL.THE VALUES SO CONSTRUCTED ARE SENT TO
	// EMAIL FORM.(emailSelectedRecords,emailSelectedFormatResults.jsp)
	$("#emaillink").click(function(e) {
		e.preventDefault();
		var form = $("#resultsform");
		var emailurl = "/delivery/email/display.url" +
			"?database="+form.find("input[name='database']").val()+
			"&displayformat="+form.find("input[name='selectoption']:checked").val()+
			"&timestamp="+new Date().getTime();
		var new_window = window.open(emailurl,'NewWindow','status=yes,resizable,scrollbars,width=600,height=400');
		new_window.focus();
		return false;
	});
	
	// Handle the "Go" button from page navigation
	$("form[name='gotopageform']").submit(function(event) {
		var pagenumber = parseInt($('input[name="BASKETCOUNT"]', this.form).val());
		var pagecount = parseInt($('input[name="pagecount"]', this.form).val());
		var numericExpression = /^[0-9]+$/;
		if (isNaN(pagenumber) || pagenumber < 1 || pagenumber > pagecount) {
			event.preventDefault();
			alert("Please enter a valid page number.");
			return false;
		}
		
		if (!($('input[name="BASKETCOUNT"]', this.form).val()).match(numericExpression)) {
			event.preventDefault();
			alert("Please enter a valid page number.");
			return false;
		}
	});
	
	/**
	 * Handle the view link
	 * 
	 * There is a duplicate of this function in the js file 'SelectedSet.js' called  viewSelectedSetFormat()
	 * The Only Difference is that the viewSelectedSetFormat() function takes one
	 * extra parameter, basketcount, which preserves the current 'page' within the basket
	 * 	 
	 */
	$("#viewlink").click(function(e) {
		e.preventDefault();
		var form = $("#resultsform");
		var viewurl = $(this).attr('href');
		
		// Set the CID according to the selectoption element
		var displaytype = form.find("input[name='selectoption']:checked").val();
		if ('citation' != displaytype) {
			$.each(viewurl.split('?'), function(i, ival) {
				if (i == 0) viewurl = ival;
				else {
					$.each(ival.split('&'), function(j,jval) {
						if (j==0) viewurl += "?"; else viewurl += "&";
						if (jval.indexOf("CID")==0) viewurl += "CID=" + displaytype + 'SelectedSet';
						else viewurl += jval;
					});
				}
			});
		}

		
		document.location = viewurl;
		return false;
	});
	
});
	
//
// Clear selected records on new search function
//
$("#topchkClr").click(function() {
	var img = new Image() ;

	img.src = "/engresources/Basket.jsp?select=clearonnewsearch" + 
		"&sessionid="+document.resultsform.sessionid.value+
		"&clearonvalue="+$(this).is(":checked")+
		"&timestamp="+new Date().getTime();		
});

function newWindow(url)
{
	var remoteurl=url;
	var NewWindow = window.open(remoteurl,'NewWindow','status=yes,resizable,scrollbars=1,width=400,height=450');
	NewWindow.focus();
}

