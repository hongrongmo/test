package org.ei.stripes.action.aws;

import java.io.IOException;
import java.io.StringReader;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.log4j.Logger;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.ISecuredAction;
import org.ei.biz.security.NoAuthAccessControl;
import org.ei.session.AWSInfo;
import org.ei.stripes.action.EVActionBean;
import org.ei.stripes.action.autoComplete.AutoCompleteException;

@UrlBinding("/HealthCheck.url")
public class AWSInfoAction extends EVActionBean implements ISecuredAction {
    private final static Logger log4j = Logger.getLogger(AWSInfoAction.class);

    private AWSInfo awsInfo;

    public AWSInfo getAwsInfo() {
        return awsInfo;
    }

    /**
     * Override for the ISecuredAction interface. Make this URL open to the world!
     */
    @Override
    public IAccessControl getAccessControl() {
        return new NoAuthAccessControl();
    }

    @DefaultHandler
    public Resolution getAWSInfo() throws IOException   {
        awsInfo = new AWSInfo();
        context.getRequest().setAttribute("usersession", context.getUserSession());
        return new ForwardResolution("/WEB-INF/pages/status/aws/awsinfo.jsp");
    }

    @HandlesEvent("JSON")
    public Resolution getAWSInfoJSON() throws AutoCompleteException {

        return new StreamingResolution("text/json", new StringReader(JSONlist()));
    }

    public String JSONlist() {

        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put(AWSInfo.AMI_ID, awsInfo.get(AWSInfo.AMI_ID));
            jsonObj.put(AWSInfo.INSTANCE_ID, awsInfo.get(AWSInfo.INSTANCE_ID));
            jsonObj.put(AWSInfo.HOST_NAME, awsInfo.get(AWSInfo.HOST_NAME));
        } catch (JSONException e) {
            log4j.error("Error Getting AWS INFO!", e);
        }
        return jsonObj.toString();

    }

}
