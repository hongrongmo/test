package org.ei.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.ws.Holder;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;
import org.ei.exception.InfrastructureException;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;
import org.ei.util.MD5Digester;

import com.elsevier.webservices.schemas.easi.headers.types.v1.RequestHeaderType;
import com.elsevier.webservices.schemas.easi.headers.types.v1.ResponseHeaderType;
import com.elsevier.webservices.schemas.ews.common.types.v2.DataResponseType;
import com.elsevier.webservices.schemas.metadata.abstracts.types.v10.AbsMetSourceType;
import com.elsevier.webservices.schemas.metadata.abstracts.types.v10.CitedByCountItemType;
import com.elsevier.webservices.schemas.metadata.abstracts.types.v10.CitedByCountType;
import com.elsevier.webservices.schemas.metadata.abstracts.types.v10.GetCitedByCountResponseType;
import com.elsevier.webservices.schemas.metadata.abstracts.types.v10.GetCitedByCountType;
import com.elsevier.webservices.schemas.metadata.abstracts.types.v10.GetLinkDataReqPayloadType;
import com.elsevier.webservices.schemas.metadata.common.types.v4.InputKeyType;
import com.elsevier.webservices.schemas.metadata.common.types.v4.MetaDataStatusCodeType;
import com.elsevier.webservices.schemas.metadata.common.types.v4.ResponseStyleType;
import com.elsevier.webservices.schemas.search.fast.types.v4.FSSStatusCodeType;
import com.elsevier.webservices.schemas.search.fast.types.v4.OrderByAttributesType;
import com.elsevier.webservices.schemas.search.fast.types.v4.OrderByListType;
import com.elsevier.webservices.schemas.search.fast.types.v4.ReturnAttributesType;
import com.elsevier.webservices.schemas.search.fast.types.v4.SearchDocumentType;
import com.elsevier.webservices.schemas.search.fast.types.v4.SearchRequestType;
import com.elsevier.webservices.schemas.search.fast.types.v4.SearchResponseType;
import com.elsevier.webservices.schemas.search.fast.types.v4.SearchType;
import com.elsevier.webservices.schemas.search.fast.types.v4.SortOrderType;
import com.elsevier.webservices.schemas.search.fast.types.v4.SummaryType;
import com.elsevier.webservices.schemas.search.fast.types.v4.ViaParamsListType;
import com.elsevier.webservices.wsdls.metadata.abstracts.service.v10.AbstractsMetadataServicePortTypeV10;
import com.elsevier.webservices.wsdls.search.fast.service.v4.FastSearchServicePortTypeV4;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author kamaramx
 *
 */
public class CitedByServiceImpl implements CitedByService {

	/** The Constant log4j. */
	private static final Logger log4j = Logger.getLogger(CitedByServiceImpl.class);

	/** The Constant citedBySaltKey. */
	private static final String citedBySaltKey = "Kyvs.FpdJvCAXVa:9TK13xB!a01ZV(iW";

	
	/** The key map. */
	private Hashtable<String, List<String>> newKeyMap = new Hashtable<String, List<String>>();


	/* (non-Javadoc)
	 * @see org.ei.service.CitedByService#getCitedByDetail(java.lang.String, java.lang.String)
	 */
	@Override
	public JSONArray getCitedByDetail(String eid, String doi)
			throws ServiceException,InfrastructureException {
		try {
			if (eid != null){
				eid = java.net.URLDecoder.decode(eid, "UTF-8");
				eid = eid.replaceAll("eid=", "");
			}

			if (doi != null){
				doi = java.net.URLDecoder.decode(doi, "UTF-8");
				eid = eid.replaceAll("doi=", "");
			}

			if (eid != null && eid.indexOf("::") > -1) {
                String[] eidArray = eid.split("::", -1);
                eid = eidArray[0];
                doi = eidArray[1];
            }
		} catch (UnsupportedEncodingException e) {
			throw new InfrastructureException(SystemErrorCodes.UNKNOWN_INFRASTRUCTURE_ERROR,"Unsupported encoding exception occured!.");
		}

		List<HashMap<String, String>>  resultList =   new ArrayList<HashMap<String, String>>();
		JSONArray jsonArray = new JSONArray();

		String xQuery = buildXQueryForDocSearch(eid,doi);
		List<SearchDocumentType> documentTypes = doFastSearch(xQuery);
   		resultList = parseDocumentSearch(documentTypes);

   		for(HashMap<String, String> mapElem : resultList) {
   			jsonArray.add(mapElem);
   		}
   		return jsonArray;
	}


