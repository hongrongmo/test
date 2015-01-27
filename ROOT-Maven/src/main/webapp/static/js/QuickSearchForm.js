var CPX = 1;
var INS = 2;
var CPX_INS = 3;
var NTIS = 4;
var USPTO = 8;
var CRC = 16;
var C84 = 32;
var PCH = 64;
var CHM = 128;
var CBN = 256;
var ELT = 1024;
var EPT = 2048;
var IBF = 4096;
var GEO = 8192;
var EU_PATENTS = 16384;
var US_PATENTS = 32768;
var US_EU_PATENTS = 49152;
var REF = 65536
var REFEREX = 131072;
var CBF = 262144;
var IBS = 1048576;
var UPT = 524288;
var INSPEC_BACKFILE = 1048576;
var GRF = 2097152;

var startYear;
var stringYear;
var endYear;
var sec1Value;
var sec2Value;
var sec3Value;
var dtypeValue;
var trtypeValue;
var disctypeValue;
var selectedDbMask = 0;

var lookupWind;

//*************************************************************************
// Initialize the page!
//*************************************************************************
$(document)
		.ready(
				function() {

					//
					// Handle database checkbox select/deselect
					//
					$(".databasechkbox")
							.click(
									function(event) {
										var chkbox = $(this);
										// Clear checkboxes appropriately - clicking 'alldb'
										// clears all others, clicking any other clears 'alldb'
										if (chkbox.attr('name') == 'alldb') {
											$("input[name='database']")
													.removeAttr('checked');
											selectedDbMask = calculateMask();
										} else {
											$("input[name='alldb']")
													.removeAttr('checked');
											selectedDbMask = calculateMask();
										}

										// Set some values into global variables.
										if (document.quicksearch.section1.value != 'NO-LIMIT') {
											sec1Value = document.quicksearch.section1.value;
										}

										if (document.quicksearch.section2.value != 'NO-LIMIT') {
											sec2Value = document.quicksearch.section2.value;
										}

										if (document.quicksearch.section3.value != 'NO-LIMIT') {
											sec3Value = document.quicksearch.section3.value;
										}

										// updateUI will be called for the first time from here (no longer called in body:onLoad event)
										// we need to intialize these global variables
										endYear = calEndYear(selectedDbMask);
										stringYear = document.quicksearch.stringYear.value;
										startYear = document.quicksearch.startYear[document.quicksearch.startYear.selectedIndex].value;
										if (document.quicksearch.doctype) {
											dtypeValue = document.quicksearch.doctype[document.quicksearch.doctype.selectedIndex].value;
										}
										if (document.quicksearch.treatmentType) {
											trtypeValue = document.quicksearch.treatmentType[document.quicksearch.treatmentType.selectedIndex].value;
										}
										if (document.quicksearch.disciplinetype) {
											disctypeValue = document.quicksearch.disciplinetype[document.quicksearch.disciplinetype.selectedIndex].value;
										}

										updateUI(selectedDbMask, startYear,
												stringYear, endYear, sec1Value,
												sec2Value, sec3Value,
												dtypeValue, trtypeValue,
												disctypeValue);
									});

					//
					// Handle add search field link
					//
					$("#addsearchllink")
							.click(
									function(event) {
										event.preventDefault();
										addSearchField('');
										if (acSessionEnabled) {
											checkACField($('.section:visible')[$('.section:visible').length - 1]);
										}
										GALIBRARY.createWebEventWithLabel(
												'Add Search Field',
												"Search Field Added",
												"Search Field Added");
									});

					//
					// Handle remove search field link
					//
					$(".removesearchlink").click(function(event) {
						event.preventDefault();
						removeSearchField(this);
					});

					//
					// Handle browse index links
					//
					$(".browseindexlookup")
							.click(
									function(event) {

										event.preventDefault();

										var lookupindex = $(this).attr(
												'lookupindex');
										selectedDbMask = calculateMask(document.quicksearch.database);
										if (selectedDbMask == 0)
											return;

										if (((selectedDbMask & NTIS) == NTIS)
												&& ((lookupindex == 'ST') || (lookupindex == 'PN'))) {
											return;
										} else {
											var tabloc = "/search/browseindex.url?database="
													+ (selectedDbMask)
													+ "&lookup="
													+ (lookupindex)
													+ "&searchtype=Quick";

											if (!lookupWind
													|| lookupWind.closed) {
												lookupWind = window
														.open(
																tabloc,
																"LookupWin",
																"height=500,width=500,top=0,left="
																		+ ((screen.width * .35))
																		+ ",scrollbars=yes,menubar=no,resizable=yes,toolbar=no,location=no,directories=no");
											} else {
												window.lookupWind.location = tabloc;
												window.lookupWind.focus();
											}
										}

									});

				});

/*********************************************************
 * Add a search field line to the form
 */
function addSearchField(searchtext, connector, section) {
	// Get the current number of search fields on the page
	// REMEMBER this includes the element inside of .addsearchfieldcopy!!

	var numfields = $('.searchword').length;
	if (!searchtext || searchtext == undefined)
		searchtext = '';
	if (!connector || connector == undefined)
		connector = '';
	if (!section || section == undefined)
		section = '';

	// Display error message when we reach limit
	if (numfields == 12) {
		showSearchFieldError();
		return false;
	}

	var nextNumfields = Number(numfields) + 1;
	var addsearchfieldcopy = $('.addsearchfieldcopy').clone(true);
	addsearchfieldcopy.toggleClass('addsearchfieldcopy addsearchfielddiv')
			.show();
	addsearchfieldcopy.find(".searchwordcopy").toggleClass(
			"searchwordcopy searchword").val(searchtext);
	addsearchfieldcopy.find(".section option[value='" + section + "']").attr(
			"selected", "true");
	addsearchfieldcopy.find(".boolean option[value='" + connector + "']").attr(
			"selected", "true");
	addsearchfieldcopy.find(".boolean").attr(
			"title",
			"boolean operator for combining textbox " + numfields
					+ " and textbox " + nextNumfields);
	$("#searchfields").append(addsearchfieldcopy);
	$("#resetlinkspan").show();

	return true;
}

/*********************************************************
 * Remvove a search field line
 * @param rowNum
 */
function removeSearchField(link) {
	hideSearchFieldError();
	$(link).parents(".addsearchfielddiv").remove();

	// If there are 3 .searchword elements left
	// then we're back to the original fields, hide reset!
	var numfields = $('.searchword').length;
	if (numfields == 3) {
		$("#resetlinkspan").hide();
	}
}

/*********************************************************
 * Show the error message for trying to exceed 12 fields
 */
function showSearchFieldError() {
	$("#addsearcherror").show();
	$("#addsearchllink").hide();
	$("#addsearchllinkgray").show();
}

/*********************************************************
 * Hide the error message
 */
function hideSearchFieldError() {
	$("#addsearcherror").hide();
	$("#addsearchllink").show();
	$("#addsearchllinkgray").hide();
}

/***************************************************************
 * Calculate the current mask from database checkboxes
 */
function calculateMask() {

	var selectedDbMask = 0;
	var alldb = $("input[name='alldb']");
	var databases = $("input[name='database']");

	if (alldb.length > 0 && alldb.is(":checked")) {
		selectedDbMask = eval(alldb.val());
	} else if (databases.length > 0) {
		databases.each(function(idx) {
			if ($(this).is(":checked"))
				selectedDbMask += eval($(this).val());
		});
	}

	return selectedDbMask;
}

/***************************************************************
 * Update the current form using database mask
 *
 * @param dbMask
 * @param sYear
 * @param strYear
 * @param eYear
 * @param sec1
 * @param sec2
 * @param sec3
 * @param dtype
 * @param trtype
 * @param disctype
 */
function updateUI(dbMask, sYear, strYear, eYear, sec1, sec2, sec3, dtype,
		trtype, disctype) {
	if (!(dbMask == 8) && !(dbMask == 16)) {
		flipImage(dbMask);

		searchin = generateSection(dbMask);
		doctypes = generateDoctypes(dbMask);
		treattypes = generateTreattypes(dbMask);
		disciplines = generateDisciplines(dbMask);
		languages = generateLanguages(dbMask);

		switchlist(dbMask, document.quicksearch.section1, searchin, sec1);
		switchlist(dbMask, document.quicksearch.section2, searchin, sec2);
		switchlist(dbMask, document.quicksearch.section3, searchin, sec3);

		$('.addsearchfielddiv .section').each(function(idx) {
			switchlist(dbMask, this, searchin, null);
		});
		switchlist(dbMask, $('.addsearchfieldcopy .section')[0], searchin, null);

		if (document.quicksearch.doctype) {
			switchlist(dbMask, document.quicksearch.doctype, doctypes, dtype);
		}
		if (document.quicksearch.treatmentType) {
			switchlist(dbMask, document.quicksearch.treatmentType, treattypes,
					trtype);
		}
		if (document.quicksearch.disciplinetype) {
			switchlist(dbMask, document.quicksearch.disciplinetype,
					disciplines, disctype);
		}
		if (document.quicksearch.language) {
			switchlist(dbMask, document.quicksearch.language, languages, "");
		}

		clearDropdown(dbMask, document.quicksearch);

		generateYear(dbMask, sYear, strYear, eYear, document.quicksearch);

		startYear = sYear;
		stringYear = strYear;
		endYear = eYear;

		checkLastUpdates();

		sec1Value = sec1;
		sec2Value = sec2;
		sec3Value = sec3;
		dtypeValue = dtype;
		trtypeValue = trtype;
		disctypeValue = disctype;

		if (acSessionEnabled) {
			checkACFields();
		}
	}
}

