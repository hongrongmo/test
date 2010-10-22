// define the unique ID
var dateObj = new Date();
var UniqueIDString = "" + dateObj.getTime();

// Load Status info
var gsStatusText = "Loading"; // WH_CUR_LNG.Loading
var gsStatusTitle = "Status";
var gsStatusFont = "";
var gsStatusColor = "";
var gsStatusBarColor = "";
var gnStatusWidth = 310;
var gnStatusHeight = 150;
var gsStatusSwf = "";

var gsUrlParams = "";

if (location.hash.length > 1)
{
	gsUrlParams = location.hash;
}
else
{
	gsUrlParams = location.search;
	gsUrlParams = gsUrlParams.substring(1);
	gsUrlParams = "#" + gsUrlParams;
}

function OpenStatus()
{
	if (gsStatusSwf != "" && !gbIE6)
	{
		var CurY = self.screenY;
		var CurX = self.screenX;
		var CurWidth = window.outerWidth;
		var CurHeight = window.outerHeight;
		var nLeft = (CurWidth-gnStatusWidth)/2 + CurX;
		var nTop = (CurHeight-gnStatusHeight)/2 + CurY;

		var width = gnStatusWidth;
		var height = gnStatusHeight;

		if (gbIE5)
		{
			height -= 10;
		}

		nLeft = parseInt(nLeft,10);
		nTop = parseInt(nTop,10);

		var param_str = "uniqueHelpID=" + UniqueIDString;
		param_str += "&gsStatusText=" + gsStatusText;
		param_str += "&gsStatusFont=" + gsStatusFont;
		param_str += "&gsStatusColor=" + gsStatusColor;
		param_str += "&gsStatusBarColor=" + gsStatusBarColor;
		param_str += "&gsStatusSwf=" + gsStatusSwf;

		param_str = escapeChar(param_str);
	
		if ((gbIE5 && gbWindows) && (!gbOpera)) {
			height += 30;
			var strWindow = 'dialogWidth:' + width + 'px;dialogHeight:' + height + 'px;resizable:no;status:no;scroll:no;help:no;center:yes;';
			gPoweredByWindow = window.showModelessDialog("wf_status.htm", param_str, strWindow);

		} else if (gbNav4 && !gbNav6) {
			// For NS 4, we need to use setTimeout. After changing to use fscommand to communicate with NS 4 from Flash,
			// NS 4 will not perform a window.open of an HTML file during the processing of an fscommand.
			var strURL = "wf_status.htm?" + param_str;
			var strWindow = 'width=' + width + ',height=' + height + ',left=' + nLeft + ',top=' + nTop;
			setTimeout("window.open('" + strURL + "', 'StatusWindow', '" + strWindow + "')", 100);
				
		} else if (gbIE4 && !gbIE5) {
			// Create the window string and add the parameters to the filename
			var strURL = "wf_status.htm#" + param_str;
			var strWindow = 'width=' + width + ',height=' + height + ',left=' + nLeft + ',top=' + nTop;
			gPoweredByWindow = window.open(strURL, 'StatusWindow', strWindow);
		} else {
			// Create the window string and add the parameters to the filename
			var strURL = "wf_status.htm?" + param_str;
			var strWindow = 'width=' + width + ',height=' + height + ',left=' + parseInt(nLeft) + ',top=' + parseInt(nTop);
			gPoweredByWindow = window.open(strURL, 'StatusWindow', strWindow);
		}
	}
}



// Variables used in the file
var gsDefaultFramesetCol = "230, *";
var gsDefaultFramesetRow = "200, *";
// Set the variable for test scenario
if (gsDefaultFramesetRow.substr(0, 2) == "%%") {
	gsDefaultFramesetRow = "300, *";
}
if (gsDefaultFramesetCol.substr(0, 2) == "%%") {
	gsDefaultFramesetCol = "200, *";
}

