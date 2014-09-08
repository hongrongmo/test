package org.ei.stripes.action.delivery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.EmailTypeConverter;
import net.sourceforge.stripes.validation.Validate;

import org.apache.commons.io.IOUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.domain.Abstract;
import org.ei.domain.BasketPage;
import org.ei.domain.Citation;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DocID;
import org.ei.domain.EIDoc;
import org.ei.domain.FullDoc;
import org.ei.domain.MultiDatabaseDocBuilder;
import org.ei.domain.Page;
import org.ei.domain.PageEntry;
import org.ei.domain.PageEntryBuilder;
import org.ei.domain.personalization.FolderPage;
import org.ei.domain.personalization.SavedRecords;
import org.ei.email.SESEmail;
import org.ei.email.SESMessage;
import org.ei.exception.InfrastructureException;
import org.ei.session.UserSession;
import org.ei.stripes.action.SystemMessage;
import org.ei.stripes.adapter.BizXmlAdapter;
import org.ei.util.GUID;

import com.icl.saxon.TransformerFactoryImpl;

@UrlBinding("/delivery/email/{$event}.url")
public class EmailDeliveryAction extends AbstractDeliveryAction {
	private final static Logger log4j = Logger.getLogger(EmailDeliveryAction.class);

	@Validate(required = true)
	private String to;
	@Validate(required = true, converter = EmailTypeConverter.class)
	private String from;
	@Validate(required = true, maxlength = 1024)
	private String subject;
	private String message;
	protected boolean confirm;

	@DontValidate
	@HandlesEvent("display")
	public Resolution display() throws InfrastructureException {

		return new ForwardResolution("/WEB-INF/pages/customer/delivery/email.jsp");
	}

