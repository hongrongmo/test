// Buttons for an alert
var alertbuttons = [
	{text:"OK", click: function() {
		$(this).dialog("close");
		if (Basket.target && Basket.target != undefined)
			Basket.target.removeAttr("checked");
		}}];

// ****************************************************************************
// This is the Basket object used to update EV's document basket.
// It depends on jQuery library
// ****************************************************************************
Basket = {
	limit: 500,			// Default limit 500
	count: 0,			// Current basket count
	pagesize: 25,		// Current page size
	results: 0,			// Current search results count
	sessionid: "",		// Session ID
	searchid: "",		// Search ID
	searchquery: "",	// Search query
	database: "",		// Database
	docid: new Array,	// Array of document IDs to be added/removed
	handle: new Array,	// Array of document handles to be added/removed

	action: "",			// Basket action - add, remove, etc.
	target: null,

	// Init Basket from page elements.
	// THIS METHOD EXPECTS SPECIFIC DOM ELEMENTS TO BE PRESENT!!
	init: function() {
		this.pagesize = parseInt($("input[name='pagesize']").val());
		this.results = parseInt($("input[name='resultscount']").val());
		this.database = $("input[name='databaseid']").val();
		this.sessionid = $("input[name='sessionid']").val();
		this.searchid = $("input[name='searchid']").val();
		this.searchquery = $("input[name='searchquery']").val();

		this.pageckbx = $("#pageckbx");
		this.cbresults = $("input[name='cbresult']");
		this.pageckbx.prop("checked",(this.cbresults.not(":checked").length==0));
		this.menu = $("#pageselect_menu");
		this.menutoggle = $("#pageselect_toggle");
		this.pagemenuitem = $("#pageselect_menu #Page");
		this.maxmenuitem = $("#pageselect_menu #Maximum");

		this.count = parseInt($("input[name='basketCount']").val());
		this.lblbasketcount = $("#lblbasketcount");
		this.lblbasketcount.val(this.count);
		this.clearbasket = $("#clearbasket");
		this.clearbasketalert = $("input[name='shownewsrchalert']");	// Flag for delete all records
		this.showmaxalert = $("input[name='showmaxalert']");			// Flag for select max, empty basket
		this.showmaxalertclear = $("input[name='showmaxalertclear']");	// Flag for select max, items in basket

		this.dialog = $("#basketdialog");
		this.dialogcheck = false;

		// ====================================================
		// Register various DOM elements for actions
		// ====================================================
		this.pageckbx.click(Basket.domevent);
		this.cbresults.click(Basket.domevent);
		this.maxmenuitem.click(Basket.domevent);
		this.pagemenuitem.click(Basket.domevent);
		this.clearbasket.click(Basket.domevent);

		// ====================================================
		// If we're on the results page, handle other DOM items
		// NOTE: expects "#pageselect_toggle" and "#pageselect_menu" DOM
		//       elements to be present
		// ====================================================

		// Hide certain elements if they are not clicked
		$(document).click(function(e) {
			if (e.target.className.indexOf('pageselect_toggle') < 0) {
				Basket.menu.hide();
				Basket.menutoggle.attr('src', '/static/images/down.jpg');
			}
		});

		// Bind the up/down arrow images to handler
		Basket.menutoggle.click(Basket.showhidemenu);

		// Get basket count via Ajax.  This is needed when the user uses
		// the Back button to return to results and the page cache is used
		if (getParameterByName('SYSTEM_PT') == "" ) {
			$.get("/basket/count.url?_=" + new Date().getTime())
				.error(function(data, status, xhr) {
					if (data.message && data.message != null) alert(message);
					return;
				})
				.success(function(data, status, xhr) {
					if (xhr.getResponseHeader("Content-Type").indexOf('application/json') < 0) {
						// Non-JSON response is most likely session expired!
						document.location = "/system/endsession.url";
					}
					if (data.status == 'error') {
						if (data.message && data.message != null) {
							alert(message);
						}
						return;
					} else {
						$("input[name='basketCount']").val(data.count);
						Basket.count = Number(data.count);
						Basket.lblbasketcount.text(Basket.count);
					}
				});
		}
	},

	ajaxstart: function(event) {
		if (Basket.action == 'maximum') {
			Basket.dialog.html("Processing request...").dialog(Dialog.clearbuttons().addtitle("Alert"));
			Basket.dialog.dialog("open");
		}
		/*
		Basket.pageckbx.attr("disabled","disabled");
		Basket.cbresults.attr("disabled","disabled");
		Basket.menutoggle.off("click");
		*/
	},

	ajaxcomplete: function(event) {


		if(Basket.dialog.data("ui-dialog")){
			Basket.dialog.dialog("close");
		}
		/*
		Basket.pageckbx.removeAttr("disabled");
		Basket.cbresults.removeAttr("disabled");
		Basket.menutoggle.off("click");
		Basket.menutoggle.click(Basket.showhidemenu);
		*/
		// Adding an unload event handler will disable the bfcache
		// for webkit browsers.  This forces the page to re-run js
		// after a back button action!
		$(window).unload(function() {var unload=true;});
	},

	//
	// Show/hide the select menu
	//
	showhidemenu: function(event) {
		var arrow = $(this);
		Basket.menu.css('left',arrow.offset().left-30).css('top',arrow.offset().top+17).toggle();
		if (Basket.menu.is(':visible')) arrow.attr('src', '/static/images/up.jpg');
		else arrow.attr('src', '/static/images/down.jpg');
	},

	//
	// Handle DOM events
	//
	domevent: function(event) {
		Basket.target = $(this);
		if (Basket.target.length == 0) {
			alert("Unable to process this request!");
			return false;
		} else if (Basket.target.attr('type') != 'checkbox') {
			event.preventDefault();
		}

		Basket.docid = new Array();
		Basket.handle = new Array();
		Basket.action = Basket.target.attr('action');
		if (Basket.action == undefined) {
			Basket.action = "docid";
			Basket.docid = [Basket.target.attr('docid')];
			Basket.handle = [Basket.target.attr('handle')];
			if (Basket.target.is(':checked')) Basket.markmessage();
			else Basket.unmark();
		} else if (Basket.action == 'page') {
			// If this is NOT the checkbox then we have to manually toggle it!
			if (Basket.target.attr('type') != 'checkbox') {
				if (Basket.pageckbx.is(":checked")) Basket.pageckbx.removeAttr("checked");
				else Basket.pageckbx.prop("checked","checked");
			}
			// Ensure we have checkboxes to select!
			if (Basket.cbresults.length > 0) {
				if (Basket.pageckbx.is(":checked")) {
					// Only submit unchecked items
					Basket.cbresults.each(function(idx, ele) {
						if (! $(ele).is(':checked')) {
							Basket.docid[Basket.docid.length] = $(ele).attr('docid');
							Basket.handle[Basket.handle.length] = $(ele).attr('handle');
						}
					});
					Basket.markmessage();
				} else {
					// Only submit checked items that match current search
					Basket.cbresults.each(function(idx, ele) {
						Basket.docid[Basket.docid.length] = $(ele).attr('docid');
						Basket.handle[Basket.handle.length] = $(ele).attr('handle');
					});
					Basket.unmark();
				}
			}
		} else if (Basket.action == 'maximum') {
			Basket.markmessage();
		} else if (Basket.action == 'clearonnewsearch') {
			Basket.clear(true);
		}

	},

	//
	// Clear all basket contents
	//
	clear: function(showmessage) {
		var alertbuttons = [
    		{text:"OK", click: function() {
    			$(this).dialog("close");
    			}}];

		// Alert if there are no items in basket
		if (Basket.count == 0) {
			Basket.dialog.html("<p>You must first select one or more records.</p>").dialog(Dialog.addbuttons(alertbuttons).addtitle("Alert"));
			Basket.dialog.dialog("open");
			return false;
		}

		// Show message?
		var doclear = true;
		if (Basket.clearbasketalert.val() == "true" && showmessage) {
			Basket.action = "clearonnewsearch";
			if (Basket.clearbasketalert.val() == "true") {
				// Create some buttons
				var confirmbuttons = [
				{text:"OK", click: function() {
					$(this).dialog("close");
					Basket.togglewarn();
					Basket.clear(false);
					}},
				{text: "Cancel", click: function() {
				    $(this).dialog("close");
				}}];

				var clearmsg = '<p class="p1">Warning: You have chosen to clear your selected records. If you wish to proceed without your selected records, click OK.</p>\n<br/>\n' +
	            '<p><input type="checkbox" id="shownewsrchalert"/>Do not show this message again.</p>\n';

				Basket.dialog.html(clearmsg).dialog(Dialog.addbuttons(confirmbuttons).addtitle("Confirm"));
				Basket.dialog.dialog("open");
				doclear = false;
			}
		}

		// Finally, clear the basket if indicated
		if (doclear) Basket.unmark();

	},

	//
	// Determine if message shown to user for mark actions
	//
	markmessage: function() {
		// Flag that the dialog check is done
		Basket.dialogcheck = true;
		// Flag for continue on to mark() funcion
		var proceed = true;

		// Create some buttons
		var confirmbuttons = [
		{text:"OK", click: function() {
			$(this).dialog("close");
			Basket.togglewarn();
			Basket.mark();
			}},
		{text: "Cancel", click: function() {
		    $(this).dialog("close");
		    Basket.target.removeAttr("checked");
		}}];


		// Contents of dialogs
		var toadd = (Basket.results > Basket.limit ? Basket.limit : Basket.results);

		var maxresults = '<p class="p1"><b>You have reached the ' + Basket.limit + ' Selected Records limit.</b></p>\n<br/>\n' +
            '<p class="p2">Select OK to proceed and automatically select the first ' + Basket.limit + ' records or Cancel if you do not want to continue.</p>\n<br/>\n' +
            '<p><input type="checkbox" id="showmaxalert"/>Do not show this message again.</p>\n';

		var maxresultsclear = '<p class="p1"><b>Your current selected records will be deleted.</b></p>\n<br/>\n' +
	        '<p class="p2">Select OK to proceed and automatically select the first ' + toadd + ' records or Cancel if you do not want to continue.</p>\n<br/>\n' +
	        '<p><input type="checkbox" id="showmaxalertclear"/>Do not show this message again.</p>\n';

		var maxselected = '<p>You have already selected a maximum of ' + Basket.limit + ' records.  You can view all currently selected records by clicking \'Selected Records\'.</p>';
		var maxpageselected = '<p>Selecting the current page would exceed the maximum of ' + Basket.limit + ' records, so no records will be selected.  You can view all currently selected records by clicking \'Selected Records\'.</p>';

		if (Basket.action == "maximum") {
			 if (Basket.count > 0) {
				if (eval(Basket.showmaxalertclear.val())) {
					Basket.dialog.html(maxresultsclear).dialog(Dialog.addbuttons(confirmbuttons).addtitle("Confirm"));
					Basket.dialog.dialog("open");
					proceed = false;
				}
			} else if (Basket.results > Basket.limit) {
				if (eval(Basket.showmaxalert.val())) {
					Basket.dialog.html(maxresults).dialog(Dialog.addbuttons(confirmbuttons).addtitle("Confirm"));
					Basket.dialog.dialog("open");
					proceed = false;
				}
			}
		} else if (Basket.count >= Basket.limit) {
			Basket.dialog.html(maxselected).dialog(Dialog.addbuttons(alertbuttons).addtitle("Alert"));
			Basket.dialog.dialog("open");
			if (Basket.action == "page") Basket.pageckbx.removeAttr("checked");
			Basket.target.removeAttr("checked");
			proceed = false;
		} else if (Basket.action == "page") {
			var notselected= Basket.cbresults.not(":checked").length;
			if(Basket.count + notselected > Basket.limit){
				Basket.dialog.html(maxpageselected).dialog(Dialog.addbuttons(alertbuttons).addtitle("Alert"));
				Basket.dialog.dialog("open");
				Basket.pageckbx.removeAttr("checked");
				proceed = false;
			}
		}

		// Continue on to mark()??
		if (proceed) Basket.mark();
	},

	//
	// Turn off warning message for user - checkbox-driven
	//
	togglewarn: function(event) {
		var doajax = true;
		var param = '';
		if ($('#showmaxalert').is(':checked')) {
			Basket.showmaxalert.val("false");
			param='&alertid=showmaxalert';
		} else if ($('#showmaxalertclear').is(':checked')) {
			Basket.showmaxalertclear.val("false");
			param='&alertid=showmaxalertclear';
		} else if ($('#shownewsrchalert').is(':checked')) {
			Basket.clearbasketalert.val("false");
			param='&alertid=shownewsrchalert';
		} else {
			doajax = false;
		}
		// Ajax call to clear alert state in session
		if (doajax) {
			$.ajax({
				type : "POST",
				cache : false,
				url: "/basket/clearalert.url?CID=basketClearAlert"+param
			});
		}
	},

	//
	// Mark some documents in the basket
	//
	mark: function() {
		// Ensure message function has been run
		if (!Basket.dialogcheck) return false;
		else Basket.dialogcheck = false;

		// Create the URL to mark results
		var markurl = "/basket/mark.url?CID=basketMark" +
			"&database=" + Basket.database +
			"&sessionid=" +Basket.sessionid +
			"&searchquery=" +Basket.searchquery+
			"&searchid=" + Basket.searchid;

		if (Basket.action == "maximum") {
			markurl = markurl + "&max=true";
		}

		// Ajax call to add to basket.  We expect a JSON response in the format:
		// {
		//	  status:"error" | "success",
		//	  message: "Output message (error only),
		//    count : <basket count>
		// }
		$.ajax({
			type : "POST",
			data: {"docid":Basket.docid, "handle":Basket.handle},
			cache : false,
			url : markurl,
			beforeSend: Basket.ajaxstart,
			complete: Basket.ajaxcomplete,
			success : function(data, status, xhr) {
				if (xhr.getResponseHeader("Content-Type").indexOf('application/json') < 0) {
					// Non-JSON response is most likely session expired!
					document.location = "/system/endsession.url";
				}
				if (data.status == 'error') {
					if (data.message && data.message != null) {
						Basket.dialog.html(data.message).dialog(Dialog.addbuttons(alertbuttons).addtitle("Alert"));
						Basket.dialog.dialog("open");
					}
					if (data.count) Basket.count = Number(data.count);
					return;
				} else {
					if (Basket.action == "page") {
						Basket.cbresults.not(":checked").attr("basketid",Basket.searchid);
						Basket.cbresults.prop("checked","checked");
					} else if (Basket.action == "maximum") {
						Basket.cbresults.removeAttr("checked");
						Basket.pageckbx.removeAttr("checked");
						// Only mark boxes if we're <= limit
						Basket.cbresults.each(function(idx,ele) {
							if (Number($(ele).attr("handle")) <= Basket.limit) {
								$(ele).prop("checked","checked").attr("basketid",Basket.searchid);
							}
						});
					} else {
						Basket.target.attr("basketid",Basket.searchid).prop("checked","checked");
					}
					Basket.count = Number(data.count);
					Basket.lblbasketcount.html(Basket.count);
					Basket.pageckbx.prop("checked",(Basket.cbresults.not(":checked").length==0?"checked":null));
					if (data.message) {
						Basket.dialog.html(data.message).dialog(Dialog.addbuttons(alertbuttons).addtitle("Alert"));
						Basket.dialog.dialog("open");
					}
				}
			},
			error : function(data) {
				alert("Unable to process this request!");
			}
		});

	},

	//
	// Remove some documents from the basket.
	//
	unmark: function() {

		// Create the remove URL
		var unmarkurl = "/basket/unmark.url?CID=basketUnmark" +
		/*
			(Basket.docid.length > 0 ? "&docid=" + Basket.docid.join() : "") +
			(Basket.handle.length > 0 ? "&handle=" + Basket.handle.join() : "") +
		*/
			"&database=" + Basket.database +
			"&searchquery=" + Basket.searchquery +
			"&sessionid=" +Basket.sessionid +
			"&searchid=" + Basket.searchid;

		if (Basket.action == "clearonnewsearch") {
			unmarkurl = unmarkurl + "&all=true";
		}
		// Ajax call to remove.  We expect a JSON response in the format:
		// {
		//	  status:"error" | "success",
		//	  message: "Output message (error only),
		//    count : <basket count>
		// }
		$.ajax({
			type : "POST",
			url : unmarkurl,
			data: {"docid":Basket.docid, "handle":Basket.handle},
			beforeSend: Basket.ajaxstart,
			complete: Basket.ajaxcomplete,
			success : function(data, status, xhr) {
				if (xhr.getResponseHeader("Content-Type").indexOf('application/json') < 0) {
					// Non-JSON response is most likely session expired!
					document.location = "/system/endsession.url";
				}
				if (data.status == 'error') {
					if (data.message && data.message != null) {
						alert(message);
					}
					return;
				} else {
					Basket.pageckbx.removeAttr("checked");
					if (Basket.action == "clearonnewsearch") Basket.cbresults.removeAttr("checked").removeAttr("basketid");
					else if (Basket.action == "page") {
						Basket.cbresults.removeAttr("checked").removeAttr("basketid");
					}
					Basket.count = Number(data.count);
					Basket.lblbasketcount.html(Basket.count);
					Basket.pageckbx.prop("checked",(Basket.cbresults.not(":checked").length==0?"checked":null));
				}
			},
			error : function(data) {
				alert("Unable to process this request!");
			}
		});

	}

};

Dialog = {

		autoOpen: false,
		//close: function(event, ui) {Basket.target.removeAttr("checked");},
		modal: true,
		draggable: false,
		resizable: false,
		position: "center",
		width: 400,
		minHeight: 16,
		buttons: this.buttons,
		title: null,
		buttons: null,

		build: function() { return this; },

		clearbuttons: function() {this.buttons = null; return this;},

		addbuttons: function(buttons) {
			this.buttons = new Array();
			this.buttons = buttons;
			return this;
		},

		addtitle: function (title) {
			this.title = title;
			return this;
		}
};

$(document).ready(function() {
	Basket.init();
});