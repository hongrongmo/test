package org.ei.evtools.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.ei.evtools.db.services.GoogleDriveUsageService;
import org.ei.evtools.exception.DatabaseAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
@Controller
public class HomeController {
	
	private static Logger logger = Logger.getLogger(HomeController.class);
	
	@Autowired
	GoogleDriveUsageService googleDriveUsageService; 

	@RequestMapping(value="/app/home")
	public ModelAndView test(HttpServletResponse response) throws IOException, DatabaseAccessException{
		logger.info("The request 'home' has been successfully processed.");
		googleDriveUsageService.getUsageData(null, null, null);
		return new ModelAndView("home").addObject("rowscount", googleDriveUsageService.getTotalCount());
	}
}