// 'constructor'
function Field(value, label) {
	this.label = label;
	this.value = value;
}

/***************************************************************
 * Builds and returns an array of Field objects that represent
 * the section dropdown (Search In)
 * @param selecteddbMask
 * @returns {Array}
 */
function generateSection(selecteddbMask) {
	var searchin = new Array();
	var index = 0;

	//All fields
	if ((selecteddbMask & REFEREX) != REFEREX) {
		searchin[index++] = new Field("NO-LIMIT", "All fields");
	}

	//KY
	if ((selecteddbMask & REFEREX) != REFEREX) {
		searchin[index++] = new Field("KY", "Subject/Title/Abstract");
	} else {
		searchin[index++] = new Field("KY", "Keyword");
	}

	//AB
	if ((selecteddbMask & REFEREX) != REFEREX) {
		searchin[index++] = new Field("AB", "Abstract");
	}

	//AU
	if ((selecteddbMask & CBN) != CBN) {
		if ((selecteddbMask & US_PATENTS) != US_PATENTS
				&& (selecteddbMask & EPT) != EPT
				&& (selecteddbMask & EU_PATENTS) != EU_PATENTS) {
			searchin[index++] = new Field("AU", "Author");
		} else if (selecteddbMask == US_PATENTS || selecteddbMask == EU_PATENTS
				|| selecteddbMask == EPT || selecteddbMask == US_EU_PATENTS
				|| selecteddbMask == US_PATENTS + EPT
				|| selecteddbMask == EPT + EU_PATENTS
				|| selecteddbMask == US_EU_PATENTS + EPT) {
			searchin[index++] = new Field("AU", "Inventor");
		} else {
			searchin[index++] = new Field("AU", "Author/Inventor");
		}
	}

	//BN - for Referex - ISBN for GeoRef is added later as to preserve ordering
	if (selecteddbMask == REFEREX) {
		searchin[index++] = new Field("BN", "ISBN");
	}

	//AF
	if (((selecteddbMask & REFEREX) != REFEREX)
			&& ((selecteddbMask & CBN) != CBN)
			&& ((selecteddbMask & IBS) != IBS)) {
		if ((selecteddbMask & US_PATENTS) != US_PATENTS
				&& (selecteddbMask & EPT) != EPT
				&& (selecteddbMask & EU_PATENTS) != EU_PATENTS) {
			searchin[index++] = new Field("AF", "Author affiliation");
		} else if ((selecteddbMask == US_PATENTS)
				|| (selecteddbMask == EU_PATENTS) || (selecteddbMask == EPT)
				|| (selecteddbMask == EPT + US_PATENTS)
				|| (selecteddbMask == EPT + EU_PATENTS)
				|| (selecteddbMask == US_EU_PATENTS)
				|| (selecteddbMask == EPT + US_EU_PATENTS)) {
			searchin[index++] = new Field("AF", "Assignee");
		} else {
			searchin[index++] = new Field("AF", "Author affiliation/Assignee");
		}
	}

	// TI
	if (selecteddbMask == EPT) {
		searchin[index++] = new Field("TI", "Patent title");
	} else {
		searchin[index++] = new Field("TI", "Title");
	}

	// CL
	if ((selecteddbMask == CPX) || (selecteddbMask == CBF)
			|| (selecteddbMask == CPX + C84)) {

		searchin[index++] = new Field("CL", "Ei Classification code");
	} else if ((selecteddbMask == INS) || (selecteddbMask == GEO)
			|| (selecteddbMask == IBS) || (selecteddbMask == INS + IBF)
			|| (selecteddbMask == INS + GEO)
			|| (selecteddbMask == INS + IBF + GEO)) {
		searchin[index++] = new Field("CL", "Classification code");
	}

	//CN
	if ((selecteddbMask & CBN) != CBN && (selecteddbMask & CHM) != CHM
			&& (selecteddbMask & CRC) != CRC && (selecteddbMask & ELT) != ELT
			&& (selecteddbMask & EPT) != EPT
			&& (selecteddbMask & EU_PATENTS) != EU_PATENTS
			&& (selecteddbMask & GEO) != GEO && (selecteddbMask & NTIS) != NTIS
			&& (selecteddbMask & REFEREX) != REFEREX
			&& (selecteddbMask & PCH) != PCH && (selecteddbMask & REF) != REF
			&& (selecteddbMask & US_PATENTS) != US_PATENTS
			&& (selecteddbMask & UPT) != UPT
			&& (selecteddbMask & USPTO) != USPTO
			&& (selecteddbMask & IBS) != IBS) {
		searchin[index++] = new Field("CN", "CODEN");
	}

	//CF
	if ((selecteddbMask & CBN) != CBN && (selecteddbMask & CHM) != CHM
			&& (selecteddbMask & CRC) != CRC && (selecteddbMask & EPT) != EPT
			&& (selecteddbMask & EU_PATENTS) != EU_PATENTS
			&& (selecteddbMask & GEO) != GEO && (selecteddbMask & NTIS) != NTIS
			&& (selecteddbMask & REFEREX) != REFEREX
			&& (selecteddbMask & PCH) != PCH && (selecteddbMask & REF) != REF
			&& (selecteddbMask & US_PATENTS) != US_PATENTS
			&& (selecteddbMask & UPT) != UPT
			&& (selecteddbMask & USPTO) != USPTO) {
		searchin[index++] = new Field("CF", "Conference information");
	}

	//CC
	if (selecteddbMask == CPX || selecteddbMask == CPX + C84) {
		searchin[index++] = new Field("CC", "Conference code");
	}

	//BN - for GeoRef
	if (selecteddbMask == GRF) {
		searchin[index++] = new Field("BN", "ISBN");
	}

	//SN
	if ((selecteddbMask & CBF) != CBF && (selecteddbMask & CBN) != CBN
			&& (selecteddbMask & CHM) != CHM && (selecteddbMask & CRC) != CRC
			&& (selecteddbMask & ELT) != ELT && (selecteddbMask & EPT) != EPT
			&& (selecteddbMask & EU_PATENTS) != EU_PATENTS
			&& (selecteddbMask & NTIS) != NTIS
			&& (selecteddbMask & REFEREX) != REFEREX
			&& (selecteddbMask & PCH) != PCH && (selecteddbMask & REF) != REF
			&& (selecteddbMask & US_PATENTS) != US_PATENTS
			&& (selecteddbMask & UPT) != UPT
			&& (selecteddbMask & USPTO) != USPTO
			&& (selecteddbMask & IBS) != IBS) {
		searchin[index++] = new Field("SN", "ISSN");
	}

	//MH
	if (selecteddbMask == CPX || selecteddbMask == CBF
			|| selecteddbMask == (CPX + C84)) {
		searchin[index++] = new Field("MH", "Ei main heading");
	}

	//PN
	if ((selecteddbMask & GEO) != GEO
			&& (selecteddbMask & US_PATENTS) != US_PATENTS
			&& (selecteddbMask & EU_PATENTS) != EU_PATENTS
			&& (selecteddbMask & NTIS) != NTIS && (selecteddbMask & EPT) != EPT
			&& (selecteddbMask & CHM) != CHM && (selecteddbMask & CBN) != CBN) {
		searchin[index++] = new Field("PN", "Publisher");
	}

	//ST
	if ((selecteddbMask & US_PATENTS) != US_PATENTS
			&& (selecteddbMask & EU_PATENTS) != EU_PATENTS
			&& (selecteddbMask & NTIS) != NTIS && (selecteddbMask & EPT) != EPT
			&& (selecteddbMask & REFEREX) != REFEREX) {
		searchin[index++] = new Field("ST", "Source title");
	}

	// Inspec unique fields
	//PM
	if ((selecteddbMask & GEO) != GEO && (selecteddbMask & CPX) != CPX
			&& (selecteddbMask & C84) != C84 && (selecteddbMask & CRC) != CRC
			&& (selecteddbMask & CBF) != CBF && (selecteddbMask & CBN) != CBN
			&& (selecteddbMask & ELT) != ELT && (selecteddbMask & CHM) != CHM
			&& (selecteddbMask & PCH) != PCH && (selecteddbMask & NTIS) != NTIS
			&& (selecteddbMask & USPTO) != USPTO
			&& (selecteddbMask & REFEREX) != REFEREX) {
		searchin[index++] = new Field("PM", "Patent number");
	}

	//PA
	if (selecteddbMask == INS || selecteddbMask == (INS + IBF)) {
		searchin[index++] = new Field("PA", "Filing date");
	}

	//PI
	if (selecteddbMask == INS || selecteddbMask == (INS + IBF)) {
		searchin[index++] = new Field("PI", "Patent issue date");
	}

	//PU
	if (selecteddbMask == INS || selecteddbMask == (INS + IBF)) {
		searchin[index++] = new Field("PU", "Country of application");
	}

	//MI
	if (selecteddbMask == INS || selecteddbMask == (INS + IBF)) {
		searchin[index++] = new Field("MI", "Material Identity Number");
	}

	//CV
	if (selecteddbMask == CPX || selecteddbMask == CBF
			|| selecteddbMask == (CPX + C84)) {
		searchin[index++] = new Field("CV", "Ei controlled term");
	} else if (selecteddbMask == INS || selecteddbMask == IBS
			|| selecteddbMask == (INS + IBF)) {
		searchin[index++] = new Field("CV", "Inspec controlled term");
	} else if (selecteddbMask == NTIS) {
		searchin[index++] = new Field("CV", "NTIS controlled term");
	} else if (selecteddbMask == REFEREX) {
		searchin[index++] = new Field("CV", "Subject");
	} else if ((selecteddbMask & US_PATENTS) != US_PATENTS
			&& (selecteddbMask & EU_PATENTS) != EU_PATENTS
			&& (selecteddbMask & REFEREX) != REFEREX
			&& (selecteddbMask & CBN) != CBN && (selecteddbMask & CHM) != CHM) {
		searchin[index++] = new Field("CV", "Controlled term");
	}

	//NTIS unique fields
	//CT
	if (selecteddbMask == NTIS) {
		searchin[index++] = new Field("CT", "Contract number");
	}

	//CO
	if ((selecteddbMask & REFEREX) != REFEREX && (selecteddbMask & CBN) != CBN
			&& (selecteddbMask & ELT) != ELT && (selecteddbMask & EPT) != EPT
			&& (selecteddbMask & PCH) != PCH && (selecteddbMask & CHM) != CHM
			&& (selecteddbMask & IBS) != IBS) {
		searchin[index++] = new Field("CO", "Country of origin");
	}

	//AG
	if (selecteddbMask == NTIS) {
		searchin[index++] = new Field("AG", "Monitoring agency");
	}

	//PD
	if (selecteddbMask == US_PATENTS || selecteddbMask == EU_PATENTS
			|| selecteddbMask == US_EU_PATENTS) {
		searchin[index++] = new Field("PD", "Publication date");
	}
	//else if(selecteddbMask == PCH)
	//{
	//  searchin[index++] = new Field("PD","Patent info");
	//}

	//AN
	if (selecteddbMask == NTIS) {
		searchin[index++] = new Field("AN", "NTIS accession number");
	}

	//PAM
	if (selecteddbMask == US_PATENTS || selecteddbMask == EU_PATENTS
			|| selecteddbMask == US_EU_PATENTS) {
		searchin[index++] = new Field("PAM", "Application number");
	}

	//RN
	if (selecteddbMask == NTIS) {
		searchin[index++] = new Field("RN", "Report number");
	}

	// Patent fields
	//PRN
	if (selecteddbMask == US_PATENTS || selecteddbMask == EU_PATENTS
			|| selecteddbMask == US_EU_PATENTS) {
		searchin[index++] = new Field("PRN", "Priority number");
	}

	//PID
	if (selecteddbMask == US_PATENTS || selecteddbMask == EU_PATENTS
			|| selecteddbMask == US_EU_PATENTS || selecteddbMask == EPT
			|| selecteddbMask == INS || selecteddbMask == EPT + INS
			|| selecteddbMask == INS + US_PATENTS
			|| selecteddbMask == INS + EU_PATENTS
			|| selecteddbMask == INS + US_EU_PATENTS
			|| selecteddbMask == EPT + INS + US_PATENTS
			|| selecteddbMask == EPT + INS + EU_PATENTS
			|| selecteddbMask == EPT + INS + US_EU_PATENTS
			|| selecteddbMask == EPT + US_PATENTS
			|| selecteddbMask == EPT + EU_PATENTS
			|| selecteddbMask == EPT + US_EU_PATENTS) {
		searchin[index++] = new Field("PID", "Int. patent classification");
	}

	//PUC
	if (selecteddbMask == EU_PATENTS) {
		searchin[index++] = new Field("PEC", "ECLA code");
	} else if (selecteddbMask == US_PATENTS) {
		searchin[index++] = new Field("PUC", "US classification");
	}

	//CR
	if (selecteddbMask == ELT || selecteddbMask == EPT
			|| selecteddbMask == EPT + ELT) {
		searchin[index++] = new Field("CR", "CAS registry number");
	}

	//PC
	if (selecteddbMask == EPT) {
		searchin[index++] = new Field("PC", "Patent country");
	}

	return searchin;
}

