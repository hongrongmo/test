<%@ page language="java" %>
<%@ page session="false" %>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="org.ei.trial.*"%>
<%@ page import="org.ei.email.*"%>
<%@ page errorPage="/error/errorPage.jsp"%>
<%

    out.write("<PAGE>");
    String firstname = request.getParameter("firstname");
    String lastname = request.getParameter("lastname");
    String jobtitle = request.getParameter("jobtitle");
    String company = request.getParameter("company");
    String website = request.getParameter("website");
    String address1 = request.getParameter("address1");
    String address2 = request.getParameter("address2");
    String city = request.getParameter("city");
    String state = request.getParameter("state");
    String country = request.getParameter("country");
    String zip = request.getParameter("zip");
    String email = request.getParameter("email");
    String phone = request.getParameter("phone");
    String howHear = request.getParameter("hearus");
    String explain = request.getParameter("explanation");
    String[] product = request.getParameterValues("prodName");
    String bymail = request.getParameter("bymail")==null?"no":"yes";
    String byemail = request.getParameter("byemail")==null?"no":"yes";
    String refurl = request.getParameter("REF-URL");
    TrialUser trialUser = new TrialUser();
    TrialUserBroker uBroker= new TrialUserBroker();
    trialUser.setFirstName(firstname);
    trialUser.setLastName(lastname);
    trialUser.setJobTitle(jobtitle);
    trialUser.setCompany(company);
    if((website!=null)&&(website.length()>0))
    {
        trialUser.setWebSite(website);
    }
    trialUser.setAddress1(address1);
    if((address2!=null)&&(address2.length()>0))
    {
        trialUser.setAddress2(address2);
    }
    trialUser.setCity(city);
    trialUser.setState(state);
    trialUser.setCountry(country);
    trialUser.setZip(zip);
    trialUser.setEmail(email);
    trialUser.setPhone(phone);
    trialUser.setHowHear(howHear);
    if((explain!=null)&&(explain.length()>0))
    {
        trialUser.setHowHearExplained(explain);
    }
    StringBuffer st=new StringBuffer();
		if(product != null)
		{
	    for(int i=0;i<product.length;i++)
	    {
	        if(product[i].length()>0)
	        {
	            if(i<product.length-1)
	            {
	                st.append(product[i]+" | ");
	            }
	            else
	            {
	                st.append(product[i]);
	            }
	        }
	    }
		}
    trialUser.setProduct(st.toString());
    trialUser.setByMail(bymail);
    trialUser.setByEmail(byemail);
    trialUser.setReferringURL(refurl);
    uBroker.addTrialUser(trialUser);

    //sending email

    EMail mail = EMail.getInstance();

    EIMessage msg = new EIMessage();
    StringBuffer sb=new StringBuffer();
    sb.append("Dear "+ trialUser.getFirstName() + " " + trialUser.getLastName() +",");
    sb.append("\n\n");
    sb.append("Thank you for your interest in EI products. You will be contacted by an Elsevier Engineering Information representative shortly to establish a trial of EI products. ");
    sb.append("\n\n\n\n");
    sb.append("Ei Customer Support");
    /*
    msg.setMessageBody(sb.toString());
    msg.setSubject("EI products Free 30-day Trial");
    msg.setSender(EIMessage.DEFAULT_SENDER);
    msg.setFrom(EIMessage.DEFAULT_SENDER);
    msg.addTORecepient(trialUser.getEmail());
    mail.sendMessage(msg);
    */
    msg = new EIMessage();
    sb=new StringBuffer();
    sb.append("First Name: "+ trialUser.getFirstName());
    sb.append("\n");
    sb.append("Last Name: "+ trialUser.getLastName());
    sb.append("\n");
    sb.append("Job Title: "+trialUser.getJobTitle());
    sb.append("\n");
    sb.append("Company: "+ trialUser.getCompany());
    sb.append("\n");
    sb.append("Address1: "+ trialUser.getAddress1());
    sb.append("\n");
    sb.append("Address2: "+ trialUser.getAddress2());
    sb.append("\n");
    sb.append("City: "+ trialUser.getCity());
    sb.append("\n");
    sb.append("State: "+ trialUser.getState());
    sb.append("\n");
    sb.append("Country: "+ trialUser.getCountry());
    sb.append("\n");
    sb.append("Zip: "+ trialUser.getZip());
    sb.append("\n");
    sb.append("Phone: "+ trialUser.getPhone());
    sb.append("\n");
    sb.append("Email: "+ trialUser.getEmail());
    sb.append("\n");
    sb.append("Company website: "+ trialUser.getWebSite());
    sb.append("\n");
    sb.append("send EI product by mail: "+trialUser.getByMail());
    sb.append("\n");
    sb.append("send EI's bi-monthly electronic news letter: "+trialUser.getByEmail());
    sb.append("\n");
    sb.append("Interested product: "+trialUser.getProduct());
    sb.append("\n");
    sb.append("Request date: "+(Calendar.getInstance().getTime()).toString());
    sb.append("\n");
    sb.append("How Hear: "+ trialUser.getHowHear());
    sb.append("\n");
    sb.append("How Hear Explained: "+ trialUser.getHowHearExplained());
    sb.append("\n");
    sb.append("Referring URL: "+ trialUser.getReferringURL());
    /*
    msg.setMessageBody(sb.toString());
    msg.setSubject("New EI product Trial Request");
    msg.setSender("eicustomersupport@elsevier.com");
    //msg.addTORecepient("h.mo@elsevier.com");
    //msg.addTORecepient("l.fiszer@elsevier.com");
    msg.setSender(EIMessage.DEFAULT_SENDER);
    msg.setFrom(EIMessage.DEFAULT_SENDER);
    msg.addTORecepient(EIMessage.DEFAULT_SENDER);
    mail.sendMessage(msg);
    */
    //finish send email

    out.write("<TRIAL>SUCCESS</TRIAL></PAGE>");
    //System.out.println("Date= "+(Calendar.getInstance().getTime()).toString());

%>
