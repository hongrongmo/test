<%@ page language="java" session="false" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/pages/include/taglibs.jsp" %>

<%-- HEADER --%>
<div id="header" >
	<div id="logoEV" aria-label="Engineering Village" role="banner">
		<a href="/home.url" title="Engineering Village - The information discovery platform of choice for the engineering community" ><img alt="Engineering Village - The information discovery platform of choice for the engineering community" src="/static/images/EV-logo.gif"/></a>
		<c:if test="${not empty actionBean.maintenanceMsg}">
    		<div style="position:absolute; left: 320px; top: 28px; font-weight: bold; font-size; 12px; width:35%">${actionBean.maintenanceMsg }</div>
		</c:if>
	</div>
	

	<c:choose>
		<c:when	test="${not empty actionBean.context.userSession.carsMetaData.headerContent && actionBean.showLoginBox}">
        ${actionBean.context.userSession.carsMetaData.headerContent}
        <script type="text/javascript">
        $("#loginBox").hide();
        $("#login ul").prepend('<li style="background:none;"><a target="new"  href="https://docs.google.com/forms/d/1Fw5dUHQosH7-uHkQ7nnJQ6BzvxpxpJYyx-peUXfpy3E/viewform" title="Give feedback on Engineering Village"><img style="height:22px;"  src="/static/images/feedback.png"/></a></li>');
        </script>
				
        </c:when>
		<c:otherwise>
		</c:otherwise>
	</c:choose>

<div class="clear"></div>
<div class="navigation txtLarger clearfix" aria-label="Engineering Village main navigation" role="navigation" style="z-index:300;">

<c:set var="startpage">${actionBean.context.userSession.user.startPage}</c:set>
<c:if test="${empty searchlink}">
<c:choose>
    <c:when test="${'quickSearch' eq startpage}"><c:set var="searchlink">/search/quick.url<c:if test="${actionBean.database != null && 'null' ne actionBean.database}">?database=${actionBean.database}</c:if></c:set></c:when>
    <c:when test="${'expertSearch' eq startpage}"><c:set var="searchlink">/search/expert.url<c:if test="${actionBean.database != null && 'null' ne actionBean.database}">?database=${actionBean.database}</c:if></c:set></c:when>
    <c:when test="${'ebookSearch' eq startpage}"><c:set var="searchlink">/search/ebook.url<c:if test="${actionBean.database != null && 'null' ne actionBean.database}">?database=${actionBean.database}</c:if></c:set></c:when>
    <c:when test="${'thesHome' eq startpage}"><c:set var="searchlink">/search/thesHome.url#init</c:set></c:when>
    <c:otherwise><c:set var="searchlink">/search/quick.url?database=${actionBean.database}</c:set></c:otherwise>
</c:choose>
</c:if>

<c:set var="userprefs" value="${actionBean.context.userSession.user.userPreferences}"/>

<ul title="top level navigation" class="nav main" style="z-index:300;">
	<li><a title="Search Engineering Village" href="${searchlink}" class="${actionBean.roomSearch ? 'selected' : ''}" id="searchnavlink">Search</a></li>
	<li><a title="Selected Records - View your selected records"	href="/selected/citation.url?CID=citationSelectedSet&DATABASEID=${actionBean.database}&searchtype=TagSearch" class="${actionBean.roomSelectedRecords ? 'selected' : ''}">Selected records</a></li>

	<c:choose>
	<c:when test="${actionBean.context.userSession.user.individuallyAuthenticated}">
		<li<c:if test="${not userprefs.tag and not (userprefs.bulletin and userprefs.bulletinEnt)}"> class="last"</c:if> style="padding-top:0px;display:none;" id="settingDropDown" >
			<ul  id="settingMenu" style="background:transparent;font-size:13px;">
				<li style="z-index:300;">
					<a href="" id="menu-2" style="font-size:14px;height: 20px;padding-top: 2px;" onclick="return false;" title="Set your personal settings">Settings</a>
					<ul style="max-width:125px;z-index:300;">
						<li style="z-index:300;"><a style="z-index:305;" href="" class="prefsOverlay" id="prefsOverlay" onclick="return false;" title="Set your preferences for default settings">My Preferences</a></li>
						<li style="z-index:300;"><a style="z-index:305;" href="/personal/savesearch/display.url" title="Manage your saved searches and email alerts" id="saveSearchSetting">Alerts &amp; Searches</a></li>
						<li style="z-index:300;"><a style="z-index:305;" href="/personal/folders/display.url?CID=viewPersonalFolders" id="folderSetting" title="View, rename or delete your folders">Folders</a></li>
						<li style="z-index:300;"><a style="z-index:305;" title="Change your personal information and password" href="/customer/settings.url?database=${actionBean.database}" id="personalDetSetting">Personal Details</a></li>
					</ul>
				</li>
			</ul>
		</li>
	</c:when>
	<c:otherwise>
		<li<c:if test="${not userprefs.tag and not (userprefs.bulletin and userprefs.bulletinEnt)}"> class="last"</c:if>><a title="Set your personal settings" href="/customer/settings.url?database=${actionBean.database}" class="${actionBean.roomMySettings ? 'selected' : ''}">Settings</a></li>
	</c:otherwise>
	</c:choose>
	<c:if test="${userprefs.tag}"><li<c:if test="${not (userprefs.bulletin and userprefs.bulletinEnt)}"> class="last"</c:if>><a title="Tags & Groups - View/Edit your tags and groups" href="/tagsgroups/display.url?searchtype=TagSearch" class="${actionBean.roomTagsGroups ? 'selected' : ''}">Tags &amp; Groups</a></li></c:if>
	<c:if test="${userprefs.bulletin and userprefs.bulletinEnt}"><li class="last"><a title="Bulletins - View EnCompass bulletins" href="/bulletins/display.url" class="${actionBean.roomBulletins ? 'selected' : ''}">Bulletins</a></li></c:if>
