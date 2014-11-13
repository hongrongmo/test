<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
	prefix="stripes"%>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp"
	pageTitle="Linda Hall Library Document Request">

	<stripes:layout-component name="header">
		<jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
	</stripes:layout-component>

	<stripes:layout-component name="contents">

    <div class="marginL10">
		<ul class="errors" id="jserrors">
		<stripes:errors>
		     <stripes:errors-header></stripes:errors-header>
		     <li><stripes:individual-error/></li>
		     <stripes:errors-footer></stripes:errors-footer>
		</stripes:errors>
		</ul>

        <h2>Linda Hall Library Document Request Sent</h2>
        <br/>
        <p CLASS="MedBlackText">
            Your request has been sent. You will be contacted by a Linda Hall Library representative for confirmation of your order and for billing arrangements. 
            To contact Linda Hall Library regarding your order, please call 1 (800) 662-1545 or email 
		        <a CLASS="LgBlueLink" href="mailto:ei@lindahall.org">ei@lindahall.org</a>
        </p>

        <br/>
        <input type="button" name="close" value="Close" onclick="javascript:window.close();"/>

    </div>

	</stripes:layout-component>
	<stripes:layout-component name="modal_dialog_msg"/>
	<stripes:layout-component name="modal_dialog"/>

	<stripes:layout-component name="footer">

		<div class="hr" style="margin: 20px 0 7px 0; color: #d7d7d7; background-color: #d7d7d7; height: 2px;">
			<hr />
		</div>

		<jsp:include page="/WEB-INF/pages/include/copyright.jsp" />

	</stripes:layout-component>
</stripes:layout-render>
