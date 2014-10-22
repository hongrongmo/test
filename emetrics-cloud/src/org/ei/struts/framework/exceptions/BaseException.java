package org.ei.struts.framework.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

/**
 *  This is the common superclass for all application exceptions. This
 *  class and its subclasses support the chained exception facility that allows
 *  a root cause Throwable to be wrapped by this class or one of its
 *  descendants. This class also supports multiple exceptions via the
 *  exceptionList field.
 */
public class BaseException extends Exception {

  /**
   *  Print both the normal and rootCause stack traces.
   */
  public void printStackTrace(PrintWriter writer) {
    super.printStackTrace(writer);
    if(getRootCause() != null) {
      getRootCause().printStackTrace(writer);
    }
    writer.flush();
  }

  /**
   *  Print both the normal and rootCause stack traces.
   */
  public void printStackTrace(PrintStream outStream) {
    printStackTrace(new PrintWriter(outStream));
  }

  /**
   *  Print both the normal and rootCause stack traces.
   */
  public void printStackTrace() {
    printStackTrace(System.err);
  }

  /**
   *  Return the root cause exception, if one exists.
   */
  public Throwable getRootCause() {
    return rootCause;
  }

  /**
   *  Retrieve the collection of exceptions.
   */
  public Collection getCollection() {
    return exceptions;
  }

  /**
   * Empty Constructor
   */
  public BaseException() {
    super();
  }

  /**
   *  A Constructor that takes a root cause throwable.
   */
  public BaseException(Throwable cause) {
    this.rootCause = cause;
  }

  /**
   * Retrieve the optional arguments.
   */
  public Object[] getMessageArgs() {
    return messageArgs;
  }
  public BaseException(String msgKey,
                       Object[] args) {
    this.messageKey = msgKey;
    this.messageArgs = args;
  }

  /**
   *  Set a nested, encapsulated exception to provide more low-level detailed
   *  information to the client.
   */
  public void setRootCause(Throwable anException) {
    rootCause = anException;
  }

  /**
   * Add an exception to the exception collection.
   */
  public void addException(BaseException ex) {
    exceptions.add(ex);
  }

  /**
   * Set the key to the bundle.
   */
  public void setMessageKey(String key) {
    this.messageKey = key;
  }

  /**
   * Retrieve the message bundle key.
   */
  public String getMessageKey() {
    return messageKey;
  }

  /**
   * Set an object array that can be used for parametric replacement.
   */
  public void setMessageArgs(Object[] args) {
    this.messageArgs = args;
  }

  /**
   *  An optional nested exception used to provide the ability to encapsulate
   *  a lower-level exception to provide more detailed context information
   *  concerning the exact cause of the exception.
   */
  protected Throwable rootCause = null;

  /**
   *  Holds a collection of exceptions added to this exception, for use when the
   *  application desires to return multiple exception conditions at once. For
   *  instance, if the application must evaluate and validate multiple pieces
   *  of data before performing an operation, it might be beneficial to
   *  validate all of the data and collect all resulting exceptions to be
   *  passed back at once.
   */
  private Collection exceptions = new ArrayList();

  /**
   * An optional array of arguments to use with the bundle message.
   */
  private Object[] messageArgs = null;

  /**
   * The key to the bundle message.
   */
  private String messageKey = null;
}
