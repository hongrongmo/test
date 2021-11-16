
public class SimpleTests {

	 public int getValue()
	    {

	        int z = 10;
	        final int y = 40 + z;
	        System.out.println(y);
	        return 5;
	    }

	    public static void main(String[] args) {
	    	SimpleTests obj = new SimpleTests();
	        final int x = obj.getValue();
	        System.out.println(x);


	    }
}
