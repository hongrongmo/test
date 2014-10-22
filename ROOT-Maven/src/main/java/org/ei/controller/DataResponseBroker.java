package org.ei.controller;

import javax.servlet.ServletException;

import org.ei.exception.InfrastructureException;
import org.ei.exception.ServiceException;

public final class DataResponseBroker {

	private static DataResponseBroker instance;
	private DataResponseCache responseCache;
	private String cacheDir;
	private DataResponseForActionBean responseForActionBean;

	private DataResponseBroker(String cacheDir) {

		this.cacheDir = cacheDir;
		responseCache = new DataResponseCache(cacheDir);
		responseForActionBean = new DataResponseForActionBean();

	}

	public static DataResponseBroker getInstance(String cacheDir) {
		if (instance == null) {
			instance = new DataResponseBroker(cacheDir);
		}

		return instance;
	}

	public DataResponse getDataResponse(OutputPrinter printer, DataRequest dataRequest) throws ServiceException {

		return responseCache.getDataResponse(printer, dataRequest);

	}

	public DataResponse getDataResponseForActionBean(OutputPrinter printer, DataRequest dataRequest, String xmlUrl) throws ServiceException, InfrastructureException {

		return responseForActionBean.getDataResponse(printer, dataRequest, xmlUrl);

	}

}
