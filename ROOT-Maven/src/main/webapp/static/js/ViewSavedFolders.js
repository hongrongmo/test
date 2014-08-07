/**
 * Run some code at document ready to prepare the
 * search results page for user interaction
 */
$(document).ready(function() {
	/**
	 * Handle the download link
	 */
	//$("#downloadlink").click(function (e) {
//		e.preventDefault();
//		var form = $("#resultsform");
//		var downloadurl = "/delivery/download/display.url?database="+form.find("input[name='database']").val()+
//			"&folderid="+form.find("input[name='folderid']").val()+
//			"&displayformat="+form.find("input[name='selectoption']:checked").val();
//		var new_window = window.open(downloadurl,'NewWindow','status=yes,resizable,scrollbars,width=600,height=600');
//		new_window.focus();
//		return false;
//	});

	/**
	 * Handle the print link
	 */
	$("#printlink").click(function(e) {
		e.preventDefault();
		var form = $("#resultsform");
		var printurl = "/delivery/print/display.url?" +
			"folderid="+form.find("input[name='folderid']").val() +
			"&displayformat="+form.find("input[name='selectoption']:checked").val();

		var new_window = window.open(printurl,'NewWindow','status=yes,resizable,scrollbars,width=700,height=600');
		new_window.focus();
		return false;
	});

	// THIS FUNCTION BASICALLY CONSTRUCT ALL THE REQUIRED PARAMETERS FOR EMAIL.THE VALUES SO CONSTRUCTED ARE SENT TO
	// EMAIL FORM.(emailSelectedRecords,emailSelectedFormatResults.jsp)
	$("#emaillink").click(function(e) {
		e.preventDefault();
		var form = $("#resultsform");
		var emailurl = "/delivery/email/display.url?folderid="+form.find("input[name='folderid']").val()+
			"&database="+form.find("input[name='database']").val()+
			"&displayformat="+form.find("input[name='selectoption']:checked").val();
		var new_window = window.open(emailurl,'NewWindow','status=yes,resizable,scrollbars,width=600,height=600');
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
	 * Handle the update folder link
	 */
	$("#updatefolderlink").click(function(e) {
		e.preventDefault();
		var form = $("#resultsform");
		// Set the CID according to the selectoption element
		var displaytype = form.find("input[name='selectoption']:checked").val();

		if ('citation' == displaytype) {
			displaytype ='viewCitationSavedRecords';
		}else if ('abstract' == displaytype) {
			displaytype ='viewAbstractSavedRecords';
		}else if ('detailed' == displaytype) {
			displaytype ='viewDetailedSavedRecords';
		}

		var viewurl = "/controller/servlet/Controller?CID="+displaytype+
		"&EISESSION="+form.find("input[name='sessionid']").val()+
		"&folderid="+form.find("input[name='folderid']").val()+
		"&database="+form.find("input[name='database']").val()+
		"&backurl="+form.find("input[name='backurl']").val();

		document.location = viewurl;
		return false;
	});

});


function newWindow(url)
{
	var remoteurl=url;
	var NewWindow = window.open(remoteurl,'NewWindow','status=yes,resizable,scrollbars=1,width=400,height=450');
	NewWindow.focus();
}

