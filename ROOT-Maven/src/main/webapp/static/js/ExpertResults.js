	// Funtion for lookup windows
	var lookupWind

	function OpenLookup(sessionId,databaseName)
	{
	var lookup = document.lookupform
	var seltab
	var tabloc
		for (var i = 0; i < document.lookupform.lookup.length; i++)
		{
			if (  document.lookupform.lookup[i].checked)
			{
				seltab =  document.lookupform.lookup[i].value
			}
		}

		 selectedIndex = seltab;
		 tabloc="/controller/servlet/Controller?EISESSION="+sessionId+"&CID=lookupIndexes&database="+escape(databaseName)+"&lookup="+escape(seltab)+"&searchtype=Expert";

		if ( tabloc )
		{
			if (!lookupWind || lookupWind.closed)
			{
				lookupWind = window.open(
					tabloc,
					"LookupWin",
					"height=500,width=500,top=0,left="+((screen.width*.35))+",scrollbars=yes,menubar=no,resizable=yes,toolbar=no,location=no,directories=no")
			}

			else
			{
				window.lookupWind.location = tabloc
				window.lookupWind.focus()
			}
		}
		else
		{
			alert("Please select a lookup table")
		}
	}

	function updateWinds()
	{
	if ( window.lookupWind )
	{
	if (!window.lookupWind.closed) {
		window.lookupWind.expertchecks();
	}
	}
	}

	// Function to close lookup window
	function closeWinds()
	{
		if ( lookupWind )
		{
			window.lookupWind.close()
		}
	}