package org.ei.service.cars.Impl;

import java.util.List;
import java.util.Map;

import org.ei.service.cars.CARSResponseStatus;
import org.ei.service.cars.PageType;



public class CARSResponse {

	private List<String> mimeList;
	private CARSResponseStatus responseStatus;
	private String sessionAffinity;
	private String athensURL;
    private String shibbolethURL;
    private String redirectURL;
	private String pageTitle;
	private PageType pageType;
	private String templateName;
	private String templateUpdateDate;
	private boolean flowComplete;
	private boolean carsCalled = true;
	private boolean force;
	private boolean pathChoice;
	private String nextRequestURI;
	private List<String> ssoURL;
	private String productId;
	private String carsCookie;
	private String pageContent;
	private String mailSubject;
	private String emailAddr;
	private String emailContent;
	private boolean ssoAuthResponse = false;
	private Map<String,String> textZoneMap;
	private boolean header = true;


	public boolean isSSOAuthResponse() {
		return ssoAuthResponse;
	}

	public void setSSOAuthResponse(boolean SSOAuthResponse) {
		this.ssoAuthResponse = SSOAuthResponse;
	}

	public String getSessionAffinity() {
		return sessionAffinity;
	}

    public String getAthensURL() {
		return athensURL;
	}

	public String getNextRequestURI() {
		return nextRequestURI;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public PageType getPageType() {
		return pageType;
	}

	public String getShibbolethURL() {
		return shibbolethURL;
	}

	public List<String> getSsoURL() {
		return ssoURL;
	}

	public String getTemplateName() {
		return templateName;
	}

	public String getTemplateUpdateDate() {
		return templateUpdateDate;
	}

	public boolean isFlowComplete() {
		return flowComplete;
	}

	public boolean isCarsCalled() {
		return carsCalled;
	}

	public void setCarsCalled(boolean carsCalled) {
		this.carsCalled = carsCalled;
	}

	public boolean isForce() {
		return force;
	}

	public void setSessionAffinity(String sessionAffinity) {
		this.sessionAffinity = sessionAffinity;
	}

	public void setAthensURL(String athenURL) {
		this.athensURL = athenURL;
	}

	public void setNextRequestURI(String nextReq) {
		this.nextRequestURI = nextReq;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public void setPageType(PageType pageType) {
		this.pageType = pageType;
	}

	public void setShibbolethURL(String shibURL) {
		shibbolethURL = shibURL;
	}

	public void setSsoURL(List<String> ssoUrls) {
		ssoURL = ssoUrls;
	}

	public void setTemplateUpdateDate(String templateDate) {
		templateUpdateDate = templateDate;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public void setFlowComplete(boolean flowComplete) {
		this.flowComplete = flowComplete;
	}

	public void setForce(boolean force) {
		this.force = force;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getCarsCookie() {
		return carsCookie;
	}

	public void setCarsCookie(String carsCookie) {
		this.carsCookie = carsCookie;
	}

    public String getPageContent() {
		return pageContent;
	}

    public void setPageContent(String pageContent) {
		this.pageContent = pageContent;
    }

    public String getSubject() {
		return mailSubject;
	}

    public void setSubject(String subject) {
		mailSubject = subject;
    }

    public String getUserEmail() {
		return emailAddr;
	}

    public void setUserEmail(String addr) {
		emailAddr = addr;
    }

    public String getEmailContent() {
		return emailContent;
	}

    public void setEmailContent(String emailContent) {
		this.emailContent = emailContent;
    }

	public Map<String, String> getTextZoneMap() {
		return textZoneMap;
	}

	public void setTextZoneMap(Map<String, String> textZoneMap) {
		this.textZoneMap = textZoneMap;
	}

	public List<String> getMimeList() {
		return mimeList;
	}

	public void setMimeList(List<String> mimeList) {
		this.mimeList = mimeList;
	}

	public CARSResponseStatus getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(CARSResponseStatus responseStatus) {
		this.responseStatus = responseStatus;
	}

    public boolean isHeader() {
        return this.header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    public boolean isPathChoice() {
        return pathChoice;
    }

    public void setPathChoice(boolean pathChoice) {
        this.pathChoice = pathChoice;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("************ CARS response ************ \n");

        builder.append("isCarsCalled:  " + this.isCarsCalled() + "\n");
        builder.append("isFlowComplete:  " + this.isFlowComplete() + "\n");
        builder.append("isForce:  " + this.isForce() + "\n");
        builder.append("isHeader:  " + this.isHeader() + "\n");
        builder.append("isPathChoice:  " + this.isPathChoice() + "\n");
        builder.append("isSSOAuthResponse:  " + this.isSSOAuthResponse() + "\n");

        builder.append("getAthensURL: " + this.getAthensURL()  + "\n");
        builder.append("getCarsCookie: " + this.getCarsCookie()  + "\n");
        builder.append("getEmailContent: " + this.getEmailContent()  + "\n");
        builder.append("getNextRequestURI: " + this.getNextRequestURI()  + "\n");
        builder.append("getPageContent: " + this.getPageContent()  + "\n");
        builder.append("getPageTitle: " + this.getPageTitle()  + "\n");
        builder.append("getProductId: " + this.getProductId()  + "\n");
        builder.append("getRedirectURL: " + this.getRedirectURL()  + "\n");
        builder.append("getSessionAffinity: " + this.getSessionAffinity()  + "\n");
        builder.append("getShibbolethURL: " + this.getShibbolethURL()  + "\n");
        builder.append("getSubject: " + this.getSubject()  + "\n");
        builder.append("getTemplateName: " + this.getTemplateName() + "\n");
        builder.append("getTemplateUpdateDate: " + this.getTemplateUpdateDate()  + "\n");
        builder.append("getUserEmail: " + this.getUserEmail()  + "\n");
        builder.append("getPageType: " + this.getPageType()  + "\n");
        builder.append("getResponseStatus: " + this.getResponseStatus().toString()  + "\n");
        builder.append("getSsoURL: " + this.getSsoURL()  + "\n");
        builder.append("getTextZoneMap: " + this.getTextZoneMap()  + "\n");

        if (this.getMimeList() != null) {
            for (int i=0; i < this.getMimeList().size(); i++) {
                builder.append("Mime list [" + i + "]: " + this.getMimeList().get(i) + "\n");
            }
        } else {
            builder.append("Mime list is empty!");
        }

        builder.append("\n************ End CARS response ************ \n");

        return builder.toString();
    }
}

