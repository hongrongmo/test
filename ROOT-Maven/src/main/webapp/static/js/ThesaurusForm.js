/*
 * There are 3 HTML forms on the page - the login box in the header (when
 * user is not logged in), the thesaurus search form and the page search
 * form.  When the Enter key is pressed on the form the browser defaults
 * to submitting the first form.  The code below sets the current used form
 * to the parent form of the element selected
 */
var currentForm = $("#thessearchform");  // default to top search form
$(document).ready(function() {
	// Handle # tag changes in the URL
	$(window).bind('hashchange', handleHashChange);
	
	if (window.location.hash == null || window.location.hash == '') {
		window.location.href = "#init";
	}

	// Run these one time at start
	handleHashChange();
	initFormHandling();

	/*
	 * Refresh page when user changes database
	 */
	$("input:radio[name='database']").each(function() {$(this).data('checked',$(this).is(':checked'));});
	$("input:radio[name='database']").click(function() {
		var refresh = true; 
		var txtTrm = $("#txtTrm").val();
		if ($("#clip").find("option").length > 0) {
			refresh = confirm("Your thesaurus terms will be lost. Are you sure you want to change databases?");
		}
		if (refresh) window.location = "/search/thesHome.url?database=" + $(this).val() + (txtTrm.length>0?"&term="+txtTrm:"") + "#init";
		else {
			$("input:radio[name='database']").each(function() {
				if ($(this).data('checked') != undefined) $(this).attr('checked',$(this).data('checked'));
			});
		}
	});

	/*
	 * Associate the submit button with an Ajax function
	 */
	$("#thessearchform").submit(function(event) { 
		event.preventDefault();
		var data = {
			database:$('input[name="database"]:checked', this).val(), 
			term:$( 'input[name="term"]', this).val(), 
			path:$('input[name="path"]', this).val(), 
			snum:0,
			formSubmit:'t'
		};
		submitThesForm(event, data);
	});

});

// Handle changes to the hash (#) values on the URL
var step=0;
var lasthash=null;
function handleHashChange(force) {
	if (lasthash != location.hash || force) {
		lasthash = location.hash;
		
		if (location.hash.indexOf('#init')>=0) {
			// If there is a search id, show the bottom search form
			var searchid = $("input[name='searchid']");
			if (searchid && searchid != null && searchid.val() != '') {
				$(".termsearchfromform").show();
				if ($("#firstslide").is(":hidden")) $("#firstslide").slideDown(600);
			} else {
				// Clear boxes and start over...
				$("#thessearchform")[0].reset();
				$("#termpath").remove();
				$("#termresults").empty().hide();
				$(".termsearchfromform").hide();
			}
			return;
		} else {
			initFromHash(location.hash);
			submitThesSearch(null, location.hash.substring(1));
		}
	}
}
function initFromHash(hash) {
	if (hash == null || hash == undefined) return;
	var hashsplit = hash.split('&');
	// Reset form controls
	var termpath = $("#termpath span[step='0']");
	if (termpath && termpath.length > 0) {
		$("#txtTrm").val(unescape(termpath.text()));
		var st = termpath.attr('scon');
		if (st == 'thesTermSearch') $("input[value='thesTermSearch']").attr('checked',true);
		else if (st == 'thesFullRec') $("input[value='thesFullRec']").attr('checked',true);
		else if (st == 'thesBrowse') $("input[value='thesBrowse']").attr('checked',true);
	}
}

// Initializes form (and link) handling for thesaurus results.  This
// function should be called after EVERY completed request so that
// any new page elements are initialized
function initFormHandling() {
	
	$(".scopenoteimg").mouseover(function() { $(this).attr('src','/static/images/Full-Text_On.png');});
	$(".scopenoteimg").mouseout(function() { $(this).attr('src','/static/images/Full-Text.png');});
	$('form').keydown(function(e) {
		if (e.keyCode == 13) {
			e.preventDefault();
			currentForm.submit();
		}
	});
	$('input').focus(function() {
		currentForm = $(this.form);
	});

	// **********************************************
	// Code for the Ajax pages
	// **********************************************

	// Handle the "Go" button from page navigation
	$("#gotopageform").submit(function(event) {
		event.preventDefault();
		var pagenumber = parseInt($('input[name="pageNumber"]', this.form).val());
		var pagecount = parseInt($('input[name="pagecount"]', this.form).val());
		if (isNaN(pagenumber) || pagenumber < 1 || pagenumber > pagecount) {
			event.preventDefault();
			alert("Please select a valid page number.");
			return false;
		}
		
		window.location.href = 
			'#/search/thes/termsearch.url?snum=' + $('input[name="step"]', this.form).val() +
			'&term='+$('input[name="term"]', this.form).val()+
			'&database='+$('input[name="database"]', this.form).val()+
			'&EISESSION='+$('input[name="EISESSION"]', this.form).val()+
			'&pageNumber='+pagenumber;

	});
	// Handle term links
	//$(".termsearchlink").click(function(event) { submitThesSearch(null, $(this).attr('ajax')); });
	//$(".fullreclink").click(function(event) { submitThesSearch(null, $(this).attr('ajax')); });
	
	// For search results suggestions, make the radio button populate the search box
	$(".termsuggest").click(function(event) {$("#txtTrm").val(this.value); });
		
	// All addtoclipboard checkboxes must populate clipboard
	$(".addtoclipboard").click(function() {
		clearErrors();
		var val = $(this).val();
		var inclip = $("#clip option[value='" + val + "']");
		if (inclip.length > 0) inclip.remove();
		if (this.checked) {
			$("#clip").append("<option value='" + val + "'>" + val + "</option>");
		}
	});

	// Scope note popup
	$(".scopenotelink").click(function(e) {	
		e.preventDefault();
		var database = $("input:radio[name='database']:checked").val();
		if(database == 2097152)
		{
			window.open(this.href,'NewWindow','status=no,resizable,scrollbars=1,width=300,height=400');void('');
		}
		else
		{
			window.open(this.href,'NewWindow','status=no,resizable,scrollbars=1,width=300,height=250');void('');
		}
	});
	
	// Re-mark checkboxes when appropriate
	$(".addtoclipboard").each(function() {
		if ($("#clip option[value='"+this.value+"']").length > 0) this.checked=true;
	});
	
	// Re-bind input elements to their current form for "Enter" key submits
	$('input').focus(function() {
		currentForm = $(this.form);
	});	
	
}

