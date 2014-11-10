package org.ei.session;

public class UserCredentials
{

	public static boolean hasCredentials(int requestMask, int credentialMask) {

	  return ((requestMask & credentialMask) ==  requestMask );

  }

}
