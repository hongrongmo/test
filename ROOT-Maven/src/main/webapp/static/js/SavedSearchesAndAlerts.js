$("#AddAlerts").click(function(event) {
	var form = $("#savedSearches");	
	var checked = form.find("input[name='alertcbx'][type='checkbox']:checked").length;
	if(checked == 0){
		alert("You must select a saved search to create an alert.");
		return false;
	}
	return true;
});

$("#DeleteAlerts").click(function(event) {
	var form = $("#savedAlerts");	
	var checked = form.find("input[name='alertcbx'][type='checkbox']:checked").length;
	if(checked == 0){
		alert("You must select an alert to clear.");
		return false;
	}
	return true;
});

function openWindowAddEdit(display, sessionid, queryid, database) {
	var now = new Date();
	var milli = now.getTime();
	var url = null;

	if (display == 'editcclist') {
		url = "/controller/servlet/Controller?EISESSION=" + sessionid
				+ "&CID=editcclist&editdisplay=true&database=" + database
				+ "&savedsearchid=" + queryid + "&timestamp=" + milli;
		NewWindow = window.open(url, 'NewWindow',
				'status=yes,resizable,scrollbars=1,width=450,height=450');
		NewWindow.focus();
	}

}

// wrapper to call to open window test to see if
// someone has clicked on empty spaer gif when email alert is
// not selected (function openWindow is in Header.xsl)
function openCCWindow(display, sessionid, queryid, database) {
	if (typeof (document.images[queryid]) != 'undefined') {
		if (document.images[queryid].src.indexOf("/cc") != -1) {
			openWindowAddEdit(display, sessionid, queryid, database);
		}
	}
}


function confirmClear(type) {
	var confirmMessage='';
	if(type =='savedAlerts'){
		confirmMessage= "Are you sure you want to delete these alerts?";
	}
	
	if(type =='savedSearches'){
		confirmMessage= "Are you sure you want to delete these saved searches?";
	}
	
	if(confirm(confirmMessage) == true){
		return true;
	}
	return false;
	
}