	@Validate
	@HandlesEvent("submit")
	public Resolution submit() throws Exception {

		// Ensure there are documents present
		if (GenericValidator.isBlankOrNull(docidlist) &&
			GenericValidator.isBlankOrNull(folderid) &&
			(basket == null || basket.countPages() == 0)) {
			log4j.warn("No documents to email!");
			return new ForwardResolution("/WEB-INF/pages/customer/delivery/email.jsp");
		}

		UserSession usersession = context.getUserSession();
		DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
		List<DocID> docidList = new ArrayList<DocID>();

		// Figure out the output format
		String stylesheetprefix = "Citation";
		String docformat = Citation.CITATION_FORMAT;
		if("abstract".equals(displayformat))
	    {
			docformat = Abstract.ABSTRACT_FORMAT;
			stylesheetprefix = "Abstract";
	    }
	    else if("detailed".equals(displayformat))
	    {
	    	docformat = FullDoc.FULLDOC_FORMAT;
	    	stylesheetprefix = "Detailed";
	    }

		//
		// What kind of request?  docidlist indicates request came from
		// abs/det page; folderid indicates Saved Folders; neither indicates
		// basket-based delivery
		//
        Writer xmlWriter;
		if (!GenericValidator.isBlankOrNull(docidlist)) {

			// ****************************************************************
			// Get documents from docid list (request param)
			// ****************************************************************

			// This is almost always a single document from abs/det page
			// so use StringWriter
			xmlWriter = new StringWriter();
			try {
				String[] docidarr = docidlist.split(",");
				String[] handlearr = handlelist.split(",");
				if (docidarr.length != handlearr.length) {
					log4j.warn("docids size does not equal handles size!");
					return SystemMessage.SYSTEM_ERROR_RESOLUTION;
				}

				// Add docids to list
				DocID docidObj;
				int handle;
				for (int i=0; i < docidarr.length; i++) {
					if (!GenericValidator.isBlankOrNull(docidarr[i])) {
			            handle =  Integer.parseInt((String) handlearr[i]);
			            docidObj = new DocID(handle,
			            		docidarr[i].trim(),
			                    databaseConfig.getDatabase(docidarr[i].substring(0,3)));
			            docidList.add(docidObj);
					}
				}

				// Build page of data from doc/handle id lists
			    MultiDatabaseDocBuilder builder = new MultiDatabaseDocBuilder();
			    List<EIDoc> bList = builder.buildPage(docidList, docformat);
			    PageEntryBuilder eBuilder = new PageEntryBuilder(usersession.getSessionID().getID());
			    List<PageEntry> pList = eBuilder.buildPageEntryList(bList);
			    Page page1 = new Page();
			    page1.addAll(pList);

			    // Transform to text via XSLT
		        xmlWriter.write("<PAGE><!--BH--><HEADER/><!--EH-->");
		        page1.toXML(xmlWriter);
		        xmlWriter.write("<!--*-->");
		        xmlWriter.write("<!--BF--><FOOTER/><!--EF-->");
		        xmlWriter.write("</PAGE>");
		        xmlWriter.write("<!--END-->");

				// Build and send email message
		        confirm = sendMessage(stylesheetprefix, xmlWriter);
				if (!confirm) {
					log4j.error("Unable to send email!");
					return SystemMessage.SYSTEM_ERROR_RESOLUTION;
				}

			} catch (Exception e) {
				if (xmlWriter != null) {
					xmlWriter.close();
				}
				log4j.error("Unable to process Docid contents!");
				return SystemMessage.SYSTEM_ERROR_RESOLUTION;
			}

		} else if (!GenericValidator.isBlankOrNull(folderid)) {

			// ****************************************************************
			// Get documents from folder id (request param)
			// ****************************************************************

			// Limit for folder is 50 docs, so use StringWriter
			xmlWriter = new StringWriter();
			try {
			    // Retrieve folder (this.folder should already be
				// set by init() method from parent class)
			    SavedRecords savedRecords = new SavedRecords(usersession.getUser().getUserId());
			    FolderPage folderpage = (FolderPage) savedRecords.viewRecordsInFolder(this.folderid, docformat);

		        xmlWriter.write("<PAGE><!--BH--><HEADER/><!--EH-->");
		        folderpage.toXML(xmlWriter);
		        xmlWriter.write("<!--*-->");
		        xmlWriter.write("<!--BF--><FOOTER/><!--EF-->");
		        xmlWriter.write("</PAGE>");
		        xmlWriter.write("<!--END-->");

				// Build and send email message
		        confirm = sendMessage(stylesheetprefix, xmlWriter);
				if (!confirm) {
					log4j.error("Unable to send email!");
					return SystemMessage.SYSTEM_ERROR_RESOLUTION;
				}

			} catch (Exception e) {
				log4j.error("Unable to process Folder contents!");
				return SystemMessage.SYSTEM_ERROR_RESOLUTION;
			} finally {
				if (xmlWriter != null) {
					xmlWriter.close();
					xmlWriter = null;
				}
			}
		} else if (basket != null && basket.countPages() > 0) {

			// ****************************************************************
			// Get documents from current basket
			// ****************************************************************
			confirm = sendMessageFromBasket(stylesheetprefix, docformat);
			if (!confirm) {
				return SystemMessage.SYSTEM_ERROR_RESOLUTION;
			}

		} else {
			log4j.warn("Unable to process email - no way to retrieve documents!");
			return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		}

		return new ForwardResolution("/WEB-INF/pages/customer/delivery/email.jsp");
	}

	/**
	 * Common method to send a message from XML input.
	 *
	 * @param stylesheetprefix
	 * @param xmlWriter
	 * @return
	 */
	private boolean sendMessage(String stylesheetprefix, Writer xmlWriter) {
		try {

			// Transform xml in Writer via Stylesheet to create message body
	        String stylesheet = this.getClass().getResource("/transform/delivery/download" + stylesheetprefix + "Ascii.xsl").toString();
            TransformerFactory tFactory = new TransformerFactoryImpl();
            Templates templates = tFactory.newTemplates(new StreamSource(stylesheet));
            Transformer transformer = templates.newTransformer();

	        StringWriter outWriter = new StringWriter();
            transformer.transform(new StreamSource(new StringReader(xmlWriter.toString().replaceAll(BizXmlAdapter.xml10_illegal_xml_pattern, ""))),
                          new StreamResult(outWriter));

		    SESMessage sesmessage = new SESMessage();
		    String toStr = to.replaceAll(";", ",");
		    sesmessage.setDestination(Arrays.asList(toStr.split(",")));
			sesmessage.setMessage(subject, "This email was sent to you on behalf of " + from + " \n \n" +
					message + " \n \n" +
					outWriter.toString(), false);

			sesmessage.setFrom(SESMessage.NOREPLY_SENDER);
            SESEmail.getInstance().send(sesmessage);

		} catch (Throwable t) {
        	log4j.error("Unable to build and send Email message, Exception: " + t.getClass().getName() + " (" + t.getMessage() + ")");
			return false;
		}
		return true;
	}

