//UDMv3.4.1.1b
//**DO NOT EDIT THIS *****
if (!exclude) { //********
//************************



///////////////////////////////////////////////////////////////////////////
//
//  ULTIMATE DROPDOWN MENU VERSION 3.4.1b by Brothercake
//  http://www.brothercake.com/dropdown/ 
//
//  Link-wrapping routine by Brendan Armstrong
//  KDE modifications by David Joham
//  Opera reload/resize routine by Michael Wallner
//  http://www.wallner-software.com/
//
//  This script featured on Dynamic Drive (http://www.dynamicdrive.com)
//
///////////////////////////////////////////////////////////////////////////



// *** POSITIONING AND STYLES *********************************************



var menuALIGN = "center";			// alignment
var absLEFT = 	0;					// absolute left or right position (if menu is left or right aligned)
var absTOP = 	68; 				// absolute top position 74 initial
	
var staticMENU = false;				// static positioning mode (ie5,ie6 and ns4 only)

var stretchMENU = false;			// show empty cells
var showBORDERS = false;			// show empty cell borders

var baseHREF = "";					// base path to .js files for the script (ie: resources/)
var zORDER = 	1000;				// base z-order of nav structure (not ns4)

var mCOLOR = 	"#2A77A1";			// main nav cell color
var rCOLOR = 	"#2A77A1";			// main nav cell rollover color
var bSIZE = 	0;					// main nav border size
var bCOLOR = 	"black"				// main nav border color
var aLINK = 	"#FFFFFF";			// main nav link color
var aHOVER = 	"";					// main nav link hover-color (dual purpose)
var aDEC = 	"none";					// main nav link decoration
var fFONT = 	"verdana,arial";	// main nav font face
var fSIZE = 	12;					// main nav font size (pixels)
var fWEIGHT = 	"bold"				// main nav font weight
var tINDENT = 	7;					// main nav text indent (if text is left or right aligned)
var vPADDING = 	7;					// main nav vertical cell padding
var vtOFFSET = 	0;					// main nav vertical text offset (+/- pixels from middle)

var keepLIT =	true;				// keep rollover color when browsing menu
var vOFFSET = 	5;					// shift the submenus vertically
var hOFFSET = 	4;					// shift the submenus horizontally

var smCOLOR = 	"#B6C9D4";			// submenu cell color

var srCOLOR = 	"#8BAFC3";			// submenu cell rollover color
var sbSIZE = 	1;					// submenu border size
var sbCOLOR = 	"black"				// submenu border color
var saLINK = 	"#000000";			// submenu link color
var saHOVER = 	"#FFFFFF";			// submenu link hover-color (dual purpose)
var saDEC = 	"none";				// submenu link decoration
var sfFONT = 	"verdana,arial";	// submenu font face
var sfSIZE = 	12;					// submenu font size (pixels)
var sfWEIGHT = 	"normal"			// submenu font weight
var stINDENT = 	5;					// submenu text indent (if text is left or right aligned)
var svPADDING = 1;					// submenu vertical cell padding
var svtOFFSET = 0;					// submenu vertical text offset (+/- pixels from middle)

var shSIZE =	2;					// submenu drop shadow size
var shCOLOR =	"999999";			// submenu drop shadow color
var shOPACITY = 75;					// submenu drop shadow opacity (not ie4,ns4 or opera)

var keepSubLIT = true;				// keep submenu rollover color when browsing child menu
var chvOFFSET = -12;				// shift the child menus vertically
var chhOFFSET = 7;					// shift the child menus horizontally

var closeTIMER = 330;				// menu closing delay time

var cellCLICK = true;				// links activate on TD click
var aCURSOR = "hand";				// cursor for active links (not ns4 or opera)

var altDISPLAY = "";				// where to display alt text
var allowRESIZE = true;				// allow resize/reload

var redGRID = false;				// show a red grid
var gridWIDTH = 0;					// override grid width
var gridHEIGHT = 0;					// override grid height
var documentWIDTH = 0;				// override document width

var hideSELECT = true;				// auto-hide select boxes when menus open (ie only)
var allowForSCALING = false;		// allow for text scaling in mozilla 5


//** LINKS ***********************************************************


// add main link item About Ei ("url","Link name",width,"text-alignment","_target","alt text",top position,left position,"key trigger")
addMainItem("http://www.ei.org/aboutei.html","<img src='http://www.ei.org/images/about.gif' border='0'",80,"center","_self","About Engineering Information",0,0,"e");
// define submenu properties for About EI (width,"align to edge","text-alignment",v offset,h offset,"filter")
defineSubmenuProperties(120,"left","left",-3,10,"");

// add submenu link items ("url","Link name","_target","alt text")
addSubmenuItem("http://www.ei.org/history.html","History","_self","");
addSubmenuItem("http://www.ei.org/management.html","Management","_self","");
addSubmenuItem("http://www.ei.org/employment.html","Employment","_self","");
addSubmenuItem("http://www.ei.org/contact.html","Contact Us","_self","");

addMainItem("","|",5,"center","","",0,0,"");

// *** change these to absolutes

// add main link item Villages ("url","Link name",width,"text-alignment","_target","alt text",top position,left position,"key trigger")

addMainItem("http://www.ei.org/villages.html","<img src='http://www.ei.org/images/village.gif' border='0'",85,"center","_self","Villages",0,0,"w");

	
// define submenu properties for Villages (width,"align to edge","text-alignment",v offset,h offset,"filter")
defineSubmenuProperties(164,"left","left",-3,0,"");
	
//add submenu items for Villages
addSubmenuItem("http://www.ei.org/ev2.html","Engineering Village 2&nbsp; &nbsp; &gt;","_self","");
//define child menu properties for EV2 (width, "align to edge","text-alignment",v offset,h offset,"filter")
defineChildmenuProperties(112,"left","left",16,-10,"");
//add child menu link items ("url","Link name","_target","alt text")
addChildmenuItem("/engresources/trialForm.jsp","Trial application","_self","");
addChildmenuItem("http://www.ei.org/ev2tour.html","Tour","_self","");
	

addSubmenuItem("http://www.ei.org/chemvillage.html","ChemVillage &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp; &nbsp; &nbsp; &nbsp;  &gt;","_self","");
defineChildmenuProperties(122,"left","left",-0,-10,"");
//add child menu link items ("url","Link name","_target","alt text")
addChildmenuItem("/engresources/trialForm.jsp","Trial application","_self","");
addChildmenuItem("http://www.ei.org/chemtour.html","Tour","_self","");
		
addSubmenuItem("http://www.ei.org/pv2.html","Paper Village 2 &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp; &nbsp; <b>&gt;</b>","_self","");
defineChildmenuProperties(140,"left","left",-0,-10,"");
//add child menu link items ("url","Link name","_target","alt text")
addChildmenuItem("/engresources/trialForm.jsp","Trial application","_self","");
//addChildmenuItem("http://www.ei.org/pv2tour.html","Tour","_parent","");

addMainItem("","|",10,"center","","",0,0,"");
		
// add main link item Databases ("url","Link name",width,"text-alignment","_target","alt text",top position,left position,"key trigger")	
addMainItem("http://www.ei.org/databases.html","<img src='http://www.ei.org/images/dbases.gif' border='0'",90,"center","","Databases",0,0,"s");


defineSubmenuProperties(195,"left","left",-3,0,"");
	
//addSubmenuItem("http://www.ei.org/beil.html","Beilstein Abstracts","_self","");
//addSubmenuItem("http://www.ei.org/bretherick.html","Bretherick's Reactive","");
addSubmenuItem("http://www.ei.org/cbnb.html","CBNB","_self","");
addSubmenuItem("http://www.ei.org/chimica.html","Chimica","_self","");
addSubmenuItem("http://www.ei.org/compendex.html","Compendex","_self","");
addSubmenuItem("http://www.ei.org/crc.html","CRC Databases&nbsp;","_self","");
//addSubmenuItem("","CRC Databases&nbsp; &nbsp; &gt;","_self","");
//define child menu properties for crc databases
//defineChildmenuProperties(122,"left","left",-0,-10,"");
//add child menu link items ("url","Link name","_target","alt text")
//addChildmenuItem("http://www.ei.org/chemnetbase.html","CHEMnetBASE","_self","");
//addChildmenuItem("http://www.ei.org/environetbase.html","ENVIROnetBASE","_self","");
addSubmenuItem("http://www.ei.org/encompass.html","EnCompass","_self","");
addSubmenuItem("http://www.ei.org/backfile.html","Engineering Index Backfile","_self","");
addSubmenuItem("http://www.ei.org/inspec.html","Inspec","_self","");
addSubmenuItem("http://www.ei.org/inspecarchive.html","Inspec Archive","_self","");
addSubmenuItem("http://www.ei.org/ntis.html","NTIS","_self","");
addSubmenuItem("http://www.ei.org/paperchem.html","PaperChem","_self","");
addSubmenuItem("http://www.ei.org/referexengineering.html","Referex Engineering &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &gt;","_self","");
//define child menu properties for Referex (width, "align to edge","text-alignment",v offset,h offset,"filter")
defineChildmenuProperties(135,"left","left",16,-10,"");
//add child menu link items ("url","Link name","_target","alt text")
addChildmenuItem("http://www.ei.org/referexstory.html","Referex Story","_self","");
	
	
	
	
	
	
	

addMainItem("","|",20,"center","","",0,0,"");
	
// add main link item More Products ("url","Link name",width,"text-alignment","_target","alt text",top position,left position,"key trigger")		
addMainItem("http://www.ei.org/morepdts.html","<img src='http://www.ei.org/images/morepdt.gif' border='0'",125,"center","","More Ei Products",0,0,"t");
	
addMainItem("","|",15,"center","","",0,0,"");
	
// add main link item Resources ("url","Link name",width,"text-alignment","_target","alt text",top position,left position,"key trigger")
//addMainItem("constr.html","<img src='http://www.ei.org/images/resources_b.gif' border='0'",90,"center","","Resources",0,0,"r");


//addMainItem("","|",25,"center","","",0,0,"");

addMainItem("","<img src='http://www.ei.org/images/newsev.gif' border='0'",110,"center","_self","News Events",0,0,"r");
defineSubmenuProperties(120,"left","left",-3,0,"");

addSubmenuItem("http://www.ei.org/press.html","Press Releases","_self","");
addSubmenuItem("http://www.ei.org/eiupdate","Ei Update","_blank","");

//**DO NOT EDIT THIS *****
}//***********************
//************************
