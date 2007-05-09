package org.ei.domain.lookup;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ei.domain.Database;


public class IndexItem
{
    private String name;
    private String value;
    private List dbases = new ArrayList();

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return this.value;
    }

    public List getDatabases()
    {
        Collections.sort(dbases);
        return dbases;
    }

    public void addDatabase(Database dbase)
    {
        if(!this.dbases.contains(dbase))
        {
            this.dbases.add(dbase);
        }
    }

}
