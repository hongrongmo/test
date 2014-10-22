var curIndex = new Object();
curIndex.fieldDefault = "NULL";

// Associative array of lookup mappings - Maps this pages lookup
// dropdown values to the Section dropdowns on quick search form
var lookuptosection = new Array();
lookuptosection["CVS"] = "CV";
lookuptosection["ST"] = "ST";
lookuptosection["AUS"] = "AU";
lookuptosection["PN"] = "PN";
lookuptosection["AAFF"] = "AAFF";
lookuptosection["AF"] = "AF";
lookuptosection["FJT"] = "FJT";
lookuptosection["PUB"] = "PUB";
lookuptosection["PC"] = "PC";
lookuptosection["PID"] = "PID";

//
// Initialization
//
$(document).ready(function() {
	lookup = $("select[name='lookup']");
	booleanlookup = $("input[name='lookup']");
	searchtype = $("input[name='searchtype']");

	// This is called just after the form loads. The function choose the methods
	// according to search type.
	if (searchtype.val() == "Quick") {
		updatechecks();
	}
	if (searchtype.val() == "Expert") {
		expertchecks();
	}

	lookup.find("option").each(function(idx) {
		if ($(this).val() == curIndex.name)
			$(this).attr('selected', 'true');
	});

	//
	// Handle checkbox select (paste/unpaste)
	//
	$("input[name='selectedchar']").click(function(event) {
		var cbox = $(this);
		if (cbox.is(":checked") && cbox.value != "") {
			// PASTE!
			if (searchtype.val() == "Quick") {
				paste(cbox.val());
			} else if (searchtype.val() == "Expert") {
				expertpaste(cbox.val());
			}
		} else {
			// UNPASTE!
			if (searchtype.val() == "Quick") {
				quickunpaste(cbox.val());
			} else if (searchtype.val() == "Expert") {
				expertunpaste(cbox.val());
			}
		}
	});

	//
	// Handle change of lookup item
	//
	lookup.change(function(event) {
		document.location = $(this).find(":selected").attr('href');
	});
});

// The function sets the boolean values in search form according to selected
// boolean value in lookup index form and for reseting the boolean values when no values
// are selected in lookup index form.
function doboolean(searchtype) {
	var curOp;
	if (searchtype == "Quick") {
		for ( var j = 0; j < document.lookup_box.lookup.length; j++) {
			if (document.lookup_box.lookup[j].checked == true) {
				curOp = document.lookup_box.lookup[j].value;
			}
		}
		if (self.opener.document.quicksearch) {
			if ((self.opener.document.quicksearch.searchWord1.value != "")
					&& (self.opener.document.quicksearch.searchWord2.value != "")) {
				curOp1 = 1;
			} else {
				if ((self.opener.document.quicksearch.searchWord2.value == "")
						&& (self.opener.document.quicksearch.searchWord3.value == "")) {
					for ( var i = 0; i < self.opener.document.quicksearch.boolean1.length; i++) {
						if (self.opener.document.quicksearch.boolean1[i].value == curOp) {
							self.opener.document.quicksearch.boolean1[i].selected = true;
						}
					}
				}
			}
			for ( var i = 0; i < self.opener.document.quicksearch.boolean2.length; i++) {
				if (self.opener.document.quicksearch.boolean2[i].value == curOp) {
					self.opener.document.quicksearch.boolean2[i].selected = true;
				}
			}
		}
	}
}

