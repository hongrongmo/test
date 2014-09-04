<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>

<html:html>
<head>
  <title><bean:write name="optionsForm" scope="request" property="action" filter="true"/> </title>
  <script language="Javascript1.1" src='<html:rewrite page="/js/stylesheet.jsp"/>'></script>
  <link rel="stylesheet" href='<html:rewrite page="/css/ui.all.css"/>' type="text/css">
  <script type="text/javascript" src='<html:rewrite page="/js/jquery-1.3.1.min.js"/>'></script>
  <script type="text/javascript" src='<html:rewrite page="/js/ui.core.js"/>'></script>
  <script type="text/javascript" src='<html:rewrite page="/js/ui.tabs.js"/>'></script>


  <script>
  $(document).ready(function(){
    $("#example").tabs();
  });
  </script>

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

        <bean:define toScope="request" id="Border" value="#BCBD78"/>
        <bean:define toScope="request" id="Inner" value="#EFEFD6"/>

        <table border="0" cellspacing="0" cellpadding="0" width="600">
          <tr>
            <td valign="top" colspan="3" ><html:img height="20" border="0" pageKey="images.spacer"/></td>
          </tr>
          <tr>
            <td valign="top" colspan="3">
              <span class="ui-widget"><bean:write name="anItem" property="product.name" filter="true"/>&nbsp;Product Options</span>
            </td>
          </tr>
          <tr><td valign="top" colspan="3" ><html:img height="10" border="0" pageKey="images.spacer"/></td></tr>
          <tr><td valign="top" colspan="3" bgcolor='<bean:write name="Border"/>'><html:img height="2" border="0" pageKey="images.spacer"/></td></tr>
          <tr>
            <td valign="top" width="2" bgcolor='<bean:write name="Border"/>'>
              <html:img width="2" border="0" pageKey="images.spacer"/>
            </td>
            <td valign="top">

                <div style='background-color:<bean:write name="Inner"/>;' id="example">

                  <ul>
                      <li><a href="#fragment-1"><span><bean:message key="prompt.tabDatabases"/></span></a></li>
                      <li><a href="#fragment-Options"><span><bean:message key="prompt.tabOptions"/></span></a></li>
                      <li><a href="#fragment-UI"><span><bean:message key="prompt.tabInterface"/></span></a></li>
                      <li><a href="#fragment-Referex"><span><bean:message key="prompt.tabReferex"/></span></a></li>
                      <li><a href="#fragment-Bulletins"><span><bean:message key="prompt.tabBulletins"/></span></a></li>
                  </ul>

              <html:form action="/saveEv2" onsubmit="return validateEv2Form(this);">
                <html:hidden name="optionsForm" property="action"/>
                <html:hidden name="optionsForm" property="options.contractID"/>
                <html:hidden name="optionsForm" property="options.customerID"/>
                <html:hidden name="optionsForm" property="options.product"/>

                  <div id="fragment-1">
                    <table border="0" cellspacing="0" cellpadding="0" width="600" bgcolor='<bean:write name="Inner"/>'>
                    <tr>
                      <td valign="top" align="right" colspan="2" width="33%">
                        <span class="MedBlackText"><bean:message key="prompt.integratedDatabases"/>:</span>&nbsp;
                      </td>
                      <td valign="top" colspan="2" >
                        <span class="MedBlackText">
                          <logic:iterate indexId="boxId" id="labelValue" name="optionsForm" property="allOptions">
                            <html:multibox onclick="javascript:setDefaultDatabase(this);" name="optionsForm" property="options.selectedOptions" styleId='<%="box"+boxId%>' >
                              <bean:write name="labelValue" property="value"/>
                            </html:multibox>
                            <label for="box<bean:write name="boxId"/>"><bean:write name="labelValue" property="label"/></label><br/>
                          </logic:iterate>
                        </span>
                      </td>
                    </tr>
                    <tr>
                      <td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td>
                    </tr>
                    <tr>
                      <td valign="top" align="right" colspan="2" width="33%">
                        <span class="MedBlackText"><bean:message key="prompt.moreDatabases"/>:</span>&nbsp;
                      </td>
                      <td valign="top" colspan="2" >
                        <span class="MedBlackText">
                          <logic:iterate indexId="boxId" id="labelValue" name="optionsForm" property="moreDBOptions">
                            <html:multibox onclick="javascript:setDefaultDatabase(this);" name="optionsForm" property="options.selectedOptions" styleId='<%="more"+boxId%>'>
                              <bean:write name="labelValue" property="value"/>
                            </html:multibox>
                            <label for="more<bean:write name="boxId"/>"><bean:write name="labelValue" property="label"/></label><br/>
                          </logic:iterate>
                        </span>
                      </td>
                    </tr>
                    </table>
                  </div>

                  <%--  UI Options --%>
                  <div id="fragment-UI">
                    <table border="0" cellspacing="0" cellpadding="0" width="600" bgcolor='<bean:write name="Inner"/>'>
                    <tr>
                      <td valign="top" align="right" colspan="2" width="33%">
                        <html:img width="2" border="0" pageKey="images.spacer"/>
                      </td>
                      <td valign="top" colspan="2" >
                        <span class="MedBlackText">
                          <logic:iterate indexId="boxId" id="labelValue" name="optionsForm" property="userInterfaceOptions">
                            <html:multibox name="optionsForm" property="options.selectedOptions" styleId='<%="uibox"+boxId%>'>
                              <bean:write name="labelValue" property="value"/>
                            </html:multibox>
                            <label for="uibox<bean:write name="boxId"/>"><bean:write name="labelValue" property="label"/></label><br/>
                          </logic:iterate>
                        </span>
                      </td>
                    </tr>
                    <tr>
                      <td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td>
                    </tr>
                    <tr>
                      <td valign="top" align="right" colspan="2" width="33%">
                        <span class="MedBlackText"><bean:message key="prompt.refservicesLink"/>:</span>&nbsp;
                      </td>
                      <td valign="top" colspan="2" >
                        <span class="MedBlackText">
                          <html:text name="optionsForm" property="options.referenceServicesLink" size="56" maxlength="256" />
                        </span>
                      </td>
                    </tr>
                    </table>
                  </div>

                  <%--  DB Options --%>
                  <div id="fragment-Options">
                    <table border="0" cellspacing="0" cellpadding="0" width="600" bgcolor='<bean:write name="Inner"/>'>
                      <tr>
                        <td valign="top" align="right" colspan="2" width="33%">
                          <span class="MedBlackText"><bean:message key="prompt.defaultDatabase"/>:</span>&nbsp;
                        </td>
                        <td valign="top" colspan="2" >
                          <span class="MedBlackText">
                              <logic:iterate indexId="boxId" id="alabelValue" name="optionsForm" property="allDatabaseOptions">
                                <html:multibox name="optionsForm" property="options.selectedDefaultDatabases"  styleId='<%="default"+boxId%>'>
                                  <bean:write name="alabelValue" property="value"/>
                                </html:multibox>
                                <label for="default<bean:write name="boxId"/>"><bean:write name="alabelValue" property="label"/></label><br/>
                              </logic:iterate>
                          </span>
                        </td>
                      </tr>
                    </table>

                    <table border="0" cellspacing="0" cellpadding="0" width="600" bgcolor='<bean:write name="Inner"/>'>
                      <logic:iterate name="optionsForm" property="options.databaseArray" id="aDatabase" indexId="ctr">
                        <tr>
                          <td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td>
                        </tr>
                        <tr>
                          <td valign="top" align="right" colspan="2" width="50%">
                            <span class="MedBlackText"><bean:write name="aDatabase" property="name" filter="true"/> <bean:message key="prompt.selectedStartYear"/></span>&nbsp;
                          </td>
                          <td valign="top" colspan="2" >
                          <span class="MedBlackText">
                            <bean:define toScope="request" id="yearoptions" name="aDatabase" property="allYearOptions"  type="java.util.Collection"/>

                            <html:select name="optionsForm" property='<%= "options.database[" + ctr + "].year" %>' size="1" >
                              <html:options collection="yearoptions" property="value" labelProperty="label"/>
                            </html:select>
                          </span>
                          </td>
                        </tr>
                      </logic:iterate>

                      <tr>
                        <td valign="top" align="right" colspan="2" width="50%">
                          <span class="MedBlackText"><bean:message key="prompt.sortBy"/>:</span>&nbsp;
                        </td>
                        <td valign="top" colspan="2" >
                        <span class="MedBlackText">
                          <bean:define toScope="request" id="sortBys" name="optionsForm" property="allSortByOptions"  type="java.util.Collection"/>
                          <html:select name="optionsForm" property="options.sortBy" size="1" >
                            <html:options collection="sortBys" property="value" labelProperty="label"/>
                          </html:select>
                        </span>
                        </td>
                      </tr>
                      <tr>
                        <td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td>
                      </tr>
                      <tr>
                        <td valign="top" align="right" colspan="2" width="50%">
                          <span class="MedBlackText"><bean:message key="prompt.autostem"/>:</span>&nbsp;
                        </td>
                        <td valign="top" colspan="2" >
                        <span class="MedBlackText">
                          <bean:define toScope="request" id="autostemOpts" name="optionsForm" property="allAutostemOptions"  type="java.util.Collection"/>
                          <html:select name="optionsForm" property="options.autostem" size="1" >
                            <html:options collection="autostemOpts" property="value" labelProperty="label"/>
                          </html:select>
                        </span>
                        </td>
                      </tr>
                      <tr>
                        <td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td>
                      </tr>
                      <tr>
                        <td valign="top" align="right" colspan="2" width="50%">
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
                    </table>
                  </div>


                  <%--  Referex --%>
                  <div id="fragment-Referex">
                    <table border="0" cellspacing="0" cellpadding="0" width="600" bgcolor='<bean:write name="Inner"/>'>
                    <tr>
                      <td valign="top" align="right" colspan="2" width="33%">
                        <html:img width="2" border="0" pageKey="images.spacer"/>
                      </td>
                      <td valign="top" colspan="2" >
                        <span class="MedBlackText">
                          <logic:iterate indexId="boxId" id="labelValue" name="optionsForm" property="allReferexOptions">
                            <html:multibox name="optionsForm" property="options.selectedOptions"  styleId='<%="referex"+boxId%>'>
                              <bean:write name="labelValue" property="value"/>
                            </html:multibox>
                            <label for="referex<bean:write name="boxId"/>"><bean:write name="labelValue" property="label"/></label><br/>
                          </logic:iterate>
                        </span>
                      </td>
                    </tr>
                    </table>
                  </div>


                  <div id="fragment-Bulletins">
                    <%--  LIT Bulletins --%>
                    <table border="0" cellspacing="0" cellpadding="0" width="600" bgcolor='<bean:write name="Inner"/>'>
                    <tr>
                      <td valign="top" align="right" colspan="2" width="33%">
                        <span class="MedBlackText">LIT <bean:message key="prompt.bulletins"/>:</span>&nbsp;
                      </td>
                      <td valign="top" colspan="2" >
                        <span class="MedBlackText">
                          <logic:iterate indexId="boxId" id="labelValue" name="optionsForm" property="allLitbulletins">
                          <html:multibox name="optionsForm" property="options.litbulletins" styleId='<%="lit"+boxId%>'>
                            <bean:write name="labelValue" property="value"/>
                          </html:multibox>
                          <label for="lit<bean:write name="boxId"/>"><bean:write name="labelValue" property="label"/></label><br/>
                          </logic:iterate>
                        </span>
                      </td>
                    </tr>
                    <tr>
                      <td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td>
                    </tr>
                    <%--  PAT Bulletins --%>
                    <tr>
                      <td valign="top" align="right" colspan="2" width="33%">
                        <span class="MedBlackText">PAT <bean:message key="prompt.bulletins"/>:</span>&nbsp;
                      </td>
                      <td valign="top" colspan="2" >
                        <span class="MedBlackText">
                          <logic:iterate indexId="boxId" id="labelValue" name="optionsForm" property="allPatbulletins">
                          <html:multibox name="optionsForm" property="options.patbulletins" styleId='<%="pat"+boxId%>'>
                            <bean:write name="labelValue" property="value"/>
                          </html:multibox>
                          <label for="pat<bean:write name="boxId"/>"><bean:write name="labelValue" property="label"/></label><br/>
                          </logic:iterate>
                        </span>
                      </td>
                    </tr>
                    </table>
                  </div>
                  <div align="center">
                    <html:image altKey="" pageKey="images.save_olive" border="0"/>
                    <html:image altKey="" pageKey="images.action.reset" border="0" onclick="javascript:reset(); return false;" />
                  </div>
                </html:form>
                </div>
            </td>
            <td valign="top" width="2" bgcolor='<bean:write name="Border"/>'><html:img width="2" border="0" pageKey="images.spacer"/></td>
          </tr>
          <tr>
            <td valign="top" colspan="3" bgcolor='<bean:write name="Border"/>'><html:img height="2" border="0" pageKey="images.spacer"/></td>
          </tr>
        </table>
        <p/>
      </td>
    </tr>
  </table>