/***************************************************************
 * Generate the Doctype dropdown
 *
 * @param selecteddbMask
 * @returns {Array}
 */
function generateDoctypes(selecteddbMask) {
	var doctypes = new Array();
	var index = 0;

	// NO-LIMIT
	if ((selecteddbMask & US_PATENTS) != US_PATENTS
			&& (selecteddbMask & REFEREX) != REFEREX
			&& (selecteddbMask & EU_PATENTS) != EU_PATENTS
			&& (selecteddbMask & NTIS) != NTIS && (selecteddbMask & EPT) != EPT
			&& (selecteddbMask & CHM) != CHM) {
		doctypes[index++] = new Field("NO-LIMIT", "All document types");
	} else if (selecteddbMask == US_PATENTS || selecteddbMask == EU_PATENTS
			|| selecteddbMask == US_EU_PATENTS) {
		doctypes[index++] = new Field("NO-LIMIT", "All patents");
	} else {
		doctypes[index++] = new Field("NO-LIMIT", "Document type not available");
	}

	if ((selecteddbMask & US_PATENTS) != US_PATENTS
			&& (selecteddbMask & EU_PATENTS) != EU_PATENTS
			&& (selecteddbMask & NTIS) != NTIS
			&& (selecteddbMask & REFEREX) != REFEREX
			&& (selecteddbMask & EPT) != EPT &&
			//(selecteddbMask & ELT) != ELT &&
			(selecteddbMask & CBN) != CBN && (selecteddbMask & CHM) != CHM) {
		doctypes[index++] = new Field("JA", "Journal article");
	}

	if ((selecteddbMask & US_PATENTS) != US_PATENTS
			&& (selecteddbMask & EU_PATENTS) != EU_PATENTS
			&& (selecteddbMask & NTIS) != NTIS
			&& (selecteddbMask & REFEREX) != REFEREX
			&& (selecteddbMask & EPT) != EPT && (selecteddbMask & ELT) != ELT
			&& (selecteddbMask & CBN) != CBN && (selecteddbMask & CHM) != CHM
			&& (selecteddbMask & PCH) != PCH) {
		doctypes[index++] = new Field("CA", "Conference article");
	} else if (selecteddbMask == ELT) {
		doctypes[index++] = new Field("CA", "Conference");
	}

	if ((selecteddbMask & REFEREX) != REFEREX && (selecteddbMask & GEO) != GEO
			&& (selecteddbMask & US_PATENTS) != US_PATENTS
			&& (selecteddbMask & EU_PATENTS) != EU_PATENTS
			&& (selecteddbMask & NTIS) != NTIS && (selecteddbMask & ELT) != ELT
			&& (selecteddbMask & EPT) != EPT && (selecteddbMask & CBN) != CBN
			&& (selecteddbMask & CHM) != CHM && (selecteddbMask & PCH) != PCH) {
		doctypes[index++] = new Field("CP", "Conference proceeding");
	}

	if ((selecteddbMask & GEO) != GEO
			&& (selecteddbMask & US_PATENTS) != US_PATENTS
			&& (selecteddbMask & EU_PATENTS) != EU_PATENTS
			&& (selecteddbMask & NTIS) != NTIS
			&& (selecteddbMask & REFEREX) != REFEREX
			&& (selecteddbMask & EPT) != EPT &&
			//(selecteddbMask & ELT) != ELT &&
			(selecteddbMask & CBN) != CBN && (selecteddbMask & CHM) != CHM
			&& (selecteddbMask & PCH) != PCH) {
		doctypes[index++] = new Field("MC", "Monograph chapter");
	}
	if ((selecteddbMask & US_PATENTS) != US_PATENTS
			&& (selecteddbMask & EU_PATENTS) != EU_PATENTS
			&& (selecteddbMask & NTIS) != NTIS
			&& (selecteddbMask & REFEREX) != REFEREX
			&& (selecteddbMask & ELT) != ELT && (selecteddbMask & EPT) != EPT
			&& (selecteddbMask & CBN) != CBN && (selecteddbMask & CHM) != CHM
			&& (selecteddbMask & PCH) != PCH) {
		doctypes[index++] = new Field("MR", "Monograph review");
	}

	//RC
	if ((selecteddbMask & GEO) != GEO
			&& (selecteddbMask & US_PATENTS) != US_PATENTS
			&& (selecteddbMask & EU_PATENTS) != EU_PATENTS
			&& (selecteddbMask & NTIS) != NTIS
			&& (selecteddbMask & REFEREX) != REFEREX
			&& (selecteddbMask & EPT) != EPT &&
			//(selecteddbMask & ELT) != ELT &&
			(selecteddbMask & CBN) != CBN && (selecteddbMask & CHM) != CHM
			&& (selecteddbMask & PCH) != PCH) {
		doctypes[index++] = new Field("RC", "Report chapter");
	}

	//RR
	if ((selecteddbMask & GEO) != GEO
			&& (selecteddbMask & US_PATENTS) != US_PATENTS
			&& (selecteddbMask & EU_PATENTS) != EU_PATENTS
			&& (selecteddbMask & NTIS) != NTIS
			&& (selecteddbMask & REFEREX) != REFEREX
			&& (selecteddbMask & EPT) != EPT &&
			//(selecteddbMask & ELT) != ELT &&
			(selecteddbMask & CBN) != CBN && (selecteddbMask & CHM) != CHM
			&& (selecteddbMask & PCH) != PCH) {
		doctypes[index++] = new Field("RR", "Report review");
	}

	//DS
	if ((selecteddbMask & GEO) != GEO
			&& (selecteddbMask & US_PATENTS) != US_PATENTS
			&& (selecteddbMask & EU_PATENTS) != EU_PATENTS
			&& (selecteddbMask & NTIS) != NTIS && (selecteddbMask & CBF) != CBF
			&& (selecteddbMask & REFEREX) != REFEREX
			&& (selecteddbMask & EPT) != EPT && (selecteddbMask & CBN) != CBN
			&& (selecteddbMask & CHM) != CHM && (selecteddbMask & PCH) != PCH) {
		doctypes[index++] = new Field("DS", "Dissertation");
	}

	if (selecteddbMask == INS || selecteddbMask == IBS) {
		doctypes[index++] = new Field("UP", "Unpublished paper");
	}
	if (selecteddbMask == CPX) {
		doctypes[index++] = new Field("PA", "Patents (before 1970)");
	} else if (selecteddbMask == CBF || selecteddbMask == PCH) {
		doctypes[index++] = new Field("PA", "Patents");
	} else if (selecteddbMask == INS || selecteddbMask == IBS) {
		doctypes[index++] = new Field("PA", "Patents (before 1977)");
	}

	if (selecteddbMask == US_PATENTS) {
		doctypes[index++] = new Field("UA", "US Applications");
		doctypes[index++] = new Field("UG", "US Granted");
	} else if (selecteddbMask == EU_PATENTS) {
		doctypes[index++] = new Field("EA", "European Applications");
		doctypes[index++] = new Field("EG", "European Granted");
	} else if ((selecteddbMask & GEO) != GEO && (selecteddbMask & GRF) != GRF
			&& (selecteddbMask & CPX) != CPX && (selecteddbMask & INS) != INS
			&& (selecteddbMask & IBS) != IBS && (selecteddbMask & NTIS) != NTIS
			&& (selecteddbMask & CBF) != CBF
			&& (selecteddbMask & REFEREX) != REFEREX
			&& (selecteddbMask & ELT) != ELT && (selecteddbMask & EPT) != EPT
			&& (selecteddbMask & CBN) != CBN && (selecteddbMask & CHM) != CHM
			&& (selecteddbMask & PCH) != PCH) {
		doctypes[index++] = new Field("UA", "US Applications");
		doctypes[index++] = new Field("UG", "US Granted");
		doctypes[index++] = new Field("EA", "European Applications");
		doctypes[index++] = new Field("EG", "European Granted");
	}

	if (selecteddbMask == PCH) {
		doctypes[index++] = new Field("(CA or CP)", "Conferences");
		doctypes[index++] = new Field("MC or MR or RC or RR or DS or UP",
				"Other documents");
	}

	if (selecteddbMask == CBN) {
		doctypes[index++] = new Field("Journal", "Journal article");
		doctypes[index++] = new Field("Advertizement", "Advertisement");
		doctypes[index++] = new Field("Book", "Book");
		doctypes[index++] = new Field("Directory", "Directory");
		doctypes[index++] = new Field("Company", "Company Report");
		doctypes[index++] = new Field("Stockbroker", "Stockbroker Report");
		doctypes[index++] = new Field("Market", "Market Research Report");
		doctypes[index++] = new Field("Press", "Press Release");
	}

	if (selecteddbMask == ELT) {
		doctypes[index++] = new Field("AB", "Abstract");
		//doctypes[index++] = new Field("({J_AB} or {J_AR} or {J_BZ} or {J_CP} or {J_ED} or {J_ER} or {J_LE} or {J_NO} or {J_RE} or {J_SH} or {D_AR} or {D_BZ} or {D_CP} or {J_BK} or {J_BR} or {J_CH} or {J_CR} or {J_DI} or {J_PA} or {J_PR} or {J_RP} or {J_WP})", "Journal article");
		//doctypes[index++] = new Field("(P or {P_AR} or {P_CP} or {P_AB} or {P_BK} or {P_BR} or {P_BZ} or {P_CH} or {P_CR} or {P_DI} or {P_ED} or {P_ER} or {P_LE} or {P_NO} or {P_PA} or {P_PR} or {P_RE} or {P_SH} or {P_RP} or {P_WP} or {D_CP} or {J_CP})","Conference");
		//doctypes[index++] = new Field("({J_BZ} or {D_BZ} or {D_AR} or {D_CP} or {D_LE} or {D_NO} or {B_BZ} or {K_BZ} or {M_BZ} or {P_BZ} or {R_BZ})","Business article");
		//doctypes[index++] = new Field("(AB or {J_AB} or {R_AB} or {P_AB} or {B_AB} or {D_AB} or {K_AB} or {M_AB})", "Abstract");
		//doctypes[index++] = new Field("Other", "Other");
	}

	// jam - added MAP exclusively for GeoRef
	// tmh - added for Georef In Process
	if (selecteddbMask == GRF) {
		doctypes[index++] = new Field("MP", "Map");
		doctypes[index++] = new Field("GI", "In Process");
	}

	// added for AIP
	if (selecteddbMask == CPX) {
		doctypes[index++] = new Field("IP", "Article in Press");
	}
	// added for AIP
	if(selecteddbMask == GRF)
	{
	doctypes[index++] = new Field("IP", "In Process");
	}


	return doctypes;

}