	/**
	 * Do fast search.
	 *
	 * @param xQuery the x query
	 * @return the list
	 * @throws ServiceException the service exception
	 */
	private List<SearchDocumentType> doFastSearch(String xQuery) throws ServiceException{
		 List<SearchDocumentType> documentTypes = null;
		 FastSearchServicePortTypeV4 port = null;
		 try {
			 	SearchType searchType = new  SearchType();
				SearchRequestType requestType = new SearchRequestType();
				String[] reqFields = new String[9];
		   		reqFields[0] = "pubyr";
		   		reqFields[1] = "eid";
		   		reqFields[2] = "auth";
		   		reqFields[3] = "itemtitle";
		   		reqFields[4] = "numcitedby";
		   		reqFields[5] = "refeid";
		   		reqFields[6] = "dbdocid";
		   		reqFields[7] = "srctitle";
		   		reqFields[8] = "authid";


		   		OrderByAttributesType orderBy = new OrderByAttributesType();
		   		orderBy.setPath("pubyr");
		   		orderBy.setSortOrder(SortOrderType.DESCENDING);

		   		OrderByListType orderByList = new OrderByListType();
		   		orderByList.getOrderByAttributes().add(orderBy);

		   		ReturnAttributesType  returnType = new ReturnAttributesType();
		   		returnType.setStart(0);
		   		returnType.setMaxResults(10);
		   		requestType.setReturnAttributes(returnType);

		   		requestType.getReqFields().add(0, "pubyr");
		   		requestType.getReqFields().add(1, "eid");
		   		requestType.getReqFields().add(2, "auth");
		   		requestType.getReqFields().add(3, "itemtitle");
		   		requestType.getReqFields().add(4, "numcitedby");
		   		requestType.getReqFields().add(5, "refeid");
		   		requestType.getReqFields().add(6, "dbdocid");
		   		requestType.getReqFields().add(7, "srctitle");
		   		requestType.getReqFields().add(8, "authid");

		   		requestType.setXQueryX(xQuery.toString());
		   		requestType.setOrderByList(orderByList);

		   		requestType.setCluster("SCOPUS");

		   		ViaParamsListType viaParamsListType=new ViaParamsListType();
		   		viaParamsListType.getParamName().add(0,"scomode");
		   		viaParamsListType.getParamName().add(1,"scimode");
		   		viaParamsListType.getParamName().add(2,"mixer");

		   		viaParamsListType.getParamValue().add(0,"on");
		   		viaParamsListType.getParamValue().add(1,"off");
		   		viaParamsListType.getParamValue().add(2,"relevancy");

		   		requestType.setViaParamsList(viaParamsListType);


		   		searchType.getSearchReqPayload().add(requestType);

		   		Holder<SearchResponseType> responseHolder = new Holder<SearchResponseType>();
		   		RequestHeaderType reqHeader = FastSearchServiceHelper.getRequestHeaderHolder();
				Holder<ResponseHeaderType> respHeaderHolder = new Holder<ResponseHeaderType>();
				log4j.debug("Retrieving FSWS proxy...");
				port = FastSearchServiceHelper.getFastSearchService();
				port.search(searchType, reqHeader, responseHolder, respHeaderHolder);
				log4j.debug("Fast search results fetched successfully using Fast Search service....");
		   		if(responseHolder.value.getSearchRspPayload() != null && responseHolder.value.getSearchRspPayload().get(0) != null
						&& responseHolder.value.getSearchRspPayload().get(0).getStatus().getStatusCode() == FSSStatusCodeType.OK && responseHolder.value.getSearchRspPayload().get(0).getSearchDocument() != null){
					 documentTypes = responseHolder.value.getSearchRspPayload().get(0).getSearchDocument();
		   		}else if(responseHolder.value.getSearchRspPayload() != null && responseHolder.value.getSearchRspPayload().get(0) != null
						&& responseHolder.value.getSearchRspPayload().get(0).getStatus().getStatusCode() != FSSStatusCodeType.OK ){
		   			log4j.warn("Something gone wrong while fetching data from fast, status : "+responseHolder.value.getSearchRspPayload().get(0).getStatus().getStatusCode());
		   		}else{
		   			log4j.warn("Fast search was not successful.");
		   		}

		 }catch(Exception e){
			 log4j.error("Error occured while doing fast search : "+e.getMessage());
			 throw  new ServiceException(SystemErrorCodes.FSWS_SEARCH_FETCH_ERROR, "Error occured while doing fast search...");
		 }finally{
			 FastSearchServiceHelper.releasePort(port);
		 }
		return documentTypes;
	}

