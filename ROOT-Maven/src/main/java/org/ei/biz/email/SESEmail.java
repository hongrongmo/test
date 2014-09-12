/**
 *
 */
package org.ei.biz.email;

import org.apache.log4j.Logger;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;
import org.ei.service.amazon.AmazonServiceHelper;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;

/**
 * @author harovetm
 *
 */
public class SESEmail {
    private Logger log4j = Logger.getLogger(SESEmail.class);

    private static SESEmail instance;

    private AmazonSimpleEmailServiceClient sesclient;

    private SESEmail() {
        sesclient = AmazonServiceHelper.getInstance().getAmazonSESClient();
    }

    public static SESEmail getInstance() {
        if (instance == null) {
            instance = new SESEmail();
        }
        return instance;
    }

    public String send(SESMessage message) throws ServiceException {

        try {
            // Send the email.
            log4j.info("Sending message from " + message.getFrom() + ", subject '" + message.getMessage().getSubject() + "'");
            return instance.sesclient.sendEmail(message.buildRequest()).getMessageId();
        } catch (Throwable t) {
            log4j.error("Unable to send message!", t);
            throw new ServiceException(SystemErrorCodes.AWS_SES_FAILURE, t.getMessage());
        }
    }
}
