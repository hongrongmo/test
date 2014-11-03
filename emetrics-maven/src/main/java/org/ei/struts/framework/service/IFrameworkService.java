package org.ei.struts.framework.service;
/**
 * The business interface for the Application. It defines all
 * of the methods that a client may call on the application.
 *
 * This interface extends the IAuthentication interface to create a
 * cohesive interface for the application.
 */
public interface IFrameworkService  {

  //public List getFeaturedItems() throws DatastoreException;

  //public ItemDetailView getItemDetailView( String itemId )
  //  throws DatastoreException;

  public void destroy();
}