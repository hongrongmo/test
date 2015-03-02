package org.ei.service.cars;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.ane.template.ANETemplatesService;
import org.ei.ane.template.ANETemplatesServiceImpl;
import org.ei.biz.email.SESEmail;
import org.ei.biz.email.SESMessage;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;
import org.ei.service.cars.Impl.CARSResponse;
import org.ei.service.cars.rest.util.XMLUtil;
import org.ei.service.cars.rest.util.XSLTTransformationException;
import org.ei.service.cars.rest.util.XSLTransformer;
import org.ei.util.DateUtil;

public class CARSResponseHelper {
	private final static Logger log4j = Logger.getLogger(CARSResponseHelper.class);

	private static String TEMPLATE_UPDATE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.S";

	/**
	 * This method handles the logic to - Fetch the template from cache (if not
	 * latest then get from cs db) - Build/Create a XML using XSLT to give as
	 * input for XSL transformation - Transform CARS template XSL to html
	 * content - If the page type is EMAIL then send the content to mail
	 * receiver, otherwise the returned content to response
	 *
	 * @param response
	 *            the response object
	 * @param templateMime
	 *            CARS mime response containing template XML
	 */
	public static String transformResponse(CARSResponse response, String templateMime, IEVWebUser user) throws ServiceException {
		if (response == null || StringUtils.isBlank(templateMime)) {
			throw new IllegalArgumentException("CARSResponse object or Mime string are empty!");
		}

		Templates templates = getTemplate(response);
		if (null == templates) {
			log4j.error("Unable to build template from response: " + response.toString() + System.lineSeparator() + "   Mime: " + templateMime);
			throw new ServiceException(SystemErrorCodes.CARS_RESPONSE_PROCESSING_ERROR, "Unable to transform CARS response - No template in response!");
		}

		// Map<String, String> textZoneMap = response.getTextZoneMap();
		String xslPath = CARSStringConstants.XSL_PATH.value();
		String templateName = response.getTemplateName();
		String xmlToTransform = null;
		String transformedHtml;
		try {
			// Build XML from CARS response with corresponding template
			xmlToTransform = XSLTransformer.buildXMLForTransformation(templateMime, templateName, xslPath, false, user);
			// Build HTML for display
			transformedHtml = transform(templateName, xmlToTransform, templates, 1);

		} catch (XSLTTransformationException e) {
			throw new ServiceException(SystemErrorCodes.CARS_RESPONSE_PROCESSING_ERROR, "Response xml tranformation failed.", e);
		}

		return transformedHtml;
	}

	/**
	 * This method is used for getting the cached CARS XSL template from
	 * TextZoneCache
	 *
	 * @param templateId
	 *            - the name of the template to be fetched
	 * @param productId
	 *            - the product id
	 * @return - the cached XSL template
	 * @throws ServiceException
	 */
	private static Templates getTemplate(CARSResponse resp) throws ServiceException {


		if (StringUtils.isNotBlank(resp.getTemplateName())) {
			ANETemplatesService templateService = new ANETemplatesServiceImpl();
			try {
				return templateService.getTemplateForNCEName(resp.getTemplateName(), getTemplateUpdateDateFromResponse(resp));
			} catch (ServiceException e) {
				throw new ServiceException(SystemErrorCodes.CARS_TEMPLATE_ERROR, e);
			} catch (ParseException e) {
				throw new ServiceException(SystemErrorCodes.CARS_TEMPLATE_ERROR, e);
			}
		}
		return null;

	}

