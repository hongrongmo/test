var NTIS = 4;
var INSPEC = 2;
var CPX = 1;
var C84 = 33;

var startYear;
var stringYear;
var endYear;
var selectedDbMask = 0;

function flipImage(selectedDbMask)
{
	if((selectedDbMask != 0) && ((selectedDbMask & INSPEC) == INSPEC))
	{
		document.lookuplink1.src="/engresources/images/ath.gif";
		document.lookuplink2.src="/engresources/images/af.gif";
		document.lookuplink3.src="/engresources/images/ct.gif";
		document.lookuplink4.src="/engresources/images/lng.gif";
		document.lookuplink5.src="/engresources/images/st.gif";
		document.lookuplink6.src="/engresources/images/pb.gif";
		document.lookuplink7.src="/engresources/images/tr.gif";
		document.lookuplink8.src="/engresources/images/dt.gif";
		document.lookuplink9.src="/engresources/images/dsc.gif";
	}
	else
	{
		if((selectedDbMask != 0) && ((selectedDbMask & CPX) == CPX))
		{
			document.lookuplink1.src="/engresources/images/ath.gif";
			document.lookuplink2.src="/engresources/images/af.gif";
			document.lookuplink3.src="/engresources/images/ct.gif";
			document.lookuplink4.src="/engresources/images/lng.gif";
			document.lookuplink5.src="/engresources/images/st.gif";
			document.lookuplink6.src="/engresources/images/pb.gif";
			document.lookuplink7.src="/engresources/images/tr.gif";
			document.lookuplink8.src="/engresources/images/dt.gif";
			document.lookuplink9.src="/engresources/images/checking.gif";
		}
		else
		{
			if((selectedDbMask != 0) && ((selectedDbMask & NTIS) == NTIS))
			{
				document.lookuplink1.src="/engresources/images/ath.gif";
				document.lookuplink2.src="/engresources/images/af.gif";
				document.lookuplink3.src="/engresources/images/ct.gif";
				document.lookuplink4.src="/engresources/images/lng.gif";
				document.lookuplink5.src="/engresources/images/checking.gif";
				document.lookuplink6.src="/engresources/images/checking.gif";
				document.lookuplink7.src="/engresources/images/checking.gif";
				document.lookuplink8.src="/engresources/images/checking.gif";
				document.lookuplink9.src="/engresources/images/checking.gif";
			}
		}
	}
}


function clearAlldb()
{
	if(document.quicksearch.alldb.checked)
	{
		document.quicksearch.alldb.checked = false;
	}
}

function clearDatabase()
{
	if(document.quicksearch.alldb.checked)
	{
		for(var i=0; i<document.quicksearch.database.length; i++)
		{
			document.quicksearch.database[i].checked=false;
		}

	}
}

function calculateMask(control)
{
	var selectedDbMask = 0;

	// CALCULATE SELECTED DB MASK
	if(document.quicksearch.alldb.checked == true)
	{
		selectedDbMask = eval(document.quicksearch.alldb.value);
	}
	else
	{
		var chk = control.length;
		for (i = 0; i < chk; i++)
		{
			if(control[i].checked == true)
			{
				selectedDbMask += eval(control[i].value);
			}
		}
	}
	return selectedDbMask;
}

function generateYear(selectedDbMask, sYear, strYear, eYear, searchform)
{
	var sy = calStartYear(selectedDbMask, strYear);
	for (i = searchform.startYear.length; i > 0; i--)
	{
	        searchform.startYear.options[i] = null;
	        searchform.endYear.options[i] = null;
    	}
	//if(sYear.length > 0)
	//{
	//	var dy = calDisplayYear(selectedDbMask, sYear);
	//}
	//else
	//{
		var dy = calDisplayYear(selectedDbMask, strYear);
	//}
	for(i=0,j=sy; j<=2006; j++)
	{
		if(searchform.startYear)
		{
			searchform.startYear.options[i] = new Option(j,j);
			if(j==dy)
			{
				searchform.startYear.options[i].selected = true;
			}

			searchform.endYear.options[i] = new Option(j,j);
			if(j==eYear)
			{
				searchform.endYear.options[i].selected = true;
			}
			i++;
		}
	}
	if(typeof(eYear) == 'undefined')
	{
		searchform.endYear.options[searchform.endYear.length - 1].selected = true;
	}
}

// jam - 12/20/2004 Same Bug as quickSearch - this code was cut and pasted
// from QuickSearchForm.js

// default start year
function calStartYear(selectedDbMask, sYear)
{
	// 1969 is arbitrary, but in case all else fails?
	// but we will never be able to get above this default value
    // since we will only overwrite this value if we find one less than
	var dYear = 1969;

    // jam 12/20/1004 - This is not an else if!
    // compare the start year for each db
    // pick up the earliest start year possible
	if((selectedDbMask != 0) && ((selectedDbMask & CPX) == CPX))
	{
		var cpxStartYear = sYear.substr(sYear.indexOf("CST")+3,4);
		dYear = (dYear > cpxStartYear) ? cpxStartYear : dYear;
	}
	if((selectedDbMask != 0) && ((selectedDbMask & INSPEC) == INSPEC))
	{
		var insStartYear = sYear.substr(sYear.indexOf("IST")+3,4);
		dYear = (dYear > insStartYear) ? insStartYear : dYear;
	}
	if((selectedDbMask != 0) && ((selectedDbMask & NTIS) == NTIS))
	{
		var ntiStartYear = sYear.substr(sYear.indexOf("NST")+3,4);
		dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
	}

	return dYear;
}