/***************************************************************
 * Generate the Treatment types dropdown
 *
 * @param selecteddbMask
 * @returns {Array}
 */
function generateTreattypes(selecteddbMask) {
	var treattypes = new Array();
	var index = 0;
	// NO-LIMIT
	if ((selecteddbMask & GEO) != GEO && (selecteddbMask & GRF) != GRF
			&& (selecteddbMask & US_PATENTS) != US_PATENTS
			&& (selecteddbMask & EU_PATENTS) != EU_PATENTS
			&& (selecteddbMask & NTIS) != NTIS && (selecteddbMask & CBF) != CBF
			&& (selecteddbMask & REFEREX) != REFEREX
			&& (selecteddbMask & ELT) != ELT && (selecteddbMask & EPT) != EPT
			&& (selecteddbMask & CBN) != CBN && (selecteddbMask & CHM) != CHM
			&& (selecteddbMask & PCH) != PCH && (selecteddbMask & IBS) != IBS) {
		treattypes[index++] = new Field("NO-LIMIT", "All treatment types");
	} else {
		treattypes[index++] = new Field("NO-LIMIT",
				"Treatment type not available");
	}

	//APP
	if ((selecteddbMask & GEO) != GEO && (selecteddbMask & GRF) != GRF
			&& (selecteddbMask & US_PATENTS) != US_PATENTS
			&& (selecteddbMask & EU_PATENTS) != EU_PATENTS
			&& (selecteddbMask & NTIS) != NTIS && (selecteddbMask & CBF) != CBF
			&& (selecteddbMask & REFEREX) != REFEREX
			&& (selecteddbMask & ELT) != ELT && (selecteddbMask & EPT) != EPT
			&& (selecteddbMask & CBN) != CBN && (selecteddbMask & CHM) != CHM
			&& (selecteddbMask & PCH) != PCH && (selecteddbMask & IBS) != IBS) {
		treattypes[index++] = new Field("APP", "Applications");
	}

	//BIO
	if ((selecteddbMask & GEO) != GEO && (selecteddbMask & GRF) != GRF
			&& (selecteddbMask & US_PATENTS) != US_PATENTS
			&& (selecteddbMask & EU_PATENTS) != EU_PATENTS
			&& (selecteddbMask & NTIS) != NTIS && (selecteddbMask & CBF) != CBF
			&& (selecteddbMask & REFEREX) != REFEREX
			&& (selecteddbMask & ELT) != ELT && (selecteddbMask & EPT) != EPT
			&& (selecteddbMask & CBN) != CBN && (selecteddbMask & CHM) != CHM
			&& (selecteddbMask & PCH) != PCH && (selecteddbMask & INS) != INS
			&& (selecteddbMask & IBS) != IBS) {
		treattypes[index++] = new Field("BIO", "Biographical");
	}

	if (selecteddbMask == INS) {
		treattypes[index++] = new Field("BIB", "Bibliography");
	}

	//ECO
	if ((selecteddbMask & GEO) != GEO && (selecteddbMask & GRF) != GRF
			&& (selecteddbMask & US_PATENTS) != US_PATENTS
			&& (selecteddbMask & EU_PATENTS) != EU_PATENTS
			&& (selecteddbMask & NTIS) != NTIS && (selecteddbMask & CBF) != CBF
			&& (selecteddbMask & REFEREX) != REFEREX
			&& (selecteddbMask & ELT) != ELT && (selecteddbMask & EPT) != EPT
			&& (selecteddbMask & CBN) != CBN && (selecteddbMask & CHM) != CHM
			&& (selecteddbMask & PCH) != PCH && (selecteddbMask & IBS) != IBS) {
		treattypes[index++] = new Field("ECO", "Economic");
	}

	//EXP
	if ((selecteddbMask & GEO) != GEO && (selecteddbMask & GRF) != GRF
			&& (selecteddbMask & US_PATENTS) != US_PATENTS
			&& (selecteddbMask & EU_PATENTS) != EU_PATENTS
			&& (selecteddbMask & NTIS) != NTIS && (selecteddbMask & CBF) != CBF
			&& (selecteddbMask & REFEREX) != REFEREX
			&& (selecteddbMask & ELT) != ELT && (selecteddbMask & EPT) != EPT
			&& (selecteddbMask & CBN) != CBN && (selecteddbMask & CHM) != CHM
			&& (selecteddbMask & PCH) != PCH && (selecteddbMask & IBS) != IBS) {
		treattypes[index++] = new Field("EXP", "Experimental");
	}

	//GEN
	if ((selecteddbMask & GEO) != GEO && (selecteddbMask & GRF) != GRF
			&& (selecteddbMask & US_PATENTS) != US_PATENTS
			&& (selecteddbMask & EU_PATENTS) != EU_PATENTS
			&& (selecteddbMask & NTIS) != NTIS && (selecteddbMask & CBF) != CBF
			&& (selecteddbMask & REFEREX) != REFEREX
			&& (selecteddbMask & ELT) != ELT && (selecteddbMask & EPT) != EPT
			&& (selecteddbMask & CBN) != CBN && (selecteddbMask & CHM) != CHM
			&& (selecteddbMask & PCH) != PCH && (selecteddbMask & IBS) != IBS) {
		treattypes[index++] = new Field("GEN", "General review");
	}

	//Cpx fields
	//HIS
	if (selecteddbMask == CPX) {
		treattypes[index++] = new Field("HIS", "Historical");
	}
	//LIT
	if (selecteddbMask == CPX) {
		treattypes[index++] = new Field("LIT", "Literature review");
	}
	//MAN
	if (selecteddbMask == CPX) {
		treattypes[index++] = new Field("MAN", "Management aspects");
	}
	//NUM
	if (selecteddbMask == CPX) {
		treattypes[index++] = new Field("NUM", "Numerical");
	}
	//Inspec fields
	//NEW
	if (selecteddbMask == INS) {
		treattypes[index++] = new Field("NEW", "New development");
	}
	if (selecteddbMask == INS) {
		treattypes[index++] = new Field("PRA", "Practical");
	}
	if (selecteddbMask == INS) {
		treattypes[index++] = new Field("PRO", "Product review");
	}

	//THR
	if ((selecteddbMask & GEO) != GEO && (selecteddbMask & GRF) != GRF
			&& (selecteddbMask & US_PATENTS) != US_PATENTS
			&& (selecteddbMask & EU_PATENTS) != EU_PATENTS
			&& (selecteddbMask & NTIS) != NTIS && (selecteddbMask & CBF) != CBF
			&& (selecteddbMask & REFEREX) != REFEREX
			&& (selecteddbMask & ELT) != ELT && (selecteddbMask & EPT) != EPT
			&& (selecteddbMask & CBN) != CBN && (selecteddbMask & CHM) != CHM
			&& (selecteddbMask & PCH) != PCH && (selecteddbMask & IBS) != IBS) {
		treattypes[index++] = new Field("THR", "Theoretical");
	}

	return treattypes;
}

