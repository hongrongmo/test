package org.ei.email;

import javax.mail.MessagingException;

/**
 * Not enough information to initialize a mail session.
 * @author  Varma Saripalli
 **/
class IncompleteMailConfigurationException extends MessagingException{
    public IncompleteMailConfigurationException(String message){
        super(message);
    }
}

