package org.ei.service.cars.security.authorization;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the possible user access types for user
 * authentication.  This class is not meant to be implemented or extended
 * outside of this file.
 */
public final class UserAccessType implements Serializable {
 
	private static final long serialVersionUID = -6483591242762462817L;

	/** The name of the user access type */
    private String m_name;

    /** An activation pending user. */
    public static final UserAccessType ACTIVPEND =
        new UserAccessType("ACTIVPEND");

    /** An activation registered user. */
    public static final UserAccessType ACTIVREG =
        new UserAccessType("ACTIVREG");

    /** A registered Athens user. */
    public static final UserAccessType ATHENS =
        new UserAccessType("ATHENS");

    /** An Athens user that is pending/unregistered. */
    public static final UserAccessType ATHENSPEND =
        new UserAccessType("ATHENSPEND");

    /** A bulk pending user. */
    public static final UserAccessType BULKPEND =
        new UserAccessType("BULKPEND");

    /** A bulk registered user. */
    public static final UserAccessType BULKREG =
        new UserAccessType("BULKREG");

    /** A guest user. */
    public static final UserAccessType GUEST =
        new UserAccessType("GUEST");

    /** A registered guest user. */
    public static final UserAccessType GUESTREG =
        new UserAccessType("GUESTREG");

    /** An interally created username/password user. */
    public static final UserAccessType INTERNAL =
        new UserAccessType("INTERNAL");

    /**
     * An IP Profile user.  A username/password user that must also
     * originate from a specified IP range to be authenticated.
     */
    public static final UserAccessType IPPROFILE =
        new UserAccessType("IPPROFILE");

    /**
     * A IP user.  This is a group of users who are authenticated by
     * their IP being in a specified range.
     */
    public static final UserAccessType IPRANGE =
        new UserAccessType("IPRANGE");

    public static final UserAccessType SELF_MANAGED =
            new UserAccessType("SELF_MANAGED");

    
    
    /**
	 * A TICURL user.  This user access Scopus through SSO.
	 * Scopus doesn't have individual TICURL user.
	 */
	public static final UserAccessType TICURL =
        new UserAccessType("TICURL");

    /**
     * A user created through the Admin Tool.  A username/password user.
     */
    public static final UserAccessType ADMINTOOL =
        new UserAccessType("ADMINTOOL");

    /** A registered SHIBBOLETH user. */
    public static final UserAccessType SHIBBOLETHREG =
        new UserAccessType("SHIBBOLETHREG");

    /** An anonymous SHIBBOLETH user. */
    public static final UserAccessType SHIBBOLETHANON =
        new UserAccessType("SHIBBOLETHANON");

    /** An Shibboleth user that is pending/unregistered. */
    public static final UserAccessType SHIBBOLETHPEND =
        new UserAccessType("SHIBBOLETHPEND");

    /** An shibboleth user that is pending/unregistered. and needs
     * to select a dept before registration can occur */
    public static final UserAccessType SHIBBOLETH_PRE_PEND =
        new UserAccessType("SHIBBOLETH_PRE_PEND");
    /**
     * The list of all the individually authenticated types.
     * NOTE: If you add a user access type then check if you should add it here!
     */
    public static final UserAccessType[] INDIVIDUAL_AUTHENTICATIONS =
        {ACTIVREG, ATHENS, BULKREG, INTERNAL, IPPROFILE, ADMINTOOL, SHIBBOLETHREG, TICURL, SELF_MANAGED};

    /**
     * The list of all the group authentications.
     * NOTE: If you add a user access type then check if you should add it here!
     */
    public static final UserAccessType[] GROUP_AUTHENTICATIONS =
        {IPRANGE, IPPROFILE};

    /** A map that will contain all of the user access types. */
    private static Map<String, UserAccessType> c_allTypes;

