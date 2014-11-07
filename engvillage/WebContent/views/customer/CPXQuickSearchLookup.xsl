<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:pid="java:org.ei.util.GetPIDDescription" 
  exclude-result-prefixes="xsl">

  <xsl:output method="html" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>
  <xsl:strip-space elements="html:* xsl:*" />

	<xsl:template match="PAGE">
	<!-- This file displays the lookup index window and transfer selected data into search form fields  -->

	<xsl:variable name="SELECTEDLOOKUP">
		<xsl:value-of select="SELECTED-LOOKUP"/>
	</xsl:variable>
	<xsl:variable name="SEARCHWORD">
		<xsl:value-of select="SEARCHWORD"/>
	</xsl:variable>
	<xsl:variable name="SESSIONID">
		<xsl:value-of select="SESSION-ID"/>
	</xsl:variable>
	<xsl:variable name="DATABASE">
		<xsl:value-of select="DATABASE"/>
	</xsl:variable>
	<xsl:variable name="CURRENTPAGECOUNT">
		<xsl:value-of select="CURR-PAGE-COUNT"/>
	</xsl:variable>
	<xsl:variable name="LOOKUP-SEARCHID">
		<xsl:value-of select="LOOKUP-SEARCHID"/>
	</xsl:variable>
	<xsl:variable name="NEXTPAGECOUNT">
		<xsl:value-of select="NEXT-PAGE-COUNT"/>
	</xsl:variable>
	<xsl:variable name="PREVPAGECOUNT">
		<xsl:value-of select="PREV-PAGE-COUNT"/>
	</xsl:variable>
	<xsl:variable name="SEARCHTYPE">
		<xsl:value-of select="SEARCH-TYPE"/>
	</xsl:variable>
	<xsl:variable name="LOOKUP">/engvillage/models/customer/LookUpParameters.jsp?sessionId=<xsl:value-of select="$SESSIONID"/>&amp;database=<xsl:value-of select="$DATABASE"/>&amp;searchtype=<xsl:value-of select="$SEARCHTYPE"/></xsl:variable>

	<html>
		<head>
		<style type="text/css">
		table.contents td {padding-left: 5px;}
		a.SubNav {font-weight: bold}
		a.PageNav {text-decoration: none; font-weight: normal}
		</style>
  		<SCRIPT TYPE="text/javascript" LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
 	    <script type="text/javascript" src="/static/js/jquery/jquery-1.7.2.min.js"></script>
      <script language="javascript">
		    <xsl:comment>
			    <xsl:text disable-output-escaping="yes">
				    <![CDATA[
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
						if(searchtype.val() == "Quick")
						{
							updatechecks();
							//doboolean(searchtype);
						}
						if(searchtype.val() == "Expert")
						{
							expertchecks()
						}

						lookup.find("option").each(function(idx) {
							if ($(this).val() == curIndex.name) $(this).attr('selected','true');
						});
						
						//
						// Handle checkbox select (paste/unpaste)
						//
						$("input[name='selectedchar']").click(function(event) {
							var cbox = $(this);
							if (cbox.is(":checked") && cbox.value != "") {
								// PASTE!
								if(searchtype.val() == "Quick") {
									paste(cbox.val())
								} else if(searchtype.val() == "Expert") {
									expertpaste(cbox.val())
								}
							} else {
								// UNPASTE!
								if(searchtype.val() == "Quick") {
									quickunpaste(cbox.val())
								} else if (searchtype.val() == "Expert") {
									expertunpaste(cbox.val())
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
					function doboolean(searchtype)
				        {
						var curOp;
						var curOp1;
						if(searchtype=="Quick")
						{
							for (var j = 0; j < document.lookup_box.lookup.length; j++ )
							{
								if ( document.lookup_box.lookup[j].checked == true )
								{
									curOp = document.lookup_box.lookup[j].value;
								}
							}
							if ( self.opener.document.quicksearch )
							{
								if ((self.opener.document.quicksearch.searchWord1.value != "") &&
									 (self.opener.document.quicksearch.searchWord2.value != ""))
								{
									 curOp1 = 1;
								}
								else
								{
									if ((self.opener.document.quicksearch.searchWord2.value == "") &&
										 (self.opener.document.quicksearch.searchWord3.value == ""))
									{
										for ( var i = 0; i < self.opener.document.quicksearch.boolean1.length; i++ )
										{
											 if (self.opener.document.quicksearch.boolean1[i].value == curOp )
											 {
												self.opener.document.quicksearch.boolean1[i].selected = true;
											 }
										}
									}
								}
									for ( var i = 0; i < self.opener.document.quicksearch.boolean2.length; i++ )
									{
										  if (self.opener.document.quicksearch.boolean2[i].value == curOp )
										  {
												self.opener.document.quicksearch.boolean2[i].selected = true;
										  }
									 }
							}
						}
					   }


					// The function paste the selected checkbox value in search form field for Quick Search.
					function paste(term)
					{
						if ( self.opener.document.quicksearch )
						{
							var searchword='{'+cleanTerm(term)+'}';
							var currentSection = lookuptosection[lookup.find("option:selected").val()];
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
							if (found) return;
							
							// Nope, find empty one to paste into
							var pasted = false;
							quicksearchwords.each(function(idx) {
								var quicksearchword = $(this);
								if (( /^\s+$/.test(quicksearchword.val()) ) || ( quicksearchword.val() == "" ))
								{
									pasted = true;
									quicksearchword.val(searchword);
									// Change section and boolean (if appropriate)
									window.opener.jQuery(".section").eq(idx).find("option[value='"+currentSection+"']").attr("selected","true");
									if (idx > 0) window.opener.jQuery(".boolean").eq(idx-1).find("option[value='"+currentBooleanValue+"']").attr('selected','true');
									return false;
								}
							});
							
							// Went through all and nowhere to paste - see if we can add a field
							if (!pasted) {
								var added = window.opener.addSearchField(searchword, currentBooleanValue, currentSection);
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
									window.opener.jQuery(".section").eq(11).find("option[value='"+currentSection+"']").attr("selected","true");
									window.opener.jQuery(".boolean").eq(10).find("option[value='"+currentBooleanValue+"']").attr('selected','true');
								}
							}
						}
					}

					// The function delete the unchecked checkbox value from search form field for Quick Search.
					function quickunpaste(term)
					{
						if ( self.opener.document.quicksearch )
						{
							var searchword='{'+cleanTerm(term)+'}';
							var currentSection = lookuptosection[lookup.find("option:selected").val()];
							var currentBooleanValue = booleanlookup.filter(":checked").val();
							var quicksearchwords = window.opener.jQuery(".searchword");
							var quicksections = window.opener.jQuery(".section");
							var quickbooleans = window.opener.jQuery(".boolean");
							
							// Go through search fields on quick form and remove selected
							var unpasted = false;
							quicksearchwords.each(function(idx) {
								if ($(this).val() == searchword) {
									unpasted = true;
									$(this).val('');
									quicksections.eq(idx).find("option:first-child").attr('selected','selected');
									if (idx > 0) {
										quickbooleans.eq(idx-1).find("option:first-child").attr('selected','selected');
									}
								} 
							});
							
							// If we un-pasted something, see if we need to shift entries up!
							if (unpasted) {
								var len = quicksearchwords.length;
								for (var i=0; i<len-1; i++) {
									if (quicksearchwords.eq(i).val() == '') {
										// Look ahead to find non-empty and copy here
										for (var j=i+1; j<len; j++) {
											if (quicksearchwords.eq(j).val() != '') {
												// Copy found entry
												quicksearchwords.eq(i).val(quicksearchwords.eq(j).val());
												quicksections.eq(i).find("option[value='"+quicksections.eq(j).find("option:selected").val()+"']").attr('selected','selected');
												if (i > 0) {
													quickbooleans.eq(i-1).find("option[value='"+quickbooleans.eq(j-1).find("option:selected").val()+"']").attr('selected','selected');
												}
												
												// Clear out old entry
												quicksearchwords.eq(j).val('');
												quicksections.eq(j).find("option:first-child").attr('selected','selected');
												quickbooleans.eq(j-1).find("option:first-child").attr('selected','selected');
												
												break;
											}
										}
									}
								}
							}
						}
						
					}


					// The function paste the selected checkbox value in search form field for Expert Search.
					function expertpaste(term)
					{
						var SelBoolean;
						if(self.opener.document.quicksearch.searchWord1)
						{
							for (var j = 0; j < document.lookup_box.lookup.length; j++ )
							{
					             		if(document.lookup_box.lookup[j].checked == true )
					             		{
					                  		SelBoolean = document.lookup_box.lookup[j].value;
						 		}
					        	}

							var searchword='{'+cleanTerm(term)+'}';
							if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "CVS" )
							{
								curIndex.field = "CV"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "ST" )
							{
								curIndex.field = "ST"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "AUS" )
							{
								curIndex.field = "AU"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "AF" )
							{
								curIndex.field = "AF"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "PN" )
							{
								curIndex.field = "PN"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "AAFF" )
							{
								curIndex.field = "AAFF"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "FJT" )
							{
								curIndex.field = "FJT"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "PUB" )
							{
								curIndex.field = "PUB"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "DT" )
							{
								curIndex.field = "DT"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "TR" )
							{
								curIndex.field = "TR"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "LA" )
							{
								curIndex.field = "LA"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "DI" )
							{
								curIndex.field = "DI"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "RTYPE" )
							{
								curIndex.field = "DT"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "TRMC" )
							{
								curIndex.field = "TR"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "PC" )
							{
								curIndex.field = "PC"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "PID" )
							{
								curIndex.field = "PID"
							}


							term = cleanTerm(term);
							if (( !/^\s+$/.test(self.opener.document.quicksearch.searchWord1.value) )
								&& ( self.opener.document.quicksearch.searchWord1.value != "" ))
							{
								if ( !((self.opener.document.quicksearch.searchWord1.value).indexOf(searchword)>0) )
								{
									self.opener.document.quicksearch.searchWord1.value = self.opener.document.quicksearch.searchWord1.value
										+ " " + SelBoolean + " (({"
										+ term
										+ "}) WN "
										+ curIndex.field
										+ ")"
								}
							}
							else
							{
								self.opener.document.quicksearch.searchWord1.value = "(({"
									+ term
									+ "}) WN "
									+ curIndex.field
									+ ")"
							}

						}
					}



					// The function delete the unchecked checkbox value from search form field for Expert Search.
					function expertunpaste(term)
					{
						var searchword1;
						var newterm;
						var available;
						if ( self.opener.document.quicksearch )
						{
							var source = self.opener.document.quicksearch
							searchword1=self.opener.document.quicksearch.searchWord1.value;

							if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "CVS" )
							{
								curIndex.field = "CV"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "ST" )
							{
								curIndex.field = "ST"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "AUS" )
							{
								curIndex.field = "AU"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "AF" )
							{
								curIndex.field = "AF"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "PN" )
							{
								curIndex.field = "PN"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "AAFF" )
							{
								curIndex.field = "AAFF"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "FJT" )
							{
								curIndex.field = "FJT"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "PUB" )
							{
								curIndex.field = "PUB"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "AFI" )
							{
								curIndex.field = "AF"
							}
							else if ( document.lookupform.lookup[document.lookupform.lookup.selectedIndex].value == "PNI" )
							{
								curIndex.field = "PN"
							}

							term = cleanTerm(term);
							newterm="(({"+term+ "}) WN "+ curIndex.field+ ")";

							available=searchword1.indexOf(newterm);

							if(available>=0){
							  var tempSearchWord;
							  var newSearchWord1;

							  var newtermend=available+newterm.length;
							  var stringBeforeNewterm=searchword1.substring(0,available);
							  var stringAfterNewterm=searchword1.substring(newtermend);

							  if(available>0)
							  {
							     tempSearchWord=removeAtEnd(stringBeforeNewterm);
							     newSearchWord1=tempSearchWord+stringAfterNewterm
							  }
							  else
							  {
							     tempSearchWord=removeAtBegin(stringAfterNewterm);
							     newSearchWord1=stringBeforeNewterm+tempSearchWord
							   }

							  if(tempSearchWord==null)
							  {
							  newSearchWord1=stringBeforeNewterm+stringAfterNewterm;
							  }


							  self.opener.document.quicksearch.searchWord1.value=newSearchWord1;

							}


						 }
					 }


					 function removeAtBegin(replaceString)
					 {
					 	 var afterReplaceBoolean;

						 if(replaceString.indexOf(" AND ")==0)
						 {
						   afterReplaceBoolean=replaceString.substring(5);
						 }
						 if(replaceString.indexOf(" OR ")==0)
						 {
						   afterReplaceBoolean=replaceString.substring(4);
						 }
						 return afterReplaceBoolean;
					 }


					 function removeAtEnd(replaceString)
					 {
					 	 var afterReplaceBoolean;
						 if(replaceString.lastIndexOf(" AND ")>0)
						 {
						   afterReplaceBoolean=replaceString.substring(0,replaceString.lastIndexOf(" AND "));
						 }
						 if(replaceString.lastIndexOf(" OR ")>0)
						 {
						   afterReplaceBoolean=replaceString.substring(0,replaceString.lastIndexOf(" OR "));
						 }
						 return afterReplaceBoolean;
					}


					function updatechecks()
					{
						$("input[name='selectedchar']").removeAttr('checked');
						if ( self.opener.document.quicksearch ) {
							$("input[name='selectedchar']").each(function(idx) {
								var selectedchar = $(this);
								var checkme = "{"+cleanTerm(selectedchar.val())+"}";
								self.opener.jQuery('.searchword').each(function(idy) {
									if ($(this).val() == checkme) {
										selectedchar.attr('checked','checked');
									} 
								});
							});
						}
					}



					function expertchecks()
					{
						if ( self.opener.document.quicksearch.searchWord1 )
						{
							if (document.pastelist.selectedchar != null ) {
								for (var i = 0; i < document.pastelist.selectedchar.length; i++)
								{
									var searchword='({'+cleanTerm(document.pastelist.selectedchar[i].value)+'})';
									var textareavalue=self.opener.document.quicksearch.searchWord1.value;
									if ( textareavalue.indexOf(searchword)>0)
									{
										document.pastelist.selectedchar[i].checked = true
									}
									else
									{

										document.pastelist.selectedchar[i].checked = false
									}
								}
							}
						}
					}


					function validation()
					{
						 var searchword=document.lookupform.searchWord.value;




						 if((searchword=="") || (searchword==null)) {
							window.alert("Enter at least one term to search in the indexes.");
							return false;
						 }

						 if(!(searchword=="")) {



							var searchLength= searchword.length;
							var tempword = searchword;
							var tempLength=0;


							while (tempword.substring(0,1) == ' ') {
							   tempword = tempword.substring(1);
							   tempLength = tempLength + 1;
							}

							if ( searchLength == tempLength) {
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
					   var tmpAfter = new String();
					   var tmpOutput = new String();
					   var intBefore = 0;
					   var intAfter = 0;

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
						tmpOutput = SearchAndReplace(tmpContent, "(", " ") ;
						tmpOutput = SearchAndReplace(tmpOutput, ")", " ") ;
						tmpOutput = SearchAndReplace(tmpOutput, "{", " ") ;
						tmpOutput = SearchAndReplace(tmpOutput, "}", " ") ;
						tmpOutput = SearchAndReplace(tmpOutput, "$", " ") ;
						tmpOutput = SearchAndReplace(tmpOutput, "*", " ") ;
						tmpOutput = SearchAndReplace(tmpOutput, '"', " ") ;
						return tmpOutput;
					}
				  ]]>
			  </xsl:text>
      // </xsl:comment>
		</script>
			<!-- end of java script -->
			<title>Engineering Village - Browse Index - Lookup <xsl:value-of select="$SELECTEDLOOKUP"/></title>
		</head>

	<body bgcolor="#FFFFFF" marginwidth="0" marginheight="0" topmargin="0">

	<!--
	  //this header should be two different formats .they
	     1.doument types ,language ,Treatment types and Discipline types the format should be same.
	     2.Author,Author affiliation,Controlled terms,serial title the format is same.

	-->
<xsl:choose>
	<xsl:when test="(LOOKUP-INDEXES/@TYPE='DYNAMIC')">

		<table border="0" width="100%" height="100" cellspacing="0" cellpadding="0">
		<tr>
			<td valign="top">
				<table border="0" width="100%" cellspacing="3" cellpadding="0" bgcolor="#148C75">
					<FORM name="lookupform" action="/controller/servlet/Controller?CID=lookupIndexes" METHOD="POST" onSubmit="return validation()">
					<input type="hidden" name="database">
					<xsl:attribute name="value">
						<xsl:value-of select="$DATABASE"/>
					</xsl:attribute>
					</input>
					<input type="hidden" name="searchtype">
					<xsl:attribute name="value">
						 <xsl:value-of select="$SEARCHTYPE"/>
					</xsl:attribute>
					</input>
					<tr>
						<td valign="middle">
							<a CLASS="SmWhiteText">&#160; <b>Search for:</b></a>
							<a CLASS="SmBlackText">&#160;
							<INPUT TYPE="text" SIZE="10" NAME="searchWord">
								<xsl:attribute name="value">
									<xsl:value-of select="$SEARCHWORD"/>
								</xsl:attribute>
							</INPUT>
							<INPUT TYPE="submit" VALUE="Find"/></a>
						</td>
						<td valign="top" width="20">
							<img src="/static/images/spacer.gif" border="0" width="20"/>
						</td>
						<td valign="top" height="20" bgcolor="#148C75" align="left">
							<a CLASS="SmWhiteText"><b>Selected index:</b></a>&#160;
							<a CLASS="SmBlackText">
							<select name="lookup">
							<xsl:for-each select="document($LOOKUP)/FIELDS/FIELD">
								<xsl:variable name="VALUE"><xsl:value-of select="@SHORTNAME"/></xsl:variable>
								<xsl:variable name="NAME"><xsl:value-of select="@DISPLAYNAME"/></xsl:variable>
								<xsl:choose>
									<xsl:when test="($VALUE=$SELECTEDLOOKUP)">
										<OPTION selected="selected">
											<xsl:attribute name="href">/controller/servlet/Controller?CID=lookupIndexes&amp;EISESSION=<xsl:value-of select="$SESSIONID"/>&amp;lookup=<xsl:value-of select="$VALUE"/>&amp;database=<xsl:value-of select="$DATABASE"/>&amp;searchtype=<xsl:value-of select="$SEARCHTYPE"/></xsl:attribute>
											<xsl:attribute name="value"><xsl:value-of select="$VALUE"/></xsl:attribute>
											<xsl:value-of select="$NAME"/>
										</OPTION>
									</xsl:when>
									<xsl:otherwise>
										<OPTION>
											<xsl:attribute name="href">/controller/servlet/Controller?CID=lookupIndexes&amp;EISESSION=<xsl:value-of select="$SESSIONID"/>&amp;lookup=<xsl:value-of select="$VALUE"/>&amp;database=<xsl:value-of select="$DATABASE"/>&amp;searchtype=<xsl:value-of select="$SEARCHTYPE"/></xsl:attribute>
											<xsl:attribute name="value"><xsl:value-of select="$VALUE"/></xsl:attribute>
											<xsl:value-of select="$NAME"/>
										</OPTION>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:for-each>
							</select>
							</a>
						</td>
					</tr>
					</FORM>
				</table>
			</td>
		</tr>
		<tr>
			<td valign="top">
				<table border="0" width="100%" cellspacing="0" cellpadding="0" class="contents">
					<FORM  NAME="lookup_box" METHOD="POST">
					<input type="hidden" name="database">
					<xsl:attribute name="value">
						<xsl:value-of select="$DATABASE"/>
					</xsl:attribute>
					</input>
					<input type="hidden" name="searchtype">
					<xsl:attribute name="value">
						 <xsl:value-of select="$SEARCHTYPE"/>
					</xsl:attribute>
					</input>
					<tr>
						<td valign="top">
							<a CLASS="MedBlackText">Click on letter below to browse index:</a>
						</td>
					</tr>
					<tr>
						<td valign="top">
							<xsl:if test="boolean($DATABASE='Compendex' and $SELECTEDLOOKUP='AF')">
								<a CLASS="LgBlueLink" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=0&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">0-9</a><xsl:text> </xsl:text>
							</xsl:if>

							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=A&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">A</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=B&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">B</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=C&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">C</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=D&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">D</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=E&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">E</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=F&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">F</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=G&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">G</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=H&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">H</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=I&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">I</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=J&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">J</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=K&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">K</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=L&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">L</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=M&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">M</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=N&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">N</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=O&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">O</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=P&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">P</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=Q&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">Q</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=R&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">R</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=S&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">S</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=T&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">T</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=U&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">U</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=V&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">V</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=W&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">W</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=X&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">X</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=Y&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">Y</a><xsl:text> </xsl:text>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord=Z&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">Z</a>
						</td>
					</tr>
					<tr>
						<td valign="top">
						<xsl:for-each select="ALPHABET-SEQUENCE/SEQUENCE">
							<xsl:variable name="SEQUENCE">
								<xsl:value-of select="."/>
							</xsl:variable>
							<a CLASS="LgBlueLink SubNav" HREF="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord={$SEQUENCE}&amp;lookup={$SELECTEDLOOKUP}&amp;database={$DATABASE}&amp;searchtype={$SEARCHTYPE}">
								<xsl:value-of select="$SEQUENCE"/>
							</a><xsl:text> </xsl:text>
						</xsl:for-each>
						</td>
					</tr>
					<tr>
						<td valign="top" height="3">
							<img src="/static/images/spacer.gif" border="0"/>
						</td>
					</tr>
					<tr>
						<td valign="top">
							<a CLASS="MedBlackText">Select terms below to add to search</a>
						</td>
					</tr>
					<tr>
						<td valign="top" height="18" bgcolor="C3C8D1" align="left">
						<span style="float:left">
							<a CLASS="MedBlackText"><b> &#160; Connect terms with: </b></a>&#160; &#160;
							<input type="radio" name="lookup" value="AND" onClick="doboolean('{$SEARCHTYPE}')"/>
							<a CLASS="MedBlackText">AND</a> &#160; &#160;
							<input type="radio" name="lookup" value="OR" onClick="doboolean('{$SEARCHTYPE}')" checked="checked"/>
							<a CLASS="MedBlackText">OR</a>&#160;
						</span>
						
						<span style="float:right; margin-right: 5px">
							<xsl:if test="not(boolean($CURRENTPAGECOUNT='1'))">
						
								<a CLASS="LgBlueLink PageNav"
									href="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord={$SEARCHWORD}&amp;COUNT={$PREVPAGECOUNT}&amp;database={$DATABASE}&amp;lookup={$SELECTEDLOOKUP}&amp;searchtype={$SEARCHTYPE}&amp;lookupSearchID={$LOOKUP-SEARCHID}">&lt; Previous page</a>
								&#160; &#160;
							</xsl:if>
							<xsl:variable name="totalcount">
								<xsl:value-of select="count(LOOKUP-INDEXES/LOOKUP-INDEX)" />
							</xsl:variable>
							<!--      <text URL2222="{$totalcount}"/> -->
							<xsl:if
								test="boolean($totalcount &gt; 0) and ((boolean($totalcount=100 )) or (boolean($totalcount=101)))">
								<a CLASS="LgBlueLink PageNav" style="text-decoration: none"
									href="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord={$SEARCHWORD}&amp;COUNT={$NEXTPAGECOUNT}&amp;database={$DATABASE}&amp;lookup={$SELECTEDLOOKUP}&amp;searchtype={$SEARCHTYPE}&amp;lookupSearchID={$LOOKUP-SEARCHID}">Next page &gt;</a>
							</xsl:if>
						</span>
						<span style="clear:both"></span>
						</td>
					</tr>
					</FORM>
				</table>
			</td>
		</tr>
		</table>
	</xsl:when>
	<xsl:otherwise>

		<table border="0" width="100%" height="100" cellspacing="0" cellpadding="0">
		  <tr><td valign="top" height="5"><img src="/static/images/spacer.gif" border="0"/></td></tr>
		<tr><td valign="top">
		<FORM name="lookupform" action="#" METHOD="POST" id="lookupform">
		<input type="hidden" name="database"><xsl:attribute name="value"><xsl:value-of select="$DATABASE"/></xsl:attribute></input>
		<input type="hidden" name="searchtype"><xsl:attribute name="value"><xsl:value-of select="$SEARCHTYPE"/></xsl:attribute></input>
		<table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#148C75">
		<tr>
		<td valign="top" height="20" bgcolor="#148C75" align="right"><a CLASS="LgWhiteText">Selected index:</a>&#160;
		<a CLASS="SmBlackText">
		<select name="lookup">
			<xsl:for-each select="document($LOOKUP)/FIELDS/FIELD">
				<xsl:variable name="VALUE"><xsl:value-of select="@SHORTNAME"/></xsl:variable>
				<xsl:variable name="NAME"><xsl:value-of select="@DISPLAYNAME"/></xsl:variable>
				<xsl:choose>
					<xsl:when test="($VALUE=$SELECTEDLOOKUP)">
						<OPTION selected="selected">
											<xsl:attribute name="href">/controller/servlet/Controller?CID=lookupIndexes&amp;EISESSION=<xsl:value-of select="$SESSIONID"/>&amp;lookup=<xsl:value-of select="$VALUE"/>&amp;database=<xsl:value-of select="$DATABASE"/>&amp;searchtype=<xsl:value-of select="$SEARCHTYPE"/></xsl:attribute>
							<xsl:attribute name="value"><xsl:value-of select="$VALUE"/></xsl:attribute>
							<xsl:value-of select="$NAME"/>
						</OPTION>
					</xsl:when>
					<xsl:otherwise>
						<OPTION>
											<xsl:attribute name="href">/controller/servlet/Controller?CID=lookupIndexes&amp;EISESSION=<xsl:value-of select="$SESSIONID"/>&amp;lookup=<xsl:value-of select="$VALUE"/>&amp;database=<xsl:value-of select="$DATABASE"/>&amp;searchtype=<xsl:value-of select="$SEARCHTYPE"/></xsl:attribute>
							<xsl:attribute name="value"><xsl:value-of select="$VALUE"/></xsl:attribute>
							<xsl:value-of select="$NAME"/>
						</OPTION>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</select>
		</a>&#160;
		</td>
		</tr>
		</table>
		</FORM>
		</td></tr>

		<tr><td valign="top">
		<table border="0" width="100%" cellspacing="0" cellpadding="0" class="contents">
		<FORM  NAME="lookup_box" METHOD="POST">
		<tr><td valign="top"><a CLASS="MedBlackText">Select terms below to add to search</a></td></tr>

		<tr>
		<td valign="top" height="18" bgcolor="#e0e3ea" align="left">
			<a CLASS="MedBlackText"><b> &#160; Connect terms with: </b></a>&#160; &#160;
				<input type="radio" name="lookup" value="AND" onClick="doboolean('{$SEARCHTYPE}')"/>
			<a CLASS="MedBlackText">AND</a> &#160; &#160;
				<input type="radio" name="lookup" value="OR" onClick="doboolean('{$SEARCHTYPE}')" checked="checked"/>
			<a CLASS="MedBlackText">OR</a>&#160;
			
			<span style="float: right; margin-right: 5px">
<!--			<text URL33333="{$CURRENTPAGECOUNT}"/> -->
			<xsl:if test="not(boolean($CURRENTPAGECOUNT='1'))">

				<a CLASS="LgBlueLink PageNav" href="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord={$SEARCHWORD}&amp;COUNT={$PREVPAGECOUNT}&amp;database={$DATABASE}&amp;lookup={$SELECTEDLOOKUP}">&lt; Previous page</a>&#160; &#160;
			</xsl:if>

			<xsl:variable name="totalcount"><xsl:value-of select="count(LOOKUP-INDEXES/LOOKUP-INDEX)"/></xsl:variable>
<!--			<text URL4444="{$totalcount}"/> -->
			<xsl:if test="boolean($totalcount &gt; 0) and ((boolean($totalcount=100 )) or (boolean($totalcount=101)))">

				<a CLASS="LgBlueLink PageNav" href="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord={$SEARCHWORD}&amp;COUNT={$NEXTPAGECOUNT}&amp;database={$DATABASE}&amp;lookup={$SELECTEDLOOKUP}">Next page &gt;</a>
			</xsl:if>
			</span>
		</td>
		</tr>

		</FORM>
		</table>
		</td></tr>
		</table>
	</xsl:otherwise>
</xsl:choose>

<xsl:choose>
	<xsl:when test="(LOOKUP-INDEXES/@TYPE='DYNAMIC')">
		<table border="0" width="100%" cellspacing="0" cellpadding="0" class="contents">
			<FORM NAME="pastelist">
			<xsl:if test="$DATABASE='Combined'">
				<tr>
					<td valign="top" colspan="2"><A CLASS="SmBlackText">(</A><img src="/static/images/bluebox.gif" border="0"/><A CLASS="SmBlackText">) Compendex only (</A><img src="/static/images/pinkbox.gif" border="0"/><A CLASS="SmBlackText">) INSPEC only <br/>(</A><img src="/static/images/blackbox.gif" border="0"/><A CLASS="SmBlackText">) Compendex &amp; INSPEC</A></td>
				</tr>
			</xsl:if>
			<!-- 
			<tr>
				<td valign="top" colspan="2">
					<A CLASS="MedBlackText"><b>	<xsl:value-of select="$SEARCHWORD"/></b></A>
				</td>
			</tr>
			 -->
			<tr>
			<xsl:apply-templates select="LOOKUP-INDEXES/LOOKUP-INDEX">
				<xsl:with-param name="SEARCHWORD"><xsl:value-of select="$SEARCHWORD"/></xsl:with-param>
				<xsl:with-param name="SELECTEDLOOKUP"><xsl:value-of select="$SELECTEDLOOKUP"/></xsl:with-param>
			</xsl:apply-templates>
			</tr>
			</FORM>
			<tr bgcolor="c3c8d1">
				<td valign="top" align="left">
					<A CLASS="LgBlueLink PageNav" HREF="#top">Back to Top</A>&#160; &#160;
					<span style="float:right; margin-right: 5px">
					<xsl:if test="not(boolean($CURRENTPAGECOUNT='1'))">
						<a CLASS="LgBlueLink PageNav" href="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord={$SEARCHWORD}&amp;COUNT={$PREVPAGECOUNT}&amp;database={$DATABASE}&amp;lookup={$SELECTEDLOOKUP}&amp;searchtype={$SEARCHTYPE}&amp;lookupSearchID={$LOOKUP-SEARCHID}">&lt; Previous page</a>&#160; &#160;
					</xsl:if>

					<xsl:variable name="totalcount"><xsl:value-of select="count(LOOKUP-INDEXES/LOOKUP-INDEX)"/></xsl:variable>
						<xsl:if test="boolean($totalcount &gt; 0) and ((boolean($totalcount=100 )) or (boolean($totalcount=101)))">
							<a CLASS="LgBlueLink PageNav" href="/controller/servlet/Controller?CID=lookupIndexes&amp;searchWord={$SEARCHWORD}&amp;COUNT={$NEXTPAGECOUNT}&amp;database={$DATABASE}&amp;lookup={$SELECTEDLOOKUP}&amp;searchtype={$SEARCHTYPE}&amp;lookupSearchID={$LOOKUP-SEARCHID}">Next page &gt;</a>
						</xsl:if>
					</span>
				</td>
			</tr>
			<tr>
				<td valign="top" height="10">
					<img src="/static/images/spacer.gif" border="0"/>
				</td>
			</tr>
		</table>
	</xsl:when>
	<xsl:otherwise>
		<table border="0" width="300" cellspacing="0" cellpadding="0" class="contents">
			<FORM NAME="pastelist">

			<xsl:apply-templates select="LOOKUP-INDEXES/LOOKUP-INDEX"/>
			</FORM>
       		</table>
	</xsl:otherwise>
</xsl:choose>
<script language="JavaScript" type="text/javascript" src="/static/js/wz_tooltip.js"></script>
</body>
</html>

</xsl:template>

<xsl:template match="LOOKUP-INDEXES">
	<xsl:param name="SEARCHWORD"></xsl:param>
	<xsl:param name="SELECTEDLOOKUP"></xsl:param>
	<xsl:apply-templates select="LOOKUP-INDEXES/LOOKUP-INDEX">
		<xsl:with-param
			name="SEARCHWORD"><xsl:value-of select="$SEARCHWORD"/></xsl:with-param>
		<xsl:with-param
			name="SELECTEDLOOKUP"><xsl:value-of select="$SELECTEDLOOKUP"/></xsl:with-param>
	  </xsl:apply-templates>
</xsl:template>

<xsl:template match="LOOKUP-INDEX">

	<xsl:param name="SEARCHWORD"></xsl:param>
	<xsl:param name="SELECTEDLOOKUP"></xsl:param>
	
	<xsl:variable name="DATABASE">
		<xsl:choose>
		<xsl:when test="child::LOOKUP-DBS" >
			<xsl:value-of select="child::LOOKUP-DBS"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="//PAGE/DATABASE"/>
		</xsl:otherwise>
		</xsl:choose>
		
	</xsl:variable>

	<xsl:variable name="AFTERUPPER-CASE">
		<xsl:value-of select = "translate($SEARCHWORD,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')" />
	 </xsl:variable>
	 
	 <xsl:variable name="LOOKUPVALUE">
	 	<xsl:choose>
	 		<xsl:when test="$DATABASE='Inspec' or $DATABASE='2'">
	 			<!--<xsl:value-of select="substring-before(child::LOOKUP-VALUE,'[]')" />-->
	 			<xsl:value-of select="child::LOOKUP-VALUE"/>
	 		</xsl:when>
	 		<xsl:otherwise>
	 			<xsl:value-of select="child::LOOKUP-VALUE"/>
	 		</xsl:otherwise>
	 	</xsl:choose>
	 </xsl:variable>
	 
	<xsl:variable name="LOOKUPDESCRIPTION">
		<xsl:choose>
	 		<xsl:when test="$DATABASE='Inspec' or $DATABASE='2' and $SELECTEDLOOKUP='PID'">						   
				
				<xsl:value-of select="pid:getDescription(child::LOOKUP-NAME)"/>
				
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="child::LOOKUP-NAME"/>	
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>


	<tr>
		<td valign="top">
		
			<INPUT TYPE="CHECKBOX"  NAME="selectedchar">			        
				<xsl:attribute name="value"><xsl:value-of select="$LOOKUPVALUE"/></xsl:attribute>
				
				<xsl:if test="$DATABASE='Inspec' or $DATABASE='2' and $SELECTEDLOOKUP='PID'">
					<xsl:attribute name="onmouseover">this.T_WIDTH=450;return escape('<xsl:value-of select="$LOOKUPDESCRIPTION"/>')</xsl:attribute>
					<!--<xsl:attribute name="title"><xsl:value-of select="$LOOKUPDESCRIPTION"/></xsl:attribute>-->
				</xsl:if>
			</INPUT>

			<A CLASS="SmBlackText">
				<xsl:if test="$DATABASE='Inspec' or $DATABASE='2' and $SELECTEDLOOKUP='PID'">
				<xsl:attribute name="onmouseover">this.T_WIDTH=450;return escape('<xsl:value-of select="$LOOKUPDESCRIPTION"/>')</xsl:attribute>
					<!--<xsl:attribute name="title"><xsl:value-of select="$LOOKUPDESCRIPTION"/></xsl:attribute>-->
				</xsl:if>				

				<xsl:choose>
				<xsl:when test="(starts-with($LOOKUPVALUE,$AFTERUPPER-CASE))">
					<FONT color="#148c75"><b><xsl:value-of select="$AFTERUPPER-CASE"/> </b></FONT>
					<xsl:value-of select = "substring-after($LOOKUPVALUE,$AFTERUPPER-CASE)" />
				 </xsl:when>
				 <xsl:otherwise>
					 <xsl:value-of select="$LOOKUPVALUE"/>
				 </xsl:otherwise>
				</xsl:choose>

				<xsl:apply-templates select="LOOKUP-DBS"/>
			</A>
		</td>
	</tr>


</xsl:template>

<xsl:template match="LOOKUP-DBS">
	<a CLASS="ExSmBlueText"><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>(<xsl:apply-templates/>)</a>
</xsl:template>

<xsl:template match="LOOKUP-VALUE">
<xsl:value-of select="."/> -
</xsl:template>




<xsl:template match="DB">
	<xsl:value-of select="."/>
	<xsl:choose>
	<xsl:when test="(position() != last())">, </xsl:when>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>