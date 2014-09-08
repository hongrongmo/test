package org.ei.stripes.action.widget.survey;

import java.util.ArrayList;
import java.util.Arrays;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.httpclient.HttpStatus;
import org.ei.stripes.action.EVActionBean;

@UrlBinding("/widget/qsurvey.url")
public class QuickSurveyAction extends EVActionBean {

	public final static String RATING_BAD = "bad";
	public final static String RATING_NEUTRAL = "neutral";
	public final static String RATING_GOOD = "good";
	public final static String FEAT_HLIGHT = "highlight";
	
	public final static ArrayList<String> ratings = new ArrayList<String>(Arrays.asList(RATING_BAD,RATING_NEUTRAL,RATING_GOOD));
	public final static ArrayList<String> features = new ArrayList<String>(Arrays.asList(FEAT_HLIGHT));

	private String feature = "";
	private String rating = RATING_NEUTRAL;
	@DefaultHandler
    @DontValidate
    public Resolution quickSurvey()  {
        // Forward on
        return new ForwardResolution("/WEB-INF/pages/widget/quickSurvey.jsp");
    }
	@HandlesEvent("rate")
	public Resolution rate(){
		//need to validate and register rating;
		if(!ratings.contains(rating)){
			getContext().getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
	    	return new StreamingResolution("text", "Invalid Rating");
		}
		if(!features.contains(feature)){
			getContext().getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
	    	return new StreamingResolution("text", "Invalid feature");
		}

		getContext().getResponse().setStatus(HttpStatus.SC_OK);
    	return new StreamingResolution("text", "");
	}
	public String getFeature() {
		return feature;
	}
	public void setFeature(String feature) {
		this.feature = feature;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}

}
