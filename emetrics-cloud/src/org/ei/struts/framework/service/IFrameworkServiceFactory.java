package org.ei.struts.framework.service;

import org.ei.struts.framework.exceptions.DatastoreException;

public interface IFrameworkServiceFactory {
  public IFrameworkService createService() throws DatastoreException;
  public void destroy();
}