var gsLastFramesetCol = gsDefaultFramesetCol;
var gsCurFramesetCol = gsDefaultFramesetCol;
var gsLastFramesetRow = gsDefaultFramesetRow;
var gsCurFramesetRow = gsDefaultFramesetRow;
var gbVertical = false;
var gsBrowseSequenceInfo = "";
var gsTOCSyncInfo = "";
var gPoweredByWindow = null;
var gnTopicBGColor = 16777215;
var gbSkinLoaded = false;
var gnScrollbarTimerID = -1;
var gnLastScrollbarWidthRequest = 0;
var gnScrollbarDragOffset = 0;
var gnInitialFrameWidth = 0;
var gnInitialXPos = 0;
var IE_FRAMESET_RESIZE_INTERVAL = 300;
var gsFlashVars="";
var gbNavReloaded=false;
var gbTbReloaded=false;
var gbReloadNavigationOnly = false;
var gbNavPaneLoaded = false;
var gbToolBarLoaded = false;
var gbHasTitle = false;
var gsNavPane = "wf_blank.htm";
var gsParamPasser = "wf_blank.htm";
var gFrameInterval = null;
var gsProjectFolder = "";
var gsProject = "";
var gsServerLocation = "";
var gbFHPro = true;

var gbDefSet = false;
var gnOpts = -1;
var gbTBSet = false;

//const
var CMD_SHOWTOC=1;
var CMD_SHOWINDEX=2;
var CMD_SHOWSEARCH=3;
var CMD_SHOWGLOSSARY=4;

// pane options
var PANE_OPT_SEARCH = 0x01;
var PANE_OPT_BROWSESEQ = 0x02;

function GetServerLocation()
{
	var strHost = document.location.host.toString();
	var strProtocol = document.location.protocol.toString();
	if (strHost == "" || strHost == null || strProtocol =="file:")
	{
		gbFHPro = false;
	}
	else if (gsServerLocation.length == 0)
	{
		gsServerLocation = document.location.href;
		var nPos = gsServerLocation.indexOf(strHost);
		if (nPos >= 0)
		{
			nPos += strHost.length;
			var nloc = gsServerLocation.indexOf("\\",nPos)
			if (nloc >=0)
			{
				nPos = nloc;
			}
			nloc = gsServerLocation.indexOf("/",nPos)
			if (nloc >=0)
			{
				nPos = nloc;
			}
			gsServerLocation = gsServerLocation.substring(0,nPos);
		}	
	}
}

function GetProjectName()
{
	var strProjectName = gsUrlParams
	var nPos =-1;

	if (strProjectName =="")
	{
		strProjectName = document.location.search
	}
	if (strProjectName.charAt(0) == '#' || strProjectName.charAt(0) == '?')
	{
		strProjectName = strProjectName.substring(1, strProjectName.length);
	}

	nPos = strProjectName.indexOf("prj=");
	if (nPos != -1)
	{
		GetServerLocation();
		strProjectName = strProjectName.substr(nPos+ 4, strProjectName.length);
		gsProject = strProjectName;
	}
	else
	{
		var strLocation = document.location.href;
		nPos = strLocation.length-4
		if ((strLocation.substr(nPos, 4)) == ".htm")
		{
			gbFHPro = false;
		}
	}
}

GetProjectName();

function CheckFrameRendered()
{
	clearInterval(gFrameInterval);
	gFrameInterval = setTimeout("RerenderFrames()",2000);
}

function RerenderFrames()
{
	var bRerender = true;
	if (window.frames["Content"])
	{
		if (window.frames["Content"].frames)
		{
			if (window.frames["Content"].frames["master"])
			{
				if (window.frames["Content"].frames["master"].DoCommand)
				{
					if (window.frames["comm"])
					{
						bRerender = false;
					}
				}
			}
		}
	}
	if (!bRerender && !window.frames["Content"].frames["master"].gbSWFLoaded && (gbNavPaneLoaded || gbToolBarLoaded))
	{
		bRerender = true;
	}

	if (bRerender)
	{
		window.location.reload();
	}
}

// Funtion to send a command to the master HTML file
function SendCmdToMasterHTML(cmd, param1, param2) {
	if ((window.frames["Content"] != null) && 
	    (window.frames["Content"].frames["master"] != null) &&
	    (window.frames["Content"].frames["master"].DoCommand != null)) 
	{
		window.frames["Content"].frames["master"].DoCommand(cmd, param1, param2);
	}
}

