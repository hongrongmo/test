
package org.ei.data.test;

/**
 * @author solovyevat
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FastSearchUnitTestYRCountsFactory
{
    private static FastSearchUnitTestYRCounts test;
    public static void main(String [] args) throws Exception
    {
        System.out.println("start test for db:"+ args[0]);
        System.out.println("input file:"+ args[1]);
        System.out.println("output file:"+ args[2]);
        FastSearchUnitTestYRCountsFactory factory = new FastSearchUnitTestYRCountsFactory();
        test = factory.setFastSearchTest(args[0],args[1], args[2]);           
        test.runTest();
    }
    
    public FastSearchUnitTestYRCounts setFastSearchTest (String db,
            							  		 		 String inputFile,
            							  		 		 String outputFile) throws Exception
    {
        if (db.equals("elt"))
        {

            return new FastSearchUnitTestYRCounts(inputFile,outputFile);
        }
        // add object creation methods when needed 
        else
        {
            return new FastSearchUnitTestYRCounts(inputFile,outputFile);
        }
      
    }
    
    

}