	/**
	 * Builds the x query for doc search.
	 *
	 * @param eid the eid
	 * @param doi the doi
	 * @return the string
	 */
	private String buildXQueryForDocSearch(String eid,String doi){
		StringBuffer xQuery = new StringBuffer();
		if(eid != null && eid.length()>0)
   		{
   			xQuery.append("<ft:fullTextQuery xmlns:ft=\"http://www.elsevier.com/2003/01/xqueryxFT-schema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.elsevier.com/2003/01/xqueryxFT-schema eql.xsd\"><ft:query><ft:word path=\"refeid\">");
			xQuery.append(eid);
		}
		else
		{
			xQuery.append("<ft:fullTextQuery xmlns:ft=\"http://www.elsevier.com/2003/01/xqueryxFT-schema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.elsevier.com/2003/01/xqueryxFT-schema eql.xsd\"><ft:query><ft:word path=\"doi\">");
			xQuery.append(doi);
		}

   		xQuery.append("</ft:word></ft:query></ft:fullTextQuery>");
		return xQuery.toString();
	}

	/**
	 * Builds the x query for author search.
	 *
	 * @param doi the doi
	 * @return the string
	 */
	private String buildXQueryForAuthorSearchUsingDOI(String doi){
		StringBuffer xQuery = new StringBuffer();
		xQuery.append("<ft:fullTextQuery xmlns:ft=\"http://www.elsevier.com/2003/01/xqueryxFT-schema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.elsevier.com/2003/01/xqueryxFT-schema eql.xsd\"><ft:query><ft:word path=\"doi\">");
		xQuery.append(doi);
		xQuery.append("</ft:word></ft:query></ft:fullTextQuery>");
		return xQuery.toString();
	}
	
	
	private String buildXQueryForAuthorSearchUsingISSNandISBN(String issn, String isbn, String isbn13, String volumn, String issue, String firstpage, String lastpage ){
		StringBuffer xQuery = new StringBuffer();
		xQuery.append("<ft:fullTextQuery xmlns:ft=\"http://www.elsevier.com/2003/01/xqueryxFT-schema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.elsevier.com/2003/01/xqueryxFT-schema eql.xsd\"><ft:query>");
		xQuery.append("<ft:andQuery>");
		if(issn != null && !issn.trim().equalsIgnoreCase("")){
			xQuery.append("<ft:word path=\"issn\">");
			xQuery.append(issn);
			xQuery.append("</ft:word>");
		}else if(isbn13 != null && !isbn13.trim().equalsIgnoreCase("")){
			xQuery.append("<ft:word path=\"isbn\">");
			xQuery.append(isbn13);
			xQuery.append("</ft:word>");
		}else if(isbn != null && !isbn.trim().equalsIgnoreCase("")){
			xQuery.append("<ft:word path=\"isbn\">");
			xQuery.append(isbn);
			xQuery.append("</ft:word>");
		}
		if(volumn != null && !volumn.trim().equalsIgnoreCase("")){
			xQuery.append("<ft:word path=\"vol\">");
			xQuery.append(volumn);
			xQuery.append("</ft:word>");
		}
		if(issue != null && !issue.trim().equalsIgnoreCase("")){
			xQuery.append("<ft:word path=\"issue\">");
			xQuery.append(issue);
			xQuery.append("</ft:word>");
		}
		if(firstpage != null && !firstpage.trim().equalsIgnoreCase("")){
			xQuery.append("<ft:word path=\"pgfirst\">");
			xQuery.append(firstpage);
			xQuery.append("</ft:word>");
		}
		// There is a error in last page incoming data, so far this is not used for document search
		/*if(lastpage != null && !lastpage.trim().equalsIgnoreCase("")){
			xQuery.append("<ft:word path=\"pglast\">");
			xQuery.append(lastpage);
			xQuery.append("</ft:word>");
		}*/
		xQuery.append("</ft:andQuery>");
		xQuery.append("</ft:query></ft:fullTextQuery>");
		return xQuery.toString();
	}