function NavigateToTopic(url, target) {
	target = target + "";
	if ((window.frames["Content"].frames["bottom"] != null) && (window.frames["Content"].frames["bottom"].frames["Topic Pane"] != null))
	{
		if (target == "" || target == null || target == "0")
		{
			if (gbNav4 && !gbNav6) 
			{
				// For NS 4, we need to use setTimeout. After changing to use fscommand to communicate with NS 4 from Flash,
				// NS 4 will not perform a change in the URL
				setTimeout("window.frames['Content'].frames['bottom'].frames['Topic Pane'].location='" + url + "'", 100);
				
			} 
			else 
			{
				window.frames["Content"].frames["bottom"].frames["Topic Pane"].location = url;
			}
		}
		else
		{
			if (gbNav4 && !gbNav6) 
			{
				setTimeout("window.open('" + url + "', '" + target + "','')",100);
			}
			else
			{
				window.open(url,target,"");
			}
		}
	}
}

function getElement(elementID) {
	var element = null;
	// See if the browser supports the functions we need to get to the element
	if (document.getElementById) {
		element = document.getElementById(elementID);
	} else if (document.all) {
		element = document.all(elementID);
	}
	return element;
}

function getBottomElement(elementID) {
	var element = null;
	// See if the browser supports the functions we need to get to the element
	if (window.frames["Content"].frames["bottom"].document.getElementById) {
		element = window.frames["Content"].frames["bottom"].document.getElementById(elementID);
	} else if (window.frames["Content"].frames["bottom"].document.all) {
		element = window.frames["Content"].frames["bottom"].document.all(elementID);
	}
	return element;
}

function ShowNavigationPane(bShow) {

	// Get to the frameset element
	var frameset = getBottomElement("Bottom Frame");

	// Show or hide as appropriate
	if (bShow) {
		if (gbVertical) {
			if (gbNav4 || gbOpera || gbKonqueror || gbSafari) {
				gsCurFramesetRow = gsLastFramesetRow;
				RedoBottomFrame();
			} else if (gbIE) {
				frameset.rows = gsLastFramesetRow;
			}
		} else {
			if (gbNav4 || gbOpera || gbKonqueror || gbSafari) {
				gsCurFramesetCol = gsLastFramesetCol;
				RedoBottomFrame();
			} else if (gbIE) {
				frameset.cols = gsLastFramesetCol;
			}
		}
	} else {
		// "frameset.cols" contains a string of the frameset columns
		// You can get and set this value for Netscape but it doesn't re-render
		// the pages so it looks like it has no effect.
		if (gbVertical) {
			if (gbNav4 || gbOpera || gbKonqueror || gbSafari) {
				// For some reason, frameset.rows doesn't work list frameset.cols. So, let's use innerHeight instead.
				gsLastFramesetRow = "" + window.frames["Content"].frames["bottom"].frames["Navigation Pane"].innerHeight + ", *";
				gsCurFramesetRow = "0, *";
				RedoBottomFrame();
			} else if (gbIE) {
				window.frames["Content"].frames["bottom"].frames["Navigation Pane"].gbNavClosed = true;
				if (gbIE6)
				{
					gsLastFramesetRow = frameset.rows;
				}
				else
				{
					gsLastFramesetRow = "" + window.frames["Content"].frames["bottom"].frames["Navigation Pane"].document.body.clientHeight + ", *";
				}
				frameset.rows = "0, *";
			}
		} else {
		
			if (gbNav4 || gbOpera || gbKonqueror || gbSafari) {
				gsLastFramesetCol = gsCurFramesetCol;
				gsCurFramesetCol = "0, *";
				RedoBottomFrame();
			} else if (gbIE) {
				gsLastFramesetCol = frameset.cols;
				frameset.cols = "0, *";
			}
		}
	}
	
	return;
}

function PrintCurrent() {
	// Give the topic pane the focus and print
	if (gbNav) {
		window.frames["Content"].frames["bottom"].frames["Topic Pane"].frames["TopicFrame"].print();
	} else {
		window.frames["Content"].frames["bottom"].frames["Topic Pane"].frames["TopicFrame"].focus();
		window.print();
	}
}

function RedoBottomFrame()
{
	SendCmdToMasterHTML("CmdGetTopicAddress");
}

function CleanNavPane()
{
	if (gbNavPaneLoaded)
	{
		SendCmdToMasterHTML("CmdNavigationCleanup");
	}
	else
	{
		DoCommand("CmdNavigationCleanupComplete");
	}
}

