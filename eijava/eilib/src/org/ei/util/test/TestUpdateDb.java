package org.ei.util.test;

import org.ei.connectionpool.*;
import org.ei.util.GUID;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestUpdateDb
{

    public void run()
        throws Exception
    {
        UpdateDbProcedure updateDbProc = new UpdateDbProcedure();

        Iterator itrDocs = null;
        List allDocs = null;

        allDocs = new ArrayList();
        for(int i = 0; i < 40; i++)
        {
            allDocs.add(new GUID().toString());
        }
        itrDocs = allDocs.iterator();
        while(itrDocs.hasNext())
        {
            updateDbProc.setDocId((String) itrDocs.next());
            updateDbProc.addAll();
        }

        itrDocs = allDocs.iterator();
        while(itrDocs.hasNext())
        {
            updateDbProc.setDocId((String) itrDocs.next());
            updateDbProc.removeSelected();
        }

        updateDbProc.removeAll();

        updateDbProc.updateClearOnNewSearch();
    }

}
