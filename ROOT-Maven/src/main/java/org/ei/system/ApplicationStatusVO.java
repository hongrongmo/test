package org.ei.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.ei.session.UserSession;

public class ApplicationStatusVO {
    private String title;
    private String message;
    private boolean main;
    private boolean environment;
    private boolean userinfo;
    private boolean simulatedip;
    private boolean openxml;
    private boolean openrss;
    private boolean openurl;
    private boolean usage;

    private UserSession usersession;
    private Queue<NameValuePair> webappproperties;
    private Map<String,String> runtimeproperties = new HashMap<String,String>();
   
	private Queue<NameValuePair> requestproperties;
    
    private List<String> customerImages = new ArrayList<String>();
    private String customerImagePath = null;

   

	// Utility inner class for name/value pair entries
    public static class NameValuePair {
        public NameValuePair(String name, String value) {
            this.name = name;
            this.value = value;
        }
        private String name;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
        private String value;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isMain() {
        return main;
    }
    public void setMain(boolean main) {
        this.main = main;
    }
    public boolean isEnvironment() {
        return environment;
    }
    public void setEnvironment(boolean environment) {
        this.environment = environment;
    }
    public boolean isUserinfo() {
        return userinfo;
    }
    public void setUserinfo(boolean userinfo) {
        this.userinfo = userinfo;
    }
    public boolean isSimulatedip() {
        return this.simulatedip;
    }

    public void setSimulatedip(boolean simulatedip) {
        this.simulatedip = simulatedip;
    }

    public boolean isOpenxml() {
        return openxml;
    }
    public void setOpenxml(boolean openxml) {
        this.openxml = openxml;
    }
    public boolean isOpenrss() {
        return openrss;
    }
    public void setOpenrss(boolean openrss) {
        this.openrss = openrss;
    }
    public boolean isOpenurl() {
		return openurl;
	}

	public void setOpenurl(boolean openurl) {
		this.openurl = openurl;
	}

	public boolean isUsage() {
		return usage;
	}

	public void setUsage(boolean usage) {
		this.usage = usage;
	}

	public Map<String,String> getRuntimeproperties() {
        return runtimeproperties;
    }
	
	public void setRuntimeproperties(Map<String, String> runtimeproperties) {
		this.runtimeproperties = runtimeproperties;
	}


    public Queue<NameValuePair> getWebappproperties() {
        if (webappproperties == null) this.webappproperties = new LinkedList<NameValuePair>();
        return webappproperties;
    }
    
    public Queue<NameValuePair> getRequestproperties() {
        if (requestproperties == null) this.requestproperties = new LinkedList<NameValuePair>();
        return requestproperties;
    }

    public UserSession getUsersession() {
        return usersession;
    }

    public void setUsersession(UserSession usersession) {
        this.usersession = usersession;
    }
    
    public List<String> getCustomerImages() {
		return customerImages;
	}

	public void setCustomerImages(List<String> customerImages) {
		this.customerImages = customerImages;
	}

	public String getCustomerImagePath() {
		return customerImagePath;
	}

	public void setCustomerImagePath(String customerImagepath) {
		this.customerImagePath = customerImagepath;
	}
}
