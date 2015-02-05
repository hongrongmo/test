var NTIS = 4;
var INSPEC = 2;
var INSPEC_BACKFILE = 1048576;
var CPX = 1;
var C84 = 33;
var GEO = 8192;
var EUP = 16384;
var UPA = 32768;
var EU_PATENTS = 16384;
var US_PATENTS = 32768;
var US_EU_PATENTS = 49152;
var REFEREX = 131072;
var CBF = 262144;
var CHM = 128;
var PCH = 64;
var ELT = 1024;
var EPT = 2048;
var CBN = 256;
var GRF = 2097152;

var startYear;
var stringYear;
var endYear;
var selectedDbMask = 0;

//
// Used to communicate with the Browse Index popup
//
var lookupWind;
function updateWinds() {
	if (window.lookupWind) {
		if (!window.lookupWind.closed) {
			window.lookupWind.expertchecks();
		}
	}
}
function closeWinds() {
	if (window.lookupWind) {
		window.lookupWind.close();
	}
}

//*************************************************************************
//Initialize the page!
//*************************************************************************
$(document).ready(function() {

	//
	// Handle database checkbox select/deselect
	//
	$(".databasechkbox").click(function(event) {
		var chkbox = $(this);
		// Clear checkboxes appropriately - clicking 'alldb'
		// clears all others, clicking any other clears 'alldb'
		if (chkbox.attr('name') == 'alldb') {
	        $("input[name='database']").removeAttr('checked');
		} else {
	        $("input[name='alldb']").removeAttr('checked');
	    }
		selectedDbMask = calculateMask(document.quicksearch.database);
		
		if($("#saveDBIcon") && $("#saveDBIcon").length > 0){
			//database state has changed.. offer to save it
			if(selectedDbMask != dbSave.mask){
				enableSaveDB();
			}else{
				disableSaveDB();
			}
		}
       
		startYear = document.quicksearch.startYear[document.quicksearch.startYear.selectedIndex].value;
		endYear = calEndYear(selectedDbMask);
		stringYear = document.quicksearch.stringYear.value;

		updateUI(selectedDbMask, startYear, stringYear, endYear);
	});

	//
	// Handle browse index links
	//
	$(".browseindexlookup").click(function(event) {

		event.preventDefault();

		var lookupindex = $(this).attr('lookupindex');
		selectedDbMask = calculateMask(document.quicksearch.database);
	    if(selectedDbMask == 0) return;

	    if ((selectedDbMask == NTIS) && ((lookupindex == 'ST') || (lookupindex == 'PN')))
	    {
	        return;
	    }
	    else
	    {
	        var tabloc="/search/browseindex.url?database="+(selectedDbMask)+"&lookup="+(lookupindex)+"&searchtype=Expert";

         if (!lookupWind || lookupWind.closed)
         {
             lookupWind = window.open(
             tabloc,
             "LookupWin",
             "height=500,width=500,top=0,left="+((screen.width*.35))+",scrollbars=yes,menubar=no,resizable=yes,toolbar=no,location=no,directories=no");
         }
         else
         {
             window.lookupWind.location = tabloc;
             window.lookupWind.focus();
         }
	    }

	});
});


/***************************************************************
 * Calculate the current mask from database checkboxes
 */
function calculateMask(control)
{
    var selectedDbMask = 0;
    
    if(!control){control = document.quicksearch.database;}
    
    // CALCULATE SELECTED DB MASK
   if(document.quicksearch.alldb != null  &&
    			document.quicksearch.alldb.checked == true)
    {
        selectedDbMask = eval(document.quicksearch.alldb.value);
    }
    else if (control != null)
    {
        var chk = control.length;
        for (i = 0; i < chk; i++)
        {
            if(control[i].checked == true)
            {
                selectedDbMask += eval(control[i].value);
            }
        }
        if(typeof(chk) == 'undefined')
        {
            selectedDbMask += eval(control.value);
        }
    }
    return selectedDbMask;
}

/**********************************************************
 * Handle changes to the browse index box
 *
 * @param selectedDbMask
 */
