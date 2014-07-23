//{name:'cvTooltip', parent:".facet_cvHighlightFeature", direction:"right",msg:"Improve your search results by selecting term(s).<br/> <a href='http://www.google.com'>Learn more.</a>", autoShow:true, showImage:true},
tips = [
			{name:'ebookToolTip', parent:".ebookFeatureHighlight", direction:"right",msg:"As of April 2014, eBooks will only be available on <a href='http://www.sciencedirect.com' target='_blank'>ScienceDirect</a>.  <br/><a href='http://info.sciencedirect.com/referex' target='_blank'>Learn more</a>.", autoShow:true, showImage:true},


	];
$(document).ready(function() {
	var popupTime = 4500;


	for(i in tips){
		var tip = tips[i];
		var selector = '.'+tip.name;
		var msg = tip.msg;

		if(tip.showImage && $(tip.parent).length != 0){
			var img = '<img src="/static/images/ev_asterisk.gif" style="vertical-align:top;" width="13px" class="'+tip.name+'"/>';
			$(tip.parent).append(img);

		}

		if ( $.browser.msie && $.browser.version == 7) {
			$(selector).tooltipster({interactive:true, arrow:false});
			$(selector).tooltipster('reposition');
		}else{
			$(selector).tooltipster({interactive:true, position:tip.direction});
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
			e.preventDefault();
			var form = $("#resultsform");
			var displayformat='citation';
			var downloadurl = "/delivery/download/display.url?database="+form.find("input[name='database']").val()
			+"&displayformat="+displayformat
			+"&allselected=true";

			$(this).tooltipster({
			    content: 'Loading...',
			    autoClose:false,
			    interactive:true,
			    contentAsHTML:true,
			    position:'bottom',
			    fixedLocation:true,
			    positionTracker:false,
			    functionInit: function(origin, content) {

			        // we'll make this function asynchronous and allow the tooltip to go ahead and show the loading notification while fetching our data

			        //console.log("get data" + content);

			            $.ajax({
			                type: 'GET',
			                url: downloadurl,
			                success: function(data) {
			                	//console.log(origin);
			                    // update our tooltip content with our returned data and cache it

			                    $(origin).tooltipster('content', data);

			                }
			            });

			    }
			});
			$(this).tooltipster('show',null);

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
//hid the popup and write a session cookie so it won't show again.
function hideTP(selector){
	$(selector).tooltipster("hide");
	$.cookie(selector, false);
}
