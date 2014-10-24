package org.ei.stripes.action.personalaccount;


/**
 * This interface is used to mark action beans 
 * that require personal profile authorization.
 * E.g. Modify profile, folders, etc.
 * 
 * @author harovetm
 *
 */
public interface IPersonalLogin {
    /**
     * Action beans must also implement this method to return the "next"
     * URL string for after the user logs in.  This *used* to go on the 
     * URL directly but with CARS, it should be stored in session.
     *  
     * @return String
     */
    public String getLoginNextUrl() ;

    /**
     * Tells caller where to go to if user cancels from login page.
     *  
     * @return String
     */
    public String getLoginCancelUrl() ;
}