function flipImage(selectedDbMask) {
	var bdiv = $("#browseindexes");

	if (selectedDbMask == REFEREX) {
		bdiv.hide();
		return;
	}

	bdiv.show();
	$(".browseindexli").hide();

	//AUS
	if (selectedDbMask != CBN && selectedDbMask != (CBN + REFEREX)) {
		if ((selectedDbMask & UPA) != UPA && (selectedDbMask & EUP) != EUP
				&& (selectedDbMask & EPT) != EPT) {
			$("#bi_aus").show();
		} else if (selectedDbMask == UPA || selectedDbMask == EUP
				|| selectedDbMask == US_EU_PATENTS || selectedDbMask == EPT
				|| selectedDbMask == (US_EU_PATENTS + EPT)
				|| selectedDbMask == (UPA + REFEREX)
				|| selectedDbMask == (EUP + REFEREX)
				|| selectedDbMask == (US_EU_PATENTS + REFEREX)
				|| selectedDbMask == (EPT + REFEREX)
				|| selectedDbMask == (US_EU_PATENTS + EPT + REFEREX)) {
			$("#bi_inv").show();
		} else {
			$("#bi_ausinv").show();
		}
	}

	//AF
	if (selectedDbMask != CBN && selectedDbMask != (CBN + REFEREX)
			&& (selectedDbMask & INSPEC_BACKFILE) != INSPEC_BACKFILE) {
		if ((selectedDbMask & UPA) != UPA && (selectedDbMask & EUP) != EUP
				&& (selectedDbMask & EPT) != EPT) {
			$("#bi_af").show();
		} else if (selectedDbMask == UPA || selectedDbMask == EUP
				|| selectedDbMask == US_EU_PATENTS || selectedDbMask == EPT
				|| selectedDbMask == (EPT + US_EU_PATENTS)
				|| selectedDbMask == (UPA + REFEREX)
				|| selectedDbMask == (EUP + REFEREX)
				|| selectedDbMask == (US_EU_PATENTS + REFEREX)
				|| selectedDbMask == (EPT + REFEREX)
				|| selectedDbMask == (EPT + US_EU_PATENTS + REFEREX)) {
			$("#bi_asg").show();
		} else {
			$("#bi_afasg").show();
		}
	}

	//CT
	if (((selectedDbMask & CPX) == CPX)
			|| ((selectedDbMask & INSPEC) == INSPEC)
			|| ((selectedDbMask & CBF) == CBF)
			|| ((selectedDbMask & GEO) == GEO)
			|| ((selectedDbMask & GRF) == GRF)
			|| ((selectedDbMask & NTIS) == NTIS)
			|| ((selectedDbMask & CHM) == CHM)
			|| ((selectedDbMask & CBN) == CBN)
			|| ((selectedDbMask & PCH) == PCH)
			|| ((selectedDbMask & ELT) == ELT)
			|| ((selectedDbMask & EPT) == EPT)) {
		$("#bi_cvs").show();
	}

	//LA
	if (((selectedDbMask & CPX) == CPX) || ((selectedDbMask & CBF) == CBF)
			|| ((selectedDbMask & INSPEC) == INSPEC)
			|| ((selectedDbMask & GEO) == GEO)
			|| ((selectedDbMask & GRF) == GRF)
			|| ((selectedDbMask & NTIS) == NTIS)
			|| ((selectedDbMask & PCH) == PCH)) {
		$("#bi_la").show();
	}

	//ST
	if (((selectedDbMask & CPX) == CPX) || ((selectedDbMask & CBF) == CBF)
			|| ((selectedDbMask & GEO) == GEO)
			|| ((selectedDbMask & GRF) == GRF)
			|| ((selectedDbMask & INSPEC) == INSPEC)
			|| ((selectedDbMask & INSPEC_BACKFILE) == INSPEC_BACKFILE)
			|| ((selectedDbMask & CHM) == CHM)
			|| ((selectedDbMask & CBN) == CBN)
			|| ((selectedDbMask & ELT) == ELT)
			|| ((selectedDbMask & PCH) == PCH)) {
		$("#bi_st").show();
	}

	//DT
	if (((selectedDbMask & CPX) == CPX) || ((selectedDbMask & GEO) == GEO)
			|| ((selectedDbMask & GRF) == GRF)
			|| ((selectedDbMask & CBF) == CBF)
			|| ((selectedDbMask & INSPEC) == INSPEC)
			|| ((selectedDbMask & PCH) == PCH)) {
		$("#bi_dt").show();
	}

	//PB
	if (((selectedDbMask & CPX) == CPX) || ((selectedDbMask & CBF) == CBF)
			|| ((selectedDbMask & INSPEC) == INSPEC)
			|| ((selectedDbMask & INSPEC_BACKFILE) == INSPEC_BACKFILE)
			|| ((selectedDbMask & ELT) == ELT)
			|| ((selectedDbMask & GRF) == GRF)
			|| ((selectedDbMask & PCH) == PCH)) {
		$("#bi_pn").show();
	}

	//TR
	if (((selectedDbMask & CPX) == CPX)
			|| ((selectedDbMask & INSPEC) == INSPEC)) {
		$("#bi_tr").show();
	}

	//PC
	if (((selectedDbMask & EPT) == EPT)) {
		$("#bi_pc").show();
	}

	//DI
	if (((selectedDbMask & INSPEC) == INSPEC)) {
		$("#bi_di").show();
	}

	//IPC
	if (((selectedDbMask & INSPEC) == INSPEC)) {
		$("#bi_pid").show();
	}

}

