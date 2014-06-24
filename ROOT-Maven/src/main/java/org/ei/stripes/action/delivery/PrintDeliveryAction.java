package org.ei.stripes.action.delivery;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
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
import org.ei.exception.InfrastructureException;
import org.ei.session.UserSession;

import com.icl.saxon.TransformerFactoryImpl;

@UrlBinding("/delivery/print/{$event}.url")
public class PrintDeliveryAction extends AbstractDeliveryAction {
	private final static Logger log4j = Logger.getLogger(PrintDeliveryAction.class);

	private String docformat;
	
	@DontValidate
	@HandlesEvent("display")
	public Resolution display() throws InfrastructureException {
		// Set the docformat based on incoming displayformat
		docformat = Citation.CITATION_FORMAT;
		if ("detailed".equals(displayformat)) {
			docformat = FullDoc.FULLDOC_FORMAT;
		} else if ("abstract".equals(displayformat)) {
			docformat = Abstract.ABSTRACT_FORMAT;
		}
		
		return new ForwardResolution("/WEB-INF/pages/customer/delivery/print.jsp");
	}

	/**
	 * Builds the XML for the download.
	 * 
	 * @param docformat
	 * @param xmlWriter
	 * @return
	 */
	public List<EIDocWrapper> getDocs() {
		if (GenericValidator.isBlankOrNull(docformat)) {
			throw new IllegalArgumentException("Document format has not been set!");
		}
        
		UserSession usersession = context.getUserSession();
		List<EIDocWrapper> docwrapperList = new ArrayList<EIDocWrapper>();
		
        try {

			// Build doc List based on request params
			if (!GenericValidator.isBlankOrNull(docidlist)) {
				// ****************************************************************
				// Get documents from docid list (request param)
				// ****************************************************************
				DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

				// Get the docids and handles
				String[] docidarr = docidlist.split(",");
				String[] handlearr = handlelist.split(",");
				if (docidarr.length != handlearr.length) {
					log4j.warn("docids size does not equal handles size!");
					return null;
				}
	
				// Add docids to list
				List<DocID> docidList = new ArrayList<DocID>();
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

			    // Build EIDocWrapper list
			    for (int i = 0; i < page1.docCount(); i++) {
			    	docwrapperList.add(new EIDocWrapper(page1.docAt(i), i+1));
				}

			} else if (!GenericValidator.isBlankOrNull(folderid)) {
				// ****************************************************************
				// Get documents from folder id (request param)
				// ****************************************************************

				// Retrieve folder (this.folder should already be 
				// set by init() method from parent class)
			    SavedRecords savedRecords = new SavedRecords(usersession.getUser().getUserId());
			    FolderPage folderpage = (FolderPage) savedRecords.viewRecordsInFolder(this.folderid, docformat);
			    for (int i=0; i<folderpage.docCount(); i++) {
			    	docwrapperList.add(new EIDocWrapper(folderpage.docAt(i).getEIDoc(), i+1));
			    }

			} else if (basket != null && basket.countPages() > 0) {
				// ****************************************************************
				// Get documents from current basket
				// ****************************************************************
				BasketPage basketPage = null;
				int index = 0;
				for (int z = 1; z <= basket.countPages(); ++z) {
					basketPage = basket.pageAt(z, docformat);
				    for (int i=0; i<basketPage.docCount(); i++) {
				    	docwrapperList.add(new EIDocWrapper(basketPage.docAt(i).getEIDoc(), ++index));
				    }
				} 

			} else {
				log4j.warn("Unable to process download - no way to retrieve documents!");
				return null;
			}

			return docwrapperList;
			
		} catch (Throwable t) {
			log4j.error("Unable to build docwrapper list, error = " + t.getClass().getName() + ", message = " + t.getMessage());
			return null;
		}
		
	}
	

	/**
	 * Inner class to wrap EIDoc object for output on JSP
	 * @author harovetm
	 *
	 */
	public static class EIDocWrapper {
		private EIDoc eidoc;
		private int index;
		/**
		 * Must be constructed with EIDoc as input
		 * @param eidoc
		 */
		public EIDocWrapper(EIDoc eidoc, int index) {
			this.eidoc = eidoc;
			this.index = index;
		}
		
		/**
		 * Convert EIDoc to XML
		 * @return
		 */
		public String getXml() {
			if (eidoc == null) {
				return "";
			}
			
			// Build XML
			Writer xmlWriter = null;
			try {
				xmlWriter = new StringWriter();
				xmlWriter.write("<PAGE-ENTRY>");
				xmlWriter.write("<DOCUMENTBASKETHITINDEX>"+index+"</DOCUMENTBASKETHITINDEX>");
				eidoc.toXML(xmlWriter);
				xmlWriter.write("</PAGE-ENTRY>");
				String xml =  xmlWriter.toString();
				return xml;
			} catch (Throwable t) {
				log4j.error("Unable to build XML for document! Error = " + t.getClass().getName() + ", message = " + t.getMessage());
				return "";
			} finally {
				try {
					if (xmlWriter != null) {
						xmlWriter.close();
						xmlWriter = null;
					}
				} catch (Throwable t) {
					log4j.error("Unable to clean up! Error = " + t.getClass().getName() + ", message = " + t.getMessage());
				}
			}
		}
		
		/**
		 * Transform current document XML to HTML
		 * @return
		 */
		public String getTransform() {
			//
			// Transform XML
			//
			Writer htmlWriter = null;
			String xslt = null;
			try {
				htmlWriter = new StringWriter();
				if (Citation.CITATION_FORMAT.equals(this.eidoc.getFormat())) {
					xslt = this.getClass().getResource("/transform/delivery/PrintCitationFormat.xsl").toString();
				} else if (Abstract.ABSTRACT_FORMAT.equals(this.eidoc.getFormat())) {
					xslt = this.getClass().getResource("/transform/delivery/PrintAbstractFormat.xsl").toString();
				} else if ("detailed".equals(this.eidoc.getFormat()) || FullDoc.FULLDOC_FORMAT.equals(this.eidoc.getFormat())) {
					xslt = this.getClass().getResource("/transform/delivery/PrintDetailedFormat.xsl").toString();
				}
				TransformerFactory tFactory = new TransformerFactoryImpl();
				Templates templates = tFactory.newTemplates(new StreamSource(xslt));
				Transformer transformer = templates.newTransformer();
				transformer.transform(new StreamSource(new StringReader(this.getXml())),
				          new StreamResult(htmlWriter));
				String html = htmlWriter.toString();
				return html;
			} catch (Throwable t) {
				log4j.error("Unable to transform XML for document! Error = " + t.getClass().getName() + ", message = " + t.getMessage());
				return "";
			} finally {
				try {
					if (htmlWriter != null) {
						htmlWriter.close();
						htmlWriter = null;
					}
				} catch (Throwable t) {
					log4j.error("Unable to clean up! Error = " + t.getClass().getName() + ", message = " + t.getMessage());
				}
			}
			
		}
	}
}
