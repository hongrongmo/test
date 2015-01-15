/**
 * Date: Thursday 01/08/2015
 * Description: test why <DEDUPKEY> of INS fast extract did not start with M, while from eijava it start with M
 * Conclusion: when UserID value generated starts with "-" then GUID value will start with "M"
 * when run this test class sometimes get GUID start with "M" and sometime not
 */
package org.ei.util;

import org.ei.util.*;

import java.lang.*;
import java.io.*;
/**
 * @author TELEBH
 *
 */
public class TestGUID {

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args){
        // TODO Auto-generated method stub
        try
        {
        System.out.println((new GUID()).toString());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

}
