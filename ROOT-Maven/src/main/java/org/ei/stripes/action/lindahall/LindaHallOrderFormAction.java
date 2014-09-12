package org.ei.stripes.action.lindahall;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrorHandler;
import net.sourceforge.stripes.validation.ValidationErrors;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.backoffice.LindaHallBroker;
import org.ei.biz.email.SESEmail;
import org.ei.biz.email.SESMessage;
import org.ei.config.EVProperties;
import org.ei.config.RuntimeProperties;
import org.ei.domain.ContactInfo;
import org.ei.domain.LhlUserInfo;
import org.ei.session.UserSession;
import org.ei.util.StringUtil;

@UrlBinding("/lindahall/orderform.url")
public class LindaHallOrderFormAction extends LindaHallAction implements ValidationErrorHandler {
    private final static Logger log4j = Logger.getLogger(LindaHallOrderFormAction.class);

    @Validate(required=true)
    private String method = "Email";
    @Validate(required=true)
    private String service = "Regular service";

    private String attention;
    @Validate(required=true)
    private String firstname;
    @Validate(required=true)
    private String lastname;
    @Validate(required=true)
    private String companyname;
    @Validate(required=true)
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String country;
    private String zip;
    private String fax;
    @Validate(required=true)
    private String confirmationEmail;
    private String phone;

    private String deliveryEmail;
    private String ariel;

    private String fedex;
    private String accountnumber;

    @Before
    private Resolution auth() {
        // Check to see if we've authenticated yet
        LhlUserInfo lhluserinfo = (LhlUserInfo) context.getRequest().getSession().getAttribute(LHL_SESSION_USERINFO);
        if (lhluserinfo == null) {
            log4j.warn("User is not yet authenticated - sending to auth form!");
            return buildAuthResolution();
        }
        return null;
    }

    /**
     * Attempt to display the LHL order form.  If the user has not yet authenticated,
     * show the authentication form first!
     * @return
     * @throws Exception
     */
    @DefaultHandler
    @DontValidate
    public Resolution orderform() throws Exception {
        LhlUserInfo lhluserinfo = (LhlUserInfo) context.getRequest().getSession().getAttribute(LHL_SESSION_USERINFO);

        // Authentication present, get the document
        context.getRequest().setAttribute("lhldoc", getDocumentXML());
        // Fill form values from contact info if they are empty
        if (GenericValidator.isBlankOrNull(firstname)) {
            firstname = lhluserinfo.getContactInfo().getUserFirstName();
        }
        if (GenericValidator.isBlankOrNull(lastname)) {
            lastname = lhluserinfo.getContactInfo().getUserLastName();
        }
        if (GenericValidator.isBlankOrNull(companyname)) {
            companyname = lhluserinfo.getContactInfo().getCompany();
        }
        if (GenericValidator.isBlankOrNull(address1)) {
            address1 = lhluserinfo.getContactInfo().getAddress1();
        }
        if (GenericValidator.isBlankOrNull(address2)) {
            address2 = lhluserinfo.getContactInfo().getAddress2();
        }
        if (GenericValidator.isBlankOrNull(city)) {
            city = lhluserinfo.getContactInfo().getCity();
        }
        if (GenericValidator.isBlankOrNull(state)) {
            state = lhluserinfo.getContactInfo().getState();
        }
        if (GenericValidator.isBlankOrNull(country)) {
            country = lhluserinfo.getContactInfo().getCountry();
        }
        if (GenericValidator.isBlankOrNull(zip)) {
            zip = lhluserinfo.getContactInfo().getZip();
        }
        if (GenericValidator.isBlankOrNull(fax)) {
            fax = lhluserinfo.getContactInfo().getFaxNumber();
        }
        if (GenericValidator.isBlankOrNull(confirmationEmail)) {
            confirmationEmail = lhluserinfo.getContactInfo().getUserEmail();
        }
        if (GenericValidator.isBlankOrNull(deliveryEmail)) {
            deliveryEmail = lhluserinfo.getContactInfo().getUserEmail();
        }
        if (GenericValidator.isBlankOrNull(phone)) {
            phone = lhluserinfo.getContactInfo().getPhoneNumber();
        }

        // Forward to JSP
        return new ForwardResolution("/WEB-INF/pages/customer/lindahall/orderform.jsp");

    }

