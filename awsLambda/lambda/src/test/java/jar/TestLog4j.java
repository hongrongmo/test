package jar;

import org.apache.log4j.Logger;


public class TestLog4j {

	//Initialize the log4j logger 
		static final Logger log = Logger.getLogger(TestLog4j.class);
		
	public static void main(String[] args) {
		
		log.info("Test Log LEVEL when it is DEBUG");

	}

}