    // Add all the user access types to the c_allTypes Map
    // NOTE: If you add a user access type make sure it is added here!
    static {
        Map<String, UserAccessType> allTypes = new HashMap<String, UserAccessType>();
        allTypes.put(ACTIVPEND.getName(), ACTIVPEND);
        allTypes.put(ACTIVREG.getName(), ACTIVREG);
        allTypes.put(ATHENS.getName(), ATHENS);
        allTypes.put(ATHENSPEND.getName(), ATHENSPEND);
        allTypes.put(BULKPEND.getName(), BULKPEND);
        allTypes.put(BULKREG.getName(), BULKREG);
        allTypes.put(GUEST.getName(), GUEST);
        allTypes.put(GUESTREG.getName(), GUESTREG);
        allTypes.put(INTERNAL.getName(), INTERNAL);
        allTypes.put(IPPROFILE.getName(), IPPROFILE);
        allTypes.put(IPRANGE.getName(), IPRANGE);
        allTypes.put(ADMINTOOL.getName(), ADMINTOOL);
        allTypes.put(SHIBBOLETHREG.getName(),SHIBBOLETHREG);
        allTypes.put(SHIBBOLETHANON.getName(),SHIBBOLETHANON);
        allTypes.put(SHIBBOLETHPEND.getName(),SHIBBOLETHPEND);
        allTypes.put(SHIBBOLETH_PRE_PEND.getName(),SHIBBOLETH_PRE_PEND);
        allTypes.put(TICURL.getName(), TICURL);
        allTypes.put(SELF_MANAGED.getName(), SELF_MANAGED);

        // Set the instance variable to a read-only version of the Map
        c_allTypes = Collections.unmodifiableMap(allTypes);
    }

	/**
	 * Constructor.  Private since only this class constructs the possible
     * user access types.
	 * @param name The string label for the user access type
	 */
    private UserAccessType(String name) {
        m_name = name;
    }

	/**
	 * Returns the string label for the user access type.
	 * @return String The string label for the user access type.
	 */
    public String getName() {
        return m_name;
    }

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return m_name;
	}

	/**
	 * Returns true if the given type if an individual authentication.
	 * @param type The type to check.
	 * @return boolean True if the given type is an individual auth type.
	 */
    public static boolean isIndividualAuthType(UserAccessType type) {
        if (type != null) {
            int length = INDIVIDUAL_AUTHENTICATIONS.length;
            for (int i = 0; i < length; i++) {
                if (type.equals(INDIVIDUAL_AUTHENTICATIONS[i])) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns true if the given type if a group authentication.
     * @param type The type to check.
     * @return boolean True if the given type is a group auth type.
     */
    public static boolean isGroupAuthType(UserAccessType type) {
        if (type != null) {
            int length = GROUP_AUTHENTICATIONS.length;
            for (int i = 0; i < length; i++) {
                if (type.equals(GROUP_AUTHENTICATIONS[i])) {
                    return true;
                }
            }
        }

        return false;
    }

	/**
	 * Returns the Map of all the user access types.  This method is
     * package scope since it is only meant to be used by the factory which
     * lives in the same package.
	 * @return Map A map of the possible UserAccessType's
	 */
    static Map<String, UserAccessType> getAllTypes() {
        return c_allTypes;
    }

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object obj) {
        // Compare the two objects using the name of the user access type
        if (obj != null && obj instanceof UserAccessType) {
            UserAccessType type = (UserAccessType) obj;
            String name = type.getName();
            if (name != null && name.equals(this.getName())) {
                return true;
            }
        }

		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return m_name.hashCode();
	}

    /**
    Resolves a de-serialized object to one of the static instances of the class
    to preserve control on instantiation.

    @return The object that should be used in place of this instance after
            deserialization.
     */
    public Object readResolve () {
        return c_allTypes.get (m_name);
    }
}

/*****************************************************************************

                               ELSEVIER
                             CONFIDENTIAL


   This document is the property of Elsevier, and its contents are
   proprietary to Elsevier.   Reproduction in any form by anyone of the
   materials contained  herein  without  the  permission  of Elsevier is
   prohibited.  Finders are  asked  to  return  this  document  to the
   following Elsevier location.

       Elsevier
       360 Park Avenue South,
       New York, NY 10010-1710

   Copyright (c) 2003 by Elsevier, A member of the Reed Elsevier plc
   group.

   All Rights Reserved.

*****************************************************************************/
