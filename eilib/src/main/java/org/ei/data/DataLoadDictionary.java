/*
 * Created on May 30, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ei.data;

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
    					case 92 :sb.append("&#92;");break;
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
    					case 194 :sb.append("&Acire;");break; 	//Capital A circumflex
    					case 195 :sb.append("&Atilde;");break; 	//Capital A tilde
    					case 196 :sb.append("&Auml;");break; 	//Capital A diaeresis
    					case 197 :sb.append("&Aring;");break; 	//Capital A ring above
    					case 198 :sb.append("&AElig;");break; 	//Capital AE
    					case 199 :sb.append("&Ccedil;");break; 	//Capital C cedilla
    					case 200 :sb.append("&Egrave;");break; 	//Capital E grave;
    					case 201 :sb.append("&Eacute;");break; 	//Capital E acute
    					case 202 :sb.append("&Ecire;");break; 	//Capital E circumflex
    					case 203 :sb.append("&Euml;");break; 	//Capital E diaeresis
    					case 204 :sb.append("&Igrave;");break; 	//Capital I acute
    					case 205 :sb.append("&Iacute;");break; 	//Capital E diaeresis
    					case 206 :sb.append("&Icire;");break; 	//Capital I circumflex
    					case 207 :sb.append("&Iuml;");break; 	//Capital I diaeresis
    					case 208 :sb.append("&ETH;");break; 	//Capital Eth, Edh, crossed D
    					case 209 :sb.append("&Ntilde;");break; 	//Capital Ntilde
    					case 210 :sb.append("&Ograve;");break; 	//Capital O grave
    					case 211 :sb.append("&Oacute;");break; 	//Capital O acute
    					case 212 :sb.append("&Ocire;");break; 	//Capital O circumflex
    					case 213 :sb.append("&Otilde;");break; 	//Capital O tilde
    					case 214 :sb.append("&Ouml;");break; 	//Capital O diaeresis
    					case 215 :sb.append("&times;");break; 	//Multiplication sign
    					case 216 :sb.append("&Oslash;");break; 	//latin O with stroke
    					case 223 :sb.append("&szlig;");break; 	//latin sharp S
    					case 225 :sb.append("&aacute;");break; 	//a acute
    					case 226 :sb.append("&acire;");break; 	//a circumflex
    					case 227 :sb.append("&atilde;");break; 	//a tilde
    					case 228 :sb.append("&auml;");break; 	//a diaeresis
    					case 229 :sb.append("&aring;");break; 	//a a ring above
    					case 230 :sb.append("&aelig;");break; 	//a ae
    					case 231 :sb.append("&ccedil;");break; 	//a c cedilla
    					case 232 :sb.append("&egrave;");break; 	//a e grave
    					case 233 :sb.append("&eacute;");break; 	//a e acute
    					case 234 :sb.append("&ecire;");break; 	//a circumflex
    					case 235 :sb.append("&euml;");break; 	//a e diaeresis
    					case 236 :sb.append("&igrave;");break; 	//a i grave
    					case 237 :sb.append("&iacute;");break; 	//a i acute
    					case 238 :sb.append("&icire;");break; 	//a circumflex
    					case 239 :sb.append("&iuml;");break; 	//a diaeresis
    					case 240 :sb.append("&eth;");break; 	//a eth
    					case 241 :sb.append("&ntilde;");break; 	//a n tilde
    					case 242 :sb.append("&ograve;");break; 	//a o grave
    					case 243 :sb.append("&oacute;");break; 	//a o acute
    					case 244 :sb.append("&ocire;");break; 	//o circumflex
    					case 245 :sb.append("&otilde;");break; 	//a tilde
    					case 246 :sb.append("&ouml;");break; 	//o diaeresis
    					case 247 :sb.append("&divide;");break; 	//division sign
    					case 248 :sb.append("&oslash;");break; 	//o stroke
    					case 249 :sb.append("&ugrave;");break; 	//u grave
    					case 250 :sb.append("&uacute;");break; 	//u acute
    					case 251 :sb.append("&ucire;");break; 	//o circumflex
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
    					case 977 :sb.append("&thetasym;");break;//theta symbol
    					case 978 :sb.append("&upsih;");break; 	//greek upsilon with hook symbol
    					case 982 :sb.append("&piv;");break; 	//greek pi symbol
    					case 1488 :sb.append("&aleph;");break; 	//hebrew aleph
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
    					case 8606 :sb.append("&Larr;");break; 	//leftwards two headed arrow
    					case 8608 :sb.append("&Rarr;");break; 	//rightwards two headed arrow
    					case 8629 :sb.append("&crarr;");break;  //downward arrow with corner leftward, carriage return
    					case 8636 :sb.append("&lharu;");break; 	//leftwards harpoon with barb upwards
    					case 8637 :sb.append("&lhard;");break; 	//leftwards harpoon with barb downwards
    					case 8640 :sb.append("&rharu;");break; 	//rightwards harpoon with barb upwars
    					case 8641 :sb.append("&rhard;");break;  //rightwards harpoon with barb downwards
    					case 8644 :sb.append("&rlarr2;");break; //right over left arrow N.B. rlarr in ES grid
    					case 8646 :sb.append("&lrarr2;");break; //left over right arrow N.B. lrarr in ES grid
    					case 8651 :sb.append("&lrhar2;");break; //left over right harpoon N.B. lrhar in ES grid
    					case 8652 :sb.append("&rlhar2;");break;  //right over left harpoon N.B. rlhar in ES grid
    					case 8656 :sb.append("&lArr;");break; 	//leftward double arrow
    					case 8657 :sb.append("&uArr;");break; 	//upward double arrow
    					case 8658 :sb.append("&rArr;");break;	//rightward double arrow
    					case 8659 :sb.append("&dArr;");break;   //Downward double arrow
    					case 8660 :sb.append("&hArr;");break;  	//left-right double
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
    					case 9001 :sb.append("&lang;");break;  	//left pointing angle bracket, bra
    					case 9002 :sb.append("&rang;");break; 	//right pointing angle bracket, ket
    					case 9632 :sb.append("&squf;");break; 	//black square
    					case 9633 :sb.append("&squ;");break;	//white square
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
    					case 9674 :sb.append("&loz;");break; 	//lozenge
    					case 9675 :sb.append("&cir;");break; 	//white circle
    					case 9792 :sb.append("&female;");break; //female sign
    					case 9794 :sb.append("&male;");break; 	//male sign
    					case 9824 :sb.append("&spades;");break; //black spade suit
    					case 9827 :sb.append("&clubs;");break; 	//black club suit, shamrock
    					case 9829 :sb.append("&hearts;");break; //black heart suit, valentine
    					case 9830 :sb.append("&diams;");break;	//Black diamond suit
    					case 10913 :sb.append("&LessLess;");break; //much less than (double) (EW=Bkl)
    					case 10914 :sb.append("&GreaterGreater;");break; 	//much greater than (double) (EW=Bml)
    					case 12296 :sb.append("&lang;");break; //left angle bracket
    					case 12297 :sb.append("&rang;");break;	//right angle bracket

    					default:sb.append("");
    					break;
    				}
    			}
    		}
    		return sb.toString();
    	}
    	
       	public static String mapThesEntity(String xml)
    		{
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

    						sb.append("&#"+(int)c+";");
    				}
    			}
    			return sb.toString();
        	}



}
