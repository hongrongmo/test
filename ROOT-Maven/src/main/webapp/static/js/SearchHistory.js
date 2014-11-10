//
// Some global variables
//
EMAIL_ALERT_COUNT = 125;
SAVED_SEARCH_COUNT = 125;
combineSearchExample = " e.g., (#1 AND #2) NOT #3";
combineDBMask = 0;

// ****************************************************************************
//
// Start of DOM-ready run items.  Handles various form
// submits, button clicks and other events
//
// ****************************************************************************
$(function() {

	// Save search/alert links
	$(".savesearch").click(handleSaveSearch);
	$(".emailalert").click(handleSaveSearch);

	// Hide certain elements if they are not clicked
	$(document).click(function(e) {
		/*
		if (e.target.className.indexOf('historydetailslink') < 0) {
			$(".historydetails").hide();
		}
		*/
		if (e.target.className.indexOf('combineselect_toggle') < 0 &&
			e.target.className.indexOf('combineselect_action') < 0) {
			$(".combineselect_menu").hide();
			$(".combineselect_toggle").attr('src','/static/images/down.jpg');
		}
	});

	// Submit the combine search
	$("#combineSearchForm").submit(function() {
		return Validate(1);
	});

	// Show/hide history item menu
	$(".combineselect_toggle").click(function(e) {
		e.preventDefault();
		hideMenu($(this).parents('.combineselect_boss').attr('num'), true);
	});

	// Select history item
	$(".combineselect_action").click(handleCombine);
	$(".combineselect_default").click(handleCombine);

	// Show/hide history details links
	$(".historydetailslink").click(function(e) {
		e.preventDefault();
		var offset = $(this).offset();
		var showing = $(this).siblings(".historydetails").is(':hidden');
		$(".historydetails").hide();
		$(this).siblings(".historydetails").css('left',offset.left+87).css('top',offset.top-25).toggle(showing);
	});
	$(".historydetails .close").click(function(e) {
		e.preventDefault();
		$(".historydetails").hide();
	});

	// Jump to history box when indicated
	var jump = getParameterByName("history");
	if (jump && jump.length > 0) {
		var searchhistorybox = $("#searchhistoryboxtop");
		$("html,body").animate({
			scrollTop : searchhistorybox.offset().top
		}, 765);
	}

	// Decorate the combine search text box
	var txtcombine = $("#combineSearchForm input[name='txtcombine']");
	txtcombine.css('color', '#9b9b9b');
	txtcombine.val(combineSearchExample);
	txtcombine.focus(function(e) {
		if ($(this).val() == combineSearchExample) {
			$(this).val('');
			$(this).css('color', 'black');
		}
	});
	// When user enters character in combine textbox, clear the checkbox state
	txtcombine.keydown(function(e) {
		resetCombine();
	});

	// Clear all history items
	$("#clearsearchhistory").click(function(e) {
		e.preventDefault();
		document.location = '/search/history/clear.url?backurl=SEARCHHISTORY';
	});

});

/**
 * Reset the combine text functionality from the checkbox state
 */
function resetCombine() {
	$("ul.combineselect_menu").each(function(idx,item) {
		$(item).empty();
		$(item).append("<li class='combineselect_action' action='start'><span>Start</span></li>\n");
	});
	$(".combineselect_default").removeAttr("checked").attr('action','start');
	$(".combineselect_action").click(handleCombine);
}

/**
 * Add error message
 */
function handleError(message) {
	if (message.constructor == String) {
		$("#searchhistoryboxtop").prepend('<div id="errormessage"><div class="errormessage"><div style="padding-left:15px; padding-bottom:15px; white-space: normal;"><img src="/static/images/red_warning.gif"><b>&nbsp;'+message+'</b></div></div></div>');
	} else {
		$("#errormessage").remove();
	}

}

/**
 * Reset the combine text functionality from the checkbox state
 */
function hideMenu(num, toggle) {
	var parent = $(".combineselect_boss[num='"+num+"']");
	var img = parent.find(".combineselect_toggle");
	var select = img.parent();
	var offset = select.offset();
	var menu = parent.find(".combineselect_menu");
	if (menu && menu != undefined) {
		menu.css('top',offset.top + 24);
		menu.css('left',offset.left);
		if (toggle) menu.toggle();
		else menu.hide();
		if (menu.is(':visible')) img.attr('src','/static/images/up.jpg');
		else img.attr('src','/static/images/down.jpg');
	}
	$(".combineselect_boss").not("[num='"+num+"']").find(".combineselect_menu").hide();
}

/**
 * Reset the combine text functionality from the checkbox state
 */
