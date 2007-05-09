package org.ei.controller;

import org.ei.exception.MajorSystemProblem;

public final class DataResponseBroker
{

	private static DataResponseBroker instance;
	private DataResponseCache responseCache;
	private String cacheDir;

	private DataResponseBroker(String cacheDir)
	{

		this.cacheDir = cacheDir;
		responseCache = new DataResponseCache(cacheDir);
	}

	public static DataResponseBroker getInstance(String cacheDir)
	{
		if(instance == null)
		{
			instance = new DataResponseBroker(cacheDir);
		}

		return instance;
	}


	public DataResponse getDataResponse(OutputPrinter printer, DataRequest dataRequest)
		throws ModelException,
		       RequestException,
		       MajorSystemProblem
	{

		return responseCache.getDataResponse(printer, dataRequest);

	}


}
