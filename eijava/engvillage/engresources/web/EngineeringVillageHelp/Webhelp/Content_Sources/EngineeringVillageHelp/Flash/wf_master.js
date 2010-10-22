
function Command(cmd, param1, param2) {
	this.cmd = cmd;
	this.param1 = param1;
	this.param2 = param2;
}

var arrayQueuedCommandsForSWF = new Array();
var QUEUE_CHECK_TIMEOUT = 300;
var PARAM_PASSER_TIMEOUT = 2000;
var gFailedParamResponse = 0;
var CHECK_LOAD_FAILURE_TIMEOUT = 10000;
var gLoadFailure = null;
var gnFailureCount = 0;
var gnLastIndex = -1;
var gbSync = true;
var gParamInterval = null;
var gbParamLoaded = false;
var gnParamFailedCount = 0;
var gbMasterLoaded = false;
var gMasterLoadedInterval = null;

if (gbNav4)
{
	CHECK_LOAD_FAILURE_TIMEOUT = 2000;
}

function ResetFrames()
{
	if (gbIE && gbMac)
	{
		if (parent.parent.getBottomElement)
		{
			var frameset = parent.parent.getBottomElement("Bottom Frame");
			if (parent.parent.gbVertical)
			{
				frameset.rows = parent.parent.gsDefaultFramesetRow;
			}
			else
			{
				frameset.cols = parent.parent.gsDefaultFramesetCol;
			}
		}
	}

	parent.parent.gsLastFramesetCol = parent.parent.gsDefaultFramesetCol;
	parent.parent.gsCurFramesetCol = parent.parent.gsDefaultFramesetCol;
	parent.parent.gsLastFramesetRow = parent.parent.gsDefaultFramesetRow;
	parent.parent.gsCurFramesetRow = parent.parent.gsDefaultFramesetRow;
	parent.parent.gbNavPaneLoaded = false;
	gbParamLoaded = false;
	gbSWFLoaded = false;
	gbMasterLoaded = false;
	parent.parent.gsFlashVars = "";
	parent.parent.gsNavPane = "wf_blank.htm";
	if (window.location.href)
		window.location.href = "wf_blank.htm";

}

function SendParamPasser(strFlashVars)
{
	parent.parent.gsFlashVars = strFlashVars;
	parent.parent.window.frames["comm"].window.location.reload();
}

function CheckParamPasser()
{
	parent.parent.window.frames["comm"].window.location.reload();
	if (!gbParamLoaded && gnParamFailedCount > 5)
	{
		parent.parent.window.location.reload();
	}
	gnParamFailedCount++;
}

if (!(gbNav4 && !gbNav6 && gbWindows))
{
	if (!gbKonqueror)
	{
		window.onunload = ResetFrames;
	}
}

function CheckNavLoaded()
{
	gnFailureCount++;
	if (gnFailureCount >= 10)
	{
		// add 2 seconds if we fail 10 times incase this is a slow connection
		gnFailureCount = 0;
		CHECK_LOAD_FAILURE_TIMEOUT+=2000; 	
	}
	if (gbNav4 && !gbNav6 && gbWindows)
	{
		parent.parent.window.frames["Content"].window.frames["bottom"].window.frames["Navigation Pane"].window.location.href = "wf_navpane.htm";
	}
	else
	{
		parent.parent.window.frames["Content"].window.frames["bottom"].window.frames["Navigation Pane"].window.location.replace("wf_navpane.htm");
	}
	if (gbOpera)
	{
		parent.parent.window.frames["Content"].window.frames["bottom"].window.location.reload();
	}
	gLoadFailure = setTimeout("CheckNavLoaded()",CHECK_LOAD_FAILURE_TIMEOUT);
}


