package test.org.ei.service.properties;


import org.ei.service.properties.RuntimePropertiesServiceException;
import org.ei.service.properties.RuntimePropertiesServiceImpl;
import org.ei.service.properties.DAO.RuntimePropertiesDAO;
import org.ei.service.properties.DAO.RuntimePropertiesDAOException;
import org.testng.Assert;
import org.testng.TestNG;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;



public class RuntimePropertiesServiceTest extends TestNG
{
   RuntimePropertiesDAO mockDao= null;
//
//   @BeforeTest
//	public void setup(){
//	   mockDao= mock(RuntimePropertiesDAO.class);
//	}
//
//
//	/**
//	 * @throws RuntimePropertiesDAOException
//	 * @throws RuntimePropertiesServiceException
//	 */
//	@Test
//	public void TestgetSSOCoreRedirectFlagWhenFlagIsNull() throws RuntimePropertiesServiceException, RuntimePropertiesDAOException{
//		RuntimePropertiesServiceImpl service= new RuntimePropertiesServiceImpl();
//		service.setDao(mockDao);
//		when(mockDao.getSSOCoreRedirectFlag()).thenReturn(null);
//		Assert.assertEquals(false, service.getSSOCoreRedirectFlag());
//	}
//
//	@Test
//	public void TestgetSSOCoreRedirectFlagWhenFlagIsFalse() throws RuntimePropertiesServiceException, RuntimePropertiesDAOException{
//		RuntimePropertiesServiceImpl service= new RuntimePropertiesServiceImpl();
//		service.setDao(mockDao);
//		when(mockDao.getSSOCoreRedirectFlag()).thenReturn("false");
//		Assert.assertEquals(false, service.getSSOCoreRedirectFlag());
//	}
//
//	@Test
//	public void TestgetSSOCoreRedirectFlagWhenFlagIsTrue() throws RuntimePropertiesServiceException, RuntimePropertiesDAOException{
//		RuntimePropertiesServiceImpl service= new RuntimePropertiesServiceImpl();
//		service.setDao(mockDao);
//		when(mockDao.getSSOCoreRedirectFlag()).thenReturn("true");
//		Assert.assertEquals(true, service.getSSOCoreRedirectFlag());
//	}

}