/*********************************************************
 * Get the end year
 *
 * @param selectedDbMask
 * @returns {Number}
 */
function calEndYear(selectedDbMask) {
	if (selectedDbMask == INSPEC_BACKFILE) {
		return 1969;
	} else if (selectedDbMask == CBF) {
		return 1969;
	} else // All others
	{
		//return (new Date()).getFullYear();
		return 2015;
	}
}

/*********************************************************
 * Generate the year parameters
 *
 * @param selectedDbMask
 * @param sYear
 * @param strYear
 * @param eYear
 * @param searchform
 */
function generateYear(selectedDbMask, sYear, strYear, eYear, searchform) {
	var sy = calStartYear(selectedDbMask, strYear);
	var ey = calEndYear(selectedDbMask);
	for (i = searchform.startYear.length; i > 0; i--) {
		searchform.startYear.options[i] = null;
		searchform.endYear.options[i] = null;
	}
	//if(sYear.length > 0)
	//{
	//  var dy = calDisplayYear(selectedDbMask, sYear);
	//}
	//else
	//{
	var dy = calDisplayYear(selectedDbMask, strYear);
	//}
	for (i = 0, j = sy; j <= ey; j++) {
		if (searchform.startYear) {
			searchform.startYear.options[i] = new Option(j, j);
			if (j == dy) {
				searchform.startYear.options[i].selected = true;
			}

			searchform.endYear.options[i] = new Option(j, j);
			if (j == eYear) {
				searchform.endYear.options[i].selected = true;
			}
			i++;
		}
	}
	if (typeof (eYear) == 'undefined') {
		searchform.endYear.options[searchform.endYear.length - 1].selected = true;
	}
}

/*****************************************************************************
 * Default the start year
 *
 * jam - 12/20/2004 Same Bug as quickSearch - this code was cut and pasted
 * from QuickSearchForm.js
  *
 * @param selectedDbMask
 * @param sYear
 * @returns
 */