</ul>

<ul class="nav misc" id="helpMenuUL" style="padding-top:0px;display:none;">
	<li class="${userprefs.reference ? '':'nodivider'}" style="padding:0px;">
		<ul id="helpMenu" class="nav">
			  <li>
			    <a href="" id="menu-1" style="font-size:14px;height: 20px;padding-top: 2px;" onclick="return false;">Support</a>
			    <ul>
			      <li>
					<c:choose>
						<c:when test="${not empty actionBean.isActionBeanInstance  and  actionBean.searchtype == 'Book' and actionBean.CID ne 'bookSummary' and actionBean.CID ne 'pageDetailedFormat'}">
							<a title="Help - Find an answer to your question"	id="menu-2" href="${actionBean.helpUrl}#Ebook_search_results.htm" class="helpurl">Help</a>
						</c:when>
						<c:when test="${actionBean.showpatentshelp}">
								<a title="Help - Find an answer to your question"	id="menu-2" href="${actionBean.helpUrl}#Patent_search_results_work_with.htm" class="helpurl">Help</a>
						</c:when>
						<c:otherwise>
							<a title="Help - Find an answer to your question" id="menu-2" href="${actionBean.helpUrl}#${actionBean.helpcontext}" class="helpurl">Help</a>
						</c:otherwise>
					 </c:choose>
			      </li>
			      <li><a title="Contact Us" href="${contactuslink}" id="menu-3" target="new">Contact</a></li>
			      <li>
			      <c:choose>
			      		<c:when test="${actionBean.context.userSession.user.userPreferences.modalDialog}">
			      			<a title="What's New in Engineering Village" href="" id="menu-4" class="whatsNewLink" onclick="return false;">What's New</a>
			      		</c:when>
			      		<c:otherwise>
			      			<a title="What's New in Engineering Village" href="http://www.ei.org/releases" id="menu-4" class="whatsNewLink2" target="new">What's New</a>
			      		</c:otherwise>
			      </c:choose>

			      </li>
			    </ul>
	  		  </li>
		</ul>
	</li>
	<c:if test="${userprefs.reference}">
		<li class="nodivider"><a title="Ask an expert - Get help from an Engineer, Product Specialist, or Librarian" href="/askanexpert/display.url">Ask an expert</a></li>

	</c:if>
	<c:choose>
	<c:when test="${(not userprefs.clientCustomLogo) || (empty actionBean.customlogo)}">
	<li class="nodivider" id='custom-logo-li' style='display:none'>
		<div id="custom-logo-top" align="center">
		</div>
	</li>
	</c:when>
	<c:otherwise>
	<li class="nodivider" id='custom-logo-li'>
		<div id="custom-logo-top" align="center">
		<a href="${actionBean.customlogo.customerurl}" title="${actionBean.customlogo.customerurl}"><img id="custom-logo-img" src="${actionBean.customlogo.imgsrc}" border="0"/></a>
		</div>
	</li>
	</c:otherwise>
	</c:choose>
</ul>

