
// **********************************************************************
//
// Handle the actions for Ask an Expert - register here for dom ready
//
// **********************************************************************
$(document).ready(function() {

	//
	// Show the appropriate discipline section on click
	//
	$(".expertbox").click(function() {
		$(".showhide").hide();
		$("#part_"+$(this).attr('id')).show();
	    GALIBRARY.createWebEventWithLabel('AskAnExpert', 'Show Expert', 'ID=' + $(this).attr('id'));

		$(".expertbox").css('background-color','');
		$(".expertbox").css('cursor','default');
		$(".expertbox").css('-webkit-box-shadow','2px 2px 5px #aaa');
		$(".expertbox").css('box-shadow','2px 2px 5px #aaa');

		$(this).css('background-color','#e8e8e8');
		$(this).css('cursor','pointer');
		$(this).css('-webkit-box-shadow','2px 2px 5px #148C75');
		$(this).css('box-shadow','2px 2px 5px #148C75');

	});

	//
	// Handle mouseover/out events for expert boxes
	//
	$(".expertbox").mouseover(function() {
		var active = $("#part_"+$(this).attr('id')).is(":visible");
		if (!active) {
			$(this).css('background-color','#e8e8e8');
			$(this).css('cursor','pointer');
			$(this).css('-webkit-box-shadow','2px 2px 5px #148C75');
			$(this).css('box-shadow','2px 2px 5px #148C75');
		}
	});
	$(".expertbox").mouseout(function() {
		var active = $("#part_"+$(this).attr('id')).is(":visible");
		if (!active) {
			$(this).css('background-color','');
			$(this).css('cursor','default');
			$(this).css('-webkit-box-shadow','2px 2px 5px #aaa');
			$(this).css('box-shadow','2px 2px 5px #aaa');
		}
	});

	//
	// Show the appropriate discipline layer on click
	//
	$(".discipline").click(function(event) {
		event.preventDefault();
		// Hide all layers
		$(".Layer").hide();
		// Show current layer
		var id = $(this).attr('id');
		activelayer = id;
		$("#"+activelayer+"_Layer").show();
	    GALIBRARY.createWebEventWithLabel('AskAnExpert', 'Show Expert Layer', 'ID=' + id);
	});

	//
	// Handle mouseover/out events
	//
	$(".discipline").mouseover(function() {
		var id = $(this).attr('id');
		$(this).attr('src','/static/images/ae/' + id + '_over.gif');
		$(this).css('cursor','pointer');
	});
	$(".discipline").mouseout(function() {
		var id = $(this).attr('id');
		$(this).attr('src','/static/images/ae/' + id + '.gif');
		$(this).css('cursor','default');
	});

	//
	// Handle "ask" buttons
	//
	$(".emaillink.button").click(function(event) {
		if ($(this).attr('href')) {
			window.open($(this).attr('href'), 'NewWindow',
					'status=yes,resizable,scrollbars,width=600,height=600');
			event.preventDefault();
		}
	});
});


function emailFormat(sessionid, section) {
	var url = "/controller/servlet/Controller?EISESSION=" + sessionid
			+ "&CID=askanexpert&section=" + escape(pageTitles[section])
			+ "&sectionid=" + section;
	new_window = window.open(url, 'NewWindow',
			'status=yes,resizable,scrollbars,width=600,height=600');
	new_window.focus();
}
function emailGuruFormat(sessionid, section, discipline, guru) {
	var url = "/controller/servlet/Controller?EISESSION=" + sessionid
			+ "&CID=askanexpert&section=" + escape(pageTitles[section])
			+ "&sectionid=" + section + "&discipline="
			+ escape(layerTitles[discipline]) + "&disciplineid=" + discipline
			+ "&guru=" + escape(guru);
	new_window = window.open(url, 'NewWindow',
			'status=yes,resizable,scrollbars,width=600,height=600');
	new_window.focus();
}