function ReloadNavigation() {
	gbReloadNavigationOnly = true;
	RedoBottomFrame();
}

function SetVertical(bVertical) {
	gbVertical = bVertical;
}

//////////////////////////////////////////////////////////////////////////////
//
// Frame resizing code
//
// We only want to try to resize the framesets every so often or the browsers
// will fall behind. A timer is set up to achieve this purpose and only the
// last requested size is used when resizing.
//
//////////////////////////////////////////////////////////////////////////////

function ScrollbarDragStart(x, y) {

	// Calculate how far the mouse is from the edge of the frameset at the beginning
	if (gbNav4) {
		gnInitialFrameWidth = window.frames["Content"].frames["bottom"].frames["Navigation Pane"].window.innerWidth;
	} else {
		gnInitialFrameWidth = window.frames["Content"].frames["bottom"].frames["Navigation Pane"].document.body.clientWidth;
	}
	gnInitialXPos = x;
	gnScrollbarDragOffset = gnInitialFrameWidth - x;
	
	// Record the position
	gnLastScrollbarWidthRequest = x;
	
	// Make an initial call to "resize" the frameset
	ResizeFrameset();

	// Start a timer that will regulate how often the frameset is "resized"
	gnScrollbarTimerID = setInterval("ResizeFrameset()", IE_FRAMESET_RESIZE_INTERVAL);
}


function ScrollbarDragStop(x, y) {
	clearInterval(gnScrollbarTimerID);

	// Limit the resize down to zero
	if (x < 0) {
		x = 0;
	}
	
	gnLastScrollbarWidthRequest = x;	

	ResizeFrameset();
	
	// Resize the framesets where necessary
	if (gbNav4 || gbOpera || gbKonqueror || gbSafari) {
		if (gnInitialXPos != x){
			gsCurFramesetCol = (x+gnScrollbarDragOffset) + ", *";
		}
		RedoBottomFrame();
	}
}

function ResizeFrameset() {
	if ((gbIE) && (!gbOpera)) {
		// Resize the frames
		var frameWidth = gnLastScrollbarWidthRequest + gnScrollbarDragOffset;
		var frameset = getBottomElement("Bottom Frame");
		frameset.cols = "" + frameWidth + ", *";
	}
}

function ScrollbarDrag(x, y) {
	gnLastScrollbarWidthRequest = x;
}


function IsHideNavEnabledForRemoteURL() {
	if (gbIE) {
		return true;
	} else {
		return false;
	}
}

function IsHideNavEnabled() {
	// We should always be able to resize and thus hide the navpane
	return true;
}


