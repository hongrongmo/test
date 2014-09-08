package org.ei.struts.framework.security;

import java.io.Serializable;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;


/**
 * Application implementation of <strong>java.security.Principal</strong>.
 *
 */

public class ApplicationPrincipal implements Principal, Serializable {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new Principal for the specified username and password.
     *
     * @param name The username of the user represented by this Principal
     * @param password Credentials used to authenticate this user
     */
    public ApplicationPrincipal(String name, String password) {

        this(name, password, null);

    }


    /**
     * Construct a new Principal, associated with the specified Realm, for the
     * specified username and password, with the specified role names
     * (as Strings).
     *
     * @param realm The Realm that owns this principal
     * @param name The username of the user represented by this Principal
     * @param password Credentials used to authenticate this user
     * @param roles List of roles (must be Strings) possessed by this user
     */
    public ApplicationPrincipal(String name, String password,
                            List roles) {

        super();
        this.name = name;
        this.password = password;
        if (roles != null) {
            this.roles = new String[roles.size()];
            this.roles = (String[]) roles.toArray(this.roles);
            if (this.roles.length > 0)
                Arrays.sort(this.roles);
        }

    }


    // ------------------------------------------------------------- Properties


    /**
     * The username of the user represented by this Principal.
     */
    protected String name = null;

    public String getName() {
        return (this.name);
    }


    /**
     * The authentication credentials for the user represented by
     * this Principal.
     */
    protected String password = null;

    public String getPassword() {
        return (this.password);
    }


    /**
     * The set of roles associated with this user.
     */
    protected String roles[] = new String[0];

    public String[] getRoles() {
        return (this.roles);
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Does the user represented by this Principal possess the specified role?
     *
     * @param role Role to be tested
     */
    public boolean hasRole(String role) {

        if (role == null)
            return (false);
        return (Arrays.binarySearch(roles, role) >= 0);

    }


    /**
     * Return a String representation of this object, which exposes only
     * information that should be public.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("ApplicationPrincipal[");
        sb.append(this.name);
        sb.append("]");
        return (sb.toString());

    }


}