	/**
	 * Sending email from basket contents can get pretty big (up to 500
	 * documents) so we need to stream XML to a temp file and then
	 * transform to an email.
	 *
	 * @param stylesheetprefix
	 * @return
	 */
	private boolean sendMessageFromBasket(String stylesheetprefix, String docformat) {

		String fileName = null;
		PrintWriter tmpWriter = null;
		StringWriter xmlWriter = null;

        try {

        	String toStr = to.replaceAll(";", ",");

			// Get the stylesheet and setup the transformer
            String stylesheet = this.getClass().getResource("/transform/delivery/download" + stylesheetprefix + "Ascii.xsl").toString();
            TransformerFactory tFactory = new TransformerFactoryImpl();
            Templates templates = tFactory.newTemplates(new StreamSource(stylesheet));
            Transformer transformer = null;

			// ************************************************************************
			// Write temp file to email to user
			// ************************************************************************
	        fileName = "/tmp/"+(new GUID()).toString();
			tmpWriter = new PrintWriter(new FileWriter(fileName));

			tmpWriter.println("This email was sent to you on behalf of " + from);
			tmpWriter.println("");
			if(message != null && message.length() != 0)
            {
            	tmpWriter.println(message);
            }
            tmpWriter.println("");

            // Write out header (body)
            transformer = templates.newTransformer();
            transformer.transform(new StreamSource(new StringReader("<SECTION-DELIM><HEADER/></SECTION-DELIM>")),
                          new StreamResult(tmpWriter));

            // Write out basket contents 1 page at a time (body)
            BasketPage basketPage = (BasketPage) basket.getBasketPage(docformat);
            for(int z = 1; z<=basket.countPages(); ++z)
            {
            	// Retrieve page of results
                basketPage = basket.pageAt(z, docformat);

                // Create XML for page
                xmlWriter = new StringWriter();
                xmlWriter.write("<SECTION-DELIM>");
                basketPage.toXML(xmlWriter);
                xmlWriter.write("</SECTION-DELIM>");

                // Transform and write to tmp file!
                transformer = templates.newTransformer();
                transformer.transform(new StreamSource(new StringReader(xmlWriter.toString().replaceAll(BizXmlAdapter.xml10_illegal_xml_pattern, ""))),
                              new StreamResult(tmpWriter));
            }
            // Write out footer (body)
            transformer = templates.newTransformer();
            transformer.transform(new StreamSource(new StringReader("<SECTION-DELIM><HEADER/></SECTION-DELIM>")),
                          new StreamResult(tmpWriter));

            tmpWriter.close();

            // ************************************************************************
			// Now stream temp file to user as Email
			// ************************************************************************
            InputStream inStream = null;
            try
            {
                inStream = new FileInputStream(fileName);
                String StringFromInputStream = IOUtils.toString(inStream, "UTF-8");
                SESMessage sesmessage = new SESMessage();
    			sesmessage.setDestination(Arrays.asList(toStr.split(",")));
    			sesmessage.setMessage(subject, StringFromInputStream, false);
    			sesmessage.setFrom(SESMessage.NOREPLY_SENDER);
                SESEmail.getInstance().send(sesmessage);

            } catch (Throwable t) {
            	log4j.error("Unable to process file: " + fileName + ", Exception: " + t.getClass().getName() + " (" + t.getMessage() + ")");
            	return false;
            } finally {
                if(inStream != null)
                {
                    inStream.close();
                }
            }

		} catch (Exception e) {
			log4j.error("Unable to process Basket contents!");
			return false;
		} finally {
			try {
				if (xmlWriter != null) {
					xmlWriter.close();
					xmlWriter = null;
				}
				if (tmpWriter != null) {
					tmpWriter.close();
					tmpWriter = null;
				}
				// Clean up tmp file if present
				if (fileName != null) {
		            File file = new File(fileName);
		            file.delete();
				}
			} catch (Throwable t) {
				log4j.error("Error trying to clean up tmp file for email delivery");
			}
		}

		return true;
	}

	//
	//
	// GETTERS/SETTERS
	//
	//
	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isConfirm() {
		return confirm;
	}

	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}

}