	/**
	 * Parses the document search.
	 *
	 * @param documentTypes the document types
	 * @return the list
	 */
	private List<HashMap<String, String>> parseDocumentSearch(List<SearchDocumentType> documentTypes){

		List<HashMap<String, String>>  resultList = new ArrayList<HashMap<String, String>>();

		if(documentTypes == null){
			return resultList;
		}

		for (SearchDocumentType documentType : documentTypes) {
			HashMap<String, String> fieldMap = new HashMap<String, String>();
			for (SummaryType summaryType : documentType.getSummary()) {
				String value = summaryType.getValue();
				String field = summaryType.getField();
				if(field.equals("pubyr"))
					{
					fieldMap.put("YEAR",value);
				}

				if(field.equals("eid"))
				{
					fieldMap.put("EID",value);
				}

				if(field.equals("auth"))
				{
					if(value!=null)
					{
						value = value.replaceAll("\\|",", ");

						value = StringEscapeUtils.escapeXml(value);

					}

					fieldMap.put("AUTHOR",value);
				}

				if(field.equals("authid"))
				{
					if(value!=null)
					{
						value = value.replaceAll(" \\| ",", ");
						value = (StringEscapeUtils.escapeXml(value)).trim();
					}
					fieldMap.put("AUTHORID",value);
				}

				if(field.equals("itemtitle") && value!=null)
				{
					value = value.replaceAll("\"", "'");
					value = StringEscapeUtils.escapeXml(value);
					fieldMap.put("TITLE",value);
				}

				if(field.equals("numcitedby"))
				{
					fieldMap.put("COUNT",value);
				}

				if(field.equals("refeid"))
				{
					fieldMap.put("REFEID",value);
				}

				if(field.equals("dbdocid"))
				{
					fieldMap.put("DOCID",value);
				}

				if(field.equals("srctitle") && value!=null)
				{
					value = StringEscapeUtils.escapeXml(value);
					fieldMap.put("SOURCETITLE",value);
				}
			}
			resultList.add(fieldMap);
		}
		return resultList;
	}


	/**
	 * @param documentTypes
	 * @return
	 */
	private HashMap<String, String> parseAuthSearchDetails(List<SearchDocumentType> documentTypes){
		HashMap<String, String> authorsMap = new HashMap<String, String>();
		SearchDocumentType documentType = documentTypes.get(0);
		for (SummaryType summaryType : documentType.getSummary()) {
			String value = summaryType.getValue();
			String field = summaryType.getField();
			if(field.equals("auth"))
			{
				if(value!=null)
				{
					value = value.replaceAll("\\|",", ");
					value = StringEscapeUtils.escapeXml(value);
				}
				authorsMap.put("PROFILE_AUTHOR",value);
			}
			if(field.equals("authid"))
			{
				if(value!=null)
				{
					value = value.replaceAll(" \\| ",", ");
					value = (StringEscapeUtils.escapeXml(value)).trim();
				}
				authorsMap.put("PROFILE_AUTHORID",value);
			}
		}
		return authorsMap;
	}
	

