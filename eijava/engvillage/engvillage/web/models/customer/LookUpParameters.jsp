<!--
   This page the follwing params as input and generates XML output.
   @param java.lang.String.database
-->

<%@ page contentType="text/xml"%>
<%@ page session="false" %>

<%@ page  import="org.ei.controller.ControllerClient"%>
<%@ page  import="org.ei.domain.DatabaseConfig"%>
<%@ page  import="org.ei.session.*"%>

<%@ page errorPage="/error/errorPage.jsp"%>


<%

	int selectedDbMask = -1;
	
	int CPX = DatabaseConfig.CPX_MASK;
	int INSPEC = DatabaseConfig.INS_MASK;
	int NTIS = DatabaseConfig.NTI_MASK;
	int USPTO = DatabaseConfig.USPTO_MASK;
	int CRC = DatabaseConfig.CRC_MASK;
	int C84 = 33; // NOT 32???
	int PCH = DatabaseConfig.PCH_MASK;
	int CHM = DatabaseConfig.CHM_MASK;
	int CBN = DatabaseConfig.CBN_MASK;
	int ELT = DatabaseConfig.ELT_MASK;
	int EPT = DatabaseConfig.EPT_MASK;
	int IBF = DatabaseConfig.IBF_MASK;
	int GEO = DatabaseConfig.GEO_MASK;
	int EU_PATENTS = DatabaseConfig.EUP_MASK;
	int US_PATENTS = DatabaseConfig.UPA_MASK;
	int US_EU_PATENTS = US_PATENTS + EU_PATENTS;  
	int REF = DatabaseConfig.REF_MASK;
	int PAG = DatabaseConfig.PAG_MASK;
	int CBF = DatabaseConfig.CBF_MASK;
	int IBS = DatabaseConfig.IBS_MASK;
	int GRF = DatabaseConfig.GRF_MASK;
         
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
	if(selectedDbMask != CBN)
	{
		if((selectedDbMask & US_PATENTS) != US_PATENTS &&
		   (selectedDbMask & EU_PATENTS) != EU_PATENTS)
		{
%>
			 <FIELD SHORTNAME="AUS" DISPLAYNAME="Author" />
<%
		}
		else if(selectedDbMask == US_PATENTS ||
			selectedDbMask == EU_PATENTS ||
			selectedDbMask == EU_PATENTS + US_PATENTS )
		{
%>
			 <FIELD SHORTNAME="AUS" DISPLAYNAME="Inventor" />
<%
		}
		else
		{
%>
			 <FIELD SHORTNAME="AUS" DISPLAYNAME="Author/Inventor" />
<%
		}
	}
	
	/*
       if((selectedDbMask & US_PATENTS) != US_PATENTS &&
          (selectedDbMask & EU_PATENTS) != EU_PATENTS)
       {
%> 	  
 	  <FIELD SHORTNAME="AUS" DISPLAYNAME="Author" />
<%     }
       else if( selectedDbMask == US_PATENTS ||
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
<%     }
	*/
%>
<%   //AF
	if(selectedDbMask != CBN)
	{
		if((selectedDbMask & PAG) != PAG &&
		   (selectedDbMask & US_PATENTS) != US_PATENTS &&
		   (selectedDbMask & EPT) != EPT &&
		   (selectedDbMask & EU_PATENTS) != EU_PATENTS)
		{
%>
			<FIELD SHORTNAME="AF" DISPLAYNAME="Author affiliation"/>
<%
		}
		else if(selectedDbMask == US_PATENTS ||
			selectedDbMask == EU_PATENTS ||
			selectedDbMask == EPT ||
			selectedDbMask == EU_PATENTS + US_PATENTS )
		{
%>
			 <FIELD SHORTNAME="AF" DISPLAYNAME="Assignee"/>
<%
		}
		else if((selectedDbMask & PAG) != PAG)
		{
%>
			 <FIELD SHORTNAME="AF" DISPLAYNAME="Affiliation/Assignee"/>
<%
		}
	}
	
	/*
       if((selectedDbMask & US_PATENTS) != US_PATENTS &&
          (selectedDbMask & EU_PATENTS) != EU_PATENTS)
       {
%> 	  
 	  <FIELD SHORTNAME="AF" DISPLAYNAME="Author affiliation"/>
<%     }
       else if( selectedDbMask == US_PATENTS ||
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
	*/
