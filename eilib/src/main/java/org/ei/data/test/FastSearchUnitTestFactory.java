/*
 * Created on Nov 13, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ei.data.test;

/**
 * @author solovyevat
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FastSearchUnitTestFactory
{
    private static FastSearchUnitTest test;
    public static void main(String [] args) throws Exception
    {
        System.out.println("start test for db:"+ args[0]);
        System.out.println("input file:"+ args[1]);
        System.out.println("output file:"+ args[2]);
        FastSearchUnitTestFactory factory = new FastSearchUnitTestFactory();
        test = factory.setFastSearchTest(args[0],args[1], args[2]);           
        test.runTest();
    }
    
    public FastSearchUnitTest setFastSearchTest (String db,
            							  		 String inputFile,
            							  		 String outputFile) throws Exception
    {
        if (db.equals("elt"))
        {

            return new FastSearchUnitTestElt(inputFile,outputFile);
        }
        // add object creation methods when needed 
        // currently reuse FastSearchUnitTestElt
        else
        {
            return new FastSearchUnitTestElt(inputFile,outputFile);
        }

//        else if (db.equals("pch"))
//        {
//            return new FastSearchUnitTestPch(inputFile,outputFile);
//        }

        
    }
    
    

}
