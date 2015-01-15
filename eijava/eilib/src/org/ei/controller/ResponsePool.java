package org.ei.controller;

import org.ei.util.LRUTable;

public class ResponsePool
{
	private LRUTable responseTable;

	public ResponsePool(int maxSize)
	{
		responseTable = new LRUTable(maxSize);
	}

	public synchronized void addResponse(String requestID, DataResponse response)
	{
		this.responseTable.put(requestID, response);
	}

	public synchronized boolean responseReady(String requestID)
	{
		boolean ready = false;

		if(responseTable.containsKey(requestID))
		{
			ready = true;
		}	

		return ready;
	}

	public synchronized DataResponse getResponse(String requestID)
	{
		DataResponse res = (DataResponse)responseTable.get(requestID);
		return res;
	}
}
