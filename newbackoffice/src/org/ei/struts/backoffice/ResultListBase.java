package org.ei.struts.backoffice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.beanutils.BeanUtils;
/**
 * Concrete implementation of ResultList.
 * @author Ted Husted
 * @version $Revision:   1.0  $ $Date:   Jan 14 2008 17:10:24  $
 */
public class ResultListBase implements ResultList {


// ----------------------------------------------------------- Properties


    /**
     * The result list [ArrayList].
     */
    protected List result = (List) new ArrayList();


    public List getResult() {
        return this.result;
    }


    public void setResult(List result) {
        this.result = result;
    }



    /**
     * Our scroller object for paging through lists.
     */
    //protected Scroller scroller = null;


    //public void setScroller(Scroller scroller){
	//	this.scroller = scroller;
	//}


    //public Scroller getScroller() {
       // return this.scroller;
    //}


    /**
     * Our intial counter value [0].
     */
    private int counter = 0;


    public int getCounter() {
        return counter++;
    }


    public void setCounter(int counter) {
           this.counter = counter;
    }


    /**
     * A result code, if returned by the resource layer.
     */
    private Integer code = null;


    public Integer getCode() {
        return (this.code);
    }


    public void setCode(Integer code) {
        this.code = code;
    }


    /**
     * A phrase describing the result list.
     * ie: field = value
     */
    private String legend = null;


    public String getLegend() {
        return (this.legend);
    }


    public void setLegend(String legend) {
        this.legend = legend;
    }


    public void setLegend(String name, String value) {

        if ((null==value) || ("".equals(value))) {

            setLegend(name + " = " + "*");

        }
        else {

            setLegend(name + " = " + value);

        }
    }


    /**
     * The displayName map, if any.
     */
    protected Map displayName = null;


    public Map getDisplayName() {
        return this.displayName;
    }


    public void setDisplayName(Map displayName) {
        this.displayName = displayName;
    }



// ------------------------------------------------------------- List Methods


    // ----- array operations -----


    public Object[] toArray() {
        return getResult().toArray();
    }


    public Object[] toArray(Object a[]) {
        return getResult().toArray(a);
    }


    // ----- basic operations -----


    public boolean isEmpty() {
        return getResult().isEmpty();
    }


    public int size() {
        return getResult().size();
    }


    public boolean contains(Object element) {
        return getResult().contains(element);
    }


    public boolean add(Object o) {
        return getResult().add(o);
    }


    public Iterator iterator() {
        return getResult().iterator();
    }


    // ----- list operations -----


    public Object get(int index) {
        return getResult().get(index);
    }


    // ----- bulk operations ------


    public boolean addAll(Collection c) {
        return getResult().addAll(c);
    }


    public void clear() {
        getResult().clear();
    }


    public boolean containsAll(Collection c) {
        return getResult().containsAll(c);
    }


    public boolean remove(Object o) {
        return getResult().remove(o);
    }


    public boolean removeAll(Collection c) {
        return getResult().removeAll(c);
    }


    public boolean retainAll(Collection c) {
        return getResult().retainAll(c);
    }



// ----------------------------------------------------------- Public Methods


    public Object getElement(int index) {
        return getResult().get(index);
    }


    public Iterator getIterator() {
        return iterator();
    }


    public int getSize() {
        return size();
    }


    public boolean populate(Object o, int index) throws Exception {
        Object bean = null;
        if (size()>index)
            bean = get(index);
        if (bean==null) return false;
        try {
            BeanUtils.copyProperties(o,bean);
        } catch (Throwable t) {
			throw new ServletException("populate", t);
        }
        return true;
    }


// ----------------------------------------------------------- Constructors


    /**
     * Default constructor.
     */
    public ResultListBase() {
        super();
    }


    /**
     * Convenience constructor to populate result with an element.
     */
    public ResultListBase(Object o) {
        super();
        add(o);
    }


    /**
     * Convenience constructor to populate result with a Collection.
     */
    public ResultListBase(Collection c) {
        super();
        addAll(c);
    }

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

