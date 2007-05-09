package org.ei.domain.lookup;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.ei.domain.Database;

public class DeDuper
{

    private Hashtable table = new Hashtable();
    private ArrayList orderedList = new ArrayList();


    public void clear()
    {
        table = new Hashtable();
        orderedList = new ArrayList();
    }

    public void put(String name,
                    String value,
                    Database database)
    {
        if(table.containsKey(name))
        {
            IndexItem item = (IndexItem)table.get(name);
            item.addDatabase(database);
        }
        else
        {
            IndexItem item = new IndexItem();
            item.setName(name);
            item.setValue(value);
            item.addDatabase(database);
            table.put(name, item);
            orderedList.add(item);
        }
    }



    public int size()
    {
        return orderedList.size();
    }

    public boolean contains(String key)
    {
        return table.containsKey(key);
    }

    public List getIndexItems()
    {
        return orderedList;
    }
}