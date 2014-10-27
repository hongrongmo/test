		function addRemoveFromClipBoard(t)
		{
			var selectList = document.forms["clipboard"].clip;
			var selectListLength = selectList.length;
			var remove = -1;
			var i = 0;

			for(i = 0; i<selectListLength; i++)
			{
				var term1 = t;
				var term2 = replaceSubstring(term1,"(", "");
				var term3 = replaceSubstring(term2,")", "");

				var sTerm = selectList.options[i].text;
				var term = term3;

				if(sTerm == "")
				{
					break;
				}
				else
				{
					if(sTerm == term)
					{
						remove = i;
					}
				}
			}

			if(remove > -1)
			{
				var a = selectListLength - 1;
				selectList.options[a] = new Option("", "");
				var b = selectListLength;
				selectList.options[b] = new Option("____________________________________________________", "____________________________________________________");
				selectList.options[remove] = null;

				if(typeof(parent.mainFrame) != "undefined")
				{
					parent.mainFrame.clearTerms();
					parent.mainFrame.checkTerms();
				}

			}
			else
			{

				selectList.options[i] = new Option(term, term);

				if(typeof(parent.mainFrame) != "undefined")
				{
					parent.mainFrame.clearTerms();
					parent.mainFrame.checkTerms();
				}

			}
		}

		function resetForm()
		{
			if(typeof(parent.frames[0]) != "undefined")
			{
				var selectList = document.forms[0].clip;
				var selectListLength = selectList.length;

				for(i = 0; i<selectListLength - 2; i++)
				{
					selectList.options[i] = new Option("", "");
				}

				selectList.options[selectListLength -1] = new Option("____________________________________________________", "____________________________________________________");

				document.clipboard.doctype[0].selected=true;
				document.clipboard.treatmentType[0].selected=true;

				if(typeof(document.clipboard.disciplinetype) != "undefined")
				{
					document.clipboard.disciplinetype[0].selected=true;
				}

				document.clipboard.language[0].selected=true;
				document.clipboard.endYear[document.clipboard.endYear.length -1].selected=true;
				document.clipboard.andor[1].checked=true;
				document.clipboard.sort[0].checked=true;
				document.clipboard.yearselect[0].checked=true;
				document.clipboard.startYear[0].selected=true;

				if(typeof(parent.mainFrame) != "undefined")
				{
					parent.mainFrame.clearTerms();
				}

			}
			else
			{

				// refine search form reset takes you back to the thesaurus home
				document.forms["clipboard"].action="/controller/servlet/Controller?CID=thesHome"
				document.forms["clipboard"].submit();
			}
		}


		function removeSelected()
		{
			var selectList = document.forms["clipboard"].clip;
			var selectListLength = selectList.length;
			var i = 0;

			for(i = 0; i<selectListLength; i++)
			{

				if(selectList.options[i].selected)
				{
					var a = selectListLength - 1;
					selectList.options[a] = new Option("", "");
					var b = selectListLength;
					selectList.options[b] = new Option("____________________________________________________", "____________________________________________________");
					selectList.options[i] = null;

					if(typeof(parent.mainFrame) != "undefined")
					{
						parent.mainFrame.clearTerms();
						parent.mainFrame.checkTerms();
					}

					removeSelected();
				}

			}

		}

		function checkForm(form)
		{
			var list = form.clip;
			var sumList = "((";
			var andor ;
			var listlen ;
			for (var i=0; i < form.andor.length; i++)
			{
				if (  form.andor[i].checked)
				{
			 		andor = form.andor[i].value;
			 	}
			}

			for (var i=0; i< list.length;i++)
			{
				if (((list.options[i].text != null)&&(list.options[i].text != ""))&&(list.options[i].text != '____________________________________________________') )
				{
					listlen = i;
				}
			}

			for ( var i=0; i <= listlen ; i++)
			{
				if (i < listlen)
				{
					sumList = sumList + "{" + list.options[i].text +"} " + andor + " ";
				}
				else if( i==listlen )
				{
					sumList = sumList +  "{" + list.options[i].text +"}) WN CV)" ;
				}
			}

			return sumList;
		}

		function runSearch1(thisForm)
		{
			var var_db;

			thisForm.searchWord1.value = checkForm(thisForm);
			if(typeof(parent.frames.length) != "undefined" &&
			   parent.frames.length > 0)
			{
				if(typeof(parent.frames[0].document.forms["search"].database[0]) != "undefined")
				{

					if ( parent.frames[0].document.forms["search"].database[0].checked)
					{

						var_db = parent.frames[0].document.forms["search"].database[0].value;
					}
					else
					{
						var_db = parent.frames[0].document.forms["search"].database[1].value;
					}
				}
				else
				{
					var_db = parent.frames[0].document.forms["search"].database.value;

				}
			}
			else
			{
				var_db = "skip";
			}

			if(var_db != "skip")
			{
				if (var_db == "inspec") {
					thisForm.database.value = "INSPEC";
				} else  if (var_db == "cpx" ) {
					thisForm.database.value = "Compendex";
				} else {
					thisForm.database.value = var_db;
				}
			}

			return true;
		}


		function runValidate()
		{
			if(document.clipboard.yearselect[0].checked)
			{
				if( parseInt(document.clipboard.startYear.value) > parseInt(document.clipboard.endYear.value))
				{
					window.alert("Start year should be less than or equal to End year");
					return false;
				}
			}
			if(document.forms["clipboard"].clip[0].text == "")
			{
				alert("Please select at least one term to search in the database. ");
				return false;
			}
			else
			{

				return runSearch1(document.forms["clipboard"]);
			}


		}

		function replaceSubstring(inputString, fromString, toString)
		{
			// Goes through the inputString and replaces every occurrence of fromString with toString
			var temp = inputString;
			if (fromString == "")
			{
				return inputString;
			}
			if (toString.indexOf(fromString) == -1)
			{
				// If the string being replaced is not a part of the replacement string (normal situation)
				while (temp.indexOf(fromString) != -1)
				{
					var toTheLeft = temp.substring(0, temp.indexOf(fromString));
					var toTheRight = temp.substring(temp.indexOf(fromString)+fromString.length, temp.length);
					temp = toTheLeft + toString + toTheRight;
				}
			}
			else
			{
				// String being replaced is part of replacement string (like "+" being replaced with "++") - prevent an infinite loop
				var midStrings = new Array("~", "`", "_", "^", "#");
				var midStringLen = 1;
				var midString = "";
				// Find a string that doesn't exist in the inputString to be used
				// as an "inbetween" string
				while (midString == "")
				{
					for (var i=0; i < midStrings.length; i++)
					{
						var tempMidString = "";
						for (var j=0; j < midStringLen; j++)
						{
							tempMidString += midStrings[i];
						}
						if (fromString.indexOf(tempMidString) == -1)
						{
							midString = tempMidString;
							i = midStrings.length + 1;
						}
					}
				}
				// Keep on going until we build an "inbetween" string that doesn't exist
				// Now go through and do two replaces - first, replace the "fromString" with the "inbetween" string
				while (temp.indexOf(fromString) != -1)
				{
					var toTheLeft = temp.substring(0, temp.indexOf(fromString));
					var toTheRight = temp.substring(temp.indexOf(fromString)+fromString.length, temp.length);
					temp = toTheLeft + midString + toTheRight;
				}
				// Next, replace the "inbetween" string with the "toString"
				while (temp.indexOf(midString) != -1)
				{
					var toTheLeft = temp.substring(0, temp.indexOf(midString));
					var toTheRight = temp.substring(temp.indexOf(midString)+midString.length, temp.length);
					temp = toTheLeft + toString + toTheRight;
				}
			}
			// Ends the check to see if the string being replaced is part of the replacement string or not
			return temp;
			// Send the updated string back to the user
		}
		// Ends the "replaceSubstring" function


		// select year range 1969-1976 for document type is Patent.
		function checkPatent(quicksearch)
		{
			if(quicksearch.doctype.options[quicksearch.doctype.selectedIndex].value=='PA')
			{
				quicksearch.startYear.selectedIndex=0;

                if(typeof(parent.frames[0]) != 'undefined')
                {
    				if ( parent.frames[0].document.forms["search"].database[0].checked)
    				{
    					var_db = parent.frames[0].document.forms["search"].database[0].value;
    				}
    				else
    				{
    					var_db = parent.frames[0].document.forms["search"].database[1].value;
    				}
                }
                else
                {
                    var_db = document.forms["clipboard"].database.value;
                }

                if(var_db == '1') // C84 (or CPX - but there is no 'PA' for CPX, only C84)
                {
    				quicksearch.endYear.selectedIndex=(quicksearch.endYear.length - ((2006 - 1969) + 1));
                }
                if(var_db == '2') // INSPEC
                {
    				quicksearch.endYear.selectedIndex=(quicksearch.endYear.length - ((2006 - 1976) + 1));
                }

				quicksearch.yearselect[0].checked=true
			}
			else
			{
				quicksearch.startYear.selectedIndex=0;
				quicksearch.endYear.selectedIndex=quicksearch.endYear.length-1;
			}
		}

