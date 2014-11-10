package org.ei.stripes.action.results;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.biz.email.SESEmail;
import org.ei.biz.email.SESMessage;
import org.ei.config.EVProperties;
import org.ei.exception.ServiceException;
import org.ei.stripes.action.EVActionBean;

/**
 * This class services LocalHoldings Links Local holdings link format:
 *
 * /search/results/localholding.url?Title=[ATITLE]&AUTHOR
 * =[AUFULL]&ISSN=[ISSN]&ISBN=[ISBN1]&Volume=[VOLUME]&Issue=[ISSUE]
 * &SerialTitle=[TITLE]&conftitle=[CTITLE]&source=[STITLE]&StartPage=[SPAGE]&Year=[YEAR]
 *
 * @author harovetm
 *
 */
@UrlBinding("/search/results/localholding.url")
public class LocalHoldingsAction extends EVActionBean {
    private final static Logger log4j = Logger.getLogger(LocalHoldingsAction.class);

    private String title;
    private String author;
    private String serialtitle;
    private String conftitle;
    private String source;
    private String issn;
    private String volume;
    private String issue;
    private String startpage;
    private String year;
    private String isbn;
    private String email;

    /**
     * Display the form
     * @return
     */
    @DontValidate
    @DefaultHandler
    public Resolution display() {
        HttpServletRequest request = context.getRequest();
        if (GenericValidator.isBlankOrNull(title)) {
            title = request.getParameter("Title");
        }
        if (GenericValidator.isBlankOrNull(author)) {
            author = request.getParameter("AUTHOR");
        }
        if (GenericValidator.isBlankOrNull(serialtitle)) {
            serialtitle = request.getParameter("SerialTitle");
        }
        if (GenericValidator.isBlankOrNull(issn)) {
            issn = request.getParameter("ISSN");
        }
        if (GenericValidator.isBlankOrNull(volume)) {
            volume = request.getParameter("Volume");
        }
        if (GenericValidator.isBlankOrNull(issue)) {
            issue = request.getParameter("Issue");
        }
        if (GenericValidator.isBlankOrNull(startpage)) {
            startpage = request.getParameter("StartPage");
        }
        if (GenericValidator.isBlankOrNull(year)) {
            year = request.getParameter("Year");
        }
        if (GenericValidator.isBlankOrNull(isbn)) {
            isbn = request.getParameter("ISBN");
        }
        log4j.info("Displaying local holding for: '" + title + "'");
        return new ForwardResolution("/WEB-INF/pages/customer/results/localholding.jsp");
    }

    /**
     * Handle the form submission
     * @return
     * @throws MessagingException
     */
    @DontValidate
    @HandlesEvent("submit")
    public Resolution submit() throws ServiceException {
        HttpServletRequest request = context.getRequest();
        String title = request.getParameter("ARTICLETITLE");
        String author = request.getParameter("AUTHOR");
        String stitle = request.getParameter("SERIALTITLE");
        String conftitle = request.getParameter("CONFTITLE");
        String source = request.getParameter("SOURCE");
        String issn = request.getParameter("ISSN");
        String isbn = request.getParameter("ISBN");
        String vol = request.getParameter("VOLUME");
        String issue = request.getParameter("ISSUE");
        String StartPage = request.getParameter("STARTPAGE");
        String year = request.getParameter("YEAR");
        String comments = request.getParameter("comments");

        StringBuffer message = new StringBuffer();

        if ((title != null) && (!title.trim().equals(""))) {
            message.append("ARTICLE TITLE: ");
            message.append(title);
            message.append("\r\n");

        }
        if ((author != null) && (!author.trim().equals(""))) {
            message.append("AUTHOR: ").append(author).append("\r\n");
        }
        if ((stitle != null) && (!stitle.trim().equals(""))) {
            message.append("SOURCE TITLE: ").append(stitle).append("\r\n");
        }
        if ((stitle == null) || (stitle.trim().equals(""))) {
            if ((source != null) && (!source.trim().equals(""))) {
                message.append("SOURCE: ").append(source).append("\r\n");
            }
        }
        if ((conftitle != null) && (!conftitle.trim().equals(""))) {
            message.append("CONFERENCE NAME: ").append(conftitle).append("\r\n");
        }
        if ((issn != null) && (!issn.trim().equals(""))) {
            message.append("ISSN:  ").append(issn).append("\r\n");
        }
        if ((isbn != null) && (!isbn.trim().equals(""))) {
            message.append("ISBN:  ").append(isbn).append("\r\n");
        }
        if ((vol != null) && (!vol.trim().equals(""))) {
            message.append("VOLUME:  ").append(vol).append("\r\n");
        }
        if ((issue != null) && (!issue.trim().equals(""))) {
            message.append("ISSUE:  ").append(issue).append("\r\n");
        }
        if ((StartPage != null) && (!StartPage.trim().equals(""))) {
            message.append("START PAGE:  ").append(StartPage).append("\r\n");
        }
        if ((year != null) && (!year.trim().equals(""))) {
            message.append("YEAR:   ").append(year).append("\r\n");
        }
        if ((comments != null) && (!comments.trim().equals(""))) {
            message.append("COMMENTS:   ").append(comments).append("\r\n");
        }

        // -------------------------- Create and Send Email ----------------------
        String to = request.getParameter("email");
        if (GenericValidator.isBlankOrNull(to)) {
            to = "library@lamrc.com";
        }

        String sender = EVProperties.getApplicationProperties().getProperty("SENDER_EMAIL_ADDRESS", "eicustomersupport@elsevier.com");
        SESMessage sesmessage = new SESMessage(to, sender, "Engineering Village Full text request", message.toString(),false);
        sesmessage.setReplyTo(request.getParameter("emailaddress"));
        log4j.info("Sending local holding email to: '" + to + "'");
        SESEmail.getInstance().send(sesmessage);

        return new ForwardResolution("/WEB-INF/pages/customer/results/localholdingsent.jsp");
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSerialtitle() {
        return this.serialtitle;
    }

    public void setSerialtitle(String serialtitle) {
        this.serialtitle = serialtitle;
    }

    public String getConftitle() {
        return this.conftitle;
    }

    public void setConftitle(String conftitle) {
        this.conftitle = conftitle;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getIssn() {
        return this.issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public String getVolume() {
        return this.volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getIssue() {
        return this.issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getStartpage() {
        return this.startpage;
    }

    public void setStartpage(String startpage) {
        this.startpage = startpage;
    }

    public String getYear() {
        return this.year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getIsbn() {
        return this.isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