	private static Timestamp getTemplateUpdateDateFromResponse(CARSResponse resp) throws ParseException {
		if (StringUtils.isNotBlank(resp.getTemplateUpdateDate())) {
			return DateUtil.convertToTimeStamp(resp.getTemplateUpdateDate(), TEMPLATE_UPDATE_DATE_FORMAT);
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

	/**
	 * This method is used for fetching the CARS UserInfo MIME response to
	 * create/update the User(WebUser) object if available otherwise fetches the
	 * first MIME response
	 *
	 * @param mimeList
	 *            - the list of MIME responses
	 * @return String - the MIME response containing UserInfo
	 * @throws ServiceException
	 * @throws ServiceException
	 */
	public static String fetchUserInfo(List<String> mimeList) throws ServiceException {
		if (mimeList == null || mimeList.size() == 0) {
			log4j.warn("Invalid parameters - CARSResponse or Mime list is null or empty");
			return null;
		}

		String userinfo = mimeList.get(0);
		String authenticateUsrResp = null;
		for (int i = 0; i < mimeList.size(); i++) {
			if (GenericValidator.isBlankOrNull(mimeList.get(i)))
				continue;
			String responseMime = mimeList.get(i);
			authenticateUsrResp = XMLUtil.fetchXPathValAsString(responseMime, XPathEnum.CARS_AUTHENTICATE_USER_RESPONSE_TYPE.value());
			if (StringUtils.isNotBlank(authenticateUsrResp)) {
				userinfo = responseMime;
			}
		}
		return userinfo;
	}

	/**
	 * Determines if the current CARS response is a path choice
	 *
	 * @param carsresponse
	 * @return
	 */
	public static boolean isPathChoice(CARSResponse carsresponse) {
		List<String> mimelist = carsresponse.getMimeList();
		if (mimelist == null || mimelist.isEmpty()) {
			log4j.warn("No mime list in CARS response!");
			return false;
		}
		try {
			String mimeResp = mimelist.get(0);
			if (!GenericValidator.isBlankOrNull(XMLUtil.fetchXPathValAsString(mimeResp, XPathEnum.PATH_CHOICE_INFO.value()))) {
				log4j.info("Found '" + XPathEnum.PATH_CHOICE_INFO.value() + "' element in CARSResponse mime list!");
				return true;
			}
		} catch (ServiceException e) {
			log4j.error("Unable to parse CARS response for path choice: " + e);
		}
		return false;
	}

	/**
	 * Determines if the current CARS response is a path choice
	 *
	 * @param carsresponse
	 * @return
	 */
	public static boolean isUserInfo(CARSResponse carsresponse) {
		List<String> mimelist = carsresponse.getMimeList();
		if (mimelist == null || mimelist.isEmpty()) {
			log4j.warn("No mime list in CARS response!");
			return false;
		}
		try {
			String mimeResp = mimelist.get(0);
			if (!GenericValidator.isBlankOrNull(XMLUtil.fetchXPathValAsString(mimeResp, XPathEnum.USER_INFO.value()))) {
				log4j.info("Found '" + XPathEnum.USER_INFO.value() + "' element in CARSResponse mime list!");
				return true;
			}
		} catch (ServiceException e) {
			log4j.error("Unable to parse CARS response for user info: " + e);
		}
		return false;
	}

	/**
	 * This method is used for setting all parameters values which is required
	 * for sending mail and call method to send mail to the receiver
	 *
	 * @param response
	 *            the response object with values required
	 * @throws ServiceException
	 *             if any error occurs
	 */
	public static void sendConfirmationMail(CARSResponse response, String userEmail, String emailSubject, String emailContent) throws ServiceException {
		String sender = CARSConfigVariables.getConstantAsString(CARSConfigVariables.SENDER_EMAIL_ADDRESS);
		// get the generic xsl path for confirmation email template
		String xslPath = CARSStringConstants.CONFIRMATION_EMAIL_XSL_PATH.value();
		if (null != xslPath) {
			String templateName = response.getTemplateName();
			String mailBodyContent = null;
			try {
				mailBodyContent = XSLTransformer.buildXMLForTransformation(emailContent, templateName, xslPath, true, null);
			} catch (XSLTTransformationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {

				SESMessage sesmessage = new SESMessage();
				sesmessage.setDestination(userEmail);
	            sesmessage.setMessage(emailSubject, mailBodyContent,true);
	            sesmessage.setFrom(sender);
	            SESEmail.getInstance().send(sesmessage);

			} catch (Throwable t) {
				log4j.error("Error occurred while sending email!", t);
			}

		}
	}

}
