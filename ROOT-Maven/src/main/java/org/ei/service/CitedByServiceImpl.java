package org.ei.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.ws.Holder;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.wink.json4j.JSONArray;
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

/**
 * The Class CitedByServiceImpl.
 */
public class CitedByServiceImpl implements CitedByService {

	/** The Constant log4j. */
	private static final Logger log4j = Logger.getLogger(CitedByServiceImpl.class);
	
	/** The Constant citedBySaltKey. */
	private static final String citedBySaltKey = "Kyvs.FpdJvCAXVa:9TK13xB!a01ZV(iW";
	
	/** The count list. */
	private List<CitedByCount> countList = new ArrayList<CitedByCount>();
	
	/** The key map. */
	private Hashtable<String, String> keyMap = new Hashtable<String, String>();
	
	
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
   		
   		if(doi != null && doi.length()>0 && resultList.size()>0){
   			xQuery = buildXQueryForAuthorSearch(doi);
   			documentTypes = doFastSearch(xQuery);
   			resultList = parseAuthSearch(documentTypes, resultList);
   		}
   		
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
	private String buildXQueryForAuthorSearch(String doi){
		StringBuffer xQuery = new StringBuffer();
		xQuery.append("<ft:fullTextQuery xmlns:ft=\"http://www.elsevier.com/2003/01/xqueryxFT-schema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.elsevier.com/2003/01/xqueryxFT-schema eql.xsd\"><ft:query><ft:word path=\"doi\">");
		xQuery.append(doi);
		xQuery.append("</ft:word></ft:query></ft:fullTextQuery>");
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
	 * Parses the auth search.
	 *
	 * @param documentTypes the document types
	 * @param resultList the result list
	 * @return the list
	 */
	private List<HashMap<String, String>> parseAuthSearch(List<SearchDocumentType> documentTypes,List<HashMap<String, String>>  resultList){
		if(documentTypes == null){
			return resultList;
		}
		
		HashMap<String, String> firstDoc = resultList.get(0);
		
		if(firstDoc != null){
			for (SearchDocumentType documentType : documentTypes) {
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

						firstDoc.put("PROFILE_AUTHOR",value);

					}

					if(field.equals("authid"))
					{
						if(value!=null)
						{
							value = value.replaceAll(" \\| ",", ");
							value = (StringEscapeUtils.escapeXml(value)).trim();
						}
						firstDoc.put("PROFILE_AUTHORID",value);

					}
				}
			}
		}
		return resultList;
	}
	
	/* (non-Javadoc)
	 * @see org.ei.service.CitedByService#getCitedByCount(java.lang.String)
	 */
	public JSONArray getCitedByCount(String citedBy) throws ServiceException,InfrastructureException{
		JSONArray citedByCountArray = new JSONArray();
		if(citedBy == null || citedBy.trim().length()==0){
			return citedByCountArray;
		}
		try {
			citedBy =  java.net.URLDecoder.decode(citedBy, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new InfrastructureException(SystemErrorCodes.UNKNOWN_INFRASTRUCTURE_ERROR,"Unsupported encoding exception occured!.");
		}
		
		 String[] inputArray;
		 
		 if (citedBy.indexOf("::") < 0) {
             inputArray = new String[1];
             inputArray[0] = citedBy;
         } else {
             inputArray = citedBy.split("::", -1);
         }
		 
		 parseInput(inputArray);
		
		 citedByCountArray = doXAbsMetadataSearch();
		 
		return citedByCountArray;
	}
	
	/**
	 * Do x abs metadata search.
	 *
	 * @return the jSON array
	 * @throws ServiceException the service exception
	 */
	private JSONArray doXAbsMetadataSearch() throws ServiceException{
		JSONArray citedByCountArray = new JSONArray();
		AbstractsMetadataServicePortTypeV10 port = null;
		try{
			 GetCitedByCountType getCitedByCountType = new GetCitedByCountType();
			 GetLinkDataReqPayloadType getCitedByCountReqPayload = new GetLinkDataReqPayloadType();
			 
			 if (countList != null) {
				for (int i = 0; i < countList.size(); i++) {
					InputKeyType input = new InputKeyType();
					CitedByCount count = (CitedByCount) countList.get(i);
					if (checkMD5(count)) {
						if (count.getDoi() != null && count.getDoi().length() > 0) {
							String doiString = count.getDoi();
							input.setDoi(doiString.trim());
						} else if (count.getPii() != null && count.getPii().length() > 0) {
							String piiString = count.getPii();
							input.setPii(piiString);
						} else {
							if (count.getIssn() != null && count.getIssn().length() > 0) {
								String issnString = count.getIssn();
								input.setIssn(issnString);
							}

							if (count.getVol() != null && count.getVol().length() > 0) {
								String volumeString = count.getVol();
								input.setVol(volumeString);
							}

							if (count.getIssue() != null && count.getIssue().length() > 0) {
								String issueString = count.getIssue();
								input.setIssue(issueString);
							}

							if (count.getFirstPageNumber() != null
									&& count.getFirstPageNumber().length() > 0) {
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
								String id = getID(citedByCount.getInputKey());
								List<CitedByCountItemType> citedByCountItemTypes = citedByCount.getLinkData();
								if(citedByCountItemTypes != null){
									for(CitedByCountItemType  citedByCountItem : citedByCountItemTypes){
										if(citedByCountItem != null && id != null) {
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
	 */
	private void parseInput(String[] inputArray) {

		if (inputArray != null) {
			for (int i = 0; i < inputArray.length; i++) {
				String param = inputArray[i];

				if (param != null) {
					CitedByCount count = new CitedByCount();

					String[] paramArray;
					if (param.indexOf("|") < 0) {
						paramArray = new String[1];
						paramArray[0] = param;
					} else {
						paramArray = param.split("\\|", -1);
					}

					for (int j = 0; j < paramArray.length; j++) {

						String singleParam = paramArray[j];
						// System.out.println("singleParam1 "+j+"  "+singleParam);

						if (singleParam.indexOf("AN:") > -1) {
							// System.out.println("singleParam "+i+"  "+singleParam);
							count.setAccessionNumber(singleParam.substring(singleParam.indexOf(":") + 1));
						}

						if (singleParam.indexOf("DOI:") > -1) {
							//System.out.println("singleParam "+i+"  "+singleParam);
							count.setDoi(singleParam.substring(singleParam.indexOf(":") + 1));
						}

						if (singleParam.indexOf("PII:") > -1) {
							// System.out.println("singleParam "+i+"  "+singleParam);
							count.setPii(singleParam.substring(singleParam.indexOf(":") + 1));
						}

						if (singleParam.indexOf("PAGE:") > -1) {
							// System.out.println("singleParam "+i+"  "+singleParam);
							count.setFirstPageNumber(singleParam.substring(singleParam.indexOf(":") + 1));
						}

						if (singleParam.indexOf("VOLUME:") > -1) {
							// System.out.println("singleParam "+i+"  "+singleParam);
							count.setVol(singleParam.substring(singleParam.indexOf(":") + 1));
						}

						if (singleParam.indexOf("ISSUE:") > -1) {
							// System.out.println("singleParam "+i+"  "+singleParam);
							count.setIssue(singleParam.substring(singleParam.indexOf(":") + 1));
						}

						if (singleParam.indexOf("ISSN:") > -1) {
							// System.out.println("singleParam "+i+"  "+singleParam);
							count.setIssn(singleParam.substring(singleParam.indexOf(":") + 1));
						}

						if (singleParam.indexOf("firstAuthorSurname:") > -1) {
							// System.out.println("singleParam "+i+"  "+singleParam);
							count.setFirstAuthorSurname(singleParam.substring(singleParam.indexOf(":") + 1));
						}

						if (singleParam.indexOf("firstInitialFirstAuthor:") > -1) {
							// System.out.println("singleParam "+i+"  "+singleParam);
							count.setFirstInitialFirstAuthor(singleParam.substring(singleParam.indexOf(":") + 1));
						}

						if (singleParam.indexOf("yearOfPublication:") > -1) {
							// System.out.println("singleParam "+i+"  "+singleParam);
							count.setYearOfPublication(singleParam.substring(singleParam.indexOf(":") + 1));
						}

						if (singleParam.indexOf("firstPageNumber:") > -1) {
							// System.out.println("singleParam "+i+"  "+singleParam);
							count.setFirstPageNumber(singleParam.substring(singleParam.indexOf(":") + 1));
						}

						if (singleParam.indexOf("lastPageNumber:") > -1) {
							// System.out.println("singleParam "+i+"  "+singleParam);
							count.setLastPageNumber(singleParam.substring(singleParam.indexOf(":") + 1));
						}

						if (singleParam.indexOf("S:") > -1) {
							// System.out.println("MD5 "+i+"  "+singleParam);
							count.setMD5(singleParam.substring(singleParam.indexOf(":") + 1));
						}

						if (singleParam.indexOf("SID:") > -1) {
							// System.out.println("singleParam "+i+"  "+singleParam);
							count.setSessionID(singleParam.substring(singleParam.indexOf(":") + 1));
						}

						buildKeyMap(count);

					}

					countList.add(count);
				}
			}
		}

	}
	
	/**
	 * Builds the key map.
	 *
	 * @param count the count
	 */
	private void buildKeyMap(CitedByCount count) {
		if (count.getAccessionNumber() != null) {
			if (count.getDoi() != null) {
				keyMap.put(count.getDoi(), count.getAccessionNumber());
			}

			if (count.getPii() != null) {
				keyMap.put(count.getPii(), count.getAccessionNumber());
			}

			if (count.getIssn() != null && count.getFirstPageNumber() != null
					&& count.getIssue() != null && count.getVol() != null) {
				String ivipKey = buildIvipKey(count.getIssn(), count.getFirstPageNumber(), count.getIssue(), count.getVol());
				keyMap.put(ivipKey, count.getAccessionNumber());
			}

		}
	}
	
	/**
	 * Builds the ivip key.
	 *
	 * @param issn the issn
	 * @param page the page
	 * @param issue the issue
	 * @param volume the volume
	 * @return the string
	 */
	private String buildIvipKey(String issn, String page, String issue,
			String volume) {
		String ivipKey = null;
		if (issn != null && issn.indexOf("-") > -1) {
			issn = issn.replaceAll("-", "");
		}

		ivipKey = issn + "_" + page + "_" + issue + "_" + volume;

		return ivipKey;

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
	 * Gets the id.
	 *
	 * @param inputKey the input key
	 * @return the id
	 */
	private String getID(InputKeyType inputKey) {
		String id = null;
		if (inputKey.getDoi() != null) {
			String doi = inputKey.getDoi();
			if (keyMap.containsKey(doi)) {
				id = (String) keyMap.get(doi);
			}

		} else if (inputKey.getPii() != null) {
			String pii = inputKey.getPii();
			if (keyMap.containsKey(pii)) {
				id = (String) keyMap.get(pii);
			}
		} else if (inputKey.getIssn() != null && inputKey.getVol() != null
				&& inputKey.getIssue() != null && inputKey.getFirstPageNumber() != null) {
			String ivip = buildIvipKey(inputKey.getIssn(), inputKey
					.getFirstPageNumber(),inputKey.getIssue(),inputKey.getVol());
			if (keyMap.containsKey(ivip)) {
				id = (String) keyMap.get(ivip);
			}
		}
		return id;
	}

	
}
