package org.ei.evtools.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.ei.evtools.db.domain.BlockedIPEvent;
import org.ei.evtools.db.domain.BlockedIPStatus;
import org.ei.evtools.db.services.DynamoDBService;
import org.ei.evtools.exception.AWSAccessException;
import org.ei.evtools.utils.EVToolsConstants;
import org.ei.evtools.utils.EVToolsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
@Controller
@PropertySource("classpath:application.properties")
public class IPBlockerController {
	
	private static Logger logger = Logger.getLogger(IPBlockerController.class);
	
	@Autowired
	private Environment environment; 
	
	@Autowired
	DynamoDBService dynamoDBService; 
	
	@RequestMapping(value="/app/ipblocker")
	public ModelAndView ipblocker()throws AWSAccessException{
		String env = "";
		if(EVToolsUtils.isValidEnv(environment.getProperty("ENVIRONMENT"))){
			env =  environment.getProperty("ENVIRONMENT");
		}else{
			env =  EVToolsConstants.ATTRIBUTE_PROD;
		}
		
		List<BlockedIPStatus> blockedIpsList = dynamoDBService.getByStatus(BlockedIPStatus.STATUS_ANY, env);
		ModelAndView model = new ModelAndView("ipblocker");
		model.addObject("environment", env);
		model.addObject("pagetype", "ipblocker");
		model.addObject("blockedIpsList", blockedIpsList);
		model.addObject("totalCount", blockedIpsList.size());
		model.addObject("pagetype", "ipblocker");
		logger.info("Request 'ipblocker' has been processed successfuly for the environment '"+env+"'");
		return model;
	}
	
	@RequestMapping(value="/app/addip", method=RequestMethod.POST)
    public String addip(@RequestParam(value="ip", required=true) String ip, 
    		RedirectAttributes redirectAttributes, HttpServletRequest request) throws AWSAccessException{
        String env = "";
		String errorMessage = null;
		if(EVToolsUtils.isValidEnv(environment.getProperty("ENVIRONMENT"))){
			env =  environment.getProperty("ENVIRONMENT");
		}else{
			errorMessage = "Invalid environment configured, please contact the technical team!";
		}
		if (errorMessage == null && !EVToolsUtils.isValidIP(ip)) {
			errorMessage = "Invalid IP '"+ip+"' address is provided!";
		}
		
		if (errorMessage == null && !request.isUserInRole("ROLE_EDIT_IPBLOCKER")){
			errorMessage = "You are not authorized for this action";
		}
			
		if(errorMessage == null){
			BlockedIPStatus ipstatus = dynamoDBService.loadBlockedIPStatus(ip, env);
			if(ipstatus != null){
				errorMessage = "IP '"+ip+"' is already existing in the system for the environment '"+env+"'";
			}else{
				dynamoDBService.addBlockedIP(ip, env);
			}
		}
		if(errorMessage != null){
			logger.info("Request 'addip' validation failed for the ip '"+ip+"' and the environment '"+env+"'");
			redirectAttributes.addFlashAttribute("error", errorMessage);
		}else{
			redirectAttributes.addFlashAttribute("message", "The ip '" + ip + "' has been successfully added for '"+env+"' environment.");
			logger.info("Request 'addip' has been processed successfuly for the ip '"+ip+"' and the '"+env+"' environment.");
		}
		return "redirect:/app/ipblocker";
	}
	
	 @RequestMapping(value="/app/updateip", method=RequestMethod.POST)
	 public String updateip(@RequestParam(value="ip", required=true) String ip, @RequestParam(value="blocked", required=true) String blocked,
	    		RedirectAttributes redirectAttributes, HttpServletRequest request)  throws AWSAccessException {
		 	String env = "";
			String errorMessage = null;
			if(EVToolsUtils.isValidEnv(environment.getProperty("ENVIRONMENT"))){
				env =  environment.getProperty("ENVIRONMENT");
			}else{
				errorMessage = "Invalid environment configured, please contact the technical team!";
			}
			
			if (errorMessage == null && !EVToolsUtils.isValidIP(ip)) {
				errorMessage = "Invalid IP '"+ip+"' address is provided!";
			}
			
			if (errorMessage == null && !EVToolsUtils.isValidIPBlockerStatus(blocked)) {
				errorMessage = "Invalid update for the IP '"+ip+"' is requested!";
			}
			
			if (errorMessage == null && !request.isUserInRole("ROLE_EDIT_IPBLOCKER")){
				errorMessage = "You are not authorized for this action";
			}
			
			if(errorMessage == null){
				BlockedIPStatus ipstatus = dynamoDBService.loadBlockedIPStatus(ip, env);
				if(ipstatus != null){
					ipstatus.setStatus(blocked.equalsIgnoreCase("true") ? BlockedIPStatus.STATUS_BLOCKED : BlockedIPStatus.STATUS_UNBLOCKED);
					ipstatus.setTimestamp(new Date());
					dynamoDBService.updateBlockedIP(ipstatus);
				}else{
					errorMessage = "IP '"+ip+"' you are trying to update is not part of blocked list.";
				}
			}
			if(errorMessage != null){
				logger.info("Request 'updateip' validation failed for the ip '"+ip+"' and the environment '"+env+"'");
				redirectAttributes.addFlashAttribute("error", errorMessage);
			}else{
				redirectAttributes.addFlashAttribute("message", "The ip '" + ip + "' has been successfully updated for '"+env+"' environment.");
				logger.info("Request 'updateip' has been processed successfuly for the ip '"+ip+"' and the '"+env+"' environment.");
			}
			return "redirect:/app/ipblocker";
     }
	 
