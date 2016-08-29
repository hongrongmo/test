/*
 * Created on May 30, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ei.dataloading;

/**
 * @author solovyevat
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DataLoadDictionary
{
    	public static String mapEntity(String xml)
    	{
    		if(xml == null)
    		{
    			return null;
    		}
    		int len = xml.length();
    		StringBuffer sb = new StringBuffer();
    		char c;

    		for (int i = 0; i < len; i++)
    		{
    			c = xml.charAt(i);
    			if((int) c >= 32 && (int) c <= 127)
    			{
    				sb.append(c);
    			}
    			else if(((int) c >= 128 || (int) c < 32))
    			{
    				//System.out.println("special char "+(int)c);
    				switch ((int) c)
    				{
    					case 30 :sb.append(c);break;
    					case 31 :sb.append(c);break;
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
    					case 986 :sb.append("&#x03DA");break; //GREEK LETTER STIGMA Ϛ
    					case 987 :sb.append("&#x03DB");break; //GREEK SMALL LETTER STIGMA ϛ
    					case 988 :sb.append("&Gammad;");break; //GREEK LETTER DIGAMMA (F) Ϝ
    					case 989 :sb.append("&gammad;");break; //GREEK SMALL LETTER DIGAMMA (f) ϝ
    					
    					case 990 :sb.append("&#x03DE");break; //GGREEK LETTER KOPPA Ϟ
    					case 991 :sb.append("&#x03DF");break; //GREEK SMALL LETTER KOPPA ϟ
    					case 992 :sb.append("&#x03E0");break; //GREEK LETTER SAMPI Ϡ
    					case 993 :sb.append("&#x03E1");break; //GREEK SMALL LETTER SAMPI ϡ
    					
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
    					case 8220 :sb.append("&ldquo;");break; 	//left double quotation
    					case 8221 :sb.append("&rdquo;");break; 	//right double quotation
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
    					default:sb.append("&#"+(int)c+";");
    					//System.out.println("UNKNOW-CHARACTERS="+"(&#"+(int)c+";)");
    					break;
    				}
    			}
    		}
    		return sb.toString();
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

}
