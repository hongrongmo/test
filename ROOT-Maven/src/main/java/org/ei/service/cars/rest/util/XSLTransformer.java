package org.ei.service.cars.rest.util;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamSource;

import net.sf.ehcache.Cache;

import org.apache.commons.lang.StringUtils;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.cache.EVCache;
import org.ei.config.EVProperties;
import org.ei.config.RuntimeProperties;
import org.ei.service.cars.CARSConfigVariables;
import org.ei.service.cars.CARSStringConstants;
import org.ei.service.cars.CARSTemplateNames;
import org.ei.session.UserPreferences;

/**
 * This class is used for performing operations related to XSLT transformation (Create a XML which will be used for CARS template transformation using XSLT)
 *
 * @author naikn1
 * @version 1.0
 *
 */
public class XSLTransformer {
    /** Dummy XML document for transforming XSL */
    public static final String DUMMY_DOCUMENT = XMLFormatter.XML_HEADER + "<document><nodata/></document>";
    public static final String CARS_RESPONSE_MODIFIER_XSL_PATH = "/common/cars/xslt/CarsResponseModifier.xsl";

    /**
     * This method will be used for creating xml structure
     *
     * @param content
     *            - the content from cars
     * @param templateId
     *            - the template name from cars
     * @param xslPath
     *            - the xsl file path
     * @param isMail
     *            - flag to check if the request is for mail confirmation
     * @return xml - the xml input
     * @throws XSLTTransformerException
     * @throws CARSXSLTTransformerException
     *             if any exception occurs
     */
    public static String buildXMLForTransformation(String content, String templateId, String xslPath, boolean isMail, IEVWebUser user)
        throws XSLTTransformationException {

        IStylesheet stylesheet = null;
        Map<String, Object> params = null;
        StringWriter writer = new StringWriter();
        Templates templates = null;
        String outputXML = null;
        InputStream stream = null;
        try {

            stream = XSLTransformer.class.getResourceAsStream(xslPath);
            templates = TransformerFactory.newInstance().newTemplates(new StreamSource(stream));

            if (isMail) {
                params = addEmailParamValues(templateId, content);
            } else {
                // Add I18N data to transform
                content = appendResurceBundleWithCarsResponseXml(content, templateId);
                params = addXMLParamValues(templateId, content, user.isIndividuallyAuthenticated());
            }

            /*
             * if (textZone != null) { params.put(XMLTagConstants.TEXT_ZONE.toString(), textZone); }
             */

            stylesheet = new Stylesheet(templates);
            templates = (Templates) stylesheet.getStylesheetImpl();
            StringReader strReader = new StringReader(DUMMY_DOCUMENT);
            // Create a transformer
            ITransformer transformer = GenericTransformer.newInstance();
            // transform to output string writer
            transformer.transform(strReader, templates, writer, params);
            outputXML = writer.toString();
            if (StringUtils.isNotBlank(outputXML)) {
                outputXML = outputXML.replaceAll(CARSStringConstants.LESS_THAN_ENCODE.value(), CARSStringConstants.LESS_THAN.value());
                outputXML = outputXML.replaceAll(CARSStringConstants.GREATER_THAN_ENCODE.value(), CARSStringConstants.GREATER_THAN.value());
                outputXML = outputXML.replaceAll(CARSStringConstants.GTR_THAN_UTF8.value(), CARSStringConstants.GREATER_THAN.value());
                outputXML = outputXML.replaceAll(CARSStringConstants.AMPERSAND_UTF8.value(), CARSStringConstants.AMPERSAND.value());

            }

            if (isCarResponseNeedsToBeOverWritten(templateId)) {
                return modifyCarResponseParameters(templateId, user.getUserPreferences(), outputXML);
            }

        } catch (TransformerConfigurationException e) {
            throw new XSLTTransformationException(e);
        } catch (TransformerFactoryConfigurationError ex) {
            throw new XSLTTransformationException(ex.getException());
        }

        return outputXML;
    }

