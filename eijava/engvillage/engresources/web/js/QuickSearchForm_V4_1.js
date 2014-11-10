var NTIS = 4;
var INSPEC = 2;
var CPX = 1;

var startYear;
var stringYear;
var endYear;
var sec1Value;
var sec2Value;
var sec3Value;
var dtypeValue;
var trtypeValue;
var disctypeValue;
var selectedDbMask = 0;


// 'constructor'
function Field(mask, value, label) {
   this.mask = mask;
   this.label = label;
   this.value = value;
}


var searchin = new Array();
// Compendex and Common fields
searchin[0]  = new Field(7, "NO-LIMIT","All fields");
searchin[1]  = new Field(7, "KY","Subject/Title/Abstract");
searchin[2]  = new Field(7, "AB","Abstract");
searchin[3]  = new Field(7, "AU","Author");
searchin[4]  = new Field(7, "AF","Author affiliation");
searchin[5]  = new Field(7, "CV","Controlled term");
searchin[6]  = new Field(1, "CL","Ei Classification code");
searchin[7]  = new Field(2, "CL","Classification code");
searchin[8]  = new Field(3, "CN","CODEN");
searchin[9]  = new Field(3, "CF","Conference information");
searchin[10]  = new Field(1, "CC","Conference code");
searchin[11]  = new Field(3, "SN","ISSN");
searchin[12]  = new Field(1, "MH","Ei main heading");
searchin[13]  = new Field(3, "PN","Publisher");
searchin[14]  = new Field(3, "ST","Serial title");
searchin[15]  = new Field(7, "TI","Title");
searchin[16]  = new Field(1, "CV", "Ei controlled term");

// Inspec unique fields
searchin[17]  = new Field(2, "PM","Patent number");
searchin[18]  = new Field(2, "PA","Filing date");
searchin[19]  = new Field(2, "PI","Patent issue date");
searchin[20]  = new Field(2, "PU","Country of application");
searchin[21]  = new Field(2, "MI","Material Identity Number");
searchin[22]  = new Field(2, "CV","Inspec controlled term");

// NTIS unique fields
searchin[23]  = new Field(4, "CT","Contract number");
searchin[24]  = new Field(4, "CO","Country of origin");
searchin[25]  = new Field(4, "AG","Monitoring agency");
searchin[26]  = new Field(4, "AN","NTIS accession number");
searchin[27]  = new Field(4, "RN","Report number");
searchin[28]  = new Field(4, "CV","NTIS controlled term");

var doctypes = new Array();
doctypes[0]  = new Field(3, "NO-LIMIT", "All document types");
doctypes[1]  = new Field(3, "JA","Journal article");
doctypes[2]  = new Field(3, "CA","Conference article");
doctypes[3]  = new Field(3, "CP","Conference proceeding");
doctypes[4]  = new Field(3, "MC","Monograph chapter");
doctypes[5]  = new Field(3, "MR","Monograph review");
doctypes[6]  = new Field(3, "RC","Report chapter");
doctypes[7]  = new Field(3, "RR","Report review");
doctypes[8]  = new Field(3, "DS","Dissertation");
doctypes[9]  = new Field(2, "UP","Unpublished paper");
doctypes[10]  = new Field(2, "PA","Patent (before 1977)");
doctypes[11]  = new Field(1, "PA","Patents (before 1970)");

var treattypes = new Array();
treattypes[0] = new Field(3, "NO-LIMIT", "All treatment types");
treattypes[1] = new Field(3, "APP", "Applications");
treattypes[2] = new Field(1, "BIO", "Biographical");
treattypes[3] = new Field(2, "BIB", "Bibliography");
treattypes[4] = new Field(3, "ECO", "Economic");
treattypes[5] = new Field(3, "EXP", "Experimental");
treattypes[6] = new Field(3, "GEN", "General review");
treattypes[7] = new Field(1, "HIS", "Historical");
treattypes[8] = new Field(1, "LIT", "Literature review");
treattypes[9] = new Field(1, "MAN", "Management aspects");
treattypes[10] = new Field(1, "NUM", "Numerical");
treattypes[11] = new Field(2, "NEW", "New development");
treattypes[12] = new Field(2, "PRA", "Practical");
treattypes[13] = new Field(2, "PRO", "Product review");
treattypes[14] = new Field(3, "THR", "Theoretical");

var disciplines = new Array();
disciplines[0] = new Field(2, "NO-LIMIT", "All disciplines");
disciplines[1] = new Field(2, "A", "Physics");
disciplines[2] = new Field(2, "B", "Electrical/Electronic engineering");
disciplines[3] = new Field(2, "C", "Computers/Control engineering");
disciplines[4] = new Field(2, "D", "Information technology");
disciplines[5] = new Field(2, "E", "Manufacturing and production engineering");

