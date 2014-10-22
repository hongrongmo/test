// The last image number is 48 and imageflip 24.
// Images for the about ei page
Image1=new Image(145,20)
Image1.src="http://www.ei.org/http://www.ei.org/images/history_l.gif"
Image2=new Image(145,20)
Image2.src="http://www.ei.org/images/history_d.gif"

Image3=new Image(145,20)
Image3.src="http://www.ei.org/images/manage_l.gif"
Image4=new Image(145,20)
Image4.src="http://www.ei.org/images/manage_d.gif"

Image5=new Image(145,20)
Image5.src="http://www.ei.org/images/employ_l.gif"
Image6=new Image(145,20)
Image6.src="http://www.ei.org/images/employ_d.gif"

Image7=new Image(145,20)
Image7.src="http://www.ei.org/images/contact_l.gif"
Image8=new Image(145,20)
Image8.src="http://www.ei.org/images/contact_d.gif"

//Images for Villages
Image9=new Image(145,20)
Image9.src="/static/images/ev2_l.gif"
Image10=new Image(145,20)
Image10.src="/static/images/ev2_d.gif"

Image11=new Image(145,20)
Image11.src="http://www.ei.org/images/pv2_l.gif"
Image12=new Image(145,20)
Image12.src="http://www.ei.org/images/pv2_d.gif"

Image13=new Image(145,20)
Image13.src="http://www.ei.org/images/chemvillage_l.gif"
Image14=new Image(145,20)
Image14.src="http://www.ei.org/images/chemvillage_d.gif"

//Images for Databases
Image15=new Image(145,20)
Image15.src="http://www.ei.org/images/cpx_l.gif"
Image16=new Image(145,20)
Image16.src="http://www.ei.org/images/cpx_d.gif"

Image17=new Image(145,20)
Image17.src="http://www.ei.org/images/chimica_l.gif"
Image18=new Image(145,20)
Image18.src="http://www.ei.org/images/chimica_d.gif"

Image19=new Image(145,20)
Image19.src="http://www.ei.org/images/paperchem_l.gif"
Image20=new Image(145,20)
Image20.src="http://www.ei.org/images/paperchem_d.gif"

Image21=new Image(145,20)
Image21.src="http://www.ei.org/images/encompass_l.gif"
Image22=new Image(145,20)
Image22.src="http://www.ei.org/images/encompass_d.gif"

Image23=new Image(145,20)
Image23.src="http://www.ei.org/images/cbnb_l.gif"
Image24=new Image(145,20)
Image24.src="http://www.ei.org/images/cbnb_d.gif"

Image39=new Image(145,20)
Image39.src="http://www.ei.org/images/beil_l.gif"
Image40=new Image(145,20)
Image40.src="http://www.ei.org/images/beil_d.gif"

Image41=new Image(145,20)
Image41.src="http://www.ei.org/images/ohs_l.gif"
Image42=new Image(145,20)
Image42.src="http://www.ei.org/images/ohs_d.gif"

Image43=new Image(145,20)
Image43.src="http://www.ei.org/images/bretherick_l.gif"
Image44=new Image(145,20)
Image44.src="http://www.ei.org/images/bretherick_d.gif"

Image45=new Image(145,20)
Image45.src="http://www.ei.org/images/backfile_l.gif"
Image46=new Image(145,20)
Image46.src="http://www.ei.org/images/backfile_d.gif"

Image47=new Image(145,20)
Image47.src="http://www.ei.org/images/ntis_l.gif"
Image48=new Image(145,20)
Image48.src="http://www.ei.org/images/ntis_d.gif"

Image49=new Image(145,20)
Image49.src="http://www.ei.org/images/ref_l.gif"
Image50=new Image(145,20)
Image50.src="http://www.ei.org/images/ref_d.gif"

Image51=new Image(145,20)
Image51.src="http://www.ei.org/images/fact_l.gif"
Image52=new Image(145,20)
Image52.src="http://www.ei.org/images/fact_d.gif"

Image53=new Image(145,20)
Image53.src="http://www.ei.org/images/story_l.gif"
Image54=new Image(145,20)
Image54.src="http://www.ei.org/images/story_d.gif"

Image55=new Image(145,20)
Image55.src="http://www.ei.org/images/crc_l.gif"
Image56=new Image(145,20)
Image56.src="http://www.ei.org/images/crc_d.gif"


//Images for trial and tour
Image25=new Image(145,20)
Image25.src="http://www.ei.org/images/trial_l.gif"
Image26=new Image(145,20)
Image26.src="http://www.ei.org/images/trial_d.gif"

Image27=new Image(145,20)
Image27.src="http://www.ei.org/images/tour_l.gif"
Image28=new Image(145,20)
Image28.src="http://www.ei.org/images/tour_d.gif"

//News and Events section
Image29=new Image(145,20)
Image29.src="http://www.ei.org/images/press_l.gif"
Image30=new Image(145,20)
Image30.src="http://www.ei.org/images/press_d.gif"

Image31=new Image(145,20)
Image31.src="http://www.ei.org/images/events_l.gif"
Image32=new Image(145,20)
Image32.src="http://www.ei.org/images/events_d.gif"


