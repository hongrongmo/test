<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

    <html:link forward="menu" styleClass="LgBlueLink" >
      Return to Main Menu
    </html:link>

    <logic:present scope="request" name="aCustomer">
       <P/>
        <html:link forward="editCustomer" paramName="aCustomer" paramProperty="customerID" paramId="customerid" styleClass="LgBlueLink" >
          Return to <bean:message key="displayname.customer"/>: <bean:write name="aCustomer" property="name" filter="true"/>
        </html:link>
    </logic:present>

    <logic:present scope="request" name="aContract">
      <P/>
      <html:link forward="editContract" paramName="aContract" paramProperty="contractID" paramId="contractid" styleClass="LgBlueLink" >
        Return to <bean:message key="displayname.contract"/>: <bean:write name="aContract" property="contractID" filter="true"/>
      </html:link>
    </logic:present>

    <logic:present scope="request" name="aContact">
      <P/>
      <html:link forward="editContact" paramName="aContact" paramProperty="contactID" paramId="contactid" styleClass="LgBlueLink" >
        Return to <bean:message key="displayname.conact"/>: <bean:write name="aContact" property="displayName" filter="true"/>
      </html:link>
    </logic:present>

    <logic:present scope="request" name="anItem">
      <P/>
      <html:link forward="editItem" name="anItem" property="linkParams" styleClass="LgBlueLink" >
        Return to <bean:message key="displayname.item"/>: <bean:write name="anItem" property="product.name" filter="true"/>
      </html:link>
    </logic:present>

    <P/>
    <html:link forward="search" styleClass="LgBlueLink" >
      Search
    </html:link>
