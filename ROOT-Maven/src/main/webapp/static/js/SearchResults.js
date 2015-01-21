/**
 * Run some code at document ready to prepare the
 * search results page for user interaction
 */
sortclick = false;
$(document).ready(function() {
	if (!Array.prototype.indexOf)
	{
	  Array.prototype.indexOf = function(elt /*, from*/)
	  {
	    var len = this.length >>> 0;

	    var from = Number(arguments[1]) || 0;
	    from = (from < 0)
	         ? Math.ceil(from)
	         : Math.floor(from);
	    if (from < 0)
	      from += len;

	    for (; from < len; from++)
	    {
	      if (from in this &&
	          this[from] === elt)
	        return from;
	    }
	    return -1;
	  };
	}

	 $("body").ajaxSend(function () { $(this).addClass("wait"); });
	 $("body").ajaxComplete(function () { $(this).removeClass("wait"); });
	//
	// Handle sorting
	//
	if ($("#sort").length > 0) {
		$("#sort").change(function(e) {
			if(_gaq){
				GALIBRARY.createWebEventWithLabel('Results', 'Sort By', $(this).find("option:selected").val());
			}

			window.location.href = $(this).find("option:selected").val();
		});
	}

	$(".pageSizeVal").change(function(e) {
		e.preventDefault();
		var form = $("#gotopage_top");
		var reruncid = form.find("input[name='CID']").val();
		var databaseval= form.find("input[name='database']").val();
		var searchid= form.find("input[name='SEARCHID']").val();
		var pagesizeval= $(this).find("option:selected").val();
		var sort= form.find("input[name='sort']").val();
		var sortdir= form.find("input[name='sortdir']").val();
		var scope = form.find("input[name='scope']").val();

		var DupFlag= form.find("input[name='DupFlag']").val();
		var dbpref= form.find("input[name='dbpref']").val();
		var fieldpref = form.find("input[name='fieldpref']").val();
		var origin = form.find("input[name='origin']").val();
		var dbLink = form.find("input[name='dbLink']").val();
		var linkSet= form.find("input[name='linkSet']").val();
		var navigator= form.find("input[name='navigator']").val();
		var linkResultCount= form.find("input[name='linkResultCount']").val();
		var rerunactionurl=form.find("input[name='rerunactionurl']").val();

		var url= rerunactionurl+"?CID="+reruncid+"&database="+databaseval+"&SEARCHID="+searchid+
		"&pageSizeVal="+pagesizeval;

		if (sort != undefined) url += "&sort="+sort;
		if (sortdir != undefined) url += "&sortdir="+sortdir;
		if (scope && scope.length > 0) url += "&scope="+scope;
		if (DupFlag != undefined) url += "&DupFlag="+DupFlag;
		if (dbpref != undefined) url += "&dbpref="+dbpref;
		if (fieldpref != undefined) url += "&fieldpref="+fieldpref;
		if (origin != undefined) url += "&origin="+origin;
		if (dbLink != undefined) url += "&dbLink="+dbLink;
		if (linkSet != undefined) url += "&linkSet="+linkSet;
		if (navigator != undefined) url += "&navigator="+navigator;
		if (linkResultCount != undefined) url += "&linkResultCount="+linkResultCount;

		window.location.href = url;
	});


	$("#pageSizeVal_patent").change(function(e) {
		e.preventDefault();
		var form = $("#gotopage_patent");
		var reruncid = form.find("input[name='CID']").val();
		var databaseval= form.find("input[name='database']").val();
		var searchid= form.find("input[name='SEARCHID']").val();
		var pagesizeval= $(this).find("option:selected").val();
		var docid= form.find("input[name='docid']").val();
		var rerunactionurl=form.find("input[name='rerunactionurl']").val();

		var url= rerunactionurl+"?CID="+reruncid+"&database="+databaseval+"&SEARCHID="+searchid+
		"&pageSizeVal="+pagesizeval+"&docid="+docid+"&format=patentReferencesFormat";
        window.location.href = url;
	});

	//
	// Make the facet/navigator list sortable
	//
	if ($("#facetsortable").length > 0) {
		$("#facetsortable").sortable({
			axis: 'y',
			//containment: 'parent',
			opacity: 0.35,
			items: '.facetbox',
			placeholder: 'facet-sortable-placeholder',
			start: function(event, ui) {
				var facetboxhelper = $(ui.helper);
				facetboxhelper.css("font-weight","normal").css("border-bottom","1px solid #ADAAAD");
			},
			stop: function(event, ui) {
				ui.item.removeAttr('style');
			},
			change: function(event, ui) {
				$(ui.helper).css("font-weight","normal").css("background-color","#FFFFAA");
			},
			update: function(event, ui) {
				sortclick = false;
				var isopen = parseInt($(ui.item).attr('isopen'));
				if (isopen && isopen == -1) $(ui.item).attr('isopen','-5');
				ajaxUpdateState();
			}
		});
	}

	//
	// Show/hide individual facet/navigator
	//
	$(".facetshowhide").click(function(e) {

		// Check if sorting is starting!
		if(sortclick){
			sortclick = false;
			return;
		}

		// Get the top-level facetbox for state info
		var facetbox = $(this).parents('.facetbox');
		// Get the facetentries box
		var facetentries = facetbox.children('.facetentries');
		// Get the current open state
		var isopen = facetbox.attr('isopen');
		if (!isopen || isopen == undefined) isopen = -1;
		if (isopen < 0) {
			// Facet is currently closed, open based on last displayed
			facetentries.show();
			if (isopen == -1) isopen = -5;
			else if (isopen <= -10) facetbox.find('.facetbottom5').show();
		} else {
			// Facet is currently open, close and save lastopen
			facetentries.hide();
		}
		isopen = -isopen;
		facetbox.attr('isopen',isopen);
		facetbox.find("img.facetupdown").attr('src', '/static/images/facet_' + (isopen > 0 ? 'up' : 'down') + '.png');
		facetbox.find(".facetshowhide").attr('title',isopen > 0 ? 'Close':'Open');
		// Save state via Ajax call - ignore response...
		ajaxUpdateState();
	});

	$(".abstractpreview").click(handleAbstractPreview);

	//
	// Updates the navigator state via Ajax call
	//

	function ajaxUpdateState() {
		var facetboxes = $(".facetbox");
		var fieldarr=new Array();
		var idx = 0;
		facetboxes.each(function() {
			var field = $(this).attr('field');
			var isopen = $(this).attr('isopen');
			fieldarr[idx++] = (field+"_"+isopen);
		});
		$.get("/session/navstate.url?CID=navstate&navid=" +fieldarr.join(','));

	}

	//cache results from navigator calls
	var allNavigators = new Array();
	var valuesChecked = new Array();
	$(".navCheck").bind("click", function (){
		//console.log($(this).attr("id"));
		valuesChecked[$(this).attr("id")] = $(this).prop("checked");
	});

	//
	// Handle navigator show more/less links
	//
	$(".facet_moreless .showmore").click(function(e) {
		var facetbox = $(this).parents('.facetbox');
		var more = parseInt(facetbox.attr('more'));
		var total = parseInt(facetbox.attr('total'));
		var isopen = parseInt(facetbox.attr('isopen'));
		var field = $(this).attr('field');
		var searchId = $(this).attr('searchId');

		checkIfOverlayOpen();

		if (more == 0 || isopen < 10) {
			if (more ==0) {
				facetbox.find('.facetbottom5').show();
				$(this).hide();
				facetbox.attr('isopen',10);
				$(this).siblings('.showless').show();
			} else if (isopen < 10) {
				facetbox.find('.facetbottom5').show();
				facetbox.attr('isopen',10);
				$(this).siblings('.showlessSeprator').show();
				$(this).siblings('.showless').show();
			}
			ajaxUpdateState();
			e.preventDefault();
			return false;
		}else{
			if(total <= 40){
				$("#showmore_overlay_" + field).hide();
				$("#showlessSeprator_" + field).hide();
			}
			//make ajax call to get more facets
			if(total > 40){
				total = 40;
			}
			showNavigatorOverlay(field,  total, searchId);

		}

		return false;
	});

	function checkIfOverlayOpen(){
		$(".facet_overlay_box").each(function(){
			if($(this).is(":visible")){
	        	hideNavigatorOverlay($(this).attr("field"), $(this).attr("searchId"));
	            $(document).unbind("click");
			}
		});
	}

	$(".facet_overlay_showhide").click(function(e){
		var field = $(this).attr("field");
		var searchId = $(this).attr('searchId');

		hideNavigatorOverlay(field);
	});
	$(".showmore_overlay").click(function (e){
		var total = parseInt($(this).attr('total'));
		var searchId = $(this).attr('searchId');
		var field = $(this).attr('field');
		populateNavigatorOverlay(field, total,searchId);
		$("#facet_overlay_entries_" + field).show();
		$("#facet_overlay_" + field).show();
		$("#showmore_overlay_" + field).hide();
		$("#showlessSeprator_" + field).hide();

		return false;
	});
	$(".showless_overlay").click(function (){
		var total = 40;
		var field = $(this).attr('field');
		var open = $("#showless_overlay_" + field).attr("numOpen");
		var searchId = $(this).attr('searchId');


		if(parseInt(open) <= 40){
			//we need to just get close the panel
			hideNavigatorOverlay(field, searchId);
			return false;
		}

		populateNavigatorOverlay(field, total,searchId);
		$("#facet_overlay_entries_" + field).show();
		$("#facet_overlay_" + field).css("top", $("#facet_" + field).offset().top);
		$("#facet_overlay_" + field).show();
		$("#showmore_overlay_" + field).show();
		$("#showlessSeprator_" + field).show();

		return false;
	});

	//Show the navigator popout
	function showNavigatorOverlay(field, modCount, searchId){
		populateNavigatorOverlay(field, modCount, searchId);
		$("#facet_overlay_entries_" + field).show();
		$("#facet_overlay_" + field).css("top", $("#facet_" + field).offset().top);
		$("#facet_overlay_" + field).show();
		$(document).click(function(event) {
		    if($(event.target).parents().index($('#facet_overlay_' + field)) == -1) {
		        if($('#facet_overlay_' + field).is(":visible")) {
		        	hideNavigatorOverlay(field, searchId);
		            $(document).unbind("click");
		        }
		    }
		});

	}
	function hideNavigatorOverlay(field, searchId){

		//check for checked or not
		$("#facet_" + field +" input").each(function(){
			if(valuesChecked[$(this).attr("id")]){
				this.checked = true;
			}else{
				this.checked = false;
			}

		});

		for(var i = 0; i < $("#facet_overlay_entries_" + field + " .navCheck").length && i < 10; i++){
			$("#facet_overlay_entries_" + field + " #" + field +"nav" + i).prop("checked", false);
		}

		$("#facet_overlay_" + field).hide();
		$("#showmore_overlay_" + field).show();
		$("#showlessSeprator_" + field).show();
		$("#showmless_overlay_" + field).show();
		ajaxUpdateState();
		updateMoreCount(field,searchId);
	}


	//add the modifiers to the popout.
	function populateNavigatorOverlay(field, modCount, searchId){

		//get the navs either from cache or ajax
		var	navs = getNavigators(field, modCount, searchId);
		var perCol = 10;

		$("#facet_overlay_entries_" + field).html("");

		if(navs && navs.modifiers){
			//we have nagivators yay
			var clearDiv = "<div class='clear'></div>";
			var ul = "<ul class='overlayUL'/>";

			var howMany = modCount > navs.modifiers.length ? navs.modifiers.length : modCount;

			if(modCount > 40){
				perCol = Math.ceil(howMany / 4);

			}

			//console.log("howMany: " + howMany);
			for(var i = 0; i < howMany; i++){
				var checkDiv = "<div class='floatL w10'></div>";
				checkDiv = $(checkDiv).append($("<input />", {type:'checkbox', name:navs.name, 'class':'navCheck', id:navs.name + (i + 1), value:navs.modifiers[i].value, checked : valuesChecked[navs.name + (i + 1)]}));

				var labelDiv = "<div class='floatL overlayValue' ></div>";
				var label = navs.modifiers[i].label;


				labelDiv = $(labelDiv).append($("<label/>",{ 'class' : 'floatL ', 'style' : navs.modifiers[i].count > 999? 'width:75%' : 'width:80%', 'for':navs.name + (i + 1), text:label, title:navs.modifiers[i].label}));
				labelDiv = $(labelDiv).append($("<span/>", {'class':'floatR', text:'(' + navs.modifiers[i].count + ')'}));

				var li = $("<li/>").append(checkDiv);
				li = $(li).append(labelDiv);
				li = $(li).append(clearDiv);
				ul = $(ul).append(li);


				if(((i + 1) % perCol) == 0){
					if(i < howMany - 1){
						// add border color
						ul = $(ul).addClass("overlayRightBorder");
					}
					//column limit reached create UL
					$("#facet_overlay_entries_" + field).append(ul);

					ul = "<ul class='overlayUL' />";
				}else if(i == navs.modifiers.length - 1){
					$("#facet_overlay_entries_" + field).append(ul);
				}
			}
			$("#facet_overlay_entries_" + field).append(clearDiv);
			$(".navCheck").unbind("click");
			$(".navCheck").bind("click", function (){
				//console.log($(this).attr("id"));
				valuesChecked[$(this).attr("id")] = $(this).prop("checked");
			});
			//uncheck any of the first 10 behind the overlay.

			for(var i = 0; i < $("#facet_" + field + " .navCheck").length && i < 10; i++){
				$("#facet_" + field + " #" + field +"nav" + i).prop("checked", false);
			}

			$("#facet_overlay_entries_" + field).attr('isOpen', modCount);
			$("#showless_overlay_" + field).attr({numOpen : modCount});
		}

	}

	function getNavigators(field, modCount, searchId){
		var jsonResp;
		var navs = allNavigators[field];
		if(navs && navs.modifiers.length > 0 && navs.modifiers.length >= modCount ){
			//console.log("returned cached");
			//console.log(navs);
			return navs;
		}
		$.ajax({
				url:"/getnav.json?field=" + field+":"+modCount + "&searchId=" + searchId,
				dataType:"json",
				type:"get",
				async: false
			}).success(function (data, status, xhr){
				jsonResp = data[0];

			});
		if(jsonResp){

			allNavigators[field] = jsonResp;
			return jsonResp;
		}

	}

	function updateMoreCount(field, searchId){
		var jsonResp;
		$.ajax({
				url:"/getnav.json?field=" + field+":10&searchId=" + searchId,
				dataType:"json",
				type:"get",
				async: true
			}).success(function (data, status, xhr){

			});

	}

	$(".facet_moreless .showless").click(function(e) {
		var facetbox = $(this).parents('.facetbox');
		var more = facetbox.attr('more');
		var isopen = parseInt(facetbox.attr('isopen'));
		var visible = $("#facet_"+$(this).attr('field')+" .facetentry:visible").length;
		if (visible > 5 && visible <= 10) { 	// Showing more than 5 so reduce to 5
			$(this).siblings('.showmore').show();
			$(this).parents('.facetbox').find('.facetbottom5').hide();
			$(this).siblings('.showlessSeprator').hide();
			$(this).hide().siblings('.showless').hide();
			facetbox.attr('isopen',5);
			ajaxUpdateState();
			e.preventDefault();
			return false;
		}
	});


	var swapCodes = new Array(8211, 8212, 8216, 8217, 8220, 8221);
	var swapStrings = new Array("-", "-", "'", "'", "\"", "\"");

	//
	// Navigators submit
	//
	$("#facetform").submit(function(e) {
		var form = $(this);
		var checked = form.find("input[type='checkbox']:checked");
		var append = form.find("input[name='topappend']");

		if (checked.length == 0 && (!append || jQuery.trim(append.val()).length == 0)) {
			alert("Please select at least one search refinement, or enter a term in the \"Add a term\" box.");
			return false;
		}else{
			if(_gaq){
				for(var i = 0; i< checked.length; i++ ){
					var facetSection = $(checked[i]).attr("name");
					var facetLabel = $("label[for="+$(checked[i]).attr("id")+"]").text();
					facetSection = facetSection.replace(/nav/g,"");
					//console.log("create event : " + facetSection + " " + facetLabel);
					GALIBRARY.createWebEventWithLabel('Facets',facetSection,facetLabel);
				}
			}
		}

		var textNodeValue = append.val();
        for (var j = 0; j < swapCodes.length; j++) {
            var swapper = new RegExp("\\u" + swapCodes[j].toString(16), "g");
            textNodeValue = textNodeValue.replace(swapper, swapStrings[j]);
        }

        append.val(textNodeValue);

		return true;
	});

	//
	// Navigators new search submit
	//
	$("#facetsearchform").submit(function(e) {
		var form = $(this);
		var append = form.find("input[name='btmappend']");
		var checked = $("#facetform").find("input[type='checkbox']:checked");

		if ((!checked || checked.length==0) && (!append || jQuery.trim(append.val()).length == 0)) {
			alert("Please select at least one search refinement, or enter a term to run a new search.");
			return false;
		}

		var textNodeValue = append.val();
        for (var j = 0; j < swapCodes.length; j++) {
            var swapper = new RegExp("\\u" + swapCodes[j].toString(16), "g");
            textNodeValue = textNodeValue.replace(swapper, swapStrings[j]);
        }

        append.val(textNodeValue);

		return true;
	});

	//
	// New search from navigators submit
	//
	$("#facetform input[type='checkbox']").click(function(e) {
		var navigatorsearchform = $("#facetsearchform");
		var checked = $(this).is(':checked');
		if (checked) navigatorsearchform.append("<input type='hidden' name='" + $(this).attr('name') + "' value='" + $(this).val() + "'/>");
		else navigatorsearchform.remove("input[type='hidden',value=$(this).val()]");
	});

	//
	// Hovers
	//
	$("img.graph").bind('mouseover', function() {
		$(this).attr({src:"/engresources/images/Graph_On.png"});
	});
	$("img.graph").bind('mouseout', function() {
		$(this).attr({src:"/engresources/images/Graph.png"});
	});
	$("img.data").bind('mouseover', function() {
		$(this).attr({src:"/engresources/images/Data_On.png"});
	});
	$("img.data").bind('mouseout', function() {
		$(this).attr({src:"/engresources/images/Data.png"});
	});


	// Initially set page/all checkbox state
	if (document.forms.resultsform) {

		var maxSelectedRecords = '500';
		var basketCount = document.forms.resultsform.basketCount.value;
		var pageSize = document.forms.resultsform.pagesize.value;
		var resultsCount =document.forms.resultsform.resultscount.value;

		if(Number(pageSize)>Number(resultsCount)){
			pageSize = resultsCount;
		}

		if(Number(resultsCount)<Number(maxSelectedRecords)){
			maxSelectedRecords = resultsCount;
		}

		if ($("input[name='cbresult']:checked").length == Number(pageSize)) {
			$("input[name='page']").attr('checked',true);
		} else {
			$("input[name='page']").attr('checked',false);
		}
	}

	//
	// Clear selected records on new search function
	//
	$("#topchkClr").click(function() {
		var img = new Image() ;

		img.src = "/engresources/Basket.jsp?select=clearonnewsearch" +
			"&sessionid="+document.resultsform.sessionid.value+
			"&clearonvalue="+$(this).is(":checked")+
			"&timestamp="+new Date().getTime();
	});
	/**
	 * Handle the view link
	 *
	 * There is a duplicate of this function in the js file 'SelectedSet.js' called  viewSelectedSetFormat()
	 * The Only Difference is that the viewSelectedSetFormat() function takes one
	 * extra parameter, basketcount, which preserves the current 'page' within the basket
	 *
	 */
	$("#viewlink").click(function(e) {
		e.preventDefault();
		var form = $("#resultsform");
		var viewurl = $(this).attr('href');

		// Set the CID according to the selectoption element
		var displaytype = 'citation';//form.find("input[name='selectoption']:checked").val();
		if ('citation' != displaytype) {
			$.each(viewurl.split('?'), function(i, ival) {
				if (i == 0) viewurl = ival;
				else {
					$.each(ival.split('&'), function(j,jval) {
						if (j==0) viewurl += "?"; else viewurl += "&";
						if (jval.indexOf("CID")==0) viewurl += "CID=" + displaytype + 'SelectedSet';
						else viewurl += jval;
					});
				}
			});
		}


		document.location = viewurl;
		return false;
	});


	/**
	 * Handle the download link
	 */
//	$("#downloadlink").click(function (e) {
//		e.preventDefault();
//		var form = $("#resultsform");
//		var displayformat='citation';
//
//		var downloadurl = "/delivery/download/display.url?database="+form.find("input[name='database']").val()+
//			"&displayformat="+displayformat+
//			"&allselected=true";
//		if(_gaq){
//			GALIBRARY.createWebEventWithLabel('Delivery Options', 'Download','');
//		}
//		var new_window = window.open(downloadurl,'NewWindow','status=yes,resizable,scrollbars=no,width=600,height=600');
//		new_window.focus();
//
//		return false;
//	});

	/**
	 * Handle the print link
	 */
	$("#printlink").click(function(e) {
		e.preventDefault();
		var form = $("#resultsform");
		var displayformat='citation';
		var printurl = "/delivery/print/display.url?timestamp="+new Date().getTime()+"&displayformat="+displayformat;
		if(_gaq){
			GALIBRARY.createWebEventWithLabel('Delivery Options', 'Print','');
		}
		var new_window = window.open(printurl,'NewWindow','status=yes,resizable,scrollbars,width=700,height=500');
		new_window.focus();

		return false;
	});


	// THIS FUNCTION BASICALLY CONSTRUCT ALL THE REQUIRED PARAMETERS FOR EMAIL.THE VALUES SO CONSTRUCTED ARE SENT TO
	// EMAIL FORM.(emailSelectedRecords,emailSelectedFormatResults.jsp)
	$("#emaillink").click(function(e) {
		e.preventDefault();
		var form = $("#resultsform");
		var displayformat='citation';

		var emailurl = "/delivery/email/display.url?"+
			"&searchid="+form.find("input[name='searchid']").val()+
			"&database="+form.find("input[name='database']").val()+
			"&displayformat="+displayformat+
			"&timestamp="+new Date().getTime();
		if(_gaq){

			GALIBRARY.createWebEventWithLabel('Delivery Options', 'Email','');
		}
		var new_window = window.open(emailurl,'NewWindow','status=yes,resizable,scrollbars=no,width=500,height=580');
		new_window.focus();

		return false;
	});

	
	$(".viewChart").click(function(){
		if(_gaq){

			GALIBRARY.createWebEventWithLabel('Refine Results', 'View Chart',$(this).attr("field"));
		}		
		
	});
	// Handle the "Go" button from page navigation
	$("form[name='gotopageform']").submit(function(event) {
		var pagenumber = parseInt($('input[name="PAGE"]', this).val());
		var pagecount = parseInt($('input[name="pagecount"]', this).val());
		var resultCountPerPage = parseInt($("input[name='resultCountPerPage']",this).val());
		var numericExpression = /^[0-9]+$/;

		if(resultCountPerPage*pagenumber > 4000){
			event.preventDefault();
			window.alert("Only the first 4000 records can be viewed.");
			return false;
		}

		if (isNaN(pagenumber) || pagenumber < 1 || pagenumber > pagecount) {
			event.preventDefault();
			alert("Please enter a valid page number.");
			return false;
		}

		if (!($('input[name="PAGE"]', this.form).val()).match(numericExpression)) {
			event.preventDefault();
			alert("Please enter a valid page number.");
			return false;
		}
	});

	if(typeof checkNavigatorForCV != 'undefined' && checkNavigatorForCV){
		var query = $.trim($("#querytext").text().toLowerCase());
		query = query.replace(/(AND|OR|NOT) /g,'');
		query = query.replace(/\(+|\)+|{+|}+/g,',');
		query = query.replace(/, +/g, ',');
		query = query.replace(/,+/g, ',');
		var qArray = query.split(',');
		var highlighted = false;
	//console.log("The query array looks like: " + qArray);

		if($("#facet_cv")){
			//we have controlled terms! Let's check them
			$("#facet_cv").find(".facetentry_label").each(function(){
			    if(qArray.indexOf($.trim($(this).text().toLowerCase()))>0){
			        //console.log($(this).text());
			    	if(highlightV1){
			    		//check for cookie, if no cookie we need to figure out what color is being used.
			    		if($.cookie('ev_highlight')){

							var hlOptions = JSON.parse($.cookie("ev_highlight"));
							if(!hlOptions.bg_highlight){
								$(this).addClass("hit");
								$(this).removeClass("bghit");
								$(this).css("color", hlOptions.color);
							}else{
					    		$(this).addClass("bghit");
					    		$(this).removeClass("hit");
							}
			    		}else{
			    			if($(".hit").length > 0){
			    				$(this).addClass("hit");
								$(this).removeClass("bghit");
			    			}else{
			    				$(this).addClass("bghit");
					    		$(this).removeClass("hit");
			    			}

			    		}
			    	}else{
			    		$(this).css('background-color', 'yellow');
				        $(this).css('font-weight', 'bold');
			    	}


			        $(this).attr("title", "Select term(s) to improve your results");
			       // $(this).attr("alt", "this is an alt tag that is really really long");
			        highlighted = true;
			    }

			});
			if(highlighted){
				if($('.facetbox').length > 3){
					//move it to the 1st position
					//console.log("Move facet");
					$('#facet_cv').insertBefore($('.facetbox')[1]);
				}
				//open facet if it's closed
				//console.log("open facet");
				if(parseInt($('#facet_cv').attr('isopen')) < 0){
					$('#facet_cv .facettitle').trigger('click');
				}
			}
		}
	}
	$("#knovelSearchSubmit").click(function(){
		//run the knovel search
		var query = $("#displayquery").text();
		var url ="http://app.knovel.com/web/search.v?kpromoter=engineeringvillage-search&q=";
		var windowName = "Knovel Search";
		var strOptions;
		
		url += escape(query);
		
		var knovelpop = window.open(url, windowName, strOptions);
		
		if (knovelpop != null) knovelpop.focus();
		GALIBRARY.createWebEventWithLabel('Knovel', 'Search Button',query );
	});
	$(window).bind('resize', resizeresults);
    resizeresults();

	if(show_all){
		$(document).ready(function(){
			togglePreview();
		});
	}
});

