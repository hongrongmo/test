package org.ei.exception;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.validator.GenericValidator;

/**
 * XML representation of an EV Exception.
 * @author harovetm
 *
 */
@XmlRootElement(name="error")
public class ErrorXml {

	private int errorCode = SystemErrorCodes.UNKNOWN;
	private String errorMessage;
	private String baseexceptionclass;
	private String baseexceptionmessage;
	private String originexceptionclass;
	private String originexceptionmessage;

	/**
	 * Base constructor
	 */
	public ErrorXml() {		
	}
	
	/**
	 * Construct from EVBaseException
	 * @param e
	 */
	public ErrorXml(EVBaseException e) {
		if (e != null) {
			this.errorCode = e.getErrorCode();
			this.errorMessage = e.getMessage();
			
			if (e.getBaseException() != null) {
				this.baseexceptionclass = e.getBaseException().getClass().getName();
				this.baseexceptionmessage = e.getBaseException().getMessage();
			}
			
			if (e.getOriginatedException() != null) {
				this.originexceptionclass = e.getOriginatedException().getClass().getName();
				this.originexceptionmessage = e.getOriginatedException().getMessage();
			}
		} else {
			this.errorCode = SystemErrorCodes.UNKNOWN;
			this.errorMessage = "Invalid construction of ErrorXml - no Exception object!";
		}
	}
		
	@XmlElement
	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	@XmlElement
	public String getErrorMessage() {
		if (GenericValidator.isBlankOrNull(this.errorMessage)) {
			if (this.baseexceptionmessage != null) return this.baseexceptionmessage;
			else if (this.originexceptionmessage != null) return this.originexceptionmessage;
			else return "No message set for this Exception!";
		} else {
			return errorMessage;
		}
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@XmlElement
	public String getBaseExceptionClass() {
		return baseexceptionclass;
	}

	@XmlElement
	public String getBaseExceptionMessage() {
		return baseexceptionmessage;
	}

	@XmlElement
	public String getOriginExceptionClass() {
		return this.originexceptionclass;
	}

	@XmlElement
	public String getOriginExceptionMessage() {
		return this.originexceptionmessage;
	}

}