	 @RequestMapping(value="/app/removeip", method=RequestMethod.POST)
	 public String removeip(@RequestParam(value="ip", required=true) String ip,
	    		RedirectAttributes redirectAttributes, HttpServletRequest request)  throws AWSAccessException {
		 	String env = "";
			String errorMessage = null;
			if(EVToolsUtils.isValidEnv(environment.getProperty("ENVIRONMENT"))){
				env =  environment.getProperty("ENVIRONMENT");
			}else{
				errorMessage = "Invalid environment configured, please contact the technical team!";
			}
			if (errorMessage == null && !EVToolsUtils.isValidIP(ip)) {
				errorMessage = "Invalid IP '"+ip+"' address is provided!";
			}
			
			if (errorMessage == null && !request.isUserInRole("ROLE_EDIT_IPBLOCKER")){
				errorMessage = "You are not authorized for this action";
			}
			
			if(errorMessage == null){
				BlockedIPStatus ipstatus = dynamoDBService.loadBlockedIPStatus(ip, env);
				if(ipstatus != null){
					dynamoDBService.deleteBlockedIP(ipstatus);
				}else{
					errorMessage = "IP '"+ip+"' you are trying to remove is not part of blocked list.";
				}
			}
			if(errorMessage != null){
				logger.info("Request 'removeip' validation failed for the ip '"+ip+"' and the environment '"+env+"'");
				redirectAttributes.addFlashAttribute("error", errorMessage);
			}else{
				redirectAttributes.addFlashAttribute("message", "The ip '" + ip + "' has been successfully removed for '"+env+"' environment.");
				logger.info("Request 'removeip' has been processed successfuly for the ip '"+ip+"' and the '"+env+"' environment.");
			}
			return "redirect:/app/ipblocker";
     }
	 
	 @RequestMapping(value="/app/iphistory", method=RequestMethod.GET)
	 public ModelAndView iphistory(@RequestParam(value="ip", required=true) String ip) throws AWSAccessException{
        String env = "";
		String errorMessage = null;
		if(EVToolsUtils.isValidEnv(environment.getProperty("ENVIRONMENT"))){
			env =  environment.getProperty("ENVIRONMENT");
		}else{
			errorMessage = "Invalid environment configured, please contact the technical team!";
		}
		if (errorMessage == null && !EVToolsUtils.isValidIP(ip)) {
			errorMessage = "Invalid IP '"+ip+"' address is provided!";
		}
		
		ModelAndView modelAndView = new ModelAndView("ipblockerhistory");
		if(errorMessage == null){
			List<BlockedIPEvent> blockedIPEvents = dynamoDBService.getByTimePeriod(ip, env, EVToolsUtils.TimePeriod.LASTYEAR);
			modelAndView.addObject("blockedIPEvents", blockedIPEvents);
		}
		modelAndView.addObject("ip", ip);
		if(errorMessage != null){
			logger.info("Request 'iphistory' validation failed for the ip '"+ip+"' and the environment '"+env+"'");
			modelAndView.addObject("error", errorMessage);
		}
		
		return modelAndView;
	 }
	 
	@ExceptionHandler({AWSAccessException.class,Exception.class})
	public ModelAndView handleDocumentBuilderException(Exception ex){
		logger.error("An error occured while processing the request, exception : "+ex.getMessage());
		ModelAndView model = new ModelAndView("error");
		model.addObject("error", "A system error occured, please try again later!");
		return model;
	}
}
