<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/app.tld" prefix="app" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>

<app:checkLogon/>

<bean:define toScope="request" id="Border" value="#BCBD78"/>
<bean:define toScope="request" id="Inner" value="#EFEFD6"/>

<html:html>
  <HEAD>
<title>Terriroty Reports</title>

      <script language="Javascript1.1" src='<html:rewrite page="/js/stylesheet.jsp"/>'></script>
  </HEAD>

<body topmargin="0" marginheight="0" marginwidth="0" bgcolor="#FFFFFF">

<jsp:useBean id="Territories" class="org.ei.struts.backoffice.territory.Territories" scope="application"/>

<!-- top logo area-->
<center>
<table border="0" width="99%" cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top" height="45" bgcolor="#CCCCCC">
			<html:img height="45" border="0" pageKey="images.spacer"/>
		</td>
	</tr>
</table>
</center>
<!-- End of top logo area -->

<!-- Start of body portion -->
<center>
<table border="0" width="99%" cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top" width="190" bgcolor="#CCCCCC">
			<!-- Start of table for the left hand navigation -->

      <template:insert template='/templates/nav.jsp'>
        <template:put name='links' content='/templates/_links.jsp'/>
      </template:insert>

			<!-- End of table for the left hand navigation -->
		</td>
		<td valign="top" width="10">
			<html:img width="10" border="0" pageKey="images.spacer"/>
		</td>
		<td valign="top">
			<!-- Start of table to put the form -->

      <table border="0" cellSpacing=0 cellPadding=0 width="100%" >
        <tr>
          <td valign="top" colspan=3 height=20><html:img height="20"  pageKey="images.spacer" border="0"/></td>
        </tr>
        <tr>
          <td valign="top" colspan=3><html:img height="4"  pageKey="images.spacer" border="0" /></td>
        </tr>
        <tr>
          <td valign="top" colSpan=3 height=4>
          <html:img height="4"  pageKey="images.spacer" border="0"/>
          <html:errors/>
          </td>
        </tr>
        <tr>
          <td valign="top" height="2" bgcolor='<bean:write name="Border"/>' colspan="3"><html:img height="2" border="0" pageKey="images.spacer"/></td>
        </tr>
        <tr>
          <td valign="top"width=2 bgcolor='<bean:write name="Border"/>'><html:img pageKey="images.spacer" width="2" border="0"/></td>
          <td vAlign=top>

            <!-- Start of table just for the form -->
            <table border="0" cellSpacing=0 cellPadding=0 width="100%" bgcolor='<bean:write name="Inner"/>'>
            	<html:form action="/territoryReport" method="post">
              <tr>
                <td valign="top" colSpan="5" >
                	<html:img height="4"  pageKey="images.spacer" border="0" />
                </td>
              </tr>
              <tr>
                <td valign="top" colSpan="5" >
                	<html:img height="4"  pageKey="images.spacer" border="0"/>
                </td>
              </tr>
              <tr>
                <td valign="top" colSpan="5" ><html:img height="4"  pageKey="images.spacer" border="0"/></td>
              </tr>
              <tr>
                <td valign="top"width="50"><html:img  pageKey="images.spacer" width="3" border="0"/></td>
                <td valign=center align="right" width="150"><span CLASS="MedBlackText">Region:</span></td>
                <td valign="top"width="50"><html:img pageKey="images.spacer" width="10" border="0"/></td>
                <td valign=top width="150">

                  <html:select property="territoryid" size="1" >
                    <bean:define id="allTerritories" name="Territories" property="territories" type="java.util.Collection"/>
                    <html:options collection="allTerritories" property="value" labelProperty="label"/>
                  </html:select>

                </td>
                <td valign="top"width="3"><html:img  pageKey="images.spacer" width="3" border="0"/></td>
              </tr>
              <tr>
                <td valign="top" colSpan="5" ><html:img height="4"  pageKey="images.spacer" border="0"/></td>
              </tr>
               <tr>
    	           <td valign="top" colSpan="5"  ><html:img height="4"  pageKey="images.spacer" border="0"/></td>
              </tr>
              <tr>
                <td valign="top" colspan=1>
                <html:img   pageKey="images.spacer" border="0"/></td>
                <td valign="top" colspan=3 align="left" >
                  <table>
                     <tr>
                			  <td width="150">&nbsp;</td>
                			  <td>
                				  <html:image altKey="alt.save" pageKey="images.action.search" border="0"/>
                				  <html:image altKey="alt.reset" pageKey="images.action.reset" border="0" onclick="javascript:reset(); return false;" />
                			  </td>
                     </tr>
                  </table>
                </td>
                <td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3"/></td>
              </tr>
              <tr>
                <td valign="top" colspan=2 align="left" >

                  <jsp:useBean id="customerTypes" class="org.ei.struts.backoffice.territory.CustomerTypes" scope="application"/>

                  <span CLASS="MedBlackText">
                  <logic:iterate id="labelValue" name="customerTypes" property="allCustomerTypes" type="org.ei.struts.backoffice.territory.Item">
                    <html:multibox name="territoryForm" property="selectedTypes">
                      <bean:write name="labelValue" property="value"/>
                    </html:multibox>
                    <bean:write name="labelValue" property="label"/><br/>
                  </logic:iterate>
                  </span>

                </td>
                <td valign="top" colspan=2 align="left" >

                  <jsp:useBean id="industries" class="org.ei.struts.backoffice.territory.Industries" scope="application"/>

                  <span CLASS="MedBlackText">
                  <logic:iterate id="labelValue" name="industries" property="allIndustries" type="org.ei.struts.backoffice.territory.Item">
                    <html:multibox name="territoryForm" property="selectedIndustries">
                      <bean:write name="labelValue" property="value"/>
                    </html:multibox>
                    <bean:write name="labelValue" property="label"/><br/>
                  </logic:iterate>
                  </span>

                </td>
              </tr>
              </html:form>
            </table>
            <!-- End of table just for the form -->
          </td>
          <td valign="top" width="2" bgColor="#bcbd78"><html:img  pageKey="images.spacer" width="2" border="0"/></td>
        </tr>
        <tr><td valign="top" height="2" bgcolor='<bean:write name="Border"/>' colspan="3"><html:img height="2" border="0" pageKey="images.spacer"/></td></tr>
      </table>
      <!-- End of table to put the form -->

    	<!-- End of table to put the form -->
		</td>
	</tr>
</table>
</center>
<!-- End of body portion -->

</body>
</html:html>
