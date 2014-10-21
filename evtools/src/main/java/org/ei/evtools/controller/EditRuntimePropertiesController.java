package org.ei.evtools.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.ei.evtools.db.domain.RunTimeProperties;
import org.ei.evtools.db.services.RunTimePropertiesService;
import org.ei.evtools.exception.AWSAccessException;
import org.ei.evtools.model.RuntimePropertiesForm;
import org.ei.evtools.utils.EVToolsUtils;
import org.ei.evtools.validator.RuntimePropertiesValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
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
public class EditRuntimePropertiesController {
	
	private static Logger logger = Logger.getLogger(EditRuntimePropertiesController.class);
	
	@Autowired
	RunTimePropertiesService runTimePropertiesService; 

	@RequestMapping(value="/app/editruntimeprops")
	public ModelAndView editruntimeprops(@RequestParam(value="environment", required=false) String environment,HttpServletResponse response) throws IOException, AWSAccessException{
		
		if( environment == null || !RunTimeProperties.getEnvRunLevels().contains(environment)){
			environment =  RunTimeProperties.ATTRIBUTE_PROD;
	    }
		List<RunTimeProperties> listUsers = runTimePropertiesService.getDefaultAndCurrentEnvironmentProps(environment);
		ModelAndView model = new ModelAndView("editruntimeprops");
		model.addObject("envprops", listUsers);
		model.addObject("envrunlevels", RunTimeProperties.getEnvRunLevels());
		model.addObject("environment", environment);
		model.addObject("pagetype", "editruntimeprops");
		model.addObject("evform", new RuntimePropertiesForm());
		logger.info("Request 'editruntimeprops' has been processed successfuly for the environment '"+environment+"'");
		return model;
	}
	
	@RequestMapping(value="/app/updateruntimeproperties", method=RequestMethod.POST)
    public String updateruntimeproperties(@ModelAttribute("evform") RuntimePropertiesForm form, BindingResult result,HttpServletRequest request,RedirectAttributes redirectAttributes) throws AWSAccessException{
        RuntimePropertiesValidator runtimePropertiesValidator = new RuntimePropertiesValidator();
        runtimePropertiesValidator.validate(form, result);
       
        if (result.hasErrors()){
        	logger.info("Request 'updateruntimeproperties' validation failed for the key '"+form.getRuntimepropkey()+"' and the environment '"+form.getRuntimepropenvlevel()+"'");
        	redirectAttributes.addFlashAttribute("error", "Your save operation is failed, please try again!");
        }else {
        	RunTimeProperties runTimeProperties = runTimePropertiesService.load(form.getRuntimepropkey());
        	if(runTimeProperties != null){
    			setUserDataToCurrentEnv(form.getRuntimepropkeyvalue(),runTimeProperties, form.getRuntimepropenvlevel());
    			runTimePropertiesService.save(runTimeProperties);
    			redirectAttributes.addFlashAttribute("message", "The key '" + form.getRuntimepropkey() + "' has been successfully updated for '"+form.getRuntimepropenvlevel()+"' environment.");
    			logger.info("Request 'updateruntimeproperties' has been processed successfuly for the key '"+form.getRuntimepropkey()+"' and the environment '"+form.getRuntimepropenvlevel()+"'");
    		}else{
    			logger.info("Request 'updateruntimeproperties' failed for the key '"+form.getRuntimepropkey()+"' and the environment '"+form.getRuntimepropenvlevel()+"'");
    			redirectAttributes.addFlashAttribute("error", "Your save operation is failed, please try again!");
    		}
        }
        return "redirect:/app/editruntimeprops.htm?environment="+form.getRuntimepropenvlevel();
	}
	
	@RequestMapping(value="/app/removeruntimeproperties", method=RequestMethod.POST)
    public String removeruntimeproperties(@ModelAttribute("evform") RuntimePropertiesForm form, BindingResult result,HttpServletRequest request,RedirectAttributes redirectAttributes) throws AWSAccessException{
        
		if(EVToolsUtils.isEmptyorNull(form.getRuntimepropkey()) || 
				EVToolsUtils.isEmptyorNull(form.getRuntimepropenvlevel()) ||
				!RunTimeProperties.getEnvRunLevels().contains(form.getRuntimepropenvlevel())){
			logger.info("Request 'removeruntimeproperties' validation failed for the key '"+form.getRuntimepropkey()+"' and the environment '"+form.getRuntimepropenvlevel()+"'");
			redirectAttributes.addFlashAttribute("error", "Your remove operation is failed, please try again!");
		}else {
        	RunTimeProperties runTimeProperties = runTimePropertiesService.load(form.getRuntimepropkey());
        	if(runTimeProperties != null){
    			setUserDataToCurrentEnv(null,runTimeProperties, form.getRuntimepropenvlevel());
    			runTimePropertiesService.save(runTimeProperties);
    			redirectAttributes.addFlashAttribute("message", "The key '" + form.getRuntimepropkey() + "' has been successfully updated by removing '"+form.getRuntimepropenvlevel()+"' attribute.");
    			logger.info("Request 'removeruntimeproperties' has been processed successfuly for the key '"+form.getRuntimepropkey()+"' and the environment '"+form.getRuntimepropenvlevel()+"'");
    		}else{
    			logger.info("Request 'removeruntimeproperties' validation failed for the key '"+form.getRuntimepropkey()+"' and the environment '"+form.getRuntimepropenvlevel()+"'");
    			redirectAttributes.addFlashAttribute("error", "Your remove operation is failed, please try again!");
    		}
        }
        return "redirect:/app/editruntimeprops.htm?environment="+form.getRuntimepropenvlevel();
	}
	
	@ExceptionHandler({AWSAccessException.class, IOException.class, Exception.class})
	public ModelAndView handleDocumentBuilderException(Exception ex){
		logger.error("An error occured while processing the request, exception : "+ex.getMessage());
		ModelAndView model = new ModelAndView("error");
		model.addObject("error", "A system error occured, please try again later!");
		return model;
	}
	
	private void setUserDataToCurrentEnv(String dataValue,RunTimeProperties entity, String envlevel){
    	if(envlevel.equalsIgnoreCase(RunTimeProperties.ATTRIBUTE_CERT)){
    		entity.setCert(dataValue);
		}else if(envlevel.equalsIgnoreCase(RunTimeProperties.ATTRIBUTE_DEV)){
			entity.setDev(dataValue);
		}else if(envlevel.equalsIgnoreCase(RunTimeProperties.ATTRIBUTE_LOCAL)){
			entity.setLocal(dataValue);
		}else if(envlevel.equalsIgnoreCase(RunTimeProperties.ATTRIBUTE_PROD)){
			entity.setProd(dataValue);
		}else if(envlevel.equalsIgnoreCase(RunTimeProperties.ATTRIBUTE_RELEASE)){
			entity.setRelease(dataValue);
		}
    }
	
	
	
	
	
}
