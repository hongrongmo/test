<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Blog This">

	<stripes:layout-component name="header">
		<jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
	</stripes:layout-component>

	<stripes:layout-component name="contents">

		<div class="paddingL15">
			<br />

			<p>Use Blog This to create a link and share the abstract of this
				record on your Blog or website.</p>
			<br />

			<p>Copy and paste the text below into your blog or website.</p>
			<br />

			<div>
				<textarea id="blogta" name="link" cols="60" rows="6"><a href='${server}<stripes:url  prependContext="false"
                        value="/blog/document.url?mid=${actionBean.mid}&database=${actionBean.database}"/>'>${title}</a>
                    <table border="0" cellspacing="0" cellpadding="0" width="99%">
                    <tr><td valign="top"><img alt="Engineering Village" src="${server}/static/images/newev.gif" border="0"/></td></tr>
                    </table></textarea>
			</div>
			<div>
				<input type="button" id="selectcontent" value="Select Content" onclick="$('#blogta').focus().select(); return false;"></input>
			</div>
		</div>
	</stripes:layout-component>

</stripes:layout-render>