function generateYear(selectedDbMask, sYear, strYear, eYear, searchform)
{

    var sy = calStartYear(selectedDbMask, strYear);

    for (i = searchform.startYear.length; i > 0; i--)
    {
        searchform.startYear.options[i] = null;
        searchform.endYear.options[i] = null;
    }

    var dy = calDisplayYear(selectedDbMask, strYear);


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


function switchlist(selectedDbMask, control, newvalues, selectedValue)
{
    // NULL OUT PREVIOUS LIST VALUES
    var i = 0;
    for (i = control.length - 1; i >= 0; i--)
    {
        control.options[i] = null;
    }

    // FILL LIST WITH VALUES MATCHING SELECTED
    var tot = newvalues.length;
    var matches = 0;
    var selectedIndex = 0;
    for (i = 0, matches = 0; i < tot; i++)
    {
        if((newvalues[i].mask & selectedDbMask) == selectedDbMask)
        {
            if(((selectedDbMask == CPX) ||
               (selectedDbMask == INSPEC) ||
               (selectedDbMask == NTIS)) &&
               (newvalues[i].label == 'Controlled term'))
            {

         }
         else
         {
            control.options[matches] = new Option(newvalues[i].label, newvalues[i].value);
            if(!(selectedValue == null) && (newvalues[i].value == selectedValue))
            {
                control.options[matches].selected = true;
                selectedIndex = matches;
            }
            matches++;
         }
       }
    }
    control.selectedIndex = selectedIndex;

    return true;
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

function clearDropdown(selectedDbMask, searchform)
{
    if((selectedDbMask !=  0) && ((selectedDbMask & NTIS) == NTIS))
    {
        if(searchform.doctype)
        {
            searchform.doctype.options[0] = null;
            searchform.doctype.options[0] = new Option("Document type not available", "NO-LIMIT");
            searchform.doctype.options[0].selected = true;
            searchform.doctype.selectedIndex = 0;
        }
        if(searchform.treatmentType)
        {
            searchform.treatmentType.options[0] = null;
            searchform.treatmentType.options[0] = new Option("Treatment type not available", "NO-LIMIT");
            searchform.treatmentType.options[0].selected = true;
            searchform.treatmentType.selectedIndex = 0;
        }
        if(searchform.disciplinetype)
        {
            searchform.disciplinetype.options[0] = null;
            searchform.disciplinetype.options[0] = new Option("Discipline type not available", "NO-LIMIT");
            searchform.disciplinetype.options[0].selected = true;
            searchform.disciplinetype.selectedIndex = 0;
        }
    }
    else if((selectedDbMask !=  0) && ((selectedDbMask & CPX) == CPX))
    {
        if(searchform.disciplinetype)
        {
            searchform.disciplinetype.options[0] = null;
            searchform.disciplinetype.options[0] = new Option("Discipline type not available", "NO-LIMIT");
            searchform.disciplinetype.options[0].selected = true;
            searchform.disciplinetype.selectedIndex = 0;
        }
    }
}

function clearDatabase()
{
    for(var i=0; i<document.quicksearch.database.length; i++)
    {
        if(document.quicksearch.database[i].checked)
        {
            document.quicksearch.database[i].checked=false;
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

function flipImage(selectedDbMask)
{
    document.lookuplink1.src="/engresources/images/ath.gif";
    document.lookuplink2.src="/engresources/images/af.gif";
    document.lookuplink3.src="/engresources/images/ct.gif";
    document.lookuplink4.src="/engresources/images/st.gif";
    document.lookuplink5.src="/engresources/images/pb.gif";

    if((selectedDbMask !=  0) && ((selectedDbMask & NTIS) == NTIS))
    {
        document.lookuplink4.src="/engresources/images/checking.gif";
        document.lookuplink5.src="/engresources/images/checking.gif";
    }
    else
    {
        document.lookuplink4.src="/engresources/images/st.gif";
        document.lookuplink5.src="/engresources/images/pb.gif";
    }
}


function change(db)
{

    if(db == 'database')
    {
        clearAlldb();
        selectedDbMask = calculateMask(document.quicksearch.database);
    }
    else
    {
        clearDatabase();
        selectedDbMask = calculateMask(document.quicksearch.alldb);
    }

    if(document.quicksearch.section1.value != 'NO-LIMIT')
    {
        sec1Value = document.quicksearch.section1.value;
    }

    if(document.quicksearch.section2.value != 'NO-LIMIT')
    {
        sec2Value = document.quicksearch.section2.value;
    }

    if(document.quicksearch.section3.value != 'NO-LIMIT')
    {
        sec3Value = document.quicksearch.section3.value;
    }

    // updateUI will be called for the first time fom here (no longer called in body:onLoad event)
    // we need to intialize these global variables
    endYear="2006";
    stringYear = document.quicksearch.stringYear.value;
    startYear=document.quicksearch.startYear[document.quicksearch.startYear.selectedIndex].value;
    if(document.quicksearch.doctype)
    {
        dtypeValue=document.quicksearch.doctype[document.quicksearch.doctype.selectedIndex].value;
    }
    if(document.quicksearch.treatmentType)
    {
        trtypeValue=document.quicksearch.treatmentType[document.quicksearch.treatmentType.selectedIndex].value;
    }
    if(document.quicksearch.disciplinetype)
    {
        disctypeValue=document.quicksearch.disciplinetype[document.quicksearch.disciplinetype.selectedIndex].value;
    }

    updateUI(selectedDbMask, startYear, stringYear, endYear, sec1Value, sec2Value, sec3Value, dtypeValue, trtypeValue, disctypeValue);

}

function updateUI(dbMask, sYear, strYear, eYear, sec1, sec2, sec3, dtype, trtype, disctype)
{

    if(!(dbMask == 8) && !(dbMask == 16))
    {
        flipImage(dbMask);

        switchlist(dbMask, document.quicksearch.section1, searchin, sec1);
        switchlist(dbMask, document.quicksearch.section2, searchin, sec2);
        switchlist(dbMask, document.quicksearch.section3, searchin, sec3);

        if(document.quicksearch.doctype)
        {
            switchlist(dbMask, document.quicksearch.doctype, doctypes, dtype);
        }
        if(document.quicksearch.treatmentType)
        {
            switchlist(dbMask, document.quicksearch.treatmentType, treattypes, trtype);
        }
        if(document.quicksearch.disciplinetype)
        {
            switchlist(dbMask, document.quicksearch.disciplinetype, disciplines, disctype);
        }

        clearDropdown(dbMask, document.quicksearch);

        generateYear(dbMask, sYear, strYear, eYear, document.quicksearch);

        startYear = sYear;
        stringYear = strYear;
        endYear = eYear;

        sec1Value = sec1;
        sec2Value = sec2;
        sec3Value = sec3;
        dtypeValue = dtype;
        trtypeValue = trtype;
        disctypeValue = disctype;
    }
}


var lookupWind;

function OpenLookup(sessionId,databaseName,seltab)
{
    if(selectedDbMask == 0)
    {
        selectedDbMask = databaseName;
    }

    if ((selectedDbMask !=  0)
        && ((selectedDbMask & NTIS) == NTIS)
        && ((seltab == 'ST') || (seltab == 'PN')))
    {
        return false;
    }
    else
    {
        var tabloc;
        tabloc="/controller/servlet/Controller?EISESSION="+sessionId+"&CID=lookupIndexes&database="+escape(selectedDbMask)+"&lookup="+escape(seltab)+"&searchtype=Quick";

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

    if((searchword1=="") || (searchword1==null))
    {
        window.alert("Enter at least one term to search in the database.");
        return false;
    }

    if(!(searchword1==""))
    {
        /* jam 11/10/2004 - now we can start with a *
        if(searchword1.substring(0,1) == '*') {
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

        if (searchLength == tempLength)
        {
            window.alert("Enter at least one term to search in the database.");
            return false;
        }

    }

    // 2/8/2005 check to see if we are searching yearselect here
    // so we don't extract year values if not necessary
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

function updateWinds()
{
    if ( window.lookupWind )
    {
        if (!window.lookupWind.closed)
        {
            window.lookupWind.updatechecks();
        }
    }
}

function closeWinds()
{
    if ( lookupWind )
    {
        window.lookupWind.close();
    }
}

function checkPatent(quicksearch)
{
    if(selectedDbMask == INSPEC)
    {
        // select year range <start>-1976 for document type is Patent.
        if(quicksearch.doctype.options[quicksearch.doctype.selectedIndex].value == 'PA')
        {
            quicksearch.startYear.selectedIndex = 0;
            quicksearch.endYear.selectedIndex
            = quicksearch.endYear.length
            - ((quicksearch.endYear[quicksearch.endYear.length-1].value - 1976) +1);

            quicksearch.yearselect[0].checked = true;
        }
        else // reset
        {
            // 'climb up' from begin dropdown value to startYear
            quicksearch.startYear.selectedIndex = calDisplayYear(INSPEC,stringYear)-quicksearch.startYear[0].value;
            quicksearch.endYear.selectedIndex = quicksearch.endYear.length -1;
            quicksearch.yearselect[0].checked = true;
        }
    }
    if(selectedDbMask == CPX)
    {
        // select year range <start>-1969 when document type is Patent.
        if(quicksearch.doctype.options[quicksearch.doctype.selectedIndex].value=='PA')
        {
            quicksearch.startYear.selectedIndex = 0;

            quicksearch.endYear.selectedIndex
            = quicksearch.endYear.length
            - ((quicksearch.endYear[quicksearch.endYear.length-1].value - 1969) + 1);

            quicksearch.yearselect[0].checked=true;
        }
        else // reset
        {
            // 'climb down' from last dropdown value to startYear
            quicksearch.startYear.selectedIndex = calDisplayYear(CPX,stringYear)-quicksearch.startYear[0].value;
            quicksearch.endYear.selectedIndex = quicksearch.endYear.length-1;
            quicksearch.yearselect[0].checked = true
        }
    }

    return false;
}





