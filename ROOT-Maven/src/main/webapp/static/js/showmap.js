// ****************************************************************************
// This file contains js code for the (beta) "Show Map" functionality.
// This feature is used with the GeoBase and GeoRef databases to plot
// results on a map.
// ****************************************************************************

//
// Variables and Constants
//
var EV_MAXZOOM = 10;
var EV_MAXMARKERCOUNT = 30;

var gmap = null;
var startLatLng = null;
var bounds = null;
var messages = {
	"show" : "Show Geographic Map",
	"hide" : "Hide Geographic Map"
};

var mapToggleAnchor = $("#mapToggleAnchor");
var mapcanvas = $("#map_canvas");
var divmap = $("#map");

//
// Some clients connect over a proxy so let's load Maps on demand.  This prevents
// an alert from popping up when their proxy domain doesn't match the API domain list.
//
var gmapsloaded = false;
var gmapsloaderror = false;

//
// Proxy pattern to override alert from Google API.  When detected, turn off Maps
// functionality
//
(function() {
	var proxied = window.alert;
	window.alert = function() {
		// do something here
		var test=true;
		if (arguments && arguments.length >=1 && arguments[0].match(/Google has disabled use of the Maps API for this application/g)) {
			mapToggleAnchor.attr('title', 'Show Map');
			mapToggleAnchor.text('Show Map');
			divmap.hide();
			gmapsloaderror = true;
		}
		return proxied.apply(this, arguments);
	};
})();

function mapsLoaded() {
	togglemap();
}

function loadMaps() {
	google.load("maps", "3", {
		"callback" : mapsLoaded,
		"other_params" : "key=AIzaSyCfQ_HSbcET25jn-cuT2Lz0CVycFnoGgZQ&sensor=false"
	});
}

//
//
// Initialize after DOM loaded
//
$(document).ready(function() {
	// Add click handler to toggle map display
	$("#mapToggleAnchor").click(function(event) {
		mapcanvas = $("#map_canvas");
		
		event.preventDefault();
		if (gmapsloaderror) return false;
		if (!gmapsloaded) {
			// Dynamically load maps
			var script = document.createElement("script");
			script.src = "https://www.google.com/jsapi?callback=loadMaps";
			script.type = "text/javascript";
			document.getElementsByTagName("head")[0].appendChild(script);
			gmapsloaded = true;
		} else {
			togglemap();
		}
	});
	
	// Add click handler to reset map
	$("#resetcenter").click(function(event) {
		event.preventDefault();
		resetCenterAndZoom();
	});
	
});

//
// Called to toggle the map display
//
function togglemap() {
	
	if (divmap.is(":hidden")) {
		mapToggleAnchor.attr('title', 'Hide Map');
		mapToggleAnchor.text('Hide Map');
		//
		// Populate the map with markers!
		//
		if(_gaq){
			GALIBRARY.createWebEvent("Query Toolbar", "Show Map","");
		}
		populatemap();
	} else {
		mapToggleAnchor.attr('title', 'Show Map');
		mapToggleAnchor.text('Show Map');
		divmap.hide();
	}
}

//
//Populate the map with coordinate values
//
function populatemap() {
	startLatLng = new google.maps.LatLng(0,0);
	bounds = new google.maps.LatLngBounds();

	mapcanvas.css("background", "#FFFFFF url(/static/images/waiting.gif) no-repeat");
	var milli = (new Date()).getTime();
	var searchid = $("#resultsform input[name='searchid']").val();
	var dbmask = $("#resultsform input[name='databaseid']").val();

	// Ajax call to get terms
	var request = $.ajax({
		dataType: "JSON",
		url: "/search/results/geoterms.url?searchid=" + searchid + "&dbmask=" + dbmask + "&timestamp=" + milli
	});
	
	request.fail(function() {
		alert("Unable to retrieve map points!");
		divmap.hide();
	});
	
	request.success(function(response) {
		mapcanvas.css('background',"none");
		
		// Initialize google map object!
	    gmap = new google.maps.Map(				
			mapcanvas[0],
			{
				center: startLatLng,
				zoom:1,
			    panControl: false,
			    zoomControl: true,
			    scaleControl: false,
			    maxZoom: EV_MAXZOOM,
			    minZoom: 1,
			    mapTypeId: google.maps.MapTypeId.ROADMAP
			}
		);

	    var places = eval(response);
		if (places.placemarks != undefined) {
			var markercount = ((places.placemarks.length < EV_MAXMARKERCOUNT) ? places.placemarks.length : EV_MAXMARKERCOUNT);
			for ( var i = 0; i < markercount; i++) {
				var point = new google.maps.LatLng(places.placemarks[i].point.lat,
						places.placemarks[i].point.lng);
				createMarker(point,
						places.placemarks[i].name,
						places.placemarks[i].description,
						places.placemarks[i].search);

				bounds.extend(point);
			}
			
			divmap.show();
			google.maps.event.trigger(gmap, 'resize');
			resetCenterAndZoom();

			if (markercount == 0){
				alert("No map points were returned for your search.");
				divmap.hide();
			}
		} else {
			alert("No map points were returned for your search.");
			divmap.hide();
		}
	});
	
}

//
//Create a marker for the map
//
function createMarker(point, name, description, searchurl) {
	var marker = new google.maps.Marker({
		position : point,
		map : gmap,
		title : description 
	});
	google.maps.event.addListener(marker, "click", function() {
		document.location = searchurl;
	});
	return marker;
}


//
// Misc functions
//
function resetCenterAndZoom() {
	if (bounds != null) {
		gmap.fitBounds(bounds);
		gmap.setCenter(bounds.getCenter());
	}
}

function isBrowserCompatible() {
	var support = $.support;
	if (support) {
		return true;
	}
	return false;
}

