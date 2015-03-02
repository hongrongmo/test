//
// Google Analytics base library
//

(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	})(window,document,'script','//www.google-analytics.com/analytics.js','_gaq');


var GALIBRARY = GALIBRARY || (function() {
	var _initargs = {};
	var _pageevents = {};

	return {
		// The init function takes 2 sets of array arguments:
		// initargs =
		//   0 : GA Account Name
		//   1 : CS Account Name
		//   2 : Individual Auth (T/F)
		//   3 : Client ID
		//	 4 : Account ID
		// pageevents =
		
		init : function(initargs) {
			_initargs = initargs;

			_gaq('create', _initargs[0], {'clientId' : _initargs[3].replace(':',''), 'cookieDomain' : '.engineeringvillage.com'});
			_gaq('set', 'page', window.location.pathname);
			_gaq('set', 'dimension1', _initargs[1]);
			_gaq('set', 'dimension2', _initargs[2]);
			_gaq('set', 'dimension3', _initargs[4]);
			_gaq('send', 'pageview');
			_gaq('require','linkid');

		},

		createWebEvent : function(category, action) {
			if (_gaq) {
				_gaq('send', 'event', cagtegory, action);
			}
		},

		createWebEventWithLabel : function(category, action, label) {
			if (_gaq) {
				_gaq('send', 'event', category, action, label);
			}
		},
		createWebEventWithValue : function(category, action, label, value) {
			if (_gaq) {
				_gaq('send', 'event', cagtegory, action, label, value);
			}
		}
	};
}());