    /**
     * Handle the order submission/edit
     * @return
     * @throws Exception
     */
    @HandlesEvent("submitorder")
    @Validate
    public Resolution submitorder() throws Exception {
        LhlUserInfo lhluserinfo = (LhlUserInfo) context.getRequest().getSession().getAttribute(LHL_SESSION_USERINFO);

        // Authentication present, get the document
        context.getRequest().setAttribute("lhldoc", getDocumentXML());

        // Misc validation (beyond @Validate)
        if ("Email".equals(method)) {
            if (GenericValidator.isBlankOrNull(deliveryEmail)) {
                context.getValidationErrors().addGlobalError(new LocalizableError("org.ei.stripes.action.lindahall.LindaHallOrderFormAction.deliveryEmail.valueNotPresent"));
                return new ForwardResolution("/WEB-INF/pages/customer/lindahall/orderform.jsp");
            }
        }
        if ("Ariel".equals(method)) {
            if (GenericValidator.isBlankOrNull(ariel)) {
                context.getValidationErrors().addGlobalError(new LocalizableError("org.ei.stripes.action.lindahall.LindaHallOrderFormAction.ariel.valueNotPresent"));
                return new ForwardResolution("/WEB-INF/pages/customer/lindahall/orderform.jsp");
            }
        }
        if ("Fax".equals(method) || "International fax".equals(method)) {
            if (GenericValidator.isBlankOrNull(fax)) {
                context.getValidationErrors().addGlobalError(new LocalizableError("org.ei.stripes.action.lindahall.LindaHallOrderFormAction.fax.valueNotPresent"));
                return new ForwardResolution("/WEB-INF/pages/customer/lindahall/orderform.jsp");
            }
        }
        if (method.startsWith("FedEx")) {
            if (GenericValidator.isBlankOrNull(fedex)) {
                context.getValidationErrors().addGlobalError(new LocalizableError("org.ei.stripes.action.lindahall.LindaHallOrderFormAction.fedex.valueNotPresent"));
                return new ForwardResolution("/WEB-INF/pages/customer/lindahall/orderform.jsp");
            }
        }

        return new ForwardResolution("/WEB-INF/pages/customer/lindahall/orderformconfirm.jsp");
    }

    /**
     * Handle emailing the order form
     * @return
     * @throws Exception
     */
    @HandlesEvent("submitedit")
    @Validate
    public Resolution submitedit() throws Exception {
        LhlUserInfo lhluserinfo = (LhlUserInfo) context.getRequest().getSession().getAttribute(LHL_SESSION_USERINFO);

        // Authentication present, get the document
        context.getRequest().setAttribute("lhldoc", getDocumentXML());
        return new ForwardResolution("/WEB-INF/pages/customer/lindahall/orderform.jsp");
    }

