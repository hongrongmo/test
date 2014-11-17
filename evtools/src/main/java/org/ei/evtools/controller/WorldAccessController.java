package org.ei.evtools.controller;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
@Controller
public class WorldAccessController {
	private static Logger logger = Logger.getLogger(WorldAccessController.class);
	
	@RequestMapping(value="/world/403")
	public ModelAndView world403(HttpServletResponse response) {
		logger.info("The request 'world403' has been successfully processed.");
		return new ModelAndView("403");
	}
}
