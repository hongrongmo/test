<%@page import="org.apache.commons.validator.GenericValidator"%>
<%@page import="org.ei.domain.DatabaseConfig"%>
<EASY-SEARCH/>
<EXPERT-SEARCH/>
<%
// this is not the selected start and end year - but the minimum
// and maximum values that appear in the drop boxes!
// "SELECTED" options are output by ClientCustomizer
// USED FOR THESAURUS FORM
int startYearOpt = 0;
final int endYearOpt = org.ei.domain.SearchForm.ENDYEAR;
int intMask = Integer.parseInt(database);
int intUpgradeMask = (DatabaseConfig.getInstance()).doUpgrade(intMask, ussession.getCartridge());
boolean doUpgrade = (intUpgradeMask != intMask);
//Page oPage=null;

if(intMask == 1)
{
    // THESAURUS SEARCH FORM USES THESE LIMITERS
    %>
    <COMBINED-QUICK-SEARCH>
    <LIMITERS>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="DT" DISPLAYNAME="Document type">
    <OPTIONS>
    <OPTION DISPLAYNAME="All document types" SHORTNAME="NO-LIMIT"/>
    <OPTION DISPLAYNAME="Journal article" SHORTNAME="JA"/>
    <OPTION DISPLAYNAME="Conference article" SHORTNAME="CA"/>
    <OPTION DISPLAYNAME="Conference proceeding" SHORTNAME="CP"/>
    <OPTION DISPLAYNAME="Monograph chapter" SHORTNAME="MC"/>
    <OPTION DISPLAYNAME="Monograph review" SHORTNAME="MR"/>
    <OPTION DISPLAYNAME="Report chapter" SHORTNAME="RC"/>
    <OPTION DISPLAYNAME="Report review" SHORTNAME="RR"/>
    <OPTION DISPLAYNAME="Dissertation" SHORTNAME="DS"/>
    <OPTION DISPLAYNAME="Unpublished paper" SHORTNAME="UP"/>
    <%
    if(doUpgrade)
    { %>
        <OPTION DISPLAYNAME="Patents (before 1970)" SHORTNAME="PA"/>
<%
    }
    %>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="TR" DISPLAYNAME="Treatment type">
    <OPTIONS>
    <OPTION DISPLAYNAME="All treatment types" SHORTNAME="NO-LIMIT"/>
    <OPTION DISPLAYNAME="Applications" SHORTNAME="APP"/>
    <OPTION DISPLAYNAME="Biographical" SHORTNAME="BIO"/>
    <OPTION DISPLAYNAME="Economic" SHORTNAME="ECO"/>
    <OPTION DISPLAYNAME="Experimental" SHORTNAME="EXP"/>
    <OPTION DISPLAYNAME="General review" SHORTNAME="GEN"/>
    <OPTION DISPLAYNAME="Historical" SHORTNAME="HIS"/>
    <OPTION DISPLAYNAME="Literature review" SHORTNAME="LIT"/>
    <OPTION DISPLAYNAME="Management aspects" SHORTNAME="MAN"/>
    <OPTION DISPLAYNAME="Numerical" SHORTNAME="NUM"/>
    <OPTION DISPLAYNAME="Theoretical" SHORTNAME="THR"/>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="LA" DISPLAYNAME="Language">
    <OPTIONS>
    <OPTION DISPLAYNAME="All languages" SHORTNAME="NO-LIMIT"/>
    <OPTION DISPLAYNAME="English" SHORTNAME="English"/>
    <OPTION DISPLAYNAME="Chinese" SHORTNAME="Chinese"/>
    <OPTION DISPLAYNAME="French" SHORTNAME="French"/>
    <OPTION DISPLAYNAME="German" SHORTNAME="German"/>
    <OPTION DISPLAYNAME="Italian" SHORTNAME="Italian"/>
    <OPTION DISPLAYNAME="Japanese" SHORTNAME="Japanese"/>
    <OPTION DISPLAYNAME="Russian" SHORTNAME="Russian"/>
    <OPTION DISPLAYNAME="Spanish" SHORTNAME="Spanish"/>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="YR" DISPLAYNAME="YEARS">
    <OPTIONS>
    <%
    if(doUpgrade)
    {
        startYearOpt = 1884;
    }
    else
    {
        startYearOpt = 1969;
    }

    for(;startYearOpt <= endYearOpt; startYearOpt++)
    { %>
        <OPTION SHORTNAME="<%=startYearOpt%>" DISPLAYNAME="<%=startYearOpt%>" />
<%  }
    %>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    </LIMITERS>
    </COMBINED-QUICK-SEARCH>
    <%
}
else if(intMask == 2)
{
    // THESAURUS SEARCH FORM USES THESE LIMITERS
    %>
    <COMBINED-QUICK-SEARCH>
    <LIMITERS>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="DT" DISPLAYNAME="Document type">
    <OPTIONS>
    <OPTION DISPLAYNAME="All document types" SHORTNAME="NO-LIMIT"/>
    <OPTION DISPLAYNAME="Journal article" SHORTNAME="JA"/>
    <OPTION DISPLAYNAME="Conference article" SHORTNAME="CA"/>
    <OPTION DISPLAYNAME="Conference proceeding" SHORTNAME="CP"/>
    <OPTION DISPLAYNAME="Monograph chapter" SHORTNAME="MC"/>
    <OPTION DISPLAYNAME="Monograph review" SHORTNAME="MR"/>
    <OPTION DISPLAYNAME="Report chapter" SHORTNAME="RC"/>
    <OPTION DISPLAYNAME="Report review" SHORTNAME="RR"/>
    <OPTION DISPLAYNAME="Dissertation" SHORTNAME="DS"/>
    <OPTION DISPLAYNAME="Patent (1969-1976)" SHORTNAME="PA"/>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="TR" DISPLAYNAME="Treatment type">
    <OPTIONS>
    <OPTION DISPLAYNAME="All treatment types" SHORTNAME="NO-LIMIT"/>
    <OPTION DISPLAYNAME="Applications" SHORTNAME="APP"/>
    <OPTION DISPLAYNAME="Bibliography" SHORTNAME="BIB"/>
    <OPTION DISPLAYNAME="Economic" SHORTNAME="ECO"/>
    <OPTION DISPLAYNAME="Experimental" SHORTNAME="EXP"/>
    <OPTION DISPLAYNAME="General review" SHORTNAME="GEN"/>
    <OPTION DISPLAYNAME="New development" SHORTNAME="NEW"/>
    <OPTION DISPLAYNAME="Practical" SHORTNAME="PRA"/>
    <OPTION DISPLAYNAME="Product review" SHORTNAME="PRO"/>
    <OPTION DISPLAYNAME="Theoretical" SHORTNAME="THR"/>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="DI" DISPLAYNAME="Discipline type">
    <OPTIONS>
    <OPTION DISPLAYNAME="All disciplines" SHORTNAME="NO-LIMIT"/>
    <OPTION DISPLAYNAME="Physics" SHORTNAME="A"/>
    <OPTION DISPLAYNAME="Electrical/Electronic engineering" SHORTNAME="B"/>
    <OPTION DISPLAYNAME="Computers/Control engineering" SHORTNAME="C"/>
    <OPTION DISPLAYNAME="Information technology" SHORTNAME="D"/>
    <OPTION DISPLAYNAME="Manufacturing and production engineering" SHORTNAME="E"/>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="LA" DISPLAYNAME="Language">
    <OPTIONS>
    <OPTION DISPLAYNAME="All languages" SHORTNAME="NO-LIMIT"/>
    <OPTION DISPLAYNAME="English" SHORTNAME="English"/>
    <OPTION DISPLAYNAME="Chinese" SHORTNAME="Chinese"/>
    <OPTION DISPLAYNAME="French" SHORTNAME="French"/>
    <OPTION DISPLAYNAME="German" SHORTNAME="German"/>
    <OPTION DISPLAYNAME="Italian" SHORTNAME="Italian"/>
    <OPTION DISPLAYNAME="Japanese" SHORTNAME="Japanese"/>
    <OPTION DISPLAYNAME="Russian" SHORTNAME="Russian"/>
    <OPTION DISPLAYNAME="Spanish" SHORTNAME="Spanish"/>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="YR" DISPLAYNAME="YEARS">
    <OPTIONS>
    <%
    if(doUpgrade)
    {
        startYearOpt = 1896;
    }
    else
    {
        startYearOpt = 1969;
    }

    for(;startYearOpt <= endYearOpt; startYearOpt++)
    { %>
        <OPTION SHORTNAME="<%=startYearOpt%>" DISPLAYNAME="<%=startYearOpt%>" />
<%  }
    %>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    </LIMITERS>
    </COMBINED-QUICK-SEARCH>
    <%
}
else if (intMask == 4)
{
    // ALL OTHER FIELD LIMITERS ARE IN DYNAMIC JAVASCRIPT
%>
    <COMBINED-QUICK-SEARCH>
    <LIMITERS>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="LA" DISPLAYNAME="Language">
    <OPTIONS>
    <OPTION DISPLAYNAME="All languages" SHORTNAME="NO-LIMIT"/>
    <OPTION DISPLAYNAME="English" SHORTNAME="English"/>
    <OPTION DISPLAYNAME="Chinese" SHORTNAME="Chinese"/>
    <OPTION DISPLAYNAME="French" SHORTNAME="French"/>
    <OPTION DISPLAYNAME="German" SHORTNAME="German"/>
    <OPTION DISPLAYNAME="Italian" SHORTNAME="Italian"/>
    <OPTION DISPLAYNAME="Japanese" SHORTNAME="Japanese"/>
    <OPTION DISPLAYNAME="Russian" SHORTNAME="Russian"/>
    <OPTION DISPLAYNAME="Spanish" SHORTNAME="Spanish"/>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    </LIMITERS>
    </COMBINED-QUICK-SEARCH>
<%
}
else if (database.equals("8"))
{
%>
    <USPTO-EXPERT-SEARCH/>
    <USPTO-QUICK-SEARCH>
    <FIELDS>
    <FIELD SHORTNAME="NO-LIMIT" DISPLAYNAME="All fields" />
    <FIELD SHORTNAME="TI" DISPLAYNAME="Title" />
    <FIELD SHORTNAME="AB" DISPLAYNAME="Abstract"/>
    <FIELD SHORTNAME="SD" DISPLAYNAME="Issue date"/>
    <FIELD SHORTNAME="PT" DISPLAYNAME="Patent number"/>
    <FIELD SHORTNAME="AP" DISPLAYNAME="Application date" />
    <FIELD SHORTNAME="APN" DISPLAYNAME="App. serial number" />
    <FIELD SHORTNAME="APT" DISPLAYNAME="Application type"/>
    <FIELD SHORTNAME="AF" DISPLAYNAME="Assignee name"/>
    <FIELD SHORTNAME="AC" DISPLAYNAME="Assignee city"/>
    <FIELD SHORTNAME="AS" DISPLAYNAME="Assignee state"/>
    <FIELD SHORTNAME="ACN" DISPLAYNAME="Assignee country" />
    <FIELD SHORTNAME="ICL" DISPLAYNAME="Internat'l classification"/>
    <FIELD SHORTNAME="CCL" DISPLAYNAME="US classification"/>
    <FIELD SHORTNAME="EXP" DISPLAYNAME="Primary examiner"/>
    <FIELD SHORTNAME="EXA" DISPLAYNAME="Assistant examiner" />
    <FIELD SHORTNAME="AU" DISPLAYNAME="Inventor name" />
    <FIELD SHORTNAME="IC" DISPLAYNAME="Inventor city" />
    <FIELD SHORTNAME="IS" DISPLAYNAME="Inventor state"/>
    <FIELD SHORTNAME="ICN" DISPLAYNAME="Inventor country"/>
    <FIELD SHORTNAME="GOVT" DISPLAYNAME="Government interest"/>
    <FIELD SHORTNAME="PARN" DISPLAYNAME="Parent case info."/>
    <FIELD SHORTNAME="LREP" DISPLAYNAME="Attorney/Agent" />
    <FIELD SHORTNAME="PCT" DISPLAYNAME="PCT information" />
    <FIELD SHORTNAME="PRIR" DISPLAYNAME="Foreign priority" />
    <FIELD SHORTNAME="REIS" DISPLAYNAME="Reissue data"/>
    <FIELD SHORTNAME="RLAP" DISPLAYNAME="Rel. US app. info"/>
    <FIELD SHORTNAME="REF" DISPLAYNAME="US references"/>
    <FIELD SHORTNAME="FREF" DISPLAYNAME="Foreign references" />
    <FIELD SHORTNAME="OREF" DISPLAYNAME="Other references"/>
    <FIELD SHORTNAME="ACLM" DISPLAYNAME="Claim(s)"/>
    <FIELD SHORTNAME="SPEC" DISPLAYNAME="Description/Spec."/>
    </FIELDS>
    <LIMITERS>
    <CONSTRAINED-FIELD SHORTNAME="YR" DISPLAYNAME="YEARS">
    <YR-OPTIONS>
    <OPTION NAME="ptxt" VALUE="1976 to present [full-text]" />
    <OPTION NAME="pall" VALUE="1790 to present [entire database]" />
    <OPTION NAME="o179" VALUE="1790 to 1975 [PN and CCL only]" />
    </YR-OPTIONS>
    </CONSTRAINED-FIELD>
    </LIMITERS>
    </USPTO-QUICK-SEARCH>
<%
}
else if(database.equals("16"))
{
%>
    <ENGnetBASE-EXPERT-SEARCH/>
    <ENGnetBASE-QUICK-SEARCH>
    <SORT-DEFAULT>relevance</SORT-DEFAULT>
    <AUTOSTEM-DEFAULT>off</AUTOSTEM-DEFAULT>
    <FIELDS>
    <FIELD AUTOSTEM="on" SHORTNAME="NO-LIMIT" DISPLAYNAME="All fields" />
    </FIELDS>
    </ENGnetBASE-QUICK-SEARCH>
<%
}
else if(database.equals("1024") )
{
    startYearOpt = 1962;
%>
    <COMBINED-QUICK-SEARCH>
    <LIMITERS>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="YR" DISPLAYNAME="YEARS">
    <OPTIONS>
    <%    
    for(;startYearOpt <= endYearOpt; startYearOpt++)
    { %>
        <OPTION SHORTNAME="<%=startYearOpt%>" DISPLAYNAME="<%=startYearOpt%>" />
<%  }
    %>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="DT" DISPLAYNAME="Document type">
    <OPTIONS>
    <OPTION DISPLAYNAME="Document type not available" SHORTNAME="NO-LIMIT"/>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="TR" DISPLAYNAME="Treatment type">
    <OPTIONS>
    <OPTION DISPLAYNAME="Treatment type not available" SHORTNAME="NO-LIMIT"/>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="LA" DISPLAYNAME="Language">
    <OPTIONS>
    <OPTION DISPLAYNAME="All languages" SHORTNAME="NO-LIMIT"/>
    <OPTION DISPLAYNAME="English" SHORTNAME="English"/>
    <OPTION DISPLAYNAME="Chinese" SHORTNAME="Chinese"/>
    <OPTION DISPLAYNAME="French" SHORTNAME="French"/>
    <OPTION DISPLAYNAME="German" SHORTNAME="German"/>
    <OPTION DISPLAYNAME="Japanese" SHORTNAME="Japanese"/>
    <OPTION DISPLAYNAME="Russian" SHORTNAME="Russian"/>
    <OPTION DISPLAYNAME="Spanish" SHORTNAME="Spanish"/>
    </OPTIONS>
    </CONSTRAINED-FIELD>    
    </LIMITERS>
    
    </COMBINED-QUICK-SEARCH>
<%
}
else if(database.equals("2048") )
{
    startYearOpt = 1963;
%>
    <COMBINED-QUICK-SEARCH>
    <LIMITERS>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="YR" DISPLAYNAME="YEARS">
    <OPTIONS>
    <%    
    for(;startYearOpt <= endYearOpt; startYearOpt++)
    { %>
        <OPTION SHORTNAME="<%=startYearOpt%>" DISPLAYNAME="<%=startYearOpt%>" />
<%  }
    %>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="DT" DISPLAYNAME="Document type">
    <OPTIONS>
    <OPTION DISPLAYNAME="Document type not available" SHORTNAME="NO-LIMIT"/>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="TR" DISPLAYNAME="Treatment type">
    <OPTIONS>
    <OPTION DISPLAYNAME="Treatment type not available" SHORTNAME="NO-LIMIT"/>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="LA" DISPLAYNAME="Language">
    <OPTIONS>
    <OPTION DISPLAYNAME="All languages" SHORTNAME="NO-LIMIT"/>
    <OPTION DISPLAYNAME="English" SHORTNAME="English"/>
    <OPTION DISPLAYNAME="Chinese" SHORTNAME="Chinese"/>
    <OPTION DISPLAYNAME="French" SHORTNAME="French"/>
    <OPTION DISPLAYNAME="German" SHORTNAME="German"/>
    <OPTION DISPLAYNAME="Japanese" SHORTNAME="Japanese"/>
    <OPTION DISPLAYNAME="Russian" SHORTNAME="Russian"/>
    <OPTION DISPLAYNAME="Spanish" SHORTNAME="Spanish"/>
    </OPTIONS>
    </CONSTRAINED-FIELD>    
    </LIMITERS>
    
    </COMBINED-QUICK-SEARCH>
<%
}
else if(database.equals("3072") )
{
    startYearOpt = 1962;
%>
    <COMBINED-QUICK-SEARCH>
    <LIMITERS>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="YR" DISPLAYNAME="YEARS">
    <OPTIONS>
    <%    
    for(;startYearOpt <= endYearOpt; startYearOpt++)
    { %>
        <OPTION SHORTNAME="<%=startYearOpt%>" DISPLAYNAME="<%=startYearOpt%>" />
<%  }
    %>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="DT" DISPLAYNAME="Document type">
    <OPTIONS>
    <OPTION DISPLAYNAME="Document type not available" SHORTNAME="NO-LIMIT"/>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="TR" DISPLAYNAME="Treatment type">
    <OPTIONS>
    <OPTION DISPLAYNAME="Treatment type not available" SHORTNAME="NO-LIMIT"/>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="LA" DISPLAYNAME="Language">
    <OPTIONS>
    <OPTION DISPLAYNAME="All languages" SHORTNAME="NO-LIMIT"/>
    <OPTION DISPLAYNAME="English" SHORTNAME="English"/>
    <OPTION DISPLAYNAME="Chinese" SHORTNAME="Chinese"/>
    <OPTION DISPLAYNAME="French" SHORTNAME="French"/>
    <OPTION DISPLAYNAME="German" SHORTNAME="German"/>
    <OPTION DISPLAYNAME="Japanese" SHORTNAME="Japanese"/>
    <OPTION DISPLAYNAME="Russian" SHORTNAME="Russian"/>
    <OPTION DISPLAYNAME="Spanish" SHORTNAME="Spanish"/>
    </OPTIONS>
    </CONSTRAINED-FIELD>    
    </LIMITERS>
    
    </COMBINED-QUICK-SEARCH>
<%
}
else if(database.equals("8192") )
{
    startYearOpt = 1973;
%>
    <COMBINED-QUICK-SEARCH>
    <LIMITERS>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="YR" DISPLAYNAME="YEARS">
    <OPTIONS>
    <%    
    for(;startYearOpt <= endYearOpt; startYearOpt++)
    { %>
        <OPTION SHORTNAME="<%=startYearOpt%>" DISPLAYNAME="<%=startYearOpt%>" />
<%  }
    %>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="DT" DISPLAYNAME="Document type">
    <OPTIONS>
    <OPTION DISPLAYNAME="All document types" SHORTNAME="NO-LIMIT"/>
    <OPTION DISPLAYNAME="Journal article" SHORTNAME="JA"/>
    <OPTION DISPLAYNAME="Conference article" SHORTNAME="CA"/>
    <OPTION DISPLAYNAME="Monograph review" SHORTNAME="MR"/>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="TR" DISPLAYNAME="Treatment type">
    <OPTIONS>
    <OPTION DISPLAYNAME="Treatment type not available" SHORTNAME="NO-LIMIT"/>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="LA" DISPLAYNAME="Language">
    <OPTIONS>
    <OPTION DISPLAYNAME="All languages" SHORTNAME="NO-LIMIT"/>
    <OPTION DISPLAYNAME="English" SHORTNAME="English"/>
    <OPTION DISPLAYNAME="Chinese" SHORTNAME="Chinese"/>
    <OPTION DISPLAYNAME="French" SHORTNAME="French"/>
    <OPTION DISPLAYNAME="German" SHORTNAME="German"/>
    <OPTION DISPLAYNAME="Italian" SHORTNAME="Italian"/>
    <OPTION DISPLAYNAME="Japanese" SHORTNAME="Japanese"/>
    <OPTION DISPLAYNAME="Russian" SHORTNAME="Russian"/>
    <OPTION DISPLAYNAME="Spanish" SHORTNAME="Spanish"/>
    </OPTIONS>
    </CONSTRAINED-FIELD>    
    </LIMITERS>
    
    </COMBINED-QUICK-SEARCH>
<%
}
else if(database.equals("2097152"))
{
    startYearOpt = 1785;
%>
    <COMBINED-QUICK-SEARCH>
    <LIMITERS>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="YR" DISPLAYNAME="YEARS">
    <OPTIONS>
    <%    
    for(;startYearOpt <= endYearOpt; startYearOpt++)
    { %>
        <OPTION SHORTNAME="<%=startYearOpt%>" DISPLAYNAME="<%=startYearOpt%>" />
<%  }
    %>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="DT" DISPLAYNAME="Document type">
    <OPTIONS>
    <OPTION DISPLAYNAME="All document types" SHORTNAME="NO-LIMIT"/>
    <OPTION DISPLAYNAME="Journal article" SHORTNAME="JA"/>
    <OPTION DISPLAYNAME="Conference article" SHORTNAME="CA"/>
    <OPTION DISPLAYNAME="Conference proceeding" SHORTNAME="CP"/>
    <OPTION DISPLAYNAME="Monograph chapter" SHORTNAME="MC"/>
    <OPTION DISPLAYNAME="Monograph review" SHORTNAME="MR"/>
    <OPTION DISPLAYNAME="Report chapter" SHORTNAME="RC"/>
    <OPTION DISPLAYNAME="Report review" SHORTNAME="RR"/>
    <OPTION DISPLAYNAME="Dissertation" SHORTNAME="DS"/>
    <OPTION DISPLAYNAME="Map" SHORTNAME="MP"/>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="TR" DISPLAYNAME="Treatment type">
    <OPTIONS>
    <OPTION DISPLAYNAME="Treatment type not available" SHORTNAME="NO-LIMIT"/>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="LA" DISPLAYNAME="Language">
    <OPTIONS>
    <OPTION DISPLAYNAME="All languages" SHORTNAME="NO-LIMIT"/>
    <OPTION DISPLAYNAME="English" SHORTNAME="English"/>
    <OPTION DISPLAYNAME="Chinese" SHORTNAME="Chinese"/>
    <OPTION DISPLAYNAME="French" SHORTNAME="French"/>
    <OPTION DISPLAYNAME="German" SHORTNAME="German"/>
    <OPTION DISPLAYNAME="Italian" SHORTNAME="Italian"/>
    <OPTION DISPLAYNAME="Japanese" SHORTNAME="Japanese"/>
    <OPTION DISPLAYNAME="Russian" SHORTNAME="Russian"/>
    <OPTION DISPLAYNAME="Spanish" SHORTNAME="Spanish"/>
    </OPTIONS>
    </CONSTRAINED-FIELD>     
    </LIMITERS>
    
    </COMBINED-QUICK-SEARCH>
<%

}
else
{
%>
    <COMBINED-QUICK-SEARCH>
    <LIMITERS>
    <CONSTRAINED-FIELD MASK="<%=database %>" SHORTNAME="LA" DISPLAYNAME="Language">
    <OPTIONS>
    <OPTION DISPLAYNAME="All languages" SHORTNAME="NO-LIMIT"/>
    <OPTION DISPLAYNAME="English" SHORTNAME="English"/>
    <OPTION DISPLAYNAME="Chinese" SHORTNAME="Chinese"/>
    <OPTION DISPLAYNAME="French" SHORTNAME="French"/>
    <OPTION DISPLAYNAME="German" SHORTNAME="German"/>
    <OPTION DISPLAYNAME="Italian" SHORTNAME="Italian"/>
    <OPTION DISPLAYNAME="Japanese" SHORTNAME="Japanese"/>
    <OPTION DISPLAYNAME="Russian" SHORTNAME="Russian"/>
    <OPTION DISPLAYNAME="Spanish" SHORTNAME="Spanish"/>
    </OPTIONS>
    </CONSTRAINED-FIELD>
    </LIMITERS>
    </COMBINED-QUICK-SEARCH>
<%
}
%>
    <UPDATES SHORTNAME="UP" DISPLAYNAME="UPDATES">
        <OPTIONS>
        <OPTION DISPLAYNAME="1" SHORTNAME="1"/>
        <OPTION DISPLAYNAME="2" SHORTNAME="2"/>
        <OPTION DISPLAYNAME="3" SHORTNAME="3"/>
        <OPTION DISPLAYNAME="4" SHORTNAME="4"/>
        </OPTIONS>
    </UPDATES>
