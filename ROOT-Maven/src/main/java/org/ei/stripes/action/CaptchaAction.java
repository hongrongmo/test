package org.ei.stripes.action;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.SimpleError;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.config.EVProperties;
import org.ei.controller.HitCount;
import org.ei.controller.IPBlocker;
import org.ei.controller.IPBlocker.SessionRate;
import org.ei.domain.ISBN;
import org.ei.security.captcha.Config;
import org.ei.security.captcha.ISkewImage;
import org.ei.security.utils.Encrypter;
import org.ei.security.utils.SecureID;
import org.ei.session.UserSession;
import org.ei.stripes.EVActionBeanContext;
import org.ei.stripes.util.HttpRequestUtil;

@UrlBinding("/captcha/{$event}.url")
public class CaptchaAction extends EVActionBean {

	private final static Logger log4j = Logger.getLogger(CaptchaAction.class);

	public static final String CAPTCHA_DISPLAY_URL = "/captcha/display.url";
	public static final String CAPTCHA_VERIFY_URL = "/captcha/verify.url";
	public static final String CAPTCHA_IMAGE_URL = "/captcha/image.url";

	public static final String CAPTCHA_REQUEST_SECUREID = "captchaID";
	public static final String CAPTCHA_SESSION_HITCOUNT = "captchaHitCount";
	public static final String CAPTCHA_SESSION_SHOWN = "captchaShown";
	
	private static Pattern sp_pattern = Pattern.compile("\\s+");
	private static Pattern wildcard_pattern = Pattern.compile("(\\*|\\?)");
	private static Pattern number_pattern = Pattern.compile("\\d{6,}+");

    private static Encrypter _stringEncrypter = new Encrypter();

	private String imagetextenc;
	private String redirectenc;
	private String supportemail = "eicustomersupport@elsevier.com";	// TODO read this from config file...
	private String userentry;
	private String message;
	
	/**
	 * Default handler - displays the captcha verification box
	 * 
	 * @return Resolution
	 * @throws Exception 
	 */
	@DefaultHandler
	@DontValidate
	public Resolution display() throws Exception {
		
		HttpServletRequest request = context.getRequest();
		HttpSession session = request.getSession(false);
		
		boolean isGenericCaptchaBlock = false;
		
        if (session != null){
        	SessionRate sessionrate = (SessionRate) session.getAttribute(IPBlocker.SESSION_RATE_LIMITOR_KEY);
        	if(sessionrate != null){
        		isGenericCaptchaBlock  = sessionrate.isBlockWithCaptcha();
        	}
        }
        
        log4j.info("Displaying captcha image...");
        // Build random letters
		imagetextenc = buildRandomLetters();
        
        if(isGenericCaptchaBlock){
        	if (redirectenc == null) {
        		redirectenc = buildGenericRedirectEnc();
        	}
        }else{
    		setRoom(ROOM.search);
    	    if (redirectenc == null) {
    	    	// Encrypt the URL to return to upon successful Captcha verification
    	    	redirectenc = buildRedirectEnc();
    	    }
        }
        
		
		
		
		return new ForwardResolution("/WEB-INF/pages/customer/captcha.jsp");
	}	
	
	/**
	 * Captcha validator - checks the entered code and either returns the user
	 * back to the captcha page (no match) or fowards to search request.
	 * 
	 * @return Resolution
	 * @throws Exception 
	 */
	@HandlesEvent("verify")
	@DontValidate
	public Resolution verify() throws Exception {
		log4j.info("Verifying captcha image...");
		
		boolean failed = false;

    	if (userentry!=null && imagetextenc !=null) {
        	userentry = userentry.trim();
        	imagetextenc = imagetextenc.trim();

            String imagetextdec = new String(Base64.decodeBase64(imagetextenc));
            String imageidclear = _stringEncrypter.decrypt(imagetextdec);
            String secureID = imageidclear.substring(imageidclear.indexOf(".")+1, imageidclear.length());
            imageidclear = imageidclear.substring(0, Config.getPropertyInt(Config.MAX_NUMBER));

            if (!SecureID.validSecureID(secureID)) {
            	failed = true;
            }
            imageidclear = imageidclear.trim();
            if (!userentry.toUpperCase().equals(imageidclear.toUpperCase())) {
            	failed = true;
            }
        }
    	else {
    		failed = true;
    	}

    	if(failed) {
    		imagetextenc = buildRandomLetters();
    		context.getValidationErrors().addGlobalError(new SimpleError("Validation failed. Please try again."));
    		return new ForwardResolution("/WEB-INF/pages/customer/captcha.jsp");
    	}
    	else {
    		if(new String(Base64.decodeBase64(redirectenc)).equalsIgnoreCase("/home.url")){
    			
    			HttpServletRequest request = context.getRequest();
    			HttpSession session = request.getSession(false);
    			
    			if (session != null){
    	        	SessionRate sessionrate = (SessionRate) session.getAttribute(IPBlocker.SESSION_RATE_LIMITOR_KEY);
    	        	if(sessionrate != null){
    	        		sessionrate.reset(true);
    	        		session.setAttribute(IPBlocker.SESSION_RATE_LIMITOR_KEY, sessionrate);
    	        	}
    	        }
    			return new RedirectResolution(new String(Base64.decodeBase64(redirectenc)) + "?" + CAPTCHA_REQUEST_SECUREID + "=" + SecureID.getSecureID(60000L));
    		}else{
    			setRoom(ROOM.search);
    			return new RedirectResolution(new String(Base64.decodeBase64(redirectenc)) + "&" + CAPTCHA_REQUEST_SECUREID + "=" + SecureID.getSecureID(60000L));
    		}
    		
    	}


	}