    /**
     * Handle emailing the order form
     * @return
     */
    @HandlesEvent("submitemail")
    @Validate
    public Resolution submitemail() {

        LhlUserInfo lhluserinfo = (LhlUserInfo) context.getRequest().getSession().getAttribute(LHL_SESSION_USERINFO);
        StringBuffer messageString;

        RuntimeProperties runtimeProps = EVProperties.getRuntimeProperties();
        String LHLTORecepients= runtimeProps.getProperty(RuntimeProperties.LHL_TO_RECIPIENTS);
        String LHLCCRecepients= runtimeProps.getProperty(RuntimeProperties.LHL_CC_RECIPIENTS);
        String LHLFROMRecepients= runtimeProps.getProperty(RuntimeProperties.LHL_FROM_RECIPIENTS);
        String LHLEmailSubject= runtimeProps.getProperty(RuntimeProperties.LHL_EMAIL_SUBJECT);

        UserSession usersession = context.getUserSession();

        lhluserinfo.setAccountNumber(usersession.getUser().getAccount().getAccountNumber());

        ContactInfo cInfo=new ContactInfo();
        cInfo.setUserFirstName(firstname);
        cInfo.setUserLastName(lastname);
        cInfo.setCompany(companyname);
        cInfo.setAddress1(address1);
        cInfo.setAddress2(address2);
        cInfo.setCity(city);
        cInfo.setState(state);
        cInfo.setCountry(country);
        cInfo.setZip(zip);
        cInfo.setPhoneNumber(phone);
        cInfo.setFaxNumber(fax);
        cInfo.setAccountNumber(accountnumber);

        try
        {
            String documentXML = getDocumentXML();

            // Code for updating the order_data table
            // Use the (possibly) updated contact information
            lhluserinfo.setContactInfo(cInfo);

            String orderNumber = Long.toString(System.currentTimeMillis());
            String methodvalue = "Email".equals(method) ? deliveryEmail : ("Ariel".equals(method) ? ariel : "");

            // Create Email message
            messageString=new StringBuffer();
            messageString.append("The following order ").append("(Order number: ").append(orderNumber).append(")").append(" has been sent to Linda Hall Library. A Linda Hall Library representative will contact you for confirmation of your order and for billing arrangements. To contact Linda Hall Library regarding your order, please call 1 (800) 662-1545 or email ei@lindahall.org").append("\n\n");

            // Transform XML document into display form for inclusion in Email and Database
            TransformerFactory tfactory = TransformerFactory.newInstance();
            String stylesheet = LindaHallOrderFormAction.class.getResource("/transform/lindahall/LindaHallAsciiEmail.xsl").toExternalForm();
            Transformer transformer = tfactory.newTransformer(new StreamSource(stylesheet));

            StringReader sr=new StringReader(documentXML);
            StringWriter sw=new StringWriter();
            transformer.transform(new StreamSource(sr),new StreamResult(sw));
            messageString.append(sw.toString());

            // send order information to database
            LindaHallBroker lhlBroker = new LindaHallBroker();
            if (!lhlBroker.sendOrderInfo(lhluserinfo, sw.toString(), attention, method, methodvalue, service, orderNumber, confirmationEmail)) {
                log4j.warn("Unable to record order info for order number: " + orderNumber);
            }

            messageString.append("\nDelivery method: ").append(method);
            if (!GenericValidator.isBlankOrNull(methodvalue)) messageString.append(", ( ").append( methodvalue ).append( " )");
            messageString.append("\nService level: ").append(service).append("\n");

            if(!GenericValidator.isBlankOrNull(accountnumber)) {
                messageString.append("\nFedEx account: ").append(accountnumber).append("\n");
            }

            messageString.append("Attention: ").append(attention).append("\n");
            messageString.append("Email: ").append(deliveryEmail).append("\n");
            messageString.append("Ship to: \n");
            messageString.append(firstname).append(" ").append(lastname).append("\n");
            messageString.append(companyname).append("\n");
            messageString.append(address1).append("\n");
            if(!GenericValidator.isBlankOrNull(address2)) {
                messageString.append("").append(address2).append("\n");
            }
            messageString.append(city).append(", ").append(state).append(" ").append(country).append(" ").append(zip).append("\n");
            messageString.append(fax).append("\n");
            messageString.append(phone).append("\n");
            messageString.append(confirmationEmail).append("\n");
            messageString.append("\nOrder submitted: ").append(StringUtil.getFormattedDate("EEE, dd MMM yyyy 'at' HH:mm:ss (z)")).append("\n");


            // send email to recipients and Linday Hall Library reference
            List<String> toAddress=new ArrayList<String>();
            List<String> ccAddress=new ArrayList<String>();
            if(GenericValidator.isBlankOrNull(LHLTORecepients)){
                LHLTORecepients = "ei@lindahall.org";
            }
            toAddress.add(LHLTORecepients);

            if(GenericValidator.isBlankOrNull(LHLCCRecepients)){
                LHLCCRecepients = "eicustomersupport@elsevier.com";
            }
            ccAddress.add(LHLCCRecepients);

            // jam - 5/12/2003 - Ariel "Address" is not an email
            // so do not send to supplied address
            if(!"Ariel".equalsIgnoreCase(method)) {
                // send email to delivery address, if email delivery is specified
                if(!GenericValidator.isBlankOrNull(deliveryEmail)) {
                    toAddress.add(deliveryEmail);
                }
            }

            // ALWAYS send to confirmation email address!!
            // send to given confirmation address
            if(!GenericValidator.isBlankOrNull(confirmationEmail)) {
                toAddress.add(confirmationEmail);
            }

            if(GenericValidator.isBlankOrNull(LHLFROMRecepients)){
                LHLFROMRecepients = "eicustomersupport@elsevier.com";
            }

            if(GenericValidator.isBlankOrNull(LHLEmailSubject)){
                LHLEmailSubject = "Engineering Village Linda Hall Library Document Request";
            }

            SESMessage sesmessage = new SESMessage();
            sesmessage.setFrom(SESMessage.DEFAULT_SENDER);
            sesmessage.setDestination(toAddress, ccAddress);
            sesmessage.setMessage(LHLEmailSubject, messageString.toString(),false);
            SESEmail.getInstance().send(sesmessage);
        }
        catch(Exception e)
        {
            log4j.error("Unable to send Order: " + e.getMessage());
            context.getValidationErrors().addGlobalError(new SimpleError("Unable to process request."));
            return new ForwardResolution("/WEB-INF/pages/customer/lindahall/orderform.jsp");
        }

        return new ForwardResolution("/WEB-INF/pages/customer/lindahall/emailconfirm.jsp");
    }