//Adjust results area with resize
function resizeresults() {
	var min_facetcol_width = 268;
	var resultsbox_width = $("#resultsbox").width();

	var resultsbox_minwidth = $("#resultsbox").css('min-width');
    if (!resultsbox_minwidth || resultsbox_minwidth == 0) resultsbox_minwidth = 1015;
    else resultsbox_minwidth = Number(resultsbox_minwidth.replace('px',''));

    if (resultsbox_width >= resultsbox_minwidth) {
		$("#resultsarea").width($("#resultsbox").width() - min_facetcol_width);
	}
}

function navigatorsOnsubmit(eventcontrol)
{
    // loop to check that at least on checkbox has
    // been seelcted - or else hold submission
    var count = 0;
    for (i=0; i < document.forms.navigator.length; i++)
    {
        if (document.forms.navigator[i].type == "checkbox")
        {
            if (document.forms.navigator[i].checked == true)
            {
                count++;
            }
        }
    }
    // if add a term has a value then submit is ok
    if(document.forms.navigator.append.value != '')
    {
        count++;
    }
    if(count == 0)
    {
        alert("Please select at least one Search refinement.");
        return false;
    }
    if(document.forms.navigator.resultsorall[1].checked == true)
    {
        if(eventcontrol == 'exclude')
        {
            alert("Conflict: Exclude cannot Search all content.");
            return false;
        }
    }

    return true;
}

