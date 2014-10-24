package org.ei.util.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ei.util.GUID;

public class TestUpdateDb {

    public void run() throws Exception {
        UpdateDbProcedure updateDbProc = new UpdateDbProcedure();

        Iterator<String> itrDocs = null;
        List<String> allDocs = null;

        allDocs = new ArrayList<String>();
        for (int i = 0; i < 40; i++) {
            allDocs.add(new GUID().toString());
        }
        itrDocs = allDocs.iterator();
        while (itrDocs.hasNext()) {
            updateDbProc.setDocId((String) itrDocs.next());
            updateDbProc.addAll();
        }

        itrDocs = allDocs.iterator();
        while (itrDocs.hasNext()) {
            updateDbProc.setDocId((String) itrDocs.next());
            updateDbProc.removeSelected();
        }

        updateDbProc.removeAll();

        updateDbProc.updateClearOnNewSearch();
    }

}
