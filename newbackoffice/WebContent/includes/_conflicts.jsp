<logic:present scope="request" name="conflicts">
	<ul>
	<logic:iterate name="conflicts" id="aConflict" type="org.ei.struts.backoffice.credentials.Credentials">
		<li>Access record <bean:write name="aConflict" property="displayString" filter="true"/> conflicts with access record for contract
		<html:link forward="editContract" paramName="aConflict" paramProperty="access.contractID" paramId="contractid" styleClass="LgBlueLink" >
			<bean:write name="aConflict" property="access.contractID" filter="true"/>
		</html:link>
		, Product
    <html:link forward="editItem" name="aConflict" property="access.item.linkParams" styleClass="LgBlueLink" >
      <bean:write name="aConflict" property="access.item.product.name" filter="true"/>
		</html:link>
		</li>
	</logic:iterate>
	</ul>
</logic:present>
