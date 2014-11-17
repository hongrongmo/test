package org.ei.evtools.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.ei.evtools.db.domain.UserAndRole;
import org.ei.evtools.db.services.EVToolsUserDetailsService;
import org.ei.evtools.exception.AWSAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
@Controller
public class EVToolsUsersController {
	
	private static Logger logger = Logger.getLogger(EVToolsUsersController.class);
	
	@Autowired
	@Qualifier("userDetailsService")
	EVToolsUserDetailsService userDetailsService;
	
	@RequestMapping(value="/admin/users")
	public ModelAndView users(HttpServletResponse response) throws AWSAccessException{
		
		List<UserAndRole> list = userDetailsService.loadAllUsers();
		logger.info("The request 'users' has been successfully processed.");
		return new ModelAndView("users").addObject("userslist", list).addObject("pagetype", "users");
	}
	
	@ExceptionHandler({AWSAccessException.class,Exception.class})
	public ModelAndView handleDocumentBuilderException(Exception ex){
		logger.error("An error occured while processing the request, exception : "+ex.getMessage());
		ModelAndView model = new ModelAndView("error");
		model.addObject("error", "A system error occured, please try again later!");
		return model;
	}
	
	
}
