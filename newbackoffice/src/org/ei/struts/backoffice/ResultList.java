package org.ei.struts.backoffice;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A container for a set of results returned from the
 * resource tier. The list may contain a Map for each
 * record in a set, or a collection of beans. A setter
 * for the list is not provided, so that different
 * implementations can specify the type it expects.
 * @author Ted Husted
 * @version $Revision:   1.0  $ $Date:   Jan 14 2008 17:10:24  $
 */
public interface ResultList extends Collection {


// ----------------------------------------------------------- Properties


    /**
     * Return the result list
     * @returns the result list
     */
    public List getResult();


    /**
     * Set our result
     * @param result The new result
     */
    public void setResult(List result);


    /**
     * Return our scroller.
     * The scroller object tracks the client's current
     * position in a result list.
     * The database (or a cache) can return part of a
     * larger list at a time.
     * The scroller object can be used to request the
     * appropriate next or previous entry on the list,
     * and also to display the relative postion of the
     * first item in this batch (x of xx).
     * @return Our scroller
     */
    //public Scroller getScroller();


    /**
     * Set our scroller.
     * @param scroller The new scroller
     */
    //public void setScroller(Scroller scroller);


    /**
     * Convenience method for maintaining a counter
     * that can be shared among multiple components
     * in some presentation systems (e.g, Tiles).
     * @returns The next counter value
     */
    public int getCounter();


    /**
     * Set a new counter value.
     */
    public void setCounter(int counter);


    /**
     * Return the code.
     * @return the code
     */
    public Integer getCode();


    /**
     * Set the code.
     * @param code The new code
     */
    public void setCode(Integer code);


    /**
     * Return the legend.
     * @return the legend
     */
    public String getLegend();


    /**
     * Set the legend.
     * @param legend The new legend
     */
    public void setLegend(String legend);


    /**
     * Set the legend.
     * @param legend The new legend
     */
    public void setLegend(String name, String value);


    /**
     * Return the displayName map (a HashMap).
     * These are localized titles for the
     * properties names in the result list.
     * @returns the displayName list
     */
    public Map getDisplayName();


    /**
     * Assign a new displayName list.
     * These are localized titles for the
     * properties names in the result list.
     */
    public void setDisplayName(Map displayName);


// ------------------------------------------------------- Collection Methods

    // ----- array operations -----


    /**
     * Returns an array containing all of the elements in this collection.
     * @return an array containing all of the elements in this collection
     */
    public Object[] toArray();


    /**
     * Returns an array containing all of the elements in this collection; the
     * runtime type of the returned array is that of the specified array.
     * @return an array containing the elements of this collection
     */
    public Object[] toArray(Object a[]);


    // ----- basic operations -----


    /**
     * Returns true if this collection contains no elements.
     * @return true if this collection contains no elements
     */
    public boolean isEmpty();


    /**
     * Return the number of elements on the List.
     * @return the size of the List
     */
    public int size();


    /**
     * Returns true if this collection contains the specified element.
     * @return true if this collection contains the specified element
     */
    public boolean contains(Object element);


    /**
     * Appends the specified element to the end of this list (optional
     * operation).
     * @return the row count
     */
    public boolean add(Object o);


    /**
     * Return an iterator for the List.
     * @return an iterator for the List
     */
    public Iterator iterator();


    // ----- list operations -----

    /**
     * Returns the element at the specified position in this list.
     */
    public Object get(int index);


    // ----- bulk operations ------

    /**
     * Appends all of the elements in the specified Collection
     * to the end of this list, in the order that they are
     * returned by the specified Collection's Iterator.
     */
    public boolean addAll(Collection c);


    /**
     * Removes all of the elements from this list..
     */
    public void clear();


    /**
     * Returns true if this collection contains all of the elements in the
     * specified collection.
     * @returns true if this collection contains all of the elements in the
     * specified collection
     */
    public boolean containsAll(Collection c);


    /**
     * Removes a single instance of the specified element from this
     * collection, if it is present (optional operation).
     */
    public boolean remove(Object o);


    /**
     * Removes all this collection's elements that are also contained in the
     * specified collection.
     */
    public boolean removeAll(Collection c);

    /**
     * Retains only the elements in this collection that are contained in the
     * specified collection.
     */
    public boolean retainAll(Collection c);



// ----------------------------------------------------------- Public Methods


    /**
     * Convenience accessor for <code>get()</code>.
     */
    public Object getElement(int index);


    /**
     * Convenience accessor for <code>iterator()</code>.
     * @return an iterator for the List
     */
    public Iterator getIterator();


    /**
     * Convenience accessor for <code>size()</code>.
     * @return the size of the List
     */
    public int getSize();


    /**
     * Populate matching properties on given object,
     * using bean at given index. Returns false if index>size.
     * <code>PropertyUtils.describe</code>.
     * @exception Throws StateException on any error.
     */
    public boolean populate(Object o, int index) throws Exception;

}



/*
 *
 *    Copyright (c) 2002 Synthis Corporation.
 *    430 10th Street NW, Suite S-108, Atlanta GA 30318, U.S.A.
 *    All rights reserved.
 *
 *    This software is licensed to you free of charge under
 *    the Apache Software License, so long as this copyright
 *    statement, list of conditions, and comments,  remains
 *    in the source code.  See bottom of file for more
 *    license information.
 *
 *    This software was written to support code generation
 *    for the Apache Struts J2EE architecture by Synthis'
 *    visual application modeling tool Adalon.
 *
 *    For more information on Adalon and Struts code
 *    generation please visit http://www.synthis.com
 *
 */



 /*
  * ====================================================================
  *
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2001 The Apache Software Foundation.  All rights
  * reserved.
  *
  * Redistribution and use in source and binary forms, with or without
  * modification, are permitted provided that the following conditions
  * are met:
  *
  * 1. Redistributions of source code must retain the above copyright
  *    notice, this list of conditions and the following disclaimer.
  *
  * 2. Redistributions in binary form must reproduce the above copyright
  *    notice, this list of conditions and the following disclaimer in
  *    the documentation and/or other materials provided with the
  *    distribution.
  *
  * 3. The end-user documentation included with the redistribution, if
  *    any, must include the following acknowlegement:
  *       "This product includes software developed by the
  *        Apache Software Foundation (http://www.apache.org/)."
  *    Alternately, this acknowlegement may appear in the software itself,
  *    if and wherever such third-party acknowlegements normally appear.
  *
  * 4. The names "The Jakarta Project", "Scaffold", and "Apache Software
  *    Foundation" must not be used to endorse or promote products derived
  *    from this software without prior written permission. For written
  *    permission, please contact apache@apache.org.
  *
  * 5. Products derived from this software may not be called "Apache"
  *    nor may "Apache" appear in their names without prior written
  *    permission of the Apache Group.
  *
  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
  * SUCH DAMAGE.
  * ====================================================================
  *
  * This software consists of voluntary contributions made by many
  * individuals on behalf of the Apache Software Foundation.  For more
  * information on the Apache Software Foundation, please see
  * <http://www.apache.org/>.
  *
  */

