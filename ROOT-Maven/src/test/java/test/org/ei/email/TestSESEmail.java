/**
 *
 */
package test.org.ei.email;

import java.io.IOException;

import org.ei.biz.email.SESEmail;
import org.ei.biz.email.SESMessage;
import org.ei.config.ApplicationProperties;
import org.ei.config.EVProperties;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author harovetm
 *
 */
public class TestSESEmail {

    private static boolean setup = false;
    @Before
    public void setUp() throws Exception {
        if (setup) return;
        init();
        setup = true;
    }

    private static void init() throws IOException {
        EVProperties.getInstance();
        EVProperties.setStartup(System.currentTimeMillis());

        ApplicationProperties rtp = EVProperties.getApplicationProperties();
        rtp.setProperty("mail.smtp.noreply", "einoreply@elsevier.com");
        EVProperties.setApplicationProperties(rtp);
    }

    @Test
    public void testSend() {
        // Try to send using unverified 'from' address
        SESMessage message = new SESMessage("harover@elsevier.com, tharover@gmail.com", "bar@fu.com", "EV Application Unit Testing", "This is a test from TestSESEmail!",false);
        SESEmail client = SESEmail.getInstance();
        String messageid;
        try {
            client.send(message);
        } catch (ServiceException e) {
            Assert.assertEquals(e.getErrorCode(), SystemErrorCodes.AWS_SES_FAILURE);
        }

        // Now send from verified but use unverified reply-to address
        message.setFrom("eicustomersupport@elsevier.com");
        message.setReplyTo("bar@fu.com");
        try {
            messageid = client.send(message);
            Assert.assertNotNull(messageid);
        } catch (ServiceException e) {
            Assert.fail(e.getMessage());
        }
    }
}
