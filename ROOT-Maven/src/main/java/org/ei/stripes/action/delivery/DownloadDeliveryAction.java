package org.ei.stripes.action.delivery;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sourceforge.stripes.action.After;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.apache.commons.validator.GenericValidator;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.biz.personalization.UserPrefs;
import org.ei.config.ApplicationProperties;
import org.ei.config.EVProperties;
import org.ei.domain.Abstract;
import org.ei.domain.BasketEntry;
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
import org.ei.domain.personalization.FolderEntry;
import org.ei.domain.personalization.FolderPage;
import org.ei.domain.personalization.SavedRecords;
import org.ei.download.util.ExcelExportUtil;
import org.ei.download.util.SaveToGoogleUsage;
import org.ei.exception.InfrastructureException;
import org.ei.session.UserSession;
import org.ei.stripes.action.SystemMessage;
import org.ei.stripes.adapter.BizXmlAdapter;
import org.ei.stripes.util.HttpRequestUtil;

import com.icl.saxon.TransformerFactoryImpl;

@UrlBinding("/delivery/download/{$event}.url")
public class DownloadDeliveryAction extends AbstractDeliveryAction {
    private final static Logger log4j = Logger.getLogger(DownloadDeliveryAction.class);
    protected String              downloadformat;
    protected StringBuffer        tmpfileName;


    /** The is referex only records. */
    private boolean referexOnlyRecords = false;

    private boolean saveToGoogleEnabled = false;

    private boolean saveToDropboxEnabled = false;

    private String dropBoxDownloadUrl = "";

    private String dropBoxClientid = "";

    private String downloadMedium = "";

	private String filenameprefix = "";

   	@DontValidate
    @HandlesEvent("display")
    public Resolution display() throws InfrastructureException {
        baseaddress = context.getRequest().getServerName();
        boolean isSaveToGoogleEnabled = false;
        boolean isSaveToDropboxEnabled = false;
        ApplicationProperties runtimeprops;
		try {
			runtimeprops = EVProperties.getApplicationProperties();
			isSaveToGoogleEnabled =  Boolean.parseBoolean(runtimeprops.getProperty("google.drive.enabled"));
			isSaveToDropboxEnabled = Boolean.parseBoolean(runtimeprops.getProperty("dropbox.save.enabled"));
		} catch (Exception e) {
			log4j.warn("Could not read the runtime property for 'google.drive.enabled' or 'dropbox.save.enabled', error occured! "+e.getMessage());
		}
        setSaveToGoogleEnabled(isSaveToGoogleEnabled);
        setSaveToDropboxEnabled(isSaveToDropboxEnabled);
        return new ForwardResolution("/WEB-INF/pages/customer/delivery/oneclickdl.jsp");
    }

	@DontValidate
    @HandlesEvent("dropbox")
    public Resolution dropbox() throws InfrastructureException {

		boolean isSaveToDropboxEnabled = false;
        ApplicationProperties runtimeprops;

		try {
			runtimeprops = EVProperties.getApplicationProperties();
			isSaveToDropboxEnabled =  Boolean.parseBoolean(runtimeprops.getProperty("dropbox.save.enabled"));
		} catch(Exception e) {
			log4j.warn("Error occured! "+e.getMessage());
			return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		}

		if(!isSaveToDropboxEnabled){
			log4j.warn("Save to Dropbox is not allowed!");
			return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		}

		UserSession userSession = context.getUserSession();
		if(userSession != null){
			userSession.setProperty("downloadformat", downloadformat);
			userSession.setProperty("dropBoxDownloadUrl", dropBoxDownloadUrl);
			userSession.setProperty("displayformat", displayformat);
			userSession.setProperty("filenameprefix", filenameprefix);
		}
        return new ForwardResolution("/WEB-INF/pages/customer/delivery/dropbox.jsp");
    }

