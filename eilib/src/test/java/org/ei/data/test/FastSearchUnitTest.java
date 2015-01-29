/*
 * Created on Nov 13, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ei.data.test;

public abstract class FastSearchUnitTest
{
    protected String inputFile;
    protected String outputFile;
    
    public FastSearchUnitTest(String inFile, 
            				  String outFile)
    {
        this.inputFile = inFile;
        this.outputFile = outFile;
    }
    
//  this method runs and writes tests 
    public abstract void runTest() throws Exception;
}
