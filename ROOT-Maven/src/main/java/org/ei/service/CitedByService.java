package org.ei.service;

import java.util.ArrayList;

import org.apache.wink.json4j.JSONArray;
import org.ei.exception.InfrastructureException;
import org.ei.exception.ServiceException;


/**
 * The Interface CitedByService.
 */
public interface CitedByService {

	public JSONArray getCitedByDetail(String eid,String doi) throws ServiceException,InfrastructureException;

	public JSONArray getCitedByCount(String citedby) throws ServiceException,InfrastructureException;
}
