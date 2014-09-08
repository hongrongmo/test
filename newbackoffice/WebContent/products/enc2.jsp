<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>

<html:html>
<head>
  <title><bean:write name="optionsForm" scope="request" property="action" filter="true"/> </title>
  <script language="Javascript1.1" src='<html:rewrite page="/js/stylesheet.jsp"/>'></script>
</head>

<body topmargin="0" marginheight="0" marginwidth="0" bgcolor="#FFFFFF">

<jsp:useBean id="Constants" class="org.ei.struts.backoffice.Constants" scope="application"/>

<html:errors/>

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

		<bean:define toScope="request" id="anItem" name="optionsForm" property="options.contractItem" />
		<bean:define toScope="request" id="aContract" name="anItem" property="contract" />
		<bean:define toScope="request" id="aCustomer" name="aContract" property="customer" />

        <template:insert template='/templates/nav.jsp'>
          <template:put name='links' content='/templates/_links.jsp'/>
        </template:insert>

      </td>
      <td valign="top" width="10">
        <html:img width="10" border="0" pageKey="images.spacer"/>
      </td>
      <td valign="top">
        <!-- Start of table to put the form -->

        <bean:define toScope="request" id="Border" value="#BCBD78"/>
        <bean:define toScope="request" id="Inner" value="#EFEFD6"/>

        <table border="0" cellspacing="0" cellpadding="0" width="600">
          <tr>
            <td valign="top" colspan="3" ><html:img height="20" border="0" pageKey="images.spacer"/></td>
          </tr>
          <tr>
            <td valign="top" colspan="3">
                <H3><bean:write name="anItem" property="product.name" filter="true"/>&nbsp;Product Options</H3>
            </td>
          </tr>
          <tr><td valign="top" colspan="3" ><html:img height="10" border="0" pageKey="images.spacer"/></td></tr>
          <tr><td valign="top" colspan="3" bgcolor='<bean:write name="Border"/>'><html:img height="2" border="0" pageKey="images.spacer"/></td></tr>
          <tr><td valign="top" width="2" bgcolor='<bean:write name="Border"/>'><html:img width="2" border="0" pageKey="images.spacer"/></td>
            <td valign="top">
              <!-- Start of table just for the form -->
<html:form action="/saveEnc2" onsubmit="return validateEnc2Form(this);">
	<html:hidden name="optionsForm" property="action"/>
	<html:hidden name="optionsForm" property="options.contractID"/>
	<html:hidden name="optionsForm" property="options.customerID"/>
	<html:hidden name="optionsForm" property="options.product"/>

              <table border="0" cellspacing="0" cellpadding="0" width="600" bgcolor='<bean:write name="Inner"/>'>
                <tr>
                  <td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td>
                </tr>
                <tr>
                  <td valign="top" align="right" colspan="2" width="33%">
                    <span class="MedBlackText"><bean:message key="prompt.cartridges"/>:</span>&nbsp;
                  </td>
                  <td valign="top" colspan="2" >
                    <span class="MedBlackText">
                      <logic:iterate id="labelValue" name="optionsForm" property="allOptions">
                        <html:multibox name="optionsForm" property="options.selectedOptions">
                          <bean:write name="labelValue" property="value"/>
                        </html:multibox>
                        <bean:write name="labelValue" property="label"/><br/>
                      </logic:iterate>
                    </span>
                  </td>
                </tr>

<tr>
    <td valign="top" colspan="3" ><html:img height="10" border="0" pageKey="images.spacer"/></td>
</tr>

<%-- ENC2 LIT Bulletins --%>
<tr>
  <td valign="top" align="right" colspan="2" width="33%">
	<span class="MedBlackText">LIT <bean:message key="prompt.bulletins"/>:</span>&nbsp;
  </td>
  <td valign="top" colspan="2" >
	<span class="MedBlackText">
	  <logic:iterate id="labelValue" name="optionsForm" property="allLitbulletins">
		<html:multibox name="optionsForm" property="options.litbulletins">
		  <bean:write name="labelValue" property="value"/>
		</html:multibox>
		<bean:write name="labelValue" property="label"/><br/>
	  </logic:iterate>
	</span>
  </td>
</tr>

<tr>
    <td valign="top" colspan="3" ><html:img height="10" border="0" pageKey="images.spacer"/></td>
</tr>

<%-- ENC2 PAT Bulletins --%>
<tr>
  <td valign="top" align="right" colspan="2" width="33%">
	<span class="MedBlackText">PAT <bean:message key="prompt.bulletins"/>:</span>&nbsp;
  </td>
  <td valign="top" colspan="2" >
	<span class="MedBlackText">
	  <logic:iterate id="labelValue" name="optionsForm" property="allPatbulletins">
		<html:multibox name="optionsForm" property="options.patbulletins">
		  <bean:write name="labelValue" property="value"/>
		</html:multibox>
		<bean:write name="labelValue" property="label"/><br/>
	  </logic:iterate>
	</span>
  </td>
</tr>

<tr>
    <td valign="top" colspan="3" ><html:img height="10" border="0" pageKey="images.spacer"/></td>
</tr>

				<tr>
				  <td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td>
				</tr>
				<tr>
				  <td valign="top" align="right" colspan="2" width="33%">
					<span class="MedBlackText"><bean:message key="prompt.startPage"/>:</span>&nbsp;
				  </td>
				  <td valign="top" colspan="2" >
					<span class="MedBlackText">
						<bean:define toScope="request" id="startPages" name="optionsForm" property="allStartPages"  type="java.util.Collection"/>

						<html:select name="optionsForm" property="options.startCID" size="1" >
							<html:options collection="startPages" property="value" labelProperty="label"/>
						</html:select>
					</span>
				  </td>
				</tr>

<tr>
  <td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td>
</tr>
<tr>
  <td valign="top" align="right" colspan="2" width="33%">
	<span class="MedBlackText"><bean:message key="prompt.defaultDatabase"/>:</span>&nbsp;
  </td>
  <td valign="top" colspan="2" >

	<span class="MedBlackText">
		<bean:define toScope="request" id="defaultDatabases" name="optionsForm" property="allDatabaseOptions"  type="java.util.Collection"/>

		<html:select name="optionsForm" property="options.defaultDatabase" size="1" >
			<html:options collection="defaultDatabases" property="value" labelProperty="label"/>
		</html:select>
	</span>
  </td>
</tr>
<tr>
  <td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td>
</tr>

                <tr>
                  <td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td>
                </tr>
                <tr>
                  <td valign="top" colspan="3">
                    <html:img border="0" pageKey="images.spacer"/>
                  </td>
                  <td valign="top">
                    <html:image altKey="" pageKey="images.save_olive" border="0"/>
                    <html:image altKey="" pageKey="images.action.reset" border="0" onclick="javascript:reset(); return false;" />
                  </td>
                </tr>
              </table>
            <!-- End of table just for the form -->
            </td>
            <td valign="top" width="2" bgcolor='<bean:write name="Border"/>'><html:img width="2" border="0" pageKey="images.spacer"/></td>
          </tr>
          <tr>
            <td valign="top" colspan="3" bgcolor='<bean:write name="Border"/>'><html:img height="2" border="0" pageKey="images.spacer"/></td>
          </tr>
        </table>
        <!-- End of table to put the form -->
</html:form>
      </td>
    </tr>
  </table>
</center>
<!-- End of body portion -->

<html:javascript formName="enc2Form" dynamicJavascript="true" staticJavascript="false"/>
<script language="Javascript1.1" src='<html:rewrite page="/staticJavascript.jsp"/>'></script>

</body>
</html:html>