%>
<%

   if(searchtype.equals("Quick"))
   {     
      //CVS
	if(selectedDbMask == CPX ||
	   selectedDbMask == CBF)
	{
%>
		<FIELD SHORTNAME="CVS" DISPLAYNAME="Ei Controlled Term"/>
<%
	}
	else if(selectedDbMask == INSPEC ||
		selectedDbMask == IBS)
	{
%>
		<FIELD SHORTNAME="CVS" DISPLAYNAME="Inspec Controlled Term"/>		   
<%
	}
	else if(selectedDbMask == NTIS)
	{
%>
		<FIELD SHORTNAME="CVS" DISPLAYNAME="NTIS Controlled Term"/>		   			  
<%
	}
	else if((selectedDbMask & PAG) != PAG &&
		(selectedDbMask & US_PATENTS) != US_PATENTS &&
		(selectedDbMask & EU_PATENTS) != EU_PATENTS)
	{
%>
		<FIELD SHORTNAME="CVS" DISPLAYNAME="Controlled Term"/>
<%
	}
      /*
      if((selectedDbMask & US_PATENTS) != US_PATENTS &&
         (selectedDbMask & EU_PATENTS) != EU_PATENTS)
      {
%> 	 
 	<FIELD SHORTNAME="CVS" DISPLAYNAME="Controlled term"/>
<%    }
      */
      
	//Patent Country	
	if((selectedDbMask & EPT) == EPT)
	{
%>
		<FIELD SHORTNAME="PC" DISPLAYNAME="Country"/>
<%
	}


      //ST
     if((selectedDbMask & PAG) != PAG &&
        (selectedDbMask & US_PATENTS) != US_PATENTS &&
        (selectedDbMask & EU_PATENTS) != EU_PATENTS &&
        (selectedDbMask & EPT) != EPT &&
        (selectedDbMask & NTIS) != NTIS)
     {
%>
     	<FIELD SHORTNAME="ST" DISPLAYNAME="Serial title"/>
<%
     }
     /*
     if (selectedDbMask < 4 || ((selectedDbMask & GEO) == GEO) || ((selectedDbMask & CBF) == CBF))
     { 
%>  
       <FIELD SHORTNAME="ST" DISPLAYNAME="Serial title"/>
<%   }
     */
     
     //PB
     if((selectedDbMask & GEO) != GEO &&
        (selectedDbMask & US_PATENTS) != US_PATENTS &&
        (selectedDbMask & EU_PATENTS) != EU_PATENTS &&
        (selectedDbMask & EPT) != EPT &&
        (selectedDbMask & NTIS) != NTIS &&
        (selectedDbMask & CBN) != CBN &&
        (selectedDbMask & CHM) != CHM)
     {
%>
     	<FIELD SHORTNAME="PN" DISPLAYNAME="Publisher"/>            
<%
     }
    /*    
    if((selectedDbMask & GEO) != GEO &&
       (selectedDbMask & US_PATENTS) != US_PATENTS &&
       (selectedDbMask & EU_PATENTS) != EU_PATENTS &&
       (selectedDbMask & NTIS) != NTIS)
     {
%>  
     	<FIELD SHORTNAME="PN" DISPLAYNAME="Publisher"/>
<%   }
     */
  }