function DoCommand(cmd, param1, param2) {
	switch (cmd) {
		case "CmdError":
			alert(param1);
			break;
		case "CmdGetTopicAddress":
		case "CmdSkinFilename":
		case "CmdToolbarOrder":
		case "CmdTopicBGColor":
		case "CmdSyncTOC":
		case "CmdBrowseSequenceInfo":
		case "CmdTopicDisplayed":
		case "CmdTopicUnloaded":
		case "CmdSelectItem":
		case "CmdNavigationCleanup":	
		case "CmdNavbarOrder":
		case "CmdNavResizeVisible":
		case "CmdAutoSyncTOCEnabled":
		case "CmdFocusCanBeSet":
		case "CmdHideNavEnabled":
		case "CmdHideNavEnabledForRemoteURL":
			SendCmdToSWF(cmd, param1, param2);
			break;
		
		case "CmdTopicAddress":	
		case "CmdDisplayTopic":
		case "CmdNavigationCleanupComplete":
		case "CmdGetSyncTOC":
		case "CmdInvokePoweredBy":
		case "CmdHideNavigation":
		case "CmdShowNavigation":
		case "CmdGetToolbarOrder":
		case "CmdGetNavbarOrder":
		case "CmdGetDefaultNav":
		case "CmdGetBrowseSequenceInfo":
		case "CmdSetFocus":
		case "CmdPrint":
		case "CmdGetTopicBGColor":
		case "CmdScrollbarDragStart":
		case "CmdScrollbarDragMove":
		case "CmdScrollbarDragStop":
		case "CmdCurrentNav":
		case "CmdIsNavResizeVisible":
		case "CmdUILoadFailure":
		case "CmdIsAutoSyncTOCEnabled":
		case "CmdCanFocusBeSet":
		case "CmdIsHideNavEnabled":
		case "CmdIsHideNavEnabledForRemoteURL":
			// Pass the command along to the parent
			SendCmdToParentHTML(cmd, param1, param2);
			break;
		
		case "CmdNavigationPaneLoaded":
			clearInterval(gLoadFailure);
			SendCmdToParentHTML(cmd, param1, param2);
			gnFailureCount = 0;
			break;

		case "CmdGetSkinFilename":
			SendCmdToParentHTML(cmd, param1, param2);
			break;
			
		case "CmdMasterLoaded":
			if (!gbMasterLoaded)
			{
				parent.parent.gsProjectFolder = param1;
				gbMasterLoaded = true;
				if (gbNav4 && !gbNav6) 
				{
					setTimeout("parent.parent.window.frames['Content'].window.frames['bottom'].window.frames['Topic Pane'].location = parent.parent.GetTopicUrl()", 100);
				}
				else 
				{
					if (parent.parent.window.frames["Content"].window.frames["bottom"].window.frames["Topic Pane"])
						parent.parent.window.frames["Content"].window.frames["bottom"].window.frames["Topic Pane"].location = parent.parent.GetTopicUrl();
				}	
			}
			if (!gbSWFLoaded) {
				SendCmdToSWF("CmdGotMasterLoaded");
			}
			break;
			
		case "CmdSkinSwfLoaded":
			parent.parent.gsNavPane = "wf_navpane.htm";
			if (gbNav4 && !gbNav6)
			{
				setTimeout("parent.window.frames['bottom'].window.frames['Navigation Pane'].window.location.replace('wf_navpane.htm')", 100);
			}
			else if (gbOpera)
			{
				// use location.href = because location.replace() doesn't function properly in Opera
				parent.window.frames["bottom"].window.frames["Navigation Pane"].window.location.href = "wf_navpane.htm";
			}
			else
			{
				parent.window.frames["bottom"].window.frames["Navigation Pane"].window.location.replace("wf_navpane.htm");
			}
			gLoadFailure = setTimeout("CheckNavLoaded()",CHECK_LOAD_FAILURE_TIMEOUT);
			break;
			
		default:
			// See if this is a debug command and send it along to the master SWF
			if (cmd.substr(0, 6) == "debug:") {
				SendCmdToSWF(cmd, param1, param2);
			}
			break;
	}
	
	return;
}



function SendCmdToParentHTML(cmd, param1, param2) {
	parent.parent.DoCommand(cmd, param1, param2);
}

function CheckMasterLoaded()
{
	if (!gbMasterLoaded)
	{
		document.location.reload();
	}
}