// customized selected start year
function calDisplayYear(selectedDbMask, sYear)
{
	// 2006 since displayed start year could be a very recent value
	// (i.e. An account could have 2000-2006 as their default range)
	// We set this as high as possible and then compare to
	// all possible values and take minimum
	var dYear =  2006;

    // same as above - not an else if
    // choose theleast of the three when picking selected start year
	if(sYear.length > 4)
	{
		if((selectedDbMask != 0) && ((selectedDbMask & CPX) == CPX))
		{
            var cpxStartYear = sYear.substr(sYear.indexOf("CSY")+3,4);
			dYear = (dYear > cpxStartYear) ? cpxStartYear : dYear;
		}
		if((selectedDbMask != 0) && ((selectedDbMask & INSPEC) == INSPEC))
		{
            var insStartYear = sYear.substr(sYear.indexOf("ISY")+3,4);
			dYear = (dYear > insStartYear) ? insStartYear : dYear;
		}
		if((selectedDbMask != 0) && ((selectedDbMask & NTIS) == NTIS))
		{
            var ntiStartYear = sYear.substr(sYear.indexOf("NSY")+3,4);
			dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
		}
	}
	else
	{
		dYear = sYear;
	}

	return dYear;
}

function change(db)
{
	if(db == 'alldb')
	{
		clearDatabase();
		selectedDbMask = calculateMask(document.quicksearch.alldb);
	}
	else
	{
		clearAlldb();
		selectedDbMask = calculateMask(document.quicksearch.database);
	}

    startYear=document.quicksearch.startYear[document.quicksearch.startYear.selectedIndex].value;
    endYear="2006";
    stringYear = document.quicksearch.stringYear.value;

	updateUI(selectedDbMask, startYear, stringYear, endYear);

	return true;
}

function updateUI(dbMask, sYear, strYear, eYear)
{
	if(!(dbMask == 8) && !(dbMask == 16))
	{
		flipImage(dbMask);

		generateYear(dbMask, sYear, strYear, eYear, document.quicksearch);

		startYear = sYear;
		stringYear = strYear;
		endYear = eYear;

		return true;
	}
}


function searchValidation()
{
	if(typeof(document.quicksearch.alldb) != 'undefined')
	{
		if(calculateMask(document.quicksearch.database) == 0)
		{
			window.alert('Please select a database');
			return false;
		}
	}

	var searchword1=document.quicksearch.searchWord1.value;
	var length=document.quicksearch.database.length;

	if((searchword1=="") || (searchword1==null))
	{
		window.alert("Enter at least one term to search in the database.");
		return false;
	}
	if(!(searchword1=="")) {

        /* jam 11/10/2004 - now we can start with a *
		if(searchword1.substring(0,1) == '*')
		{
			window.alert("Search word cannot begin with * character.");
			return (false);
		}
        */

		var searchLength= searchword1.length;
		var tempword = searchword1;
		var tempLength=0;
		while (tempword.substring(0,1) == ' ')
		{
			tempword = tempword.substring(1);
			tempLength = tempLength + 1;
		}
		if ( searchLength == tempLength)
		{
			window.alert("Enter at least one term to search in the database.");
			return (false);
		}
	}

    if(document.quicksearch.yearselect && (document.quicksearch.yearselect[0].checked))
	{
		var startYear = "0";
		var endYear = "0";

        // no  longer using loops to find selected years! This is less time consuming
        if(document.quicksearch.startYear)
        {
            startYear=document.quicksearch.startYear[document.quicksearch.startYear.selectedIndex].value;
        }
        if(document.quicksearch.endYear)
        {
            endYear=document.quicksearch.endYear[document.quicksearch.endYear.selectedIndex].value;
        }

		if( parseInt(startYear) > parseInt(endYear))
		{
			window.alert("Start year should be less than or equal to End year");
			return false;
		}
	}
	return true;
}

var lookupWind;
function OpenLookup(sessionId,databaseName,seltab)
{
	if(selectedDbMask == 0)
	{
		selectedDbMask = databaseName;
	}

	if((selectedDbMask == NTIS)
			&&((seltab == 'ST') || (seltab == 'PN') || (seltab == 'TR')|| (seltab == 'DT')|| (seltab == 'DI')))
	{
		return false;
	}
	else if(((selectedDbMask & CPX) == CPX)
			&& (seltab == 'DI')
			&& ((selectedDbMask & INSPEC) != INSPEC))
	{
		return false;
	}
	else
	{
		var tabloc;
		tabloc="/controller/servlet/Controller?EISESSION="+sessionId+"&CID=lookupIndexes&database="+escape(selectedDbMask)+"&lookup="+escape(seltab)+"&searchtype=Expert";

		if (tabloc)
		{
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
		else
		{
			alert("Please select a lookup table");
		}
	}
}

function updateWinds()
{
	if ( window.lookupWind )
	{
		if (!window.lookupWind.closed)
		{
			window.lookupWind.expertchecks();
		}
	}
}

function closeWinds()
{
	if ( window.lookupWind )
	{
		window.lookupWind.close();
	}
}