    @Override
    public Resolution handleValidationErrors(ValidationErrors arg0) throws Exception {
        context.getRequest().setAttribute("lhldoc", getDocumentXML());
        return null;
    }

    //
    // GETTERS/SETTERS
    //

    public String getFirstname() {
        return this.firstname;
    }
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    public String getLastname() {
        return this.lastname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public String getCompanyname() {
        return this.companyname;
    }
    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }
    public String getAddress1() {
        return this.address1;
    }
    public void setAddress1(String address1) {
        this.address1 = address1;
    }
    public String getAddress2() {
        return this.address2;
    }
    public void setAddress2(String address2) {
        this.address2 = address2;
    }
    public String getCity() {
        return this.city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getState() {
        return this.state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getCountry() {
        return this.country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getZip() {
        return this.zip;
    }
    public void setZip(String zip) {
        this.zip = zip;
    }
    public String getFax() {
        return this.fax;
    }
    public void setFax(String fax) {
        this.fax = fax;
    }
    public String getConfirmationEmail() {
        return this.confirmationEmail;
    }
    public void setConfirmationEmail(String confirmationEmail) {
        this.confirmationEmail = confirmationEmail;
    }
    public String getPhone() {
        return this.phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAriel() {
        return ariel;
    }

    public void setAriel(String ariel) {
        this.ariel = ariel;
    }

    public String getFedex() {
        return fedex;
    }

    public void setFedex(String fedex) {
        this.fedex = fedex;
    }

    public String getAttention() {
        return attention;
    }

    public void setAttention(String attention) {
        this.attention = attention;
    }

    public String getDeliveryEmail() {
        return this.deliveryEmail;
    }

    public void setDeliveryEmail(String deliveryEmail) {
        this.deliveryEmail = deliveryEmail;
    }

    public String getService() {
        return this.service;
    }

    public void setService(String servicelevel) {
        this.service = servicelevel;
    }

    public String getAccountnumber() {
        return this.accountnumber;
    }

    public void setAccountnumber(String accountnumber) {
        this.accountnumber = accountnumber;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

}
