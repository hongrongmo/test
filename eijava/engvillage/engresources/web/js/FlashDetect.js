<!--

var flash2Installed = false;    // boolean. true if flash 2 is installed
var flash3Installed = false;    // boolean. true if flash 3 is installed
var flash4Installed = false;    // boolean. true if flash 4 is installed
var flash5Installed = false;    // boolean. true if flash 5 is installed
var flash6Installed = false;    // boolean. true if flash 6 is installed
var maxVersion = 6;             // highest version we can actually detect
var actualVersion = 0;          // version the user really has

// Check the browser...we're looking for ie/win
var isIE = (navigator.appVersion.indexOf("MSIE") != -1) ? true : false;    // true if we're on ie
var isWin = (navigator.appVersion.indexOf("Windows") != -1) ? true : false; // true if we're on windows


// Write vbscript detection on ie win. IE on Windows doesn't support regular
// JavaScript plugins array detection.
if(isIE && isWin){
  document.write('<SCR' + 'IPT LANGUAGE=VBScript\> \n');
  document.write('on error resume next \n');
  document.write('flash2Installed = (IsObject(CreateObject("ShockwaveFlash.ShockwaveFlash.2"))) \n');
  document.write('flash3Installed = (IsObject(CreateObject("ShockwaveFlash.ShockwaveFlash.3"))) \n');
  document.write('flash4Installed = (IsObject(CreateObject("ShockwaveFlash.ShockwaveFlash.4"))) \n');
  document.write('flash5Installed = (IsObject(CreateObject("ShockwaveFlash.ShockwaveFlash.5"))) \n');
  document.write('flash6Installed = (IsObject(CreateObject("ShockwaveFlash.ShockwaveFlash.6"))) \n');
  document.write('</SCR' + 'IPT\> \n'); // break up end tag so it doesn't end our script
}

function detectFlash() {
  // If navigator.plugins exists...
  if (navigator.plugins) {
    // ...then check for flash 2 or flash 3+.
    if (navigator.plugins["Shockwave Flash 2.0"] || navigator.plugins["Shockwave Flash"]) {

      // Some version of Flash was found. Time to figure out which.

      // Set convenient references to flash 2 and the plugin description.
      var isVersion2 = navigator.plugins["Shockwave Flash 2.0"] ? " 2.0" : "";
      var flashDescription = navigator.plugins["Shockwave Flash" + isVersion2].description;

      var flashVersion = parseInt(flashDescription.charAt(flashDescription.indexOf(".") - 1));

      // We found the version, now set appropriate version flags. Make sure
      // to use >= on the highest version so we don't prevent future version
      // users from entering the site.
      flash2Installed = flashVersion == 2;
      flash3Installed = flashVersion == 3;
      flash4Installed = flashVersion == 4;
      flash5Installed = flashVersion == 5;
      flash6Installed = flashVersion >= 6;
    }
  }

  // Loop through all versions we're checking, and
  // set actualVersion to highest detected version.
  for (var i = 2; i <= maxVersion; i++) {
    if (eval("flash" + i + "Installed") == true) actualVersion = i;
  }

  if(navigator.userAgent.indexOf("WebTV") != -1) actualVersion = 3;

	if(typeof(document.quicksearch.FlashInstalled) != 'undefined') {
		document.quicksearch.FlashInstalled.value =  (actualVersion != 0);
		document.quicksearch.FlashVersion.value = actualVersion;
	}
//	alert(document.quicksearch.FlashInstalled.value);
//	alert(document.quicksearch.FlashVersion.value);

}  // end detectFlash()


// call our detector now that it's safely loaded.
// detectFlash();

// -->