	@DontValidate
    @HandlesEvent("dropboxredirect")
    public Resolution dropboxredirect() throws InfrastructureException {
		String clientId = "";
		boolean isSaveToDropboxEnabled = false;
        ApplicationProperties runtimeprops;
		try {
			runtimeprops = EVProperties.getApplicationProperties();
			clientId =  runtimeprops.getProperty("dropbox.client.id");
			isSaveToDropboxEnabled =  Boolean.parseBoolean(runtimeprops.getProperty("dropbox.save.enabled"));
		} catch(Exception e) {
			log4j.warn("Error occured! "+e.getMessage());
			return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		}

		if(!isSaveToDropboxEnabled){
			log4j.warn("Save to Dropbox is not allowed!");
			return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		}

        setDropBoxClientid(clientId);

        UserSession userSession = context.getUserSession();
		if(userSession != null){
			setDownloadformat(userSession.getProperty("downloadformat"));
			setDropBoxDownloadUrl(userSession.getProperty("dropBoxDownloadUrl"));
			setDisplayformat(userSession.getProperty("displayformat"));
			setFilenameprefix(userSession.getProperty("filenameprefix"));
		}

        return new ForwardResolution("/WEB-INF/pages/customer/delivery/dropbox.jsp");
    }
	@DontValidate
    @HandlesEvent("googledrive")
    public Resolution googleDrive() throws InfrastructureException {

		boolean isSaveToGoogleEnabled = false;
        ApplicationProperties runtimeprops;
		try {
			runtimeprops = EVProperties.getApplicationProperties();
			isSaveToGoogleEnabled =  Boolean.parseBoolean(runtimeprops.getProperty("google.drive.enabled"));
		} catch(Exception e) {
			log4j.warn("Error occured! "+e.getMessage());
			return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		}

		if(!isSaveToGoogleEnabled){
			log4j.warn("Save to Google Drive is not allowed!");
			return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		}

		UserSession userSession = context.getUserSession();
		if(userSession != null){
			userSession.setProperty("downloadformat", downloadformat);
			//userSession.setProperty("dropBoxDownloadUrl", dropBoxDownloadUrl);
			userSession.setProperty("displayformat", displayformat);
		}
        return new ForwardResolution("/WEB-INF/pages/customer/delivery/googledrive.jsp");
    }
    @DontValidate
    @HandlesEvent("submit")
    public Resolution submit() throws Exception {

    	// Ensure there are documents present
        if (GenericValidator.isBlankOrNull(docidlist) && GenericValidator.isBlankOrNull(folderid) && (basket == null || basket.countPages() == 0)) {
            log4j.warn("No documents to download!");
            return new ForwardResolution("/WEB-INF/pages/customer/delivery/download.jsp");
        } else if (GenericValidator.isBlankOrNull(downloadformat)) {
            log4j.warn("Download format must be selected.");
            return new ForwardResolution("/WEB-INF/pages/customer/delivery/download.jsp");
        }

        trackDriveUsage(downloadformat);

        // Set the docformat based on incoming displayformat
        String docformat = Citation.CITATION_FORMAT;
        if (!(DOWNLOAD_FORMAT_ASCII.equals(downloadformat)) && !(DOWNLOAD_FORMAT_CSV.equals(downloadformat))
        		&& !(DOWNLOAD_FORMAT_PDF.equals(downloadformat))
        		&& !(DOWNLOAD_FORMAT_RTF.equals(downloadformat))
        		&& !(DOWNLOAD_FORMAT_EXCEL.equals(downloadformat))) {
            docformat = "RIS";
        } else if ("detailed".equals(displayformat)) {
            docformat = FullDoc.FULLDOC_FORMAT;
        } else if ("abstract".equals(displayformat)) {
            docformat = Abstract.ABSTRACT_FORMAT;
        }

        //
        // Set the response content type and stylesheet name
        // based on the download format
        //
        String stylesheetname = "download";
        String contenttype = "application/x-no-such-app"; // Default
        if (DOWNLOAD_FORMAT_ASCII.equals(downloadformat)) {
            if ("abstract".equals(displayformat)) {
                stylesheetname += "AbstractAscii.xsl";
            } else if ("detailed".equals(displayformat)) {
                stylesheetname += "DetailedAscii.xsl";
            } else {
                stylesheetname += "CitationAscii.xsl";
            }
        } else if (DOWNLOAD_FORMAT_RIS.equals(downloadformat)) {
            contenttype = "application/x-research-info-systems";
            stylesheetname += "RISAscii.xsl";
        } else if (DOWNLOAD_FORMAT_BIBTEXT.equals(downloadformat)) {
            contenttype = "application/bibtex";
            stylesheetname += "BibTex.xsl";
        } else if (DOWNLOAD_FORMAT_REFWORKS.equals(downloadformat)) {
            stylesheetname += "RISAscii.xsl";
        }else if(DOWNLOAD_FORMAT_CSV.equals(downloadformat)){
        	//contenttype = "text/csv";
        	if ("abstract".equals(displayformat)) {
                stylesheetname += "AbstractCSV.xsl";
            } else if ("detailed".equals(displayformat)) {
                stylesheetname += "DetailedCSV.xsl";
            } else {
                stylesheetname += "CitationCSV.xsl";
            }
        }else if(DOWNLOAD_FORMAT_PDF.equals(downloadformat)){
        	if ("abstract".equals(displayformat)) {
                stylesheetname += "AbstractPDF.xsl";
            } else if ("detailed".equals(displayformat)) {
                stylesheetname += "DetailedPDF.xsl";
            } else {
                stylesheetname += "CitationPDF.xsl";
            }
        }else if(DOWNLOAD_FORMAT_RTF.equals(downloadformat)){
        	if ("abstract".equals(displayformat)) {
                stylesheetname += "AbstractRTF.xsl";
            } else if ("detailed".equals(displayformat)) {
                stylesheetname += "DetailedRTF.xsl";
            } else {
                stylesheetname += "CitationRTF.xsl";
            }
        }



        //
        // Start the transform process. This will do the following:
        // * Create a temp file name to hold download output
        // * Build XML input to be fed to a stylesheet
        // * Transform XML and output to temp file
        // * Stream contents back to response
        //
        Writer xmlWriter = null;
        PrintWriter tmpWriter = null;
        InputStream returnstream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        ByteArrayInputStream byteArrayInputStream = null;

        try {
            // Build the file name - NOTE that the tmpfileName var
            // is an instance variable so that the removeTmpFile()
            // method can attempt to delete it after the response!
            buildTmpFileName(docformat);

            //
            // Build the XML for transform
            //
            xmlWriter = buildXML(docformat);
            if (xmlWriter == null) { return SystemMessage.SYSTEM_ERROR_RESOLUTION; }

            if(DOWNLOAD_FORMAT_PDF.equals(downloadformat)){

            	byteArrayOutputStream = new ByteArrayOutputStream();

            	String appPath = context.getServletContext().getRealPath("");
            	FopFactory fopFactory = FopFactory.newInstance();
            	fopFactory.setBaseURL(appPath);
            	Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, byteArrayOutputStream);

            	String fopStyleSheet = this.getClass().getResource("/transform/delivery/" + stylesheetname).toString();
            	TransformerFactory tFactory = new TransformerFactoryImpl();
            	Transformer transformer = tFactory.newTransformer(new StreamSource(fopStyleSheet));
            	Result res = new SAXResult(fop.getDefaultHandler());
            	transformer.transform(new StreamSource(new StringReader(xmlWriter.toString().replaceAll(BizXmlAdapter.xml10_illegal_xml_pattern, ""))), res);

            	byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            	return new StreamingResolution("application/pdf", byteArrayInputStream).setFilename(tmpfileName.toString());
            }else if (DOWNLOAD_FORMAT_RTF.equals(downloadformat)){

            	byteArrayOutputStream = new ByteArrayOutputStream();
            	String appPath = context.getServletContext().getRealPath("");
            	FopFactory fopFactory = FopFactory.newInstance();
            	fopFactory.setBaseURL(appPath);
            	Fop fop = fopFactory.newFop(MimeConstants.MIME_RTF, byteArrayOutputStream);

            	String fopStyleSheet = this.getClass().getResource("/transform/delivery/" + stylesheetname).toString();
            	TransformerFactory tFactory = new TransformerFactoryImpl();
            	Transformer transformer = tFactory.newTransformer(new StreamSource(fopStyleSheet));
            	Result res = new SAXResult(fop.getDefaultHandler());
            	transformer.transform(new StreamSource(new StringReader(xmlWriter.toString().replaceAll(BizXmlAdapter.xml10_illegal_xml_pattern, ""))), res);

            	byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            	return new StreamingResolution("application/rtf", byteArrayInputStream).setFilename(tmpfileName.toString());
            }else if(DOWNLOAD_FORMAT_EXCEL.equals(downloadformat)){
            	XSSFWorkbook workbook = new XSSFWorkbook();
            	ExcelExportUtil excelExportUtil = new ExcelExportUtil(docformat);
            	excelExportUtil.createWorkBook(workbook, docformat, xmlWriter);
            	if(excelExportUtil.isReferexOnlyRecords()){
            		setReferexOnlyRecords(true);
            		log4j.warn("Referex database is not supported in XSLX download format!");
                    return new ForwardResolution("/WEB-INF/pages/customer/delivery/download.jsp");
            	}
            	byteArrayOutputStream = new ByteArrayOutputStream();
            	workbook.write(byteArrayOutputStream);
            	byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                return new StreamingResolution("application/ms-excel", byteArrayInputStream).setFilename(tmpfileName.toString());
            }

            //tmpWriter = new PrintWriter(new FileWriter("c:\\tmp\\" + tmpfileName.toString()));
            tmpWriter = new PrintWriter(new FileWriter("/tmp/" + tmpfileName.toString()));

            //
            // Transform XML
            //
            String stylesheet = this.getClass().getResource("/transform/delivery/" + stylesheetname).toString();
            TransformerFactory tFactory = new TransformerFactoryImpl();
            Templates templates = tFactory.newTemplates(new StreamSource(stylesheet));
            Transformer transformer = templates.newTransformer();
            transformer.transform(new StreamSource(new StringReader(xmlWriter.toString().replaceAll(BizXmlAdapter.xml10_illegal_xml_pattern, ""))), new StreamResult(tmpWriter));

            tmpWriter.close();

            //
            // Stream back the temp file
            //
            //returnstream = new FileInputStream("c:\\tmp\\" + tmpfileName.toString());
            returnstream = new FileInputStream("/tmp/" + tmpfileName.toString());
            // Only set the filename response header when NOT doing refworks!
            if (!DOWNLOAD_FORMAT_REFWORKS.equals(downloadformat)) {
                // String strFilename = getContentDispositionFilenameTimestamp(tmpfileName.toString());
                context.getResponse().setHeader("Content-Disposition", "attachment; filename=" + tmpfileName.toString() + "");
            }

            if (log4j.isInfoEnabled()) {
                //log4j.info("Streaming results from file: 'c:\\tmp\\" + tmpfileName.toString() + "'");
            	log4j.info("Streaming results from file: '/tmp/" + tmpfileName.toString() + "'");
            }
            return new StreamingResolution(contenttype, returnstream);

        } catch (Throwable t) {
            log4j.error("Unable to tranform output for download, error = " + t.getClass().getName() + ", message = " + t.getMessage());
            try {

            	// Only try to close this stream when there is an error. Stripes
                // will close it otherwise.

            	if (byteArrayOutputStream != null) {
                	byteArrayOutputStream.close();
                	byteArrayOutputStream = null;
                }

                if (byteArrayInputStream != null) {
                	byteArrayInputStream.close();
                	byteArrayInputStream = null;
                }

                if (returnstream != null) {
                    returnstream.close();
                    returnstream = null;
                }
            } catch (Throwable t2) {
                log4j.error("Unable to close returnstream! Error = " + t2.getClass().getName() + ", message = " + t2.getMessage());
            }
            return SystemMessage.SYSTEM_ERROR_RESOLUTION;
        } finally {
            try {

            	if (byteArrayOutputStream != null) {
                	byteArrayOutputStream.close();
                	byteArrayOutputStream = null;
                }

                if (byteArrayInputStream != null) {
                	byteArrayInputStream.close();
                	byteArrayInputStream = null;
                }

                if (xmlWriter != null) {
                    xmlWriter.close();
                    xmlWriter = null;
                }
                if (tmpWriter != null) {
                    tmpWriter.close();
                    tmpWriter = null;
                }
            } catch (Throwable t) {
                log4j.error("Unable to clean up! Error = " + t.getClass().getName() + ", message = " + t.getMessage());
            }
        }

    }

    /**
     * Builds the XML for the download.
     *
     * @param docformat
     * @param xmlWriter
     * @return
     */
    private Writer buildXML(String docformat) {

        Writer xmlWriter = null;

        try {
            xmlWriter = new StringWriter();

            xmlWriter.write("<PAGE>");

            // Build XML based on request params
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
                for (int i = 0; i < docidarr.length; i++) {
                    if (!GenericValidator.isBlankOrNull(docidarr[i])) {
                        handle = Integer.parseInt((String) handlearr[i]);
                        docidObj = new DocID(handle, docidarr[i].trim(), databaseConfig.getDatabase(docidarr[i].substring(0, 3)));
                        docidList.add(docidObj);

                    }
                }

                // Build page of data from doc/handle id lists
                MultiDatabaseDocBuilder builder = new MultiDatabaseDocBuilder();
                List<EIDoc> bList = builder.buildPage(docidList, docformat);
                PageEntryBuilder eBuilder = new PageEntryBuilder(this.getSessionid());
                List<PageEntry> pList = eBuilder.buildPageEntryList(bList);
                Page page1 = new Page();
                page1.addAll(pList);
                xmlWriter.write("<PAGE-RESULTS>");
                // Build XML
                for (int i = 0; i < page1.docCount(); i++) {
                    EIDoc doc = page1.docAt(i);
                    xmlWriter.write("<PAGE-ENTRY>");
                    doc.toXML(xmlWriter);
                    xmlWriter.write("</PAGE-ENTRY>");

                }
                xmlWriter.write("</PAGE-RESULTS>");

            } else if (!GenericValidator.isBlankOrNull(this.folderid)) {
                // ****************************************************************
                // Get documents from folder id (request param)
                // ****************************************************************

                // Retrieve folder (this.folder should already be
                // set by init() method from parent class)
                SavedRecords savedRecords = new SavedRecords();
                FolderPage folderpage = (FolderPage) savedRecords.viewRecordsInFolder(this.folderid, docformat);

                if (!DOWNLOAD_FORMAT_ASCII.equals(downloadformat) && !DOWNLOAD_FORMAT_CSV.equals(downloadformat) && !DOWNLOAD_FORMAT_PDF.equals(downloadformat)
                		&& !DOWNLOAD_FORMAT_RTF.equals(downloadformat) && !DOWNLOAD_FORMAT_EXCEL.equals(downloadformat)) {
                    for (int x = 0; x < folderpage.docCount(); x++) {
                        // walk folder selecting only records that match
                        // the db chosen for RIS
                        FolderEntry entryDoc = folderpage.docAt(x);
                        if (DOWNLOAD_FORMAT_RIS.equals(downloadformat) || DOWNLOAD_FORMAT_BIBTEXT.equals(downloadformat)
                            || DOWNLOAD_FORMAT_REFWORKS.equals(downloadformat)) {
                            entryDoc.toXML(xmlWriter);
                        }
                    }
                } else {
                    folderpage.toXML(xmlWriter);
                }

            } else if (basket != null && basket.countPages() > 0) {
                // ****************************************************************
                // Get documents from current basket
                // ****************************************************************
                BasketPage basketPage = null;
                for (int z = 1; z <= basket.countPages(); ++z) {
                    basketPage = basket.pageAt(z, docformat);
                    // if the download format is not ASCII
                    if (!DOWNLOAD_FORMAT_ASCII.equals(downloadformat) && !DOWNLOAD_FORMAT_CSV.equals(downloadformat) && !DOWNLOAD_FORMAT_PDF.equals(downloadformat)
                    		&& !DOWNLOAD_FORMAT_RTF.equals(downloadformat) && !DOWNLOAD_FORMAT_EXCEL.equals(downloadformat)) {
                        for (int x = 0; x < basketPage.docCount(); x++) {
                            BasketEntry be = basketPage.docAt(x);
                            if (DOWNLOAD_FORMAT_RIS.equals(downloadformat) || DOWNLOAD_FORMAT_BIBTEXT.equals(downloadformat)
                                || DOWNLOAD_FORMAT_REFWORKS.equals(downloadformat)) {
                                be.toXML(xmlWriter);
                            }
                        }
                    } else {
                        basketPage.toXML(xmlWriter);
                    }
                }

            } else {
                log4j.warn("Unable to process download - no way to retrieve documents!");
                return null;
            }

            // Signal footer section
            xmlWriter.write("<FOOTER/>");
            xmlWriter.write("</PAGE>");

            return xmlWriter;

        } catch (Throwable t) {
            log4j.error("Unable to tranform output for download, error = " + t.getClass().getName() + ", message = " + t.getMessage());
            return null;
        }

    }

    /**
     * Builds the temporary file name
     *
     * @param docformat
     */
    private void buildTmpFileName(String docformat) {
        if (tmpfileName == null) tmpfileName = new StringBuffer();

        if(filenameprefix == null || filenameprefix.trim().isEmpty() || filenameprefix.trim().length()<3){
        	filenameprefix = UserPrefs.DL_FILENAME_PFX;
        }
        tmpfileName.append(filenameprefix);
        tmpfileName.append("_");
        if(docformat.equalsIgnoreCase(FullDoc.FULLDOC_FORMAT)){
        	tmpfileName.append("detailed");
        }else{
        	if(DOWNLOAD_FORMAT_BIBTEXT.equals(downloadformat)){
        		tmpfileName.append("BIB");
        	}else{
        		tmpfileName.append(docformat);
        	}
        }

        tmpfileName.append("_");
        tmpfileName.append(downloadformat);

        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH)+1; // Note: zero based!
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        int millis = now.get(Calendar.MILLISECOND);
        tmpfileName.append("_");
        tmpfileName.append(month);
        tmpfileName.append("-");
        tmpfileName.append(day);
        tmpfileName.append("-");
        tmpfileName.append(year);
        tmpfileName.append("_");
        tmpfileName.append(hour);
        tmpfileName.append(minute);
        tmpfileName.append(second);
        tmpfileName.append(millis);

        if (DOWNLOAD_FORMAT_ASCII.equals(downloadformat)) {
            tmpfileName.append(".txt");
        } else if (DOWNLOAD_FORMAT_BIBTEXT.equals(downloadformat)) {
            tmpfileName.append(".bib");
        } else if (DOWNLOAD_FORMAT_CSV.equals(downloadformat)){
        	tmpfileName.append(".csv");
        } else if (DOWNLOAD_FORMAT_PDF.equals(downloadformat)){
        	tmpfileName.append(".pdf");
        } else if (DOWNLOAD_FORMAT_RTF.equals(downloadformat)){
        	tmpfileName.append(".rtf");
        }else if (DOWNLOAD_FORMAT_EXCEL.equals(downloadformat)){
        	tmpfileName.append(".xlsx");
        }else {
            tmpfileName.append(".ris");
        }
    }

    @After(stages = LifecycleStage.RequestComplete)
    /**
     * Removes any temporary file created during download.  NOTE
     * that it runs AFTER RequestComplete stage!
     */
    private void removeTmpFile() {
        // Clean up tmp file if present
        if (tmpfileName != null) {
            File file = new File("/tmp/" + tmpfileName.toString());
            file.delete();
        }
    }

    private void trackDriveUsage(String downloadFormat){
    	if(this.downloadMedium.equalsIgnoreCase("googledrive")){
    		String ip = HttpRequestUtil.getIP(context.getRequest());
    		UserSession usersession = context.getUserSession();
			if (usersession == null || usersession.getUser() == null) return;
			IEVWebUser evWebUser = usersession.getUser();
			if(evWebUser == null || evWebUser.getAccount() == null ) return;

    		String acctNo = evWebUser.getAccount().getAccountId();

    		SaveToGoogleUsage.trackUsage(downloadFormat,ip,acctNo);
    	}
    }

    //
    //
    // GETTERS/SETTERS
    //
    //
    public String getDownloadformat() {
        return downloadformat;
    }

    public void setDownloadformat(String downloadformat) {
        this.downloadformat = downloadformat;
    }

    public boolean isSaveToDropboxEnabled() {
		return saveToDropboxEnabled;
	}

	public void setSaveToDropboxEnabled(boolean saveToDropboxEnabled) {
		this.saveToDropboxEnabled = saveToDropboxEnabled;
	}

    public boolean isReferexOnlyRecords() {
		return referexOnlyRecords;
	}

    public String getDropBoxDownloadUrl() {
		return dropBoxDownloadUrl;
	}

	public void setDropBoxDownloadUrl(String dropBoxDownloadUrl) {
		this.dropBoxDownloadUrl = dropBoxDownloadUrl;
	}

	public String getDropBoxClientid() {
		return dropBoxClientid;
	}

	public void setDropBoxClientid(String dropBoxClientid) {
		this.dropBoxClientid = dropBoxClientid;
	}

	public void setReferexOnlyRecords(boolean referexOnlyRecords) {
		this.referexOnlyRecords = referexOnlyRecords;
	}

	public boolean isSaveToGoogleEnabled() {
		return saveToGoogleEnabled;
	}

	public void setSaveToGoogleEnabled(boolean saveToGoogleEnabled) {
		this.saveToGoogleEnabled = saveToGoogleEnabled;
	}

	public String getDownloadMedium() {
		return downloadMedium;
	}

	public void setDownloadMedium(String downloadMedium) {
		this.downloadMedium = downloadMedium;
	}


    public String getFilenameprefix() {
		return filenameprefix;
	}

	public void setFilenameprefix(String filenameprefix) {
		this.filenameprefix = filenameprefix;
	}

}