// Easy-search navigation bar refine form functions
function shortSearchValidation(sourceform)
{
  var searchword1 = sourceform.searchWord1.value;
  if((searchword1=="") || (searchword1==null)) {
      window.alert("Enter at least one term to search in the database.");
      return false;
  }

  var where = sourceform.where.value;
  if(where == '1')
  {
    sourceform.action="/search/results/expert.url?CID=expertSearchCitationFormat";
    sourceform.SEARCHID.value="";
    sourceform.RERUN.value="";
    return true;
  }
  else if(where == '2')
  {
    sourceform.action="/search/results/expert.url?CID=expertSearchCitationFormat";
    sourceform.SEARCHID.value="";
    sourceform.append.value=searchword1;
    return true;
  }

  return false;
}

// THIS FUNCTION FOR SETTING THE VALUES IN BOTTOM RESULTS MANAGER WHEN THE TOP RESULTS MANAGER VALUES CHANGED (or vice versa)
function setOppositeClearOnSearchValue(sourceform,destform,sessionid)
{
	var img = new Image() ;
	var now = new Date() ;
	var milli = now.getTime() ;
	var sourcelength = sourceform.selectoption.length;
	var i=0;
	var clearonvalue = null;

	for(i=0; i < sourcelength ; i++)
	{
	   if(sourceform.selectoption[i].checked)
	   {
	   		destform.selectoption[i].checked = true;
	   		break;
	   }
	}

	if(sourceform.cbclear.checked)
	{
		destform.cbclear.checked = true;
		clearonvalue="true";
	}
	else
	{
		destform.cbclear.checked = false;
		clearonvalue="false";
	}
	img.src = "/engresources/Basket.jsp?select=clearonnewsearch&sessionid="+sessionid+"&clearonvalue="+clearonvalue+"&timestamp="+milli;
}