function DoCommand(cmd, param1, param2) {
	switch (cmd) {
		case "CmdCurrentNav":
			gsDefaultTab = param1;
			break;
			
		case "CmdSkinSwfLoaded":
			gbSkinLoaded = true;
			break;

		case "CmdTopicIsLoaded":
		case "CmdTopicDisplayed":
		case "CmdTopicUnloaded":
			SendCmdToMasterHTML(cmd, param1, param2);
			break;
			
		case "CmdSyncTOC":
			// Store the information in case it is requested later
			gsTOCSyncInfo = param1;

			// Send it off
			SendCmdToMasterHTML(cmd, param1, param2);
			break;
			
		case "CmdSyncInfo":
			// Store the information in case it is requested later
			gsTOCSyncInfo = param1;
			break;

		case "CmdBrowseSequenceInfo":
			// Store the information in case it is requested later
			gsBrowseSequenceInfo = param1;
			
			// Send it off
			SendCmdToMasterHTML(cmd, param1, param2);
			break;
			
		case "CmdGetBrowseSequenceInfo":
			// Send it off
			SendCmdToMasterHTML("CmdBrowseSequenceInfo", gsBrowseSequenceInfo);
			break;

		case "CmdTopicBGColor":
			// Store the information in case it is requested later and send it off
			gnTopicBGColor = param1;
			SendCmdToMasterHTML(cmd, param1, param2);
			break;

		case "CmdGetTopicBGColor":
			SendCmdToMasterHTML("CmdTopicBGColor", gnTopicBGColor);
			break;
								
		case "CmdGetSkinFilename":
			SendCmdToMasterHTML("CmdSkinFilename", gsActiveSkinFileName);
			break;

		case "CmdDisplayTopic":
			// Navigate to the proper HTML file
			NavigateToTopic(param1, param2);
			break;

		case "CmdHideNavigation":
			ShowNavigationPane(false);
			break;
	
		case "CmdGetToolbarOrder":
			gbToolBarLoaded = true;
			CheckForPrint();
			SendCmdToMasterHTML("CmdToolbarOrder", gsToolbarOrder);
			break;

		case "CmdGetNavbarOrder":
			SendCmdToMasterHTML("CmdNavbarOrder", gsNavigationElement);
			break;

		case "CmdShowNavigation":
			ShowNavigationPane(true);
			break;
		
		case "CmdGetSyncTOC":
			SendCmdToMasterHTML("CmdSyncTOC", gsTOCSyncInfo, 0);
			break;
		
		case "CmdIsAutoSyncTOCEnabled":
			SendCmdToMasterHTML("CmdAutoSyncTOCEnabled", gbAutoSync);
			break;
			
		case "CmdGetDefaultNav":
			SendCmdToMasterHTML("CmdSelectItem", gsDefaultTab, param1);
			break;
		
		case "CmdSetFocus":
		
			if (param1 == "Navigation") {
				if (window.frames["Content"].frames["bottom"].frames["Navigation Pane"])
				{
					if (window.frames["Content"].frames["bottom"].frames["Navigation Pane"].navpaneSWF) {
						if (window.frames["Content"].frames["bottom"].frames["Navigation Pane"].navpaneSWF.focus)
						{
							window.frames["Content"].frames["bottom"].frames["Navigation Pane"].navpaneSWF.focus();
						}
					}
				}
			} else if (param1 == "Topic") {
				window.frames["Content"].frames["bottom"].frames["Topic Pane"].focus();
			}
			break;
		
		case "CmdPrint":
			PrintCurrent();
			break;
									
		case "CmdInvokePoweredBy":

			// Create a centered window
			var left = (screen.width / 2) - 200;
			var top = (screen.height / 2) - 200;
			var width = 300;
			var height = 200;
			if ((gbNav) && (gbNav4) && (!gbNav6)) {
				width += 10;
				height += 10;
			}

			// Open the window if it is not already open
			if ((gPoweredByWindow == null) || (gPoweredByWindow.closed == true)) {
			
				// Create the parameter list
				var param_str = "uniqueHelpID=" + UniqueID();
				param_str += "&pbpoweredby=" + gsFlashHelpVersion;
				param_str += "&pbgeneratedby=" + gsGeneratorVersion;
				param_str += "&pbaboutlabel="+param1;
				
				// Take different action depending on the browser
				if ((gbIE5 && gbWindows) && (!gbOpera)) {
					height += 30;
					var strWindow = 'dialogWidth:' + width + 'px;dialogHeight:' + height + 'px;resizable:no;status:no;scroll:no;help:no;center:yes;';
					gPoweredByWindow = window.showModelessDialog("wf_poweredby.htm", param_str, strWindow);

				} else if (gbNav4 && !gbNav6) {
				
					// For NS 4, we need to use setTimeout. After changing to use fscommand to communicate with NS 4 from Flash,
					// NS 4 will not perform a window.open of an HTML file during the processing of an fscommand.
					var strURL = "wf_poweredby.htm?" + param_str;
					var strWindow = 'width=' + width + ',height=' + height + ',left=' + left + ',top=' + top;
					setTimeout("window.open('" + strURL + "', 'PoweredByWindow', '" + strWindow + "')", 100);
					
				} else if (gbIE4 && !gbIE5) {

					// Create the window string and add the parameters to the filename
					var strURL = "wf_poweredby.htm#" + param_str;
					var strWindow = 'width=' + width + ',height=' + height + ',left=' + left + ',top=' + top;
					gPoweredByWindow = window.open(strURL, 'PoweredByWindow', strWindow);
				} else {
					// Create the window string and add the parameters to the filename
					var strURL = "wf_poweredby.htm?" + param_str;
					var strWindow = 'width=' + width + ',height=' + height + ',left=' + left + ',top=' + top;
					gPoweredByWindow = window.open(strURL, 'PoweredByWindow', strWindow);
				}
			} else if (gPoweredByWindow != null) {
				gPoweredByWindow.focus();
			}
			break;
			
		case "CmdAskIsTopicOnly":
			param1.isTopicOnly=false;
			break;
			
		case "CmdScrollbarDragStart":
			ScrollbarDragStart(Number(param1), Number(param2));
			break;
			
		case "CmdScrollbarDragStop":
			ScrollbarDragStop(Number(param1), Number(param2));
			break;
			
		case "CmdScrollbarDragMove":
			ScrollbarDrag(Number(param1), Number(param2));
			break;
		
		case "CmdIsNavResizeVisible":
			SendCmdToMasterHTML("CmdNavResizeVisible", 	(gbNav4 || gbOpera) ? 1 : 0);
			break;

		case "CmdTopicAddress":
			gsTopic = param1;
			CleanNavPane();
			break;

		case "CmdNavigationCleanupComplete":
			// Cleanup finished - we can now reload
			gbNavPaneLoaded = false;
			if (gbReloadNavigationOnly) {
				window.frames["Content"].frames["bottom"].frames["Navigation Pane"].window.location.reload();
				gbReloadNavigationOnly = false;
			} else {
				window.frames["Content"].frames["bottom"].window.location.reload();
			}
			break;
		case "CmdNavigationPaneLoaded":
			gbNavPaneLoaded = true;
			break;			
		case "CmdUILoadFailure":
			// Inform the user and load the plain HTML version of the help system
			alert(param1 + " failed to load the following components:\n" + param2 + "\nThe non-Flash version of the help system will be displayed.");
			window.location.replace("wf_njs.htm");
			break;
		
		case "CmdReloadNavigation":
			ReloadNavigation();
			break;

		case "CmdCanFocusBeSet":
			SendCmdToMasterHTML("CmdFocusCanBeSet", ((gbIE && !gbMac) ? true : false));
			break;
		
		case "CmdIsHideNavEnabled":
			SendCmdToMasterHTML("CmdHideNavEnabled", IsHideNavEnabled());
			break;
		
		case "CmdIsHideNavEnabledForRemoteURL":
			SendCmdToMasterHTML("CmdHideNavEnabledForRemoteURL", IsHideNavEnabledForRemoteURL());
			break;
		
		default:
			// See if this is a debug command and send it along to the master
			if (cmd.substr(0, 6) == "debug:") {
				SendCmdToMasterHTML(cmd, param1, param2);
			}
			break;
	}
}

