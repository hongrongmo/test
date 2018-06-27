package org.ei.dataloading.xmlDataLoading;

public class DataMapping
{

		public static final String M_ID		= 	"";							//unique document ID by EI
	 	public static final String ID		=	"";
	 	public static final String AN		=	"itemid";					//accession number    	"Item_info-item_id"
	 	public static final String IG		=	"";							//publisher number
	 	public static final String CN		=	"codencode";				//coden				"Head-Source-Codencode"/"Related_item-Source-Codencode"
	 	public static final String ISS		=	"voliss";					//issue					"Source-voliss_issue"/"Ref_info-voliss_issue"/"Refd_itemcitation-voliss_issue"

	 	public static final String LA		=	"citation-language"; 		//language
	 	public static final String LA1		=	"abstract-language"; 		//language

	 	public static final String AUS		=	"author";					//authors
		public static final String AUS1		=	"contributor";				//authors when role=auth

	 	public static final String AV		=	"affiliation_address-part"; //affiliation providence
	 	public static final String AF		=	"organization";				//author affiliation
	 	public static final String AC		=	"affiliation_city";			//affiliation city
	 	public static final String AFS		=	"affiliation_address-part";	//affiliation state
	 	public static final String AY		=	"affiliation_country";		//affiliation country
	 	public static final String AB		=	"abstract";					//abstract
	 	public static final String AT		=	null;						//abstract type
	 	public static final String BN		=	"isbn";						//ISBN
	 	public static final String BR		=	null;						//book review number
	 	public static final String CF		=	"confname";					//conference name
	 	public static final String CC		=	"confcode";					//conference code
	 	public static final String CL		=	"classification";			//classification codes
	 	public static final String CLS		=	"classification.CPXCLASS";	//classification codes
	 	public static final String CVS		=  	"mainterm";					//controll word when candidate=y and type=CCV
	 	public static final String DS		=  	null;						//sponsor city
	 	public static final String DT		=	"citation-type.code";		//document type

	 	public static final String ED		=	"editor";					//editor
	 	public static final String ED1		=	"contributor";				//second editor option( when role attribute=edit )

	 	public static final String EF		=	"editororganization";		//editor affiliation
	 	public static final String EM		=  	"ce:e-address";				//editor affiliation corperate division
	 	public static final String EC		=	"city";						//editor city
	 	public static final String ES		=	"state";					//editor state
	 	public static final String EV		=	null;						//editor providence
	 	public static final String EY		=	"conflocation.country";		//editor country
	 	public static final String FLS		=	"mainterm";					//free language when controll=n and type="CLU"
	 	public static final String MT		=	"issuetitle";				//monograph title
	 	public static final String ME		=	null;						//abbr monograph title
	 	public static final String MD  		=	"confdate";					//meeting date
	 	public static final String M1		=	null;						//diff version of meeting date
	 	public static final String M2		=	null;						//diff version of meeting date
	 	public static final String MC		=	"city";						//meeting city
	 	public static final String MS		=	"state";					//meeting state
	 	public static final String MV		=	null;						//meeting providence
	 	public static final String MY		= 	"conflocation.country";		//meeting country
	 	public static final String MH		=	"mainterm";					//main heading when descriptors.type="CMH"
	 	public static final String MN		=	null;						//monthly publication number
	 	public static final String NR		=	"bibliography.refcount";	//number of referrences
	 	public static final String NV		=	null;						//number of volumes
	 	public static final String OA		=	null;						//origional accession number
	 	public static final String PA		=	"reportnumber";				//paper number
	 	public static final String PN		=	"publishername";			//publisher department
	 	public static final String PC		=	"publisheraddress";			//publisher city
	 	public static final String PS		=	"publisheraddress";			//publisher state
	 	public static final String PV		=	"publisheraddress";			//publisher providence
	 	public static final String PY		=	"publisheraddress";			//publisher country
	 	public static final String PP		=	"pagerange";				//number of page

	 	public static final String SD		=	"publicationdate";			//issue date
	 	public static final String SD_YEAR	=	"publicationdate_year";		//issue year
	 	public static final String SD_MONTH	=	"publicationdate_month";	//issue month
	 	public static final String SD_DAY	=	"publicationdate_day";		//issue day

	 	public static final String ST		=	"sourcetitle";				//serial title
	 	public static final String SE		=	"sourcetitle-abbrev";		//serial abbr. title
	 	public static final String SN		=	"issn";						//issn.
	 	public static final String SP		=	"confsponsor";				//sponsor name
	 	public static final String SU		=	null;						//subset code
	 	public static final String TG		=	null;						//subset tag
	 	public static final String TI		=	"titletext";				//article title
	 	public static final String TR		=	null;						//treatment type
	 	public static final String TT		=	"translated-sourcetitle";	//translated title
	 	public static final String UP		=	null;						//update stamp
	 	public static final String VO		=	"voliss.volume";			//volume number
	 	public static final String VT		=	"volumetitle";				//volume title
	 	public static final String XP		=	"pages";					//page range
	 	public static final String YN		=	null;						//annual publication number

	 	public static final String YR		=	"publicationyear";			//publication year

	 	public static final String RN		=	null;						//
	 	public static final String SH		=	null;						//subheading
	 	public static final String WT		=	null;						//working title
	 	public static final String AM		=	"organization";				//affiliation corperate division
	 	public static final String CAL		=	"classification";			//primary cal code
	 	public static final String SC		=	null;						//from toc(Sponsor city)
	 	public static final String SS		=	null;						//sponsor state
	 	public static final String SV		=	null;						//sponsor provience
	 	public static final String SY		=	null;						//sponsor county
	 	public static final String PO		=	null;						//publisher order number
	 	public static final String VN		=	null;						//availability
	 	public static final String VM		=	null;						//availability dept
	 	public static final String VC		=	null;						//availability city
	 	public static final String VS		=	null;						//availability state
	 	public static final String VV		=	null;						//availability providence
	 	public static final String VY		=	null;						//availability country
	 	public static final String VX		=	null;						//availability order number
	 	public static final String EX		=	"itemid";					//accession number
	 	public static final String LF		=	null;						//expanded language field
	 	public static final String UR		=	null;						//
	 	public static final String YP		=	null;						//year publisher
	 	public static final String ML		=	null;						//
	 	public static final String ASS		=	null;						//association state
	 	public static final String OD		=	null;						//
	 	public static final String RT		=	null;						//record type;
	 	public static final String LOAD_NUMBER 	=null;						//loading number
	 	public static final String DO		=	"ce:doi";					//DOI
	 	public static final String PI		=	"ce:pii";					//
	 	public static final String AR		=	"pages";					//page range
	 	public static final String EN		=	null;


}