// THIS FUNCTION FOR SETTING THE VALUES IN BOTTOM RESULTS MANAGER WHEN THE TOP RESULTS MANAGER VALUES CHANGED (or vice versa)
function setOppositeSelectOptionValues(sourceform,destform)
{
	var sourcelength = sourceform.selectoption.length;
	var i=0;

	for(i=0; i < sourcelength ; i++)
	{
	   if(sourceform.selectoption[i].checked)
	   {
	   		destform.selectoption[i].checked = true;
	   		break;
	   }
	}

	if(sourceform.cbclear.checked)
	{
		destform.cbclear.checked = true;
	}
	else
	{
		destform.cbclear.checked = false;
	}
}


function recordCountValidation(srcForm){

	var resultCountPerPage = srcForm.resultCountPerPage.value;
	var page = srcForm.PAGE.value;

	if(resultCountPerPage*page > 4000){
        window.alert("Only the first 4000 records can be viewed.");
		return false;

	}
 return true;
}


function handleAbstractPreview(event) {
	event.preventDefault();

	var link = $(this);
	var num = link.attr('num');
	var previewtext = $("#previewtext_"+num);
	var linktext = jQuery.trim(link.text());
	var previewtextval = jQuery.trim(previewtext.text());
	var absLink = $("#abslink_" + num).attr("href");
	var absTitle = $("#abslink_" + num).attr("title");


	if (previewtextval == "") {
		var previewurl = link.attr("href");
		var physicalquery = $("#physicalquery").text();
		previewurl += "&query=" + physicalquery;

		if(highlightV1){
			previewurl += "&partial=true";
		}

		link.html("Loading...");
		$.ajax({
			type : "GET",
			cache : false,
			dataType : "html",
			url : previewurl,
			success : function(data, status, xhr) {
				var json = jQuery.parseJSON(data);

				if (json.previewtext == 'error') {
					link.html("<img id=\"previewimage\" src=\"/static/images/EV_hide.png\"/>Hide preview");
					previewtext.text("No preview available.").slideDown("slow", resizeresults);
					return;
				} else {
				    link.html("<img id=\"previewimage\" src=\"/static/images/EV_hide.png\"/>Hide preview");
				    var previewHtml = json.previewtext;
				    if(typeof(json.theRest)!= 'undefined' && json.theRest.length > 0){
				    	previewHtml += "<span id='theRest_"+ num +"' style='display:none;'>"+json.theRest+"</span>";
				    }
				    if(typeof(json.countLeft)!= 'undefined' && json.countLeft > 0){
				    	previewHtml += "<a href='' title='Click to show full abstract inline' onclick='$(\"#theRest_"+ num +"\").toggle();$(this).hide();return false;'>... ("+ json.countLeft + " more search terms)</a>";

				    }else if(typeof(json.theRest)!= 'undefined' && json.theRest.length > 0){
				    	previewHtml += "<a href='' title='Click to show full abstract inline' onclick='$(\"#theRest_"+ num +"\").toggle();$(this).hide();return false;'>... see more</a>";
				    }
				    previewtext.html(previewHtml).slideDown("slow", resizeresults);
				}
				if((highlightV1)){
					checkHighlightCookie();
				}
			},
			error : function(data) {
				link.html("<img id=\"previewimage\" src=\"/static/images/EV_hide.png\"/>Hide preview");
				previewtext.text("No preview available.").slideDown("slow", resizeresults);
			}
		});
	} else {
		previewtext.slideToggle("slow", resizeresults);
	}

	if( linktext == "Hide preview"){
		link.html("<img id=\"previewimage\" src=\"/static/images/EV_show.png\"/>Show preview");
		if(_gaq){
			GALIBRARY.createWebEventWithLabel('Show Preview', 'Hide','' );
		}
	}else{
		link.html("<img id=\"previewimage\" src=\"/static/images/EV_hide.png\"/>Hide preview");
		if(_gaq){
			GALIBRARY.createWebEventWithLabel('Show Preview', 'Show','' );
		}
	}

}

function togglePreview(){
	$(".abstractpreview").each(function(){
		$(this).trigger("click");
	});
}