function calStartYear(selectedDbMask, sYear) {
	// 1969 is arbitrary, but in case all else fails?
	// but we will never be able to get above this default value
	// since we will only overwrite this value if we find one less than
	var dYear = calEndYear(selectedDbMask);

	// jam 12/20/1004 - This is not an else if!
	// compare the start year for each db
	// pick up the earliest start year possible
	if ((selectedDbMask != 0) && ((selectedDbMask & CPX) == CPX)) {
		var cpxStartYear = sYear.substr(sYear.indexOf("CST") + 3, 4);
		dYear = (dYear > cpxStartYear) ? cpxStartYear : dYear;
	}
	if ((selectedDbMask != 0) && ((selectedDbMask & CBF) == CBF)) {
		var cbfStartYear = sYear.substr(sYear.indexOf("ZST") + 3, 4);
		dYear = (dYear > cbfStartYear) ? cbfStartYear : dYear;
	}
	if ((selectedDbMask != 0) && ((selectedDbMask & INSPEC) == INSPEC)) {
		var insStartYear = sYear.substr(sYear.indexOf("IST") + 3, 4);
		dYear = (dYear > insStartYear) ? insStartYear : dYear;
	}
	if ((selectedDbMask != 0)
			&& ((selectedDbMask & INSPEC_BACKFILE) == INSPEC_BACKFILE)) {
		var ibsStartYear = sYear.substr(sYear.indexOf("FST") + 3, 4);
		dYear = (dYear > ibsStartYear) ? ibsStartYear : dYear;
	}
	if ((selectedDbMask != 0) && ((selectedDbMask & NTIS) == NTIS)) {
		var ntiStartYear = sYear.substr(sYear.indexOf("NST") + 3, 4);
		dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
	}
	if ((selectedDbMask != 0) && ((selectedDbMask & GEO) == GEO)) {
		var ntiStartYear = sYear.substr(sYear.indexOf("GST") + 3, 4);
		dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
	}
	if ((selectedDbMask != 0) && ((selectedDbMask & EUP) == EUP)) {
		var ntiStartYear = sYear.substr(sYear.indexOf("EST") + 3, 4);
		dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
	}
	if ((selectedDbMask != 0) && ((selectedDbMask & UPA) == UPA)) {
		var ntiStartYear = sYear.substr(sYear.indexOf("UST") + 3, 4);
		dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
	}
	if ((selectedDbMask != 0) && (selectedDbMask == US_EU_PATENTS)) {
		var ntiStartYear = sYear.substr(sYear.indexOf("UST") + 3, 4);
		dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
	}
	if ((selectedDbMask != 0) && (selectedDbMask == REFEREX)) {
		var paStartYear = sYear.substr(sYear.indexOf("PST") + 3, 4);
		dYear = (dYear > paStartYear) ? paStartYear : dYear;
	}
	if ((selectedDbMask != 0) && ((selectedDbMask & PCH) == PCH)) {
		var pchStartYear = sYear.substr(sYear.indexOf("AST") + 3, 4);
		dYear = (dYear > pchStartYear) ? pchStartYear : dYear;
	}
	if (selectedDbMask != 0 && ((selectedDbMask & CHM) == CHM)) {
		var chmStartYear = sYear.substr(sYear.indexOf("HST") + 3, 4);
		dYear = (dYear > chmStartYear) ? chmStartYear : dYear;
	}
	if (selectedDbMask != 0 && ((selectedDbMask & CBN) == CBN)) {
		var cbnStartYear = sYear.substr(sYear.indexOf("BST") + 3, 4);
		dYear = (dYear > cbnStartYear) ? cbnStartYear : dYear;
	}
	if (selectedDbMask != 0 && ((selectedDbMask & ELT) == ELT)) {
		var eltStartYear = sYear.substr(sYear.indexOf("LST") + 3, 4);
		dYear = (dYear > eltStartYear) ? eltStartYear : dYear;
	}
	if (selectedDbMask != 0 && ((selectedDbMask & EPT) == EPT)) {
		var eptStartYear = sYear.substr(sYear.indexOf("MST") + 3, 4);
		dYear = (dYear > eptStartYear) ? eptStartYear : dYear;
	}
	if ((selectedDbMask != 0) && ((selectedDbMask & GRF) == GRF)) {
		var ntiStartYear = sYear.substr(sYear.indexOf("XST") + 3, 4);
		dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
	}
	return dYear;
}

/**********************************************************
 * Customized selected start year
 * @param selectedDbMask
 * @param sYear
 * @returns
 */