function handleCombine(event) {

	$("#errormessage").remove();
	// option is a jquery object representing either
	// the link clicked or the checkbox selected
	var combine = $("input[name='txtcombine']");

	var option = $(this);

	var parent = option.parents(".combineselect_boss");
	if (!parent || parent == undefined) return;
	var num = parent.attr('num');
	var menu = parent.find("ul.combineselect_menu");
	var action = option.attr('action');
	var ckbx = parent.find(".combineselect_default");
	var restart = new RegExp("\\s*#"+num+"\\s*(AND|OR|NOT)*\\s*");
	var reconnect = new RegExp("\\s*(AND|OR|NOT)\\s*#"+num+"\\s*");

	hideMenu(num);

	if (action == 'start') {
		// Some search has been selected
		combineDBMask = parent.attr('dbmask');
		menu.empty();
		menu.append("<li class='combineselect_action' num='" + num + "' action='remove'><span>Remove</span></li>\n");
		ckbx.attr('checked','checked');
		ckbx.attr('action','remove');
		// Give all others a menu
		var others = $(".combineselect_boss").not("[num='"+num+"']");
		others.find("ul.combineselect_menu").each(function(idx,item) {
			$(item).empty();
			$(item).append(
				"<li class='combineselect_action' action='and'><span>And</span></li>\n"+
				"<li class='combineselect_action' action='or'><span>Or</span></li>\n"+
				"<li class='combineselect_action' action='not'><span>Not</span></li>\n"
			);
		});
		others.find(".combineselect_default").attr('action','and');
		combine.css('color', 'black');
		combine.val("#"+num);
	} else if (action == 'remove') {
		ckbx.removeAttr('checked');
		var match = combine.val().match(restart);
		if (match == null) return; // Should not happen!
		combine.val($.trim(combine.val().replace(reconnect, "")));
		combine.val($.trim(combine.val().replace(restart, "")));

		combine.val($.trim(combine.val()));
		match = combine.val().match("#\\d");

		if (match == null) {
			// Nothing left, set all to start
			$("ul.combineselect_menu").empty();
			$("ul.combineselect_menu").append("<li class='combineselect_action' num='" + num + "' action='start'><span>Start</span></li>\n");
			$(".combineselect_default").attr('action','start');
			combineDBMask = 0;
		} else {
			// update current
			menu.empty().append(
				"<li class='combineselect_action' action='and'><span>And</span></li>\n"+
				"<li class='combineselect_action' action='or'><span>Or</span></li>\n"+
				"<li class='combineselect_action' action='not'><span>Not</span></li>\n"
			);
			ckbx.attr('action','and');

			// update next item from combine box
			num = match.input[1];
			parent = $(".combineselect_boss[num='"+num+"']");
			menu = parent.find("ul.combineselect_menu");
			menu.empty();
			menu.append("<li class='combineselect_action' action='remove'><span>Remove</span></li>\n");
			ckbx = parent.find(".combineselect_default");
			ckbx.attr('action','remove');
		}
		// if there is a new first item, mark it's checkbox
	} else if (action == 'and' || action == 'or' || action == 'not') {
		// Ensure database(s) match
		var dbmask = parent.attr('dbmask');
		if (combineDBMask != dbmask) {
			handleError("Only searches from the same database can be combined.");
			ckbx.removeAttr('checked');
			var searchhistorybox = $("#searchhistoryboxtop");
			$("html,body").animate({
				scrollTop : searchhistorybox.offset().top
			}, 765);
			event.stopPropagation();
			return;
		}

		// Append to textbox
		var appendme = ' ' + action.toUpperCase() + ' #'+num + ' ';
		if (combine.val().match(reconnect)) {
			combine.val($.trim(combine.val().replace(reconnect, appendme)));
		} else {
			combine.val($.trim(combine.val()) + appendme);
		}
		combine.val($.trim(combine.val()));

		// Mark checkbox
		ckbx.attr('checked','checked');
		ckbx.attr('action','remove');
	}
	// Re-register links with click handler
	$(".combineselect_action").click(handleCombine);

}

/**
 * Handle the save search/alert link click
 */