	/**
	 * Captcha image - builds the image for the captcha
	 * 
	 * @return Resolution
	 * @throws ServletException 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@HandlesEvent("image")
	@DontValidate
	public Resolution image() throws ServletException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		HttpServletResponse response = context.getResponse();
        response.setContentType("image/jpeg");

        if (imagetextenc == null) {
            ServletException se = new ServletException("Invalid Image ID");
            throw se;
        }
        if (imagetextenc.length() < Config.getPropertyInt(Config.MAX_NUMBER)) {
            ServletException se = new ServletException("Invalid Image ID Length");
            throw se;
        }
        imagetextenc = imagetextenc.trim();
        String imagetextclear = _stringEncrypter.decrypt(new String(Base64.decodeBase64(imagetextenc)));
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        ISkewImage skewImage = (ISkewImage) cl.loadClass("org.ei.security.captcha.SkewImageSimple").newInstance();
        BufferedImage bufferedImage = skewImage.skewImage(imagetextclear.substring(0, imagetextclear.indexOf(".")));
        ImageIO.write(bufferedImage, "jpeg", response.getOutputStream());
		
        return null;
	}

	/**
	 * Builds and encrypts the redirect URL for after captcha verification
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private String buildRedirectEnc() throws UnsupportedEncodingException {
		HttpServletRequest request = context.getRequest();
		StringBuffer redirect  = new StringBuffer("/search/submit.url?");
		
		Enumeration<String> e = request.getParameterNames();
    	while(e.hasMoreElements())
		{
			String name = e.nextElement();
			String value = "";
			if(name.equalsIgnoreCase("database"))
			{
				int dbvalue = 0;
				String[] multiValue = request.getParameterValues(name);	    				
				for(int i=0;i<multiValue.length; i++)
				{	    					
					dbvalue += Integer.parseInt(multiValue[i]);
				}
				value = Integer.toString(dbvalue);
			}
			else
			{
				value = (String) request.getParameter(name);
			}	    				    				    		
			redirect.append(name + "=" + URLEncoder.encode(value, "UTF-8"));
			if (e.hasMoreElements()) redirect.append("&");

		}
    	return new String(Base64.encodeBase64(redirect.toString().getBytes()));
	
	}
	
	private String buildGenericRedirectEnc() {
		StringBuffer redirect  = new StringBuffer("/home.url");
		return new String(Base64.encodeBase64(redirect.toString().getBytes()));
	}
	
	private String buildRandomLetters() throws Exception {
		String randomLetters = new String("");
	    for (int i = 0; i < Config.getPropertyInt(Config.MAX_NUMBER); i++) {
	        randomLetters += (char) (65 + (Math.random() * 24));
	    }
	    randomLetters = randomLetters.replaceAll("I","X");
	    randomLetters = randomLetters.replaceAll("Q","Z");
	    // Make Captcha from random letters + secure ID string
	    String imagetextclear = randomLetters + "." + SecureID.getSecureID(120000L);
	    
	    // Encrypt it all 
	    return new String(Base64.encodeBase64(_stringEncrypter.encrypt(imagetextclear).getBytes()));
	}
	
	/**
	 * This method looks into the current request and checks for a few things:
	 * 1) Is this a search request 
	 * 2) Does the search contain a wildcarded accession number 
	 * 3) OR the request may be the captcha ID coming back in
	 * 
	 * If this is a wildcarded accession number search OR customer has exceeded the
	 * allowed number of non-wildcarded accession number searches then forward to a 
	 * captcha page.
	 * 
	 * @param request
	 * @param usess
	 * 
	 * @throws Exception
	 * 
	 */
	public static Resolution handleCaptcha(HttpServletRequest request, UserSession usess) throws Exception {
		
		HttpSession session = request.getSession(false);
		if (session == null) {
		    log4j.error("No session object available!");
		    return null;
		}
		String ip = HttpRequestUtil.getIP(request);
		String custID = usess.getUser().getCustomerID();
		
		// TODO - replace with Fence!
		Map<String, String> custBypass = EVProperties.getInstance().getCustBypass();
		Map<String, String> ipBypass = EVProperties.getInstance().getIpBypass();
		
		// Only do check if:
		// 1) NOT bypassing based on customer ID or IP
		// 2) NOT an openXML or openRSS request
		if (!custBypass.containsKey(custID) && !ipBypass.containsKey(ip)
			&& !EVActionBeanContext.XML_CID.equals(request.getParameter("CID"))
			&& !EVActionBeanContext.RSS_CID.equals(request.getParameter("CID"))) {
			
			// If captchaID is present then it is coming from a successful
			// captcha validation.
			String captchaID = request.getParameter(CAPTCHA_REQUEST_SECUREID);
			if (captchaID != null) {
				if (SecureID.validSecureID(captchaID)) {
					// Remove HitCount object from session to restart captcha
		    		session.removeAttribute(CAPTCHA_SESSION_HITCOUNT);
		    		return null;
				} else {
					return new ForwardResolution("/servlet/SecurityService");
				}
			}

			// Check to see if the search has multiple
			String search = request.getParameter("searchWord1");
			HitCount hitcount = null;
			if (search != null && captchaID == null) {
				search = search.toLowerCase();
				search = search.replace('(', ' ');
				search = search.replace(')', ' ');
				search = search.replace('"', ' ');
				search = search.replace('{', ' ');
				search = search.replace('}', ' ');
				String queryWords[] = sp_pattern.split(search);
				int numAccessionPatternMatches = 0;
				boolean captchaNow = false;
				for (int i = 0; i < queryWords.length; i++) {
					boolean wildcard = false;
					String word = queryWords[i];

					Matcher wildcard_matcher = wildcard_pattern.matcher(word);
					if (wildcard_matcher.find()) {
						word = wildcard_matcher.replaceAll("");
						wildcard = true;
					}

					Matcher number_matcher = number_pattern.matcher(word);
					if (number_matcher.matches() && !isbn(word)) {
						numAccessionPatternMatches++;
						if (wildcard) {
							captchaNow = true;
							break;
						}
					}
				}

				if (numAccessionPatternMatches > 0) {
					String hitcountvalue = (String) session.getAttribute(CAPTCHA_SESSION_HITCOUNT);
					if (!GenericValidator.isBlankOrNull(hitcountvalue)) {
						hitcount = new HitCount(CAPTCHA_SESSION_HITCOUNT, hitcountvalue);
					} else {
						hitcount = new HitCount(CAPTCHA_SESSION_HITCOUNT);
					}

					if (captchaNow || numAccessionPatternMatches >= 7) {
						hitcount.setBlocked(true);
					}

					session.setAttribute(hitcount.getKey(),hitcount.toString());
					if (hitcount.getBlocked()) {
						/*
						 * User is blocked so forward them to the Captcha page.
						 */
						log4j.warn("Captcha display for IP:" + ip + 
								", searchWord1: " + search +
								", accession pattern matches: " + numAccessionPatternMatches + 
								", captcha now: " + captchaNow);
						return new ForwardResolution("/captcha/display.url");
					}
				}
			}
		}
		return null;
	}

	/**
	 * Utility method to convert word to ISBN value
	 * 
	 * @param word
	 * @return
	 */
	private static boolean isbn(String word) {
		int length = word.length();
		if (length != 13 && length != 10) {
			return false;
		} else if (length == 13) {
			if (word.indexOf("978") == 0) {
				return true;
			} else {
				return false;
			}
		} else if (length == 10) {
			char checkDigit = ISBN.getISBN10CheckDigit(word);
			if (word.charAt(9) == checkDigit) {
				return true;
			} else {
				return false;
			}
		}

		return false;
	}

	//
	//
	// GETTERS/SETTERS
	//
	//
	public String getImagetextenc() {
		return imagetextenc;
	}

	public void setImagetextenc(String imagetextenc) {
		this.imagetextenc = imagetextenc;
	}

	public String getRedirectenc() {
		return redirectenc;
	}

	public void setRedirectenc(String redirenc) {
		this.redirectenc = redirenc;
	}

	public String getSupportemail() {
		return supportemail;
	}

	public void setSupportemail(String supportemail) {
		this.supportemail = supportemail;
	}

	public String getUserentry() {
		return userentry;
	}

	public void setUserentry(String userentry) {
		this.userentry = userentry;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
