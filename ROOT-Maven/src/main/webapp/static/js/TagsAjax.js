// The autocomplete datasource URL - (initial)
var datasource = "/controller/servlet/Controller?CID=tagAutocomplete";

//**************************************************************************
// On document ready, initialize the autocomplete
//
$(document).ready(function() {
	//
	// Handle edit tags (abstract/book tagbubble)
	//
	$("#tageditlink").click(function(event) {
		$("#tagedit_table").toggle();
		event.preventDefault();
	});

	//
	// Handle entries into the text field for autocomplete
	//
	$("#searchgrouptags, #SEARCHID").autocomplete({
		appendTo:"#autocompletewrap",
		search: function(event, ui) { 
			$(this).autocomplete("option", "source",buildDataSource(event));
		}
	});
	
	//
	// Add a tag
	//
	$("#tagbubbleform").submit(function(event) {
		var searchgrouptags = $(this).find("#searchgrouptags")
		if (!searchgrouptags || searchgrouptags.length == 0 || $.trim(searchgrouptags.val()).length == 0) {
			alert("You must enter a value to add a tag.");
			event.preventDefault();
		} 
		if(_gaq){
			
			GALIBRARY.createWebEventWithLabel("Tags and Groups", "Add Tag",searchgrouptags.val());
		}
		var tagsplit = searchgrouptags.val().split(",");
		$.each(tagsplit, function(idx, value) {
			if ($.trim(value).length > 20) {
				alert("You may only enter a maximum of 20 characters for each tag.");
				event.preventDefault();
				searchgrouptags.select();
				return false;
			} 
		});
	});
	
	
	//
	// Validate edit input
	//
	$("#tagedit").submit(function(event) {
		$(".tageditinput").each(function(inputidx, inputvalue) {
			var goon = true;
			var tagsplit = inputvalue.value.split(",");
			$.each(tagsplit, function(idx, value) {
				if ($.trim(value).length > 20) {
					alert("You may only enter a maximum of 20 characters for each tag.");
					event.preventDefault();
					goon = false;
					return false;
				} 
			});
			if (!goon) {
				$(inputvalue).select();
				return goon;
			}
		});
	});
	
	//
	// Code for the tagbubble box on Abstract/Detailed page
	//
	$("p.taglocation #scope").change(function(event) {
		var val = $(this).val();
		if (val == "6") {
			var database = $("#tagbubbleform input[name='database']").val();
			var backurl = $("#tagbubbleform input[name='backurl']").val();
			document.location = "/customer/authenticate/loginfull.url";
			return;
		}
		
		var selected = $(this).find("option:selected");
		if (selected.attr('class') == 'group') $("#gpopup").show();
		else $("#gpopup").hide();
	});
	

});

function buildDataSource(event) {
	// Build the data source URL - barebones sends just the tags content
	var datasource = "/tagsgroups/ajax.url?searchgrouptags="+event.currentTarget.value;
	// If form object is present, use it to find 'scope' value
	var searchtagsform = $("form[name='searchtagsform']");
	if (searchtagsform.length > 0) {
		var scope = searchtagsform.find("[name='scope']");
		if (scope.length > 0) {
			datasource += "&scope=" + scope.val();
		}
	}
	return datasource;
}

//
//Code for the tagbubble box on Abstract/Detailed page
//

var tageditTableBody;
var tageditTable;
var editDiv;
var editInputField;
var editflag = "1";

function flipimg() {
	if (editflag == "1") {
		drawEditTags();
		editflag = "2";
	} else if (editflag == "2") {
		clearEdit();
		editflag = "1";
	}
}

function drawEditTags() {
	initBubbleVars();
	setOffsetEdit();
	drawEdit();
	return false;
}

function initBubbleVars() {
	editDiv = document.getElementById("editfield");
	editInputField = document.getElementById("editfield");
	tageditTableBody = document.getElementById("tagedit_table_body");
	tageditTable = document.getElementById("tagedit_table");
}

function clearEdit() {
	if (tageditTableBody != null) {
		var gi = tageditTableBody.childNodes.length;
		for ( var i = gi - 1; i >= 0; i--) {
			tageditTableBody
					.removeChild(tageditTableBody.childNodes[i]);
		}
		editDiv.style.border = "none";
	}
}

