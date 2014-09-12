package test.org.ei.session;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ei.ane.entitlements.UserEntitlement;
import org.ei.biz.personalization.EVWebUser;
import org.ei.biz.personalization.cars.Account;
import org.ei.service.cars.security.authorization.UserAccessType;
import org.ei.session.CARSMetadata;
import org.ei.session.UserSession;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import test.org.ei.action.BaseActionTest;

import com.elsevier.webservices.schemas.csas.constants.types.v7.RefworksAuthTypeCodeType;

public class UserSessionTest extends BaseActionTest {
    private static Logger log4j = Logger.getLogger(UserSessionTest.class);

    private static EVWebUser sampleUser = new EVWebUser();



    @BeforeClass
    private void init() {
    }

    /**
     * Tests to ensure various configurations of the UserSession object can be serialized/deserialized
     */
    @Test
    public void serializeSessionTest() {
        CARSMetadata sampleCARSMetadata = new CARSMetadata();
        Set<UserEntitlement> sampleEntitlements = new HashSet<UserEntitlement>();
        Properties sampleSessionProperties = new Properties();

        // ***************************************************************************
        // Setup items to be put into UserSession object
        // ***************************************************************************
        sampleUser.setAccessType(UserAccessType.IPRANGE);
        Account account = new Account();
        account.setAccountId("123456");
        account.setAccountName("Sample Test Account");
        account.setAccountNumber("A123456");
        account.setDepartmentId("D123456");
        account.setDepartmentName("Sample Dept");

        account.setRefworksAuthenticationType(RefworksAuthTypeCodeType.REFWORKS);
        sampleUser.setAccount(account);

        sampleCARSMetadata.setHeaderContent(sampleHeader);
        sampleCARSMetadata.setCurrentPage(null);
        sampleCARSMetadata.setFlowComplete(true);
        sampleCARSMetadata.setiPAuthStatus("TEST");
        sampleCARSMetadata.setLastSuccessAccessTime((new Date()).toString());

        sampleEntitlements.add(UserEntitlement.CPX_ENTITLEMENT);
        sampleEntitlements.add(UserEntitlement.C84_ENTITLEMENT);
        sampleEntitlements.add(UserEntitlement.INS_ENTITLEMENT);
        sampleEntitlements.add(UserEntitlement.IBF_ENTITLEMENT);

        sampleSessionProperties.put(UserSession.AUTHTOKEN, "<authtoken_value>");
        sampleSessionProperties.put(UserSession.NAV_STATE, "<nav_state>");
        sampleSessionProperties.put(UserSession.HIGHLIGHT_STATE, "<highlight_state>");
        sampleSessionProperties.put(UserSession.RECORDS_PER_PAGE, "<recordsperpage_state>");
        sampleSessionProperties.put(UserSession.SHOW_MAX_ALERT, "<showmaxalert_state>");
        sampleSessionProperties.put(UserSession.SHOW_MAX_ALERTCLEAR, "<showmaxalertclear_state>");
        sampleSessionProperties.put(UserSession.SHOW_NEWSEARCH_ALERT, "<shownewsearchalert_state>");
        sampleSessionProperties.put(UserSession.NEXT_URL, "<nexturl_state>");
        sampleSessionProperties.put(UserSession.BACK_URL, "<backurl_state>");

        UserSession us = new UserSession();
        us.setCarsMetaData(sampleCARSMetadata);
        us.addUserEntitlements(sampleEntitlements);
        us.setProperties(sampleSessionProperties);

        byte[] ser = null;
        try{
            ser = serialize(us);
        } catch (Throwable t) {
            Assert.fail("Unable to serialize, case 1!", t);
            return;
        }

        log4j.info("Size of UserSession object: " + ser.length);

        try{
            UserSession uscopy = (UserSession) deserialize(ser);
            Assert.assertNull(uscopy.getCarsMetaData(), "CARSMetaData object should be null after deser!");
            Assert.assertTrue(uscopy.getUserEntitlements().isEmpty(), "UserEntitlements Set should be empty after deser!");
            Assert.assertTrue(uscopy.getUserTextZones().isEmpty(), "UserTextZones List should be null after deser!");

            Assert.assertEquals(uscopy.getAuthtoken(), us.getAuthtoken());
            Assert.assertEquals(uscopy.getBackUrl(), us.getBackUrl());
            Assert.assertEquals(uscopy.getNextUrl(), us.getNextUrl());
            Assert.assertEquals(uscopy.getBrowserSSOKey(), us.getBrowserSSOKey());
            Assert.assertEquals(uscopy.getEntryToken(), us.getEntryToken());
            Assert.assertEquals(uscopy.getHighlightState(), us.getHighlightState());
            Assert.assertEquals(uscopy.getNavState(), us.getNavState());
            Assert.assertEquals(uscopy.getHighlightState(), us.getHighlightState());
            Assert.assertEquals(uscopy.getProperty(UserSession.SHOW_MAX_ALERT), us.getProperty(UserSession.SHOW_MAX_ALERT));
            Assert.assertEquals(uscopy.getProperty(UserSession.SHOW_MAX_ALERTCLEAR), us.getProperty(UserSession.SHOW_MAX_ALERTCLEAR));
            Assert.assertEquals(uscopy.getProperty(UserSession.SHOW_NEWSEARCH_ALERT), us.getProperty(UserSession.SHOW_NEWSEARCH_ALERT));
            Assert.assertEquals(uscopy.getRecordsPerPage(), us.getRecordsPerPage());
            Assert.assertEquals(uscopy.getHighlightState(), us.getHighlightState());
            Assert.assertEquals(uscopy.getHighlightState(), us.getHighlightState());

        } catch (Throwable t) {
            Assert.fail("Unable to deserialize, case 1!", t);
        }


    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(obj);
        return b.toByteArray();
    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        return o.readObject();
    }

