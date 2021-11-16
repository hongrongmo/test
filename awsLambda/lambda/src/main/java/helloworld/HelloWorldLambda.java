package helloworld;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


	public class HelloWorldLambda implements RequestHandler <String,String>
	{
	    /**
	     * 
	     */
	    private static final long serialVersionUID = 6998188851979224629L;

	   
	    public HelloWorldLambda()
	    {
	                
	    }
	    
	    public String handleRequest(String text, Context context) 
	    {
	        System.out.println("Hello World from AWS Lambda!");
	        return text;
	    }

		/*public String handleRequest(Object count, Context arg1) {
			 System.out.println("Hello World from AWS Lambda!");
			return count.toString();
		}*/
	}

