
	/*
		This function will be called when you want to add some range of documents
	 	to the selected set.

		jam 10/3/2002
		This was originally included as CDATA within the following stylesheet
		\engvillage\web\views\customer\compendex\CPXCitationResults.xsl
	*/

	function selectedRange(selectform,resultsform)
	{

		var  searchquery = selectform.searchquery.value;
		var  database = selectform.database.value;
		var  sessionid = selectform.sessionid.value;
		var  searchid = selectform.searchid.value;
		var  pagesize = selectform.pagesize.value;
		var  resultscount = selectform.resultscount.value;

		var  databaseid = selectform.databaseid.value;
		var  count = selectform.currentpage.value;
		var  searchtype = selectform.searchtype.value;
		
		var startrange = 0;
		var endrange = 0;
		var i=0;
		var displaytype = null;

		if( ( selectform.selectrangefrom.value == '') || ( selectform.selectrangeto.value == ''))
		{
			window.alert("Start Range and End Range must be given");
		}
		else if((isNaN(selectform.selectrangefrom.value)) || (isNaN(selectform.selectrangeto.value)))
		{
			window.alert("Please enter numbers only");
		}
		else
		{
			var selectFromLength= selectform.selectrangefrom.value.length;
			var tempFromWord = selectform.selectrangefrom.value;
			var tempFromLength=0;

			while (tempFromWord.substring(0,1) == ' ')
			{
				tempFromWord = tempFromWord.substring(1);
				tempFromLength = tempFromLength + 1;
			}

			var selectToLength = selectform.selectrangeto.value.length;
			var tempToWord = selectform.selectrangeto.value;
			var tempToLength=0;

			while (tempToWord.substring(0,1) == ' ')
			{
				tempToWord = tempToWord.substring(1);
				tempToLength = tempToLength + 1;
			}

			if (( selectFromLength == tempFromLength) || ( selectToLength == tempToLength) )
			{
				window.alert("Spaces are not allowed in the  range");
			}
			else
			{

				var selectlength= resultsform.selectoption.length;

				for(i=0; i < selectlength ; i++)
				{
					if(resultsform.selectoption[i].checked)
					{
						displaytype = resultsform.selectoption[i].value;
						break;
					}
				} // for
				startrange=parseInt(selectform.selectrangefrom.value);
				endrange = parseInt(selectform.selectrangeto.value);

				if( ( startrange <= 0) || ( endrange <= 0))
				{
					window.alert("Range starts with 1");
				}
				else if( startrange >= endrange )
				{
					window.alert("start range should be less than end range");
				}
				else if( ( startrange >= resultscount) || ( endrange > resultscount))
				{
					window.alert("Please enter the range within the Results range");
				}
				else
				{
					document.location="/controller/servlet/Controller?EISESSION="+sessionid+"&CID=addSelectedRange&searchid="+searchid+"&searchquery="+searchquery+"&searchtype="+searchtype+"&database="+database+"&databaseid="+databaseid+"&startrange="+startrange+"&endrange="+endrange+"&pagesize="+pagesize+"&resultscount="+resultscount+"&count="+count+"&displaytype="+displaytype;
				}
			} // else
		} // else
	} // function


	/* This function will be called when you want to add all documents to the selected set and when you
	   want to remove all the documents from selected set in the page .In this two
	   functions will be performed based on selectoption argument.If the selectoption value is markall
	   the documents which are not there in the selected set will be added and when the selectoption value
	   is unmarkall it will delete all the documents corresponding to that page from the selected set.
	*/

	function selectUnselectAllRecords(selectoption)
	{
		var  searchquery = document.forms.selectUnselectAllRecords.searchquery.value;
		var  database = document.forms.selectUnselectAllRecords.database.value;
		var  sessionid = document.forms.selectUnselectAllRecords.sessionid.value;
		var  searchid = document.forms.selectUnselectAllRecords.searchid.value;
		var  pagesize = document.forms.selectUnselectAllRecords.pagesize.value;
		var  resultscount = document.forms.selectUnselectAllRecords.resultscount.value;
		
		var now = new Date() ;
		var milli = now.getTime() ;
		var img = new Image() ;

		var docidstring  = "";
		var handlestring = "";
		var count = 0;
		var index = 0;
		var handlecount=0;
		var docidcount=0;

		var arrDocID = new Array(pagesize);
		var arrHandle = new Array(pagesize);


		//window.alert("selectUnselectAllRecords method called"+arrDocID);

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


		var cbsize = 0;
		var cbcheckvalue= false;

		if( resultscount == 1)
		{
			cbcheckvalue=document.quicksearchresultsform.cbresult.checked;
			if (selectoption == 'markall')
			{
				if(!cbcheckvalue)
				{
					var subdocstring = "&docid="+arrDocID[0];
					docidstring +=subdocstring;
					var subhandlestring = "&handle="+arrHandle[0];
					handlestring += subhandlestring;
				}
			}
			else
			{
				var subdocstring = "&docid="+arrDocID[0];
				docidstring +=subdocstring;
				var subhandlestring = "&handle="+arrHandle[0];
				handlestring += subhandlestring;
			}
		}
		else
		{
			cbsize = document.quicksearchresultsform.cbresult.length;

			for(index = 0 ; index < cbsize ; index++)
			{
					cbcheckvalue=document.quicksearchresultsform.cbresult[index].checked;

					if (selectoption == 'markall')
					{
						if(!cbcheckvalue)
						{
							var subdocstring = "&docid="+arrDocID[index];
							docidstring +=subdocstring;
							var subhandlestring = "&handle="+arrHandle[index];
							handlestring += subhandlestring;
						}
					}
					else
					{
						var subdocstring = "&docid="+arrDocID[index];
						docidstring +=subdocstring;
						var subhandlestring = "&handle="+arrHandle[index];
						handlestring += subhandlestring;
					}
			}
		}

		document.images['image_basket'].src = "/engresources/Basket.jsp?select="+selectoption+"&database="+escape(database)+"&searchquery="+escape(searchquery)+"&sessionid="+sessionid+"&searchid="+searchid+docidstring+handlestring+"&timestamp=" + milli;

		if( selectoption == 'markall' )
		{
			if( resultscount == 1)
			{
					document.quicksearchresultsform.cbresult.checked = true;
			}
			else
			{
				for(index = 0 ; index < cbsize ; index++)
				{
					document.quicksearchresultsform.cbresult[index].checked = true;
				}
			}
		 }
		 else
		 {
			if( resultscount == 1)
			{
				document.quicksearchresultsform.cbresult.checked = false;
			}
			else
			{
				for(index = 0 ; index < cbsize ; index++)
				{
					document.quicksearchresultsform.cbresult[index].checked = false;
				}
			}
		 }

	}

	/*
	  This function will be called when you want to delete all documents from the selected set
	  in the corresponding page.
	*/

	function clearAllSelectedRecords(selectvalue,sessionid,resultscount)
	{
		var now = new Date() ;
		var milli = now.getTime() ;
		var img = new Image() ;
		var cbsize = 0;

		document.images['image_basket'].src = "/engresources/Basket.jsp?select="+selectvalue+"&sessionid="+sessionid+"&timestamp=" + milli;


		if( resultscount == 1)
		{
			document.quicksearchresultsform.cbresult.checked = false;
		}
		else
		{
			cbsize = document.quicksearchresultsform.cbresult.length;

			for(index = 0 ; index < cbsize ; index++)
			{
				document.quicksearchresultsform.cbresult[index].checked = false;
			}
		}

	}


	/*
	  This function called when you want to add the document to the selected set and when
	  you want to delete the document from the selected set.In this two
	  functions will be performed based on checkbox checked value.If the checked value is true
	  the document will add to the selected set otherwise the document will be removed from
	  the selected set.
	*/

	function selectUnselectRecord(thebox,handle,docid,searchquery,database,sessionid,searchid,resultscount)
	{
		  var now = new Date() ;
		  var milli = now.getTime() ;
		  var img = new Image() ;
		  var cbcheck = false;
		  var cbsize = document.quicksearchresultsform.cbresult.length;

		 
		  if(thebox.checked)
		  {
				//window.alert("entered into adding to the basket");
				document.images['image_basket'].src="/engresources/Basket.jsp?"+
				"select=mark&handle="+handle+"&docid="+docid+"&database="+escape(database)+"&sessionid="+
				sessionid+"&searchquery="+escape(searchquery)+"&searchid="+searchid+"&timestamp="+ milli;
		  } else {
			  //window.alert(" deleting from the basket");
			  document.images['image_basket'].src="/engresources/Basket.jsp?"+
			  "select=unmark&handle="+handle+"&docid="+docid+"&database="+escape(database)+
			  "&sessionid="+sessionid+"&searchquery="+escape(searchquery)+"&searchid="+searchid+"&timestamp="+ milli ;
		  }
	 }



