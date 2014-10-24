//To validate that user types in a name
function checkfolderforform(savetofolder) 
{
	if(typeof(savetofolder.choose) != 'undefined')  
	{
		if (savetofolder.choose[0].checked == true) 
		{
			// 1, 2 or 3 folder variation - folder name in select box chosen
			return validateSelectedFolder(savetofolder);			
		}
		else if(savetofolder.choose[1].checked == true) 
		{
			// 1, 2 - create folder text box chosen
			return validateFolderName(savetofolder);					
		}
	}
	else if(typeof(savetofolder.foldername) != 'undefined') 
	{
		// 0 folder variation - just text box
		return validateFolderName(savetofolder);
	}
	else 
	{
		// 3 folder variation - folder name in select box chosen
		return validateSelectedFolder(savetofolder);			
	}
}

// To validate that user types in a name 
function validateFolderName(savetofolder)
{
	// unselect in case we have to return to the form
	if(typeof(savetofolder.folderid) != 'undefined') 	
	{
		for(i=0;i<savetofolder.folderid.length; i++)
		{
			savetofolder.folderid.options[i].selected = false;
		}
	}
	
	var stripped = stripWhitespace(savetofolder.foldername.value)
	
	if (isWhitespace(stripped))
	{
		window.alert("Please enter a folder name to save");
		savetofolder.foldername.focus();
		return false;
	}
	// if there are existing foldernames
	if(typeof(savetofolder.folderid) != 'undefined') 	
	{
		// cycle through existing folder names looking for duplicates
		var existingFodlers = savetofolder.folderid.options.length
		var cap = stripped.toUpperCase();
		for(var i = 0; i<existingFodlers; i++)
		{
			var foldername = savetofolder.folderid.options[i].text;
			var capfoldername = foldername.toUpperCase();
			if(capfoldername == cap)
			{
				window.alert ("Folder name already exists, choose another folder name");
				return false;
			}
		}
	}
	// check for illegal characters!!
	for(var i = 0; i < stripped.length; i++)
	{
		if(badchars.indexOf(stripped.charAt(i)) != -1)
		{
			window.alert ("Folder name cannot contain a " + stripped.charAt(i));
			return false;
		}
	}

	return true;
}


function validateSelectedFolder(savetofolder)
{
	var folderid = savetofolder.folderid.value;
	var flag = false;

	// constant - max allowed in folder
	var maxfoldersize=savetofolder.maxfoldersize.value;
	var intmaxfoldersize=parseInt(maxfoldersize,10);

	// count of selected records or documents in basket
	var doccount=savetofolder.documentcount.value;
	var intdoccount=parseInt(doccount,10);
	
	for(i=0;i<savetofolder.folderid.length; i++)
	{
		// check selected folder to see if it has enough room
		// for all of the documents
		if (savetofolder.folderid.options[i].selected)
		{
			var foldersize=savetofolder.folderid.options[i].size;
			var intfoldersize=parseInt(foldersize);

			if((intfoldersize+intdoccount)>intmaxfoldersize)
			{
				window.alert("You reached the maximum number of records that can be saved to this folder, please select another folder");
				return false;
			}
			flag = true;
		}
	}

	if(!flag)
	{
		window.alert(" Please select a Folder ");
		return false;
	}

}


// onfocus event handlers
// to check radio button next to select box or text input
// when user directly selects select box or text input control
function selectradiotext()
{
	document.savetofolder.choose[1].checked=true;
}
function selectradiodrop()
{
	document.savetofolder.choose[0].checked=true;
}


// Javascript 1.0 Boilerplate Form Validation code from
// http://developer.netscape.com/docs/examples/javascript/regexp/overview.html

// whitespace characters
var whitespace = " \t\n\r";
var badchars = "&";

// Check whether string s is empty.
function isEmpty(s)
{   return ((s == null) || (s.length == 0))
}

// Removes all whitespace characters from s.
// Global variable whitespace (see above)
// defines which characters are considered whitespace.

function stripWhitespace (s)
{   return stripCharsInBag (s, whitespace)
}

// Returns true if string s is empty or 
// whitespace characters only.
function isWhitespace (s)
{   var i;

    // Is s empty?
    if (isEmpty(s)) return true;

    // Search through string's characters one by one
    // until we find a non-whitespace character.
    // When we do, return false; if we don't, return true.

    for (i = 0; i < s.length; i++)
    {   
        // Check that current character isn't whitespace.
        var c = s.charAt(i);

        if (whitespace.indexOf(c) == -1) return false;
    }

    // All characters are whitespace.
    return true;
}

function stripCharsInBag (s, bag)
{   var i;
    var returnString = "";

    // Search through string's characters one by one.
    // If character is not in bag, append to returnString.

    for (i = 0; i < s.length; i++)
    {   
        // Check that current character isn't whitespace.
        var c = s.charAt(i);
        if (bag.indexOf(c) == -1) returnString += c;
    }

    return returnString;
}
