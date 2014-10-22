package org.ei.biz.security;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A security attribute that describes a user.  Security attributes are
 * arbitrary pieces of data that describe a user.  They are used by access
 * controls to decide who can access a given resource.  This approach is
 * similar to Shibboleth.  The application is responsible for providing
 * the appropriate security attributes for a user.
 */
public final class SecurityAttribute implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** An individual user. */
    public static final SecurityAttribute INDIVIDUAL = 
        new SecurityAttribute("Individual");

    /** An anonymous user. */
    public static final SecurityAttribute ANONYMOUS = 
        new SecurityAttribute("Anonymous");

    /** An Athens user. */
    public static final SecurityAttribute ATHENS = 
        new SecurityAttribute("Athens");

    /** A Shibboleth user. */
    public static final SecurityAttribute SHIBBOLETH = 
        new SecurityAttribute("Shibboleth");

    /** A guest user. */
    public static final SecurityAttribute GUEST = 
        new SecurityAttribute("Guest");

    /** A pending user. */
    public static final SecurityAttribute CANREGISTER = 
        new SecurityAttribute("CanRegister");

    /** An inward link. */
    public static final SecurityAttribute INWARDLINK = 
        new SecurityAttribute("InwardLink");

    /** An individual user. */
    public static final SecurityAttribute AUTHORFREELOOKUP = 
        new SecurityAttribute("AuthorFreeLookup");

    /** The value of the security attribute. */
    private String value;

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return value.hashCode();
    }

    /** A map that will contain all of the security attributes. */
    private static Map<String, SecurityAttribute> c_allTypes;
    
    // Add all the security attributes to the c_allTypes Map
    // NOTE: If you add a security attribute make sure it is added here!
    static {
        Map<String, SecurityAttribute> allTypes = new HashMap<String, SecurityAttribute>();
        allTypes.put(INDIVIDUAL.getValue(), INDIVIDUAL);
        allTypes.put(ANONYMOUS.getValue(), ANONYMOUS);
        allTypes.put(ATHENS.getValue(), ATHENS);
        allTypes.put(SHIBBOLETH.getValue(), SHIBBOLETH);
        allTypes.put(GUEST.getValue(), GUEST);
        allTypes.put(CANREGISTER.getValue(), CANREGISTER);
        allTypes.put(INWARDLINK.getValue(), INWARDLINK);
        allTypes.put(AUTHORFREELOOKUP.getValue(), AUTHORFREELOOKUP);
        

        // Set the instance variable to a read-only version of the Map
        c_allTypes = Collections.unmodifiableMap(allTypes);
    }

    /**
     * Constructor.  Private since only this class constructs the possible
     * security attributes.
     * @param value The string label for the security attribute.
     */
    private SecurityAttribute(String val) {
        value = val;
    }

    /**
     * Returns the string label for the security attribute.
     * @return String The string label for the security attribute.
     */
    public String getValue() {
        return value;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return value;
    }

    /**
     * Returns the Map of all the security attributess.  This method is
     * package scope since it is only meant to be used by the factory which
     * lives in the same package.
     * @return Map A map of the possible SecurityAttribute's
     */
    static Map<String, SecurityAttribute> getAllTypes() {
        return c_allTypes;
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object obj) {
        // Compare the two objects using the value of the security attribute.
        if (obj != null && obj instanceof SecurityAttribute) {
            SecurityAttribute type = (SecurityAttribute) obj;
            String val = type.getValue();
            if (val != null && val.equals(getValue())) {
                return true;
            }
        }

        return false;
    }
    
    /**
     * Resolves a de-serialized object to one of the static instances of 
     * the class to preserve control on instantiation.
     * @return The object that should be used in place of this instance after
     *         deserialization.
     */    
    public Object readResolve() {
        return c_allTypes.get(value);
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

   Copyright (c) 2006 by Elsevier, A member of the Reed Elsevier plc
   group.

   All Rights Reserved.

*****************************************************************************/
