package org.ei.exception;

/**
 * Value not found for the property name.
 * @author  Varma Saripalli
 **/
public class ValueNotFoundException  extends Exception{
    public ValueNotFoundException (String message){
        super(message);    
    }
}
