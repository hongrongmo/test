var dom = (document.getElementById) ? true : false;
var ns5 = ((navigator.userAgent.indexOf("Gecko")>-1) && dom) ? true: false;
var ie5 = ((navigator.userAgent.indexOf("MSIE")>-1) && dom) ? true : false;
var ns4 = (document.layers && !dom) ? true : false;
var ie4 = (document.all && !dom) ? true : false;
var nodyn = (!ns5 && !ns4 && !ie4 && !ie5) ? true : false;

// resize fix for ns4
var origWidth, origHeight;
if (ns4) {
        origWidth = window.innerWidth; origHeight = window.innerHeight;
        window.onresize = function() { if (window.innerWidth != origWidth || window.innerHeight != origHeight) history.go(0); }
}

// avoid error of passing event object in older browsers
if (nodyn) { event = "nope" }

///////////////////////  CUSTOMIZE FOR ONMOUSE OVER   ////////////////////
// settings for tooltip 
// Do you want tip to move when mouse moves over link?
var tipFollowMouse= false;      
// Be sure to set tipWidth wide enough for widest image
var tipWidth= 400;
var offX= 20;   // how far from mouse to show tip
var offY= 12; 
var tipFontFamily= "Verdana, arial, helvetica, sans-serif";
var tipFontSize= "8pt";
// set default text color and background color for tooltip here
// individual tooltips can have their own (set in messages arrays)
// but don't have to
var tipFontColor= "#000000";
var tipBgColor= "#DDECFF"; 
var tipBorderColor= "#000080";
var tipBorderWidth= 1;
var tipBorderStyle= "ridge";
var tipPadding= 4;

// tooltip content (image, description, optional bgColor, optional textcolor)
var messages = new Array();
// multi-dimensional arrays containing: 
// image and text for tooltip
// optional: bgColor and color to be sent to tooltip
messages[0] = new Array('/engresources/images/spacer.gif',"Compendex&#174;  is the most comprehensive interdisciplinary engineering database in the world with almost seven million records referencing 5,000 engineering journals and conferences dating from 1970.  The database is updated weekly.","#FFFFCC");
messages[1] = new Array('/engresources/images/spacer.gif',"INSPEC&#174; is the leading bibliographic database providing access to the world's scientific literature in electrical engineering, electronics, physics, control engineering, information technology, communications, computers and computing.","#FFFFCC");
messages[2] = new Array('/engresources/images/spacer.gif',"Your institutions add-on subscription to ENGnetBASE allows you access to some of the world's leading engineering handbooks published by CRC Press.","#FFFFCC");
messages[3] = new Array('/engresources/images/spacer.gif',"The United States Patent Office offers access to its full text patent database.  Full text patents are available from 1790 to date, with weekly updates.","#FFFFCC");
messages[4] = new Array('/engresources/images/spacer.gif',"esp@cenet provides access to patents produced by national patent offices in Europe as well as the European Patent Office (EPO), the World Intellectual Property Organization (WIPO) and Japan.","#FFFFCC");
messages[5] = new Array('/engresources/images/spacer.gif',"Techstreet offers one of the world's largest collections of industry standards and specifications aggregated from over 350 leading standards developing organizations.","#FFFFCC");
messages[6] = new Array('/engresources/images/spacer.gif',"Scirus is the most comprehensive science-specific search engine available on the internet.  Driven by the latest search engine technology, it enables scientists, students and anyone searching for scientific information to pinpoint data, locate university sites and find reports articles quickly and easily.","#FFFFCC");
messages[7] = new Array('/engresources/images/spacer.gif',"LexisNexis Daily news feeds from LexisNexis on a broad spectrum of engineering topics.","#FFFFCC");


////////////////////  END OF CUSTOMIZATION AREA  ///////////////////

// preload images that are to appear in tooltip
// from arrays above
if (document.images) {
        var theImgs = new Array();
        for (var i=0; i<messages.length; i++) {
        theImgs[i] = new Image();
                theImgs[i].src = messages[i][0];
  }
}

// to layout image and text, 2-row table, image centered in top cell
// these go in var tip in doTooltip function
// startStr goes before image, midStr goes between image and text
var startStr = '<table width="' + tipWidth + '"><tr><td align="center" width="100%"><img src="';
var midStr = '" border="0"></td></tr><tr><td valign="top">';
var endStr = '</td></tr></table>';

////////////////////////////////////////////////////////////
//  initTip     - initialization for tooltip.
//              Global variables for tooltip. 
//              Set styles for all but ns4. 
//              Set up mousemove capture if tipFollowMouse set true.
////////////////////////////////////////////////////////////
var tooltip, tipcss;
function initTip() {
        if (nodyn) return;
        tooltip = (ns4)? document.tipDiv.document: (ie4)? document.all['tipDiv']: (ie5||ns5)? document.getElementById('tipDiv'): null;
        tipcss = (ns4)? document.tipDiv: tooltip.style;
        if (ie4||ie5||ns5) {    // ns4 would lose all this on rewrites
                tipcss.width = tipWidth+"px";
                tipcss.fontFamily = tipFontFamily;
                tipcss.fontSize = tipFontSize;
                tipcss.color = tipFontColor;
                tipcss.backgroundColor = tipBgColor;
                tipcss.borderColor = tipBorderColor;
                tipcss.borderWidth = tipBorderWidth+"px";
                tipcss.padding = tipPadding+"px";
                tipcss.borderStyle = tipBorderStyle;
        }
        if (tooltip&&tipFollowMouse) {
                if (ns4) document.captureEvents(Event.MOUSEMOVE);
                document.onmousemove = trackMouse;
        }
}