    private static String modifyCarResponseParameters(String templateId, UserPreferences userPreferences, String outputXML) throws XSLTTransformationException {
        Templates carResponseModifierTemplate = null;
        InputStream carResponseModifierStream = null;
        StringWriter modifiedCarResponseWriter = new StringWriter();
        try {
            StringReader outputXmlReader = new StringReader(outputXML);
            ITransformer transformer = GenericTransformer.newInstance();
            carResponseModifierStream = XSLTransformer.class.getResourceAsStream(CARS_RESPONSE_MODIFIER_XSL_PATH);
            carResponseModifierTemplate = TransformerFactory.newInstance().newTemplates(new StreamSource(carResponseModifierStream));
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("userPreferences", userPreferences);

            transformer.transform(outputXmlReader, carResponseModifierTemplate, modifiedCarResponseWriter, paramMap);

        } catch (TransformerConfigurationException e) {
            throw new XSLTTransformationException(e);
        } catch (TransformerFactoryConfigurationError ex) {
            throw new XSLTTransformationException(ex.getException());
        }

        return modifiedCarResponseWriter.toString();
    }

    private static boolean isCarResponseNeedsToBeOverWritten(String templateId) {
        return EVProperties.getCarResponseOverwriteRequiredList().contains(templateId);
    }

    @SuppressWarnings("unchecked")
    private static String appendResurceBundleWithCarsResponseXml(String content, String templateId) {
        Cache cache = EVCache.getCarsI18nDataCache();
        if ((null != cache.get(EVCache.NCE_CARS_I18N_DATA))) {
            Map<String, Object> i18nData = (Map<String, Object>) cache.get(EVCache.NCE_CARS_I18N_DATA).getObjectValue();
            String i18nXML = (String) i18nData.get(templateId);
            content = content + i18nXML;
        }
        return content;
    }

    /**
     * This method will be used for getting the SMAPI/Config values required to be passed to XML template
     *
     * @param templateId
     *            - the cars template id
     * @param content
     *            - the mime response content with cars info
     * @return Map<String, Object> - map with all params to be passed to xsl
     * @throws XSLTTransformerException
     */
    public static Map<String, Object> addXMLParamValues(String templateId, String content, boolean isindividual) throws XSLTTransformationException {
        Map<String, Object> params = new HashMap<String, Object>();

        content = fetchMimeForXMLBuild(content);
        params.put(XMLTagConstants.CARS_MIME_RESP.toString(), content);

        params.put(XMLTagConstants.APP_DOMAIN.toString(), CARSConfigVariables.getConstantAsString(CARSConfigVariables.APP_DOMAIN));
        params.put(XMLTagConstants.APP_PRODUCT_NAME.toString(), CARSConfigVariables.getConstantAsString(CARSConfigVariables.APP_PRODUCT_NAME));
        params.put(XMLTagConstants.PRODUCT_SPECIFIC_LINKS.toString(), CARSConfigVariables.getConstantAsString(CARSConfigVariables.PRODUCT_SPECIFIC_LINKS));
        params.put(XMLTagConstants.LOGIN_TOP_LINKS.toString(), CARSConfigVariables.getConstantAsString(CARSConfigVariables.LOGIN_TOP_LINKS));
        params.put(XMLTagConstants.LOGOUT_TOP_LINKS.toString(), CARSConfigVariables.getConstantAsString(CARSConfigVariables.LOGOUT_TOP_LINKS));
        params.put(XMLTagConstants.USER_AGREEMENT_URL.toString(), CARSConfigVariables.getConstantAsString(CARSConfigVariables.USER_AGREEMENT_URL));
        params.put(XMLTagConstants.PRIVACY_POLICY_URL.toString(), CARSConfigVariables.getConstantAsString(CARSConfigVariables.PRIVACY_POLICY_URL));
        params.put(XMLTagConstants.APP_HIDE_IMAGE.toString(), CARSConfigVariables.getConstantAsString(CARSConfigVariables.APP_SHOW_IMAGE));
        params.put(XMLTagConstants.APP_SHOW_IMAGE.toString(), CARSConfigVariables.getConstantAsString(CARSConfigVariables.APP_HIDE_IMAGE));

        if (!CARSTemplateNames.CARS_CHANGE_PASSWORD_SUCCESSFUL.toString().equals(templateId)) {
            params.put(XMLTagConstants.APP_HOME_URI.toString(), CARSConfigVariables.getConstantAsString(CARSConfigVariables.APP_HOME_URI));
        }

        if (CARSTemplateNames.CARS_INSTITUTION_CHOICE.toString().equals(templateId)) {
            params.put(XMLTagConstants.APP_LEARN_MORE_URL.toString(), EVProperties.getRuntimeProperties().getHelpUrl() + "#"
                + EVProperties.getRuntimeProperties().getProperty(RuntimeProperties.HELP_CONTEXT_CUSTOMER_INSTITUTIONCHOICE));
        }
        if (CARSTemplateNames.CARS_LOGIN_FULL.toString().equals(templateId)) {
            params.put(XMLTagConstants.LOGIN_FULL_REG_TZ.toString(), CARSConfigVariables.getConstantAsString(CARSConfigVariables.LOGIN_FULL_TEXT));
            params.put(XMLTagConstants.CARS_NOBODY_LOGIN_FULL_TZ.toString(),
                CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_NOBODY_LOGIN_FULL_TZ));
        } else if (CARSTemplateNames.CARS_REGISTER.toString().equals(templateId)) {
            params.put(XMLTagConstants.PRIVACY_POLICY_URL.toString(), CARSConfigVariables.getConstantAsString(CARSConfigVariables.PRIVACY_POLICY_URL));
        }

