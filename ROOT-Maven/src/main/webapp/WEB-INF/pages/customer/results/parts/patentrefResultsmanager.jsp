<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tlds/functions.tld" prefix="f"%>

			<div id="resultsmanager" class="patentrefs">
                <div id="pageselect" class="patentrefs">
                    <input type="checkbox" name="page" id="pageckbx" title="Select current page" action="page"> <label for="page">Page</label>
                </div>

				<div id="toolbar" class="col patentrefs">
					<ul id="outputtools" class="horizlist" style="*width:80%">
						<li class="email"><a id="emaillink" title="Email selections" style="padding-left:0" aria-labelledby="viewlink" href="">Email</a> | </li>
						<li class="print"><a id="printlink" title="Print selections" aria-labelledby="viewlink" href="">Print</a> | </li>
						<li class="download" id="downloadli"><a id="oneclickDL" title="Download selections" style="display:none"></a><a id="downloadlink" aria-labelledby="viewlink" title="Download selections" href="">Download</a></li>

					</ul>
				</div>

                <div class="clear"></div>
			</div>
