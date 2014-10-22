package org.ei.service.cars.Impl;

import org.apache.log4j.Logger;



public class ANERequestProcessor {
	
	private final static Logger log4j = Logger.getLogger(ANERequestProcessor.class);

	private static String TEMPLATE_UPDATE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.S";
	/**
	 * This method will be invoked from web to make REST call.
	 * Validate CARSRequest object and invoke Rest service client for further processing
	 * 
	 * @param carsReq the cars request coming from web
	 * @param httpReq the http servlet request
	 * @param httpResp the http servlet response
	 * @throws CARSResponseParseException 
	 * @throws CARSServiceException if any service fails during REST call
	 */
	
	/*public CARSResponse processRequest(CARSRequest carsReq,HttpServletRequest httpReq, HttpServletResponse httpResp)
			throws ServiceException{
		
		//EVSessionHelper.setHttpSession(httpReq.getSession());
		String nextRequestUri=null;

		if (null == carsReq || carsReq.isEmptyRequest()) {
			throw new ServiceException( "CARS_INVALID_REQUEST_ERROR");
		}
		
		if (!carsReq.isSiteIdentifierPresent()) {
			throw new ServiceException("CARS_SITEIDENTIFIER_NOT_PRESENT");
		}
		
		if (!carsReq.isPlatformCodePresent()) {
			throw new ServiceException("CARS_PLATFORMCODE_NOT_PRESENT");
		}
		
		
		CARSResponse response = new CARSResponse();
		if(isInvokeCARSRestCall(carsReq)) {
			if (GenericValidator.isBlankOrNull(carsReq.getRequestURI())) {
				throw new IllegalStateException("CARS request URI is emtpy!");
			}
			
			List<String> mimeRespData = CARSRestRequestProcessor.invoke(carsReq, httpReq);
			mimeRespData = reAthenticatErrorResponse(carsReq, mimeRespData, httpReq);
			
			if (null == mimeRespData || mimeRespData.isEmpty() || mimeRespData.size() <= 0) {
				log4j.debug("User authentication completed with  "+ "[null response payload]");
				throw new ServiceException("CARS_EMPTY_RESPONSE_ERROR", null);
			}

			processResponse(mimeRespData, response, carsReq);
			nextRequestUri = response.getNextRequestURI();

			if (StringUtils.isNotBlank(nextRequestUri)) {
				CARSRequestImpl nextCARSRequest = new CARSRequestImpl(nextRequestUri);
				nextCARSRequest.addRestRequestParameter(RESTRequestParameters.DYNAMIC_REQUEST_URI,nextRequestUri);
				response = processRequest(nextCARSRequest, httpReq, httpResp);
			}
			
			setCarsResponsePageTypeInSession(response);
			
			if (HttpMethod.DELETE.equals(carsReq.getHTTPMethod())) {
				//processLogOutFlow(response, mimeRespData.get(0), carsReq,httpReq, httpResp);
			}
			
			CARSCommonUtil.handleCookie(httpReq, httpResp, response.getCarsCookie());
		}else{
			response.setCarsCalled(false);
		}
		
		return response;
	}

	
	private boolean isInvokeCARSRestCall(CARSRequest carsReq) {
		
		//boolean ssoDisabledFlag = CARSConfigVariables.getConstantAsBoolean(CARSConfigVariables.DISABLE_SSO_AUTH);
		boolean ssoDisabledFlag = false;
		
		if (CARSCommonUtil.isUserAlreadyExistsInSession() && !ssoDisabledFlag  && isRequestURINullInCarReq(carsReq) && !carsReq.isSSOAuthenticationReq()) {
			//return false;
			return true;
		}
		
		return true;
	}

	private boolean isRequestURINullInCarReq(CARSRequest carsReq) {
		return null == carsReq.getRequestURI();
	}
    
	
	private void setCarsResponsePageTypeInSession(CARSResponse response) {

		if (StringUtils.isNotBlank(getPageType(response)) && PageType.LOGIN.toString().equals(getPageType(response))) {
			EVSessionHelper.setSessionAttribute(EVSessionKeys.HEADER_CONTENT, getPageHtmlContent(response));
		}
	}

	private String getPageHtmlContent(CARSResponse response) {
		return response.getPageContent();
	}

	private String getPageType(CARSResponse response) {
		return response.getPageType();
	}

	
	private List<String> reAthenticatErrorResponse(CARSRequest request, List<String> mimeRespData, HttpServletRequest httprequest) 
			throws ServiceException {
		
		String mimeResp1 = fetchMIMERespWithUserInfo(mimeRespData);
		
		if (null != mimeResp1 && mimeResp1.length() > 0) {
			String errType= null;
			String errFieldName= null;
			String status= null;
			try {
				errType = XMLUtil.fetchXPathValAsString(mimeResp1,XPathEnum.ERROR_TYPE.value());
				errFieldName = XMLUtil.fetchXPathValAsString(mimeResp1,XPathEnum.ERROR_FIELD_NAME.value());
				status = XMLUtil.fetchXPathValAsString(mimeResp1,XPathEnum.STATUS_CODE.value());
			} catch (CARSResponseParseException e) {
				throw new ServiceException("error in repsone parsing for "+e.getMessage(), e);
			}
		
			if (null != status && status.length() > 0) {
				CARSResponseStatus statusCode = CARSResponseStatus.valueOf(status);
				
				if (!CARSResponseStatus.OK.equals(statusCode)) {
					if (null != errType && errType.length() > 0) {

						log4j.info("ErrorType->" + errType + "; ErrorFieldName->" + errFieldName + "; StatusCode->" + status);
						
						if ("SSO_SESSION_ID_INVALIDATED_LOG_OFF".equals(errType)) {
							mimeRespData = resetCARSRequest(request, false, httprequest);
						} else if("MISMATCHED_SSO_SESSION_ID".equals(errType)) {
							mimeRespData = resetCARSRequest(request, true, httprequest);
						}
						if (CARSResponseStatus.VALIDATION_ERROR.equals(statusCode) && "INVALID".equalsIgnoreCase(errType) 
								&& "AUTH_TOKEN".equalsIgnoreCase(errFieldName)) {
							mimeRespData = resetCARSRequest(request, false,httprequest);
						}
					}
				}
			}
		}
		return mimeRespData;
	}


	private List<String> resetCARSRequest(CARSRequest request, boolean isSsoKeyReq,HttpServletRequest httprequest) throws ServiceException{
		
		List<String> mimeRespData;
		CARSRequestImpl carsRequest = (CARSRequestImpl) request;
		
		carsRequest.addRestRequestParameter(RESTRequestParameters.AUTH_TYPE,AuthenticationType.IP.toString());
		carsRequest.addRestRequestParameter(RESTRequestParameters.AUTH_TOKEN, null);
		if (!isSsoKeyReq) {
			carsRequest.addRestRequestParameter(RESTRequestParameters.SSO_KEY, null);
		}
		carsRequest.addRestRequestParameter(RESTRequestParameters.CARS_COOKIE,null);
		carsRequest.addRestRequestParameter(RESTRequestParameters.ASSOCIATION_TYPE, null);
		carsRequest.addRestRequestParameter(RESTRequestParameters.ANON_TYPE, null);

		mimeRespData = CARSRestRequestProcessor.invoke(request,httprequest);

		//SessionDataHelper.setObject(CARSAuthenticationConstants.SSO_CSAS_ERROR.value(), SessionDataHelper.SCOPE_SESSION, Boolean.TRUE);

		return mimeRespData;
	}
	
	
	*//**
	 * This method is responsible for following task - Fetch MIME response and
	 * create user object if user info is available - Populate required
	 * informations to CARSResponse from MIME using xpath - Prepare/build other
	 * response data's
	 * 
	 * @param mimeRespList - the list of MIME returned from CS
	 * @param response -
	 *            the response to which informations will be populated
	 * @param request - the ICARS Request object
	 * @throws CARSResponseParseException 
	 * @throws CARSServiceException -
	 *             exception to be thrown if any service fails during processing
	 *             data.
	 *//*
	@SuppressWarnings("null")
	public void processResponse(List<String> mimeRespList, CARSResponse response, CARSRequest request) throws ServiceException {
		
		if (!(null == mimeRespList && mimeRespList.isEmpty())) {
			//set the last access time in session so that it will be used for 
			//sso revalidation after 10 minutes
			
			if ( request.isSSOAuthenticationReq()){
				response.setSSOAuthResponse(true);
			}

			EVSessionHelper.setSessionAttribute(EVSessionKeys.CARS_LAST_SUCESS_ACCESS_TIME, Long.valueOf(System.currentTimeMillis()));
			
			String mimeResp1 = fetchMIMERespWithUserInfo(mimeRespList);
			String statusCode;
			
			try {
				statusCode = XMLUtil.fetchXPathValAsString(mimeResp1, XPathEnum.STATUS_CODE.value());
			} catch (CARSResponseParseException e) {
				throw new ServiceException("error in repsone parsing for STATUS_CODE: ", e);
			}
			
			if(CARSResponseStatus.AUTHENTICATION_ERROR.toString().equals(statusCode) && 
					AuthenticationType.IP.toString().equalsIgnoreCase((String) request.getRestRequestParams().get(RESTRequestParameters.AUTH_TYPE))){
				EVSessionHelper.setSessionAttribute(EVSessionKeys.IP_AUTH_STATUS, CARSResponseStatus.AUTHENTICATION_ERROR);
				
			}

			if (CARSResponseStatus.OK.toString().equals(statusCode)) {
				response.setStatusSuccess(true);
			}
                
        	if (!HttpMethod.DELETE.equals(request.getHTTPMethod())) {
	        	try {
					prepareResponseData(mimeRespList, response);
					processUserData(response, mimeResp1, statusCode);
				} catch (CARSResponseParseException e) {
					throw new ServiceException(e.getMessage(), e);
				}
        	}
        	
			responseTransformation(mimeRespList, response);
		}
	}


	private void processUserData(CARSResponse response, String mimeResp1, String statusCode) throws ServiceException{

		if (StringUtils.isNotBlank(statusCode)) {
			
			if (CARSResponseStatus.OK.toString().equals(statusCode) || CARSResponseStatus.AUTHENTICATION_ERROR.toString().equals(statusCode)) {
				try{
				if (isUserInfoAvailableInResponse(mimeResp1)) {
					createOrUpdateWebUser(response, mimeResp1, statusCode);
				}
				} catch (CARSResponseParseException e) {
					log4j.info("user data prcoessing from rest response falied:",e);
					throw new ServiceException("user data prcoessing from rest response falied:"+e.getMessage(), e);
				}
			} else {
				try {
					processValidErrorData(response, statusCode,mimeResp1);
				} catch (CARSResponseParseException e) {
					throw new ServiceException("error data prcoessing from rest response falied:"+e.getMessage(), e);				}
			}
		}
		
	}

	private boolean isUserInfoAvailableInResponse(String mimeResp1)throws CARSResponseParseException {
		return CARSCommonUtil.isUserInfoAvailable(mimeResp1);
	}

	*//**
	 * This method handles the logic to set appropriate error message to cars response
	 * Here, we expect CARS_LOGIN template to be set to CARSResponse
	 * 
	 * @param respImpl the cars response to which values will be set
	 * @param statusCode the cars response status
	 * @param mimeResp1 the cars mime response with error info
	 * @throws CARSResponseParseException 
	 *//*
	private void processValidErrorData(CARSResponse resp, String status, String mimeResp1) throws CARSResponseParseException {
		
		String userAnonymity = XMLUtil.fetchXPathValAsString(mimeResp1,XPathEnum.USER_ANONYMITY.value());
		String errType = XMLUtil.fetchXPathValAsString(mimeResp1,XPathEnum.ERROR_TYPE.value());
		String errFieldName = XMLUtil.fetchXPathValAsString(mimeResp1,XPathEnum.ERROR_FIELD_NAME.value());
		EVWebUser webUser = null;
		
		if (CARSCommonUtil.isUserAlreadyExistsInSession()) {
			webUser = CARSCommonUtil.getExistingUserFromSession();
			if (CARSCommonUtil.isExistingUserLoggedIn(webUser) && !CARSCommonUtil.isValidUserInResponse(userAnonymity)) {
				//CARSCommonUtil.setValueInTransaction(CARSStringConstants.USER_LOGGED_OUT_BY_SSO.value(), CARSStringConstants.TRUE.value());
				webUser = null;
			} else if(CARSResponseStatus.AUTHENTICATION_ERROR.toString().
					equals(status)) {
				webUser = null;//reset the user to null
			} else {
				StringBuffer sbErrorCode = new StringBuffer(25);
				sbErrorCode.append(status);
				sbErrorCode.append(CARSStringConstants.UNDERSCORE.value());
				sbErrorCode.append(errType);
				sbErrorCode.append(CARSStringConstants.UNDERSCORE.value());
				sbErrorCode.append(errFieldName);

				resp.setErrorCode(sbErrorCode.toString());

			}
		}
	}


	*//**
	 * This method handles the logic to do XSL transformation by passing 
	 * the proper MIME and send back either html content which will be displayed in Web
	 * or content which will be sent as email to the user.
	 * - Get the MIME2 from response list as it will be passed for XSL transformation
	 * - Check for the page type, and make transformation for all pages except for 
	 * SHIBBOLETH and ATHENS
	 * 
	 * (Note: Always we get CARS MIMES in a given order. 
	 * The second MIME is used for XSL transformation
	 * So if the MIME2 is not present than no need to go for transformation.)
	 * 
	 * @param mimeRespList the MIME response list
	 * @param response the response holding all required data
	 * @throws CARSResponseParseException 
	 *//*
	private void responseTransformation(List<String> mimeRespList,CARSResponse response) throws ServiceException {
		String mimeResp2 = null;
		String statusCode =null;
		if (null != mimeRespList && mimeRespList.size() > 1) {
			
			mimeResp2 = mimeRespList.get(1);
		}
		if (StringUtils.isNotBlank(mimeResp2)) {
			String pageType = getPageType(response);
			if (StringUtils.isNotBlank(pageType)) {
				
				if (!(PageType.REDIRECT.toString().equals(pageType))) {
					responseXslTransformation(response, mimeResp2, pageType);
				}
			} else {
				log4j.info("XSL Transformation not required.");
			}
		}
		
		if (mimeRespList.size() > 3) {
            // This is an exception scenario in registration process, 
			// Where we get 4 MIMES,
			// first 2 is for email and second 2 is for successful page.
			// After processing the first 2 in above condition, 
			// removing the same so that next 2 mimes will be processed for 
			// showing registration successful page
            mimeRespList.remove(0);
            mimeRespList.remove(0);
            try {
				prepareResponseData(mimeRespList, response);
			} catch (CARSResponseParseException e) {
				throw new ServiceException(e.getMessage(), e);
			}
            try {
				statusCode = XMLUtil.fetchXPathValAsString(mimeRespList.get(0), XPathEnum.STATUS_CODE.value());
			} catch (CARSResponseParseException e) {
				throw new ServiceException("error in parsing STATUS_CODE in response", e);
			}

            processUserData(response, mimeRespList.get(0), statusCode);
            responseTransformation(mimeRespList, response);
		}
	}

	*//**
	 * This method handles the logic to 
	 * - Fetch the template from cache (if not latest then get from cs db)
	 * - Build/Create a XML using XSLT to give as input for XSL transformation
	 * - Transform CARS template XSL to html content 
	 * - If the page type is EMAIL then send the content to mail receiver, 
	 * 		otherwise the returned content to response 
	 * 
	 * @param response the response object
	 * @param mimeResp the mime response for building xml
	 * @param pageType the page type returned from cars
	 *//*
	private void responseXslTransformation(CARSResponse response, String mimeResp, String pageType) throws ServiceException {

		String xmlToTransform = null;
		String transformedHtml;

		Templates templates = getTemplate(response);
		
		if (null != templates && null != mimeResp) {
			
			String templateName = response.getTemplateName();
			
			String xslPath = CARSStringConstants.XSL_PATH.value();
			Map<String, String> textZoneMap = response.getTextZoneMap();
			String textZone = generateTextZone(textZoneMap);

			try {
				xmlToTransform = XSLTransformer.buildXMLForTransformation(mimeResp, templateName, xslPath, false, textZone);
				transformedHtml = transform(templateName, xmlToTransform, templates, 1);
				
			} catch (XSLTTransformationException e) {
				throw new ServiceException("resp xml tranformation failed.", e);
			}
			
			if (PageType.EMAIL.toString().equals(pageType)) {
				response.setEmailContent(transformedHtml);
					// invoke to send mail
				//sendConfirmationMail(response);
			} else {
				response.setPageContent(transformedHtml);
			}
		}
	}

	
	@SuppressWarnings("rawtypes")
	private String generateTextZone(Map<String, String> textZoneMap) {
		String textZone = null;
		StringBuilder textZoneStr = new StringBuilder();
		if(textZoneMap != null && textZoneMap.size()>0) {
			Iterator it = textZoneMap.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        textZoneStr.append(CARSStringConstants.LEFT_OPEN_TAG.value());
		        textZoneStr.append(pairs.getKey());
		        textZoneStr.append(CARSStringConstants.RIGHT_CLOSE_TAG.value());
		        textZoneStr.append(pairs.getValue());
		        textZoneStr.append(CARSStringConstants.LEFT_CLOSE_TAG.value());
		        textZoneStr.append(pairs.getKey());
		        textZoneStr.append(CARSStringConstants.RIGHT_CLOSE_TAG.value());
		        it.remove(); 
		    }
		    if(textZoneStr.length()>0){
		    	textZone=textZoneStr.toString();
		    }
		}
		return textZone;
	}
	
	private void createOrUpdateWebUser(CARSResponse response, String mimeResp1, String status) throws CARSResponseParseException  {
		
		EVWebUser webUser=null;
		if (CARSResponseStatus.OK.toString().equals(status)) {
			response.setStatusSuccess(true);
		}
		
		if (CARSCommonUtil.isUserAlreadyExistsInSession()) {
			webUser= updateWebUser(mimeResp1, status);
		} else {
			    webUser= new EVWebUser();
				UserDataProcessor.processUserData(webUser,mimeResp1);
		}

		if(CARSTemplateNames.CARS_CHANGE_PASSWORD_SUCCESSFUL.toString().equalsIgnoreCase(response.getTemplateName())){
			webUser.setPasswordResetFlagEnabled(false);
		}
		
		try {
			UserFencesHandler.processUserFences(webUser);
		} catch (FatalDataException e) {
			throw new CARSResponseParseException("Error processing in USER's fences..",e);
		}
		EVSessionHelper.setSessionAttribute(EVSessionKeys.USER_OBJECT, webUser);
		response.setWebUser(webUser);
	}


	private EVWebUser updateWebUser(String mimeResp1, String status) throws CARSResponseParseException {
		
		String userAnonymity;
		try {
			userAnonymity = XMLUtil.fetchXPathValAsString(mimeResp1,XPathEnum.USER_ANONYMITY.value());
		} catch (CARSResponseParseException e) {
			log4j.info("respose parsing falied for userAnonymity info ", e);
			throw new CARSResponseParseException("respose parsing falied for userAnonymity info", e);
		}
		
		EVWebUser webUser = CARSCommonUtil.getExistingUserFromSession();
		if (CARSCommonUtil.isExistingUserLoggedIn(webUser) && !CARSCommonUtil.isValidUserInResponse(userAnonymity)) {
			// set SSO logout to true
			CARSCommonUtil.setValueInTransaction(
					CARSStringConstants.USER_LOGGED_OUT_BY_SSO.value(), 
					CARSStringConstants.TRUE.value());
			
			if (CARSResponseStatus.AUTHENTICATION_ERROR.toString().equals(status)) {
				webUser = null;
			}
		}
		try {
			if (null != webUser) {
					UserDataProcessor.processUserData(webUser, mimeResp1);
			} else {
				UserDataProcessor.processUserData(new EVWebUser(), mimeResp1);
			}
			} catch (CARSResponseParseException e) {
				log4j.info("cars respose parsing falied for user data"+e.getMessage(), e);
				throw new CARSResponseParseException("cars respose parsing falied for user data"+e.getMessage(), e);
			}
		
		return webUser;
	}

	
	private static CARSResponse processDisplayModel(String mimeResp,CARSResponse response) throws ServiceException {
 		
		String pageTitle=null;
		String platformName=null;
		try {
			response.setPageType(PageType.valueOf(XMLUtil.fetchXPathValAsString(mimeResp,XPathEnum.PAGE_TYPE.value())));
			response.setTemplateName(XMLUtil.fetchXPathValAsString(mimeResp,XPathEnum.TEMPLATE_NS2.value()));
			platformName = CARSConfigVariables.getConstantAsString(CARSConfigVariables.APP_PRODUCT_NAME);
			pageTitle =XMLUtil.fetchXPathValAsString(mimeResp,XPathEnum.PAGE_TITLE.value());
		
		if (StringUtils.isNotBlank(pageTitle)) {
			if (null != platformName && !"".equals(platformName.trim())) {
				pageTitle = platformName + " - " + pageTitle;
			}
		}
		response.setPageTitle(pageTitle);
		response.setTemplateUpdateDate(XMLUtil.fetchXPathValAsString(mimeResp, XPathEnum.TEMPLATE_UPDATE_DATE.value()));
		response.setFlowComplete(XMLUtil.fetchXPathValAsBoolean(mimeResp,XPathEnum.FLOW_COMPLETE_TOKEN.value()));
		response.setCarsCookie(XMLUtil.fetchXPathValAsString(mimeResp,XPathEnum.CARS_COOKIE.value()));
		response.setNextRequestURI(XMLUtil.fetchXPathValAsString(mimeResp,XPathEnum.NEXT_REQUEST_URI.value()));
		response.setForce(XMLUtil.fetchXPathValAsBoolean(mimeResp,XPathEnum.FORCE_TOKEN.value()));
		response.setProductId(XMLUtil.fetchXPathValAsString(mimeResp, XPathEnum.PRODUCT_ID.value()));

		} catch (CARSResponseParseException e) {
			throw new ServiceException("error in repsone parsing: ", e);
		}
		return response;
	}
	
	*//**
	 * This method is used for setting all parameters values 
	 * which is required for sending mail and call method 
	 * to send mail to the receiver
	 * 
	 * @param response the response object with values required
	 * @throws CARSServiceException if any error occurs
	 *//*
	public static void sendConfirmationMail(CARSResponse response) 
	throws CARSServiceException {
		String sender = CARSConfigVariables.getConstantAsString(
				CARSConfigVariables.SENDER_EMAIL_ADDRESS);
		try {
			//get the generic xsl path for confirmation email template
			String xslPath = CARSStringConstants.CONFIRMATION_EMAIL_XSL_PATH.value();
			if (null != xslPath) {
				String templateName = response.getTemplateName();
				String mailBodyContent = XSLTransformer
						.buildXMLForTransformation(response.getEmailContent(),
								templateName, xslPath, true, null);
				Map<MailParameters, String> mailParamMap = new HashMap<MailParameters, String>(
						32, 0.75f);
				mailParamMap.put(MailParameters.SENDER, sender);
				mailParamMap.put(MailParameters.RECIPIENT,
						response.getUserEmail());
				mailParamMap.put(MailParameters.SUBJECT, response.getSubject());
				mailParamMap.put(MailParameters.MESSAGE, mailBodyContent);

				CARSMailUtil.sendMail(mailParamMap);
			}
		} catch (CARSServiceException carsExp) {
			throw new CARSServiceException(
					CARSErrorEventID.EMAIL_DELIVERY_ERROR, 
					CARSStringConstants.SEND_MAIL_ERROR.value(), carsExp);
		}
	}
	
	*//**
	 * This method handles the logic to populate CARS response node value
	 * retrieved from CS to its corresponding attribute in CARSresponse
	 * object using XPATH
	 * 
	 * @param mimeRespList - the list of MIME retrieved from REST call
	 * @param response - the response to which the values are populated
	 * @return CARSResponse - the response to be returned
	 * @throws ServiceException 
	 * @throws CARSResponseParseException 
	 *//*
	@SuppressWarnings("unchecked")
	public static CARSResponse prepareResponseData(List<String> mimeRespList, CARSResponse response) throws ServiceException, CARSResponseParseException {

		String mimeResp1 = null;
		String mimeResp2 = null;
		//The response coming from CS can have one or multiple MIMES 
		if (null != mimeRespList && mimeRespList.size() > 0) {
			mimeResp1 = fetchMIMERespWithUserInfo(mimeRespList);
			
			//check why are we doing this
			prepareKeyEventData(mimeResp1);
			prepareTextZoneData(mimeResp1, response);
			try{
			if (null != mimeResp1) {
				
				processDisplayModel(mimeResp1, response);
				
				List<String> ssoURLs = ((List<String>) XMLUtil.fetchXpathValueFromXML(mimeResp1, XPathEnum.SSO_URL.value(), true));
				response.setSsoURL(ssoURLs);
			}
			if (mimeRespList.size() > 1) {
				mimeResp2 = mimeRespList.get(1);
				if (null != mimeResp2) {
					response.setAthensURL(XMLUtil.fetchXPathValAsString(mimeResp2, XPathEnum.ATHENS_URL.value()));
					response.setShibbolethURL(XMLUtil.fetchXPathValAsString(mimeResp2, XPathEnum.SHIBBOLETH_URL.value()));
					response.setUserEmail(XMLUtil.fetchXPathValAsString(mimeResp2, XPathEnum.MAIL_RECIPIENT.value()));
					response.setSubject(XMLUtil.fetchXPathValAsString(mimeResp2, XPathEnum.MAIL_SUBJECT.value()));
				}
			}
			}catch(CARSResponseParseException e){
				throw new ServiceException("error in repsone parsing: ", e);
			}
		}
		return response;
	}
	
	*//**
	 * CARS4
	 * 
	 * This method will build the text zone for Shibboleth activate personalization page.
	 * @param mimeResp1
	 * @param response
	 * @throws CARSResponseParseException 
	 *//*
	@SuppressWarnings("unchecked")
	private static void prepareTextZoneData(String mimeResp1, CARSResponse response) throws CARSResponseParseException {
		
		List<String> textZoneNamesList = (List<String>) XMLUtil.fetchXpathValueFromXML(mimeResp1, XPathEnum.TEXTZONE_NAME.value(),true);
		List<String> textZoneVarList = (List<String>) XMLUtil.fetchXpathValueFromXML(mimeResp1, XPathEnum.TEXTZONE_VAR.value(),true);
		
		if (textZoneNamesList != null && textZoneNamesList.size() > 0) {
			Map<String, String> textZoneMap = new HashMap<String, String>();
			int count = 0;
		System.out.println("---------------------we got text zone name data--------- handle it.----------------------------------------------- ");
			for (String textZoneKey : textZoneNamesList) {
				String textZoneValue=(String)TextZoneHelper.retrieveFromTextzoneCache(textZoneKey, CARSStringConstants.CARS_TEXT.value());
				if(textZoneValue!=null){
					textZoneMap.put(textZoneVarList.get(count), (String)TextZoneHelper
							.retrieveFromTextzoneCache(textZoneKey, CARSStringConstants.CARS_TEXT.value()));
				}
				count++;
			}
			response.setTextZoneMap(textZoneMap);
		}
	}	

	@SuppressWarnings("unchecked")
	private static void prepareKeyEventData(String mimeResp1) throws CARSResponseParseException {
	
		String recordId = (String) XMLUtil.fetchXpathValueFromXML(mimeResp1, XPathEnum.RECORD_ID.value(), false);
		//if record_id is not present then no need to cut key events
		if (StringUtils.isNotBlank(recordId)) {
			CARSGenericKeyEventData keyEventData = new CARSGenericKeyEventData();
			keyEventData.setRecordId(recordId);
			
			List<String> keyEventNameList = (List<String>) XMLUtil.fetchXpathValueFromXML(mimeResp1, XPathEnum.KEYEVENT_NAME.value(), true);
			
			List<String> keyEventValueList = (List<String>) XMLUtil.fetchXpathValueFromXML(mimeResp1, XPathEnum.KEYEVENT_VALUE.value(), true);
			
			keyEventData.setKeyEventName(keyEventNameList);
			keyEventData.setKeyEventValue(keyEventValueList);
			generateKeyEvent(keyEventData);
		}
	}

	*//**
	 * This method handles the logic to get all the key_event_name and 
	 * key_event_value from MIME and write a setter method for each attribute 
	 * to set values to key event
	 * 
	 * @param keyEventData - object with all key event information
	 *//*
	@SuppressWarnings("rawtypes")
	private static void generateKeyEvent(CARSGenericKeyEventData keyEventData) {
		CARSGenericKeyEvent keyevent = new CARSGenericKeyEvent();
		List<String> keyEventNameList = keyEventData.getKeyEventName();
		List<String> keyEventValueList = keyEventData.getKeyEventValue();
		
		Class<? extends CARSGenericKeyEvent> keyEventClass = keyevent.getClass();
		Class[] paramTypes = new Class[1];
		paramTypes[0] = String.class;
		Method methodName = null;
		int count = 0;
		try {
			if (null != keyEventNameList && keyEventNameList.size() > 0) {
				StringBuilder method = new StringBuilder();
				for (String name : keyEventNameList) {
					method.append(Character.toUpperCase(name.charAt(0))).append(name.substring(1));
					methodName = keyEventClass.getMethod(CARSStringConstants.SETTER.value()+ method.toString(), paramTypes);
					Object[] params = new Object[1];
					params[0] = keyEventValueList.get(count);
					methodName.invoke(keyevent, params);
					count++;
					method.setLength(0);
				}
			}
		} catch (NoSuchMethodException nsme) {
			nsme.printStackTrace();
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		} catch (InvocationTargetException ite) {
			ite.printStackTrace();
		}
		//log the key event
		//logUsageKeyEvent(keyevent);
	}

	*//**
	 * This method will log the CARS generic usage key event
	 * with all generic values present in transaction
	 * 
	 * @param keyEvent the usage key event
	 *//*
	public static void logUsageKeyEvent(UsageKeyEvent keyEvent) {
    	try {
    		// Local vars
    		IWebTransaction transaction = SessionDataHelper.getTransaction();
    		TransactionDescription description = transaction.getDescription();

    		// Populate the key event with the transaction description data
    		if (description != null) {
    			description.populateKeyEvent(keyEvent);
    			// Set the machine ID "was set" flag
    			keyEvent.setMachineIDWasSet(description.isMachineIDSet());
    		}
    		//Log the usage event
    		LogUtil.logUsageKeyEvent(keyEvent);
    	} catch (Exception e) {
    		// Log the exception
    		// Allow processing to continue
    		if (e instanceof BaseException) {
    			((BaseException) e).getErrorEvent();
    		} else {
    			new ErrorEvent(
    					EditCommonWebErrors.USAGE_KEYEVENT_ERROR,
    					"An Exception was encountered while logging a key event!",
    					e);
    		}
    	}
    } 	
	*//**
	 * This method is used for fetching the CARS UserInfo 
	 * MIME response to create/update the User(WebUser) object
	 * if available otherwise fetches the first MIME response
	 * 
	 * @param mimeList - the list of MIME responses
	 * @return String - the MIME response containing UserInfo
	 * @throws ServiceException 
	 * @throws CARSResponseParseException 
	 *//*
	public static String fetchMIMERespWithUserInfo(List<String> mimeList) throws ServiceException  {
		String responseMime = null;
		String mimeResp1 = null;
		String authenticateUsrResp = null;
		for (int i = 0; i < mimeList.size(); i++) {
			responseMime = mimeList.get(i);
			if (null != responseMime && responseMime.length() > 0) {
				try {
					authenticateUsrResp = XMLUtil.fetchXPathValAsString(responseMime, XPathEnum.CARS_AUTHENTICATE_USER_RESPONSE.value());
				} catch (CARSResponseParseException e) {
					throw new ServiceException("error in repsone parsing: ", e);
				}
				if (StringUtils.isNotBlank(authenticateUsrResp)) {
					mimeResp1 = responseMime;
				} else {
					mimeResp1 = mimeList.get(0);
				}

			}
		}
		return mimeResp1;
	}

	*//**
	 * This method is used for getting the cached CARS XSL template 
	 * from TextZoneCache
	 * 
	 * @param templateId - the name of the template to be fetched
	 * @param productId - the product id
	 * @return - the cached XSL template
	 * @throws ServiceException 
	 *//*
	private static Templates getTemplate(CARSResponse resp) throws ServiceException {

		if (StringUtils.isNotBlank(resp.getTemplateName())) {
			ANETemplatesService templateService= new ANETemplatesServiceImpl();
			try {
				return templateService.getTemplateForNCEName(resp.getTemplateName(), getTemplateUpdateDateFromResponse(resp));
			} catch (ServiceException e) {
				throw new ServiceException("failed in reteriving template from template service",e);
			} catch (ParseException e) {
				throw new ServiceException("template last update date parsing exception",e);
			}
		}
		return null;			
			
	}

	private static Timestamp getTemplateUpdateDateFromResponse(CARSResponse resp) throws ParseException {
		if (null != resp.getTemplateUpdateDate()) {
			return  DateUtil.convertToTimeStamp(resp.getTemplateUpdateDate(), TEMPLATE_UPDATE_DATE_FORMAT);
		}
		return null;
	}

	
	private static String transform(String templateId, String xmlInputToTransform, Templates latestTemplate, int counter) throws XSLTTransformationException {
		String transformedHtml = null;

		try {
			
			if (null != xmlInputToTransform && null != latestTemplate) {
				transformedHtml = XMLUtil.transformXML(xmlInputToTransform, latestTemplate);
			}
		} catch (TransformerConfigurationException exp) {
			throw new XSLTTransformationException(exp.getMessage(), exp);
		} catch (TransformerException exp) {
			throw new XSLTTransformationException(exp.getMessage(), exp);
		}
		return transformedHtml;
	}
	




	
	
	
    *//**
     * This method is used for handling the logout scenario where 
     * User object will be set to null and cookies will be deleted 
     * if the remember me flag is set to false 
     * 
     * @param response the CARS response
     * @param mimeResponse the MIME response string
     * @param carsReq the CARS request object
     * @param httpReq the Http Servlet request
     * @param httpResp the Http Servlet response
     *//*
    private void processLogOutFlow(CARSResponse response,
    		String mimeResponse,
			CARSRequest carsReq, HttpServletRequest httpReq,
			HttpServletResponse httpResp) {
		CARSresponse response = (CARSresponse) response;

		// inside logout method
		if (CommonLogger.isInfoEnabled()) {
			CommonLogger.info(CARSStringConstants.LOGOUT_METHOD.value());
		}
		if (response != null) {
			response.setCarsCookie(XMLUtil.fetchXPathValAsString(
					mimeResponse, XPathEnum.CARS_COOKIE.value()));
			response.setStatusSuccess(true);
			// set user as null for logout operation
			response.setWebUser(null);
			SessionDataHelper.getCurrentTransaction().setUser(null);

			// Delete  cookie (for LOG-OUT requests)
			if (response.getCarsCookie() == null) {
				BaseCookie.deleteCookie(httpReq, httpResp,
						CARSAuthenticationConstants.CARS_COOKIE_NAME
						.value());
			}
			// Invalidating the Session attributes
			if (ObjectStoreManager.getInstance() != null) {
				ObjectStoreManager.getInstance()
				.setSessionLevelObjectStore(null);
			}
			if (httpReq.getSession() != null) {
				httpReq.getSession().invalidate();
			}
			if (CommonLogger.isInfoEnabled()) {
				CommonLogger.info("The Session is terminated successfully");
			}
			// SSO logout scenario has happened
			boolean ssoDisableFlag = CARSConfigVariables
					.getConstantAsBoolean(CARSConfigVariables.DISABLE_SSO_AUTH);
			if (!ssoDisableFlag) {
				BaseCookie.deleteCookie(httpReq, httpResp,
						CARSAuthenticationConstants.USER_ANONYMITY_TYPE_COOKIE
								.value());
				BaseCookie.deleteCookie(httpReq, httpResp,
						CARSAuthenticationConstants.USER_AUTH_TOKEN_COOKIE
								.value());
				BaseCookie.deleteCookie(httpReq, httpResp,
						CARSAuthenticationConstants.SESSIONID_COOKIE.value());
				if (CommonLogger.isInfoEnabled()) {
					CommonLogger.info("The SSO logout has happened successfully");
				}
			}
		}
    }*/
    
}