%>
<% if(!searchtype.equals("Quick"))
   {
   	
   	//CLS	
   	if (((selectedDbMask & CPX) == CPX) || 
   	    ((selectedDbMask & CBF) == CBF) || 
   	    ((selectedDbMask & INSPEC) == INSPEC) || 
   	    ((selectedDbMask & IBS) == IBS) ||
   	    ((selectedDbMask & GEO) == GEO) ||
   	    ((selectedDbMask & GRF) == GRF) ||
   	    ((selectedDbMask & PCH) == PCH) || 
   	    ((selectedDbMask & CHM) == CHM) || 
   	    ((selectedDbMask & EPT) == EPT) || 
   	    ((selectedDbMask & ELT) == ELT) || 
   	    ((selectedDbMask & CBN) == CBN) || 
   	    ((selectedDbMask & NTIS) == NTIS))
        {
%>   
          	<FIELD SHORTNAME="CVS" DISPLAYNAME="Controlled term"/>
<%      }

	//Patent Country
	
	if((selectedDbMask & EPT) == EPT)
	{
%>
		<FIELD SHORTNAME="PC" DISPLAYNAME="Country"/>
<%
	}

	//LA
      	if (((selectedDbMask & CPX) == CPX) || 
      	    ((selectedDbMask & CBF) == CBF) || 
      	    ((selectedDbMask & INSPEC) == INSPEC) || 
      	    ((selectedDbMask & IBS) == IBS) ||
      	    ((selectedDbMask & GEO) == GEO) ||
      	    ((selectedDbMask & GRF) == GRF) ||
      	    ((selectedDbMask & PCH) == PCH) || 
      	    ((selectedDbMask & CHM) == CHM) || 
      	    ((selectedDbMask & EPT) == EPT) || 
      	    ((selectedDbMask & ELT) == ELT) || 
      	    ((selectedDbMask & CBN) == CBN) || 
      	    ((selectedDbMask & NTIS) == NTIS) )
     	{
%>
      		<FIELD SHORTNAME="LA" DISPLAYNAME="Language"/>
<%   	}

        //ST
	if (((selectedDbMask & CPX) == CPX) || 
	    ((selectedDbMask & CBF) == CBF) || 
	    ((selectedDbMask & GEO) == GEO) ||
	    ((selectedDbMask & GRF) == GRF) ||
	    ((selectedDbMask & PCH) == PCH) || 
	    ((selectedDbMask & CHM) == CHM) || 
	    ((selectedDbMask & ELT) == ELT) || 
	    ((selectedDbMask & CBN) == CBN) || 
	    ((selectedDbMask & INSPEC) == INSPEC) ||
	    ((selectedDbMask & IBS) == IBS))
	{
%>
         	<FIELD SHORTNAME="ST" DISPLAYNAME="Serial title"/>
<%	}

	//DT
     	if (((selectedDbMask & CPX) == CPX) || 
     	    ((selectedDbMask & CBF) == CBF) || 
     	    ((selectedDbMask & GEO) == GEO) ||
     	    ((selectedDbMask & GRF) == GRF) ||
     	    ((selectedDbMask & PCH) == PCH) || 
     	    ((selectedDbMask & CHM) == CHM) || 
     	    ((selectedDbMask & ELT) == ELT) || 
     	    ((selectedDbMask & CBN) == CBN) || 
     	    ((selectedDbMask & INSPEC) == INSPEC) ||
     	    ((selectedDbMask & IBS) == IBS))
     	{
%>
       		<FIELD SHORTNAME="DT" DISPLAYNAME="Document type"/>
<%   	}

	//PN
	if (((selectedDbMask & CPX) == CPX) || 
	    ((selectedDbMask & CBF) == CBF) ||
	    ((selectedDbMask & GRF) == GRF) ||
	    ((selectedDbMask & PCH) == PCH) || 
	    ((selectedDbMask & ELT) == ELT) || 
	    ((selectedDbMask & INSPEC) == INSPEC) ||
	    ((selectedDbMask & IBS) == IBS))
	{
%>    
         <FIELD SHORTNAME="PN" DISPLAYNAME="Publisher"/>
<%	}
      
     	//TR
     	if (((selectedDbMask & CPX) == CPX) ||  ((selectedDbMask & INSPEC) == INSPEC) || ((selectedDbMask & IBS) == IBS))
     	{
%>
      		<FIELD SHORTNAME="TR" DISPLAYNAME="Treatment type"/>
<%   	}
     
     	//DSC
     	if (((selectedDbMask & INSPEC) == INSPEC) || ((selectedDbMask & IBS) == IBS))
     	{
%> 
      		<FIELD SHORTNAME="DI" DISPLAYNAME="Discipline"/>
<%   	}
   
  }
%>
 </FIELDS>










