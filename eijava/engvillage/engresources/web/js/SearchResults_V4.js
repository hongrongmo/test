    // Right-hand side Navigators form functions
/*    function radioConflicts(eventcontrol, index)
    {
        if(document.forms.navigator.resultsorall[1].checked)
        {
            if(document.forms.navigator.inclexcl[1].checked)
            {
                alert("Conflict: Search all content cannot Exclude all selected");
                eventcontrol[index % 1].checked = true;
                eventcontrol[index].checked = false;
                return false;
            }
        }
        return true;
    }
*/
    function navigatorsOnsubmit(eventcontrol)
    {
        // loop to check that at least on checkbox has
        // been seelcted - or else hold submission
        var count = 0;
        for (i=0; i < document.forms.navigator.length; i++)
        {
            if (document.forms.navigator[i].type == "checkbox")
            {
                if (document.forms.navigator[i].checked == true)
                {
                    count++;
                }
            }
        }
        // if add a term has a value then submit is ok
        if(document.forms.navigator.append.value != '')
        {
            count++;
        }
        if(count == 0)
        {
            alert("Please select at least one Search refinement.");
            return false;
        }
        if(document.forms.navigator.resultsorall[1].checked == true)
        {
            if(eventcontrol == 'exclude')
            {
                alert("Conflict: Exclude cannot Search all content.");
                return false;
            }
        }

        return true;
    }

    // Easy-search navigation bar refine form functions
    function shortSearchValidation(sourceform)
    {
      var searchword1 = sourceform.searchWord1.value;
      if((searchword1=="") || (searchword1==null)) {
          window.alert("Enter at least one term to search in the database.");
          return false;
      }

      var where = sourceform.where.value;
      if(where == '1')
      {
        sourceform.action="/controller/servlet/Controller?CID=expertSearchCitationFormat"
        sourceform.SEARCHID.value="";
        sourceform.RERUN.value="";
        return true;
      }
      else if(where == '2')
      {
        sourceform.action="/controller/servlet/Controller?CID=expertSearchCitationFormat"
        sourceform.SEARCHID.value="";
        sourceform.append.value=searchword1;
        return true;
      }

      return false;
    }

	// THIS FUNCTION FOR SETTING THE VALUES IN BOTTOM RESULTS MANAGER WHEN THE TOP RESULTS MANAGER VALUES CHANGED (or vice versa)
	function setOppositeClearOnSearchValue(sourceform,destform,sessionid)
	{
		var img = new Image() ;
		var now = new Date() ;
		var milli = now.getTime() ;
		var sourcelength = sourceform.selectoption.length;
		var i=0;
		var clearonvalue = null;

		for(i=0; i < sourcelength ; i++)
		{
		   if(sourceform.selectoption[i].checked)
		   {
		   		destform.selectoption[i].checked = true;
		   		break;
		   }
		}

		if(sourceform.cbclear.checked)
		{
			destform.cbclear.checked = true;
			clearonvalue="true";
		}
		else
		{
			destform.cbclear.checked = false;
			clearonvalue="false";
		}
		img.src = "/engresources/Basket.jsp?select=clearonnewsearch&sessionid="+sessionid+"&clearonvalue="+clearonvalue+"&timestamp="+milli;
	}

	// THIS FUNCTION FOR SETTING THE VALUES IN BOTTOM RESULTS MANAGER WHEN THE TOP RESULTS MANAGER VALUES CHANGED (or vice versa)
	function setOppositeSelectOptionValues(sourceform,destform)
	{
		var sourcelength = sourceform.selectoption.length;
		var i=0;

		for(i=0; i < sourcelength ; i++)
		{
		   if(sourceform.selectoption[i].checked)
		   {
		   		destform.selectoption[i].checked = true;
		   		break;
		   }
		}

		if(sourceform.cbclear.checked)
		{
			destform.cbclear.checked = true;
		}
		else
		{
			destform.cbclear.checked = false;
		}
	}

    /* There is a duplicate of this function in the js file 'SelectedSet.js' called  viewSelectedSetFormat()
     * The Only Difference is that the viewSelectedSetFormat() function takes one
     * extra parameter, basketcount, which preserves the current 'page' within the basket
     */
	function viewFormat(sessionid, searchtype, searchid, count, database, formvalue)
	{
		var displaytype = null;
		var viewformat = null;
		var i=0;

		var rlength = formvalue.selectoption.length;
		for(i=0; i < rlength ; i++)
		{
		   if(formvalue.selectoption[i].checked)
		   {
			  displaytype = formvalue.selectoption[i].value;

			  if(displaytype == 'citation')
			  {
				viewformat = 'citationSelectedSet';
			  }
			  if(displaytype == 'abstract')
			  {
				viewformat = 'abstractSelectedSet';
			  }
			  if(displaytype == 'detailed')
			  {
				viewformat = 'detailedSelectedSet';
			  }
			  break;
		   } //eof if
		}
		document.location ="/controller/servlet/Controller?EISESSION="+sessionid+"&CID="+viewformat+"&SEARCHTYPE="+searchtype+"&SEARCHID="+searchid+"&COUNT="+count+"&DATABASETYPE="+database;

	}

	function downloadFormat(sessionid, database, formvalue)
	{
		var displaytype = null;

		var rlength = formvalue.selectoption.length;
		for(i=0; i < rlength ; i++)
		{
			if(formvalue.selectoption[i].checked)
			{
				displaytype = formvalue.selectoption[i].value;
			}
		}

		url= "/controller/servlet/Controller?EISESSION="+sessionid+"&CID=downloadform"+"&database="+database+"&displayformat="+displaytype+"&allselected=true";
		new_window = window.open(url,'NewWindow','status=yes,resizable,scrollbars,width=600,height=400');
		new_window.focus();
	}

	function printFormat(sessionid, database ,formvalue)
	{
		var now = new Date() ;
		var milli = now.getTime() ;
		var selectedPrintFormat = null;
		var displaytype = null;

		var rlength = formvalue.selectoption.length;
		for(i=0; i < rlength ; i++)
		{
			if(formvalue.selectoption[i].checked)
			{
				displaytype = formvalue.selectoption[i].value;
				if(displaytype == 'citation')
				{
					selectedPrintFormat = "printCitationSelectedSet";
					break;
				}
				else if(displaytype == 'abstract')
				{
					selectedPrintFormat = "printAbstractSelectedSet";
					break;
				}
				else if(displaytype == 'detailed')
				{
					selectedPrintFormat = "printDetailedSelectedSet";
					break;
				}
			} // eof if
		} //eof for

		var url= "/controller/servlet/Controller?EISESSION="+sessionid+"&CID="+selectedPrintFormat+"&timestamp="+milli;
		new_window = window.open(url,'NewWindow','status=yes,resizable,scrollbars,width=700,height=400');
		new_window.focus();
	}

	// THIS FUNCTION BASICALLY CONSTRUCT ALL THE REQUIRED PARAMETERS FOR EMAIL.THE VALUES SO CONSTRUCTED ARE SENT TO
	// EMAIL FORM.(emailSelectedRecords,emailSelectedFormatResults.jsp)
	function emailFormat(sessionid,searchtype,searchid,count,resultscount,database,databaseid,pagesize,formvalue)
	{
		var displaytype = null;

		var rlength = formvalue.selectoption.length;

		for(i=0; i < rlength ; i++)
		{
			if(formvalue.selectoption[i].checked)
			{
				displaytype = formvalue.selectoption[i].value;
			}
		}

		var cbsize = 0;
		var cbcheckvalue = false;
		var i=0;
		var url = null;
		var docidstring  = "&docidlist=";
		var handlestring = "&handlelist=";
		var handlecount=0;
		var docidcount=0;

		if( resultscount == 1)
		{
		   var hiddensize = document.quicksearchresultsform.elements.length;

		   for(var i=0 ; i < hiddensize ; i++)
		   {
				var nameOfElement = document.quicksearchresultsform.elements[i].name;
				var valueOfElement = document.quicksearchresultsform.elements[i].value;

				if((nameOfElement.search(/HANDLE/)!=-1) && (valueOfElement != ""))
				{
					handlestring += valueOfElement ;
				}

				if((nameOfElement.search(/DOC-ID/)!=-1) && (valueOfElement != ""))
				{
					docidstring += valueOfElement ;
				}
			}

			url= "/controller/servlet/Controller?EISESSION="+sessionid+"&CID=emailForm"+"&searchid="+searchid+"&database="+database+"&displayformat="+displaytype+docidstring+handlestring;
		}
		else
		{
			cbsize = document.quicksearchresultsform.cbresult.length;


			for( i =0; i < cbsize; i++)
			{
			   if(document.quicksearchresultsform.cbresult[i].checked)
			   {
					cbcheckvalue = true;
					break;
			   }
			}

			if(cbcheckvalue)
			{

				var arrDocID = new Array(pagesize);
				var arrHandle = new Array(pagesize);
				var hiddensize = document.quicksearchresultsform.elements.length;

				for(var i=0 ; i < hiddensize ; i++)
				{
					var nameOfElement=document.quicksearchresultsform.elements[i].name;
					var valueOfElement = document.quicksearchresultsform.elements[i].value;

					if((nameOfElement.search(/HANDLE/)!=-1) && (valueOfElement != ""))
					{
							arrHandle[handlecount++] = valueOfElement ;
					}

					if((nameOfElement.search(/DOC-ID/)!=-1) && (valueOfElement != ""))
					{
							arrDocID[docidcount++] = valueOfElement ;
					}
				}

				for(index = 0 ; index < cbsize ; index++)
				{
					cbcheckvalue=document.quicksearchresultsform.cbresult[index].checked;


					if(cbcheckvalue)
					{
						var subdocstring = arrDocID[index]+",";
						docidstring +=subdocstring;
						var subhandlestring = arrHandle[index]+",";
						handlestring += subhandlestring;
					}
				}

				url= "/controller/servlet/Controller?EISESSION="+sessionid+"&CID=emailForm"+"&searchid="+searchid+"&database="+database+"&displayformat="+displaytype+docidstring+handlestring;
			}
			else
			{

			   url = "/controller/servlet/Controller?EISESSION="+sessionid+"&CID=emailForm"+"&searchid="+searchid+"&count="+count+"&resultscount="+resultscount+"&database="+database+"&pagesize="+pagesize+"&displayformat="+displaytype;
			}
		}
		new_window = window.open(url,'NewWindow','status=yes,resizable,scrollbars,width=600,height=400');
		new_window.focus();

	}


