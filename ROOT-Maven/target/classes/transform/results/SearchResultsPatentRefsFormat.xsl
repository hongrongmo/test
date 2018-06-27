<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet
[
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLEncoder"
  xmlns:decoder="java:java.net.URLDecoder"
  xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
  xmlns:srt="java:org.ei.domain.Sort"
  xmlns:bit="java:org.ei.util.BitwiseOperators"
  xmlns:xslcid="java:org.ei.domain.XSLCIDHelper"
  xmlns:custoptions="java:org.ei.fulldoc.FullTextOptions"
  xmlns:actionbean="java:org.ei.stripes.action.results.SearchResultsAction"
  xmlns:searchresult="java:org.ei.stripes.view.SearchResult"
  xmlns:patentrefsummary="java:org.ei.stripes.view.PatentrefSummary"
  xmlns:searchresultdoc="java:org.ei.stripes.view.Doc"
  xmlns:author="java:org.ei.stripes.view.Author"
  xmlns:affil="java:org.ei.stripes.view.Affil"
  exclude-result-prefixes="java html xsl DD srt bit xslcid custoptions">
 
  <xsl:output method="html" indent="no" />
  <xsl:strip-space elements="html:* xsl:*" />
  
   
  <xsl:template match="PAGE-RESULTS/UPT-DOC">
       	<xsl:variable name="patentrefsummary" select="patentrefsummary:new()"/>
       	<xsl:variable name="sr" select="searchresult:new()"/>
       	<xsl:value-of select="patentrefsummary:addSearchPresult($patentrefsummary,$sr)" />
  
  <xsl:apply-templates select="EI-DOCUMENT">
			<xsl:with-param name="sr" select="$sr"></xsl:with-param>
 </xsl:apply-templates>
  
    <xsl:choose>
      <xsl:when test="($CID='quickSearchNonPatentReferencesFormat' or $CID='expertSearchNonPatentReferencesFormat')">
       		
       	 <xsl:variable name="NP-COUNT"> <xsl:value-of select="//NP-COUNT"/></xsl:variable>
		 <xsl:variable name="AREIS">
            <xsl:if test="($NP-COUNT) and ($NP-COUNT ='1')">
              <xsl:text> is </xsl:text>
            </xsl:if>
            <xsl:if test="($NP-COUNT) and not($NP-COUNT ='1')">
              <xsl:text> are </xsl:text>
            </xsl:if>
          </xsl:variable>
          <xsl:variable name="REFS">
            <xsl:if test="($NP-COUNT) and ($NP-COUNT ='1')">
              <xsl:text> reference </xsl:text>
            </xsl:if>
            <xsl:if test="($NP-COUNT) and not($NP-COUNT ='1')">
              <xsl:text> references </xsl:text>
            </xsl:if>
          </xsl:variable>
          <xsl:variable name="SUMMARYMESSAGE">
         	 There <xsl:value-of select="$AREIS" /> <xsl:value-of select="$NP-COUNT"/> other <xsl:value-of select="$REFS" /> for the following patent:
          </xsl:variable>
          <xsl:value-of select="patentrefsummary:setPatrefsummsg($patentrefsummary, $SUMMARYMESSAGE)" />  
		</xsl:when>
     <xsl:otherwise>
	    	
	            <xsl:variable name="REF-CNT">
			      <xsl:value-of select="EI-DOCUMENT/RCT" />
			    </xsl:variable>
			
			    <xsl:variable name="AREIS">
			       <xsl:if test="($REF-CNT) and ($REF-CNT ='1')">
			  
			         <xsl:text> is </xsl:text>
			       </xsl:if>
			       <xsl:if test="($REF-CNT) and not($REF-CNT ='1')">
			
			         <xsl:text> are </xsl:text>
			       </xsl:if>
			     </xsl:variable>
			     <xsl:variable name="REFS">
			       <xsl:if test="($REF-CNT) and ($REF-CNT ='1')">
			         <xsl:text> reference </xsl:text>
			       </xsl:if>
			       <xsl:if test="($REF-CNT) and not($REF-CNT ='1')">
			         <xsl:text> references </xsl:text>
			       </xsl:if>
			     </xsl:variable>
			     <xsl:variable name="SUMMARYMESSAGE">
				     <xsl:choose>
						 <xsl:when test="$DATABASE='2'">	   
						 	There <xsl:value-of select="$AREIS" /> <xsl:value-of select="$REF-CNT"/> inspec <xsl:value-of select="$REFS" /> for the following record:
						 </xsl:when>
						 <xsl:otherwise>
						    There <xsl:value-of select="$AREIS" /> <xsl:value-of select="$REF-CNT"/> patent <xsl:value-of select="$REFS" /> for the following patent:
				        </xsl:otherwise>
				     </xsl:choose>    
			     </xsl:variable>
			     <xsl:value-of select="patentrefsummary:setPatrefsummsg($patentrefsummary, $SUMMARYMESSAGE)" />  
	    </xsl:otherwise>
     </xsl:choose>
     	    
     <xsl:value-of select="actionbean:setPatentrefsummary($actionbean, $patentrefsummary)"/>
   </xsl:template> 
   
   
   
</xsl:stylesheet>