// The function paste the selected checkbox value in search form field for Quick Search.
function paste(term) {
	if (self.opener.document.quicksearch) {
		var searchword = '{' + cleanTerm(term) + '}';
		var currentSection = lookuptosection[lookup.find("option:selected")
				.val()];
		var currentBooleanValue = booleanlookup.filter(":checked").val();
		var quicksearchwords = window.opener.jQuery(".searchword");

		// See if the current word is already on the form
		var found = false;
		quicksearchwords.each(function(idx) {
			if ($(this).val() == searchword) {
				found = true;
				return false;
			}
		});
		if (found)
			return;

		// Nope, find empty one to paste into
		var pasted = false;
		quicksearchwords.each(function(idx) {
			var quicksearchword = $(this);
			if ((/^\s+$/.test(quicksearchword.val()))
					|| (quicksearchword.val() == "")) {
				pasted = true;
				quicksearchword.val(searchword);
				// Change section and boolean (if appropriate)
				window.opener.jQuery(".section").eq(idx).find(
						"option[value='" + currentSection + "']").attr(
						"selected", "true");
				if (idx > 0)
					window.opener.jQuery(".boolean").eq(idx - 1).find(
							"option[value='" + currentBooleanValue + "']")
							.attr('selected', 'true');
				return false;
			}
		});

		// Went through all and nowhere to paste - see if we can add a field
		if (!pasted) {
			var added = window.opener.addSearchField(searchword,
					currentBooleanValue, currentSection);
			if (!added) {
				// At the max, first see if we need to un-check a box on current page
				var quicksearchword = quicksearchwords.eq(11);
				$("input[name='selectedchar']:checked").each(function(idx) {
					var checkme = '{' + cleanTerm($(this).attr('value')) + '}';
					if (checkme == quicksearchword.val()) {
						$(this).removeAttr('checked');
						return false;
					}
				});

				// Now paste new values into quick search form
				quicksearchword.val(searchword);
				window.opener.jQuery(".section").eq(11).find(
						"option[value='" + currentSection + "']").attr(
						"selected", "true");
				window.opener.jQuery(".boolean").eq(10).find(
						"option[value='" + currentBooleanValue + "']").attr(
						'selected', 'true');
			}
		}
	}
}

// The function delete the unchecked checkbox value from search form field for Quick Search.
function quickunpaste(term) {
	if (self.opener.document.quicksearch) {
		var searchword = '{' + cleanTerm(term) + '}';
		var quicksearchwords = window.opener.jQuery(".searchword");
		var quicksections = window.opener.jQuery(".section");
		var quickbooleans = window.opener.jQuery(".boolean");

		// Go through search fields on quick form and remove selected
		var unpasted = false;
		quicksearchwords.each(function(idx) {
			if ($(this).val() == searchword) {
				unpasted = true;
				$(this).val('');
				quicksections.eq(idx).find("option:first-child").attr(
						'selected', 'selected');
				if (idx > 0) {
					quickbooleans.eq(idx - 1).find("option:first-child").attr(
							'selected', 'selected');
				}
			}
		});

		// If we un-pasted something, see if we need to shift entries up!
		if (unpasted) {
			var len = quicksearchwords.length;
			for ( var i = 0; i < len - 1; i++) {
				if (quicksearchwords.eq(i).val() == '') {
					// Look ahead to find non-empty and copy here
					for ( var j = i + 1; j < len; j++) {
						if (quicksearchwords.eq(j).val() != '') {
							// Copy found entry
							quicksearchwords.eq(i).val(
									quicksearchwords.eq(j).val());
							quicksections.eq(i).find(
									"option[value='"
											+ quicksections.eq(j).find(
													"option:selected").val()
											+ "']")
									.attr('selected', 'selected');
							if (i > 0) {
								quickbooleans.eq(i - 1).find(
										"option[value='"
												+ quickbooleans.eq(j - 1).find(
														"option:selected")
														.val() + "']").attr(
										'selected', 'selected');
							}

							// Clear out old entry
							quicksearchwords.eq(j).val('');
							quicksections.eq(j).find("option:first-child")
									.attr('selected', 'selected');
							quickbooleans.eq(j - 1).find("option:first-child")
									.attr('selected', 'selected');

							break;
						}
					}
				}
			}
		}
	}

}

