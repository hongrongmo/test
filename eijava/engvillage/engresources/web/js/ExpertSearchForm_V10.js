var NTIS = 4;
var INSPEC = 2;
var CPX = 1;
var C84 = 33;
var GEO = 8192;
var EUP = 16384;
var UPA = 32768;
var US_EU_PATENTS = 49152;
var REFEREX = 131072;
var CBF = 262144;
var CHM = 128;
var PCH = 64;
var ELT	= 1024;
var EPT = 2048;
var CBN = 256;

var startYear;
var stringYear;
var endYear;
var selectedDbMask = 0;

  function newLookupLink()
  {
    var alink = document.createElement("a");
    alink.className="MedBluePermLink";
    alink.setAttribute("href","#");

    return alink;
  }

  function OpenLookup_AUS()
  {
    OpenLookup("AUS");
  }
  function OpenLookup_AF()
  {
    OpenLookup("AF");
  }
  function OpenLookup_CVS()
  {
    OpenLookup("CVS");
  }
  function OpenLookup_ST()
  {
    OpenLookup("ST");
  }
  function OpenLookup_PN()
  {
    OpenLookup("PN");
  }
  function OpenLookup_LA()
  {
    OpenLookup("LA");
  }
  function OpenLookup_DT()
  {
    OpenLookup("DT");
  }
  function OpenLookup_PB()
  {
    OpenLookup("PB");
  }
  function OpenLookup_TR()
  {
    OpenLookup("TR");
  }
  function OpenLookup_DSC()
  {
    OpenLookup("DSC");
  }
  function OpenLookup_PC()
  {
    OpenLookup("PC");
  }
  function OpenLookup_DI()
  {
    OpenLookup("DI");
  }

  function flipImage(selectedDbMask)
  {
    var bdiv = document.getElementById("browseindexes");
    var adiv = document.getElementById("lookups");
    var link;

    while(adiv.firstChild) adiv.removeChild(adiv.firstChild);

    var uldiv = document.createElement("ul");
    uldiv.style.listStyleType = "none";
    uldiv.style.margin = "0";
    uldiv.style.padding = "0";
    uldiv.style.marginBottom = "1px";
    adiv.appendChild(uldiv);

    if((selectedDbMask & REFEREX) == REFEREX)
    {
      bdiv.style.display = "none";
      return;
    }

    bdiv.style.display = "block";

    if((selectedDbMask & CBN) != CBN)
    {
      link = newLookupLink();
      link.onclick=OpenLookup_AUS

      if((selectedDbMask & UPA) != UPA &&
        (selectedDbMask & EUP) != EUP &&
        (selectedDbMask & EPT) != EPT)
      {
        link.appendChild(document.createTextNode("Author"));
      }
      else if(selectedDbMask == UPA ||
         selectedDbMask == EUP ||
         selectedDbMask == US_EU_PATENTS ||
         selectedDbMask == EPT)
      {
        link.appendChild(document.createTextNode("Inventor"));
      }
      else
      {
        link.appendChild(document.createTextNode("Author/Inventor"));
      }

      lidiv = document.createElement("li");
      lidiv.appendChild(link);
      uldiv.appendChild(lidiv);
    }


    if((selectedDbMask & CBN) != CBN)
    {
      link = newLookupLink();
      link.onclick=OpenLookup_AF;
       //AF
      if((selectedDbMask & UPA) != UPA &&
        (selectedDbMask & EUP) != EUP &&
        (selectedDbMask & EPT) != EPT )
      {
        link.appendChild(document.createTextNode("Author affiliation"));
      }
      else if(selectedDbMask == UPA ||
        selectedDbMask == EUP ||
        selectedDbMask == US_EU_PATENTS ||
        selectedDbMask == EPT )
      {
        link.appendChild(document.createTextNode("Assignee"));
      }
      else
      {
        link.appendChild(document.createTextNode("Affiliation/Assignee"));
      }

      lidiv = document.createElement("li");
      lidiv.appendChild(link);
      uldiv.appendChild(lidiv);
    }

    //CT
    if (((selectedDbMask & CPX) == CPX) ||
    	((selectedDbMask & INSPEC) == INSPEC) ||
        ((selectedDbMask & CBF) == CBF) ||
    	((selectedDbMask & GEO) == GEO) ||
    	((selectedDbMask & NTIS) == NTIS) ||
    	((selectedDbMask & CHM) == CHM) ||
    	((selectedDbMask & CBN) == CBN) ||
    	((selectedDbMask & PCH) == PCH) ||
    	((selectedDbMask & ELT) == ELT) ||
    	((selectedDbMask & EPT) == EPT))
    {
        link = newLookupLink();
        link.onclick=OpenLookup_CVS;
        link.appendChild(document.createTextNode("Controlled term"));

        lidiv = document.createElement("li");
        lidiv.appendChild(link);
        uldiv.appendChild(lidiv);
    }

    //LA
    if (((selectedDbMask & CPX) == CPX) ||
    	((selectedDbMask & CBF) == CBF) ||
    	((selectedDbMask & INSPEC) == INSPEC) ||
    	((selectedDbMask & GEO) == GEO) ||
    	((selectedDbMask & NTIS) == NTIS) ||
    	((selectedDbMask & PCH) == PCH))
    {
        link = newLookupLink();
        link.onclick=OpenLookup_LA;
        link.appendChild(document.createTextNode("Language"));

        lidiv = document.createElement("li");
        lidiv.appendChild(link);
        uldiv.appendChild(lidiv);
    }

    //ST
    if (((selectedDbMask & CPX) == CPX) ||
    	((selectedDbMask & CBF) == CBF) ||
    	((selectedDbMask & GEO) == GEO) ||
    	((selectedDbMask & INSPEC) == INSPEC) ||
    	((selectedDbMask & CHM) == CHM) ||
    	((selectedDbMask & CBN) == CBN) ||
    	((selectedDbMask & ELT) == ELT) ||
    	((selectedDbMask & PCH) == PCH))
    {
        link = newLookupLink();
        link.onclick=OpenLookup_ST;
        link.appendChild(document.createTextNode("Serial title"));

        lidiv = document.createElement("li");
        lidiv.appendChild(link);
        uldiv.appendChild(lidiv);
    }

    //DT
    if (((selectedDbMask & CPX) == CPX) ||
        ((selectedDbMask & GEO) == GEO) ||
        ((selectedDbMask & CBF) == CBF) ||
        ((selectedDbMask & INSPEC) == INSPEC) ||
        ((selectedDbMask & PCH) == PCH))
    {
        link = newLookupLink();
        link.onclick=OpenLookup_DT;
        link.appendChild(document.createTextNode("Document type"));

        lidiv = document.createElement("li");
        lidiv.appendChild(link);
        uldiv.appendChild(lidiv);
    }

    //PB
    if (((selectedDbMask & CPX) == CPX) ||
    	((selectedDbMask & CBF) == CBF) ||
        ((selectedDbMask & INSPEC) == INSPEC) ||
        ((selectedDbMask & ELT) == ELT) ||
        ((selectedDbMask & PCH) == PCH))
    {
        link = newLookupLink();
        link.onclick=OpenLookup_PN;
        link.appendChild(document.createTextNode("Publisher"));

        lidiv = document.createElement("li");
        lidiv.appendChild(link);
        uldiv.appendChild(lidiv);
    }

    //TR
    if (((selectedDbMask & CPX) == CPX) ||
        ((selectedDbMask & INSPEC) == INSPEC))
    {
        link = newLookupLink();
        link.onclick=OpenLookup_TR;
        link.appendChild(document.createTextNode("Treatment type"));

        lidiv = document.createElement("li");
        lidiv.appendChild(link);
        uldiv.appendChild(lidiv);
    }

    //PC
    if (((selectedDbMask & EPT) == EPT))
    {
        link = newLookupLink();
        link.onclick=OpenLookup_PC;
        link.appendChild(document.createTextNode("Country"));

        lidiv = document.createElement("li");
        lidiv.appendChild(link);
        uldiv.appendChild(lidiv);
    }

    //DI
    if (((selectedDbMask & INSPEC) == INSPEC))
    {
        link = newLookupLink();
        link.onclick=OpenLookup_DI;
        link.appendChild(document.createTextNode("Discipline"));

        lidiv = document.createElement("li");
        lidiv.appendChild(link);
        uldiv.appendChild(lidiv);
    }

    adiv.appendChild(document.createElement("br"));

  }