</div>
</div>
<c:if test="${not empty actionBean.IE7Msg}">
  	<div id="ie7msg" style="text-align:left;display:none"><img src="/static/images/red_warning.gif" style="padding-right:5px;width:20px;margin-top:-3px; float:left"/><span id="ie7messageholder">${actionBean.IE7Msg}</span></div>
</c:if>
<div id="prefsSaved" style="display:none;text-align:left;"><img src="/static/images/ev_checkmark.png" style="padding-right:5px;width:20px;"/>Preferences Saved!</div>
<c:choose>
	<c:when test="${actionBean.context.userSession.user.individuallyAuthenticated}">
	<div id="dlprefsSaved" style="display:none;text-align:left;"><img src="/static/images/ev_checkmark.png" style="padding-right:5px;width:20px;"/>Your download settings for this session have been saved. To keep these settings, change your preference in Settings.</div>
	<div id="dlprefsandsettingsSaved" style="display:none;text-align:left;"><img src="/static/images/ev_checkmark.png" style="padding-right:5px;width:20px;"/>Your download settings have been saved to My Preferences.</div>
	<div id="dlprefsandsettingsnotSaved" style="display:none;text-align:left;"><img src="/static/images/No_results_found.png" style="padding-right:5px;width:20px;"/>Your download settings for this session have not been saved. Please refresh the page and try again.</div>
	</c:when>
	<c:otherwise>
	<div id="dlprefsSaved" style="display:none;text-align:left;"><img src="/static/images/ev_checkmark.png" style="padding-right:5px;width:20px;"/>Your download settings for this session have been saved. To keep these settings, login or register and save your preferences in Settings.</div>
	</c:otherwise>
</c:choose>
<input type="hidden" id="authStatus" value="${actionBean.context.userSession.user.individuallyAuthenticated}"/>

