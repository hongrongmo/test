package org.ei.service.cars.security.authorization;


public final class UserAccessTypeFactory {

	private static UserAccessTypeFactory c_instance = new UserAccessTypeFactory();
    
	
    private UserAccessTypeFactory() {
    }
    
	public UserAccessType getUserAccessType(String typeName) {
        return (UserAccessType) UserAccessType.getAllTypes().get(typeName);
    }
    
    public static UserAccessTypeFactory getInstance() {
        return c_instance;
    }
}