function setOffsetEdit() {
	var gend = editInputField.offsetWidth;
	var gleft = calculateOffsetLeftGroup(editInputField) + gend;
	var gtop = calculateOffsetTopGroup(editInputField);
	editDiv.style.border = "black 0px solid";
	editDiv.style.left = gleft + "px";
	editDiv.style.top = gtop + "px";
	tageditTable.style.width = "160px";
}

function calculateOffsetLeftGroup(gfield) {
	return calculateOffsetGroup(gfield, "offsetLeft");
}
function calculateOffsetTopGroup(gfield) {
	return calculateOffsetGroup(gfield, "offsetTop");
}
function calculateOffsetGroup(gfield, gattr) {
	var goffset = 0;
	while (gfield) {
		goffset += gfield[gattr];
		gfield = gfield.offsetParent;
	}
	return goffset;
}

function addNewTag() {
	if (document.tagbubble.searchgrouptags.value != null) {
		var avalue = document.tagbubble.searchgrouptags.value;
		var arr = avalue.split(",");
		var k = 0;
		for (k = 0; k < arr.length; k++) {
			if (arr[k].length > 30) {
				alert("Tag title should not exceed 30 characters : "
						+ arr[k]);
				return false;
			}
		}
	}

	var url = document.tagbubble.addtagurl.value;
	var tag = encodeURIComponent(document.tagbubble.searchgrouptags.value);
	var scopelength = document.tagbubble.scope.length;
	var tagdoc = document.tagbubble.tagdoc.value;
	var personalization = document.tagbubble.personalization.value;
	var personallogin = document.tagbubble.personallogin.value;
	var nexturl = document.tagbubble.nexturl.value;
	var notifygroup = "off";
	var i = 0;
	var scopevalue = null;
	var groupID = null;

	if (typeof (document.tagbubble.notifygroup) != "undefined") {
		// if a checkbox with no value always has the value of 'on'
		// even if it is not checked - so test if the box is checked first
		if (document.tagbubble.notifygroup.checked) {
			notifygroup = document.tagbubble.notifygroup.value;
		}
	}
	for (i = 0; i < scopelength; i++) {
		if (document.tagbubble.scope[i].selected) {
			scopevalue = document.tagbubble.scope[i].value;
			break;Add
		}
	}
	if (tag == null || tag == "") {
		return false;
	}
	if (personalization == 'true') {
		url = "/controller/servlet/addTagForward?" + url + "&tag=" + tag
				+ "&tagdoc=" + tagdoc + "&notifygroup=" + notifygroup
				+ "&scope=" + scopevalue;
		document.location = url;
	} else {

		nexturl = url + "&tag=" + tag + "&tagdoc=" + tagdoc		
				+ "&notifygroup=" + notifygroup + "&scope="
				+ scopevalue;
		personallogin = personallogin + "&nexturl=" + escape(nexturl)
				+ "&backurl=" + escape(nexturl);

		document.location = personallogin;
	}	
}

function setOffsetsGroup() {
	var ginputField = document.getElementById("scope");
	var gcompleteDiv = document.getElementById("gpopup");

	/*var gend = ginputField.offsetWidth;
	var gleft = calculateOffsetLeftGroup(ginputField)+gend;
	var gtop = calculateOffsetTopGroup(ginputField);
	gcompleteDiv.style.border = "black 0px solid";
	gcompleteDiv.style.left = gleft + "px";
	gcompleteDiv.style.top = gtop + "px";
	//gnameTable.style.width =  "";
	 */
}

function calculateOffsetLeftGroup(gfield) {
	return calculateOffsetGroup(gfield, "offsetLeft");
}

function calculateOffsetTopGroup(gfield) {
	return calculateOffsetGroup(gfield, "offsetTop");
}

function calculateOffsetGroup(gfield, gattr) {
	var goffset = 0;
	while (gfield) {
		goffset += gfield[gattr];
		gfield = gfield.offsetParent;
	}
	return goffset;
}

