package org.ei.data.bd;

import java.util.*;
import org.ei.data.bd.loadtime.*;

public class BdDatabase
{
    private static final String BD = "bd";

    private static final Set bddatasources = new HashSet();
    static
    {
        bddatasources.add("cpx");
        bddatasources.add("geo");
        bddatasources.add("pch");
        bddatasources.add("chm");
        bddatasources.add("elt");
    }

    public boolean isBdDatabase(String dbKey)
    {
        if(bddatasources.contains(dbKey))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public String getID()
    {
        return BD;
    }

}