function handleSaveSearch(event) {
	var savedSeachesAndAlertsLimit = $("#savedSeachesAndAlertsLimit").val();

	var link = $(this);

	var type = link.attr('class');
	var selectvalue = link.attr('selectvalue');
	var num = link.attr('num');
	var savedCount = parseInt($("#savedCount").val());
	var alertCount = parseInt($("#alertCount").val());

	// Check if user is logged in
	var login = link.attr('loggedin');
	if (!eval(login)) {
		return;
	}

	// Ensure they are below limit
	if (selectvalue == 'mark') {
		if ((type == 'savesearch' && (savedCount + 1)>savedSeachesAndAlertsLimit) ||
			(type == 'emailalert' && (alertCount + 1)>savedSeachesAndAlertsLimit)) {
			alert("You may only create "
					+ savedSeachesAndAlertsLimit
					+ " total saved searches and alerts.\nPlease delete a search or alert to create a new one.");
		}
	}

	event.preventDefault();

	// AJAX call to add/delete saved search/alert
	var saveurl = link.attr("href").replace(/selectvalue=\s*(unmark|mark)/,"selectvalue="+selectvalue)+"&ajax=true";
	$.ajax({
		type : "GET",
		cache : false,
		dataType : "html",
		url : saveurl,
		success : function(data, status, xhr) {
			var json = jQuery.parseJSON(data);
			if (json.status == 'error') {
				if (type == 'savesearch') alert("Unable to save your search!");
				if (type == 'emailalert') alert("Unable to save your email alert!");
				return;
			} else if (json.status == 'limit') {
				alert("You may only create "
						+ savedSeachesAndAlertsLimit
						+ " total saved searches and alerts.\nPlease delete a search or alert to create a new one.");
				return;
			} else {
				var savesearchlink = $("#savesearch"+num);
				var emailalertlink = $("#emailalert"+num);
				if (selectvalue == 'mark') {
					$("#savedCount").val(savedCount + 1);
					savesearchlink.text('Remove Search').attr('selectvalue','unmark').attr('title','Remove your saved search query').parent('li').attr('class','savedsearch');
					if (type == 'emailalert') {
						emailalertlink.text('Remove Alert').attr('selectvalue','unmark').attr('title','Remove alert for this search query').parent('li').attr('class','createdalert');
						$("#alertCount").val(alertCount + 1);
					}
				} else {
					if (type == 'savesearch') {
						savesearchlink.text('Save Search').attr('selectvalue','mark').attr('title','Save this search query').parent('li').attr('class','savesearch');
						$("#savedCount").val(savedCount - 1);
						if (emailalertlink.attr('selectvalue') == 'unmark') {
							$("#alertCount").val(alertCount - 1);
							emailalertlink.text('Create Alert').attr('selectvalue','mark').attr('title','Add Email alert for this search query').parent('li').attr('class','createalert');
						}
					} else {
						$("#alertCount").val(alertCount - 1);
						emailalertlink.text('Create Alert').attr('selectvalue','mark').attr('title','Add Email alert for this search query').parent('li').attr('class','createalert');
					}
				}
			}
		},
		error : function(data) {
			if (type == 'savesearch') alert("Unable to save your search!");
			if (type == 'emailalert') alert("Unable to save your email alert!");
		}
	});

}

//
// Validator for the combine searches text field
//
function Validate(numberofsearchs) {
	if (numberofsearchs == 0) {
		window
				.alert("Search history is empty, combine search is not possible.");
		return false;
	}
	var combineString = document.combineSearch.txtcombine.value;

	if (combineString == combineSearchExample) {
		window.alert("Enter values to combine searches.");
		return false;
	}
	if ((combineString == "") || (combineString == null)) {
		window.alert("Enter values to combine searches.");
		return false;
	}
	if (!(combineString == "")) {
		var searchLength = combineString.length;
		var tempword = combineString;
		var tempLength = 0;
		while (tempword.substring(0, 1) == ' ') {
			tempword = tempword.substring(1);
			tempLength = tempLength + 1;
		}
		if (searchLength == tempLength) {
			window.alert("Enter values to combine searches.");
			return (false);
		}
	}
	var searchLength = combineString.length;
	var tempword = combineString;
	var hashindex = tempword.indexOf('#');
	if (hashindex < 0) {
		window
				.alert("Each query number must be preceded by a # sign to combine searches (e.g., #1 AND #2, or combustion AND #2).");
		return false;
	}
	for ( var i = 0; i < searchLength; i++) {
		var hashindex = tempword.indexOf('#');
		if (hashindex >= 0 && tempword.charAt(hashindex + 1) == ' ') {
			window
					.alert("Space is not allowed between # and search query number.");
			return false;
		}
		if (hashindex >= 0 && isNaN(tempword.charAt(hashindex + 1))) {
			window.alert("only number is allowed after #.");
			return false;
		}

		tempword = tempword.substring(1);
		searchLength = tempword.length;
	}
	var lasthashindex = combineString.lastIndexOf('#');
	if (lasthashindex == (combineString.length - 1)) {
		window.alert("Please enter a number after #.");
		return false;
	}
	return true;
}

function getParameterByName(name) {
	var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
	return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
}