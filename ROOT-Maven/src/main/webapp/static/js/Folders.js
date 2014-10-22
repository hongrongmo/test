// Initialize form handling
$(function() {
	$(".folderbtn").click(function(event) {
		if (!validate(document.forms["folderform"], $("#foldercount").val())) {
			event.preventDefault();
		} else {
			GALIBRARY.createWebEventWithLabel('Folders', 'Save Folder', 'Folder Name: ' + $("#foldername").val());
		}
	});

	$(".deletefolder").click(function(event){
		var agree=confirm("Are you sure you want to remove the folder and the contents in it?");
		if(!agree) event.preventDefault();
		else GALIBRARY.createWebEvent('Folders', 'Delete Folder');
	});
});

// This function validates all the fields before submission.
function validate(folder, foldercount) {

	// Empty name?
	if (!$.trim(folder.foldername.value)) {
		alert("Please enter a new folder name.");
		folder.foldername.focus();
		return false;

	// No special characters
	} else {
		var foldername = folder.foldername.value;
		var valid = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
		var temp;
		for ( var i = 0; i < foldername.length; i++) {
			temp = foldername.substring(i, i + 1);
			if ((valid.indexOf(temp) == "-1") && (temp != ' ')) {
				window
						.alert("Special Characters are not allowed in folder names. Please try another name.");
				folder.foldername.focus();
				return false;
			}
		}

	}
	// Do not allow same name
	var matched = false;
	$("input[name=FOLDER-NAME]").each(function(index){
		if (this.value.toLowerCase() == folder.foldername.value.toLowerCase()) {
			window.alert("Folder name already exists. Please try another name.");
			folder.foldername.focus();
			matched = true;
			return false;
		}
	});
	if (matched) return false;

	return true;
}

// this function validates the form fields before submission.
function validateFolder(savefolder)
{
	if (document.forms["savefolder"].newfoldername.value =="")
	{
			window.alert ("Please enter a new folder name.");
			document.forms["savefolder"].newfoldername.focus();
			return false;
	}
	else
	{
			var folderLength= document.forms["savefolder"].newfoldername.value.length;
			var tempWord = document.forms["savefolder"].newfoldername.value;
			var tempLength=0;

			while (tempWord.substring(0,1) == ' ')
			{
			   tempWord = tempWord.substring(1);
			   tempLength = tempLength + 1;
			}

			if ( folderLength == tempLength)
			{
			   window.alert("Spaces are not allowed as folder name.");
			   document.forms["savefolder"].newfoldername.focus();
			   return false;
			}
			var foldername= document.forms["savefolder"].newfoldername.value;
			var valid = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
			var temp = null;
			for (var i=0; i<foldername.length; i++)
			{
				temp = foldername.substring(i, i+1);
				if ((valid.indexOf(temp) == "-1") && ( temp != ' '))
				{
					window.alert("Special Characters are not allowed in folder names. Please try another name.");
					document.forms["savefolder"].newfoldername.focus();
					return false;
				}
			}
	 }
	 var hiddensize = document.forms["savefolder"].elements.length;
	 var i = 0;
	 for( i=0 ; i < hiddensize ; i++)
	 {
			var nameOfElement = document.forms["savefolder"].elements[i].name;
			var valueOfElement = document.forms["savefolder"].elements[i].value;

			if((nameOfElement.search(/FOLDER-NAME/)!=-1) && (valueOfElement != ""))
			{
				if(valueOfElement.toLowerCase() == document.forms["savefolder"].newfoldername.value.toLowerCase())
				{
					window.alert("Folder name already exists. Please try another name.");
					document.forms["savefolder"].newfoldername.focus();
					return false;
				}
			}
	 }
	 return true;
}