        // Add registration confirmation mail parameters
        if (CARSTemplateNames.CARS_REGISTRATION_CONFIRMATION_EMAIL.toString().equals(templateId)) {
            params.put(XMLTagConstants.REGISTRATION_CONFIRMATION_EMAIL_TXT_COLOR.toString(),
                CARSConfigVariables.getConstantAsString(CARSConfigVariables.REGISTRATION_CONFIRMATION_EMAIL_TXT_COLOR));
            params.put(XMLTagConstants.REGISTRATION_CONFIRMATION_EMAIL_BAR_COLOR.toString(),
                CARSConfigVariables.getConstantAsString(CARSConfigVariables.REGISTRATION_CONFIRMATION_EMAIL_BAR_COLOR));
            params.put(XMLTagConstants.REGISTRATION_CONFIRMATION_EMAIL_MESSAGE_LIST.toString(),
                CARSConfigVariables.getConstantAsString(CARSConfigVariables.REGISTRATION_CONFIRMATION_EMAIL_MESSAGE_LIST));
        }
        if (CARSTemplateNames.CARS_EMAIL_SETUP.toString().equals(templateId)) {
            params
                .put(XMLTagConstants.CARS_EMAIL_SETUP_BOT_TZ.toString(), CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_EMAIL_SETUP_BOT_TZ));
            params
                .put(XMLTagConstants.CARS_EMAIL_SETUP_TOP_TZ.toString(), CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_EMAIL_SETUP_TOP_TZ));
        }
        if (CARSTemplateNames.CARS_LOGIN_FULL.toString().equalsIgnoreCase(templateId)) {
            params.put(XMLTagConstants.LOGIN_FULL_CANCEL_URI.toString(), CARSConfigVariables.getConstantAsString(CARSConfigVariables.LOGIN_FULL_CANCEL_URI));

        }
        /*
         * if (CARSTemplateNames.CARS_PATH_CHOICE.toString() .equals(templateId)) { params.put(XMLTagConstants.PATH_CHOICE_NOTE_TEXT.toString(),
         * CARSConfigVariables.getConstantAsString( CARSConfigVariables.PATH_CHOICE_NOTE_TEXT)); }
         */
        /*
         * if (CARSTemplateNames.CARS_INACCESSIBLE.toString() .equals(templateId)) { if (SessionDataHelper.getObject(
         * CARSAuthenticationConstants.PREVIOUS_URL.name(), SessionDataHelper.SCOPE_SESSION) != null) { String previousPage = (String)
         * SessionDataHelper.getObject( CARSAuthenticationConstants.PREVIOUS_URL.name(), SessionDataHelper.SCOPE_SESSION); previousPage =
         * previousPage.replaceAll( CARSStringConstants.AMPERSAND.value(), CARSStringConstants.AMPERSAND_UTF8.value());
         * params.put(XMLTagConstants.APP_PREV_URI.toString(), previousPage); } params.put(XMLTagConstants.APP_HOME_URI.toString(),
         * CARSConfigVariables.getConstantAsString( CARSConfigVariables.APP_HOME_URI)); } params.put(XMLTagConstants.PRIVACY_POLICY_URL.toString(),
         * CARSConfigVariables.getConstantAsString( CARSConfigVariables.PRIVACY_POLICY_URL)); params.put(XMLTagConstants.FGT_PWD_EMAIL_PROD_URI.toString(),
         * CARSConfigVariables.getConstantAsString( CARSConfigVariables.FGT_PWD_EMAIL_PROD_URI)); params.put(XMLTagConstants.USER_AGREEMENT_URL.toString(),
         * CARSConfigVariables.getConstantAsString( CARSConfigVariables.USER_AGREEMENT_URL)); params.put(XMLTagConstants.APP_HIDE_IMAGE.toString(),
         * CARSConfigVariables.getConstantAsString( CARSConfigVariables.APP_HIDE_IMAGE)); params.put(XMLTagConstants.APP_SHOW_IMAGE.toString(),
         * CARSConfigVariables.getConstantAsString( CARSConfigVariables.APP_SHOW_IMAGE)); params.put(XMLTagConstants.APP_DEFAULT_IMAGE.toString(),
         * CARSConfigVariables.getConstantAsString( CARSConfigVariables.APP_DEFAULT_IMAGE)); if (CARSTemplateNames.CARS_LOGIN_FULL
         * .toString().equalsIgnoreCase(templateId)) { if (SessionDataHelper.getObject (CARSAuthenticationConstants.PREVIOUS_URL.
         * name(),SessionDataHelper.SCOPE_SESSION) != null) { String previousPage = (String) SessionDataHelper.getObject(
         * CARSAuthenticationConstants.PREVIOUS_URL.name(), SessionDataHelper.SCOPE_SESSION); previousPage = previousPage.replaceAll(
         * CARSStringConstants.AMPERSAND.value(), CARSStringConstants.AMPERSAND_UTF8.value()); params.put(XMLTagConstants.LOGIN_FULL_CANCEL_URI.toString(),
         * previousPage); } else { params.put(XMLTagConstants.LOGIN_FULL_CANCEL_URI.toString(), CARSConfigVariables.getConstantAsString(
         * CARSConfigVariables.APP_HOME_URI)); } }
         */

        /*
         * boolean isFromScopus = CARSConfigVariables.getConstantAsBoolean(CARSConfigVariables .REQUEST_FROM_SCOPUS);
         */
        params.put(XMLTagConstants.APP_URL_EXTN.toString(), CARSConfigVariables.getConstantAsString(CARSConfigVariables.APP_URL_EXTN));
        params.put(XMLTagConstants.APP_DEFAULT_IMAGE.toString(), CARSConfigVariables.getConstantAsString(CARSConfigVariables.APP_DEFAULT_IMAGE));

        if (CARSTemplateNames.CARS_REG_ID_ASSOCIATION.toString().equals(templateId)) {
        	params.put(XMLTagConstants.LOGIN_FULL_REG_TZ.toString(), CARSConfigVariables.getConstantAsString(CARSConfigVariables.LOGIN_FULL_TEXT));
            params.put(XMLTagConstants.REG_ID_ASSOC_CANCEL.toString(), CARSConfigVariables.getConstantAsString(CARSConfigVariables.REG_ID_ASSOC_CANCEL));
        }

        // add settings links at the top, this is not applicable for other
        // clusters
        if (CARSTemplateNames.CARS_SETTINGS.toString().equalsIgnoreCase(templateId)) {
            String settingsTopLinks = buildSettingsLinksAtTop(isindividual);
            if (StringUtils.isNotBlank(settingsTopLinks)) {
                params.put(XMLTagConstants.SETTINGS_LINKS_TOP.toString(), settingsTopLinks);
            }
            /*
             * String settingsBottomLinks = buildSettingsLinksAtBottom(); if (null != settingsBottomLinks && !CARSStringConstants.EMPTY.value().equals(
             * settingsBottomLinks)) { params.put(XMLTagConstants.SETTINGS_LINKS_BOTTOM.toString(), settingsBottomLinks); }
             */
        }

        return params;
    }

    private static String buildSettingsLinksAtTop(boolean isindividual) {
        StringBuilder evLinksforSettingsPage = new StringBuilder();
        evLinksforSettingsPage.append(CARSStringConstants.SETTINGS_LINK_OPEN_DIV.value());
        if (isindividual) {
            String myPrefsPopupLink = CARSConfigVariables.getConstantAsString(CARSConfigVariables.VIEW_MY_PREFS_LINK);
            evLinksforSettingsPage.append(myPrefsPopupLink);
        }
        String savedSearchesAndAlertsPageLink = CARSConfigVariables.getConstantAsString(CARSConfigVariables.SAVED_SEARCHES_ALERTS_LINK);
        evLinksforSettingsPage.append(savedSearchesAndAlertsPageLink);
        String viewUpdateFolderLink = CARSConfigVariables.getConstantAsString(CARSConfigVariables.VIEW_UPDATE_FOLDER_LINK);
        evLinksforSettingsPage.append(viewUpdateFolderLink);

        evLinksforSettingsPage.append(CARSStringConstants.SETTINGS_LINK_CLOSE_DIV.value());

        return evLinksforSettingsPage.toString();
    }

    /*
     * private static String buildSettingsLinksAtBottom() { String allLeftSettingsLink = null; StringBuilder stringBuilder = new StringBuilder(); // RefWorks
     * settings Links String settingsLink = CARSConfigVariables .getConstantAsString(CARSConfigVariables.REFWORKS_SETTINGS_LINK); boolean isRefworksEnabled =
     * CARSConfigVariables.getConstantAsBoolean( CARSConfigVariables.ENABLE_REFWORKS); if (null != settingsLink && isRefworksEnabled) {
     * stringBuilder.append(settingsLink); }
     *
     * // Manage applications settingsLink = CARSConfigVariables .getConstantAsString(CARSConfigVariables.MANAGE_APP_LINK); boolean isManageApp = CARSCommonUtil
     * .isFenceAvailable(CARSStringConstants.MANAGE_APP_FENCE.value()); if (null != settingsLink && isManageApp) { stringBuilder.append(settingsLink); }
     *
     * // Account administration settingsLink = CARSConfigVariables .getConstantAsString(CARSConfigVariables.ADMIN_TOOL_LINK); boolean isAdminUser =
     * CARSCommonUtil .isFenceAvailable(CARSStringConstants.ADMIN_USER_FENCE.value()); if (null != settingsLink && isAdminUser) {
     * stringBuilder.append(settingsLink); } allLeftSettingsLink = stringBuilder.toString(); return allLeftSettingsLink; }
     */
    /**
     * Method to remove the xml declaration in MIME response and returns the cars response structure which will be used for XML building
     *
     * @param originalMime
     *            the mime with xml declaration
     * @return mime response without xml declaration
     */
    public static String fetchMimeForXMLBuild(String originalMime) {
        if (StringUtils.isNotBlank(originalMime)) {
            int len = originalMime.indexOf(CARSStringConstants.XML_END_PARAM.value());
            String strToBeRemoved = originalMime.substring(0, len + 2);
            if (null != strToBeRemoved) {
                originalMime = originalMime.replace(strToBeRemoved, "");
            }
        }
        return originalMime;
    }

    /**
     * This method is used for adding the param and values required for email content construction (to be used in ForgotPassword and Registration Confirmation
     * XSLs)
     *
     * @param content
     *            the mail content from cars
     * @param templateId
     *            the email template id from CS
     * @return Map<String, Object> - map with all param values
     */
    public static Map<String, Object> addEmailParamValues(String templateId, String content) {
        Map<String, Object> params = new HashMap<String, Object>(10, 0.05f);
        params.put(XMLTagConstants.MAIL_BODY.toString(), content);

        // currently only for forgotten password email we need to add header and
        // footer info from web (SMAPI)
        if (CARSTemplateNames.CARS_FORGOTTEN_PASSWORD_EMAIL.toString().equalsIgnoreCase(templateId)) {
            params.put(XMLTagConstants.CARS_MAIL_FOOTER.toString(), CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_MAIL_FOOTER));
        }
        return params;
    }

}
