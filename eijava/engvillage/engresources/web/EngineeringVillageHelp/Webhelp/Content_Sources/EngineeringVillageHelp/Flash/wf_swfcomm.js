var arrayQueuedCommandsForSWF = new Array();
var gbSWFLoaded = false;
var QUEUE_CHECK_TIMEOUT = 300;
var PARAM_PASSER_TIMEOUT = 5000;
var gFailedParamResponse = 0;
var CHECK_LOAD_FAILURE_TIMEOUT = 10000;
var gnLastIndex = -1;
var gbSync = true;
var gParamInterval = null;
var gbParamLoaded = false;
var gnParamFailedCount = 0;
var gsCommSwfID = null;
var cmdTimerID = null;
var gbCommLoaded=true;

function debug_msg(msg)
{
	if (parent.gsDomain==document.domain)
	{
		if (parent.parent.window.frames["commDebug"])
		{
//			parent.parent.window.frames["commDebug"].debug_msg(msg);
		}
	}
}

function debug_topic(msg)
{
	if (parent.window.frames["commDebug"])
	{
//		parent.window.frames["commDebug"].debug_msg(msg);
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

function UseSetVariable()
{
	var bResult = false;
	if (gbWindows && !gbOpera && !gbMozilla && (gbIE || (gbNav4 && !gbNav6) || gbNav7))
	{
		bResult = true;
	}
	return bResult;

}

function Command(cmd, param1, param2) {
	this.cmd = cmd;
	this.param1 = param1;
	this.param2 = param2;
}

// strSwfID that is passed to CreateFSHandler should be the same ID that is passed to CreateCommSwf
function CreateFSHandler(strSwfID)
{
	var sVBScript = '';

	var sJScript = '';
	sJScript += '<script language="JavaScript">';
	sJScript += 'function '+ strSwfID +'_DoFSCommand(command, args) {';
	sJScript += '	var param1 = "";';
	sJScript += '	var param2 = "";';
	sJScript += '	var msgIndex = "";';
	sJScript += '	var params = "" + args;';
	sJScript += '	if (params.indexOf("\\n") > 0) {';
	sJScript += '		msgIndex = params.substring(0, params.indexOf("\\n"));';
	sJScript += '		params = params.substring(params.indexOf("\\n")+1);';
	sJScript += '		param1 = params.substring(0, params.indexOf("\\n"));';
	sJScript += '		param2 = params.substring(params.indexOf("\\n") + 1, params.length);';
	sJScript += '	}';
	sJScript += '	var cmd = "" + command; ';
	sJScript += '	DoSwfCommand(cmd, msgIndex, param1, param2);';
	sJScript += '}';
	sJScript += '</script>';

	document.write(sJScript);


	// Create subroutine to handle fscommand from swf
	sVBScript += '<script language="VBScript"> \n';
	sVBScript += 'Sub ' + strSwfID + '_FSCommand(ByVal command, ByVal args) \n';
	sVBScript += 'call ' + strSwfID + '_DoFSCommand(command, args) \n';
	sVBScript += 'end sub'
	sVBScript += '</script>'

	document.write(sVBScript);
}

function CreateCommSwf(strSwfName, strSwfID, strFlashVars, strWidth, strHeight, strUniqueID)
{
	var sHTML = "";

	// Add the unique ID to the end of the list of FlashVars
	if (strFlashVars != "" )
	{
		strFlashVars += "&uniqueHelpID=" + strUniqueID;
	}
	else
	{
		strFlashVars += "uniqueHelpID=" + strUniqueID;
	}

	// Tell the swf whether or not to use FScommand to communicate with the HTML
	strFlashVars += "&bUseFScommand=";
	if (((gbIE4) && (gbWindows) && (!gbOpera)) || ((gbNav4 && !gbNav6) && gbWindows)) 
	{
		strFlashVars += "1";
	} 
	else 
	{
		strFlashVars += "0";
	}

	// Make sure that the swf is loaded from the base folder
	if (typeof(gsPPath)=="string")
	{
		strSwfName = gsPPath + strSwfName;
	}

	// Save the Swf ID for future reference
	gsCommSwfID = strSwfID;

	sHTML += "<OBJECT classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' codebase='http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,0,0'";
	sHTML += "WIDTH='" + strWidth + "' HEIGHT='" + strHeight + "' id='" + strSwfID + "' >";
	sHTML += "<PARAM NAME='movie' VALUE='"+strSwfName+"'>";
	sHTML += "<PARAM NAME=quality VALUE=high>";
	sHTML += "<PARAM NAME='menu' value='false'>";

	// FlashVars for Object tag:
	sHTML += "<PARAM NAME=FlashVars VALUE='" + strFlashVars + "'>";
	sHTML += "<EMBED src='" + strSwfName + "' quality=high WIDTH='100%' menu='false' HEIGHT='" + strHeight + "' NAME='" + strSwfID + "' swliveconnect=true ";

	// FlashVars for Embed tag:
	sHTML += "FlashVars='" + strFlashVars + "' ";
	sHTML += "TYPE='application/x-shockwave-flash' PLUGINSPAGE='http://www.macromedia.com/go/getflashplayer'>";
	sHTML += "</EMBED>";
	sHTML += "</OBJECT>";

	document.write(sHTML);
}

function DoCommCommand(cmd, param1, param2)
{
	switch (cmd) {
		case "CmdParamPasserLoaded":
			gbParamLoaded = true;
			clearInterval(gParamInterval);
			gbCommLoaded = true;
			CheckCommandQueues();
			break;
		case "CmdMasterLoaded":
		case "CmdTopicSwfLoaded":
			DoCommand(cmd, param1, param2)
			gbSWFLoaded = true;
			break;
		default:
			DoCommand(cmd, param1, param2)
			break;
	}
	return;
}

function DoSwfCommand(cmd, msgIndex, param1, param2)
{
//	debug_msg(cmd);
	msgIndex = Number(msgIndex);

	if (msgIndex != (gnLastIndex + 1) && gbSWFLoaded)
	{
		// make sure we still execute the cmd CmdParamPasserLoaded so we do not get into a deadlock
		if (cmd == "CmdParamPasserLoaded")
		{
			DoCommCommand(cmd, param1, param2);
		}
		if (gbSync)
		{
			gbSync = false;
			var msgCount = arrayQueuedCommandsForSWF.length;

			if (msgCount == 0)
			{
				SendCmdToSWF("CmdResendMessages",gnLastIndex + 1);
			}
			else
			{
				var tempArray = new Array();
				tempArray[0] = new Command("CmdResendMessages",gnLastIndex + 1, "");
				var j = 1;
				for (var i = 0; i < msgCount; i++)
				{
					tempArray[j] = arrayQueuedCommandsForSWF[i];
					j++;
				}
				arrayQueuedCommandsForSWF = tempArray;				
			}
		}
	}
	else if (!gbSWFLoaded && (cmd == "CmdMasterLoaded" || cmd == "CmdTopicSwfLoaded"))
	{
		gbSync = true;
		gnLastIndex = msgIndex;
		DoCommCommand(cmd, param1, param2);		
	}
	else if (gbSWFLoaded)
	{
		gbSync = true;
		gnLastIndex = msgIndex;
		DoCommCommand(cmd, param1, param2);
	}
}

function CheckCommandQueues() {
	// See if the SWF file is ready to receive commands
	if (gbSWFLoaded) {
		clearInterval(cmdTimerID);

		// If we are not using the ParamPasser Swf send all the messages at once
		if (UseSetVariable())
		{
			var msgCount = arrayQueuedCommandsForSWF.length;
			for (var iCmd = 0; iCmd < msgCount; iCmd++) {
				var queuedCmd = arrayQueuedCommandsForSWF[iCmd];
				SendCmdToSWF(queuedCmd.cmd, queuedCmd.param1, queuedCmd.param2, true);
				delete queuedCmd;
			}
			arrayQueuedCommandsForSWF.length = 0;
		}
		else if (gbCommLoaded) 
		{
			var msgCount = arrayQueuedCommandsForSWF.length;

			if (msgCount >= 1)
			{
				var queuedCmd = arrayQueuedCommandsForSWF[0];
				SendCmdToSWF(queuedCmd.cmd, queuedCmd.param1, queuedCmd.param2, true);
				delete queuedCmd;
			}

			if ( msgCount > 1 )
			{
				var tempArray = new Array();
				var j = 0;
				for (var i = 1; i < msgCount; i++)
				{
					tempArray[j] = arrayQueuedCommandsForSWF[i];
					j++;
				}
				arrayQueuedCommandsForSWF = tempArray;
			}
			else
			{
				arrayQueuedCommandsForSWF.length = 0;
			}
		}
	}	

	return;
}

function SendParamPasserCmd(cmd, param1, param2)
{
	if (parent == this)
		return;

	// Make sure the parameters don't contain single quotes that would mess up the string construction below
	if (typeof(param1) == "string") {
		var searchExp = /\x27/;
		param1 = param1.replace(searchExp, "\\x27");
		searchExp = /\x22/;
		param1 = param1.replace('"', "\\x22");
	}
	if (typeof(param2) == "string") {
		var searchExp = /\x27/;
		param2 = param2.replace(searchExp, "\\x27");
		searchExp = /\x22/;
		param2 = param2.replace('"', "\\x22");
	}
		
	// OK, so SetVariable is not supported so we need to create a temporary Flash OBJECT
	// used to pass variables.
	var strFlashVars;

	// Build up the variable string we will be sending
	strFlashVars = "Param1NewCommand=" + param1 + "&Param2NewCommand=" + param2 + "&NewCommand=" + cmd;
	strFlashVars += "&uniqueHelpID=" + parent.UniqueID();
		
	clearInterval(gParamInterval);
	gParamInterval = setInterval("CheckParamPasser()",PARAM_PASSER_TIMEOUT);
	gFailedParamResponse = 0;
	gbCommLoaded = false;
	SendParamPasser(strFlashVars);
}

////////////////////////
// FLASH TO JS COMMUNICATION
function SendCmdToSWF(cmd, param1, param2, bFromQueue) {
	if (arguments.length < 4) 
	{
		bFromQueue = false;
	}

	if (!bFromQueue)
	{
		// See if the SWF is ready for commands and it has been enough time since the last one
		if (!gbSWFLoaded || !gbCommLoaded || arrayQueuedCommandsForSWF.length > 0) {
			// SWF is not ready - add it to the queue
			var newCmd = new Command(cmd, param1, param2);
			arrayQueuedCommandsForSWF[arrayQueuedCommandsForSWF.length] = newCmd;
			return;
		}
	}

	// First, let's handle the simple cases when the Flash/browser combination can handle SetVariable
	if (UseSetVariable()) {
		if (gbIE)
		{
			var targetSwf = getElement(gsCommSwfID);
			targetSwf.SetVariable("Param1NewCommand", param1);
			targetSwf.SetVariable("Param2NewCommand", param2);
			targetSwf.SetVariable("paramListener.NewCommand", cmd);
		}
		else
		{
			eval("window.document." + gsCommSwfID + ".SetVariable(\"Param1NewCommand\",param1);");
			eval("window.document." + gsCommSwfID + ".SetVariable(\"Param2NewCommand\",param2);");
			eval("window.document." + gsCommSwfID + ".SetVariable(\"paramListener.NewCommand\",cmd);");
		}

	} else {
		SendParamPasserCmd(cmd, param1, param2);
	}
	
	return;
}

// decrease load failure timeout for NS4
if (gbNav4 && !gbNav6)
{
	CHECK_LOAD_FAILURE_TIMEOUT = 2000;
	if (!gbWindows && !gbMac)
	{
		gParamInterval = setTimeout("CheckParamPasser()",PARAM_PASSER_TIMEOUT);
	}
}