    private final String sampleHeader =
        "<link xmlns:cars=\"http://cars-services.elsevier.com/cars/server\" xmlns:apps=\"http://apps-services.elsevier.com/apps/server\" xmlns:rb=\"http://RB-services.elsevier.com/resourceBundle/server\" rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"https://cdc315-acw.elsevier.com/SSOCore/css/cars/cars_common.css\"><script xmlns:cars=\"http://cars-services.elsevier.com/cars/server\" xmlns:apps=\"http://apps-services.elsevier.com/apps/server\" xmlns:rb=\"http://RB-services.elsevier.com/resourceBundle/server\" type=\"text/javascript\">\n"
            +
            "function flipLogin(button,from){\n"
            +
            "         if(from == \"loginbox\"){ \n"
            +
            "          var inBox = document.getElementById('loginBox');\n"
            +
            "          if (inBox.style.display == \"none\"){\n"
            +
            "              inBox.style.display = 'block';\n"
            +
            "              }\n"
            +
            "          else {\n"
            +
            "                inBox.style.display = 'none';\n"
            +
            "          }\n"
            +
            "\n"
            +
            "           }\n"
            +

            "   if(from == \"logoutbox\"){\n"
            +
            "       var outBox = document.getElementById('logoutBox').style;\n"
            +
            "       var outPlus = document.getElementById('logoutPlus').style;\n"
            +
            "       var outMinus = document.getElementById('logoutMinus').style;\n"
            +
            "       if (outBox.display == \"none\"){\n"
            +
            "           outBox.display = 'block';\n"
            +
            "           outPlus.display = 'none';\n"
            +
            "           outMinus.display = 'block';\n"
            +
            "       } else {\n"
            +
            "            outBox.display = 'none';\n"
            +
            "            outPlus.display = 'block';\n"
            +
            "            outMinus.display = 'none';\n"
            +
            "       }\n"
            +
            "\n"
            +
            "      }\n"
            +
            "}\n"
            +
            "    \n"
            +
            "function openPopup(uri){\n"
            +
            "    window.open(uri,'registeredUserAgreement',\n"
            +
            "    'scrollbars=yes,resizable=yes,directories=no,toolbar=no,menubar=no,status=no,width=760,height=600,left=25,top=25');\n"
            +
            "}\n"
            +
            "</script><div xmlns:cars=\"http://cars-services.elsevier.com/cars/server\" xmlns:apps=\"http://apps-services.elsevier.com/apps/server\" xmlns:rb=\"http://RB-services.elsevier.com/resourceBundle/server\" class=\"clearfix\" id=\"login\">\n"
            +
            "<ul>\n"
            +
            "<li class=\"cars_register\"><a href=\"https://localhost.engineeringvillage.com/customer/profile/display.url?origin=home&amp;zone=header\" title=\"Register a username and password for using alerts and other personal features\">Register</a></li>\n"
            +
            "<li class=\"login\">\n"
            +
            "<div id=\"divJSLogin\" style=\"display: none\"><a class=\"plus\" id=\"loginPlus\" onclick=\"flipLogin('loginBox','loginbox');\" href=\"javascript:void(0)\" title=\"Login to EV\">Login</a></div><script type=\"text/javascript\">\n"
            +
            "                document.getElementById (\"divJSLogin\").style.display = \"block\";\n"
            +
            "            </script><noscript><a class=\"plus\" href=\"https://localhost.engineeringvillage.com/customer/authenticate/loginfull.url?origin=loginBox&amp;zone=header\" id=\"loginPlusnojs\" title=\"Login to EV\">Login</a></noscript>\n"
            +
            "</li>\n"
            +
            "<li class=\"last\">\n"
            +
            " <apps:login_links><a href=\"/customer/terminate.url?http_method_name=DELETE&amp;isClaimingRemoteAccess=FALSE\">End Session</a></apps:login_links>\n"
            +
            "</li>\n"
            +
            "</ul>\n"
            +
            "</div>\n"
            +
            "<div xmlns:cars=\"http://cars-services.elsevier.com/cars/server\" xmlns:apps=\"http://apps-services.elsevier.com/apps/server\" xmlns:rb=\"http://RB-services.elsevier.com/resourceBundle/server\" id=\"loginBox\" class=\"bg4\" style=\"width:600px;padding:14px;\">\n"
            +
            "<div class=\"\" id=\"social-remote-institution\" style=\"display:none;float:right;width:280px;min-height:180px;border-left:1px solid #9B9B9B;padding-left:10px;\">\n"
            +
            "<p id=\"institutionheading\" style=\"display:none;padding-left:0px\"><label><b>Login via your institution</b></label></p>\n"
            +
            "<div id=\"institutionAvail\">\n"
            +
            " <p class=\"institutiontext\" style=\"border:none;margin-left:0px;padding 10px 0;padding-left:0px;\"><a href=\"#\" onclick=\"document.institutionForm.submit();return false;\" title=\"Go to Athens / Institution login\">Go to Athens / Institution login</a></p>\n"
            +
            "</div>\n"
            +
            "<hr id=\"institutionHrline\" style=\"display:none;margin-top:5px;background-color:#9B9B9B;height:1px;border:none; width:270px;\">\n"
            +
            "<form name=\"institutionForm\" action=\"https://localhost.engineeringvillage.com/customer/institutionchoice.url\" method=\"post\"><input type=\"hidden\" name=\"shibboleth_fence\" value=\"true\"><input type=\"hidden\" name=\"origin\" value=\"loginBox\"><input type=\"hidden\" name=\"zone\" value=\"main\"></form>\n"
            +
            "<form name=\"institutionChoiceForm\" action=\"https://localhost.engineeringvillage.com/customer/institutionchoice.url\" method=\"post\"><input type=\"hidden\" name=\"shibboleth_fence\" value=\"true\"><input type=\"hidden\" name=\"origin\" value=\"loginBox\"><input type=\"hidden\" name=\"zone\" value=\"main\"></form\n"
            +
            "<div id=\"pathAvail\"></div>\n"
            +
            "<hr id=\"pathchoiceHrline\" style=\"display:none;margin-top:5px;background-color:#9B9B9B;height:1px;border:none; width:270px;\">\n"
            +
            "<div id=\"selfmanraAvail\">\n"
            +
            " <p><label><b>Remote access activation</b></label></p>\n"
            +
            " <p class=\"manraenabledtext\" style=\"border:none;padding-left:0px;\"><a href=\"https://localhost.engineeringvillage.com/customer/authenticate/manra.url\" title=\"Remote access activation\">Click here to activate</a></p>\n"
            +
            "</div>\n"
            +
            "<hr id=\"selfmanraHrline\" style=\"display:none;margin-top:5px;background-color:#9B9B9B;height:1px;border:none; width:270px;\">\n"
            +
            "<div id=\"socialAvail\"></div>\n"
            +
            "</div>\n"
            +
            "<div class=\"\" id=\"form-div\" style=\"width:285px;float:right;padding-right:10px;\">\n"
            +
            "<form id=\"login-box-form\" action=\"https://localhost.engineeringvillage.com/customer/authenticate.url\" method=\"post\">\n"
            +
            " <p style=\"margin: 0 0 10px !important;\"><label><b>Login using your Elsevier credentials</b></label></p>\n"
            +
            " <p style=\"margin: 0 0 2px !important\"><label for=\"username\"><span>Username:</span><input id=\"username\" name=\"username\" type=\"text\" maxlength=\"100\" size=\"13\" style=\"width:190px;\" alt=\"Username\"></label></p>\n"
            +
            " <p style=\"margin: 0px !important\"><label for=\"password\"><span>Password:</span><input name=\"password\" type=\"password\" id=\"password\" maxlength=\"100\" size=\"13\" style=\"width:190px;\" alt=\"Password\"></label></p>\n"
            +
            " <p class=\"remember\" style=\"margin: 10px 0 9px 70px !important;\"><input id=\"remember\" name=\"remember_flag\" type=\"checkbox\" checked value=\"true\" class=\"loginCheck marginR5\" title=\"Remember me\"><label for=\"remember\" title=\"Remember my username and password on this computer\">Remember me</label></p>\n"
            +
            " <p class=\"submit\" style=\"margin: 16px 0 9px 70px !important;\"><input name=\"submit\" type=\"submit\" value=\"Login\" class=\"button\" style=\"margin: 0 15px 0 0 !important;\" alt=\"Login\" title=\"Login\"><span class=\"cars_register\">\n"
            +
            "                                    | <a href=\"https://localhost.engineeringvillage.com/customer/profile/display.url?origin=loginBox&amp;zone=main\" style=\"margin-left: 15px !important;\" title=\"Register a username and password for using alerts and other personal features\">Not Registered?</a></span></p><input type=\"hidden\" name=\"auth_type\" value=\"LOGIN_PASSWORD\"><p class=\"forgotpass\" style=\"margin: 10px 0 9px 68px !important;\"><a href=\"javascript:openPopup('https://localhost.engineeringvillage.com/customer/reminder.url?origin=loginBox&amp;zone=main');\" title=\"Forgotten your username or password\">Forgotten your username or password?</a></p><input type=\"hidden\" name=\"http_method_name\" value=\"GET\"><input type=\"hidden\" name=\"isClaimingRemoteAccess\" value=\"FALSE\"><input type=\"hidden\" name=\"shibboleth_fence\" value=\"true\"><input type=\"hidden\" name=\"remember_path_flag\" value=\"false\"><input type=\"hidden\" name=\"path_choice\" value=\"\"><input type=\"hidden\" name=\"path_choice_exists\" value=\"false\"><input type=\"hidden\" name=\"acct_name\" value=\"EngVillagePathChoice\"><input type=\"hidden\" name=\"dept_name\" value=\"Path Choice Dept\"><input type=\"hidden\" name=\"cars_cookie_flag\" value=\"true\"><input type=\"hidden\" name=\"origin\" value=\"loginBox\"><input type=\"hidden\" name=\"zone\" value=\"main\"></form>\n"
            +
            "</div>\n"
            +
            "</div><script xmlns:cars=\"http://cars-services.elsevier.com/cars/server\" xmlns:apps=\"http://apps-services.elsevier.com/apps/server\" xmlns:rb=\"http://RB-services.elsevier.com/resourceBundle/server\" type=\"text/javascript\">\n"
            +
            "\n"
            +
            "if('' !=\"\"){\n"
            +
            "gigya.socialize.showLoginUI({\n"
            +
            "        height: 100\n"
            +
            "           ,width: 300,\n"
            +
            "        showTermsLink:false \n"
            +
            "        ,hideGigyaLink:true \n"
            +
            "        ,buttonsStyle: 'standard'\n"
            +
            "        ,enabledProviders: ''\n"
            +
            "        ,showWhatsThis: false \n"
            +
            "               ,containerID: 'loginDiv'\n"
            +
            "        ,cid:''\n"
            +
            "        ,sessionExpiration:0,\n"
            +
            "        redirectURL:'https://localhost.engineeringvillage.com/customer/authenticate.url?auth_type=SOCIAL',\n"
            +
            "        authCodeOnly:'true',\n"
            +
            "        authFlow:'redirect'\n"
            +
            "        ,UIConfig: '<config><body><controls><snbuttons buttonsize=\"20\" /></controls></body></config>'\n"
            +
            "        ,onLoad:function(){changegigyaTitle();}\n"
            +
            "        });\n"
            +
            "    }\n"
            +
            "    \n"
            +
            "    var instElem = document.getElementById('institutionAvail').innerHTML.length;\n"
            +
            "    var pathElem = document.getElementById('pathAvail').innerHTML.length;\n"
            +
            "    var manraElem = document.getElementById('selfmanraAvail').innerHTML.length;\n"
            +
            "    var socialElem = '';    \n"
            +
            "    if(socialElem && (instElem || pathElem || manraElem)){\n"
            +
            "        document.getElementById('selfmanraHrline').style.display = 'block';\n"
            +
            "    }\n"
            +
            "    if(manraElem && (instElem || pathElem)){\n"
            +
            "        document.getElementById('pathchoiceHrline').style.display = 'block';\n"
            +
            "    }\n"
            +
            "    if(pathElem && instElem){\n"
            +
            "        document.getElementById('institutionHrline').style.display = 'block';\n"
            +
            "    }\n"
            +
            "    if(instElem){\n"
            +
            "        document.getElementById('institutionheading').style.display = 'block';\n"
            +
            "    }\n"
            +
            "    \n"
            +
            "    if(socialElem || instElem || pathElem || manraElem){\n"
            +
            "        document.getElementById('social-remote-institution').style.display = 'block';\n"
            +
            "        \n"
            +
            "    }else{\n"
            +
            "        document.getElementById('social-remote-institution').style.display = 'none';\n"
            +
            "        document.getElementById('loginBox').style.width = '320px';\n"
            +
            "    }\n"
            +
            "    \n"
            +
            "    function changegigyaTitle(){\n"
            +
            "        var gigyaElemarr=document.getElementById('loginDiv').getElementsByTagName('*');\n"
            +
            "        for(var i=0;i<gigyaElemarr.length;i++){\n"
            +
            "         if(gigyaElemarr[i].getAttribute('gigid')){\n"
            +
            "           var socialName = gigyaElemarr[i].parentNode.getAttribute('title');\n"
            +
            "           gigyaElemarr[i].parentNode.setAttribute('title',\"Use your \"+socialName+\" account to login to EV\");\n"
            +
            "            }\n"
            +
            "        }\n"
            +
            "    }\n"
            +
            "    \n"
            +
            "    \n"
            +
            "</script><style xmlns:cars=\"http://cars-services.elsevier.com/cars/server\" xmlns:apps=\"http://apps-services.elsevier.com/apps/server\" xmlns:rb=\"http://RB-services.elsevier.com/resourceBundle/server\" type=\"text/css\">\n"
            +
            "    \n" +
            "    #loginDiv, #loginDiv table, #loginDiv table > table {\n" +
            "        height: auto !important;\n" +
            "        width: auto !important;\n" +
            "        padding-top:10px;\n" +
            "    }\n" +
            "    #login-box-form label{\n" +
            "        margin-left:0px !important;\n" +
            "    }\n" +
            "    #loginDiv table td{\n" +
            "        text-align:left !important;\n" +
            "    }\n" +

            "</style>\\n" +
            "\n";

}