// Create a unique string to ID this instance of the help system
function UniqueID() {
	return UniqueIDString;
}

// Send the first command - setting the skin filename to the master
function Loaded() {
	// Reset the CurFrameset Col & Row because opera will not do this on reload
	gsCurFramesetCol = gsDefaultFramesetCol;
	gsCurFramesetRow = gsDefaultFramesetRow

	DoCommand("CmdSetFocus","Navigation");
}

var gnPans=2;
var gsTopic="Engineering_Village_Overview.htm";

var gsAutoSync="1";
var gbAutoSync = (gsAutoSync == "1") ? 1 : 0;

var gsNavigationElement="Previous|Next|Hide";
var gsToolbarOrder="Contents|Index|Search|Glossary||SearchInput|Logo/Author";
var gsDefaultTab="Contents";
var gsActiveSkinFileName="flashhelp_default.xml";
var gsLowCaseFileName="0";
var gsFlashHelpVersion="FlashHelp%201.5";
var gsGeneratorVersion="Adobe%20RoboHelp%206.0";
var gsResFileName="wfres.xml";
var gsToolbarPaneHeight="50";

// Set a default topic for testing
if (gsTopic.substr(0, 2) == "%%" && gsActiveSkinFileName.substr(0, 2) == "%%") 
{
	gbAutoSync = 1;
	gsNavigationElement="Previous|Next|Sync TOC|Hide";
	gsToolbarOrder="Contents|Index|Search|Glossary|Print|SearchInput|Logo/Author";
	gsFlashHelpVersion="FlashHelp 5.5";
	gsGeneratorVersion="RoboHelp XX";
	gsTopic = "Topics/New_Topic15.htm"; //"wf_topic.htm";
	gsToolbarPaneHeight = "50";
	gsActiveSkinFileName = "skin.xml";
	gsDefaultTab="Index";
}