function calEndYear(selectedDbMask)
{
	if (selectedDbMask != CBF)
	{
 		return 2008;
	}
	else // CBF
	{
 		return 1969;
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

function generateYear(selectedDbMask, sYear, strYear, eYear, searchform)
{
    var sy = calStartYear(selectedDbMask, strYear);
    var ey = calEndYear(selectedDbMask);
    for (i = searchform.startYear.length; i > 0; i--)
    {
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
    for(i=0,j=sy; j<=ey; j++)
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
    var dYear = calEndYear(selectedDbMask);

    // jam 12/20/1004 - This is not an else if!
    // compare the start year for each db
    // pick up the earliest start year possible
    if((selectedDbMask != 0) && ((selectedDbMask & CPX) == CPX))
    {
        var cpxStartYear = sYear.substr(sYear.indexOf("CST")+3,4);
        dYear = (dYear > cpxStartYear) ? cpxStartYear : dYear;
    }
    if((selectedDbMask != 0) && ((selectedDbMask & CBF) == CBF))
    {
        var cbfStartYear = sYear.substr(sYear.indexOf("ZST")+3,4);
        dYear = (dYear > cbfStartYear) ? cbfStartYear : dYear;
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
    if((selectedDbMask != 0) && ((selectedDbMask & GEO) == GEO))
    {
        var ntiStartYear = sYear.substr(sYear.indexOf("GST")+3,4);
        dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
    }
    if((selectedDbMask != 0) && ((selectedDbMask & EUP) == EUP))
    {
        var ntiStartYear = sYear.substr(sYear.indexOf("EST")+3,4);
        dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
    }
    if((selectedDbMask != 0) && ((selectedDbMask & UPA) == UPA))
    {
        var ntiStartYear = sYear.substr(sYear.indexOf("UST")+3,4);
        dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
    }
    if((selectedDbMask != 0) && (selectedDbMask == US_EU_PATENTS ))
    {
        var ntiStartYear = sYear.substr(sYear.indexOf("UST")+3,4);
        dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
    }
    if((selectedDbMask != 0) && (selectedDbMask == REFEREX))
    {
        var paStartYear = sYear.substr(sYear.indexOf("PST")+3,4);
        dYear = (dYear > paStartYear) ? paStartYear : dYear;
    }
    if((selectedDbMask != 0) && ((selectedDbMask & PCH) == PCH))
    {
       var pchStartYear = sYear.substr(sYear.indexOf("AST")+3,4);
       dYear = (dYear > pchStartYear) ? pchStartYear : dYear;
    }
    if (selectedDbMask != 0 && ((selectedDbMask & CHM) == CHM))
    {
       var chmStartYear = sYear.substr(sYear.indexOf("HST") + 3, 4);
       dYear = (dYear > chmStartYear) ? chmStartYear : dYear;
    }
    if (selectedDbMask != 0 && ((selectedDbMask & CBN) == CBN))
    {
       var cbnStartYear = sYear.substr(sYear.indexOf("BST") + 3, 4);
       dYear = (dYear > cbnStartYear) ? cbnStartYear : dYear;
    }
    if (selectedDbMask != 0 && ((selectedDbMask & ELT) == ELT))
    {
       var eltStartYear = sYear.substr(sYear.indexOf("LST") + 3, 4);
       dYear = (dYear > eltStartYear) ? eltStartYear : dYear;
    }
    if (selectedDbMask != 0 && ((selectedDbMask & EPT) == EPT))
    {
       var eptStartYear = sYear.substr(sYear.indexOf("MST") + 3, 4);
       dYear = (dYear > eptStartYear) ? eptStartYear : dYear;
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
    var dYear =  calEndYear(selectedDbMask);

    // same as above - not an else if
    // choose theleast of the three when picking selected start year
    if(sYear.length > 4)
    {
        if((selectedDbMask != 0) && ((selectedDbMask & CPX) == CPX))
        {
            var cpxStartYear = sYear.substr(sYear.indexOf("CSY")+3,4);
            dYear = (dYear > cpxStartYear) ? cpxStartYear : dYear;
        }
        if((selectedDbMask != 0) && ((selectedDbMask & CBF) == CBF))
        {
            var cbfStartYear = sYear.substr(sYear.indexOf("ZSY")+3,4);
            dYear = (dYear > cbfStartYear) ? cbfStartYear : dYear;
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
        if((selectedDbMask != 0) && ((selectedDbMask & GEO) == GEO))
        {
            var ntiStartYear = sYear.substr(sYear.indexOf("GSY")+3,4);
            dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
        }
        if((selectedDbMask != 0) && ((selectedDbMask & EUP) == EUP))
        {
            var ntiStartYear = sYear.substr(sYear.indexOf("ESY")+3,4);
            dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
        }
        if((selectedDbMask != 0) && ((selectedDbMask & UPA) == UPA))
        {
            var ntiStartYear = sYear.substr(sYear.indexOf("USY")+3,4);
            dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
        }
        if((selectedDbMask != 0) && (selectedDbMask == US_EU_PATENTS))
        {
            var ntiStartYear = sYear.substr(sYear.indexOf("USY")+3,4);
            dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
        }
        if((selectedDbMask != 0) && (selectedDbMask == REFEREX))
        {
            var paStartYear = sYear.substr(sYear.indexOf("PSY")+3,4);
            dYear = (dYear > paStartYear) ? paStartYear : dYear;
        }
        if((selectedDbMask != 0) && ((selectedDbMask & PCH) == PCH))
        {
            var pchStartYear = sYear.substr(sYear.indexOf("AST")+3,4);
            dYear = (dYear > pchStartYear) ? pchStartYear : dYear;
        }
        if (selectedDbMask != 0 && ((selectedDbMask & CHM) == CHM))
        {
            var chmStartYear = sYear.substr(sYear.indexOf("HST") + 3, 4);
            dYear = (dYear > chmStartYear) ? chmStartYear : dYear;
        }
        if (selectedDbMask != 0 && ((selectedDbMask & CBN) == CBN))
        {
            var cbnStartYear = sYear.substr(sYear.indexOf("BST") + 3, 4);
            dYear = (dYear > cbnStartYear) ? cbnStartYear : dYear;
        }
        if (selectedDbMask != 0 && ((selectedDbMask & ELT) == ELT))
        {
            var eltStartYear = sYear.substr(sYear.indexOf("LST") + 3, 4);
            dYear = (dYear > eltStartYear) ? eltStartYear : dYear;
        }
        if (selectedDbMask != 0 && ((selectedDbMask & EPT) == EPT))
        {
          var eptStartYear = sYear.substr(sYear.indexOf("MST") + 3, 4);
          dYear = (dYear > eptStartYear) ? eptStartYear : dYear;
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
    endYear=calEndYear(selectedDbMask);
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

        checkLastUpdates();

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

function OpenLookup(sessionId,lookupindex)
{
    selectedDbMask = calculateMask(document.quicksearch.database);

    if(selectedDbMask == 0)
    {
        return;
    }

    if((selectedDbMask == NTIS)
            &&((lookupindex == 'ST') || (lookupindex == 'PN') || (lookupindex == 'TR')|| (lookupindex == 'DT')|| (lookupindex == 'DI')))
    {
        return false;
    }
    else if((((selectedDbMask & CPX) == CPX)||((selectedDbMask & CBF) == CBF))
            && (lookupindex == 'DI')
            && ((selectedDbMask & INSPEC) != INSPEC))
    {
        return false;
    }
    else
    {
        var tabloc;
        tabloc="/controller/servlet/Controller?EISESSION="+sessionId+"&CID=lookupIndexes&database="+escape(selectedDbMask)+"&lookup="+escape(lookupindex)+"&searchtype=Expert";

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

// YES this is Expert searcdh but the form still has the NAME quicksearch

function selectYearRange(radioidx)
{
  if(typeof(document.quicksearch.yearselect[radioidx]) != 'undefined')
  {
    if(document.quicksearch.yearselect[radioidx].checked == false)
    {
      document.quicksearch.yearselect[radioidx].checked=true
    }
  }
}
function checkLastUpdates()
{
  var seldbmask = calculateMask(document.quicksearch.database)

  if((document.quicksearch.yearselect[1].checked == true) && (seldbmask == REFEREX))
  {
    document.quicksearch.yearselect[0].checked = true;
    document.quicksearch.yearselect[0].focus();
    alert("Last updates selection does not apply to Referex collections.");
    return false;
  }
  else if((document.quicksearch.yearselect[1].checked == true) &&
  										(seldbmask == CBF))
  {
    document.quicksearch.yearselect[0].checked = true;
    document.quicksearch.yearselect[0].focus();
    alert("Last updates selection does not apply to Ei Backfile.");
    return false;
  }
  else if((document.quicksearch.yearselect[1].checked == true) &&
  										(seldbmask == (CBF + REFEREX)))
  {
    document.quicksearch.yearselect[0].checked = true;
    document.quicksearch.yearselect[0].focus();
    alert("Last updates selection does not apply to Ei Backfile and Referex collections.");
    return false;
  }
  else
  {
    return true;
  }
}
