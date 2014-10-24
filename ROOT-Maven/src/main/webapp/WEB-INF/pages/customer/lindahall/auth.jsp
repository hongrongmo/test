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

		<stripes:form name="lhlrequest" method="get" action="/lindahall/auth.url">
			<stripes:hidden name="docid" />
			<stripes:hidden name="database" />

				<h2>Linda Hall Library Document Request</h2>

				<p CLASS="MedBlackText">A password is required to access the
					Linda Hall Library document request form:</p>
				​
				<div class="contentMain" style="margin-right:10px !important;">
					<div class="contentShadow">
						<div class="padding10">
							<p CLASS="MedBlackText">
								Password:
								<stripes:password name="lhlpassword" size="24" />
							</p>
							<p CLASS="MedBlackText" style="padding-left: 61px">
								<stripes:submit name="authsubmit" value="Submit" />
							</p>
						</div>
					</div>
				</div>

				<br />

				<p CLASS="MedBlackText">
					If you don't have a password, you can place an order directly with
					<a CLASS="LgBlueLink"
						href="http://www.lindahall.org/services/document_delivery/partners/eicompendex.shtml">Linda
						Hall Library.</a>
				</p>
		</stripes:form>
    </div>

	</stripes:layout-component>

	<stripes:layout-component name="footer">

		<div class="hr" style="margin: 20px 0 7px 0; color: #d7d7d7; background-color: #d7d7d7; height: 2px;">
			<hr />
		</div>

		<jsp:include page="/WEB-INF/pages/include/copyright.jsp" />

	</stripes:layout-component>
</stripes:layout-render>
