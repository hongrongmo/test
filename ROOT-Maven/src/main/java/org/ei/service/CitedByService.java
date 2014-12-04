package org.ei.service;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;
import org.ei.exception.InfrastructureException;
import org.ei.exception.ServiceException;


/**
 * The Interface CitedByService.
 * @author kamaramx
 *
 */
public interface CitedByService {

	public JSONArray getCitedByDetail(String eid,String doi) throws ServiceException,InfrastructureException;

	public JSONArray getCitedByCount(String citedby) throws ServiceException,InfrastructureException;
	
	public JSONObject getAuthorDetails(String citedby) throws ServiceException,InfrastructureException;
}
