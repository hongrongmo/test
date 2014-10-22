
  function submitSelecteddatabase(sessionId)
  {
	if(typeof(document.expertsearch.alldb) != 'undefined')
	{
		if(calculateMask(document.expertsearch.database) == 0)
		{
			window.alert('Please select a database');
			return false;
		}
	}
  }

  // THIS FUNCTION WILL VALIDATE THE DATA ENTERED IN EACH FIELD OF THE EXPERT SEARCH FORM
  function searchValidation()
  {

    	var searchword1=document.expertsearch.combine.value;
    
    	if((searchword1=="") || (searchword1==null)){
    		window.alert("Enter at least one term to search in the database.");
    		return false;
    	}
    
    	if(!(searchword1=="")){
    
    		if(searchword1.substring(0,1) == '*') {
    			window.alert("Search word cannot begin with * character.");
    			return (false);
    		}
    
    		var searchLength= searchword1.length;
    		var tempword = searchword1;
    		var tempLength=0;
    
    		while (tempword.substring(0,1) == ' ') {
    			tempword = tempword.substring(1);
    			tempLength = tempLength + 1;
    		}
    
    		if ( searchLength == tempLength) {
    			window.alert("Enter at least one term to search in the database.");
    			return (false);
    		}
    
    	}
    
		return true;
	}

function calculateMask(control) 
{

	var selectedDbMask = 0;

	// CALCULATE SELECTED DB MASK
	if(document.expertsearch.alldb.checked == true) 
	{
		selectedDbMask = eval(document.expertsearch.alldb.value);
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

    function clearDatabase()
    {
    	for(var i=0; i<document.expertsearch.database.length; i++) 
    	{ 
    		if(document.expertsearch.database[i].checked)
    		{
    			document.expertsearch.database[i].checked=false;
    		}
    	}
    }
    function clearAlldb()
    {	
    	if(document.expertsearch.alldb.checked)
    	{
    		document.expertsearch.alldb.checked = false;
    	}
    }

    function change(db) 
    {		
    	if(db == 'database')
    	{
    		clearAlldb();		
    		selectedDbMask = calculateMask(document.expertsearch.database);
    	}
    	else
    	{	
    		clearDatabase();
    		selectedDbMask = calculateMask(document.expertsearch.alldb);
    	}
    	
    }

		function updateUI(){
    }

    function doReset()
    {
		document.expertsearch.combine.value="";
		document.expertsearch.sort[0].checked=true;
		self.focus();
    }
