package org.ei.ane.template;

import java.sql.Timestamp;

import javax.xml.transform.Templates;

import org.ei.exception.ServiceException;


public interface ANETemplatesService {

	public Templates getTemplateForNCEName( String nceName, Timestamp templateUpdateDate) throws ServiceException;
	
	
}