	/**
	 * @param citedBy
	 * @return
	 * @throws ServiceException
	 * @throws InfrastructureException
	 */
	public JSONObject getAuthorDetails(String citedBy) throws ServiceException,InfrastructureException{
		
		JSONObject jsonObject = null;
		
		if(citedBy == null || citedBy.length() ==0){
			return jsonObject;
		}
		String doiVal = null;
		String volumn = null; 
		String issue = null;
		String issn = null;
		String isbn = null;
		String isbn13 = null;
		String firstpage = null;
		String lastpage = null;
		List<CitedByCount> countList = parseInput(citedBy);
		if (countList != null) {
			CitedByCount count = (CitedByCount) countList.get(0);
			if(count != null && checkMD5(count)){
				doiVal = count.getDoi();
				volumn = count.getVol();
				issn = count.getIssn();
				isbn = count.getIsbn();
				isbn13 = count.getIsbn13();
				issue = count.getIssue();
				firstpage = count.getFirstPageNumber();
				lastpage = count.getLastPageNumber();
			}
		}
		String xQuery = null;
		if(doiVal != null && !doiVal.trim().equalsIgnoreCase("")){
			xQuery = buildXQueryForAuthorSearchUsingDOI(doiVal);
		}else if((issn != null && !issn.trim().equalsIgnoreCase("")) || (isbn != null && !isbn.trim().equalsIgnoreCase("")) || (isbn13 != null && !isbn13.trim().equalsIgnoreCase(""))){
			xQuery = buildXQueryForAuthorSearchUsingISSNandISBN(issn, isbn, isbn13, volumn, issue, firstpage, lastpage);
		}
		if(xQuery != null){
			List<SearchDocumentType> documentTypes = doFastSearch(xQuery);
			if(documentTypes != null && documentTypes.size()>0){
				if(documentTypes.size() > 1){
					log4j.warn("The author details search has more than one results, search criteria: doi="+doiVal+", issn="+issn+", isbn="+isbn+", isbn13="+isbn13+", vol="+volumn+" ,issue="+issue+", firstpage="+firstpage);
				}
				HashMap<String, String> hashMap = parseAuthSearchDetails(documentTypes);
				if(hashMap != null && hashMap.size()>0){
					jsonObject = new JSONObject(hashMap);
				}
			}
		}
		return jsonObject;
	}

	/* (non-Javadoc)
	 * @see org.ei.service.CitedByService#getCitedByCount(java.lang.String)
	 */
	public JSONArray getCitedByCount(String citedBy) throws ServiceException,InfrastructureException{
		JSONArray citedByCountArray = new JSONArray();
		if(citedBy == null || citedBy.length() ==0){
			return citedByCountArray;
		}
		
		 citedByCountArray = doXAbsMetadataSearch(citedBy);

		return citedByCountArray;
	}