/***************************************************************
 * Generate the Languages dropdown
 *
 * @param selecteddbMask
 * @returns {Array}
 */
function generateLanguages(selecteddbMask) {
	var languages = new Array();
	var index = 0;

	// if only REFEREX, then leave all blanked out
	if ((selecteddbMask & REFEREX) == REFEREX) {
		languages[index++] = new Field("NO-LIMIT", "Language Not Available");
	} else {
		languages[index++] = new Field("NO-LIMIT", "All languages");
		languages[index++] = new Field("English", "English");
		languages[index++] = new Field("Chinese", "Chinese");
		languages[index++] = new Field("French", "French");
		languages[index++] = new Field("German", "German");
		if ((selecteddbMask & ELT) != ELT && (selecteddbMask & EPT) != EPT
				&& (selecteddbMask & PCH) != PCH) {
			languages[index++] = new Field("Italian", "Italian");
		}
		languages[index++] = new Field("Japanese", "Japanese");
		languages[index++] = new Field("Russian", "Russian");
		languages[index++] = new Field("Spanish", "Spanish");
	}

	return languages;
}

/***************************************************************
 * Generate the Discipline dropdown
 *
 * @param selecteddbMask
 * @returns {Array}
 */
function generateDisciplines(selecteddbMask) {
	var disciplines = new Array();
	var index = 0;
	var isDisciplines = 0;
	// NOT 2 is INS
	if (selecteddbMask == INS || selecteddbMask == IBS) {
		isDisciplines = 1;
		disciplines[index++] = new Field("NO-LIMIT", "All disciplines");
	} else {
		disciplines[index++] = new Field("NO-LIMIT",
				"Discipline type not available");
	}
	// A for INS
	if (isDisciplines == 1) {
		disciplines[index++] = new Field("A", "Physics");
		disciplines[index++] = new Field("B",
				"Electrical/Electronic engineering");
		disciplines[index++] = new Field("C", "Computers/Control engineering");
		disciplines[index++] = new Field("D", "Information technology");
		disciplines[index++] = new Field("E",
				"Manufacturing and production engineering");

	}
	return disciplines;
}

/***************************************************************
 * Calculate the end year based on db mask
 *
 * @param selectedDbMask
 * @returns {Number}
 */
function calEndYear(selectedDbMask) {
	if (selectedDbMask == IBS) {
		return 1969;
	} else if (selectedDbMask == CBF) {
		return 1969;
	} else {
		//return (new Date()).getFullYear();
		return 2015;
	}
}

/**************************************************************
 * Generate the Year dropdowns
 *
 * @param selectedDbMask
 * @param sYear
 * @param strYear
 * @param eYear
 * @param searchform
 */
function generateYear(selectedDbMask, sYear, strYear, eYear, searchform) {
	var sy = calStartYear(selectedDbMask, strYear);
	var dy = calDisplayYear(selectedDbMask, strYear);
	var ey = calEndYear(selectedDbMask);

	var quicksearchform = $("form[name='quicksearch']");
	var formstartyear = quicksearchform.find("select[name='startYear']");
	var formendyear = quicksearchform.find("select[name='endYear']");

	if (formstartyear.length == 0 || formendyear.length == 0)
		return;

	formstartyear.empty();
	formendyear.empty();

	for (var j = sy; j <= ey; j++) {
		formstartyear.append($('<option value="' + j
				+ (j == dy ? '" selected="selected"' : '') + '">' + j
				+ '</option>'));
		formendyear.append($('<option value="' + j
				+ (j == eYear ? '" selected="selected"' : '') + '">' + j
				+ '</option>'));
	}

	if (typeof (eYear) == 'undefined') {
		formendyear.find("option:last-child").attr("selected", "selected");
	}

}