/*
 * Change the form action with the search type
 */
$(".searchtype").click(function() {
	$("#thessearchform input[name='path']").val($(this).attr('action'));
});


/**
 * Submit the form
 * 
 * @param event
 * @return
 */
function submitThesForm(event, data) {
	// Clear any errors on the page
	clearErrors();
    /* Ensure term entered */
    if (!data.term || !$.trim(data.term)) {
    	event.preventDefault();
    	alert("Please enter a term");
    	return;
    }
    
    /* Submit the search */
	window.location.href = '#'+data.path+'snum=0&term='+data.term+'&database='+data.database+'&formSubmit=t';
};

/**
 * Submit a search, browse or full record search.  
 * 
 * @param event The event 
 * @param link The URL for the request.  If the data param is emtpy this param should contain an appropriate query string
 * @param data JSON formatted KV pairs e.g. {database:1, CID: thesTermSearch,...}
 * 
 * @return void
 */
function submitThesSearch(event, link, data) {
    /* stop form from submitting normally */
	//if (event) event.preventDefault();
	
  	$(".termsearchfromform").show();
	var loading = $("#loading").clone().addClass("loading");
	$("#termresults").empty().append(loading).show();
	loading.show();
	if ($("#firstslide").is(":hidden")) $("#firstslide").slideDown(600);

    $.ajax({
        cache: false,
        url: link,
        type: "GET",
        data: data,
        dataType: "html",
        
    	/*
    	 * On success show the results to the user...
    	 */
		success: function(data, status, xhr) {
       			$(".loading").hide();
       			/*
       			 If the response contains an <HTML> tag then this is NOT an ajax 
       			 response!  Note that the better way to do this would be to use 
       			 response headers but the Controller doesn't pass these through!!
       			 */
	        	if (xhr.getResponseHeader('EV_END_SESSION') != null) {
	        		window.location = "/system/endsession.url?SYSTEM_LOGOUT=true";
		        } else {
		        	// Update the results area
      	        	$( "#thesresultswrap" ).empty().append( data );
      	        	// Re-init form elements (in case this is a page refresh)
		        	initFromHash(location.hash);
		        }
    		},
    	complete: function(data, status, xhr) {
	    		$(".loading").hide();
	    		// Re-init form handling
	    		initFormHandling();
	    		//console.log("complete");
	    		// Re-populate the term box
	    		var txtTrm = $('#txtTrm');
	    		if (txtTrm.val().length == 0) {
	    			var term = $('#termpath').find('[step=0]').text();
	    			txtTrm.val(term);
	    		}
	
	    		// Restore the searchtype
	    		var searchtype = $("span[step='0']").attr('scon');
	    		$("input.searchtype[value='"+searchtype+"']").click();
			},
    	error: function() {
    	      $( "#thesresultswrap" ).empty().append( "<div id='termpath'><b>Unable to process this request<b></div><div class='clear'></div><div id='termresults'></div>" );
    	      initFromHash(window.location.hash);
    		}
    });
}

/*
 * Remove from clipboard on delete
 */
$("#clip").keydown( function(e) {
	clearErrors();
	if (e.keyCode == 46) {  // DELETE
		removeterm(e);
	} else if (e.keyCode == 9) { // TAB
		e.preventDefault();
		$("input[value='Search']").focus();
	}
});
$("#removeterm").bind('click', function(e) {
	clearErrors();
	removeterm(e);
});
function removeterm(event) {
	var selected = $("#clip").find("option:selected");
	$(selected).each(function() {
		var chkbox = $("input.addtoclipboard[value='" + $(this).val() + "']");
		if (chkbox.length > 0) chkbox.attr('checked', false);
	});
	selected.remove();
}

/*
 * Submit the thesaurus search when Enter pressed in search field
 */