Image33=new Image(145,20)
Image33.src="http://www.ei.org/images/eiupdate_l.gif"
Image34=new Image(145,20)
Image34.src="http://www.ei.org/images/eiupdate_d.gif"

//More ei products
Image35=new Image(145,20)
Image35.src="http://www.ei.org/images/chemnet_l.gif"
Image36=new Image(145,20)
Image36.src="http://www.ei.org/images/chemnet_d.gif"

Image37=new Image(145,20)
Image37.src="http://www.ei.org/images/environet_l.gif"
Image38=new Image(145,20)
Image38.src="http://www.ei.org/images/environet_d.gif"







// Roll over for about ei page functions start
function HistoryOut() {
document.imageflip1.src=Image2.src;return true;
}
function HistoryIn() {
document.imageflip1.src=Image1.src;return true;
}

function ManageOut() {
document.imageflip2.src=Image4.src;return true;
}
function ManageIn() {
document.imageflip2.src=Image3.src;return true;
}

function EmployOut() {
document.imageflip3.src=Image6.src;return true;
}
function EmployIn() {
document.imageflip3.src=Image5.src;return true;
}

function ContactOut() {
document.imageflip4.src=Image8.src;return true;
}
function ContactIn() {
document.imageflip4.src=Image7.src;return true;
}

//Functions for Villages
function Ev2Out() {
document.imageflip5.src=Image10.src;return true;
}
function Ev2In() {
document.imageflip5.src=Image9.src;return true;
}

function Pv2Out() {
document.imageflip6.src=Image12.src;return true;
}
function Pv2In() {
document.imageflip6.src=Image11.src;return true;
}

function ChemOut() {
document.imageflip7.src=Image14.src;return true;
}
function ChemIn() {
document.imageflip7.src=Image13.src;return true;
}

//Functions for Database
function CpxOut() {
document.imageflip8.src=Image16.src;return true;
}
function CpxIn() {
document.imageflip8.src=Image15.src;return true;
}

function ChimicaOut() {
document.imageflip9.src=Image18.src;return true;
}
function ChimicaIn() {
document.imageflip9.src=Image17.src;return true;
}

function PaperchemOut() {
document.imageflip10.src=Image20.src;return true;
}
function PaperchemIn() {
document.imageflip10.src=Image19.src;return true;
}


function EncompassOut() {
document.imageflip11.src=Image22.src;return true;
}
function EncompassIn() {
document.imageflip11.src=Image21.src;return true;
}

function CbnbOut() {
document.imageflip12.src=Image24.src;return true;
}
function CbnbIn() {
document.imageflip12.src=Image23.src;return true;
}

function BeilOut() {
document.imageflip20.src=Image40.src;return true;
}
function BeilIn() {
document.imageflip20.src=Image39.src;return true;
}

function OhsOut() {
document.imageflip21.src=Image42.src;return true;
}
function OhsIn() {
document.imageflip21.src=Image41.src;return true;
}

function BretherickOut() {
document.imageflip22.src=Image44.src;return true;
}
function BretherickIn() {
document.imageflip22.src=Image43.src;return true;
}

function BackOut() {
document.imageflip23.src=Image46.src;return true;
}
function BackIn() {
document.imageflip23.src=Image45.src;return true;
}

function NtisOut() {
document.imageflip24.src=Image48.src;return true;
}
function NtisIn() {
document.imageflip24.src=Image47.src;return true;
}

function RefOut() {
document.imageflip25.src=Image50.src;return true;
}
function RefIn() {
document.imageflip25.src=Image49.src;return true;
}

function FactOut() {
document.imageflip26.src=Image52.src;return true;
}
function FactIn() {
document.imageflip26.src=Image51.src;return true;
}

function StoryOut() {
document.imageflip27.src=Image54.src;return true;
}
function StoryIn() {
document.imageflip27.src=Image53.src;return true;
}

function CrcOut() {
document.imageflip28.src=Image56.src;return true;
}

function CrcIn() {
document.imageflip28.src=Image55.src;return true;
}


//functions for trial and tour
function TrialOut() {
document.imageflip13.src=Image26.src;return true;
}
function TrialIn() {
document.imageflip13.src=Image25.src;return true;
}

function TourOut() {
document.imageflip14.src=Image28.src;return true;
}
function TourIn() {
document.imageflip14.src=Image27.src;return true;
}

//functions for news and events
function PressOut() {
document.imageflip15.src=Image30.src;return true;
}
function PressIn() {
document.imageflip15.src=Image29.src;return true;
}

function EventsOut() {
document.imageflip16.src=Image32.src;return true;
}
function EventsIn() {
document.imageflip16.src=Image31.src;return true;
}


function UpdateOut() {
document.imageflip17.src=Image34.src;return true;
}
function UpdateIn() {
document.imageflip17.src=Image33.src;return true;
}

//more ei product
function ChemnetOut() {
document.imageflip18.src=Image36.src;return true;
}
function ChemnetIn() {
document.imageflip18.src=Image35.src;return true;
}

function EnviroOut() {
document.imageflip19.src=Image38.src;return true;
}
function EnviroIn() {
document.imageflip19.src=Image37.src;return true;
}