// default start year
function calStartYear(selectedDbMask, sYear) {

	// 1969 is arbitrary, but in case all else fails?
	// but we will never be able to get above this default value
	// since we will only overwrite this value if we find one less than
	var dYear = 1973;

	// jam 12/20/1004 - This is not an else if!
	// compare the start year for each db
	// pick up the earliest start year possible
	if ((selectedDbMask != 0) && ((selectedDbMask & US_PATENTS) == US_PATENTS)) {
		var upaStartYear = sYear.substr(sYear.indexOf("UST") + 3, 4);
		dYear = (dYear > upaStartYear) ? upaStartYear : dYear;
	}

	if ((selectedDbMask != 0) && ((selectedDbMask & EU_PATENTS) == EU_PATENTS)) {
		var eupStartYear = sYear.substr(sYear.indexOf("EST") + 3, 4);
		dYear = (dYear > eupStartYear) ? eupStartYear : dYear;
	}

	if ((selectedDbMask != 0) && (selectedDbMask == US_EU_PATENTS)) {
		var useupStartYear = sYear.substr(sYear.indexOf("UST") + 3, 4);
		dYear = (dYear > useupStartYear) ? useupStartYear : dYear;
	}

	if ((selectedDbMask != 0) && ((selectedDbMask & CBF) == CBF)) {
		var cbfStartYear = sYear.substr(sYear.indexOf("YST") + 3, 4);
		dYear = (dYear > cbfStartYear) ? cbfStartYear : dYear;
	}
	if ((selectedDbMask != 0) && ((selectedDbMask & CPX) == CPX)) {
		var cpxStartYear = sYear.substr(sYear.indexOf("CST") + 3, 4);
		dYear = (dYear > cpxStartYear) ? cpxStartYear : dYear;
	}
	if ((selectedDbMask != 0) && ((selectedDbMask & INS) == INS)) {
		var insStartYear = sYear.substr(sYear.indexOf("IST") + 3, 4);
		dYear = (dYear > insStartYear) ? insStartYear : dYear;
	}

	if ((selectedDbMask != 0) && ((selectedDbMask & IBS) == IBS)) {
		var ibsStartYear = sYear.substr(sYear.indexOf("FST") + 3, 4);
		dYear = (dYear > ibsStartYear) ? ibsStartYear : dYear;
	}

	if ((selectedDbMask != 0) && ((selectedDbMask & NTIS) == NTIS)) {
		var ntiStartYear = sYear.substr(sYear.indexOf("NST") + 3, 4);
		dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
	}

	if ((selectedDbMask != 0) && ((selectedDbMask & GEO) == GEO)) {
		var geoStartYear = sYear.substr(sYear.indexOf("GST") + 3, 4);
		dYear = (dYear > geoStartYear) ? geoStartYear : dYear;
	}

	if ((selectedDbMask != 0) && ((selectedDbMask & REFEREX) == REFEREX)) {
		var pagStartYear = sYear.substr(sYear.indexOf("PST") + 3, 4);
		dYear = (dYear > pagStartYear) ? pagStartYear : dYear;
	}

	if ((selectedDbMask != 0) && ((selectedDbMask & PCH) == PCH)) {
		var pchStartYear = sYear.substr(sYear.indexOf("AST") + 3, 4);
		dYear = (dYear > pchStartYear) ? pchStartYear : dYear;
	}

	if (selectedDbMask != 0 && ((selectedDbMask & CHM) == CHM)) {
		var chmStartYear = sYear.substr(sYear.indexOf("HST") + 3, 4);
		dYear = (dYear > chmStartYear) ? chmStartYear : dYear;
	}

	if (selectedDbMask != 0 && ((selectedDbMask & CBN) == CBN)) {
		var cbnStartYear = sYear.substr(sYear.indexOf("BST") + 3, 4);
		dYear = (dYear > cbnStartYear) ? cbnStartYear : dYear;
	}

	if (selectedDbMask != 0 && ((selectedDbMask & ELT) == ELT)) {
		var eltStartYear = sYear.substr(sYear.indexOf("LST") + 3, 4);
		dYear = (dYear > eltStartYear) ? eltStartYear : dYear;

	}

	if (selectedDbMask != 0 && ((selectedDbMask & EPT) == EPT)) {
		var eptStartYear = sYear.substr(sYear.indexOf("MST") + 3, 4);
		dYear = (dYear > eptStartYear) ? eptStartYear : dYear;

	}

	if ((selectedDbMask != 0) && ((selectedDbMask & GRF) == GRF)) {
		var geoStartYear = sYear.substr(sYear.indexOf("XST") + 3, 4);
		dYear = (dYear > geoStartYear) ? geoStartYear : dYear;
	}
	return dYear;
}

// customized selected start year
function calDisplayYear(selectedDbMask, sYear) {

	// 2006 since displayed start year could be a very recent value
	// (i.e. An account could have 2000-2006 as their default range)
	// We set this as high as possible and then compare to
	// all possible values and take minimum
	var dYear = calEndYear(selectedDbMask);

	// same as above - not an else if
	// choose theleast of the three when picking selected start year
	if (sYear.length > 4) {
		if ((selectedDbMask != 0) && ((selectedDbMask & CPX) == CPX)) {
			var cpxStartYear = sYear.substr(sYear.indexOf("CSY") + 3, 4);
			dYear = (dYear > cpxStartYear) ? cpxStartYear : dYear;
		}

		if ((selectedDbMask != 0) && ((selectedDbMask & INS) == INS)) {
			var insStartYear = sYear.substr(sYear.indexOf("ISY") + 3, 4);
			dYear = (dYear > insStartYear) ? insStartYear : dYear;
		}
		if ((selectedDbMask != 0) && ((selectedDbMask & IBS) == IBS)) {
			var ibsStartYear = sYear.substr(sYear.indexOf("FSY") + 3, 4);
			dYear = (dYear > ibsStartYear) ? ibsStartYear : dYear;
		}

		if ((selectedDbMask != 0) && ((selectedDbMask & CBF) == CBF)) {
			var cbfStartYear = sYear.substr(sYear.indexOf("YSY") + 3, 4);
			dYear = (dYear > cbfStartYear) ? cbfStartYear : dYear;
		}

		if ((selectedDbMask != 0) && ((selectedDbMask & NTIS) == NTIS)) {
			var ntiStartYear = sYear.substr(sYear.indexOf("NSY") + 3, 4);
			dYear = (dYear > ntiStartYear) ? ntiStartYear : dYear;
		}

		if ((selectedDbMask != 0) && ((selectedDbMask & GEO) == GEO)) {
			var geoStartYear = sYear.substr(sYear.indexOf("GSY") + 3, 4);
			dYear = (dYear > geoStartYear) ? geoStartYear : dYear;
		}

		if ((selectedDbMask != 0) && ((selectedDbMask & PCH) == PCH)) {
			var pchStartYear = sYear.substr(sYear.indexOf("ASY") + 3, 4);
			dYear = (dYear > pchStartYear) ? pchStartYear : dYear;
		}

		if ((selectedDbMask != 0)
				&& ((selectedDbMask & EU_PATENTS) == EU_PATENTS)) {
			var eupStartYear = sYear.substr(sYear.indexOf("ESY") + 3, 4);
			dYear = (dYear > eupStartYear) ? eupStartYear : dYear;
		}

		if ((selectedDbMask != 0)
				&& ((selectedDbMask & US_PATENTS) == US_PATENTS)) {
			var uspStartYear = sYear.substr(sYear.indexOf("USY") + 3, 4);
			dYear = (dYear > uspStartYear) ? uspStartYear : dYear;
		}

		if ((selectedDbMask != 0) && ((selectedDbMask & REFEREX) == REFEREX)) {
			var uspStartYear = sYear.substr(sYear.indexOf("PSY") + 3, 4);
			dYear = (dYear > uspStartYear) ? uspStartYear : dYear;
		}

		if (selectedDbMask != 0 && ((selectedDbMask & CHM) == CHM)) {
			var chmStartYear = sYear.substr(sYear.indexOf("HSY") + 3, 4);
			dYear = (dYear > chmStartYear) ? chmStartYear : dYear;
		}
		if (selectedDbMask != 0 && ((selectedDbMask & CBN) == CBN)) {
			var cbnStartYear = sYear.substr(sYear.indexOf("BSY") + 3, 4);
			dYear = (dYear > cbnStartYear) ? cbnStartYear : dYear;
		}
		if (selectedDbMask != 0 && ((selectedDbMask & ELT) == ELT)) {
			var eltStartYear = sYear.substr(sYear.indexOf("LSY") + 3, 4);
			dYear = (dYear > eltStartYear) ? eltStartYear : dYear;
		}
		if (selectedDbMask != 0 && ((selectedDbMask & EPT) == EPT)) {
			var eptStartYear = sYear.substr(sYear.indexOf("MSY") + 3, 4);
			dYear = (dYear > eptStartYear) ? eptStartYear : dYear;
		}
		if ((selectedDbMask != 0) && ((selectedDbMask & GRF) == GRF)) {
			var geoStartYear = sYear.substr(sYear.indexOf("XSY") + 3, 4);
			dYear = (dYear > geoStartYear) ? geoStartYear : dYear;
		}
	} else {
		dYear = sYear;
	}
	return dYear;
}