$("#txtTrm").bind('keypress', function(e) {
	clearErrors();
	if (e.keyCode == 13) {
		submitThesForm(e);
	}
});

/*
 * Reset the form!
 */
$("input[type='reset']").click(function(e) {
	clearErrors();
	this.form.reset();
	$("#clip").empty();
	$("input.addtoclipboard").attr('checked', false);
});

/*
 * Run the search!
 */
$( "#pagesearchform" ).submit(function(e) {
	// Copy the current thesaurus search term.  This is used
	// in case there are zero results, we come back to this form with 
	// this value filled in!
	$(this).find("input[name='thesterm']").val($("#txtTrm").val());
	
	// Setup some variables
	var search = $(this).find("input[name='searchWord1']");
	var clip = $(this).find("#clip");
	var clipoptions = $(this).find("#clip > option");
	var database = $(this).find("input[name='database']").val();
	var andor = $(this).find("input[name='andor']:checked").val();
	
	// REMOVE!!!!!!!!!!!!
	//e.preventDefault();

	// Validate controls
	if ($(this).find("input[name='yearselect']").is(":checked")) {
		if ($(this).find("select[name='startYear']").val() > $("select[name='endYear']").val()) {
			e.preventDefault();
			window.alert("Start year should be less than or equal to End year");
			return false;
		}
	}
	if (clipoptions.length == 0) {
		e.preventDefault();
		alert("Please select at least one term to search in the database. ");
		return false;
	}	
	
	// Formulate search string
	search.val("((");
	for (var i=0; i<clipoptions.length; i++) {
		var term = clipoptions[i].text;
		if (i < clipoptions.length - 1) {
			if (database == 8192) {
				search.val(search.val() + (i>0 ? "(" : "") + "{" + term + "} WN CV OR {" + term + "} WN RGI) " + andor + " ");
			}else if(database == 2048) {
			    if(term.charAt(0)=="-"){
			    	search.val(search.val() + "{" + term + "}  WN CL) " + andor + " (");
			    }else{
				search.val(search.val() + "({" + term + "} WN CV) OR "+ "({" + term + "-A} WN CVA) OR "+ "({" + term + "-N} WN CVN) OR "+ "({" + term + "} WN CVM) OR "+ "({" + term + "-P} WN CVP) OR "+ "({" + term + "-N} WN CVMN) OR "+ "({" + term + "-A} WN CVMA) OR "+ "({" + term + "-P} WN CVMP)) " + andor + " (");
			    }
			} else {
				search.val(search.val() + "({" + term + "}  WN CV) " + andor + " ");
			}
		} else if (i == clipoptions.length - 1) {
			if (database == 8192) {
				search.val(search.val() + (i>0 ? "(" : "") + "{" + term + "} WN CV OR {" + term + "} WN RGI))");
			}else if(database == 2048 || database == 3072 || database == 1024) {
			    if(term.charAt(0)=="-"){
				search.val(search.val() + "{" + term + "} WN CL))");
			    }else{
			    	search.val(search.val() + "({" + term + "} WN CV) OR "+ "({" + term + "-A} WN CVA) OR "+ "({" + term + "-N} WN CVN) OR "+ "({" + term + "} WN CVM) OR "+ "({" + term + "-P} WN CVP) OR "+ "({" + term + "-N} WN CVMN) OR "+ "({" + term + "-A} WN CVMA) OR "+ "({" + term + "-P} WN CVMP)))");
			    }
			} else {
				search.val(search.val() + "({" + term + "} WN CV)))");
			}
		}
	}
	
});

// select year range 1969-1976 for document type is Patent.
function checkPatent(quicksearch) {
	if (quicksearch.doctype.options[quicksearch.doctype.selectedIndex].value == 'PA') {
		quicksearch.startYear.selectedIndex = 0;

		if (typeof (parent.frames[0]) != 'undefined') {
			if (parent.frames[0].document.forms["search"].database[0].checked) {
				var_db = parent.frames[0].document.forms["search"].database[0].value;
			} else {
				var_db = parent.frames[0].document.forms["search"].database[1].value;
			}
		} else {
			var_db = document.forms["clipboard"].database.value;
		}

		if (var_db == '1') // C84 (or CPX - but there is no 'PA' for CPX, only C84)
		{
			quicksearch.endYear.selectedIndex = (quicksearch.endYear.length - ((2006 - 1969) + 1));
		}
		if (var_db == '2') // INSPEC
		{
			quicksearch.endYear.selectedIndex = (quicksearch.endYear.length - ((2006 - 1976) + 1));
		}

		quicksearch.yearselect[0].checked = true
	} else {
		quicksearch.startYear.selectedIndex = 0;
		quicksearch.endYear.selectedIndex = quicksearch.endYear.length - 1;
	}
}

function clearErrors() {
	var ulerrors = $("ul.errors");
	if (ulerrors && ulerrors.length > 0)
		ulerrors.empty();
	var ulerrorslist = $("ul.errorslist");
	if (ulerrorslist && ulerrorslist.length > 0)
		ulerrorslist.empty();
}