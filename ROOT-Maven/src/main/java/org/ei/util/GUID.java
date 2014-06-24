package org.ei.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.server.UID;

/**
 * This class produces a Globally Unique Identifier.
 **/

public class GUID {
	private String guid;

	public GUID() throws UnknownHostException {
		UID uid = new UID();

		String vmid = System.getProperty("vmid");

		if (vmid == null) {
			InetAddress i = InetAddress.getLocalHost();
			guid = uid.toString() + i.getHostAddress();
		} else {
			guid = uid.toString() + vmid;
		}

		guid = guid.replace(".", "").replace(":", "").replace("-", "M");

	}

	public String toString() {
		return guid;
	}

}
