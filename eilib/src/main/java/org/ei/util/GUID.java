package org.ei.util;

import java.util.UUID;

/**
 * This class produces a Globally Unique Identifier.
 **/

public class GUID {
	private String guid;

	public GUID() {
		guid = normalize(String.valueOf(UUID.randomUUID()));
	}

	public static String normalize(String uid) {
	    return uid.replace(".", "").replace(":", "").replace("-", "M").replace("_", "");
	}

	public String toString() {
		return guid;
	}

}