function calDisplayYear(selectedDbMask, sYear) {
	// 2006 since displayed start year could be a very recent value
	// (i.e. An account could have 2000-2006 as their default range)
	// We set this as high as possible and then compare to
	// all possible values and take minimum
	var dYear = calEndYear(selectedDbMask);

	// same as above - not an else if
	// choose theleast of the three when picking selected start year
	if (sYear.length > 4) {
		if ((selectedDbMask != 0) && ((selectedDbMask & CPX) == CPX)) {
			var cpxStartYear = sYear.substr(sYear.indexOf("CSY") + 3, 4);
			dYear = (dYear > cpxStartYear) ? cpxStartYear : dYear;
		}
		if ((selectedDbMask != 0) && ((selectedDbMask & CBF) == CBF)) {
			var cbfStartYear = sYear.substr(sYear.indexOf("ZSY") + 3, 4);
			dYear = (dYear > cbfStartYear) ? cbfStartYear : dYear;
		}
		if ((selectedDbMask != 0) && ((selectedDbMask & INSPEC) == INSPEC)) {
			var insStartYear = sYear.substr(sYear.indexOf("ISY") + 3, 4);
			dYear = (dYear > insStartYear) ? insStartYear : dYear;
		}
		if ((selectedDbMask != 0)
				&& ((selectedDbMask & INSPEC_BACKFILE) == INSPEC_BACKFILE)) {
			var ibsStartYear = sYear.substr(sYear.indexOf("FSY") + 3, 4);
			dYear = (dYear > ibsStartYear) ? ibsStartYear : dYear;
		}
		if ((selectedDbMask != 0) && ((selectedDbMask & NTIS) == NTIS)) {
			var ntiStartYear = sYear.substr(sYear.indexOf("NSY") + 3, 4);
			dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
		}
		if ((selectedDbMask != 0) && ((selectedDbMask & GEO) == GEO)) {
			var ntiStartYear = sYear.substr(sYear.indexOf("GSY") + 3, 4);
			dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
		}
		if ((selectedDbMask != 0) && ((selectedDbMask & EUP) == EUP)) {
			var ntiStartYear = sYear.substr(sYear.indexOf("ESY") + 3, 4);
			dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
		}
		if ((selectedDbMask != 0) && ((selectedDbMask & UPA) == UPA)) {
			var ntiStartYear = sYear.substr(sYear.indexOf("USY") + 3, 4);
			dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
		}
		if ((selectedDbMask != 0) && (selectedDbMask == US_EU_PATENTS)) {
			var ntiStartYear = sYear.substr(sYear.indexOf("USY") + 3, 4);
			dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
		}
		if ((selectedDbMask != 0) && (selectedDbMask == REFEREX)) {
			var paStartYear = sYear.substr(sYear.indexOf("PSY") + 3, 4);
			dYear = (dYear > paStartYear) ? paStartYear : dYear;
		}
		if ((selectedDbMask != 0) && ((selectedDbMask & PCH) == PCH)) {
			var pchStartYear = sYear.substr(sYear.indexOf("AST") + 3, 4);
			dYear = (dYear > pchStartYear) ? pchStartYear : dYear;
		}
		if (selectedDbMask != 0 && ((selectedDbMask & CHM) == CHM)) {
			var chmStartYear = sYear.substr(sYear.indexOf("HST") + 3, 4);
			dYear = (dYear > chmStartYear) ? chmStartYear : dYear;
		}
		if (selectedDbMask != 0 && ((selectedDbMask & CBN) == CBN)) {
			var cbnStartYear = sYear.substr(sYear.indexOf("BST") + 3, 4);
			dYear = (dYear > cbnStartYear) ? cbnStartYear : dYear;
		}
		if (selectedDbMask != 0 && ((selectedDbMask & ELT) == ELT)) {
			var eltStartYear = sYear.substr(sYear.indexOf("LST") + 3, 4);
			dYear = (dYear > eltStartYear) ? eltStartYear : dYear;
		}
		if (selectedDbMask != 0 && ((selectedDbMask & EPT) == EPT)) {
			var eptStartYear = sYear.substr(sYear.indexOf("MST") + 3, 4);
			dYear = (dYear > eptStartYear) ? eptStartYear : dYear;
		}
		if ((selectedDbMask != 0) && ((selectedDbMask & GRF) == GRF)) {
			var ntiStartYear = sYear.substr(sYear.indexOf("XSY") + 3, 4);
			dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
		}
	} else {
		dYear = sYear;
	}

	return dYear;
}

/**********************************************************
 * Update the UI
 *
 * @param dbMask
 * @param sYear
 * @param strYear
 * @param eYear
 * @returns {Boolean}
 */
function updateUI(dbMask, sYear, strYear, eYear) {
	if (!(dbMask == 8) && !(dbMask == 16)) {
		flipImage(dbMask);

		generateYear(dbMask, sYear, strYear, eYear, document.quicksearch);

		startYear = sYear;
		stringYear = strYear;
		endYear = eYear;

		checkLastUpdates();

		return true;
	}
}

var swapCodes = new Array(8211, 8212, 8216, 8217, 8220, 8221);
var swapStrings = new Array("-", "-", "'", "'", "\"", "\"");

/**********************************************************
 * Validate the search form (on submit)
 *
 * @returns {Boolean}
 */