// Don't show the navigation pane if there is nothing to show
if ((gsToolbarOrder.substr(0, 2) != "%%") &&
	(gsToolbarOrder.indexOf('Contents') == -1) &&
	(gsToolbarOrder.indexOf('Index') == -1) &&
	(gsToolbarOrder.indexOf('Search') == -1) &&
	(gsToolbarOrder.indexOf('Glossary') == -1)) {
	
	if (gbVertical) {
		gsCurFramesetRow = "0, *";
	} else {
		gsCurFramesetCol = "0, *";
	}
}

// Used to contruct the URL to load the topic pane
function GetTopicUrl()
{
	var strResult = "";
	strResult += gsProjectFolder

	// Use a hash value for all browsers that cannot support a search string properly
	if (gbOpera || (gbMac && gbNav7))
	{
		strResult += "wf_topicfs.htm#";
	}
	else
	{
		strResult += "wf_topicfs.htm?";
	}

	strResult += "HID=" + UniqueIDString;
	strResult += "&Topic=" + gsTopic;

	return strResult;
}

// Remove the "print" button as a possibility if the print function is not supported
function CheckForPrint() {
	if (!window.print || (gbMac && (gbNav6 && !gbNav7)) || gbOpera) {
		var iPrint = gsToolbarOrder.indexOf("Print");
		if (iPrint != -1) {
			var sNewOrder = gsToolbarOrder.substr(0, iPrint);
			if (gsToolbarOrder.length > iPrint + 5) {
				sNewOrder += gsToolbarOrder.substr(iPrint + 6, gsToolbarOrder.length);
			}
			gsToolbarOrder = sNewOrder;
		}
	}
}

function escapeChar(in_str)
{
	var out_str = in_str;

	out_str = replaceChar(out_str,'%');
	out_str = replaceChar(out_str,'\'');
	out_str = replaceChar(out_str,'&');
	out_str = replaceChar(out_str,'+');
	out_str = replaceChar(out_str,' ');
	out_str = replaceChar(out_str,'#');

	return out_str;
}

function dec2hex(dec_num)
{
	// This function will convert a dec number <= 255 to a hex string
	var hex_str = "";
	var hexArray = new Array("0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F");
	hex_str += hexArray[Math.floor(dec_num/16)];
	hex_str += hexArray[dec_num%16];
	return hex_str;	
}

function replaceChar(in_str, sChar)
{
	var out_str = in_str;
	var temp_str = "";
	var nOldIndex=0;
	var nIndex = out_str.indexOf(sChar);
	while (nIndex >= 0)
	{
		temp_str = out_str.substring(0,nIndex);
		temp_str +="%" + dec2hex(sChar.charCodeAt(0)) ;
		temp_str +=out_str.substring(nIndex+1);
		out_str = temp_str;
		nOldIndex = nIndex;
		nIndex = out_str.indexOf(sChar, nOldIndex+1);
	}
	return out_str;
}

	
if (gsUrlParams.length > 1)
{
	if (gsUrlParams.indexOf("#<") == 0)
	{
		document.location = "whcsh_home.htm#" + gsUrlParams.substring(2);
	}
	else if (gsUrlParams.indexOf("#>>") == 0)
	{
		parseParam(gsUrlParams.substring(3));
		gsUrlParams = "#" + gsTopic + gsUrlParams.substring(1);
	}
	else
	{
		var nPos = gsUrlParams.indexOf(">>");
		if (nPos>1)
		{
			gsTopic = gsUrlParams.substring(1, nPos);
			parseParam(gsUrlParams.substring(nPos+2));
		}
		else
			gsTopic = gsUrlParams.substring(1);
	}

	if (gnPans == 1 && gsTopic)
	{
		var strURL=location.href;
		if (gsUrlParams)
		{
			var nPos=location.href.indexOf("#");
			if (nPos < 0)
			{
				nPos=location.href.indexOf("?");
			}
			strURL=strURL.substring(0, nPos);
		}
		if (gbHasTitle)
			document.location=_getPath(strURL)+ "whskin_tw.htm" + gsUrlParams;
		else
			document.location=_getPath(strURL)+ gsTopic;
	}
}

