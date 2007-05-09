<!--
   This page the follwing params as input and generates XML output.
   @param java.lang.String.database
-->

<%@ page contentType="text/xml"%>
<%@ page session="false" %>

<%@ page  import="org.ei.controller.ControllerClient"%>
<%@ page  import="org.ei.session.*"%>

<%@ page errorPage="/error/errorPage.jsp"%>


<%

	int selectedDbMask = -1;
	int NTIS = 4;
	int INSPEC = 2;
	int CPX = 1;
	int C84 = 33;
	int EU_PATENTS = 16384;
	int US_PATENTS = 32768;
    int US_EU_PATENTS = 49152;
    int GEO = 8192;
    int CBF = 262144;
         
	String searchtype = null;
	

	if(request.getParameter("database")!=null) 
 	{
		selectedDbMask = Integer.parseInt(request.getParameter("database"));
    	}
    
    	if(request.getParameter("searchtype")!=null)
    	{
		searchtype = request.getParameter("searchtype").trim();
    	}
%>    	
       <FIELDS>
<%     //AU
       if((selectedDbMask & US_PATENTS) != US_PATENTS &&
          (selectedDbMask & EU_PATENTS) != EU_PATENTS)
       {
%> 	  
 	  <FIELD SHORTNAME="AUS" DISPLAYNAME="Author" />
<%     }
       else if(selectedDbMask == US_PATENTS ||
 	   selectedDbMask == EU_PATENTS ||
 	   selectedDbMask == US_EU_PATENTS )
       {
%> 	  
 	 <FIELD SHORTNAME="AUS" DISPLAYNAME="Inventor" />
<%     }
       else
       {
%>     
         <FIELD SHORTNAME="AUS" DISPLAYNAME="Author/Inventor" />
<%      }
%>
<%       //AF
     if((selectedDbMask & US_PATENTS) != US_PATENTS &&
          (selectedDbMask & EU_PATENTS) != EU_PATENTS)
       {
%> 	  
 	  <FIELD SHORTNAME="AF" DISPLAYNAME="Author affiliation"/>
<%     }
       else if(selectedDbMask == US_PATENTS ||
 	   selectedDbMask == EU_PATENTS ||
 	   selectedDbMask == US_EU_PATENTS )
       {
%> 	  
 	  <FIELD SHORTNAME="AF" DISPLAYNAME="Assignee"/>
<%     }
       else
       {
%>     
         <FIELD SHORTNAME="AF" DISPLAYNAME="Affiliation/Assignee"/>
<%      }
%>
<%

   if(searchtype.equals("Quick"))
   {     
      //CVS
      if((selectedDbMask & US_PATENTS) != US_PATENTS &&
         (selectedDbMask & EU_PATENTS) != EU_PATENTS)
      {
%> 	 
 	<FIELD SHORTNAME="CVS" DISPLAYNAME="Controlled term"/>
<%    }

      //ST
     if (selectedDbMask < 4 || ((selectedDbMask & GEO) == GEO) || ((selectedDbMask & CBF) == CBF))
     { 
%>  
       <FIELD SHORTNAME="ST" DISPLAYNAME="Serial title"/>
<%   }
     //PB
    if((selectedDbMask & GEO) != GEO &&
       (selectedDbMask & US_PATENTS) != US_PATENTS &&
       (selectedDbMask & EU_PATENTS) != EU_PATENTS &&
       (selectedDbMask & NTIS) != NTIS)
     {
%>  
     	<FIELD SHORTNAME="PN" DISPLAYNAME="Publisher"/>
<%   }
  }
%>
<% if(!searchtype.equals("Quick"))
   {
   	//CLS	
   	if (((selectedDbMask & CPX) == CPX) || ((selectedDbMask & CBF) == CBF) || ((selectedDbMask & INSPEC) == INSPEC) || ((selectedDbMask & GEO) == GEO) || ((selectedDbMask & NTIS) == NTIS))
        {
%>   
          	<FIELD SHORTNAME="CVS" DISPLAYNAME="Controlled term"/>
<%       }

	//LA
      	if (((selectedDbMask & CPX) == CPX) || ((selectedDbMask & CBF) == CBF) || ((selectedDbMask & INSPEC) == INSPEC) || ((selectedDbMask & GEO) == GEO) || ((selectedDbMask & NTIS) == NTIS) )
     	{
%>
      		<FIELD SHORTNAME="LA" DISPLAYNAME="Language"/>
<%   	}

        //ST
	if (((selectedDbMask & CPX) == CPX) || ((selectedDbMask & CBF) == CBF) || ((selectedDbMask & GEO) == GEO) || ((selectedDbMask & INSPEC) == INSPEC))
	{
%>
         	<FIELD SHORTNAME="ST" DISPLAYNAME="Serial title"/>
<%	}

	//DT
     	if (((selectedDbMask & CPX) == CPX) || ((selectedDbMask & CBF) == CBF) || ((selectedDbMask & GEO) == GEO) ||((selectedDbMask & INSPEC) == INSPEC))
     	{
%>
       		<FIELD SHORTNAME="DT" DISPLAYNAME="Document type"/>
<%   	}

	//PN
	if (((selectedDbMask & CPX) == CPX) || ((selectedDbMask & CBF) == CBF) || ((selectedDbMask & INSPEC) == INSPEC))
	{
%>    
         <FIELD SHORTNAME="PN" DISPLAYNAME="Publisher"/>
<%	}
      
     	//TR
     	if (((selectedDbMask & CPX) == CPX) ||  ((selectedDbMask & INSPEC) == INSPEC))
     	{
%>
      		<FIELD SHORTNAME="TR" DISPLAYNAME="Treatment type"/>
<%   	}
     
     	//DSC
     	if ((selectedDbMask & INSPEC) == INSPEC)
     	{
%> 
      		<FIELD SHORTNAME="DI" DISPLAYNAME="Discipline"/>
<%   	}
   
  }
%>
 </FIELDS>










