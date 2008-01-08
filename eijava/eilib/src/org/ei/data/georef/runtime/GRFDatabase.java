package org.ei.data.georef.runtime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.ei.domain.DataDictionary;
import org.ei.domain.Database;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.FastSearchControl;
import org.ei.domain.SearchControl;
import org.ei.domain.SearchField;
import org.ei.domain.sort.SortField;
import org.ei.data.compendex.runtime.CPXLinkingStrategy;
import org.ei.fulldoc.LinkingStrategy;

public class GRFDatabase extends Database
{
    private LinkingStrategy linkingStrategy = new CPXLinkingStrategy();
    private static Map searchfield = new HashMap();
    static
    {
      searchfield.put("ALL", "Y");
      searchfield.put("AB", "Y");
      searchfield.put("AN", "Y");
      searchfield.put("AU", "Y");
      searchfield.put("AF", "Y");
      searchfield.put("CL", "Y");
      searchfield.put("CV", "Y");
      searchfield.put("DT", "Y");
      searchfield.put("BN", "Y");
      searchfield.put("SN", "Y");
      searchfield.put("LA", "Y");
      searchfield.put("PN", "Y");
      searchfield.put("ST", "Y");
      searchfield.put("KY", "Y");
      searchfield.put("TI", "Y");
      searchfield.put("FL", "Y");
      searchfield.put("RGI", "Y");
      searchfield.put("CO", "Y");
    }

    public List getSortableFields()
    {
      return Arrays.asList(new SortField[]{SortField.RELEVANCE, SortField.AUTHOR, SortField.YEAR, SortField.SOURCE});
    }

    private DataDictionary dataDictionary = new GRFDataDictionary();
    public int getStartYear(boolean hasBackFile)
    {
      return 1973;
    }

    protected String getBaseTableHook()
    {
      return "grf_master";
    }


    public DataDictionary getDataDictionary()
    {
      return dataDictionary;
    }

    public DocumentBuilder newBuilderInstance()
    {
      return new GRFDocBuilder(this);
    }

    public SearchControl newSearchControlInstance()
    {
      return new FastSearchControl();
    }

    public String getID()
    {
      return "grf";
    }

    public String getLegendID()
    {
      return "f";
    }

    public String getName()
    {
      return "GeoRef";
    }

    public String getIndexName()
    {
      return "grf";
    }

    public String getShortName()
    {
      return "GeoRef";
    }

    public int getMask()
    {
      return 2097152;
    }

    public int getSortValue()
    {
      return getMask();
    }

    public Map getTreatments()
    {

      Map mp = new Hashtable();

      return mp;
    }

    public boolean hasField(SearchField searchField, int userMaskMax)
    {
      return searchfield.containsKey(searchField.getID());
    }

    public LinkingStrategy getLinkingStrategy()
    {
        return linkingStrategy;
    }

    public boolean linkLocalHoldings(String linklabel)
    {
      if(linklabel.indexOf("NTIS") > -1)
      {
        return false;
      }
      else
      {
        return true;
      }
    }

    public String getSingleCharName()
    {
      return "X";
    }
}