// The function paste the selected checkbox value in search form field for Expert Search.
function expertpaste(term) {
	var SelBoolean;
	if (self.opener.document.quicksearch.searchWord1) {
		for ( var j = 0; j < document.lookup_box.lookup.length; j++) {
			if (document.lookup_box.lookup[j].checked == true) {
				SelBoolean = document.lookup_box.lookup[j].value;
			}
		}

		var searchword = '{' + cleanTerm(term) + '}';
		if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "CVS") {
			curIndex.field = "CV";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "ST") {
			curIndex.field = "ST";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "AUS") {
			curIndex.field = "AU";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "AF") {
			curIndex.field = "AF";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "PN") {
			curIndex.field = "PN";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "AAFF") {
			curIndex.field = "AAFF";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "FJT") {
			curIndex.field = "FJT";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "PUB") {
			curIndex.field = "PUB";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "DT") {
			curIndex.field = "DT";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "TR") {
			curIndex.field = "TR";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "LA") {
			curIndex.field = "LA";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "DI") {
			curIndex.field = "DI";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "RTYPE") {
			curIndex.field = "DT";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "TRMC") {
			curIndex.field = "TR";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "PC") {
			curIndex.field = "PC";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "PID") {
			curIndex.field = "PID";
		}

		term = cleanTerm(term);
		if ((!/^\s+$/.test(self.opener.document.quicksearch.searchWord1.value))
				&& (self.opener.document.quicksearch.searchWord1.value != "")) {
			if (!((self.opener.document.quicksearch.searchWord1.value)
					.indexOf(searchword) > 0)) {
				self.opener.document.quicksearch.searchWord1.value = self.opener.document.quicksearch.searchWord1.value
						+ " "
						+ SelBoolean
						+ " (({"
						+ term
						+ "}) WN "
						+ curIndex.field + ")";
			}
		} else {
			self.opener.document.quicksearch.searchWord1.value = "(({" + term
					+ "}) WN " + curIndex.field + ")";
		}

	}
}

// The function delete the unchecked checkbox value from search form field for Expert Search.
function expertunpaste(term) {
	var searchword1;
	var newterm;
	var available;
	if (self.opener.document.quicksearch) {
		var source = self.opener.document.quicksearch;
		searchword1 = self.opener.document.quicksearch.searchWord1.value;

		if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "CVS") {
			curIndex.field = "CV";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "ST") {
			curIndex.field = "ST";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "AUS") {
			curIndex.field = "AU";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "AF") {
			curIndex.field = "AF";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "PN") {
			curIndex.field = "PN";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "AAFF") {
			curIndex.field = "AAFF";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "FJT") {
			curIndex.field = "FJT";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "PUB") {
			curIndex.field = "PUB";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "AFI") {
			curIndex.field = "AF";
		} else if (document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "PNI") {
			curIndex.field = "PN";
		}

		term = cleanTerm(term);
		newterm = "(({" + term + "}) WN " + curIndex.field + ")";

		available = searchword1.indexOf(newterm);

		if (available >= 0) {
			var tempSearchWord;
			var newSearchWord1;

			var newtermend = available + newterm.length;
			var stringBeforeNewterm = searchword1.substring(0, available);
			var stringAfterNewterm = searchword1.substring(newtermend);

			if (available > 0) {
				tempSearchWord = removeAtEnd(stringBeforeNewterm);
				newSearchWord1 = tempSearchWord + stringAfterNewterm;
			} else {
				tempSearchWord = removeAtBegin(stringAfterNewterm);
				newSearchWord1 = stringBeforeNewterm + tempSearchWord;
			}

			if (tempSearchWord == null) {
				newSearchWord1 = stringBeforeNewterm + stringAfterNewterm;
			}

			self.opener.document.quicksearch.searchWord1.value = newSearchWord1;

		}

	}
}

function removeAtBegin(replaceString) {
	var afterReplaceBoolean;

	if (replaceString.indexOf(" AND ") == 0) {
		afterReplaceBoolean = replaceString.substring(5);
	}
	if (replaceString.indexOf(" OR ") == 0) {
		afterReplaceBoolean = replaceString.substring(4);
	}
	return afterReplaceBoolean;
}

