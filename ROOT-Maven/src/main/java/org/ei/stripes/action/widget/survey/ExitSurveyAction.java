package org.ei.stripes.action.widget.survey;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.ei.stripes.action.EVActionBean;

@UrlBinding("/widget/exitsurvey.url")
public class ExitSurveyAction extends EVActionBean {

	@DefaultHandler
    @DontValidate
    public Resolution quickSurvey()  {
        // Forward on
        return new ForwardResolution("/WEB-INF/pages/widget/exitSurveyWait.jsp");
    }

}
