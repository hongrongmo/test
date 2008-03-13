<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	>
<xsl:template match="/">

	<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
	    <head>
			<xsl:text disable-output-escaping="yes">
			<![CDATA[
				<xsl:comment>
					<SCRIPT type="text/javascript" language="Javascript">
						var submitted=0;
						function Validator(trialform)
						{
							if (submitted==0)
							{
								if (trialform.firstname.value.length == 0)
								{
									alert("Please enter your first name.");
									trialform.firstname.focus();
										return false ;
								}
								if (trialform.lastname.value.length == 0)
								{
									alert("Please enter your last name.");
									trialform.lastname.focus();
									return false;
								}
								if (trialform.jobtitle.value.length == 0)
								{
									alert("Please enter your job title.");
									trialform.jobtitle.focus();
									return false;
								}
								if (trialform.company.value.length == 0)
								{
									alert("Please enter your company or institution.");
									trialform.company.focus();
									return false;
								}

								if (trialform.address1.value.length == 0){
									alert("Please enter your address.");
									trialform.address1.focus();
									return false;
								}
								if (trialform.city.value.length == 0){
									alert("Please enter your city.");
									trialform.city.focus();
									return false;
								}
								if (trialform.state.value.length == 0){
									alert("Please enter your state/province/region.");
									trialform.state.focus();
									return false;
								}
								if (trialform.zip.value.length == 0){
									alert("Please enter the ZIP/postal code.");
									trialform.zip.focus();
									return false;
								}

								selcountry = document.trialform.country;
								if (selcountry.options[selcountry.selectedIndex].value == "") {
								alert('Please choose a country.');
								var msg='document.trialform.country.selectedIndex="0"'
								eval(msg)
								trialform.country.focus();
								return false;
								}
								if (trialform.phone.value.length == 0) {
									alert("Please enter your phone number.");
									trialform.phone.focus();
									return false;
								}
								if (trialform.email.value.length == 0) {
									alert("Please enter your E-mail address.");
									trialform.email.focus();
									return false;
								}

								if (trialform.email.value.indexOf ('@',0) == -1 ){
									alert("The E-mail field contains an invalid E-mail format.\nPlease enter your correct E-mail address.");
									trialform.email.focus();
									return false;
								}
								selhearus = document.trialform.hearus;
								if (selhearus.options[selhearus.selectedIndex].value == "") {
								alert('Please select an option for how you heard about us.');
								var msg='document.trialform.hearus.selectedIndex="0"'
								eval(msg)
								trialform.hearus.focus();
								return false;
								}
								var selproduct = trialform.prodName;
								var product = 0;

								for (i = 0; i<selproduct.length; i++ )
								{
									if ( selproduct.options[i].selected )
									{
										if(selproduct.options[i].value !="")
										{
											product = 1;
										}
									}
								}

								if(product == 0)
								{
									alert("Please select a product.");
									trialform.prodName.focus();
									return false;
								}

								if(selproduct.selectedIndex == -1)
								{
									alert("Please select a product.");
									trialform.prodName.focus();
									return false;
								}

								submitted=1;
							}
							else {
								alert('The form has already been successfully submitted.');
								return false;
							}
						}
					</script>
				</xsl:comment>
				]]>
			</xsl:text>

	        <meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
	        <meta name="description" content="Elsevier Engineering Information, the leader in providing online information" />
	        <meta name="keywords" content="Engineering,Chemistry, Paper &amp; Pulp, Online Search Engine, Referex" />
	        <meta name="formatter" content="Us Media B.V. - http://www.usmedia.nl/" />
	        <meta name="author" content="Us Media B.V. - http://www.usmedia.nl/" />

	        <script type="text/javascript" src="http://www.ei.org/js/prototype.lite.js"></script>
	        <script type="text/javascript" src="http://www.ei.org/js/moo.fx.js"></script>
	        <script type="text/javascript" src="http://www.ei.org/js/moo.fx.pack.js"></script>
	        <script type="text/javascript">
	            var webproducts, content_databases, customer_support, news;
	            var toggle_elements = new Array ();

	            window.onload = function() {
	                toggle_elements[0] = new fx.Combo('webproducts', {height: true, opacity: false, duration: 150});
	                toggle_elements[1] = new fx.Combo('content_databases', {height: true, opacity: false, duration: 250});
	                toggle_elements[2] = new fx.Combo('support_center', {height: true, opacity: false, duration: 175});
	                toggle_elements[3] = new fx.Combo('news', {height: true, opacity: false, duration: 175});

	                toggle_elements[3].hide();
	                toggle_elements[1].hide();
	                toggle_elements[0].hide();

	            }
	            function do_toggle (n) {
	                for (var i = 0; i &lt; toggle_elements.length; i++) {
	                    if (i!= n) toggle_elements[i].hide();
	                    else toggle_elements[i].toggle();
	                }
	            }
	        </script>
	        <link href="http://www.ei.org/css/layout.css" rel="stylesheet" type="text/css" />
	        <!--[if IE]>
	        <link href="http://www.ei.org/css/ie_fix.css" rel="stylesheet" type="text/css" />
	        <![endif]-->
	        <link rel="alternate" type="application/rss+xml" title="RSS" href="http://www.ei.org/pressrelease.xml" />
	        <title>Ei.org - Trial Request</title>
	    </head>
	    <body id="trial_request">
	        <div id="container">
	        <div id="container_inner">
	            <h1><a href="http://www.ei.org/"><span>Engineering Information - Step Ahead</span></a></h1>
	            <dl id="navigation">
	                <dt>Navigation</dt>
	                <dd>
	                    <ul>
	                        <li id="nav_webproducts">
	                            <a onclick="do_toggle(0);"><span>Webproducts</span></a>
	                            <ul id="webproducts">
	                                <li><a href="http://www.ei.org/products/engineeringvillage.html">EngineeringVillage</a></li>
	                                <li><a href="http://www.ei.org/products/chemvillage.html">ChemVillage</a></li>
	                                <li><a href="http://www.ei.org/products/papervillage.html">PaperVillage</a></li>
	                                <li><a href="http://www.ei.org/products/encompassweb.html">EnCompassWEB</a></li>
	                            </ul>
	                        </li>
	                        <li id="nav_content_databases">
	                            <a onclick="do_toggle(1);"><span>Content Databases</span></a>
	                            <ul id="content_databases">
	                                <li><a href="http://www.ei.org/databases/compendex.html">Compendex</a></li>
	                                <li><a href="http://www.ei.org/databases/eib.html">Engineering Index Backfile </a></li>
	                                <li><a href="http://www.ei.org/databases/inspec.html">Inspec</a></li>
	                                <li><a href="http://www.ei.org/databases/inspec_archive.html">Inspec Archive</a></li>
	                                <li><a href="http://www.ei.org/databases/geobase.html">GeoBase</a></li>
	                                <li><a href="http://www.ei.org/databases/ntis_database.html">NTIS Database</a></li>
	                                <li><a href="http://www.ei.org/databases/ei_patents.html">Ei Patents</a></li>
	                                <li><a href="http://www.ei.org/databases/paperchem.html">PaperChem</a></li>
	                                <li><a href="http://www.ei.org/databases/referex_engineering.html">Referex Engineering</a></li>
	                                <li><a href="http://www.ei.org/databases/chimica.html">Chimica</a></li>
	                                <li><a href="http://www.ei.org/databases/cbnb.html">Chemical Business News Base</a></li>
	                                <li><a href="http://www.ei.org/databases/other.html">Other Publications</a></li>
	                            </ul>
	                        </li>
	                        <li id="nav_support_center">
	                            <a onclick="do_toggle(2);"><span>Support Center</span></a>
	                            <ul id="support_center">
	                                <li><a href="http://www.ei.org/support/index.html">Customer Support</a></li>
	                                <li><a href="http://www.ei.org/support/online_seminars.html">Online Seminars</a></li>
	                                <li><a href="http://www.ei.org/support/training_materials.html">Training Materials</a></li>
	                                <li><a href="http://www.ei.org/support/product_tours.html">Product Tours</a></li>
	                                <li><a href="http://www.ei.org/support/downloads.html">Downloads</a></li>
	                                <li class="active"><a href="http://www.ei.org/support/trial_request.html">Trial Request</a></li>
	                            </ul>
	                        </li>
	                        <li id="nav_news">
	                            <a onclick="do_toggle(3);"><span>Company News</span></a>
	                            <ul id="news">
	                                <li><a href="http://www.ei.org/news/archive.html">News Archive</a></li>
	                                <li><a href="http://www.ei.org/news/newsletters.html">Newsletters</a></li>
	                                <li><a href="http://www.ei.org/news/press.html">Media Contacts</a></li>
	<!--                            <li><a href="http://www.ei.org/news/out_and_about.html">Out and about</a></li> -->
	                            </ul>
	                        </li>
	<!--                    <li id="nav_about_ei">
	                            <a href="http://www.ei.org/about/"><span>About Ei</span></a>
	                        </li> -->
	                    </ul>
	                </dd>
	            </dl>

	            <div id="content">
	                <div id="content_inner">
	                    <!--[if lt IE 7]><em id="png_fixer"></em><![endif]-->
	                    <h2><span>Trial Request</span></h2>
	                    <p>Customers interested in learning about site licenses may request a free 30-day trial for any of Ei's electronic products.  See how your organization's research needs can be met.</p>
	                    <p>For site license customers we provide comprehensive usage reporting to help you understand and manage product uptake  across the communities you serve.</p>

	                    <form name="trialform" id="trialform" method="post" action="http://www.engineeringvillage.com/engresources/controller/servlet/Controller" onsubmit="return Validator(this);">
	                        <label for="firstname">
	                            <span>First Name: *</span>
	                            <input id="firstname" name="firstname" type="text" />
	                        </label>
	                        <label for="lastname">
	                            <span>Last Name: *</span>
	                            <input id="lastname" name="lastname" type="text" />
	                        </label>
	                        <label for="jobtitle">
	                            <span>Job Title:</span>
	                            <input id="jobtitle" name="jobtitle" type="text" />
	                        </label>
	                        <label for="company">
	                            <span>Company/Institution: *</span>
	                            <input id="company" name="company" type="text" />
	                        </label>
	                        <label for="website">
	                            <span>Company Web Site:</span>
	                            <input id="website" name="website" type="text" />
	                        </label>
	                        <label for="address1">
	                            <span>Address1:</span>
	                            <input id="address1" name="address1" type="text" />
	                        </label>
	                        <label for="address2">
	                            <span>Address2:</span>
	                            <input id="address2" name="address2" type="text" />
	                        </label>
	                        <label for="city">
	                            <span>City:</span>
	                            <input id="city" name="city" type="text" />
	                        </label>
	                        <label for="state">
	                            <span>State/Province/Region:</span>
	                            <input id="state" name="state" type="text" />
	                        </label>
	                        <label for="zip">
	                            <span>ZIP/Postal Code:</span>
	                            <input id="zip" name="zip" type="text" />
	                        </label>
	                        <label for="country">
	                            <span>Country: *</span>
	                            <select id="country" name="country">
	                                <option value="  " selected="selected">(please select a country)</option>
	                                <option value="--">none</option>
	                            <option value="Afghanistan">Afghanistan</option>
	                            <option value="Albania">Albania</option>
	                            <option value="Algeria">Algeria</option>
	                            <option value="American Samoa">American Samoa</option>
	                            <option value="Andorra">Andorra</option>
	                            <option value="Angola">Angola</option>
	                            <option value="Anguilla">Anguilla</option>
	                            <option value="Antarctica">Antarctica</option>
	                            <option value="Antigua and Barbuda">Antigua and Barbuda</option>
	                            <option value="Argentina">Argentina</option>
	                            <option value="Armenia">Armenia</option>
	                            <option value="Aruba">Aruba</option>
	                            <option value="Australia">Australia</option>
	                            <option value="Austria">Austria</option>
	                            <option value="Azerbaijan">Azerbaijan</option>
	                            <option value="Bahamas">Bahamas</option>
	                            <option value="Bahrain">Bahrain</option>
	                            <option value="Bangladesh">Bangladesh</option>
	                            <option value="Barbados">Barbados</option>
	                            <option value="Belarus">Belarus</option>
	                            <option value="Belgium">Belgium</option>
	                            <option value="Belize">Belize</option>
	                            <option value="Benin">Benin</option>
	                            <option value="Bermuda">Bermuda</option>
	                            <option value="Bhutan">Bhutan</option>
	                            <option value="Bolivia">Bolivia</option>
	                            <option value="Bosnia and Herzegowina">Bosnia and Herzegowina</option>
	                            <option value="Botswana">Botswana</option>
	                            <option value="Bouvet Island">Bouvet Island</option>
	                            <option value="Brazil">Brazil</option>
	                            <option value="British Indian Ocean Territory">British Indian Ocean Territory</option>
	                            <option value="Brunei Darussalam">Brunei Darussalam</option>
	                            <option value="Bulgaria">Bulgaria</option>
	                            <option value="Burkina Faso">Burkina Faso</option>
	                            <option value="Burundi">Burundi</option>
	                            <option value="Cambodia">Cambodia</option>
	                            <option value="Cameroon">Cameroon</option>
	                            <option value="Canada">Canada</option>
	                            <option value="Cape Verde">Cape Verde</option>
	                            <option value="Cayman Islands">Cayman Islands</option>
	                            <option value="Central African Republic">Central African Republic</option>
	                            <option value="Chad">Chad</option>
	                            <option value="Chile">Chile</option>
	                            <option value="China">China</option>
	                            <option value="Christmas Island">Christmas Island</option>
	                            <option value="Cocoa (Keeling) Islands">Cocoa (Keeling) Islands</option>
	                            <option value="Colombia">Colombia</option>
	                            <option value="Comoros">Comoros</option>
	                            <option value="Congo">Congo</option>
	                            <option value="Cook Islands">Cook Islands</option>
	                            <option value="Costa Rica">Costa Rica</option>
	                            <option value="Cote Divoire">Cote Divoire</option>
	                            <option value="Croatia (local name: Hrvatska)">Croatia (local name: Hrvatska)</option>
	                            <option value="Cuba">Cuba</option>
	                            <option value="Cyprus">Cyprus</option>
	                            <option value="Czech Republic">Czech Republic</option>
	                            <option value="Denmark">Denmark</option>
	                            <option value="Djibouti">Djibouti</option>
	                            <option value="Dominica">Dominica</option>
	                            <option value="Dominican Republic">Dominican Republic</option>
	                            <option value="East Timor">East Timor</option>
	                            <option value="Ecuador">Ecuador</option>
	                            <option value="Egypt">Egypt</option>
	                            <option value="El Salvador">El Salvador</option>
	                            <option value="Equatorial Guinea">Equatorial Guinea</option>
	                            <option value="Eritrea">Eritrea</option>
	                            <option value="Estonia">Estonia</option>
	                            <option value="Ethiopia">Ethiopia</option>
	                            <option value="Falkland Islands (Malvinas)">Falkland Islands (Malvinas)</option>
	                            <option value="Faroe Islands">Faroe Islands</option>
	                            <option value="Fiji">Fiji</option>
	                            <option value="Finland">Finland</option>
	                            <option value="France">France</option>
	                            <option value="France, Metropolitan">France, Metropolitan</option>
	                            <option value="French Guiana">French Guiana</option>
	                            <option value="French Polynesia">French Polynesia</option>
	                            <option value="French Southern Territories">French Southern Territories</option>
	                            <option value="Gabon">Gabon</option>
	                            <option value="Gambia">Gambia</option>
	                            <option value="Georgia">Georgia</option>
	                            <option value="Germany">Germany</option>
	                            <option value="Ghana">Ghana</option>
	                            <option value="Gibraltar">Gibraltar</option>
	                            <option value="Greece">Greece</option>
	                            <option value="Greenland">Greenland</option>
	                            <option value="Grenada">Grenada</option>
	                            <option value="Guadeloupe">Guadeloupe</option>
	                            <option value="Guam">Guam</option>
	                            <option value="Guatemala">Guatemala</option>
	                            <option value="Guinea">Guinea</option>
	                            <option value="Guinea-Bissau">Guinea-Bissau</option>
	                            <option value="Guyana">Guyana</option>
	                            <option value="Haiti">Haiti</option>
	                            <option value="Heard and Mc Donald Islands">Heard and Mc Donald Islands</option>
	                            <option value="Honduras">Honduras</option>
	                            <option value="Hong Kong">Hong Kong</option>
	                            <option value="Hungary">Hungary</option>
	                            <option value="Iceland">Iceland</option>
	                            <option value="India">India</option>
	                            <option value="Indonesia">Indonesia</option>
	                            <option value="Iran (Islamic Republic of)">Iran (Islamic Republic of)</option>
	                            <option value="Iraq">Iraq</option>
	                            <option value="Ireland">Ireland</option>
	                            <option value="Israel">Israel</option>
	                            <option value="Italy">Italy</option>
	                            <option value="Jamaica">Jamaica</option>
	                            <option value="Japan">Japan</option>
	                            <option value="Jordan">Jordan</option>
	                            <option value="Kazakhstan">Kazakhstan</option>
	                            <option value="Kenya">Kenya</option>
	                            <option value="Kiribati">Kiribati</option>
	                            <option value="Korea, Democratic Peoples Republic of">Korea, Democratic Peoples Republic of</option>
	                            <option value="Korea, Republic of">Korea, Republic of</option>
	                            <option value="Kuwait">Kuwait</option>
	                            <option value="Kyrgyzstan">Kyrgyzstan</option>
	                            <option value="Lao Peoples Democratic Republic">Lao Peoples Democratic Republic</option>
	                            <option value="Latvia">Latvia</option>
	                            <option value="Lebanon">Lebanon</option>
	                            <option value="Lesotho">Lesotho</option>
	                            <option value="Liberia">Liberia</option>
	                            <option value="Libyan Arab Jamahiriya">Libyan Arab Jamahiriya</option>
	                            <option value="Liechtenstein">Liechtenstein</option>
	                            <option value="Lithuania">Lithuania</option>
	                            <option value="Luxembourg">Luxembourg</option>
	                            <option value="Macau">Macau</option>
	                            <option value="Macedonia">Macedonia</option>
	                            <option value="Madagascar">Madagascar</option>
	                            <option value="Malawi">Malawi</option>
	                            <option value="Malaysia">Malaysia</option>
	                            <option value="Maldives">Maldives</option>
	                            <option value="Mali">Mali</option>
	                            <option value="Malta">Malta</option>
	                            <option value="Marshall Islands">Marshall Islands</option>
	                            <option value="Martinique">Martinique</option>
	                            <option value="Mauritania">Mauritania</option>
	                            <option value="Mauritius">Mauritius</option>
	                            <option value="Mayotte">Mayotte</option>
	                            <option value="Mexico">Mexico</option>
	                            <option value="Micronesia, Federated States Of">Micronesia, Federated States Of</option>
	                            <option value="Moldova, Republic of">Moldova, Republic of</option>
	                            <option value="Monaco">Monaco</option>
	                            <option value="Mongolia">Mongolia</option>
	                            <option value="Montserrat">Montserrat</option>
	                            <option value="Morocco">Morocco</option>
	                            <option value="Mozambique">Mozambique</option>
	                            <option value="Myanmar">Myanmar</option>
	                            <option value="Namibia">Namibia</option>
	                            <option value="Nauru">Nauru</option>
	                            <option value="Nepal">Nepal</option>
	                            <option value="Netherlands">Netherlands</option>
	                            <option value="Netherlands Antilles">Netherlands Antilles</option>
	                            <option value="New Caledonia">New Caledonia</option>
	                            <option value="New Zealand">New Zealand</option>
	                            <option value="Nicaragua">Nicaragua</option>
	                            <option value="Niger">Niger</option>
	                            <option value="Nigeria">Nigeria</option>
	                            <option value="Niue">Niue</option>
	                            <option value="Norfolk Island">Norfolk Island</option>
	                            <option value="Northern Mariana Islands">Northern Mariana Islands</option>
	                            <option value="Norway">Norway</option>
	                            <option value="Oman">Oman</option>
	                            <option value="Pakistan">Pakistan</option>
	                            <option value="Palau">Palau</option>
	                            <option value="Panama">Panama</option>
	                            <option value="Papua New Guinea">Papua New Guinea</option>
	                            <option value="Paraguay">Paraguay</option>
	                            <option value="Peru">Peru</option>
	                            <option value="Philippines">Philippines</option>
	                            <option value="Pitcairn">Pitcairn</option>
	                            <option value="Poland">Poland</option>
	                            <option value="Portugal">Portugal</option>
	                            <option value="Puerto Rico">Puerto Rico</option>
	                            <option value="Qatar">Qatar</option>
	                            <option value="Reunion">Reunion</option>
	                            <option value="Romania">Romania</option>
	                            <option value="Russian Federation">Russian Federation</option>
	                            <option value="Rwanda">Rwanda</option>
	                            <option value="Saint Kitts and Nevis">Saint Kitts and Nevis</option>
	                            <option value="Saint Lucia">Saint Lucia</option>
	                            <option value="Saint Vincent and the Grenadines">Saint Vincent and the Grenadines</option>
	                            <option value="Samoa">Samoa</option>
	                            <option value="San Marino">San Marino</option>
	                            <option value="Sao Tome and Principe">Sao Tome and Principe</option>
	                            <option value="Saudi Arabia">Saudi Arabia</option>
	                            <option value="Senegal">Senegal</option>
	                            <option value="Seychelles">Seychelles</option>
	                            <option value="Sierra Leone">Sierra Leone</option>
	                            <option value="Singapore">Singapore</option>
	                            <option value="Slovakia (Slovak Republic)">Slovakia (Slovak Republic)</option>
	                            <option value="Slovenia">Slovenia</option>
	                            <option value="Solomon Islands">Solomon Islands</option>
	                            <option value="Somalia">Somalia</option>
	                            <option value="South Africa">South Africa</option>
	                            <option value="South Georgia">South Georgia</option>
	                            <option value="Spain">Spain</option>
	                            <option value="Sri Lanka">Sri Lanka</option>
	                            <option value="St. Helena">St. Helena</option>
	                            <option value="St. Pierre and Miquelon">St. Pierre and Miquelon</option>
	                            <option value="Sudan">Sudan</option>
	                            <option value="Suriname">Suriname</option>
	                            <option value="Svalbard and Jan Mayen Islands">Svalbard and Jan Mayen Islands</option>
	                            <option value="Swaziland">Swaziland</option>
	                            <option value="Sweden">Sweden</option>
	                            <option value="Switzerland">Switzerland</option>
	                            <option value="Syrian Arab Republic">Syrian Arab Republic</option>
	                            <option value="Taiwan">Taiwan</option>
	                            <option value="Tajikistan">Tajikistan</option>
	                            <option value="Tanzania, United Republic of">Tanzania, United Republic of</option>
	                            <option value="Thailand">Thailand</option>
	                            <option value="Togo">Togo</option>
	                            <option value="Tokelau">Tokelau</option>
	                            <option value="Tonga">Tonga</option>
	                            <option value="Trinidad and Tobago">Trinidad and Tobago</option>
	                            <option value="Tunisia">Tunisia</option>
	                            <option value="Turkey">Turkey</option>
	                            <option value="Turkmenistan">Turkmenistan</option>
	                            <option value="Turks and Caicos Islands">Turks and Caicos Islands</option>
	                            <option value="Tuvalu">Tuvalu</option>
	                            <option value="Uganda">Uganda</option>
	                            <option value="Ukraine">Ukraine</option>
	                            <option value="United Arab Emirates">United Arab Emirates</option>
	                            <option value="United Kingdom">United Kingdom</option>
	                            <option value="United States" selected="selected">United States of America</option>
	                            <option value="United States Minor Outlying Islands">United States Minor Outlying Islands</option>
	                            <option value="Uruguay">Uruguay</option>
	                            <option value="Uzbekistan">Uzbekistan</option>
	                            <option value="Vanuatu">Vanuatu</option>
	                            <option value="Vatican City State (Holy See)">Vatican City State (Holy See)</option>
	                            <option value="Venezuela">Venezuela</option>
	                            <option value="Viet Nam">Viet Nam</option>
	                            <option value="Virgin Islands (British)">Virgin Islands (British)</option>
	                            <option value="Virgin Islands (U.S.)">Virgin Islands (U.S.)</option>
	                            <option value="Wallis and Futuna Islands">Wallis and Futuna Islands</option>
	                            <option value="Western Sahara">Western Sahara</option>
	                            <option value="Yemen">Yemen</option>
	                            <option value="Yugoslavia">Yugoslavia</option>
	                            <option value="Zaire">Zaire</option>
	                            <option value="Zambia">Zambia</option>
	                            <option value="Zimbabwe">Zimbabwe</option>
	                        </select>
	                        </label>
	                        <label for="phone">
	                            <span>Phone number:</span>
	                            <input id="phone" name="phone" type="text" />
	                        </label>
	                        <label for="email">
	                            <span>E-mail address: *</span>
	                            <input id="email" name="email" type="text" />
	                        </label>
	                        <label for="hearus">
	                            <span>How did you hear about EI products:</span>
	                            <select id="hearus" name="hearus">
	        						<option value="">Please select one</option>
	        						<option value="web">Link from other Web site (please state below)</option>
	        						<option value="conf">Conference/Exhibit (please state below)</option>
	        						<option value="mail">Direct mail (please state reference # below)</option>
	        						<option value="print">Print ad (please state which publication below)</option>
	        						<option value="search">Search engine (please state which one below)</option>
							        <option value="mouth">Word of mouth</option>
					        		<option value="banner">Internet banner ad</option>
			        				<option value="email">E-mail</option>
	        						<option value="other">Other (please explain)</option>
	                            </select>
	                        </label>
	                        <label for="explanation">
	                            <span>Reference / Source:</span>
	                            <input id="explanation" name="explanation" type="text" />
	                        </label>
	                        <label for="prodname">
	                            <span>Which products do you wish to trial?:</span>
	                            <select id="prodname" name="prodName" multiple="multiple">
	                                <option value="ev2">Engineering Village</option>
	                                <option value="">***********************************</option>
	                                <option value="compendex">Compendex</option>
	                                <option value="backfile">Engineering Index Backfile</option>
	                                <option value="inspec">Inspec</option>
                                  <option value="inspecback">Inspec Archive</option>
	                                <option value="ntis">NTIS</option>
	                                <option value="referex">Referex</option>
	                                <option value="patents">Ei Patents </option>
	                                <option value="enclit">EnCompassLIT</option>
	                                <option value="encpat">EnCompassPAT</option>
	                                <option value="geobase">GeoBase</option>
	                                <option value="chimica">Chimica</option>
	                                <option value="newsbase">Chemical Business NewsBase</option>
	                                <option value="paperchem">PaperChem</option>
	                                <option value="georef">GeoRef</option>
	                            </select>
	                        </label>

	                        <label class="expanded_label whitespace" for="bymail">
	                            <input id="bymail" name="bymail" type="checkbox" value="selected" />
	                            <span>Please send me Ei product literature by mail</span>
	                        </label>

	                        <label class="expanded_label" for="byemail">
	                            <input id="byemail" name="byemail" type="checkbox" value="selected" />
	                            <span>Please send me Ei's bi-monthly electronic news letter</span>
	                        </label>
	                        <hr />
	                        <p>
	                            <input type="submit" value="Send" /> <input type="reset" value="Reset" />
	                        </p>
	                    </form>

	                </div>
	            </div>


	            <dl id="links_key">
	                <dt>Key links</dt>
	                <dd>
	                    <ul>
	                        <li id="ten_years"><a href="http://www.ei.org/databases/ei_patents.html"><span>Introducing Ei Patents</span></a></li>
	                        <li id="new_technology"><a href="http://www.ei.org/support/online_seminars.html"><span>Featured Online Seminars</span></a></li>
	                        <li id="sign_up"><a href="http://daypass.engineeringvillage.com"><span>New Engineering Village Day Pass</span></a></li>
	                    </ul>
	                </dd>
	            </dl>

	            <dl id="navigation_misc">
	                <dt>Sitemap Navigation</dt>
	                <dd id="navigation_sitemap">
	                    <ul>
	                        <li class="first_child"><a href="http://www.ei.org/index.html">Home</a></li>
	                        <li><a href="http://www.ei.org/about/index.html">About Ei</a></li>
	                        <li><a href="http://www.ei.org/rss/">RSS feeds</a></li>
	                        <li><a href="http://www.ei.org/sitemap.html">Sitemap</a></li>
	                    </ul>
	                </dd>
	            </dl>

	            <hr />
	        </div>
	        </div>
	        <dl id="footer">
	            <dt>Footer Navigation</dt>
	            <dd>
	                <ul>
	                    <li class="first_child"><a href="mailto:eicustomersupport@elsevier.com">Feedback</a></li>
	                    <li><a href="http://www.ei.org/contact.html">Contact</a></li>
	                    <li><a href="http://www.ei.org/about/privacy.html">Privacy</a></li>
	                </ul>
	                <ul>
	                    <li class="first_child"><a href="http://www.ei.org/about/copyright.html">Copyright 2008 Elsevier Inc. All Rights Reserved</a></li>
	                </ul>
	            </dd>
	        </dl>
	    </body>
	</html>

</xsl:template>

</xsl:stylesheet>