function switchlist(selectedDbMask, control, newvalues, selectedValue) {
	// NULL OUT PREVIOUS LIST VALUES
	var i = 0;
	for (i = control.length - 1; i >= 0; i--) {
		control.options[i] = null;
	}

	// FILL LIST WITH VALUES MATCHING SELECTED
	var tot = newvalues.length;
	var matches = 0;
	var selectedIndex = 0;
	for (i = 0, matches = 0; i < tot; i++) {
		control.options[i] = new Option(newvalues[i].label, newvalues[i].value);
		/*
		if(!(selectedValue == null) && (newvalues[i].value == selectedValue))
		{
		control.options[matches].selected = true;
		selectedIndex = matches;
		}
		 */
		matches++;
	}
	control.selectedIndex = selectedIndex;

	return true;
}

function clearDropdown(selectedDbMask, searchform) {

	if ((selectedDbMask != 0) && ((selectedDbMask & NTIS) == NTIS)) {
		if (searchform.doctype) {
			searchform.doctype.options[0] = null;
			searchform.doctype.options[0] = new Option(
					"Document type not available", "NO-LIMIT");
			searchform.doctype.options[0].selected = true;
			searchform.doctype.selectedIndex = 0;
		}
		if (searchform.treatmentType) {
			searchform.treatmentType.options[0] = null;
			searchform.treatmentType.options[0] = new Option(
					"Treatment type not available", "NO-LIMIT");
			searchform.treatmentType.options[0].selected = true;
			searchform.treatmentType.selectedIndex = 0;
		}
		if (searchform.disciplinetype) {
			searchform.disciplinetype.options[0] = null;
			searchform.disciplinetype.options[0] = new Option(
					"Discipline type not available", "NO-LIMIT");
			searchform.disciplinetype.options[0].selected = true;
			searchform.disciplinetype.selectedIndex = 0;
		}
	}

	if ((selectedDbMask != 0) && ((selectedDbMask & REFEREX) == REFEREX)) {
		if (searchform.doctype) {
			searchform.doctype.options[0] = null;
			searchform.doctype.options[0] = new Option(
					"Document type not available", "NO-LIMIT");
			searchform.doctype.options[0].selected = true;
			searchform.doctype.selectedIndex = 0;
		}
		if (searchform.treatmentType) {
			searchform.treatmentType.options[0] = null;
			searchform.treatmentType.options[0] = new Option(
					"Treatment type not available", "NO-LIMIT");
			searchform.treatmentType.options[0].selected = true;
			searchform.treatmentType.selectedIndex = 0;
		}
		if (searchform.disciplinetype) {
			searchform.disciplinetype.options[0] = null;
			searchform.disciplinetype.options[0] = new Option(
					"Discipline type not available", "NO-LIMIT");
			searchform.disciplinetype.options[0].selected = true;
			searchform.disciplinetype.selectedIndex = 0;
		}
		if (searchform.language) {
			searchform.language.options[0] = null;
			searchform.language.options[0] = new Option(
					"Language not available", "NO-LIMIT");
			searchform.language.options[0].selected = true;
			searchform.language.selectedIndex = 0;
		}
	}

	if ((selectedDbMask != 0) && ((selectedDbMask & GEO) == GEO)) {
		if (searchform.treatmentType) {
			searchform.treatmentType.options[0] = null;
			searchform.treatmentType.options[0] = new Option(
					"Treatment type not available", "NO-LIMIT");
			searchform.treatmentType.options[0].selected = true;
			searchform.treatmentType.selectedIndex = 0;
		}
		if (searchform.disciplinetype) {
			searchform.disciplinetype.options[0] = null;
			searchform.disciplinetype.options[0] = new Option(
					"Discipline type not available", "NO-LIMIT");
			searchform.disciplinetype.options[0].selected = true;
			searchform.disciplinetype.selectedIndex = 0;
		}
	} else if ((selectedDbMask != 0)
			&& ((selectedDbMask & EU_PATENTS) == EU_PATENTS)) {

		if (searchform.doctype) {
			if (selectedDbMask != EU_PATENTS && selectedDbMask != US_EU_PATENTS) {
				searchform.doctype.options[0] = null;
				searchform.doctype.options[0] = new Option(
						"Document type not available", "NO-LIMIT");
				searchform.doctype.options[0].selected = true;
				searchform.doctype.selectedIndex = 0;
			}
		}

		if (searchform.treatmentType) {
			searchform.treatmentType.options[0] = null;
			searchform.treatmentType.options[0] = new Option(
					"Treatment type not available", "NO-LIMIT");
			searchform.treatmentType.options[0].selected = true;
			searchform.treatmentType.selectedIndex = 0;
		}
		if (searchform.disciplinetype) {
			searchform.disciplinetype.options[0] = null;
			searchform.disciplinetype.options[0] = new Option(
					"Discipline type not available", "NO-LIMIT");
			searchform.disciplinetype.options[0].selected = true;
			searchform.disciplinetype.selectedIndex = 0;
		}
	} else if ((selectedDbMask != 0)
			&& ((selectedDbMask & US_PATENTS) == US_PATENTS)) {

		if (searchform.doctype) {
			if (selectedDbMask != US_PATENTS && selectedDbMask != US_EU_PATENTS) {
				searchform.doctype.options[0] = null;
				searchform.doctype.options[0] = new Option(
						"Document type not available", "NO-LIMIT");
				searchform.doctype.options[0].selected = true;
				searchform.doctype.selectedIndex = 0;
			}
		}
		if (searchform.treatmentType) {
			searchform.treatmentType.options[0] = null;
			searchform.treatmentType.options[0] = new Option(
					"Treatment type not available", "NO-LIMIT");
			searchform.treatmentType.options[0].selected = true;
			searchform.treatmentType.selectedIndex = 0;
		}
		if (searchform.disciplinetype) {
			searchform.disciplinetype.options[0] = null;
			searchform.disciplinetype.options[0] = new Option(
					"Discipline type not available", "NO-LIMIT");
			searchform.disciplinetype.options[0].selected = true;
			searchform.disciplinetype.selectedIndex = 0;
		}
	} else if ((selectedDbMask != 0) && ((selectedDbMask & CPX) == CPX)) {
		if (searchform.disciplinetype) {
			searchform.disciplinetype.options[0] = null;
			searchform.disciplinetype.options[0] = new Option(
					"Discipline type not available", "NO-LIMIT");
			searchform.disciplinetype.options[0].selected = true;
			searchform.disciplinetype.selectedIndex = 0;
		}
	}
}

/*********************************************************
 * Handle changes to the browse index box
 *
 * @param selectedDbMask
 */
function flipImage(selectedDbMask) {
	var bdiv = $("#browseindexes");

	if ((selectedDbMask & REFEREX) == REFEREX) {
		bdiv.hide();
		return;
	}
	if ((selectedDbMask & CBN) == CBN
			&& (selectedDbMask & US_PATENTS) == US_PATENTS) {
		bdiv.hide();
		return;
	}
	if ((selectedDbMask & CBN) == CBN
			&& (selectedDbMask & EU_PATENTS) == EU_PATENTS) {
		bdiv.hide();
		return;
	}

	bdiv.show();
	$(".browseindexli").hide();

	// AU
	if ((selectedDbMask & CBN) != CBN) {
		if ((selectedDbMask & US_PATENTS) != US_PATENTS
				&& (selectedDbMask & EU_PATENTS) != EU_PATENTS
				&& (selectedDbMask & EPT) != EPT) {
			$("#bi_aus").show();
		} else if (selectedDbMask == US_PATENTS || selectedDbMask == EU_PATENTS
				|| selectedDbMask == US_EU_PATENTS || selectedDbMask == EPT) {
			$("#bi_inv").show();
		} else {
			$("#bi_ausinv").show();
		}
	}

	// AF
	if ((selectedDbMask & CBN) != CBN
			&& (selectedDbMask & INSPEC_BACKFILE) != INSPEC_BACKFILE) {
		if ((selectedDbMask & US_PATENTS) != US_PATENTS
				&& (selectedDbMask & EU_PATENTS) != EU_PATENTS
				&& (selectedDbMask & EPT) != EPT
				&& (selectedDbMask & INSPEC_BACKFILE) != INSPEC_BACKFILE) {
			$("#bi_af").show();
		} else if (selectedDbMask == US_PATENTS || selectedDbMask == EU_PATENTS
				|| selectedDbMask == EPT || selectedDbMask == US_EU_PATENTS) {
			$("#bi_asg").show();
		} else {
			$("#bi_afasg").show();
		}
	}

	// CT
	if ((selectedDbMask & US_PATENTS) != US_PATENTS
			&& (selectedDbMask & EU_PATENTS) != EU_PATENTS
			&& (selectedDbMask & INSPEC_BACKFILE) != INSPEC_BACKFILE) {
		$("#bi_cvs").show();
	}

	// ST
	if ((selectedDbMask & US_PATENTS) != US_PATENTS
			&& (selectedDbMask & EU_PATENTS) != EU_PATENTS
			&& (selectedDbMask & EPT) != EPT && (selectedDbMask & NTIS) != NTIS) {
		$("#bi_st").show();
	}

	// PN
	if ((selectedDbMask & GEO) != GEO
			&& (selectedDbMask & US_PATENTS) != US_PATENTS
			&& (selectedDbMask & EU_PATENTS) != EU_PATENTS
			&& (selectedDbMask & NTIS) != NTIS && (selectedDbMask & CBN) != CBN
			&& (selectedDbMask & CHM) != CHM && (selectedDbMask & EPT) != EPT) {
		$("#bi_pn").show();
	}

	// PC
	if ((selectedDbMask & INS) != INS && (selectedDbMask & IBS) != IBS
			&& (selectedDbMask & CPX) != CPX && (selectedDbMask & GEO) != GEO
			&& (selectedDbMask & GRF) != GRF && (selectedDbMask & PCH) != PCH
			&& (selectedDbMask & US_PATENTS) != US_PATENTS
			&& (selectedDbMask & EU_PATENTS) != EU_PATENTS
			&& (selectedDbMask & NTIS) != NTIS && (selectedDbMask & CBN) != CBN
			&& (selectedDbMask & ELT) != ELT && (selectedDbMask & CHM) != CHM
			&& (selectedDbMask & INSPEC_BACKFILE) != INSPEC_BACKFILE) {
		$("#bi_pc").show();
	}

	if (selectedDbMask == INS) {
		$("#bi_pid").show();
	}

}

