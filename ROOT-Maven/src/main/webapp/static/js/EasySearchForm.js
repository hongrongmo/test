	function clearAlldb()
	{	
		if(document.easysearch.alldb.checked)
		{
			document.easysearch.alldb.checked = false;
		}
	}

	function clearDatabase()
	{
		if(document.easysearch.alldb.checked)
		{	
			for(var i=0; i<document.easysearch.database.length; i++) 
			{ 
				document.easysearch.database[i].checked=false;
			}
		
		}	
	}

	function calculateMask(control) 
	{
		var selectedDbMask = 0;
	
		// CALCULATE SELECTED DB MASK
		if(document.easysearch.alldb.checked == true) 
		{
			selectedDbMask = eval(document.easysearch.alldb.value);
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

	function change(db)
	{
		if(db == 'alldb')
		{
			clearDatabase();
			selectedDbMask = calculateMask(document.easysearch.alldb);		
		}
		else
		{
			clearAlldb();
			selectedDbMask = calculateMask(document.easysearch.database);		
		}
	
		return true;
	}
	
// There is no reset for easy search
//    function doReset() {
//      document.easysearch.searchWord1.value="";
//      document.easysearch.searchWord1.focus();
//    }

    function searchValidation() {

        var searchword1=document.easysearch.searchWord1.value;

        if((searchword1=="") || (searchword1==null)) {
            window.alert("Enter at least one term to search in the database.");
            return false;
        }
		
		if(typeof(document.easysearch.alldb)  != 'undefined')
		{
			if(calculateMask(document.easysearch.database) == 0)
			{
				window.alert("Select at least one database to search in.");
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
        if ( typeof(window.lookupWind) != 'undefined' )
        {
            window.lookupWind.close();
        }
    }


