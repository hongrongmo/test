//
// Add a "resize" function to jquery to resize images.  Used
// to resize any custom logos.
//
$.fn.resize = function(max_size) {
	m = Math.ceil;
	if (max_size == undefined) {
		max_size = 234;
	}
	h = w = max_size;
	$(this).each(function() {
		image_h = $(this).height();
		image_w = $(this).width();
		if (image_h > image_w) {
			w = m(image_w / image_h * max_size);
		} else {
			h = m(image_h / image_w * max_size);
		}
		$(this).css({
			height : h,
			width : w
		});
	})
};

//
// After the DOM is ready, resize the custom logo
//
$(document).ready(function() {

	//move the survey tip to the bottom
	if($("#ev_survey").length > 0){
		//move to bottom
		$("#ev_survey").css("top", $(window).height());
	}

	// Initialize the login opener.  It starts as display:none
	// in case js is turned off.
	$("#loginPlus").show();
	$("#loginNojs").hide();
	$("#loginPlus").unbind('click');
	$("#logoutPlus").unbind('click');
	// Now bind click to toggle the
	$("#loginPlus").bind({
		click : function(event) {
			event.preventDefault();
			$("#loginBox").toggle();
			if ($(this).is(":visible")) {
				$(this).toggleClass("minus bg4");
				$(this).toggleClass("plus");
			}
		}
	});

	$("#logoutPlus").bind({
		click : function(event) {
			event.preventDefault();
			$("#logoutBox").toggle();
			if ($(this).is(":visible")) {
				$(this).toggleClass("minus bg4");
				$(this).toggleClass("plus");
			}
		}
	});
	//skip to show links code
	$(".skipnav a").focus(function(){$(".skipnav a").addClass("show");$(".skipnav").css("height", "auto");});
	$(".skipnav a").last().blur(function(){$(".skipnav a").removeClass("show");$(".skipnav").css("height", "0px");});
	
	// Bind the login submit to validate login
	$("#personallogin").submit(function() {
		// TODO: validate login!
	});

	// Check if the custom-logo-img element exists and
	// if it does, resize appropriately
	if ($("#custom-logo-img").length > 0) {
		$("#custom-logo-img")[0].style['width'] = "210px";
		$("#custom-logo-img")[0].style['height'] = "60px";
	}

	// Bind all help URLs to a popup maker
	$('.helpurl').click(function(event) {
		event.preventDefault();

		var url = $(this).attr("href");
		var windowName = "helpPop";
		var w_left = (screen.width / 4);
		var w_top = (screen.height / 4);
		var w_width = 933;
		var w_height = 752;
		var strOptions = "location=no";
		strOptions += ",toolbar=no";
		strOptions += ",menubar=no";
		strOptions += ",status=no";
		strOptions += ",scrollbars=yes";
		strOptions += ",resizable=yes";
		strOptions += ",left=" + w_left;
		strOptions += ",top=" + w_top;
		strOptions += ",width=" + w_width;
		strOptions += ",height=" + w_height;
		strOptions += ";";

		var helppop = window.open(url, windowName, strOptions);
		if (helppop != null) helppop.focus();
	});
	$('.knovelLink').click(function(event) {
		event.preventDefault();
		var url = $(this).attr("href");
		var title = $(this).attr("title");
		GALIBRARY.createWebEvent("Knovel", title, url);
		var strOptions = "location=no";
		var w_left = (screen.width / 4);
		var w_top = (screen.height / 4);
		var w_width = 1024;
		var w_height = 768;
		strOptions += ",toolbar=no";
		strOptions += ",menubar=no";
		strOptions += ",status=no";
		strOptions += ",scrollbars=yes";
		strOptions += ",resizable=yes";
		strOptions += ",left=" + w_left;
		strOptions += ",top=" + w_top;
		strOptions += ",width=" + w_width;
		strOptions += ",height=" + w_height;
		strOptions += ";";
		window.open(url, "Knovel", strOptions);
	});

	function openTips(title, url, id){
		var elem = "#" + id + "Dialog";
		if(!$(elem).length){
			//console.log("none");
			$("body").append('<div id="'+id+'Dialog" >	<p id="'+id+'Content"></p></div>');
		}
		$( elem ).dialog({
			modal:false,
			width:400,
			height:300,
			open:popTips(url, "#" + id + "Content"),
			title:title

		});
		return false;
	}

	function popTips(url, elem){
		$.ajax({
			url:url
		}).success(function(data){
			$(elem).html(data);

		});

	}
//	 Associate evpopup links with popup functionality
	$(".evdialog").click(function(ev){
		openTips($(this).text(), $(this).attr("href"), $(this).attr("id"));
		ev.preventDefault();
		ev.stopImmediatePropagation();
		return false;
	});

	// Associate newsearch class links with function
	// to check if there's a parent window
	$(".newsearch").click(function(ev) {
		if (window.opener != null) {
			ev.preventDefault();
			window.opener.location = $(this).attr('href');
			self.close();
		}
	});

	// Handle GA webevent link clicks
	$(".webeventlink").click(function(e) {
		var category = $(this).attr('category');
		var action = $(this).attr('action');
		if (category && action) {
			e.preventDefault();
			GALIBRARY.createWebEvent(category, action);
			// Use delay to ensure request gets to GA!
			timeouthref = $(this).attr('href');
			setTimeout(go, 180);
			return false;
		}
	});

	$(".mosrchsourcesidebarli a").click(function(e) {
			GALIBRARY.createWebEvent("External Link", $(this).text());
			return false;
	});
});

var timeouthref = "home.url";
function go() {
	if ('#' != timeouthref) window.location=timeouthref;
}

function helpurlClick(link){

	var url = $(link).attr("href");
	var windowName = "helpPop";
	var w_left = (screen.width / 4);
	var w_top = (screen.height / 4);
	var w_width = 933;
	var w_height = 752;
	var strOptions = "location=no";
	strOptions += ",toolbar=no";
	strOptions += ",menubar=no";
	strOptions += ",status=no";
	strOptions += ",scrollbars=yes";
	strOptions += ",resizable=yes";
	strOptions += ",left=" + w_left;
	strOptions += ",top=" + w_top;
	strOptions += ",width=" + w_width;
	strOptions += ",height=" + w_height;
	strOptions += ";";

	var helppop = window.open(url, windowName, strOptions);
	if (helppop != null) helppop.focus();

}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results == null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}