var swapCodes = new Array(8211, 8212, 8216, 8217, 8220, 8221);
var swapStrings = new Array("-", "-", "'", "'", "\"", "\"");

/*********************************************************
 * Validate the search form before submit
 *
 * @returns {Boolean}
 */
function searchValidation() {
	if (typeof (document.quicksearch.alldb) != 'undefined') {
		if (calculateMask(document.quicksearch.database) == 0) {
			window.alert('Please select a database');
			return false;
		}
	}

	var searchword1 = document.quicksearch.searchWord1.value;

	var allEmpty = true;

	if (typeof String.prototype.trim !== 'function') {
		String.prototype.trim = function() {
			return this.replace(/^\s+|\s+$/g, '');
		}
	}

	$("input.searchword").each(
			function(index) {
				if (($(this).val() != null) && ($(this).val() != "")
						&& ($(this).val().trim != "")) {
					if (allEmpty != false) {
						allEmpty = false;
					}
					var textNodeValue = $(this).val();
					for (var j = 0; j < swapCodes.length; j++) {
						var swapper = new RegExp("\\u"
								+ swapCodes[j].toString(16), "g");
						textNodeValue = textNodeValue.replace(swapper,
								swapStrings[j]);
					}

					$(this).val(textNodeValue);

				}
			});

	if (allEmpty == true) {
		window.alert("Enter at least one term to search in the database.");
		return false;
	}

	if ((searchword1 == "") || (searchword1 == null)) {
		window.alert("Please enter search term in first text box.");
		return false;
	}

	if (!(searchword1 == "")) {
		/* jam 11/10/2004 - now we can start with a *
		if(searchword1.substring(0,1) == '*') {
		    window.alert("Search word cannot begin with * character.");
		    return (false);
		}
		 */
		var searchLength = searchword1.length;
		var tempword = searchword1;
		var tempLength = 0;

		while (tempword.substring(0, 1) == ' ') {
			tempword = tempword.substring(1);
			tempLength = tempLength + 1;
		}

		if (searchLength == tempLength) {
			window.alert("Enter at least one term to search in the database.");
			return false;
		}

	}

	// 2/8/2005 check to see if we are searching yearselect here
	// so we don't extract year values if not necessary
	if (document.quicksearch.yearselect
			&& (document.quicksearch.yearselect[0].checked)) {
		var startYear = "0";
		var endYear = "0";
		// no  longer using loops to find selected years! This is less time consuming
		if (document.quicksearch.startYear) {
			startYear = document.quicksearch.startYear[document.quicksearch.startYear.selectedIndex].value;
		}
		if (document.quicksearch.endYear) {
			endYear = document.quicksearch.endYear[document.quicksearch.endYear.selectedIndex].value;
		}

		if (parseInt(startYear) > parseInt(endYear)) {
			window.alert("Start year should be less than or equal to End year");
			return false;
		}
	}

	return true;
}

function updateWinds() {
	if (window.lookupWind) {
		if (!window.lookupWind.closed) {
			window.lookupWind.updatechecks();
		}
	}
}

function closeWinds() {
	if (lookupWind) {
		window.lookupWind.close();
	}
}

function checkPatent(quicksearch) {
	if (selectedDbMask == INS) {
		// select year range <start>-1976 for document type is Patent.
		if (quicksearch.doctype.options[quicksearch.doctype.selectedIndex].value == 'PA') {
			quicksearch.startYear.selectedIndex = 0;
			quicksearch.endYear.selectedIndex = quicksearch.endYear.length
					- ((quicksearch.endYear[quicksearch.endYear.length - 1].value - 1976) + 1);

			quicksearch.yearselect[0].checked = true;
		} else // reset
		{
			// 'climb up' from begin dropdown value to startYear
			quicksearch.startYear.selectedIndex = calDisplayYear(INS,
					stringYear)
					- quicksearch.startYear[0].value;
			quicksearch.endYear.selectedIndex = quicksearch.endYear.length - 1;
			quicksearch.yearselect[0].checked = true;
		}
	}
	if (selectedDbMask == CPX) {
		// select year range <start>-1969 when document type is Patent.
		if (quicksearch.doctype.options[quicksearch.doctype.selectedIndex].value == 'PA') {
			quicksearch.startYear.selectedIndex = 0;

			quicksearch.endYear.selectedIndex = quicksearch.endYear.length
					- ((quicksearch.endYear[quicksearch.endYear.length - 1].value - 1969) + 1);

			quicksearch.yearselect[0].checked = true;
		} else // reset
		{
			// 'climb down' from last dropdown value to startYear
			quicksearch.startYear.selectedIndex = calDisplayYear(CPX,
					stringYear)
					- quicksearch.startYear[0].value;
			quicksearch.endYear.selectedIndex = quicksearch.endYear.length - 1;
			quicksearch.yearselect[0].checked = true
		}
	}

	if (selectedDbMask == CBF) {
		// select year range <start>-1969 when document type is Patent.
		if (quicksearch.doctype.options[quicksearch.doctype.selectedIndex].value == 'PA') {
			quicksearch.startYear.selectedIndex = 0;

			quicksearch.endYear.selectedIndex = quicksearch.endYear.length
					- ((quicksearch.endYear[quicksearch.endYear.length - 1].value - 1969) + 1);

			quicksearch.yearselect[0].checked = true;
		} else // reset
		{
			// 'climb down' from last dropdown value to startYear
			quicksearch.startYear.selectedIndex = calDisplayYear(CBF,
					stringYear)
					- quicksearch.startYear[0].value;
			quicksearch.endYear.selectedIndex = quicksearch.endYear.length - 1;
			quicksearch.yearselect[0].checked = true
		}
	}

	return false;
}

function selectYearRange(radioidx) {
	if (typeof (document.quicksearch.yearselect[radioidx]) != 'undefined') {
		if (document.quicksearch.yearselect[radioidx].checked == false) {
			document.quicksearch.yearselect[radioidx].checked = true
		}
	}
}
function checkLastUpdates() {
	var result = true;
	var seldbmask = calculateMask(document.quicksearch.database);
	var alertmsg = "";

	if (document.quicksearch.yearselect[1].checked == true) {
		if (seldbmask == REFEREX) {
			alertmsg = "Last updates selection does not apply to Referex collections.";
			result = false;
		} else if (seldbmask == CBF) {
			alertmsg = "Last updates selection does not apply to Ei Backfile.";
			result = false;
		} else if (seldbmask == IBS) {
			alertmsg = "Last updates selection does not apply to Inspec Archive.";
			result = false;
		} else if (seldbmask == (IBS + CBF)) {
			alertmsg = "Last updates selection does not apply to Ei Backfile and Inspec Archive.";
			result = false;
		} else if (seldbmask == (IBS + REFEREX)) {
			alertmsg = "Last updates selection does not apply to Inspec Archive and Referex collections.";
			result = false;
		} else if (seldbmask == (CBF + REFEREX)) {
			alertmsg = "Last updates selection does not apply to Ei Backfile and Referex collections.";
			result = false;
		} else if (seldbmask == (IBS + CBF + REFEREX)) {
			alertmsg = "Last updates selection does not apply to Ei Backfile, Inspec Archive and Referex collections.";
			result = false;
		}
	}
	if (!result) {
		alert(alertmsg);
		document.quicksearch.yearselect[0].checked = true;
		document.quicksearch.yearselect[0].focus();
	}
	return result;
}