</center>
<!-- End of body portion -->

<html:javascript formName="ev2Form" dynamicJavascript="true" staticJavascript="false"/>
<script language="Javascript1.1" src='<html:rewrite page="/staticJavascript.jsp"/>'></script>

<script language="Javascript1.1">
<!--
    var dbmasks = new Array();
<logic:iterate indexId="boxId" id="labelValue" name="optionsForm" property="allDatabaseMasks">
    dbmasks['<bean:write name="labelValue" property="label"/>'] = '<bean:write name="labelValue" property="value"/>';</logic:iterate>

function setDefaultDatabase() {
  var args=setDefaultDatabase.arguments;
  var thisCheckbox = args[0];
  // get default database checkbox collection
  var default_databases = document.optionsForm['options.selectedDefaultDatabases']
  // get mask from checked cartridge value
  var dbmask = dbmasks[thisCheckbox.value];

  // loop through default databases
  for (i = 0; i < default_databases.length; i++) {
    // find mask of checked database cartridge
    if(dbmask == default_databases[i].value) {
      // set value of database checkbox as a default
      // check off if database is no longer available to customer
      // add as default if database has been added to customer options
      default_databases[i].checked = thisCheckbox.checked;
      break;
    }
  }


}
// -->
</script>

</body>
</html:html>
