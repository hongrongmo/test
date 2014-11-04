package org.ei.evtools.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.ei.evtools.cookies.SimulatedIPCookie;
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
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
@Controller
@PropertySource("classpath:application.properties")
public class SimulatedIPController {
	
	private static Logger logger = Logger.getLogger(SimulatedIPController.class);
	
	@Autowired
	private Environment environment; 
	
	@RequestMapping(value="/app/simulatedip")
	public ModelAndView simulatedip(HttpServletRequest request){
		
		Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
		String isErroredirectPresent = null;
		if (inputFlashMap != null) {
			isErroredirectPresent = (String) inputFlashMap.get("error");
		}
		
		String simulatedIP = null;
		if(isErroredirectPresent == null){
			simulatedIP = new SimulatedIPCookie(EVToolsUtils.getCookie(request, SimulatedIPCookie.SIMULATED_IP_COOKIE_NAME)).getSimulatedIP();
		}
		ModelAndView model = new ModelAndView("simulatedip");
		model.addObject("simulatedip", simulatedIP);
		model.addObject("pagetype", "simulatedip");
		model.addObject("domain", environment.getProperty("simulatedip.cookie.domain"));
		logger.info("Request 'simulatedip' has been processed successfuly.");
		return model;
	}
	
	@RequestMapping(value="/app/simulatedipsubmit", method=RequestMethod.POST)
    public String simulatedipsubmit(@RequestParam(value="ip", required=true) String ip, 
    		RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response){
       
		String errorMessage = null;
		if (EVToolsUtils.isValidIP(ip)) {
			EVToolsUtils.setCookie(request, response, new SimulatedIPCookie("11.22.33.44"),environment.getProperty("simulatedip.cookie.domain"),"/");
		}else{
			errorMessage = "Invalid IP address '"+ip+"' is provided!";
		}
		if(errorMessage != null){
			logger.info("Request 'simulatedipsubmit' validation failed for the ip '"+ip+"'");
			redirectAttributes.addFlashAttribute("error", errorMessage);
		}else{
			redirectAttributes.addFlashAttribute("message", "The simulate ip cookie has been successfully created for the ip '" + ip + "'.");
			logger.info("The simulate ip cookie has been successfully created for the ip '" + ip + "'.");
		}
		return "redirect:/app/simulatedip";
	}
	
	@RequestMapping(value="/app/simulatedipclear", method=RequestMethod.POST)
    public String simulatedipclear(RedirectAttributes redirectAttributes, HttpServletResponse response){
        response.addCookie(EVToolsUtils.clearCookie(SimulatedIPCookie.SIMULATED_IP_COOKIE_NAME, environment.getProperty("simulatedip.cookie.domain")));
		redirectAttributes.addFlashAttribute("message", "The simulate ip cookie has been successfully removed.");
		logger.info("The simulate ip cookie has been successfully removed.");
		return "redirect:/app/simulatedip";
	}
	
	@ExceptionHandler({Exception.class})
	public ModelAndView handleDocumentBuilderException(Exception ex){
		logger.error("An error occured while processing the request, exception : "+ex.getMessage());
		ModelAndView model = new ModelAndView("error");
		model.addObject("error", "A system error occured, please try again later!");
		return model;
	}
}
