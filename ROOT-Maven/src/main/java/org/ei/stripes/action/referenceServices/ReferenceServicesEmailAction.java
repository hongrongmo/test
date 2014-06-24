package org.ei.stripes.action.referenceServices;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.ei.config.EVProperties;
import org.ei.config.RuntimeProperties;
import org.ei.email.SESEmail;
import org.ei.email.SESMessage;
import org.ei.session.UserPreferences;
import org.ei.session.UserSession;
import org.ei.stripes.action.EVActionBean;
import org.ei.stripes.action.SystemMessage;
import org.ei.stripes.exception.EVExceptionHandler;

@UrlBinding("/askanexpert/email/{$event}.url")
public class ReferenceServicesEmailAction extends EVActionBean {

	public static final String ASK_AN_ENGINEER = "one";
	public static final String ASK_A_PRODUCTSPECIALIST = "two";
	public static final String ASK_A_LIBRARIAN = "three";

	private String section;
	private String sectionid;
	private String discipline;
	private String disciplineid;

	// Email form items
	private String guru;
	private String from_name;
	private String from_email;
	private String institution;
	private String message;
	private boolean share_question;

	private boolean success;
	
	/**
	 * Default handler - displays the ask an expert email form
	 * 
	 * @return Resolution
	 */
	@HandlesEvent("display")
	@DontValidate
	public Resolution display() {
		setRoom(ROOM.blank);

		return new ForwardResolution(
				"/WEB-INF/pages/customer/askanexpert/email.jsp");
	}

	/**
	 * Submits the email.
	 * 
	 * @return Resolution
	 */
	@HandlesEvent("submit")
	@DontValidate
	public Resolution submit() {
		setRoom(ROOM.blank);
		UserSession usersession = context.getUserSession();
		String refEmail = usersession.getUserTextZones().get(UserPreferences.TZ_REFERENCE_SERVICES_LINK);
		if (GenericValidator.isBlankOrNull(refEmail) || !GenericValidator.isEmail(refEmail)) {
		    refEmail = EVProperties.getRuntimeProperty(RuntimeProperties.LIBRARIAN_EMAIL);
		}
		
		List<String> recipients = new ArrayList<String>();
		if (ASK_AN_ENGINEER.equals(sectionid)) {
			// ask an engineer email
			recipients.add(EVProperties.getRuntimeProperty(RuntimeProperties.ENGINEER_EMAIL));
		} else if (ASK_A_PRODUCTSPECIALIST.equals(sectionid)) {
			recipients.add(EVProperties.getRuntimeProperty(RuntimeProperties.SPECIALIST_EMAIL));
		} else if (ASK_A_LIBRARIAN.equals(sectionid)) {
			recipients.add(refEmail);
		}

		
		SESMessage sesmessage = new SESMessage();
		sesmessage.setFrom(SESMessage.DEFAULT_SENDER);
		sesmessage.setDestination(recipients);
		try {
			
			Writer messagebody = new StringWriter();
			if (ASK_AN_ENGINEER.equals(sectionid)) {
				messagebody.write(section);
				messagebody.write("\n");
				messagebody.write(discipline);
				messagebody.write("\n");
				messagebody.write(guru);
				messagebody.write("\n");
			}

			if (!GenericValidator.isBlankOrNull(refEmail)) {
				messagebody.write("Share: ");
				if (!share_question) {
					messagebody
							.write(" User has requested we DO NOT share this question.");
				} else {
					messagebody
							.write(" Anonymously share this question and response with other Engineering Village users");
				}
			}
			messagebody.write("\n");
			messagebody.write("Message contents");
			messagebody.write("\n");
			messagebody.write("From: ");
			messagebody.write(from_name);
			messagebody.write("\n");
			messagebody.write("Institution: ");
			messagebody.write(institution);
			messagebody.write("\n");
			messagebody.write("From email: ");
			messagebody.write(from_email);
			messagebody.write("\n");
			messagebody.write("Message: \n");
			messagebody.write(message);
			messagebody.write("\n");
			messagebody
					.write("---------------------------------------------------");
			messagebody.write("\n");
			// if email is coming to default ei.org address, add user info
			if (EVProperties.getRuntimeProperty(RuntimeProperties.LIBRARIAN_EMAIL).equals(refEmail)) {
				messagebody.write(usersession.getUser().toString());
			} else {
				messagebody
						.write("This email was sent to you via Engineering Village Ask a Librarian feature.");
			}
			messagebody.write("\n");
			
			sesmessage.setMessage(section, messagebody.toString(), false);

			SESEmail.getInstance().send(sesmessage);
			
			// Signals showing the confirmation page!
			success = true;
		} catch (Exception e) {
			EVExceptionHandler.logException("Unable to send email from Ask an Expert!", e, context.getRequest());
			return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		}

		return new ForwardResolution("/WEB-INF/pages/customer/askanexpert/email.jsp");
	}

	//
	//
	// GETTERS/SETTERS
	//
	//
	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getSectionid() {
		return sectionid;
	}

	public void setSectionid(String sectionid) {
		this.sectionid = sectionid;
	}

	public String getDiscipline() {
		return discipline;
	}

	public void setDiscipline(String discipline) {
		this.discipline = discipline;
	}

	public String getDisciplineid() {
		return disciplineid;
	}

	public String getGuru() {
		return guru;
	}

	public void setGuru(String guru) {
		this.guru = guru;
	}

	public void setDisciplineid(String disciplineid) {
		this.disciplineid = disciplineid;
	}

	public String getFrom_name() {
		return from_name;
	}

	public void setFrom_name(String from_name) {
		this.from_name = from_name;
	}

	public String getFrom_email() {
		return from_email;
	}

	public void setFrom_email(String from_email) {
		this.from_email = from_email;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isShare_question() {
		return share_question;
	}

	public void setShare_question(boolean share_question) {
		this.share_question = share_question;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