	/**
	 * Do x abs metadata search.
	 *
	 * @return the jSON array
	 * @throws ServiceException the service exception
	 * @throws InfrastructureException
	 */
	private JSONArray doXAbsMetadataSearch(String citedBy) throws ServiceException, InfrastructureException{
		JSONArray citedByCountArray = new JSONArray();
		AbstractsMetadataServicePortTypeV10 port = null;
		List<CitedByCount> countList = parseInput(citedBy);

		try{
			 GetCitedByCountType getCitedByCountType = new GetCitedByCountType();
			 GetLinkDataReqPayloadType getCitedByCountReqPayload = new GetLinkDataReqPayloadType();

			 if (countList != null) {
				for (int i = 0; i < countList.size(); i++) {
					InputKeyType input = new InputKeyType();
					CitedByCount count = (CitedByCount) countList.get(i);
					if (checkMD5(count)) {
						if (StringUtils.isNotBlank(count.getDoi())) {
							String doiString = count.getDoi();
							input.setDoi(doiString.trim());
						} else if (StringUtils.isNotBlank(count.getPii())) {
							String piiString = count.getPii();
							input.setPii(piiString);
						} else {
							if (StringUtils.isNotBlank(count.getIssn())) {
								String issnString = count.getIssn();
								input.setIssn(issnString);
							}else if(StringUtils.isNotBlank(count.getIsbn13())){
								input.setIsbn(count.getIsbn13());
							}else if(StringUtils.isNotBlank(count.getIsbn())){
								input.setIsbn(count.getIsbn());
							}

							if (StringUtils.isNotBlank(count.getVol())) {
								String volumeString = count.getVol();
								input.setVol(volumeString);
							}

							
							
							if (StringUtils.isNotBlank(count.getIssue())) {
								String issueString = count.getIssue();
								input.setIssue(issueString);
							}

							if (StringUtils.isNotBlank(count.getFirstPageNumber())) {
								String pageString = count.getFirstPageNumber();
								input.setFirstPageNumber(pageString);
							}
						}
					}
					getCitedByCountReqPayload.getInputKey().add(input);
				}
				getCitedByCountReqPayload.setAbsMetSource(AbsMetSourceType.ALL);
				getCitedByCountReqPayload.setResponseStyle(ResponseStyleType.WELL_DEFINED);
				getCitedByCountReqPayload.setDataResponseStyle(DataResponseType.MESSAGE);
				getCitedByCountType.setGetCitedByCountReqPayload(getCitedByCountReqPayload);

				Holder<GetCitedByCountResponseType> responseHolder = new Holder<GetCitedByCountResponseType>();
				RequestHeaderType reqHeader = XAbstractMDServiceHelper.getRequestHeaderHolder();
				Holder<ResponseHeaderType> respHeaderHolder = new Holder<ResponseHeaderType>();
				log4j.debug("Retrieving XABS MD WS proxy...");
				port = XAbstractMDServiceHelper.getXABSMDService();
				Holder<DataHandler> holder = new Holder<DataHandler>();
				port.getCitedByCount(getCitedByCountType, reqHeader, responseHolder, holder,respHeaderHolder);
				log4j.debug("X AB metadata count(cited by) fetched successfully using XABSMD service....");

				if(responseHolder.value != null && responseHolder.value.getStatus().getStatusCode() == MetaDataStatusCodeType.OK &&
						responseHolder.value.getGetCitedByCountRspPayload() != null && responseHolder.value.getGetCitedByCountRspPayload().getCitedByCountList() != null){
					List<CitedByCountType> citedByCounts = responseHolder.value.getGetCitedByCountRspPayload().getCitedByCountList().getCitedByCount();
					if(citedByCounts != null){
						for(CitedByCountType  citedByCount : citedByCounts){
							if(citedByCount != null && citedByCount.getInputKey() != null){
								List<String> ids = getIDs(citedByCount.getInputKey());
								List<CitedByCountItemType> citedByCountItemTypes = citedByCount.getLinkData();
								if(citedByCountItemTypes != null && ids != null && !ids.isEmpty()){
									for(CitedByCountItemType  citedByCountItem : citedByCountItemTypes){
										if(citedByCountItem != null) {
											for(String id : ids){
												HashMap<String, String> linkMap = new HashMap<String, String>();
												linkMap.put("ID", id);
												linkMap.put("SID", (citedByCountItem.getScopusID()).toString());
												linkMap.put("EID", (citedByCountItem.getEid()).toString());
												linkMap.put("COUNT", (citedByCountItem.getCitedByCount()).toString());
												citedByCountArray.add(linkMap);
											}
										}
										
									}
								}
								
							}
						}
					}
				}else if(responseHolder.value != null && responseHolder.value.getStatus().getStatusCode() != MetaDataStatusCodeType.OK){
					log4j.warn("Something gone wrong while fetching data from x abs metadata service, status : "+responseHolder.value.getStatus().getStatusCode());
				}else{
					log4j.warn("X ABS METADATA service call was not successful.");
				}
			}
		}catch (Exception e) {
			log4j.error("Error occured while doing x abs metadata search : "+e.getMessage());
			 throw  new ServiceException(SystemErrorCodes.XABS_MD_FETCH_ERROR, "Error occured while doing x abs metadata search...");
		}finally{
			 XAbstractMDServiceHelper.releasePort(port);
		}
		return citedByCountArray;
	}

	/**
	 * Parses the input.
	 *
	 * @param inputArray the input array
	 * @throws InfrastructureException
	 */
	private List<CitedByCount> parseInput(String inputArray) throws InfrastructureException {
		String citedbyJSON;
		List<CitedByCount> citedByCountList = new ArrayList<CitedByCount>();
		try {
			citedbyJSON = java.net.URLDecoder.decode(inputArray, "UTF-8");
		}catch (UnsupportedEncodingException e) {
			throw new InfrastructureException(SystemErrorCodes.UNKNOWN_INFRASTRUCTURE_ERROR,"Unsupported encoding exception occured!.");
		}

		ObjectMapper mapper = new ObjectMapper();
		try {
			citedByCountList = Arrays.asList( mapper.readValue(citedbyJSON, CitedByCount[].class));
			//buildKeyMap(citedByCountList);
			buildNewKeyMap(citedByCountList);
		} catch (IOException e) {
			throw new InfrastructureException(SystemErrorCodes.UNKNOWN_INFRASTRUCTURE_ERROR,"Could not parse JSON to Java Object.");
		}
		return citedByCountList;
	}
	
