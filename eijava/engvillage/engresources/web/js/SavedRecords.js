
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

	}

	// This function will be called when you want to display the records in printformat based on the
	// format which is selected in the results manager

	function downloadSavedRecords(sessionid,folderid,formvalue)
	{
		var rlength = formvalue.selectoption.length;
		var displaytype = null;

		for(i=0; i < rlength ; i++)
		{
			if(formvalue.selectoption[i].checked)
			{
				displaytype = formvalue.selectoption[i].value;
			}
		}
		window.open("/controller/servlet/Controller?EISESSION="+sessionid+"&CID=downloadform&folderid="+folderid+"&displayformat="+displaytype+"&savedrecords=true",'NewWindow','status=yes,resizable,scrollbars,width=600,height=400');
	}

   // THIS FUNCTION BASICALLY CONSTRUCT ALL THE REQUIRED PARAMETERS FOR EMAIL.THE VALUES SO CONSTRUCTED ARE SENT TO
   //EMAIL FORM.(emailSelectedRecords,emailSelectedFormatResults.jsp)

	function emailSavedRecordsFormat(sessionid,folderid,formvalue)
	{
		var rlength = formvalue.selectoption.length;
		var displaytype = null;
		var i=0;

		for(i=0; i < rlength ; i++)
		{
		   if(formvalue.selectoption[i].checked)
		   {
			  displaytype = formvalue.selectoption[i].value;
		   }
		}

		new_window=window.open("/controller/servlet/Controller?EISESSION="+sessionid+"&CID=emailForm&folderid="+folderid+"&displayformat="+displaytype+"&savedrecords=true",'NewWindow','status=yes,resizable,scrollbars,width=600,height=400');

		new_window.focus();

	}

	//this function is to print saved records
	function printSavedRecords(sessionid,folderid,formvalue)
	{
		var rlength = formvalue.selectoption.length;
		var displaytype = null;
		var printFormat = null;
		var i=0;

		for(i=0; i < rlength ; i++)
		{
			   if(formvalue.selectoption[i].checked)
			   {
				  displaytype = formvalue.selectoption[i].value;
				  if(displaytype == 'citation')
				  {
						printFormat = "printCitationSavedRecords";
						break;
				  }
				  if(displaytype == 'abstract')
				  {
						printFormat = "printAbstractSavedRecords";
						break;
				  }
				  if(displaytype == 'detailed')
				  {
						printFormat = "printDetailedSavedRecords";
						break;
				  }
			   }
		}

		window.open("/controller/servlet/Controller?EISESSION="+sessionid+"&CID="+printFormat+"&folderid="+folderid,'NewWindow','status=yes,resizable,scrollbars,width=600,height=400');
	}

	function updateDisplayOfSavedRecords(sessionid,folderid,database,formvalue)
	{

		var rlength = formvalue.selectoption.length;
		var displaytype = null;
		var cid = null;
		var i=0;

		for(i=0; i < rlength ; i++)
		{
			   if(formvalue.selectoption[i].checked)
			   {
				  displaytype = formvalue.selectoption[i].value;
				  if(displaytype == 'citation')
				  {
						cid = "viewCitationSavedRecords";
						break;
				  }
				  if(displaytype == 'abstract')
				  {
						cid = "viewAbstractSavedRecords";
						break;
				  }
				  if(displaytype == 'detailed')
				  {
						cid = "viewDetailedSavedRecords";
						break;
				  }
			   }
		}

				var url="/controller/servlet/Controller?EISESSION="+sessionid+"&CID="+cid+"&folderid="+folderid+"&database="+database;

		document.location=url;
	}

	 function newWindow(url)
	 {
		var remoteurl=url;
		NewWindow = window.open(remoteurl,'NewWindow','status=yes,resizable,scrollbars=1,width=400,height=450');
		NewWindow.focus();
	 }

