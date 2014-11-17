//
// Google Analytics base library
//

var _gaq = _gaq || [];
(function() {
	var ga = document.createElement('script');
	ga.type = 'text/javascript';
	ga.async = true;
	ga.src = ('https:' == document.location.protocol ? 'https://ssl'
			: 'http://www')
			+ '.google-analytics.com/ga.js';
	var s = document.getElementsByTagName('script')[0];
	s.parentNode.insertBefore(ga, s);
})();

var GALIBRARY = GALIBRARY || (function() {
	var _initargs = {};
	var _pageevents = {};

	return {
		// The init function takes 2 sets of array arguments:
		// initargs =
		//   0 : GA Account Name
		//   1 : CS Account Name
		//   2 : Individual Auth (T/F)
		// pageevents =
		//   0-n : JSON objects represent events - [{ category:'category', action : 'action', label : 'label'}, ...]
		init : function(initargs, pageevents) {
			_initargs = initargs;
			_pageevents = pageevents;

			_gaq.push([ '_setAccount', _initargs[0] ]);
			_gaq.push([ '_setDomainName(".engineeringvillage.com")' ]);
			_gaq.push([ "_setCustomVar", 1, 'Account Name', _initargs[1], 3 ]);
			_gaq.push([ '_setCustomVar', 2, 'Individual User', _initargs[2], 3 ]);
			_gaq.push([ '_trackPageview', window.location.pathname ]);

			var pluginUrl = '//www.google-analytics.com/plugins/ga/inpage_linkid.js';
			_gaq.push([ '_require', 'inpage_linkid', pluginUrl ]);

			// Track page events
			for (var i = 0; i < _pageevents.length; i++) {
				this.createWebEventWithLabel(_pageevents[i].category, _pageevents[i].action, _pageevents[i].label);
			}

		},

		createWebEvent : function(category, action) {
			if (_gaq) {
				_gaq.push([ "_trackEvent", category, action, "" ]);
			}
		},

		createWebEventWithLabel : function(category, action, label) {
			if (_gaq) {
				_gaq.push([ "_trackEvent", category, action, label ]);
			}
		}

	};
}());

/*
 var _gaq = _gaq || [];
 _gaq.push(['_setAccount', '${actionBean.context.googleAnalyticsAccount}']);
 _gaq.push(['_setDomainName(".engineeringvillage.com")']);
 (function() {
 var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
 ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
 var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
 })();

 function createWebEvent(category,action){
 if(_gaq){
 _gaq.push(["_trackEvent",category, action,""]);
 }
 }

 function createWebEventWithLabel(category,action,label){
 if(_gaq){
 _gaq.push(["_trackEvent",category, action,label]);
 }
 }

 var page_load_start = new Date(${actionBean.starttime});
 window.onload = function() {
 var page_load_end = new Date();
 var load_time = page_load_end.getTime() - page_load_start.getTime();
 load_time = parseFloat( load_time / 1000 );
 var accntName = '${usersession.user.account.accountName}';
 var idividualUser = '${usersession.user.individuallyAuthenticated}';
 _gaq.push(["_setCustomVar",1,'Account Name',accntName,3]);
 _gaq.push(['_setCustomVar',2,'Individual User',idividualUser, 3]);
 _gaq.push(['_trackPageview', window.location.pathname]);

 // event not empty
 <c:forEach items="${webAnalyticsEvent}" var="webEvent">
 ${webEvent.webEvent}
 </c:forEach>

 // alert("start: " + page_load_start.toTimeString() + ", load: " +
 // page_load_end.toTimeString() + ", sent _setCustomVar: " +
 // load_time);
 }
 */