function removeAtEnd(replaceString) {
	var afterReplaceBoolean;
	if (replaceString.lastIndexOf(" AND ") > 0) {
		afterReplaceBoolean = replaceString.substring(0, replaceString
				.lastIndexOf(" AND "));
	}
	if (replaceString.lastIndexOf(" OR ") > 0) {
		afterReplaceBoolean = replaceString.substring(0, replaceString
				.lastIndexOf(" OR "));
	}
	return afterReplaceBoolean;
}

function updatechecks() {
	$("input[name='selectedchar']").removeAttr('checked');
	if (self.opener.document.quicksearch) {
		$("input[name='selectedchar']").each(function(idx) {
			var selectedchar = $(this);
			var checkme = "{" + cleanTerm(selectedchar.val()) + "}";
			self.opener.jQuery('.searchword').each(function(idy) {
				if ($(this).val() == checkme) {
					selectedchar.attr('checked', 'checked');
				}
			});
		});
	}
}

function expertchecks() {
	if (self.opener.document.quicksearch.searchWord1) {
		if (document.pastelist.selectedchar != null) {
			for ( var i = 0; i < document.pastelist.selectedchar.length; i++) {
				var searchword = '({'
						+ cleanTerm(document.pastelist.selectedchar[i].value)
						+ '})';
				var textareavalue = self.opener.document.quicksearch.searchWord1.value;
				if (textareavalue.indexOf(searchword) > 0) {
					document.pastelist.selectedchar[i].checked = true;
				} else {

					document.pastelist.selectedchar[i].checked = false;
				}
			}
		}
	}
}

function validation() {
	var searchword = document.lookupform.searchWord.value;

	if ((searchword == "") || (searchword == null)) {
		window.alert("Enter at least one term to search in the indexes.");
		return false;
	}

	if (!(searchword == "")) {

		var searchLength = searchword.length;
		var tempword = searchword;
		var tempLength = 0;

		while (tempword.substring(0, 1) == ' ') {
			tempword = tempword.substring(1);
			tempLength = tempLength + 1;
		}

		if (searchLength == tempLength) {
			window.alert("Enter at least one term to search in the indexes.");
			return (false);
		}

	}
	return true;

}

// search and replace strings
function SearchAndReplace(Content, SearchFor, ReplaceWith) {

	var tmpContent = Content;
	var tmpBefore = new String();
	var tmpOutput = new String();
	var intBefore = 0;

	if (SearchFor.length == 0)
		return;

	while (tmpContent.toUpperCase().indexOf(SearchFor.toUpperCase()) > -1) {

		// Get all content before the match
		intBefore = tmpContent.toUpperCase().indexOf(SearchFor.toUpperCase());
		tmpBefore = tmpContent.substring(0, intBefore);
		tmpOutput = tmpOutput + tmpBefore;

		// Get the string to replace
		tmpOutput = tmpOutput + ReplaceWith;

		// Get the rest of the content after the match until
		// the next match or the end of the content
		intAfter = tmpContent.length - SearchFor.length + 1;
		tmpContent = tmpContent.substring(intBefore + SearchFor.length);

	}

	return tmpOutput + tmpContent;

}

// clean term of unwanted characters
function cleanTerm(Content) {
	var tmpContent = Content;
	var tmpOutput = new String();
	tmpOutput = SearchAndReplace(tmpContent, "(", " ");
	tmpOutput = SearchAndReplace(tmpOutput, ")", " ");
	tmpOutput = SearchAndReplace(tmpOutput, "{", " ");
	tmpOutput = SearchAndReplace(tmpOutput, "}", " ");
	tmpOutput = SearchAndReplace(tmpOutput, "$", " ");
	tmpOutput = SearchAndReplace(tmpOutput, "*", " ");
	tmpOutput = SearchAndReplace(tmpOutput, '"', " ");
	return tmpOutput;
}