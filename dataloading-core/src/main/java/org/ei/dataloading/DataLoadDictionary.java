/*
 * Created on May 30, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ei.dataloading;
import java.util.*;
import java.util.regex.*;
import org.apache.commons.text.StringEscapeUtils;
import org.ei.dataloading.bd.loadtime.BdParser;
import org.ei.data.bd.runtime.BDDocBuilder;

/**
 * @author solovyevat
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DataLoadDictionary
{
	private static final Pattern ENTITY_PATTERN = Pattern.compile("(&[^\\s]+?;)");
	private static final Pattern ENTITY_PATTERN1 = Pattern.compile("(&amp;)");
	private static final Pattern ENTITY_PATTERN2 = Pattern.compile("&");
	private static final Pattern ENTITY_PATTERN3 = Pattern.compile("(&#[^\\s]+?;)");
	private static Map<String, String> entityMap = new HashMap<String, String>();
	static{
		
		entityMap.put("&quot;", "&#34;");
		entityMap.put("&amp;", "&#38;");
		entityMap.put("&lt;", "&#60;");
		entityMap.put("&gt;", "&#62;");
		entityMap.put("&Ccedil;","&#128;");
		entityMap.put("&uuml;","&#129;");
		entityMap.put("&eacute;","&#130;");
		entityMap.put("&acirc;","&#131;");
		entityMap.put("&auml;","&#132;");
		entityMap.put("&agrave;","&#133;");
		entityMap.put("&aring;","&#134;");
		entityMap.put("&ccedil;","&#135;");
		entityMap.put("&ecirc;","&#136;");
		entityMap.put("&euml;","&#137;");
		entityMap.put("&egrave;","&#138;");
		entityMap.put("&iuml;","&#139;");
		entityMap.put("&icirc;","&#140;");
		entityMap.put("&igrave;","&#141;");
		entityMap.put("&Auml;","&#142;");
		entityMap.put("&Aring;","&#143;");
		entityMap.put("&Eacute;","&#144;");
		entityMap.put("&aelig;","&#145;");
		entityMap.put("&AElig;","&#146;");
		entityMap.put("&ocirc;","&#147;");
		entityMap.put("&ouml;","&#148;");
		entityMap.put("&ograve;","&#149;");
		entityMap.put("&ucirc;","&#150;");
		entityMap.put("&ugrave;","&#151;");
		entityMap.put("&yuml;","&#152;");
		entityMap.put("&Ouml;","&#153;");
		entityMap.put("&Uuml;","&#154;");
		entityMap.put("&pound;","&#156;");
		entityMap.put("&yen;","&#157;");
    
	    entityMap.put("&nbsp;", "&#160;");
	    entityMap.put("&iexcl;", "&#161;");
	    entityMap.put("&cent;", "&#162;");
	    entityMap.put("&pound;", "&#163;");
	    entityMap.put("&curren;", "&#164;");
	    entityMap.put("&yen;", "&#165;");
	    entityMap.put("&brvbar;", "&#166;");
	    entityMap.put("&sect;", "&#167;");
	    entityMap.put("&uml;", "&#168;");
	    entityMap.put("&copy;", "&#169;");
	    entityMap.put("&ordf;", "&#170;");
	    entityMap.put("&laquo;", "&#171;");
	    entityMap.put("&not;", "&#172;");
	    entityMap.put("&shy;", "&#173;");
	    entityMap.put("&reg;", "&#174;");
	    entityMap.put("&macr;", "&#175;");
	    entityMap.put("&deg;", "&#176;");
	    entityMap.put("&plusmn;", "&#177;");
	    entityMap.put("&sup2;", "&#178;");
	    entityMap.put("&sup3;", "&#179;");
	    entityMap.put("&acute;", "&#180;");
	    entityMap.put("&micro;", "&#181;");
	    entityMap.put("&para;", "&#182;");
	    entityMap.put("&middot;", "&#183;");
	    entityMap.put("&cedil;", "&#184;");
	    entityMap.put("&sup1;", "&#185;");
	    entityMap.put("&ordm;", "&#186;");
	    entityMap.put("&raquo;", "&#187;");
	    entityMap.put("&frac14;", "&#188;");
	    entityMap.put("&frac12;", "&#189;");
	    entityMap.put("&frac34;", "&#190;");
	    entityMap.put("&iquest;", "&#191;");
	    entityMap.put("&Agrave;", "&#192;");
	    entityMap.put("&Aacute;", "&#193;");
	    entityMap.put("&Acirc;", "&#194;");
	    entityMap.put("&Atilde;", "&#195;");
	    entityMap.put("&Auml;", "&#196;");
	    entityMap.put("&Aring;", "&#197;");
	    entityMap.put("&AElig;", "&#198;");
	    entityMap.put("&Ccedil;", "&#199;");
	    entityMap.put("&Egrave;", "&#200;");
	    entityMap.put("&Eacute;", "&#201;");
	    entityMap.put("&Ecirc;", "&#202;");
	    entityMap.put("&Euml;", "&#203;");
	    entityMap.put("&Igrave;", "&#204;");
	    entityMap.put("&Iacute;", "&#205;");
	    entityMap.put("&Icirc;", "&#206;");
	    entityMap.put("&Iuml;", "&#207;");
	    entityMap.put("&ETH;", "&#208;");
	    entityMap.put("&Ntilde;", "&#209;");
	    entityMap.put("&Ograve;", "&#210;");
	    entityMap.put("&Oacute;", "&#211;");
	    entityMap.put("&Ocirc;", "&#212;");
	    entityMap.put("&Otilde;", "&#213;");
	    entityMap.put("&Ouml;", "&#214;");
	    entityMap.put("&times;", "&#215;");
	    entityMap.put("&Oslash;", "&#216;");
	    entityMap.put("&Ugrave;", "&#217;");
	    entityMap.put("&Uacute;", "&#218;");
	    entityMap.put("&Ucirc;", "&#219;");
	    entityMap.put("&Uuml;", "&#220;");
	    entityMap.put("&Yacute;", "&#221;");
	    entityMap.put("&THORN;", "&#222;");
	    entityMap.put("&szlig;", "&#223;");
	    entityMap.put("&agrave;", "&#224;");
	    entityMap.put("&aacute;", "&#225;");
	    entityMap.put("&acirc;", "&#226;");
	    entityMap.put("&atilde;", "&#227;");
	    entityMap.put("&auml;", "&#228;");
	    entityMap.put("&aring;", "&#229;");
	    entityMap.put("&aelig;", "&#230;");
	    entityMap.put("&ccedil;", "&#231;");
	    entityMap.put("&egrave;", "&#232;");
	    entityMap.put("&eacute;", "&#233;");
	    entityMap.put("&ecirc;", "&#234;");
	    entityMap.put("&euml;", "&#235;");
	    entityMap.put("&igrave;", "&#236;");
	    entityMap.put("&iacute;", "&#237;");
	    entityMap.put("&icirc;", "&#238;");
	    entityMap.put("&iuml;", "&#239;");
	    entityMap.put("&eth;", "&#240;");
	    entityMap.put("&ntilde;", "&#241;");
	    entityMap.put("&ograve;", "&#242;");
	    entityMap.put("&oacute;", "&#243;");
	    entityMap.put("&ocirc;", "&#244;");
	    entityMap.put("&otilde;", "&#245;");
	    entityMap.put("&ouml;", "&#246;");
	    entityMap.put("&divide;", "&#247;");
	    entityMap.put("&oslash;", "&#248;");
	    entityMap.put("&ugrave;", "&#249;");
	    entityMap.put("&uacute;", "&#250;");
	    entityMap.put("&ucirc;", "&#251;");
	    entityMap.put("&uuml;", "&#252;");
	    entityMap.put("&yacute;", "&#253;");
	    entityMap.put("&thorn;", "&#254;");
	    entityMap.put("&yuml;", "&#255;");
	    
	    entityMap.put("&Lstrok;","&#321;");
	    entityMap.put("&lstrok;","&#322;");
	    
	    entityMap.put("&OElig;", "&#338;");
	    entityMap.put("&oelig;", "&#339;");
	    entityMap.put("&Scaron;", "&#352;");
	    entityMap.put("&scaron;", "&#353;");
	    entityMap.put("&Yuml;", "&#376;");	    
	    entityMap.put("&fnof;","&#402;");	    
	    entityMap.put("&circ;", "&#710;");
	    entityMap.put("&tilde;", "&#732;");
	    entityMap.put("&grave;","&#768;"); 
	    entityMap.put("&acute;","&#769;"); 
	    entityMap.put("&circ;","&#770;"); 
	    entityMap.put("&tilde;","&#771;"); 
	    entityMap.put("&macr;","&#772;"); 
	    entityMap.put("&dot;","&#775;"); 
	    entityMap.put("&die;","&#776;"); 
	    entityMap.put("&ring;","&#778;"); 
	    entityMap.put("&caron;","&#780;"); 
	    entityMap.put("&cedil;","&#807;"); 
	   	
	    entityMap.put("&Alpha;", "&#913;");
	    entityMap.put("&Beta;", "&#914;");
	    entityMap.put("&Gamma;", "&#915;");
	    entityMap.put("&Delta;", "&#916;");
	    entityMap.put("&Epsilon;", "&#917;");
	    entityMap.put("&Zeta;", "&#918;");
	    entityMap.put("&Eta;", "&#919;");
	    entityMap.put("&Theta;", "&#920;");
	    entityMap.put("&Iota;", "&#921;");
	    entityMap.put("&Kappa;", "&#922;");
	    entityMap.put("&Lambda;", "&#923;");
	    entityMap.put("&Mu;", "&#924;");
	    entityMap.put("&Nu;", "&#925;");
	    entityMap.put("&Xi;", "&#926;");
	    entityMap.put("&Omicron;", "&#927;");
	    entityMap.put("&Pi;", "&#928;");
	    entityMap.put("&Rho;", "&#929;");
	    entityMap.put("&Sigma;", "&#931;");
	    entityMap.put("&Tau;", "&#932;");
	    entityMap.put("&Upsi;", "&#933;");
	    entityMap.put("&Phi;", "&#934;");
	    entityMap.put("&Chi;", "&#935;");
	    entityMap.put("&Psi;", "&#936;");
	    entityMap.put("&Omega;", "&#937;");
	    entityMap.put("&alpha;", "&#945;");
	    entityMap.put("&beta;", "&#946;");
	    entityMap.put("&gamma;", "&#947;");
	    entityMap.put("&delta;", "&#948;");
	    entityMap.put("&epsi;", "&#949;");
	    entityMap.put("&zeta;", "&#950;");
	    entityMap.put("&eta;", "&#951;");
	    entityMap.put("&theta;", "&#952;");
	    entityMap.put("&iota;", "&#953;");
	    entityMap.put("&kappa;", "&#954;");
	    entityMap.put("&lambda;", "&#955;");
	    entityMap.put("&mu;", "&#956;");
	    entityMap.put("&nu;", "&#957;");
	    entityMap.put("&xi;", "&#958;");
	    entityMap.put("&omicron;", "&#959;");
	    entityMap.put("&pi;", "&#960;");
	    entityMap.put("&rho;", "&#961;");
	    entityMap.put("&sigmaf;", "&#962;");
	    entityMap.put("&sigma;", "&#963;");
	    entityMap.put("&tau;", "&#964;");
	    entityMap.put("&upsi;", "&#965;");
	    entityMap.put("&phi;", "&#966;");
	    entityMap.put("&chi;", "&#967;");
	    entityMap.put("&psi;", "&#968;");
	    entityMap.put("&omega;", "&#969;");
	    entityMap.put("&theta;", "&#977;");
	    entityMap.put("&upsih;", "&#978;");
	    entityMap.put("&straightphi;", "&#981;");
	    entityMap.put("&piv;", "&#982;"); 
	    entityMap.put("&Gammad;", "&#988;");//GREEK LETTER DIGAMMA (F) Ïœ
	    entityMap.put("&gammad;", "&#989;");//GREEK SMALL LETTER DIGAMMA (f) Ï�

	  	    
	    entityMap.put("&IOcy;","&#1025;");	//CyrilliccapitalIO,likecapitalEumlaut
	    entityMap.put("&DJcy;","&#1026;");	//CyrilliccapitalletterDJ
	    entityMap.put("&GJcy;","&#1027;");	//CyrilliccapitalletterGJ
	    entityMap.put("&Jukcy;","&#1028;");	//CyrilliccapitalletterJuk
	    entityMap.put("&DScy;","&#1029;");	//CyrilliccapitalletterDS
	    entityMap.put("&Iukcy;","&#1030;");	//CyrillicByelorussionI
	    entityMap.put("&YIcy;","&#1031;");	//CyrilliccapitalletterYI
	    entityMap.put("&Jsercy;","&#1032;"); //CyrilliccapitalletterJser
	    entityMap.put("&LJcy;","&#1033;");	//CyrilliccapitalletterLJ
	    entityMap.put("&NJcy;","&#1034;");	//CyrilliccapitalletterNJ
	    entityMap.put("&TSHcy;","&#1035;");	//CyrilliccapitalletterTSH
	    entityMap.put("&KJcy;","&#1036;");	//CyrilliccapitalletterKJ
	    entityMap.put("&Ubrcy;","&#1038;");	//CyrillicUcapitalletterbreve
	    entityMap.put("&DZcy;","&#1039;");	//CyrilliccapitalletterDZ
	    entityMap.put("&Acy;","&#1040;");	//CyrilliccapitalletterA
	    entityMap.put("&Bcy;","&#1041;");	//CyrilliccapitalletterBE
	    entityMap.put("&Vcy;","&#1042;");	//CyrilliccapitalletterVE
	    entityMap.put("&Gcy;","&#1043;");	//CyrilliccapitalletterGHE
	    entityMap.put("&Dcy;","&#1044;");	//CyrilliccapitalletterDE
	    entityMap.put("&IEcy;","&#1045;");	//CyrilliccapitalletterEE
	    entityMap.put("&ZHcy;","&#1046;");	//CyrilliccapitalletterZHE
	    entityMap.put("&Zcy;","&#1047;");	//CyrilliccapitalletterZE
	    entityMap.put("&Icy;","&#1048;");	//CyrilliccapitalletterI
	    entityMap.put("&Jcy;","&#1049;");	//CyrilliccapitalshortI
	    entityMap.put("&Kcy;","&#1050;");	//CyrilliccapitalletterKA
	    entityMap.put("&Lcy;","&#1051;");	//CyrilliccapitalletterEL
	    entityMap.put("&Mcy;","&#1052;");	//CyrilliccapitalletterM
	    entityMap.put("&Ncy;","&#1053;");	//CyrilliccapitalletterEN
	    entityMap.put("&Ocy;","&#1054;");	//CyrilliccapitalletterO
	    entityMap.put("&Pcy;","&#1055;");	//CyrilliccapitalletterPE
	    entityMap.put("&Rcy;","&#1056;");	//CyrilliccapitalletterER
	    entityMap.put("&Scy;","&#1057;");	//CyrilliccapitalletterES
	    entityMap.put("&Tcy;","&#1058;");	//CyrilliccapitalletterTE
	    entityMap.put("&Ucy;","&#1059;");	//CyrilliccapitalletterU
	    entityMap.put("&Fcy;","&#1060;");	//CyrilliccapitalletterEF
	    entityMap.put("&KHcy;","&#1061;");	//CyrilliccapitalletterHA
	    entityMap.put("&TScy;","&#1062;");	//CyrilliccapitalletterTSE
	    entityMap.put("&CHcy;","&#1063;");	//CyrilliccapitalletterCHE
	    entityMap.put("&SHcy;","&#1064;");	//CyrilliccapitalletterSHA
	    entityMap.put("&SHCHcy;","&#1065;"); 	//CyrilliccapitalletterSHCHA
	    entityMap.put("&HARDcy;","&#1066;"); 	//Cyrilliccapitalhardsign
	    entityMap.put("&Ycy;","&#1067;");	//CyrilliccapitalletterYERU
	    entityMap.put("&SOFTcy;","&#1068;"); 	//Cyrilliccapitalsoftsign
	    entityMap.put("&Ecy;","&#1069;");	//CyrilliccapitalletterE
	    entityMap.put("&YUcy;","&#1070;");	//CyrilliccapitalletterYU
	    entityMap.put("&YAcy;","&#1071;");	//CyrilliccapitalletterYA
	    entityMap.put("&acy;","&#1072;");	//Cyrillicsmalllettera
	    entityMap.put("&bcy;","&#1073;");	//Cyrillicsmallletterbe
	    entityMap.put("&vcy;","&#1074;");	//Cyrillicsmallletterve
	    entityMap.put("&gcy;","&#1075;");	//Cyrillicsmallletterghe
	    entityMap.put("&dcy;","&#1076;");	//Cyrillicsmallletterde
	    entityMap.put("&iecy;","&#1077;");	//Cyrillicsmallletterie
	    entityMap.put("&zhcy;","&#1078;");	//Cyrillicsmallletterzhe
	    entityMap.put("&zcy;","&#1079;");	//Cyrillicsmallletterze
	    entityMap.put("&icy;","&#1080;");	//Cyrillicsmallletteri
	    entityMap.put("&jcy;","&#1081;");	//Cyrillicsmallshorti
	    entityMap.put("&kcy;","&#1082;");	//Cyrillicsmallletterka
	    entityMap.put("&lcy;","&#1083;");	//Cyrillicsmallletterel
	    entityMap.put("&mcy;","&#1084;");	//Cyrillicsmallletterem
	    entityMap.put("&ncy;","&#1085;");	//Cyrillicsmallletteren
	    entityMap.put("&ocy;","&#1086;");	//Cyrillicsmalllettero
	    entityMap.put("&pcy;","&#1087;");	//Cyrillicsmallletterpe
	    entityMap.put("&rcy;","&#1088;");	//Cyrillicsmallletterer
	    entityMap.put("&scy;","&#1089;");	//Cyrillicsmallletteres
	    entityMap.put("&tcy;","&#1090;");	//Cyrillicsmallletterte
	    entityMap.put("&ucy;","&#1091;");	//Cyrillicsmallletteru
	    entityMap.put("&fcy;","&#1092;");	//Cyrillicsmallletteref
	    entityMap.put("&khcy;","&#1093;");	//Cyrillicsmallletterha
	    entityMap.put("&tscy;","&#1094;");	//Cyrillicsmalllettertse
	    entityMap.put("&chcy;","&#1095;");	//Cyrillicsmallletterche
	    entityMap.put("&shcy;","&#1096;");	//Cyrillicsmalllettersha
	    entityMap.put("&shchcy;","&#1097;");	//Cyrillicsmalllettershcha
	    entityMap.put("&hardcy;","&#1098;");	//Cyrillicsmallhardsign
	    entityMap.put("&ycy;","&#1099;");	//Cyrillicsmallletteryeru
	    entityMap.put("&softcy;","&#1100;");	//Cyrillicsmallsoftsign
	    entityMap.put("&ecy;","&#1101;");	//Cyrillicsmalllettere
	    entityMap.put("&yucy;","&#11002;");	//Cyrillicsmallletteryu
	    entityMap.put("&yacy;","&#1103;");	//Cyrillicsmallletterya
	    entityMap.put("&iocy;","&#1105;");	//Cyrillicsmallletterio,likesmalleumlaut
	    entityMap.put("&djcy;","&#1106;");	//Cyrillicsmallletterdj
	    entityMap.put("&gjcy;","&#1107;");	//Cyrillicsmalllettergj
	    entityMap.put("&jukcy;","&#1108;");	//Cyrillicsmallletterjuk
	    entityMap.put("&dscy;","&#1109;");	//Cyrillicsmallletterds
	    entityMap.put("&iukcy;","&#1110;");	//CyrillicsmallletterByelorussioni
	    entityMap.put("&yicy;","&#1111;");	//Cyrillicsmallletteryi
	    entityMap.put("&jsercy;","&#1112;");	//Cyrillicsmallletterjser
	    entityMap.put("&ljcy;","&#1113;");	//Cyrillicsmallletterlj
	    entityMap.put("&njcy;","&#1114;");	//Cyrillicsmallletternj
	    entityMap.put("&tshcy;","&#1115;");	//Cyrillicsmalllettertsh
	    entityMap.put("&kjcy;","&#1116;");	//Cyrillicsmallletterkj
	    entityMap.put("&ubrcy;","&#1118;");	//Cyrillicsmallletterubreve
	    entityMap.put("&dzcy;","&#1119;");	//Cyrillicsmallletterdz	    
	    entityMap.put("&aleph;","&#1488;"); 	//hebrew aleph
		    
	    entityMap.put("&ensp;", "&#8194;");
	    entityMap.put("&emsp;", "&#8195;");
	    entityMap.put("&thinsp;", "&#8201;");
	    entityMap.put("&zwnj;", "&#8204;");
	    entityMap.put("&zwj;", "&#8205;");
	    entityMap.put("&lrm;", "&#8206;");
	    entityMap.put("&rlm;", "&#8207;");
	    entityMap.put("&ndash;", "&#8211;");
	    entityMap.put("&mdash;", "&#8212;");
	    entityMap.put("&lsquo;", "&#8216;");
	    entityMap.put("&rsquo;", "&#8217;");
	    entityMap.put("&sbquo;", "&#8218;");
	    entityMap.put("&ldquo;", "&#8220;");
	    entityMap.put("&rdquo;", "&#8221;");
	    entityMap.put("&bdquo;", "&#8222;");
	    entityMap.put("&dagger;", "&#8224;");
	    entityMap.put("&Dagger;", "&#8225;");
	    entityMap.put("&permil;", "&#8240;");
	    entityMap.put("&lsaquo;", "&#8249;");
	    entityMap.put("&rsaquo;", "&#8250;");	    
	    entityMap.put("&bull;", "&#8226;");
	    entityMap.put("&hellip;", "&#8230;");
	    entityMap.put("&permil;", "&#8240;"); //per mille sign
	    entityMap.put("&prime;", "&#8242;");
	    entityMap.put("&Prime;", "&#8243;");
	    entityMap.put("&tprime;", "&#8244;"); //triple prime,
	    entityMap.put("&Isaquo;", "&#8249;"); //single left-pointing angle quotation mark
	    entityMap.put("&rsaquo;", "&#8250;"); //single right-pointing angle quotation mark
	    entityMap.put("&oline;", "&#8254;");
	    entityMap.put("&Hscr;", "&#8259;");  //script capital H
	    entityMap.put("&frasl;", "&#8260;");	    
	    entityMap.put("&Hdbl;", "&#8261;"); 
	    entityMap.put("&plankv;", "&#8263;"); //planck constant over two pi
	    entityMap.put("&euro;", "&#8364;"); 
	    entityMap.put("&image;", "&#8465;");  //blackletter capital I
	    entityMap.put("&Lscr;", "&#8466;");   //script capital L
	    entityMap.put("&ell;", "&#8467;");
	    entityMap.put("&Ndbl;", "&#8469;");   //double-struck capital N	    
	    entityMap.put("&weierp;", "&#8472;");
	    entityMap.put("&Pdbl;", "&#8473;");   //double-struck capital P
	    entityMap.put("&Qdbl;", "&#8474;");   //double-struck capital Q
	    entityMap.put("&Rscr;", "&#8475;");   //script capital R
	    entityMap.put("&real;", "&#8476;");
	    entityMap.put("&Rdbl;", "&#8477;");
	    entityMap.put("&trade;", "&#8482;");
	    
	    entityMap.put("&Zdbl;", "&#8484;");  //double-struck capital Z
	    entityMap.put("&mho;", "&#8487;");    //inverted ohm sign
	    entityMap.put("&Bscr;", "&#8492;"); 
	    entityMap.put("&Escr;", "&#8496;"); 
	    entityMap.put("&Fscr;", "&#8497;");
	    entityMap.put("&Mscr;", "&#8499;"); 	    
	    entityMap.put("&alefsym;", "&#8501;");
	   	    
	    entityMap.put("&larr;", "&#8592;");
	    entityMap.put("&uarr;", "&#8593;");
	    entityMap.put("&rarr;", "&#8594;");
	    entityMap.put("&darr;", "&#8595;");
	    entityMap.put("&harr;", "&#8596;");	    
	    entityMap.put("&varr;", "&#8597;");
	    entityMap.put("&nwarr;", "&#8598;");  //north west arrow
	    entityMap.put("&nearr;", "&#8599;"); 
	    entityMap.put("&searr;", "&#8600;"); 
	    entityMap.put("&swarr;", "&#8601;"); 	
	    entityMap.put("&Larr;", "&#8606;"); 
	    entityMap.put("&Rarr;", "&#8608;");    
	    entityMap.put("&crarr;", "&#8629;");	    
	    entityMap.put("&lharu;", "&#8636;"); 
	    entityMap.put("&lhard;", "&#8637;"); 
	    entityMap.put("&rharu;", "&#8640;");
	    entityMap.put("&rhard;", "&#8641;");  //rightwards harpoon with barb downwards
	    entityMap.put("&rlarr2;", "&#8644;"); //right over left arrow N.B. rlarr in ES grid
	    entityMap.put("&lrarr2;", "&#8646;"); //left over right arrow N.B. lrarr in ES grid
	    entityMap.put("&lrhar2;", "&#8651;"); //left over right harpoon N.B. lrhar in ES grid
	    entityMap.put("&rlhar2;", "&#8652;");  //right over left harpoon N.B. rlhar in ES grid	    
	    entityMap.put("&lArr;", "&#8656;");
	    entityMap.put("&uArr;", "&#8657;");
	    entityMap.put("&rArr;", "&#8658;");
	    entityMap.put("&dArr;", "&#8659;");
	    entityMap.put("&hArr;", "&#8660;");
	    entityMap.put("&forall:", "&#8704;");
	    entityMap.put("&part;", "&#8706;");
	    entityMap.put("&exist;", "&#8707;");
	    entityMap.put("&nexist;", "&#8708;");  //there does not exist
	    entityMap.put("&empty;", "&#8709;");
	    entityMap.put("&nabla;", "&#8711;");
	    entityMap.put("&isin;", "&#8712;");
	    entityMap.put("&notin;", "&#8713;");
	    entityMap.put("&ni;", "&#8715;");
	    entityMap.put("&notni;", "&#8716;");  //does not contain as member
	    entityMap.put("&prod;", "&#8719;");
	    entityMap.put("&coprod;", "&#8720;"); //n-ary coproduct
	    entityMap.put("&sum;", "&#8721;");	    
	    entityMap.put("&minus;", "&#8722;");	    
	    entityMap.put("&mnplus;", "&#8723;"); //minus-or-plus sign	    
	    entityMap.put("&lowast;", "&#8727;");
	    entityMap.put("&radic;", "&#8730;");
	    entityMap.put("&prop;", "&#8733;");
	    entityMap.put("&infin;", "&#8734;");
	    entityMap.put("&ang;", "&#8736;");	    
	    entityMap.put("&par;", "&#8741;"); 
	    entityMap.put("&npar;", "&#8742;");
	    entityMap.put("&and;", "&#8743;"); 
	    entityMap.put("&or;", "&#8744;");
	    entityMap.put("&cap;", "&#8745;");
	    entityMap.put("&cup;", "&#8746;");
	    entityMap.put("&int;", "&#8747;");
	    entityMap.put("&conint;", "&#8750;");
	    entityMap.put("&there4;", "&#8756;"); //therefore
	    entityMap.put("&sim;", "&#8764;"); 
	    entityMap.put("&nsim;", "&#8769;");	    	   	  
	    entityMap.put("&sime;","&#8771;");
	    entityMap.put("&cong;", "&#8773;");
	    entityMap.put("&asymp;", "&#8776;");	    
	    entityMap.put("&nap;", "&#8777;");
	    entityMap.put("&ape;", "&#8778;");
	    entityMap.put("&efDot;", "&#8786;"); //approximately equal to or the image of
	    entityMap.put("&erDot;", "&#8787;");  //image of or approximately equal to
	    entityMap.put("&wedgeq;", "&#8793;");
	    entityMap.put("&ne;", "&#8800;");
	    entityMap.put("&equiv;", "&#8801;");
	    entityMap.put("&nequiv;", "&#8802;");//not identical to
	    entityMap.put("&le;", "&#8804;");
	    entityMap.put("&ge;", "&#8805;");
	    entityMap.put("&lE;", "&#8806;"); 
	    entityMap.put("&gE;", "&#8807;");
	    entityMap.put("&Lt;", "&#8810;");
	    entityMap.put("&Gt;", "&#8811;"); 
	    entityMap.put("&nlt;", "&#8814;"); 
	    entityMap.put("&ngt;", "&#8815;"); 
	    entityMap.put("&nle;", "&#8816;"); 
	    entityMap.put("&nge;", "&#8817;"); 
	    entityMap.put("&lsim;","&#8818;");
	    entityMap.put("&gsim;","&#8819;");
	    entityMap.put("&pr;","&#8826;");
	    entityMap.put("&sc;","&#8827;"); 
	    entityMap.put("&sub;", "&#8834;");
	    entityMap.put("&sup;", "&#8835;");
	    entityMap.put("&nsub;", "&#8836;");
	    entityMap.put("&nsup;", "&#8837;");
	    entityMap.put("&sube;", "&#8838;");
	    entityMap.put("&supe;", "&#8839;");	    
	    entityMap.put("&nsube;", "&#8840;");
	    entityMap.put("&nsupe;", "&#8841;");
	    entityMap.put("&oplus;", "&#8853;");
	    entityMap.put("&ominus;", "&#8854;");
	    entityMap.put("&otimes;", "&#8855;");
	    entityMap.put("&odot;", "&#8857;");
	    entityMap.put("&vdash;", "&#8866;");
	    entityMap.put("&dashv;", "&#8867;");
	    entityMap.put("&top;", "&#8868;");
	    entityMap.put("&perp;", "&#8869;");
	    entityMap.put("&or;", "&#8870;");
	    entityMap.put("&ltrie;", "&#8884;");
	    entityMap.put("&rtrie;", "&#8885;"); 
	    entityMap.put("&sdot;", "&#8901;");
	    entityMap.put("&ltimes;", "&#8905;");
	    entityMap.put("&rtimes;", "&#8906;");
	    entityMap.put("&Ll;", "&#8920;");
	    entityMap.put("&Gg;", "&#8921;");
	    entityMap.put("&vellip;", "&#8942;"); //vertical ellipsis
	    entityMap.put("&mellip;","&#8943;");	
	    entityMap.put("&lceil;", "&#8968;");
	    entityMap.put("&rceil;", "&#8969;");
	    entityMap.put("&lfloor;", "&#8970;");
	    entityMap.put("&rfloor;", "&#8971;");
	    entityMap.put("&lang;", "&#9001;");
	    entityMap.put("&rang;","&#9002;");	 	
		entityMap.put("&squf;","&#9632;");		//black square
	    entityMap.put("&squ;","&#9633;");		//white square	 
	    entityMap.put("&utrif;","&#9650;");  	//black up-pointing triangle
	    entityMap.put("&utri;","&#9651;");  

	    entityMap.put("&rtrif;","&#9654;"); 	//black down-pointing triangle
	    entityMap.put("&rtri;","&#9655;");		//white right-pointing triangle	    
	    entityMap.put("&dtrif;","&#9660;"); 	//black up-pointing triangle
	    entityMap.put("&dtri;","&#9661;");		//white down-pointing triangle	    
	    entityMap.put("&ltrif;","&#9664;"); 	//black left-pointing triangle
	    entityMap.put("&ltri;","&#9665;");		//white left-pointing triangle
	    entityMap.put("&diams;","&#9670;");		//black diamond   
	    entityMap.put("&diam;","&#9671;"); 		//white diamond
	    entityMap.put("&loz;", "&#9674;");		//lozenge
	    entityMap.put("&cir;","&#9675;");		//white circle	    
	    entityMap.put("&female;", "&#9792;");	//female sign
	    entityMap.put("&male;","&#9794;");		//male sign	      
	    entityMap.put("&spades;", "&#9824;");
	    entityMap.put("&clubs;", "&#9827;");
	    entityMap.put("&hearts;", "&#9829;");
	    entityMap.put("&diams;", "&#9830;");	
	    
	    entityMap.put("&LessLess;", "&#10913;"); //much less than (double) (EW=Bkl)
	    entityMap.put("&GreaterGreater;", "&#10914;"); 	//much greater than (double) (EW=Bml)
	    entityMap.put("&lang;", "&#12296;"); 	//left angle bracket
	    entityMap.put("&rang;", "&#12297;");	//right angle bracket

	}
	
	private static Map<String, String> badCharacterMap = new HashMap<String, String>();
    static {
        // &die is the same as an &uml
        badCharacterMap.put("a&die;", "&#228;");
        badCharacterMap.put("e&die;", "&#235;");
        badCharacterMap.put("i&die;", "&#239;");
        badCharacterMap.put("o&die;", "&#246;");
        badCharacterMap.put("u&die;", "&#252;");
        badCharacterMap.put("A&die;", "&#196;");
        badCharacterMap.put("E&die;", "&#203;");
        badCharacterMap.put("I&die;", "&#207;");
        badCharacterMap.put("O&die;", "&#214;");
        badCharacterMap.put("U&die;", "&#220;");

        badCharacterMap.put("A&grave;", "&#192;");
        badCharacterMap.put("A&acute;", "&#193;");
        badCharacterMap.put("A&circ;", "&#194;");
        badCharacterMap.put("A&tilde;", "&#195;");
        badCharacterMap.put("A&uml;", "&#196;");
        badCharacterMap.put("A&ring;", "&#197;");
        badCharacterMap.put("C&cedil;", "&#199;");
        badCharacterMap.put("E&grave;", "&#200;");
        badCharacterMap.put("E&acute;", "&#201;");
        badCharacterMap.put("E&circ;", "&#202;");
        badCharacterMap.put("E&uml;", "&#203;");
        badCharacterMap.put("I&grave;", "&#204;");
        badCharacterMap.put("I&acute;", "&#205;");
        badCharacterMap.put("I&circ;", "&#206;");
        badCharacterMap.put("I&uml;", "&#207;");
        badCharacterMap.put("N&tilde;", "&#209;");
        badCharacterMap.put("O&grave;", "&#210;");
        badCharacterMap.put("O&acute;", "&#211;");
        badCharacterMap.put("O&circ;", "&#212;");
        badCharacterMap.put("O&tilde;", "&#213;");
        badCharacterMap.put("O&uml;", "&#214;");
        badCharacterMap.put("O&slash;", "&#216;");
        badCharacterMap.put("S&caron;", "&#352;");
        badCharacterMap.put("U&grave;", "&#217;");
        badCharacterMap.put("U&acute;", "&#218;");
        badCharacterMap.put("U&circ;", "&#219;");
        badCharacterMap.put("U&uml;", "&#220;");
        badCharacterMap.put("Y&acute;", "&#221;");
        badCharacterMap.put("Y&uml;", "&#376;");
        badCharacterMap.put("a&grave;", "&#224;");
        badCharacterMap.put("a&acute;", "&#225;");
        badCharacterMap.put("a&circ;", "&#226;");
        badCharacterMap.put("a&tilde;", "&#227;");
        badCharacterMap.put("a&uml;", "&#228;");
        badCharacterMap.put("a&ring;", "&#229;");
        badCharacterMap.put("c&cedil;", "&#231;");
        badCharacterMap.put("e&grave;", "&#232;");
        badCharacterMap.put("e&acute;", "&#233;");
        badCharacterMap.put("e&circ;", "&#234;");
        badCharacterMap.put("e&uml;", "&#235;");
        badCharacterMap.put("i&grave;", "&#236;");
        badCharacterMap.put("i&acute;", "&#237;");
        badCharacterMap.put("i&circ;", "&#238;");
        badCharacterMap.put("i&uml;", "&#239;");
        badCharacterMap.put("n&tilde;", "&#241;");
        badCharacterMap.put("o&grave;", "&#242;");
        badCharacterMap.put("o&acute;", "&#243;");
        badCharacterMap.put("o&circ;", "&#244;");
        badCharacterMap.put("o&tilde;", "&#245;");
        badCharacterMap.put("o&uml;", "&#246;");
        badCharacterMap.put("o&slash;", "&#248;");
        badCharacterMap.put("s&caron;", "&#353;");
        badCharacterMap.put("u&grave;", "&#249;");
        badCharacterMap.put("u&acute;", "&#250;");
        badCharacterMap.put("u&circ;", "&#251;");
        badCharacterMap.put("u&uml;", "&#252;");
        badCharacterMap.put("y&acute;", "&#253;");
        badCharacterMap.put("y&uml;", "&#255;");
    }
    
    public static String cleanBadCharacters(String input)
	{
    	//System.out.println("INPUT3"+input);
    	Set<String> keys = badCharacterMap.keySet();
        for(String key: keys){
            String value = badCharacterMap.get(key);
            input = input.replaceAll(key, value);
        }
        //System.out.println("INPUT4"+input);
        return input;
    	
	}
	
	/*
	public static String mapEntity(String xml)
	{
		if(xml == null)
		{
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
		return StringEscapeUtils.escapeHtml4(xml);
		//return sb.toString();
	}
	
	*/
    	public static String mapEntity(String xml)
    	{
    		if(xml == null)
    		{
    			return null;
    		}
    		
    		StringBuffer sb = new StringBuffer();
    		
    		int[] xmlChars = toCodePointArray(xml);//use this method to take care of double digit characters
    		int len = xmlChars.length;
    		   		
    		//int len = xml.length();
    		//char c;
    		int c;
    		

    		for (int i = 0; i < len; i++)
    		{
    			//c = xml.charAt(i);
    			c = xmlChars[i];
    			if((int) c >= 32 && (int) c <= 127)
    			{
    				sb.append((char)c);
    			}
    			else if(((int) c >= 128 || (int) c < 32))
    			{
    				/*
    				if((int)c==769)
    				{
    					//System.out.println(BdParser.accessNumberS+"\t"+xml);					//HT commented on 09/21/2020 It seems it has been added only for checking accented names, Uncomment for debugging, otherwise makes log file big
    				}
    				*/
    				
    				switch ((int) c)
    				{
    					case 30 :sb.append((char)c);break;
    					case 31 :sb.append((char)c);break;
    					case 96 :sb.append("&#96;");break;    					
    					case 128 :sb.append("&Ccedil;");break;
    					case 129 :sb.append("&uuml;");break;
    					case 130 :sb.append("&eacute;");break;
    					case 131 :sb.append("&acirc;");break;
    					case 132 :sb.append("&auml;");break;
    					case 133 :sb.append("&agrave;");break;
    					case 134 :sb.append("&aring;");break;
    					case 135 :sb.append("&ccedil;");break;
    					case 136 :sb.append("&ecirc;");break;
    					case 137 :sb.append("&euml;");break;
    					case 138 :sb.append("&egrave;");break;
    					case 139 :sb.append("&iuml;");break;
    					case 140 :sb.append("&icirc;");break;
    					case 141 :sb.append("&igrave;");break;
    					case 142 :sb.append("&Auml;");break;
    					case 143 :sb.append("&Aring;");break;
    					case 144 :sb.append("&Eacute;");break;
    					case 145 :sb.append("&aelig;");break;
    					case 146 :sb.append("&AElig;");break;
    					case 147 :sb.append("&ocirc;");break;
    					case 148 :sb.append("&ouml;");break;
    					case 149 :sb.append("&ograve;");break;
    					case 150 :sb.append("&ucirc;");break;
    					case 151 :sb.append("&ugrave;");break;
    					case 152 :sb.append("&yuml;");break;
    					case 153 :sb.append("&Ouml;");break;
    					case 154 :sb.append("&Uuml;");break;
    					case 156 :sb.append("&pound;");break;
    					case 157 :sb.append("&yen;");break;
    					case 160 :sb.append("&nbsp;");break;	//space
    					case 161 :sb.append("&iexcl;");break; 	//Inverted exclamation mark
    					case 162 :sb.append("&cent;");break; 	//Cent sign
    					case 163 :sb.append("&pound;");break; 	//Pound sign
    					case 164 :sb.append("&curren;");break; 	//Currency sign
    					case 165 :sb.append("&yen;");break; 	//Yen sign
    					case 166 :sb.append("&brvbar;");break; 	//broken bar,
    					case 167 :sb.append("&sect;");break; 	//scetion sign
    					case 168 :sb.append("&uml;");break; 	//diaeresis
    					case 169 :sb.append("&copy;");break; 	//copyright
    					case 170 :sb.append("&ordf;");break; 	//feminine ordinal indicator
    					case 171 :sb.append("&laquo;");break; 	//left-pointing double angle
    					case 172 :sb.append("&not;");break; 	//not sign
    					case 173 :sb.append("&shy;");break; 	//soft hyphen
    					case 174 :sb.append("&reg;");break; 		//registered sign
    					case 175 :sb.append("&macr;");break; 	//macron
    					case 176 :sb.append("&deg;");break; 	//degree sign
    					case 177 :sb.append("&plusmn;");break; 	//plus-minus sign
    					case 178 :sb.append("&sup2;");break; 	//superscript two
    					case 179 :sb.append("&sup3;");break; 	//superscript 3
    					case 180 :sb.append("&acute;");break; 	//acute accent
    					case 181 :sb.append("&micro;");break; 	//micro sign
    					case 182 :sb.append("&para;");break; 	//pilcrow sign
    					case 183 :sb.append("&middot;");break; 	//middle dot
    					case 184 :sb.append("&cedil;");break; 	//cedilla
    					case 185 :sb.append("&supl;");break; 	//superscript one
    					case 186 :sb.append("&ordm;");break; 	//masculine ordinal indicator
    					case 187 :sb.append("&raquo;");break; 	//right-pointing double angle
    					case 188 :sb.append("&frac14;");break; 	//vulgar fraction one quarter
    					case 189 :sb.append("&frac12;");break; 	//vulgar fraction one half
    					case 190 :sb.append("&frac12;");break; 	//vulgar fraction three quarters
    					case 191 :sb.append("&iquest;");break; 	//Invert question mark
    					case 192 :sb.append("&Agrave;");break; 	//Capital A grave
    					case 193 :sb.append("&Aacute;");break; 	//Capital A acute
    					case 194 :sb.append("&Acirc;");break; 	//Capital A circumflex
    					case 195 :sb.append("&Atilde;");break; 	//Capital A tilde
    					case 196 :sb.append("&Auml;");break; 	//Capital A diaeresis
    					case 197 :sb.append("&Aring;");break; 	//Capital A ring above
    					case 198 :sb.append("&AElig;");break; 	//Capital AE
    					case 199 :sb.append("&Ccedil;");break; 	//Capital C cedilla
    					case 200 :sb.append("&Egrave;");break; 	//Capital E grave;
    					case 201 :sb.append("&Eacute;");break; 	//Capital E acute
    					case 202 :sb.append("&Ecirc;");break; 	//Capital E circumflex
    					case 203 :sb.append("&Euml;");break; 	//Capital E diaeresis
    					case 204 :sb.append("&Igrave;");break; 	//Capital I acute
    					case 205 :sb.append("&Iacute;");break; 	//Capital E diaeresis
    					case 206 :sb.append("&Icirc;");break; 	//Capital I circumflex
    					case 207 :sb.append("&Iuml;");break; 	//Capital I diaeresis
    					case 208 :sb.append("&ETH;");break; 	//Capital Eth, Edh, crossed D
    					case 209 :sb.append("&Ntilde;");break; 	//Capital Ntilde
    					case 210 :sb.append("&Ograve;");break; 	//Capital O grave
    					case 211 :sb.append("&Oacute;");break; 	//Capital O acute
    					case 212 :sb.append("&Ocirc;");break; 	//Capital O circumflex
    					case 213 :sb.append("&Otilde;");break; 	//Capital O tilde
    					case 214 :sb.append("&Ouml;");break; 	//Capital O diaeresis
    					case 215 :sb.append("&times;");break; 	//Multiplication sign
    					case 216 :sb.append("&Oslash;");break; 	//latin O with stroke
    					case 217 :sb.append("&Ugrave;");break; 	//Capital u with grave accent    					
    					case 218 :sb.append("&Uacute;");break; 	//Capital u with acute accent
    					case 219 :sb.append("&Ucirc;");break; 	//Capital u with circumflex accent
    					case 220 :sb.append("&Uuml;");break; 	//Capital u with umlaut    					
    					case 221 :sb.append("&Yacute;");break; 	//Capital y with acute accent    					
    					case 222 :sb.append("&THORN;");break; 	//Capital thorn (Icelandic)    		   					
    					case 223 :sb.append("&szlig;");break; 	//Lowercase sharp s (German)
    					case 224 :sb.append("&agrave;");break; 	//Lowercase a with grave accent
    					case 225 :sb.append("&aacute;");break; 	//a acute
    					case 226 :sb.append("&acirc;");break; 	//a circumflex
    					case 227 :sb.append("&atilde;");break; 	//a tilde
    					case 228 :sb.append("&auml;");break; 	//a diaeresis
    					case 229 :sb.append("&aring;");break; 	//a a ring above
    					case 230 :sb.append("&aelig;");break; 	//a ae
    					case 231 :sb.append("&ccedil;");break; 	//a c cedilla
    					case 232 :sb.append("&egrave;");break; 	//a e grave
    					case 233 :sb.append("&eacute;");break; 	//a e acute
    					case 234 :sb.append("&ecirc;");break; 	//a circumflex
    					case 235 :sb.append("&euml;");break; 	//a e diaeresis
    					case 236 :sb.append("&igrave;");break; 	//a i grave
    					case 237 :sb.append("&iacute;");break; 	//a i acute
    					case 238 :sb.append("&icirc;");break; 	//a circumflex
    					case 239 :sb.append("&iuml;");break; 	//a diaeresis
    					case 240 :sb.append("&eth;");break; 	//a eth
    					case 241 :sb.append("&ntilde;");break; 	//a n tilde
    					case 242 :sb.append("&ograve;");break; 	//a o grave
    					case 243 :sb.append("&oacute;");break; 	//a o acute
    					case 244 :sb.append("&ocirc;");break; 	//o circumflex
    					case 245 :sb.append("&otilde;");break; 	//a tilde
    					case 246 :sb.append("&ouml;");break; 	//o diaeresis
    					case 247 :sb.append("&divide;");break; 	//division sign
    					case 248 :sb.append("&oslash;");break; 	//o stroke
    					case 249 :sb.append("&ugrave;");break; 	//u grave
    					case 250 :sb.append("&uacute;");break; 	//u acute
    					case 251 :sb.append("&ucirc;");break; 	//o circumflex
    					case 252 :sb.append("&uuml;");break; 	//u diaeresis
    					case 253 :sb.append("&yacute;");break; 	//y acute
    					case 254 :sb.append("&thorn;");break; 	//thorn
    					case 255 :sb.append("&uyuml;");break; 	//y diaeresis
    					
    					case 321 :sb.append("&Lstrok;");break; 	//latin L with stroke
    					case 322 :sb.append("&lstrok;");break; 	//latin l with stroke
    					case 338 :sb.append("&OElig;");break; 	//Capital ligaturen OE
    					case 339 :sb.append("&oelig;");break; 	//Ligature oe
    					case 352 :sb.append("&Scaron;");break; 	//Capital S caron
    					case 353 :sb.append("&scaron;");break; 	//s caron
    					case 376 :sb.append("&Yuml;");break; 	//Capital Y diaeresis
    					case 402 :sb.append("&fnof;");break; 	//F hook
    					case 710 :sb.append("&circ;");break; 	//modifier letter circumflex accent
    					case 732 :sb.append("&tilde;");break; 	//small tilde
    					case 768 :sb.append("&grave;");break; 	//combining grave accent
    					case 769 :sb.append("&acute;");break; 	//combining acute accent
    					case 770 :sb.append("&circ;");break; 	//modifier letter circumflex accent
    					case 771 :sb.append("&tilde;");break; 	//small tilde
    					case 772 :sb.append("&macr;");break; 	//combining macron
    					case 775 :sb.append("&dot;");break; 	//combining dot above
    					case 776 :sb.append("&die;");break; 	//combining diaeresis
    					case 778 :sb.append("&ring;");break; 	//combining caron
    					case 780 :sb.append("&caron;");break; 	//combining caron
    					case 807 :sb.append("&cedil;");break; 	//combining cedilla
    					case 913 :sb.append("&Alpha;");break; 	//Alpha
    					case 914 :sb.append("&Beta;");break; 	//Beta
    					case 915 :sb.append("&Gamma;");break; 	//Capital gamma
    					case 916 :sb.append("&Delta;");break; 	//Capital delta
    					case 917 :sb.append("&Epsilon;");break; //Capital epsilon
    					case 918 :sb.append("&Zeta;");break; 	//Capital zeta
    					case 919 :sb.append("&Eta;");break; 	//Capital eta
    					case 920 :sb.append("&Theta;");break; 	//Capital theta
    					case 921 :sb.append("&Iota;");break; 	//Capital iota
    					case 922 :sb.append("&Kappa;");break; 	//Capital Kappa
    					case 923 :sb.append("&Lambda;");break; 	//Capital lambda
    					case 924 :sb.append("&Mu;");break; 		//Capital mu
    					case 925 :sb.append("&Nu;");break; 		//Capital nu
    					case 926 :sb.append("&Xi;");break; 		//Capital xi
    					case 927 :sb.append("&Omicron;");break; //Capital omicron
    					case 928 :sb.append("&Pi;");break; 		//Capital pi
    					case 929 :sb.append("&Rho;");break; 	//Capital rho
    					case 931 :sb.append("&Sigma;");break; 	//Capital sigma
    					case 932 :sb.append("&Tau;");break; 	//Capital tau
    					case 933 :sb.append("&Upsilon;");break; //Capital Upsilon
    					case 934 :sb.append("&Phi;");break; 	//Capital Phi
    					case 935 :sb.append("&Chi;");break; 	//Capital chi
    					case 936 :sb.append("&Psi;");break; 	//Capital psi
    					case 937 :sb.append("&Omega;");break; 	//Capital omega
    					case 945 :sb.append("&alpha;");break; 	//Alpha
    					case 946 :sb.append("&beta;");break; 	//Beta
    					case 947 :sb.append("&gamma;");break; 	//Gamma
    					case 948 :sb.append("&delta;");break; 	//Delta
    					case 949 :sb.append("&Epsilon;");break; //Epsilon
    					case 950 :sb.append("&zeta;");break; 	//Zeta
    					case 951 :sb.append("&eta;");break; 	//Eta
    					case 952 :sb.append("&theta;");break; 	//Theta
    					case 953 :sb.append("&iota;");break; 	//Iota
    					case 954 :sb.append("&kappa;");break; 	//Kappa
    					case 955 :sb.append("&lambda;");break; 	//Lambda
    					case 956 :sb.append("&mu;");break; 		//Mu
    					case 957 :sb.append("&nu;");break; 		//Nu
    					case 958 :sb.append("&xi;");break; 		//Xi
    					case 959 :sb.append("&omicron;");break; //omicron
    					case 960 :sb.append("&pi;");break; 		//pi
    					case 961 :sb.append("&rho;");break; 	//rho
    					case 962 :sb.append("&sigmaf;");break; 	//final sigma
    					case 963 :sb.append("&sigma;");break; 	//sigma
    					case 964 :sb.append("&tau;");break; 	//Tau
    					case 965 :sb.append("&upsilon;");break; //Upsilon
    					case 966 :sb.append("&phi;");break; 	//Phi
    					case 967 :sb.append("&chi;");break; 	//chi
    					case 968 :sb.append("&psi;");break; 	//psi
    					case 969 :sb.append("&omega;");break; 	//Omega
    					case 976 :sb.append("&#x03D0");break;//GREEK BETA SYMBOL 
    					case 977 :sb.append("&thetasym;");break;//theta symbol
    					case 978 :sb.append("&upsih;");break; 	//greek upsilon with hook symbol
    					case 979 :sb.append("&#x03D3");break; 	//GREEK UPSILON WITH ACUTE AND HOOK SYMBOL
    					case 980 :sb.append("&#x03D4");break; 	//GREEK UPSILON WITH DIAERESIS AND HOOK SYMBOL
    					case 981 :sb.append("&straightphi;");break; 	//GREEK PHI SYMBOL (cursive)
    					case 982 :sb.append("&piv;");break; 	//greek pi symbol
    					case 983 :sb.append("&#x03D7");break; //GREEK KAI SYMBOL
    					case 986 :sb.append("&#x03DA");break; //GREEK LETTER STIGMA Ïš
    					case 987 :sb.append("&#x03DB");break; //GREEK SMALL LETTER STIGMA Ï›
    					case 988 :sb.append("&Gammad;");break; //GREEK LETTER DIGAMMA (F) Ïœ
    					case 989 :sb.append("&gammad;");break; //GREEK SMALL LETTER DIGAMMA (f) Ï�
    					
    					case 990 :sb.append("&#x03DE");break; //GGREEK LETTER KOPPA Ïž
    					case 991 :sb.append("&#x03DF");break; //GREEK SMALL LETTER KOPPA ÏŸ
    					case 992 :sb.append("&#x03E0");break; //GREEK LETTER SAMPI Ï 
    					case 993 :sb.append("&#x03E1");break; //GREEK SMALL LETTER SAMPI Ï¡
    					
    					//New Cyrillic (Russian) Alphabetic Entities in HTML 5
    					
    					case 1025 :sb.append("&IOcy;");break; 	//Cyrillic capital IO, like capital E umlaut
    					case 1026 :sb.append("&DJcy;");break; 	//Cyrillic capital letter DJ
    					case 1027 :sb.append("&GJcy;");break; 	//Cyrillic capital letter GJ
    					case 1028 :sb.append("&Jukcy;");break; 	//Cyrillic capital letter Juk
    					case 1029 :sb.append("&DScy;");break; 	//Cyrillic capital letter DS
    					case 1030 :sb.append("&Iukcy;");break; 	//Cyrillic Byelorussion I
    					case 1031 :sb.append("&YIcy;");break; 	//Cyrillic capital letter YI
    					case 1032 :sb.append("&Jsercy;");break; //Cyrillic capital letter Jser
    					case 1033 :sb.append("&LJcy;");break; 	//Cyrillic capital letter LJ
    					case 1034 :sb.append("&NJcy;");break; 	//Cyrillic capital letter NJ
    					case 1035 :sb.append("&TSHcy;");break; 	//Cyrillic capital letter TSH
    					case 1036 :sb.append("&KJcy;");break; 	//Cyrillic capital letter KJ
    					case 1038 :sb.append("&Ubrcy;");break; 	//Cyrillic U capital letter breve
    					case 1039 :sb.append("&DZcy;");break; 	//Cyrillic capital letter DZ
    					case 1040 :sb.append("&Acy;");break; 	//Cyrillic capital letter A
    					case 1041 :sb.append("&Bcy;");break; 	//Cyrillic capital letter BE
    					case 1042 :sb.append("&Vcy;");break; 	//Cyrillic capital letter VE
    					case 1043 :sb.append("&Gcy;");break; 	//Cyrillic capital letter GHE
    					case 1044 :sb.append("&Dcy;");break; 	//Cyrillic capital letter DE
    					case 1045 :sb.append("&IEcy;");break; 	//Cyrillic capital letter EE
    					case 1046 :sb.append("&ZHcy;");break; 	//Cyrillic capital letter ZHE
    					case 1047 :sb.append("&Zcy;");break; 	//Cyrillic capital letter ZE
    					case 1048 :sb.append("&Icy;");break; 	//Cyrillic capital letter I
    					case 1049 :sb.append("&Jcy;");break; 	//Cyrillic capital short I
    					case 1050 :sb.append("&Kcy;");break; 	//Cyrillic capital letter KA
    					case 1051 :sb.append("&Lcy;");break; 	//Cyrillic capital letter EL
    					case 1052 :sb.append("&Mcy;");break; 	//Cyrillic capital letter M
    					case 1053 :sb.append("&Ncy;");break; 	//Cyrillic capital letter EN
    					case 1054 :sb.append("&Ocy;");break; 	//Cyrillic capital letter O
    					case 1055 :sb.append("&Pcy;");break; 	//Cyrillic capital letter PE
    					case 1056 :sb.append("&Rcy;");break; 	//Cyrillic capital letter ER
    					case 1057 :sb.append("&Scy;");break; 	//Cyrillic capital letter ES
    					case 1058 :sb.append("&Tcy;");break; 	//Cyrillic capital letter TE
    					case 1059 :sb.append("&Ucy;");break; 	//Cyrillic capital letter U
    					case 1060 :sb.append("&Fcy;");break; 	//Cyrillic capital letter EF
    					case 1061 :sb.append("&KHcy;");break; 	//Cyrillic capital letter HA
    					case 1062 :sb.append("&TScy;");break; 	//Cyrillic capital letter TSE
    					case 1063 :sb.append("&CHcy;");break; 	//Cyrillic capital letter CHE
    					case 1064 :sb.append("&SHcy;");break; 	//Cyrillic capital letter SHA
    					case 1065 :sb.append("&SHCHcy;");break; //Cyrillic capital letter SHCHA
    					case 1066 :sb.append("&HARDcy;");break; //Cyrillic capital hard sign
    					case 1067 :sb.append("&Ycy;");break; 	//Cyrillic capital letter YERU
    					case 1068 :sb.append("&SOFTcy;");break; //Cyrillic capital soft sign
    					case 1069 :sb.append("&Ecy;");break; 	//Cyrillic capital letter E
    					case 1070 :sb.append("&YUcy;");break; 	//Cyrillic capital letter YU
    					case 1071 :sb.append("&YAcy;");break; 	//Cyrillic capital letter YA
    					case 1072 :sb.append("&acy;");break; 	//Cyrillic small letter a
    					case 1073 :sb.append("&bcy;");break; 	//Cyrillic small letter be
    					case 1074 :sb.append("&vcy;");break; 	//Cyrillic small letter ve
    					case 1075 :sb.append("&gcy;");break; 	//Cyrillic small letter ghe
    					case 1076 :sb.append("&dcy;");break; 	//Cyrillic small letter de
    					case 1077 :sb.append("&iecy;");break; 	//Cyrillic small letter ie
    					case 1078 :sb.append("&zhcy;");break; 	//Cyrillic small letter zhe
    					case 1079 :sb.append("&zcy;");break; 	//Cyrillic small letter ze
    					case 1080 :sb.append("&icy;");break; 	//Cyrillic small letter i
    					case 1081 :sb.append("&jcy;");break; 	//Cyrillic small short i
    					case 1082 :sb.append("&kcy;");break; 	//Cyrillic small letter ka
    					case 1083 :sb.append("&lcy;");break; 	//Cyrillic small letter el
    					case 1084 :sb.append("&mcy;");break; 	//Cyrillic small letter em
    					case 1085 :sb.append("&ncy;");break; 	//Cyrillic small letter en
    					case 1086 :sb.append("&ocy;");break; 	//Cyrillic small letter o
    					case 1087 :sb.append("&pcy;");break; 	//Cyrillic small letter pe
    					case 1088 :sb.append("&rcy;");break; 	//Cyrillic small letter er
    					case 1089 :sb.append("&scy;");break; 	//Cyrillic small letter es
    					case 1090 :sb.append("&tcy;");break; 	//Cyrillic small letter te
    					case 1091 :sb.append("&ucy;");break; 	//Cyrillic small letter u
    					case 1092 :sb.append("&fcy;");break; 	//Cyrillic small letter ef
    					case 1093 :sb.append("&khcy;");break; 	//Cyrillic small letter ha
    					case 1094 :sb.append("&tscy;");break; 	//Cyrillic small letter tse
    					case 1095 :sb.append("&chcy;");break; 	//Cyrillic small letter che
    					case 1096 :sb.append("&shcy;");break; 	//Cyrillic small letter sha
    					case 1097 :sb.append("&shchcy;");break; //Cyrillic small letter shcha
    					case 1098 :sb.append("&hardcy;");break; //Cyrillic small hard sign
    					case 1099 :sb.append("&ycy;");break; 	//Cyrillic small letter yeru
    					case 1100 :sb.append("&softcy;");break; //Cyrillic small soft sign
    					case 1101 :sb.append("&ecy;");break; 	//Cyrillic small letter e
    					case 1102 :sb.append("&yucy;");break; 	//Cyrillic small letter yu
    					case 1103 :sb.append("&yacy;");break; 	//Cyrillic small letter ya
    					case 1105 :sb.append("&iocy;");break; 	//Cyrillic small letter io, like small e umlaut
    					case 1106 :sb.append("&djcy;");break; 	//Cyrillic small letter dj
    					case 1107 :sb.append("&gjcy;");break; 	//Cyrillic small letter gj
    					case 1108 :sb.append("&jukcy;");break; 	//Cyrillic small letter juk
    					case 1109 :sb.append("&dscy;");break; 	//Cyrillic small letter ds
    					case 1110 :sb.append("&iukcy;");break; 	//Cyrillic small letter Byelorussion i
    					case 1111 :sb.append("&yicy;");break; 	//Cyrillic small letter yi
    					case 1112 :sb.append("&jsercy;");break; //Cyrillic small letter jser
    					case 1113 :sb.append("&ljcy;");break; 	//Cyrillic small letter lj
    					case 1114 :sb.append("&njcy;");break; 	//Cyrillic small letter nj
    					case 1115 :sb.append("&tshcy;");break; 	//Cyrillic small letter tsh
    					case 1116 :sb.append("&kjcy;");break; 	//Cyrillic small letter kj
    					case 1118 :sb.append("&ubrcy;");break; 	//Cyrillic small letter u breve
    					case 1119 :sb.append("&dzcy;");break; 	//Cyrillic small letter dz
    					
    					
    					case 1488 :sb.append("&aleph;");break; 	//hebrew aleph
    					case 2155 :sb.append("&#2155;");break; 	//Fraction one fifth
    					case 2156 :sb.append("&#2156;");break; 	//Fraction two fifths
    					case 2157 :sb.append("&#2157;");break; 	//Fraction three fifths
    					case 2158 :sb.append("&2158;");break; 	//Fraction four fifths
    					case 2159 :sb.append("&2159;");break; 	//Fraction one sixth
    					//case 1488 :sb.append("&aleph;");break; 	//hebrew aleph
    					
    					case 8194 :sb.append("&ensp;");break; 	//en space
    					case 8195 :sb.append("&emsp;");break; 	//em space
    					case 8201 :sb.append("&thinsp;");break; //thin space
    					case 8204 :sb.append("&zwnj;");break; 	//zero width non-joiner
    					case 8205 :sb.append("&zwj;");break; 	//zero width joiner
    					case 8206 :sb.append("&lrm;");break; 	//left to right mark
    					case 8207 :sb.append("&rlm;");break; 	//right to left mark
    					case 8211 :sb.append("&ndash;");break; 	//En dash
    					case 8212 :sb.append("&mdash;");break; 	//Em dash
    					case 8216 :sb.append("&lsquo;");break; 	//left single quotation
    					case 8217 :sb.append("&rsquo;");break; 	//right single quotation
    					case 8218 :sb.append("&sbquo;");break; 	//single low 9 quation mark
    					case 8220 :sb.append("\"");break; 	//left double quotation
    					case 8221 :sb.append("\"");break; 	//right double quotation changed on 7/23/2019 by hmo based on EVOPS 822
    					//case 8220 :sb.append("&ldquo;");break; 	//left double quotation
    					//case 8221 :sb.append("&rdquo;");break; 	//right double quotation
    					case 8222 :sb.append("&bdquo;");break; 	//Double low-9 quotation mark
    					case 8224 :sb.append("&dagger;");break; //Dagger
    					case 8225 :sb.append("&Dagger;");break; //Double Dagger
    					case 8226 :sb.append("&bull;");break; 	//Bullet, black small circle
    					case 8230 :sb.append("&hellip;");break; 	//Horizontal ellipsis
    					case 8240 :sb.append("&permil;");break; //per mille sign
    					case 8242 :sb.append("&prime;");break; 	//prime, minutes,feet
    					case 8243 :sb.append("&Prime;");break; 	//double prime,
    					case 8244 :sb.append("&tprime;");break; //triple prime,
    					case 8249 :sb.append("&Isaquo;");break; //single left-pointing angle quotation mark
    					case 8250 :sb.append("&rsaquo;");break; //single right-pointing angle quotation mark
    					case 8254 :sb.append("&oline;");break;  //Overline, spacing overscore
    					case 8259 :sb.append("&Hscr;");break;   //script capital H
    					case 8260 :sb.append("&frasl;");break; 	//Fraction slash
    					case 8261 :sb.append("&Hdbl;");break; 	//double-struck capital H
    					case 8263 :sb.append("&plankv;");break; //planck constant over two pi
    					case 8364 :sb.append("&euro;");break; 	//Euro sign
    					
    					case 8465 :sb.append("&image;");break;  //blackletter capital I
    					case 8466 :sb.append("&Lscr;");break;   //script capital L
    					case 8467 :sb.append("&ell;");break; 	//script small l
    					case 8469 :sb.append("&Ndbl;");break;   //double-struck capital N
    					case 8472 :sb.append("&weierp;");break; //script capital P, power set, Weierstrass p
    					case 8473 :sb.append("&Pdbl;");break;   //double-struck capital P
    					case 8474 :sb.append("&Qdbl;");break;   //double-struck capital Q
    					case 8475 :sb.append("&Rscr;");break;   //script capital R
    					case 8476 :sb.append("&real;");break; 	//Blackletter capital R, real part symbol,
    					case 8477 :sb.append("&Rdbl;");break; 	//double-struck capital R
    					case 8482 :sb.append("&trade;");break; 	//trademark sign
    					case 8484 :sb.append("&Zdbl;");break;   //double-struck capital Z
    					case 8487 :sb.append("&mho;");break;    //inverted ohm sign
    					case 8492 :sb.append("&Bscr;");break; 	//Bscript capital B
    					case 8496 :sb.append("&Escr;");break; 	//script capital E
    					case 8497 :sb.append("&Fscr;");break; 	//script capital F
    					case 8499 :sb.append("&Mscr;");break; 	//script capital M
    					
    					case 8501 :sb.append("&alefsym;");break;//Alef symbol, first transfinite cardinal
    					case 8538 :sb.append("&#8538;");break;//Fraction five sixths
    					case 8539 :sb.append("&#8539;");break;//Fraction one eighth
    					case 8540 :sb.append("&#8540;");break;//Fraction three eighths
    					case 8541 :sb.append("&#8541;");break;//Fraction five eighths
    					case 8542 :sb.append("&#8542;");break;//Fraction seven eighths
    					
    					case 8592 :sb.append("&larr;");break; 	//Leftward arrow
    					case 8593 :sb.append("&uarr;");break;   //Upward arrow
    					case 8594 :sb.append("&rarr;");break; 	//Righteard arrow
    					case 8595 :sb.append("&darr;");break; 	//Downward arrow
    					case 8596 :sb.append("&harr;");break;   //left right arrow
    					case 8597 :sb.append("&varr;");break; 	//up down arrow
    					case 8598 :sb.append("&nwarr;");break;  //north west arrow
    					case 8599 :sb.append("&nearr;");break; 	//north east arrow
    					
    					case 8600 :sb.append("&searr;");break; 	//south east arrow
    					case 8601 :sb.append("&swarr;");break; 	//south west arrow
    					case 8602 :sb.append("&#8602;");break; 	//Leftwards arrow with stroke
    					case 8603 :sb.append("&#8603;");break; 	//Rightwards arrow with stroke
    					case 8604 :sb.append("&#8604;");break; 	//Leftwards wave arrow
    					case 8605 :sb.append("&#8605;");break; 	//Rightwards wave arrow		
    					case 8606 :sb.append("&Larr;");break; 	//leftwards two headed arrow
    					case 8607 :sb.append("&#8607;");break; 	//Upwards two headed arrow
    					case 8608 :sb.append("&Rarr;");break; 	//rightwards two headed arrow
    					case 8609 :sb.append("&#8609;");break; 	//Downwards two headed arrow
    					
    					case 8610 :sb.append("&#8610;");break; 	//Leftwards arrow with tail
    					case 8611 :sb.append("&#8611;");break; 	//Rightwards arrow with tail
    					case 8612 :sb.append("&#8612;");break; 	//Leftwards arrow from bar
    					case 8613 :sb.append("&#8613;");break; 	//Upwards arrow from bar
    					case 8614 :sb.append("&#8614;");break; 	//Rightwards arrow from bar
    					case 8615 :sb.append("&#8615;");break; 	//Downwards arrow from bar
    					case 8616 :sb.append("&#8616;");break; 	//Up down arrow with base
    					case 8617 :sb.append("&#8617;");break; 	//Leftwards arrow with hook
    					case 8618 :sb.append("&#8618;");break; 	//Rightwards arrow with hook
    					case 8619 :sb.append("&#8619;");break; 	//Leftwards arrow with loop
    					
    					case 8620 :sb.append("&#8620;");break; 	//Rightwards arrow with loop
    					case 8621 :sb.append("&#8621;");break; 	//Left right wave arrow
    					case 8622 :sb.append("&#8622;");break; 	//Left right arrow with stroke
    					case 8623 :sb.append("&#8623;");break; 	//Downwards zigzag arrow
    					case 8624 :sb.append("&#8624;");break; 	//Upwards arrow with tip leftwards
    					case 8625 :sb.append("&#8625;");break; 	//Upwards arrow with tip rightwards
    					case 8626 :sb.append("&#8626;");break; 	//Downwards arrow with tip leftwards
    					case 8627 :sb.append("&#8627;");break; 	//Downwards arrow with tip rightwards
    					case 8628 :sb.append("&#8628;");break; 	//Rightwards arrow with corner downwards  					    					
    					case 8629 :sb.append("&crarr;");break;  //downward arrow with corner leftward, carriage return
    					
    					case 8630 :sb.append("&#8630");break; 	//Anticlockwise top semicircle arrow
    					case 8631 :sb.append("&#8631");break; 	//Clockwise top semicircle arrow
    					case 8632 :sb.append("&#8632");break; 	//North west arrow to long bar
    					case 8633 :sb.append("&#8633");break; 	//Leftwards arrow to bar over rightwards arrow to bar
    					case 8634 :sb.append("&#8634");break; 	//Anticlockwise open circle arrow
    					case 8635 :sb.append("&#8635");break; 	//Clockwise open circle arrow
    					case 8636 :sb.append("&lharu;");break; 	//leftwards harpoon with barb upwards
    					case 8637 :sb.append("&lhard;");break; 	//leftwards harpoon with barb downwards
    					case 8638 :sb.append("&#8638");break; 	//Upwards harpoon with barb rightwards
    					case 8639 :sb.append("&#8639");break; 	//Upwards harpoon with barb leftwards
    					
    					case 8640 :sb.append("&rharu;");break; 	//rightwards harpoon with barb upwars
    					case 8641 :sb.append("&rhard;");break;  //rightwards harpoon with barb downwards
    					case 8642 :sb.append("&#8642;");break;  //Downwards harpoon with barb rightwards
    					case 8643 :sb.append("&#8643;");break;  //Downwards harpoon with barb leftwards
    					case 8644 :sb.append("&rlarr2;");break; //right over left arrow N.B. rlarr in ES grid
    					case 8645 :sb.append("&#8645;");break;  //Upwards arrow leftwards of downwards arrow
    					case 8646 :sb.append("&lrarr2;");break; //left over right arrow N.B. lrarr in ES grid
    					case 8647 :sb.append("&#8647;");break;  //Leftwards paired arrows
    					case 8648 :sb.append("&#8648;");break;  //Upwards paired arrows
    					case 8649 :sb.append("&#8649;");break;  //Rightwards paired arrows
    					
    					case 8650 :sb.append("&#8650;");break;  //Downwards paired arrows
    					case 8651 :sb.append("&lrhar2;");break; //left over right harpoon N.B. lrhar in ES grid
    					case 8652 :sb.append("&rlhar2;");break;  //right over left harpoon N.B. rlhar in ES grid
    					case 8653 :sb.append("&#8653;");break;  //Leftwards double arrow with stroke
    					case 8654 :sb.append("&#8654;");break;  //Left right double arrow with stroke
    					case 8655 :sb.append("&#8655;");break;  //Rightwards double arrow with stroke    					
    					case 8656 :sb.append("&lArr;");break; 	//leftward double arrow
    					case 8657 :sb.append("&uArr;");break; 	//upward double arrow
    					case 8658 :sb.append("&rArr;");break;	//rightward double arrow
    					case 8659 :sb.append("&dArr;");break;   //Downward double arrow
    					
    					case 8660 :sb.append("&hArr;");break;  	//left-right double
    					case 8661 :sb.append("&#8661;");break;  //Up down double arrow
    					case 8662 :sb.append("&#8662;");break;  //North west double arrow
    					case 8663 :sb.append("&#8663;");break;  //North east double arrow
    					case 8664 :sb.append("&#8664;");break;  //South east double arrow
    					case 8665 :sb.append("&#8665;");break;  //South west double arrow
    					case 8666 :sb.append("&#8666;");break;  //Leftwards triple arrow
    					case 8667 :sb.append("&#8667;");break;  //Rightwards triple arrow
    					case 8668 :sb.append("&#8668;");break;  //Leftwards squiggle arrow
    					case 8669 :sb.append("&#8669;");break;  //Rightwards squiggle arrow
    					
    					case 8670 :sb.append("&#8670;");break;  //Upwards arrow with double stroke
    					case 8671 :sb.append("&#8671;");break;  //Downwards arrow with double stroke
    					case 8672 :sb.append("&#8672;");break;  //Leftwards dashed arrow
    					case 8673 :sb.append("&#8673;");break;  //Upwards dashed arrow
    					case 8674 :sb.append("&#8674;");break;  //Rightwards dashed arrow
    					case 8675 :sb.append("&#8675;");break;  //Downwards dashed arrow
    					case 8676 :sb.append("&#8676;");break;  //Leftwards arrow to bar
    					case 8677 :sb.append("&#8677;");break;  //Rightwards arrow to bar
    					case 8678 :sb.append("&#8678;");break;  //Leftwards white arrow
    					case 8679 :sb.append("&#8679;");break;  //Upwards white arrow
    					case 8680 :sb.append("&#8680;");break;  //Rightwards white arrow
    					case 8681 :sb.append("&#8681;");break;  //Downwards white arrow
    					
    					case 8704 :sb.append("&forall;");break; //for all
    					
    					case 8706 :sb.append("&part;");break; 	//Partial differential
    					case 8707 :sb.append("&exist;");break;  //there exist
    					case 8708 :sb.append("&nexist;");break;  //there does not exist
    					case 8709 :sb.append("&empty;");break; 	//empty set
    					case 8711 :sb.append("&nabla;");break; 	//Nabla, backward difference
    					case 8712 :sb.append("&isin;");break; 	//Element of
    					case 8713 :sb.append("&notin;");break;	//not an element of
    					case 8715 :sb.append("&ni;");break;   	//Contains as member
    					case 8716 :sb.append("&notni;");break;  //does not contain as member
    					case 8719 :sb.append("&prod;");break;  	//n_ary product, product sign
    					case 8720 :sb.append("&coprod;");break; //n-ary coproduct
    					case 8721 :sb.append("&sum;");break; 	//n-ary sumation
    					case 8722 :sb.append("&minus;");break; 	//Minus sign
    					case 8723 :sb.append("&mnplus;");break; //minus-or-plus sign
    					case 8724 :sb.append("&#8724;");break; //Dot plus
    					case 8725 :sb.append("&#8725;");break; //Division slash
    					case 8726 :sb.append("&#8726;");break; //Set minus				
    					case 8727 :sb.append("&lowast;");break; //Asterisk operation
    					case 8730 :sb.append("&radic;");break; 	//Square root, radical sign
    					case 8733 :sb.append("&prop;");break; 	//proportional to
    					case 8734 :sb.append("&infin;");break; 	//infinity
    					case 8736 :sb.append("&ang;");break;	//angle
    					case 8741 :sb.append("&par;");break; 	//parallel to
    					case 8742 :sb.append("&npar;");break;	//not parallel to
    					case 8743 :sb.append("&and;");break; 	//logical and wedge
    					case 8744 :sb.append("&or;");break;		//logical or, vee
    					case 8745 :sb.append("&cap;");break;	//intersection,cap
    					case 8746 :sb.append("&cup;");break; 	//union cup
    					case 8747 :sb.append("&int;");break;	//integral
    					case 8750 :sb.append("&conint;");break;	//contour integral
    					case 8756 :sb.append("&there4;");break; //therefore
    					case 8764 :sb.append("&sim;");break; 	//tilde operator, varies with, similar to
    					case 8769 :sb.append("&nsim;");break;	//not tilde
    					case 8771 :sb.append("&sime;");break;   //asymptotically equal to
    					case 8772 :sb.append("&nsime;");break;  //not asymptotically equal to
    					case 8773 :sb.append("&cong;");break;	//approximaterly equal to
    					case 8775 :sb.append("&ncong;");break;	//neither aproximately nor actually equal to
    					case 8776 :sb.append("&asymp;");break;  //almost equal to, asymptotic to
    					case 8777 :sb.append("&nap;");break; 	//not almost equal to
    					case 8778 :sb.append("&ape;");break;	//almost equal or equal to
    					case 8786 :sb.append("&efDot;");break;  //approximately equal to or the image of
    					case 8787 :sb.append("&erDot;");break;  //image of or approximately equal to
    					case 8793 :sb.append("&wedgeq;");break;	//estimates
    					case 8800 :sb.append("&ne;");break;  	//not equal
    					case 8801 :sb.append("&equiv;");break; 	//identical to
    					case 8802 :sb.append("&nequiv;");break; //not identical to
    					case 8804 :sb.append("&le;");break; 		//less - than or equal to
    					case 8805 :sb.append("&ge;");break; 	//greater than or equal to
    					case 8806 :sb.append("&lE;");break; 	//less-than over equal to
    					case 8807 :sb.append("&gE;");break; 	//greater-than over equal to
    					case 8810 :sb.append("&Lt;");break; 	//much less-than
    					case 8811 :sb.append("&Gt;");break; 	//much greater-than
    					case 8814 :sb.append("&nlt;");break; 	//not less-than
    					case 8815 :sb.append("&ngt;");break; 	//not greater-than
    					case 8816 :sb.append("&nle;");break; 	//neither less-than nor equal to
    					case 8817 :sb.append("&nge;");break; 	//neither greater-than nor equal to
    					case 8818 :sb.append("&lsim;");break; 	//less-than or equivalent to
    					case 8819 :sb.append("&gsim;");break; 	//greater-than or equivalent to
    					case 8826 :sb.append("&pr;");break; 	//precedes
    					case 8827 :sb.append("&sc;");break; 	//succeeds
    					case 8834 :sb.append("&sub;");break; 	//subset of
    					case 8835 :sb.append("&sup;");break; 	//superset of
    					case 8836 :sb.append("&nsub;");break; 	//not a subset
    					case 8837 :sb.append("&nsup;");break; 	//not a superset of
    					case 8838 :sb.append("&sube;");break;	//subset of or equal to
    					case 8839 :sb.append("&supe;");break; 	//superset of equal to
    					case 8840 :sb.append("&nsube;");break;	//neither a subset of nor equal to
    					case 8841 :sb.append("&nsupe;");break; 	//neither a superset of nor equal to
    					case 8853 :sb.append("&oplus;");break;	//circled plus, direct sum
    					case 8854 :sb.append("&ominus;");break;	//circled minus
    					case 8855 :sb.append("&otimes;");break;	//circled times, vector product
    					case 8857 :sb.append("&odot;");break;	//circled dot operator
    					case 8866 :sb.append("&vdash;");break;	//right tack
    					case 8867 :sb.append("&dashv;");break;	//left tack
    					case 8868 :sb.append("&top;");break;	//down tack
    					case 8869 :sb.append("&perp;");break; 	//up tack, orthogonal to, perpendicular
    					case 8870 :sb.append("&#8870;");break; 	//Logical OR, or vee
    					case 8884 :sb.append("&ltrie;");break;	//normal subgroup of or equal to
    					case 8885 :sb.append("&rtrie;");break; 	//cantains as normal subgroup or equal to
    					case 8901 :sb.append("&sdot;");break;	//dot operator
    					case 8905 :sb.append("&ltimes;");break;	//left normal factor semidirect product
    					case 8906 :sb.append("&rtimes;");break;	//right normal factor semidirect product
    					case 8920 :sb.append("&Ll;");break; 	//very much less-than
    					case 8921 :sb.append("&Gg;");break;		//very much greater-than
    					case 8942 :sb.append("&vellip;");break; //vertical ellipsis
    					case 8943 :sb.append("&mellip;");break;	//midline horizontal ellipsis
    					case 8968 :sb.append("&lceil;");break; 	//left ceiling, APL downstile
    					case 8969 :sb.append("&rceil;");break; 	//Right ceiling
    					case 8970 :sb.append("&lfloor;");break;	//left floor, APL downstile
    					case 8971 :sb.append("&rfloor;");break; //right floor
    					case 8974 :sb.append("&#8974;");break;  //Lozenge
    					case 9001 :sb.append("&lang;");break;  	//left pointing angle bracket, bra
    					case 9002 :sb.append("&rang;");break; 	//right pointing angle bracket, ket
    					case 9632 :sb.append("&squf;");break; 	//black square
    					case 9633 :sb.append("&squ;");break;	//white square
    					case 9642 :sb.append("&#9642;");break;	//white square
    					case 9650 :sb.append("&utrif;");break;  //black up-pointing triangle
    					case 9651 :sb.append("&utri;");break;  	//white up-pointing triangle
    					case 9654 :sb.append("&rtrif;");break; 	//black right-pointing triangle
    					case 9655 :sb.append("&rtri;");break; 	//white right-pointing triangle
    					case 9660 :sb.append("&dtrif;");break;	//black down-pointing triangle
    					case 9661 :sb.append("&dtri;");break;   //white down-pointing triangle
    					case 9664 :sb.append("&ltrif;");break;  //black left-pointing triangle
    					case 9665 :sb.append("&ltri;");break; 	//white left-pointing triangle
    					case 9670 :sb.append("&diams;");break;  //black diamond
    					case 9671 :sb.append("&diam;");break; 	//white diamond
    					case 9673 :sb.append("&#9673;");break; 	//Fisheye
    					case 9674 :sb.append("&loz;");break; 	//lozenge
    					case 9675 :sb.append("&cir;");break; 	//white circle
    					case 9678 :sb.append("&#9678;");break; //female sign
    					case 9792 :sb.append("&female;");break; //female sign
    					case 9794 :sb.append("&male;");break; 	//male sign
    					case 9824 :sb.append("&spades;");break; //black spade suit
    					case 9827 :sb.append("&clubs;");break; 	//black club suit, shamrock
    					case 9829 :sb.append("&hearts;");break; //black heart suit, valentine
    					case 9830 :sb.append("&diams;");break;	//Black diamond suit
    					case 10077 :sb.append("&#10077;");break; //much less than (double) (EW=Bkl)
    					case 10078 :sb.append("&#10078;");break; 	//much greater than (double) (EW=Bml)
    					
    					case 10913 :sb.append("&LessLess;");break; //much less than (double) (EW=Bkl)
    					case 10914 :sb.append("&GreaterGreater;");break; 	//much greater than (double) (EW=Bml)
    					case 12296 :sb.append("&lang;");break; //left angle bracket
    					case 12297 :sb.append("&rang;");break;	//right angle bracket
    					
    					//HH 07/27/2016 for Cafe AU/AF ES profile
    					case 34: sb.append("&#x00022;");break;   // unicode double quotes
    					case 44: sb.append("&#x0002C;");break;	  // unicode comma
    					case 58: sb.append("&#x0003A;");break;	  // unicode colon
    					case 91: sb.append("&#x0005B;");break;	  // unicode left square bracket
    					case 93: sb.append("&#x0005D;");break;	  // unicode right square bracket
    					case 123: sb.append("&#x0007B;");break;	  // unicode left curly bracket
    					case 125: sb.append("&#x0007D;");break;	  // unicode right curly bracket
    					case 92: sb.append("&#x0005C;");break;	  //reverse solidus or backslash
    					case 39: sb.append("&#x00027;");break;	  //apostroph
    					//HH END
    					
    					
    					//updated on 5/26/2016
    					//something is wrong, remove this line by hmo at 10/26/2016
    					default:sb.append("&#"+(int)c+";");
    					//System.out.println("UNKNOW-CHARACTERS="+"(&#"+(int)c+";)");
    					break;
    				}
    			}
    		}
    		return (new BDDocBuilder()).cleanBadCharacters(sb.toString());
    	}
    	
    	
    	// added by hmo to deal with multiple bype characters at 10/27/2016, 
    	public static int[]  toCodePointArray(String str) { // 
    	    int len = str.length();          // the length of str
    	    int[] acp = new int[str.codePointCount(0, len)];
    	    int j = 0;                       // an index for acp

    	    for (int i = 0, cp; i < len; i += Character.charCount(cp)) {
    	        cp = str.codePointAt(i);
    	        acp[j++] = cp;
    	    }
    	    return acp;
    	}


    	//HH 07/27/2016 added for cafe AU/Af ES profile
    	
    	public static String mapUnicodeEntity(String str)
    	{
    		if(str == null)
    		{
    			return null;
    		}
    		int len = str.length();
    		StringBuffer sb = new StringBuffer();
    		char c;

    		for (int i = 0; i < len; i++)
    		{
    			c = str.charAt(i);
    			if((int) c != 34 && (int) c != 44 && (int) c !=58 && (int)c != 91 && (int)c != 93 && (int)c != 123 && (int)c != 125)
    			{
    				sb.append(c);
    			}
    			else 
    			{
    				//System.out.println("special char "+(int)c);
    				switch ((int) c)
    				{
    					
    					//HH 07/27/2016 for Cafe AU/AF ES profile
    					case 34: sb.append("&#x00022;");break;   // unicode double quotes
    					case 44: sb.append("&#x0002C;");break;	  // unicode comma
    					case 58: sb.append("&#x0003A;");break;	  // unicode colon
    					case 91: sb.append("&#x0005B;");break;	  // unicode left square bracket
    					case 93: sb.append("&#x0005D;");break;	  // unicode right square bracket
    					case 123: sb.append("&#x0007B;");break;	  // unicode left curly bracket
    					case 125: sb.append("&#x0007D;");break;	  // unicode right curly bracket
    					//HH END
    					
    					
    					//normal character
    					default:sb.append(c);
    					
    					break;
    				}
    			}
    		}
    		return sb.toString();
    	}
    	
    	
    	
    	public static String AlphaEntitysToNumericEntitys(String xml) 
    	{   
    		if(xml!=null)
    		{
    			   		
	    		StringBuffer stringBuffer = new StringBuffer();
	    		//xml = xml.replaceAll(" & ", "&#38;");
	    		Matcher matcher = ENTITY_PATTERN.matcher(xml);
	
	    		while (matcher.find()) {
	    			String replacement = entityMap.get(matcher.group(1));
	    			//System.out.println("input="+xml);
	    			//System.out.println("PATERN="+matcher.group(1)+" replacement="+replacement);
	    			if(replacement!=null && replacement.length()>0)
	    			{	    			
	    				xml = xml.replaceAll(matcher.group(1), replacement);
	    			}	    			
	    		}
	    			    		
	    		xml = handleJustAndSign(xml);
	    		    		
    		} 
    		
    		return xml;
    	}
    	
    	private static String handleJustAndSign(String input)
    	{
    		char[] inputCharArray = input.toCharArray();
    		StringBuffer output = new StringBuffer();
    		for(int i=0;i<inputCharArray.length;i++)
    		{
    			if(i<inputCharArray.length-1)
    			{
    				if(inputCharArray[i]=='&' && inputCharArray[i+1]!='#')
    				{
    					output.append("&#38;");
    				}
    				else
    				{
    					output.append(inputCharArray[i]);
    				}
    			}
    			else if(inputCharArray[inputCharArray.length-1]=='&')
    			{
    				output.append("&#38;");
    			}
    			else
    			{
    				output.append(inputCharArray[i]);
    			}
    		}
    		return output.toString();
    	}
    	
    	

}