	private void createId(String key, String accnum){
		List<String> idList = newKeyMap.get(key);
		if(idList == null){
			idList = new ArrayList<String>();
		}
		idList.add(accnum);
		newKeyMap.put(key, idList);
	}
	
	/**
	 * @param citedbyCountList
	 */
	private void buildNewKeyMap(List<CitedByCount> citedbyCountList) {
		for(CitedByCount count : citedbyCountList){
			if (count.getAccessionNumber() != null) {
				if (StringUtils.isNotBlank(count.getDoi())) {
					createId("key_doi"+count.getDoi(), count.getAccessionNumber());
					
				}else if(StringUtils.isNotBlank(count.getPii())) {
					createId("key_pii"+count.getPii(), count.getAccessionNumber());
					
				}else if (StringUtils.isNotBlank(count.getIssn()) || StringUtils.isNotBlank(count.getIsbn13()) || StringUtils.isNotBlank(count.getIsbn())){
					String ivipKey = "key";
					if(StringUtils.isNotBlank(count.getIssn())){
						ivipKey += "_issn"+count.getIssn();
					}else if(StringUtils.isNotBlank(count.getIsbn13())){
						ivipKey += "_isbn"+count.getIsbn13();
					}else if(StringUtils.isNotBlank(count.getIsbn())){
						ivipKey += "_isbn"+count.getIsbn();
					}
					if(StringUtils.isNotBlank(count.getVol())){
						ivipKey += "_vol"+count.getVol();
					}
					if(StringUtils.isNotBlank(count.getIssue())){
						ivipKey += "_issue"+count.getIssue();
					}
					if(StringUtils.isNotBlank(count.getFirstPageNumber())){
						ivipKey += "_firstpgnumber"+count.getFirstPageNumber();
					}
					createId(ivipKey, count.getAccessionNumber());
				}
			}
		}
	}


	/**
	 * Check m d5.
	 *
	 * @param count the count
	 * @return true, if successful
	 */
	private boolean checkMD5(CitedByCount count) {
		MD5Digester digester = new MD5Digester();
		String theDigest = null;
		boolean md5Flag = false;

		try {

			if (count.getSessionID() != null && count.getAccessionNumber() != null
					&& count.getMD5() != null) {

				theDigest = digester.asHex(digester.digest(count.getSessionID()
						+ count.getAccessionNumber() + citedBySaltKey));

				if (count.getMD5().equals(theDigest)) {
					md5Flag = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return md5Flag;
	}


	
	/**
	 * @param inputKey
	 * @return
	 */
	private List<String> getIDs(InputKeyType inputKey) {
		List<String> ids = null;
		if (inputKey.getDoi() != null) {
			String doi = "key_doi"+inputKey.getDoi();
			if (newKeyMap.containsKey(doi)) {
				ids = newKeyMap.get(doi);
			}

		} else if (inputKey.getPii() != null) {
			String pii = "key_pii"+inputKey.getPii();
			if (newKeyMap.containsKey(pii)) {
				ids = newKeyMap.get(pii);
			}
		}else if (inputKey.getIssn() != null || inputKey.getIsbn() != null ){
			String ivipKey = "key";
			if(inputKey.getIssn() != null){
				ivipKey += "_issn"+inputKey.getIssn();
			}else if(inputKey.getIsbn() != null){
				ivipKey += "_isbn"+inputKey.getIsbn();
			}
			if(inputKey.getVol() != null){
				ivipKey += "_vol"+inputKey.getVol();
			}
			if(inputKey.getIssue() != null){
				ivipKey += "_issue"+inputKey.getIssue();
			}
			if(inputKey.getFirstPageNumber() != null){
				ivipKey += "_firstpgnumber"+inputKey.getFirstPageNumber();
			}
			if (newKeyMap.containsKey(ivipKey)) {
				ids =  newKeyMap.get(ivipKey);
			}
		}
		if(ids == null){
			log4j.warn("The record has 'id' generated as null or list empty: doi="+inputKey.getDoi()+", issn="+inputKey.getIssn()+", isbn="+inputKey.getIsbn()+", vol="+inputKey.getVol()+" ,issue="+inputKey.getIssue()+", firstpage="+inputKey.getFirstPageNumber());
		}
		return ids;
	}


}