<%
    int userMaskMAX = (DatabaseConfig.getInstance()).getMask(ussession.getCartridge());
    int userScrubbedMaskMAX = (DatabaseConfig.getInstance()).getScrubbedMask(ussession.getCartridge());

    //System.out.println("USER_Cartridge= "+ussession.getCartridge());
    //System.out.println("userMaskMAX= "+userMaskMAX);
    //System.out.println("userScrubbedMaskMAX= "+userScrubbedMaskMAX);

//    log(" userMaskMAX " + userMaskMAX);
//    log(" userScrubbedMaskMAX " + userScrubbedMaskMAX);

    int wholeMask = userScrubbedMaskMAX;

    // NEWS-TITLE BASED ON SCRUBBED MASK - ONLY MAIN DATABASES ARE INCLUDED
    // NEWS-TEXT BASED ON USER'S MAX MASK - SO BACKFILES ARE INCLUDED
%>
    <NEWS-TITLE><![CDATA[Databases]]></NEWS-TITLE>
    <NEWS-TEXT><![CDATA[<%= DatabaseDisplayHelper.getNewsText(userMaskMAX) %>]]></NEWS-TEXT>
<%
// ALL SEARCH CODES ARE NOW INCLUDED REGARDLESS OF SELECTED DB
// THEY ARE BASED ON USER'S MAX MASK - SO INCLUDE ALL POSSIBLE DB CHOICES
// add test here for ONLY OUTPUT ON AN EXPERT SEARCH
{
    // explanation of the logic of testing (intMask & <CODE MASK>) > 0
    //
    // example using values intMask=5 and CODE MASK="3"
    //
    // intMask 5 means the user has CPX and NTIS
    // CODE MASK 3 means the code is to be displayed for CPX and Inspec
    //
    // the '&' operation will cancel out any database which does not exist in the intMask - (here Inspec)
    // and cancel out any database which does not exist in the CODE MASK - (here NTIS)
    // leaving us with the result - (here CPX)
    // so the code gets displayed with a 'C' after it
    // i.e.
    // (5 & 3) = 1
    // since 001 is  > 0 - this means that intMask contains (at least one) a bit from the
    // CODE MASK - so then we need to display the legend for this CODE
    //
    // the "if ((intMask & 7) > 0)" test saves a call to DatabaseDisplayHelper.getDisplayInits(0)
    // which should return an empty string (?) :-)

    // avoid legend initials after fields when usermask is a single database
    boolean blnSingleDatabase = DatabaseDisplayHelper.singleDatabase(wholeMask);
    %>
<SEARCH-CODES>
    <LEGEND><![CDATA[<%= DatabaseDisplayHelper.getSearchCodesLegend(wholeMask) %>]]></LEGEND>
 <%
        CustomSearchField[] customsearchfield = SearchFields.unionSearchFields(userMaskMAX, userScrubbedMaskMAX);

    	for (int i = 0; i < customsearchfield.length; i++)
    	{
	   SearchField searchfield = customsearchfield[i].getSearchField();
	   Database[] customdatabases = customsearchfield[i].getDatabases();

	   out.write("<FIELD ID=\"");
	   out.write(searchfield.getID());
	   out.write("\" LABEL=\"");
	   out.write(customsearchfield[i].getTitle());
	   out.write("\">");

	   for (int j = 0; j < customdatabases.length; j++)
	   {
	   	  //String dbindex = customdatabases[j].getName().substring(0,1);
	   	  String dbindex = customdatabases[j].getLegendID();
	   	  out.write("<DB>");
	   	  out.write(dbindex);
	   	  out.write("</DB>");
	   }
	   out.write("</FIELD>");

   	 }
	 
  %>
</SEARCH-CODES>
 <%
}
%>
<%
  String reqCID = request.getParameter("CID");
  String dbCode = request.getParameter("database");
  if (dbCode == null || dbCode.length() == 0) dbCode = Integer.toString(DatabaseConfig.PAG_MASK);

  if(reqCID != null && dbCode != null && (reqCID.startsWith("ebookSearch") || (dbCode.equals("131072") && reqCID.startsWith("errorQuickSearchResult"))))
  {
  //output browse collections
      String[] creds = ussession.getCartridge();
      Arrays.sort(creds);
      boolean perpetual =  (Arrays.binarySearch(creds, "BPE") >= 0);
      boolean col = true, colstar = true;

%>
    <EBOOK-SEARCH>
    <%
    String sbstate = request.getParameter("bstate");
    if (GenericValidator.isBlankOrNull(sbstate)) sbstate = "119";
    org.ei.books.library.Library library = org.ei.books.library.Library.getInstance();
    org.ei.books.library.LibraryVisitor libraryVisitor = null;
    libraryVisitor = new org.ei.books.library.LibraryVisitor(creds,perpetual);
    library.accept(libraryVisitor);
    libraryVisitor.toXML(out,sbstate);
    %>
    </EBOOK-SEARCH>
<%
  }
%>