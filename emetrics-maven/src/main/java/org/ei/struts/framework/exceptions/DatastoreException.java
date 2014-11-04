package org.ei.struts.framework.exceptions;

/**
 * This exception is used to report an error that occurs when interacting with
 * the underlying datastore.
 */
public class DatastoreException extends BaseException {

  /**
   *  Return a DatastoreException for datastoreError condition.
   *
   * @return  The new DatastoreException object.
   */
  public static DatastoreException retrieveConstraint() {
    Object[] args = {};
    DatastoreException datastoreException = new DatastoreException(RETRIEVE_CONSTRAINT, args);
    return (datastoreException);
  }

  public static DatastoreException objectNotFound() {
    Object[] args = {};
    DatastoreException datastoreException = new DatastoreException(OBJECTNOTFOUND_ERROR, args);
    return (datastoreException);
  }

  public static DatastoreException datastoreError( Throwable rootCause) {
    Object[] args = {};
    DatastoreException datastoreException = new DatastoreException(DATASTORE_ERROR, args);
    datastoreException.setRootCause( rootCause );
    return (datastoreException);
  }

  /**
   * For use by subclasses of DatastoreException.
   */
  protected DatastoreException(String newErrorCode,
                               Object[] args) {
    super(newErrorCode, args);
  }
  public static final String RETRIEVE_CONSTRAINT = "RetrieveConstraint";
  public static final String DATASTORE_ERROR = "DatastoreError";
  public static final String OBJECTNOTFOUND_ERROR = "ObjectNotFound";
}