function searchValidation() {
	if (typeof (document.quicksearch.alldb) != 'undefined') {
		if (calculateMask(document.quicksearch.database) == 0) {
			window.alert('Please select a database');
			return false;
		}
	}

	var searchword1 = document.quicksearch.searchWord1.value;
	var length = document.quicksearch.database.length;

	if ((searchword1 == "") || (searchword1 == null)) {
		window.alert("Enter at least one term to search in the database.");
		return false;
	}
	if (!(searchword1 == "")) {

		/* jam 11/10/2004 - now we can start with a *
		if(searchword1.substring(0,1) == '*')
		{
		    window.alert("Search word cannot begin with * character.");
		    return (false);
		}
		 */

		var searchLength = searchword1.length;
		var tempword = searchword1;
		var tempLength = 0;
		while (tempword.substring(0, 1) == ' ') {
			tempword = tempword.substring(1);
			tempLength = tempLength + 1;
		}
		if (searchLength == tempLength) {
			window.alert("Enter at least one term to search in the database.");
			return (false);
		}
		
		var textNodeValue = searchword1;
		//add for remove newline character by hmo-1/15/2015 
		textNodeValue = textNodeValue.replace(/(\r\n|\n|\r)/gm," ");
		for (var j = 0; j < swapCodes.length; j++) {
	        var swapper = new RegExp("\\u" + swapCodes[j].toString(16), "g");
	        textNodeValue = textNodeValue.replace(swapper, swapStrings[j]);
	    }
		document.quicksearch.searchWord1.value = textNodeValue;
	}

	if (document.quicksearch.yearselect
			&& (document.quicksearch.yearselect[0].checked)) {
		var startYear = "0";
		var endYear = "0";

		// no  longer using loops to find selected years! This is less time consuming
		if (document.quicksearch.startYear) {
			startYear = document.quicksearch.startYear[document.quicksearch.startYear.selectedIndex].value;
		}
		if (document.quicksearch.endYear) {
			endYear = document.quicksearch.endYear[document.quicksearch.endYear.selectedIndex].value;
		}

		if (parseInt(startYear) > parseInt(endYear)) {
			window.alert("Start year should be less than or equal to End year");
			return false;
		}
	}
	return true;
}


/***************************************************************
 * Switch year range selected
 * YES this is Expert searcdh but the form still has the NAME quicksearch
 *
 * @param radioidx
 */
function selectYearRange(radioidx) {
	if (typeof (document.quicksearch.yearselect[radioidx]) != 'undefined') {
		if (document.quicksearch.yearselect[radioidx].checked == false) {
			document.quicksearch.yearselect[radioidx].checked = true
		}
	}
}

/***************************************************************
 * Check updates field on change
 *
 * @returns {Boolean}
 */
function checkLastUpdates() {
	var result = true;
	var seldbmask = calculateMask(document.quicksearch.database);
	var alertmsg = "";

	if (document.quicksearch.yearselect[1].checked == true) {
		if (seldbmask == REFEREX) {
			alertmsg = "Last updates selection does not apply to Referex collections.";
			result = false;
		} else if (seldbmask == CBF) {
			alertmsg = "Last updates selection does not apply to Ei Backfile.";
			result = false;
		} else if (seldbmask == IBS) {
			alertmsg = "Last updates selection does not apply to Inspec Archive.";
			result = false;
		} else if (seldbmask == (IBS + CBF)) {
			alertmsg = "Last updates selection does not apply to Ei Backfile and Inspec Archive.";
			result = false;
		} else if (seldbmask == (IBS + REFEREX)) {
			alertmsg = "Last updates selection does not apply to Inspec Archive and Referex collections.";
			result = false;
		} else if (seldbmask == (CBF + REFEREX)) {
			alertmsg = "Last updates selection does not apply to Ei Backfile and Referex collections.";
			result = false;
		} else if (seldbmask == (IBS + CBF + REFEREX)) {
			alertmsg = "Last updates selection does not apply to Ei Backfile, Inspec Archive and Referex collections.";
			result = false;
		}
	}
	if (!result) {
		alert(alertmsg);
		document.quicksearch.yearselect[0].checked = true;
		document.quicksearch.yearselect[0].focus();
	}
	return result;
}

/***************************************************************
 * Reset the search form
 *
 * @param cid
 */
function resetSearchForm(cid) {
	var dataBaseValue = document.quicksearch.resetDataBase.value;
	window.location.href = '/controller/servlet/Controller?CID=' + cid
			+ '&database=' + dataBaseValue;

}