window.onload = initTip;

/////////////////////////////////////////////////
//  doTooltip function
//                      Assembles content for tooltip and writes 
//                      it to tipDiv
/////////////////////////////////////////////////
var t1,t2;      // for setTimeouts
var tipOn = false;      // check if over tooltip link
function doTooltip(evt,num) {
        if (!tooltip) return;
        if (t1) clearTimeout(t1);       if (t2) clearTimeout(t2);
        tipOn = true;
        // set colors if included in messages array
        if (messages[num][2])   var curBgColor = messages[num][2];
        else curBgColor = tipBgColor;
        if (messages[num][3])   var curFontColor = messages[num][3];
        else curFontColor = tipFontColor;
        if (ns4) {
                var tip = '<table bgcolor="' + tipBorderColor + '" width="' + tipWidth + '" cellspacing="0" cellpadding="' + tipBorderWidth + '" border="0"><tr><td><table bgcolor="' + curBgColor + '" width="100%" cellspacing="0" cellpadding="' + tipPadding + '" border="0"><tr><td>'+ startStr + messages[num][0] + midStr + '<span style="font-family:' + tipFontFamily + '; font-size:' + tipFontSize + '; color:' + curFontColor + ';">' + messages[num][1] + '</span>' + endStr + '</td></tr></table></td></tr></table>';
                tooltip.write(tip);
                tooltip.close();
        } else if (ie4||ie5||ns5) {
                var tip = startStr + messages[num][0] + midStr + '<span style="font-family:' + tipFontFamily + '; font-size:' + tipFontSize + '; color:' + curFontColor + ';">' + messages[num][1] + '</span>' + endStr;
                tipcss.backgroundColor = curBgColor;
                tooltip.innerHTML = tip;
        }
        if (!tipFollowMouse) positionTip(evt);
        else t1=setTimeout("tipcss.visibility='visible'",100);
}

var mouseX, mouseY;
function trackMouse(evt) {
        mouseX = (ns4||ns5)? evt.pageX: window.event.clientX + document.body.scrollLeft;
        mouseY = (ns4||ns5)? evt.pageY: window.event.clientY + document.body.scrollTop;
        if (tipOn) positionTip(evt);
}

/////////////////////////////////////////////////////////////
//  positionTip function
//              If tipFollowMouse set false, so trackMouse function
//              not being used, get position of mouseover event.
//              Calculations use mouseover event position, 
//              offset amounts and tooltip width to position
//              tooltip within window.
/////////////////////////////////////////////////////////////
function positionTip(evt) {
        if (!tipFollowMouse) {
                mouseX = (ns4||ns5)? evt.pageX: window.event.clientX + document.body.scrollLeft;
                mouseY = (ns4||ns5)? evt.pageY: window.event.clientY + document.body.scrollTop;
        }
        // tooltip width and height
        var tpWd = (ns4)? tooltip.width: (ie4||ie5)? tooltip.clientWidth: tooltip.offsetWidth;
        var tpHt = (ns4)? tooltip.height: (ie4||ie5)? tooltip.clientHeight: tooltip.offsetHeight;
        // document area in view (subtract scrollbar width for ns)
        var winWd = (ns4||ns5)? window.innerWidth-20+window.pageXOffset: document.body.clientWidth+document.body.scrollLeft;
        var winHt = (ns4||ns5)? window.innerHeight-20+window.pageYOffset: document.body.clientHeight+document.body.scrollTop;
        // check mouse position against tip and window dimensions
        // and position the tooltip 
        if ((mouseX+offX+tpWd)>winWd) 
                tipcss.left = (ns4)? mouseX-(tpWd+offX): mouseX-(tpWd+offX)+"px";
        else tipcss.left = (ns4)? mouseX+offX: mouseX+offX+"px";
        if ((mouseY+offY+tpHt)>winHt) 
                tipcss.top = (ns4)? winHt-(tpHt+offY): winHt-(tpHt+offY)+"px";
        else tipcss.top = (ns4)? mouseY+offY: mouseY+offY+"px";
        if (!tipFollowMouse) t1=setTimeout("tipcss.visibility='visible'",100);
}

function hideTip() {
        if (!tooltip) return;
        t2=setTimeout("tipcss.visibility='hidden'",100);
        tipOn = false;
}

function validForm(passwordval) {
            if (passwordval.SYSTEM_USERNAME.value =="") {
                        alert ("You must enter a Username")
                        passwordval.SYSTEM_USERNAME.focus()
                        return false
            }
            if (passwordval.SYSTEM_PASSWORD.value =="") {
                        alert ("You must enter a password")
                        passwordval.SYSTEM_PASSWORD.focus()
                        return false
            }
            document.passwordval.submit();
}

function setFocus(){
	document.passwordval.SYSTEM_USERNAME.focus();
}
