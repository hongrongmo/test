	// THIS FUNCTION FOR SETTING THE VALUES IN BOTTOM RESULTS MANAGER WHEN THE TOP RESULTS MANAGER VALUES CHANGED (or vice versa)
	function setOppositeClearOnSearchValue(sourceform,destform,sessionid)
	{
		var img = new Image() ;
		var now = new Date() ;
		var milli = now.getTime() ;
		var sourcelength= sourceform.selectoption.length;
		
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

	// This function will be called when you want to display 
	// the selected records based on the
	// format which is selected in the results manager.
    /* There is a duplicate of this function in the js file 'SearchResults.js' called  viewFormat()
     * The Only Difference is that the viewFormat() function dose not take
     * the extra parameter, basketcount, which preserves the current 'page' within the basket 
     */
	function viewSelectedSetFormat(sessionid, basketcount, searchtype, searchid, count, database, formvalue)
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
				break;
			}
		}

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
        document.location ="/controller/servlet/Controller?EISESSION="+sessionid+"&CID="+viewformat+"&BASKETCOUNT="+basketcount+"&SEARCHTYPE="+searchtype+"&SEARCHID="+searchid+"&COUNT="+count+"&DATABASETYPE="+database
	}
	
	function downloadFormat(sessionid,basketcount,searchtype,searchid,count,resultscount,database,databaseid,formvalue)
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
		new_window=window.open("/controller/servlet/Controller?EISESSION="+sessionid+"&CID=downloadform&basketcount="+basketcount+"&searchtype="+searchtype+"&searchid="+searchid+"&count="+count+"&resultcount="+resultscount+"&database="+database+"&displayformat="+displaytype+"&databaseid="+databaseid+"&selectedset=true",'NewWindow','status=yes,resizable,scrollbars,width=600,height=400');
		new_window.focus();
	}
	
	// This function will be called when you want to display the records in printformat based on the
	// format which is selected in the results manager.
	function printSelectedSetFormat(sessionid,formvalue)
	{
		var displaytype = null;
		var printFormat = null;
		var i=0;
		var url = null;
		var now = new Date() ;
		var milli = now.getTime() ;
	
		var rlength = formvalue.selectoption.length;
	
		for(i=0; i < rlength ; i++)
		{
			if(formvalue.selectoption[i].checked)
			{
				displaytype = formvalue.selectoption[i].value;
				break;
			}
		}

		if(displaytype == 'citation')
		{
			printFormat = "printCitationSelectedSet";
		}
		if(displaytype == 'abstract')
		{
			printFormat = "printAbstractSelectedSet";
		}
		if(displaytype == 'detailed')
		{
			printFormat = "printDetailedSelectedSet";
		}
	
		url = "/controller/servlet/Controller?EISESSION="+sessionid+"&CID="+printFormat+"&timestamp="+milli;
		NewWindow = window.open(url,'NewWindow','status=yes,resizable,scrollbars,width=600,height=400');
		NewWindow.focus();
	}
	
	// THIS FUNCTION BASICALLY CONSTRUCT ALL THE REQUIRED PARAMETERS FOR EMAIL.THE VALUES SO CONSTRUCTED ARE SENT TO
	//EMAIL FORM.(emailSelectedset.jsp)
	function emailSelectedSetFormat(sessionid,basketcount,searchtype,searchid,count,resultscount,database,databaseid,formvalue)
	{
		var displaytype = null;
		var i=0;
		var rlength = formvalue.selectoption.length;
		for(i=0; i < rlength ; i++)
		{
			if(formvalue.selectoption[i].checked)
			{
				displaytype = formvalue.selectoption[i].value;
			}
		}
		new_window=window.open("/controller/servlet/Controller?EISESSION="+sessionid+"&CID=emailForm"+"&BASKETCOUNT="+basketcount+"&SEARCHTYPE="+searchtype+"&SEARCHID="+searchid+"&COUNT="+count+"&RESULTSCOUNT="+resultscount+"&DATABASETYPE="+database+"&DATABASEID="+databaseid+"&displayformat="+displaytype+"&selectedset=true",'NewWindow','status=yes,resizable,scrollbars,width=600,height=400');
		new_window.focus();
	}
	
	
	function newWindow(url)
	{
		var remoteurl=url;
		NewWindow = window.open(remoteurl,'NewWindow','status=yes,resizable,scrollbars=1,width=400,height=450');
		NewWindow.focus();
	}