<div id="prefsNotSaved" style="display:none;text-align:left;"><img src="/static/images/No_results_found.png" style="padding-right:5px;width:20px;"/>Preferences Could Not Be Saved!</div>

  <script>
  var savedDLPrefs;
  <c:if test="${actionBean.context.userSession.user.individuallyAuthenticated}">
  	$.cookie('ev_oneclickdl',null,{path:'/'});
  	savedDLPrefs = {
  			location:'${actionBean.context.userSession.user.userPrefs.dlLocation}',
  			format:'${actionBean.context.userSession.user.userPrefs.dlFormat}',
  			displaytype:'${actionBean.context.userSession.user.userPrefs.dlOutput}',
  			filenameprefix:'${actionBean.context.userSession.user.userPrefs.dlFileNamePrefix}',
  			baseaddress:'${actionBean.baseaddress}'
  	};
  </c:if>
  <c:if test="${actionBean.context.userSession.user.individuallyAuthenticated eq 'false'}">
   	$.cookie('ev_dldpref',null,{path:'/'});
  </c:if>

  $(function() {	  if($("#settingMenu").length > 0){
	  $("#settingMenu").menu({position:{my:'right+25 top+20'}, icons: { submenu: "ui-icon-triangle-1-s" }});
		$("#settingDropDown").show();
		//showTooltip(".settingMenu","We have Added New Settings!", "top-left", 4500, true);
	  }
    $( "#helpMenu" ).menu({position:{my:'right top+23'}, icons: { submenu: "ui-icon-triangle-1-s" }});
    $("#helpMenuUL").show();



    $(".whatsNewLink").click(function(){
    	GALIBRARY.createWebEventWithLabel('Dialog Open', 'What\'s New', 'Support Dropdown');

    	TINY.box.show({html:document.getElementById("modalmsg"),ariaLabeledBy:"mmPopupTitle",clickmaskclose:false,width:900,height:450,close:true,opacity:20,topsplit:3,closejs:function(){closeX();},openjs:function(){$("#hidePopup").focus();$(document).keydown(setTabListener);}});
    	$("#menu-1").click(function(){return false;});

    });

   $(".prefsOverlay").click(function(){
	   	GALIBRARY.createWebEventWithLabel('Dialog Open', 'Edit Preferences', 'Settings Dropdown');
		TINY.box.show({url:'/customer/userprefs.url',ariaLabeledBy:"myPrefTitle",clickmaskclose:false,width:400,height:520,close:true,opacity:20,topsplit:3,closejs:function(){$(document).unbind("keydown", setTabListenerPrefs);},openjs:function(){$("#hidePopup").focus();$(document).keydown(setTabListenerPrefs);}});

	});

  });
	function setTabListenerPrefs(e){

		if(e.keyCode == 9 && document.activeElement.id == 'hidePopup'){
			$($('#settingsPopup a, #settingsPopup input[type!="hidden"]')[0]).focus();
			e.preventDefault();
		}
	}
  	function isValidInput(fileNamePrefix) {
	  var re = /^\w+$/;
	  if (!re.test(fileNamePrefix)) {
	      return false;
	  }
	  return true;
	}

  	function handlevalidationerror(msg){
  		$("#valerrormsg").html(msg);
		$("#valerrormsgcontainer").css("display","block");
  	}

	function submitSavePrefsForm(){
		$(".saved").hide();
		var url = "/customer/userprefs.url?save=true&";
		var params = "";
		var hlight;
		var back_highlight = false;
		$("#userPreferencesForm input:checked").each(function (){
				if($(this).attr("name") != 'highlightBackground'){
					if(params != ""){params += "&";}
					//skip the highlight portion. We will do this later.
					params += $(this).attr("name") + "=" + $(this).val();
				}
		});
		if(highlightV1){
			hlight = $("#hlight_color").spectrum("get").toString();
			back_highlight = $("#hlight_bg_chkbx").prop("checked");
			if(back_highlight){
				hlight = JSON.parse($.cookie('ev_highlight')).color;
			}

			params += "&highlight=" + escape(hlight);
			params += "&highlightBackground=" + back_highlight;
		}


		var fileNamePrefix = $.trim($('#dlFileNamePrefix').val());
		if(fileNamePrefix.length < 3){
			handlevalidationerror("Prefix cannot be empty and should have minimum of 3 characters");
			return false;
		}
		if(fileNamePrefix.length > 50){
			handlevalidationerror("Prefix cannot have more than 50 characters");
			return false;
		}

		if(!isValidInput(fileNamePrefix)){
			handlevalidationerror("Prefix can have only letters, numbers and underscore character");
			return false;
		}
		params += "&dlFileNamePrefix=" + fileNamePrefix;


		url += params;
		GALIBRARY.createWebEventWithLabel('Preferences', 'Preferences Saved', params);

		$.ajax({
			url:url,
			cache: false
		}).success(function(data){
			TINY.box.hide();
			//change any highlight color on the fly
			if(highlightV1){

				if(back_highlight){
					$(".hit").addClass("bghit");
					$(".bghit").removeClass("hit");
					$(".bghit").css("color", "#000000");
					$.cookie('ev_highlight', '{"bg_highlight":'+true+', "color":"'+hlight+'"}',{path:'/'});
					if($("#hlight_color_abs").length > 0){
						$("#hlight_color_abs").spectrum("set", "#000000");
						$("#hlight_color_abs").spectrum("disable");
						$("#hlight_color_abs_lbl").css("color","gray");
					}
				}else{
					$(".bghit").addClass("hit");
					$(".hit").removeClass("bghit");
					$(".hit").css("color", hlight);
					$("a span.hit").css("color", "inherit");
					$.cookie('ev_highlight', '{"bg_highlight":'+false+', "color":"'+hlight+'"}',{path:'/'});

					if($("#hlight_color_abs").length > 0){
						$("#hlight_color_abs").spectrum("set", hlight);
						$("#hlight_color_abs").spectrum("enable");
						$("#hlight_color_abs_lbl").css("color","black");
					}
				}

				if($("#ckbackhighlight")){
					$("#ckbackhighlight").prop("checked",back_highlight);
				}
			}

			if($('#downloadlink').length > 0){
				changeOneClick($("input[name='dlLocation']:checked").val());
				dlOptions = {
						location:$("input[name='dlLocation']:checked").val(),
						format:$("input[name='dlFormat']:checked").val(),
						displaytype:$("input[name='dlOutput']:checked").val(),
						filenameprefix:fileNamePrefix,
						baseaddress:dlOptions.baseaddress
				};
				$.cookie('ev_dldpref', '{"location":"'+dlOptions.location+'","format":"'+dlOptions.format+'","displaytype":"'+dlOptions.displaytype+'","baseaddress":"'+dlOptions.baseaddress+'","filenameprefix":"'+dlOptions.filenameprefix+'"}',{path:'/'});
			}

			$("#prefsNotSaved,#dlprefsSaved,#dlprefsandsettingsSaved,#dlprefsandsettingsnotSaved").hide();
			$("#prefsSaved").fadeIn("slow");
		}).error(function(data){
			TINY.box.hide();
			$("#prefsSaved,#dlprefsSaved,#dlprefsandsettingsSaved,#dlprefsandsettingsnotSaved").hide();
			$("#prefsNotSaved").fadeIn("slow");
		});

		return false;
	}


  </script>