function parseParam(sParam)
{
	if (sParam)
	{
		var nBPos=0;
		do 
		{
			var nPos=sParam.indexOf(">>", nBPos);
			if (nPos!=-1)
			{
				if (nPos>0)
				{
					var sPart=sParam.substring(nBPos, nPos);
					parsePart(sPart);
				}
				nBPos = nPos + 2;
			}
			else
			{
				var sPart=sParam.substring(nBPos);
				parsePart(sPart);
				break;
			}
		} while(nBPos < sParam.length);

		if (gnOpts != -1)
		{
			if ((PANE_OPT_SEARCH & gnOpts) && gbTBSet)
			{
				gsToolbarOrder+="|SearchInput";
			}
			if (PANE_OPT_BROWSESEQ & gnOpts)
			{
				// Add the browse sequence btns
				if (gsNavigationElement.indexOf("Previous|Next") < 0)
				{
					if (gsNavigationElement.length > 0)
						gsNavigationElement = "|" + gsNavigationElement;
					gsNavigationElement = "Previous|Next" + gsNavigationElement;
				}
			}
			else
			{
				// Remove the previous and next buttons
				var nLoc = gsNavigationElement.indexOf("Previous|Next");
				if (nLoc >= 0)
				{
					gsNavigationElement = gsNavigationElement.substr(13);
					if (gsNavigationElement.indexOf("|")==0)
					{
						gsNavigationElement = gsNavigationElement.substr(1);
					}
				}
			}
		}
		if (gbTBSet)
		{
			gsToolbarOrder+="|Logo/Author";
		}
	}	
}

function parsePart(sPart)
{
	if(sPart.toLowerCase().indexOf("cmd=")==0)
	{
		gbDefSet = true;
		gnCmd=parseInt(sPart.substring(4));
		if(gnCmd == CMD_SHOWTOC)
			gsDefaultTab="Contents";
		else if(gnCmd == CMD_SHOWINDEX)
			gsDefaultTab="Index";
		else if(gnCmd == CMD_SHOWSEARCH)
			gsDefaultTab="Search";
		else if(gnCmd == CMD_SHOWGLOSSARY)
			gsDefaultTab="Glossary";
		else
			gbDefSet = false;
	}
	else if(sPart.toLowerCase().indexOf("svr=")==0)
	{
		gbFHPro = true;
		gsServerLocation = sPart.substring(4);
	}
	else if(sPart.toLowerCase().indexOf("prj=")==0)
	{
		gsProject = sPart.substring(4);
	} 
	else if(sPart.toLowerCase().indexOf("cap=")==0)
	{
		document.title=_browserStringToText(sPart.substring(4));
		gbHasTitle=true;
	}
	else if(sPart.toLowerCase().indexOf("pan=")==0)
	{
		gnPans=parseInt(sPart.substring(4));
	}
	else if(sPart.toLowerCase().indexOf("pot=")==0)
	{
		gnOpts=parseInt(sPart.substring(4));
	}
	else if(sPart.toLowerCase().indexOf("pbs=")==0)
	{
		var sRawBtns = sPart.substring(4);
		var aBtns = sRawBtns.split("|");
		for (var i=0;i<aBtns.length;i++)
		{
			aBtns[i] = transferAgentNameToPaneName(aBtns[i]);
		}
		gbTBSet = true;
		gsToolbarOrder = aBtns.join("|");
	}
	else if(sPart.toLowerCase().indexOf("pdb=")==0)
	{
		if (!gbDefSet)
		{
			gsDefaultTab=transferAgentNameToPaneName(sPart.substring(4));
		}
	}
	else if(sPart.toLowerCase().indexOf("url=")==0)
	{
		gsTopic = sPart.substring(4);
	}
}

function transferAgentNameToPaneName(sAgentName)
{
	if (sAgentName =="toc")
		return "Contents";
	else if	(sAgentName =="ndx")
		return "Index";
	else if	(sAgentName =="nls")
		return "Search";
	else if	(sAgentName =="gls")
		return "Glossary";
	else if	(sAgentName =="prt")
		return "Print";
	else if (sAgentName =="sif")
		return "SearchInput";
	return "";
}
