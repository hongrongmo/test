package org.ei.util.test;

import org.ei.connectionpool.*;
import org.ei.util.GUID;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestPA
{
    public TestPA()
    {
    }
    
    public void run()
        throws Exception
    {
        Iterator itrDocs = null;
        List allDocs = null;
        
        PersonalizationProcedure personalproc = new PersonalizationProcedure();
        
        personalproc.createUserProfile();
        personalproc.touch();
        personalproc.updateUserProfile();

        SavedRecordProcedure savedproc = new SavedRecordProcedure();
        
        savedproc.setUserProfileID(personalproc.getUserProfileID());
        
        savedproc.addFolder();
        savedproc.renameFolder();
        savedproc.removeAllInFolder();

        savedproc.addSelectedRecord();
        savedproc.removeSelected();

        allDocs = new ArrayList();
        for(int i = 0; i < 40; i++)
        {
            allDocs.add(new GUID().toString());
        }
        
        itrDocs = allDocs.iterator();
        while(itrDocs.hasNext())
        {
            savedproc.setDocID((String) itrDocs.next());
            savedproc.addSelectedRecord();
        }
        itrDocs = allDocs.iterator();
        while(itrDocs.hasNext())
        {
            savedproc.setDocID((String) itrDocs.next());
            savedproc.removeSelected();
        }
        itrDocs = allDocs.iterator();
        while(itrDocs.hasNext())
        {
            savedproc.setDocID((String) itrDocs.next());
            savedproc.addSelectedRecord();
        }
        savedproc.removeAllInFolder();
        
        savedproc.removeFolder();

        personalproc.removeUserProfile();
        
    }        
   
}
