package org.ei.evtools.controller;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ei.evtools.db.services.GoogleDriveUsageService;
import org.ei.evtools.exception.DatabaseAccessException;
import org.ei.evtools.model.GoogleDriveUsageForm;
import org.ei.evtools.utils.EVToolsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
@Controller
@PropertySource("classpath:application.properties")
public class GoogleDriveUsageController {
	
	
	private static Logger logger = Logger.getLogger(GoogleDriveUsageController.class);
	
	private static String dateFormat = "dd-MM-yyyy HH:mm:ss";
	
	@Autowired
	GoogleDriveUsageService googleDriveUsageService; 
	
	@Autowired
	private Environment environment; 
	
	@RequestMapping(value="/app/googledriveusage")
	public ModelAndView googledriveusage(@ModelAttribute("googledriveusageform") GoogleDriveUsageForm form) throws IOException, DatabaseAccessException{
		
		if(EVToolsUtils.isEmptyorNull(form.getUsageOption()) || !EVToolsUtils.isValidUsageOption(form.getUsageOption())){
			form.setUsageOption("downloadformat");
		}
		
		String usageOption = form.getUsageOption();
		Date startDate = null; 
		if(!EVToolsUtils.isEmptyorNull(form.getStartDate()) && EVToolsUtils.isValidDate(form.getStartDate()+" 00:00:00", dateFormat)){
			startDate = EVToolsUtils.convertStringToDate(form.getStartDate()+" 00:00:00", dateFormat);
		}
		
		Date endDate = null; 
		if(!EVToolsUtils.isEmptyorNull(form.getEndDate()) && EVToolsUtils.isValidDate(form.getEndDate()+" 23:59:59", dateFormat)){
			endDate = EVToolsUtils.convertStringToDate(form.getEndDate()+" 23:59:59", dateFormat);
		}
		
		Map<String, String> usageData = googleDriveUsageService.getUsageData(usageOption, startDate, endDate);
		
		ModelAndView model = new ModelAndView("googledriveusage");
		model.addObject("totalCount", googleDriveUsageService.getTotalCount());
		model.addObject("usageData", usageData);
		model.addObject("usageOption", usageOption);
		model.addObject("pagetype", "googledriveusage");
		model.addObject("environment", environment.getProperty("ENVIRONMENT"));
		
		if(startDate != null && endDate != null){
			model.addObject("dateQueryinfo", "Date filter applied from " + form.getStartDate() + " to " + form.getEndDate());
		}
		form.setEndDate(null);
		form.setStartDate(null);
		logger.info("Request 'googledriveusage' has been processed successfuly!");
		return model;
	}
	
	@ExceptionHandler({DatabaseAccessException.class, IOException.class, Exception.class})
	public ModelAndView handleDocumentBuilderException(Exception ex){
		logger.error("An error occured while processing the request, exception : "+ex.getMessage());
		ModelAndView model = new ModelAndView("error");
		model.addObject("error", "A system error occured, please try again later!");
		return model;
	}
	
	
	
	
}
