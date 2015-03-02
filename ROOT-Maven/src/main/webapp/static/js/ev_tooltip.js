//{name:'cvTooltip', parent:".facet_cvHighlightFeature", direction:"right",msg:"Improve your search results by selecting term(s).<br/> <a href='http://www.google.com'>Learn more.</a>", autoShow:true, showImage:true},
tips = [
			
			

	];
$(document).ready(function() {
	var popupTime = 4500;
	$(document).keyup(function(e){
		if(e.keyCode == 27 && $(".tooltipstered").length > 0){
			$(".tooltipstered").each(function(){$(this).tooltipster("destroy");})
		}
	})
	for(i in tips){
		var tip = tips[i];
		var selector = '.'+tip.name;
		var msg = tip.msg;
		var isHtml = tip.contentAsHTML;

		if(tip.showImage && $(tip.parent).length != 0){
			var img = '<img src="/static/images/ev_asterisk.gif" style="vertical-align:top;" width="13px" class="'+tip.name+'"/>';
			$(tip.parent).append(img);

		}

		if ( $.browser.msie && $.browser.version == 7) {
			$(selector).tooltipster({interactive:true, arrow:false, contentAsHTML:isHtml});
			$(selector).tooltipster('reposition');
		}else{
			$(selector).tooltipster({interactive:true, position:tip.direction, contentAsHTML:isHtml});
		}
		$(selector).tooltipster('update',tip.msg);
		if(tip.autoShow && !$.cookie(tip.name) && $(tip.parent).length != 0){
			$(selector).tooltipster("show");
			setTimeout("hideTP('" + selector + "')",popupTime);
		}
	}
	//setup other tooltips that are more like overlays
	if($(".searchTipsToolTip").length != 0){
		$(".searchTipsToolTip").tooltipster({
		    position:'bottom-left',
		    fixedWidth:450,
		    maxWidth:450,
		    offsetX:50,
		    content: 'Loading...',
		    contentAsHTML:true,
		    autoClose:false,
		    interactive:true,
		    functionInit: function(origin, content) {

		        // we'll make this function asynchronous and allow the tooltip to go ahead and show the loading notification while fetching our data

		        //console.log("get data" + content);

		            $.ajax({
		                type: 'GET',
		                url: '/searchtips.jsp?topic=quick',
		                success: function(data) {
		                	//console.log(origin);
		                    // update our tooltip content with our returned data and cache it

		                    $(origin).tooltipster('content', data);
		                }
		            });

		    }
		});

	}
	if($("#downloadlink").length != 0){
		$("#downloadlink").click(function(e){
			if($(this).hasClass('tooltipstered')){
				return false;
			}
			e.preventDefault();
			var displayformat='citation';
			var downloadurl;
			if(typeof($("#downloadlink").attr('href')) != 'undefined' && $("#downloadlink").attr('href').length > 0){
				downloadurl = $("#downloadlink").attr('href');
			}else{
				var form = $("#resultsform");
				var folderid = form.find("input[name='folderid']").val();


				downloadurl = "/delivery/download/display.url?database="+form.find("input[name='database']").val()
				+"&displayformat="+displayformat
				+"&allselected=true";

				if(typeof(folderid) != 'undefined' && folderid.length > 0 ){
					downloadurl += "&folderid=" +folderid;
				}
			}

			if(((typeof(Basket) == 'undefined' || (Basket.count > 0)) || (typeof($(this).attr("href")) != 'undefined' &&  $(this).attr("href").length > 0))){

				$(this).tooltipster({
				    content: 'Loading...',
				    autoClose:false,
				    interactive:true,
				    contentAsHTML:true,
				    position:'bottom',
				    fixedLocation:true,
				    positionTracker:false,
				    multiple:true,
				    trigger:'click',
				    delay:0,
				    speed:0,
				    debug:false,
				    functionBefore: function(origin, continueTooltip) {
				        // we'll make this function asynchronous and allow the tooltip to go ahead and show the loading notification while fetching our data
				    	$(origin).tooltipster('content', "Loading...");
				        //console.log("get data" + content);
				    	continueTooltip();
				            $.ajax({
				                type: 'GET',
				                url: downloadurl,
				                cache: false,
				                success: function(data) {
				                    $(origin).tooltipster('content', data);

				                }
				            });

				    },
				    functionAfter: function(origin){
				    	$(origin).attr('title', 'Click to change one click download preferences.');

				    }
				});
				$(this).tooltipster('show',null);
			}else {
				$(this).tooltipster({
				    content: 'Please select records from the search results and try again',
				    autoClose:true,
				    interactive:false,
				    contentAsHTML:true,
				    position:'bottom',
				    fixedLocation:true,
				    positionTracker:false,
				    delay:0,
				    speed:0,
				    functionAfter: function(origin){$(origin).tooltipster('destroy');}
				});
				$(this).tooltipster('show',null);
			}
			return false;
		});
	}
	function getToolTipContent(origin, continueTooltip){
		         // we'll make this function asynchronous and allow the tooltip to go ahead and show the loading notification while fetching our data
		        continueTooltip();

		        // next, we want to check if our data has already been cached
		        if (origin.data('ajax') !== 'cached') {
		            $.ajax({
		                type: 'POST',
		                url: 'example.php',
		                success: function(data) {
		                    // update our tooltip content with our returned data and cache it
		                    origin.tooltipster('content', data).data('ajax', 'cached');
		                }
		            });
		        }

	}

});
function showTooltip(selector, msg, direction, duration, autoShow){
	if ( $.browser.msie && $.browser.version == 7) {
		$(selector).tooltipster({interactive:true, arrow:false});
		$(selector).tooltipster('reposition');
	}else{
		$(selector).tooltipster({interactive:true, position:direction});
	}
	$(selector).tooltipster('update',msg);
	if(autoShow && !$.cookie(selector) ){
		$(selector).tooltipster("show");
		setTimeout("hideTP('" + selector + "')",duration);
	}
}
function showSurvey(feature, location){
	var surveyUrl = "/widget/qsurvey.url?feature="+feature;
	var surveyCookie;
	var fromRecord = false;

	if(location == 'record'){
		fromRecord = true;
	}
	if($.cookie("ev_survey")){
		surveyCookie = JSON.parse($.cookie("ev_survey"));
		surveyCookie.pageTrack++;
		$.cookie("ev_survey", '{"pageTrack":'+surveyCookie.pageTrack+',"dontShow":'+surveyCookie.dontShow+',"fromRecord":'+fromRecord+'}',{expires: 365, path:'/'});
	}else{
		$.cookie("ev_survey", '{"pageTrack":'+1+',"dontShow":'+false+',"fromRecord":'+fromRecord+'}',{expires: 365, path:'/'});
		surveyCookie = {
				pageTrack:1,
				dontShow:false,
				fromRecord:fromRecord
		};
	}

	if(!surveyCookie.dontShow && surveyCookie.pageTrack >= 3 && (!fromRecord || (fromRecord && surveyCookie.fromRecord))){

		$("#ev_survey").tooltipster({
		    content: 'Loading...',
		    autoClose:false,
		    interactive:true,
		    contentAsHTML:true,
		    position:'top',
		    fixedLocation:true,
		    positionTracker:false,
		    multiple:true,
		    delay:0,
		    speed:0,
		    debug:false,
		    arrow:false,
		    theme:'tooltipster-default surveyTheme',
		    functionBefore: function(origin, continueTooltip) {

		        // we'll make this function asynchronous and allow the tooltip to go ahead and show the loading notification while fetching our data
		    	$(origin).tooltipster('content', "Loading...");
		        //console.log("get data" + content);
		    	continueTooltip();
		            $.ajax({
		                type: 'GET',
		                url: surveyUrl,
		                success: function(data) {
		                    $(origin).tooltipster('content', data);
		                	$(".surveyTheme").css("left", "30%");
		                	$(".surveyTheme").css("top", "30%");
		                }
		            });

		    },

		});


		$("#ev_survey").show();
		$("#ev_survey").tooltipster('show',null);
	}

}


//hid the popup and write a session cookie so it won't show again.
function hideTP(selector){
	$(selector).tooltipster("hide");
	$.cookie(selector, false);
}
