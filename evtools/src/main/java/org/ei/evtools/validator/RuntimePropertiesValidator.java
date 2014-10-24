package org.ei.evtools.validator;

import org.ei.evtools.model.RuntimePropertiesForm;
import org.ei.evtools.utils.EVToolsUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
public class RuntimePropertiesValidator implements Validator {
	 
	@Override
	public boolean supports(Class<?> clazz) {
		return RuntimePropertiesForm.class.equals(clazz);

	}

	@Override
	public void validate(Object target, Errors errors) {
		RuntimePropertiesForm form = (RuntimePropertiesForm) target;
		if(isEmptyorNull(form.getRuntimepropkey()) || 
			isEmptyorNull(form.getRuntimepropkeyvalue()) || 
			isEmptyorNull(form.getRuntimepropenvlevel()) ||
			!EVToolsUtils.isValidEnv(form.getRuntimepropenvlevel())){
			errors.rejectValue("runtimepropkey", "Some validation Error occured!");
		}
	}

	private boolean isEmptyorNull(String value){
    	if(value == null || value.trim().equalsIgnoreCase("")) {
    		return true;
    	}
    	return false;
    }
		 
}
