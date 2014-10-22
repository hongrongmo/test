/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/customeroptions/village/ev2/Ev2Form.java-arc   1.7   Feb 25 2009 11:10:30   johna  $
 * $Revision:   1.7  $
 * $Date:   Feb 25 2009 11:10:30  $
 *
 */
package org.ei.struts.backoffice.customeroptions.village.ev2;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.util.LabelValueBean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.customeroptions.OptionConstants;
import org.ei.struts.backoffice.customeroptions.village.Village;
import org.ei.struts.backoffice.customeroptions.village.VillageForm;

import org.ei.struts.backoffice.customeroptions.databases.*;

/**
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.7  $
 */

public final class Ev2Form extends VillageForm {

  private static Log log = LogFactory.getLog("Ev2Form");

  public Ev2Form() {
    setAction(Constants.ACTION_CREATE);
    setOptions(new Ev2());
  }

  public Collection getAllAutostemOptions() { return OptionConstants.getOnOffOption(); }

  public Collection getAllSortByOptions() { return OptionConstants.getDefaultSortByOptions( getOptions().getProduct() ); }

  public Collection getAllStartPages() { return OptionConstants.getDefaultStartPageOptions( getOptions().getProduct() ); }

  public Collection getAllLitbulletins() { return OptionConstants.getAllLitbulletins( getOptions().getProduct() ); }

  public Collection getAllPatbulletins() { return OptionConstants.getAllPatbulletins( getOptions().getProduct() ); }

  public Collection getAllReferexOptions() { return OptionConstants.getReferexOptions(); }

  public Collection getUserInterfaceOptions() { return OptionConstants.getUserInterfaceOptions(); }

  public Collection getMoreDBOptions() { return OptionConstants.getMoreDBOptions(getOptions().getProduct() ); }


  public Collection getAllDatabaseMasks() {

    List availableDefaultDatabases = new ArrayList();
    Collection dbarray = (new Ev2()).getDatabaseArray();
    Iterator itrDbs = dbarray.iterator();
    while(itrDbs.hasNext())
    {
      Database db = (Database) itrDbs.next();
      availableDefaultDatabases.add(new LabelValueBean(db.getCartridge(),String.valueOf(db.getMask())));
    }

    availableDefaultDatabases.add(new LabelValueBean(OptionConstants.UPO_Cartridge, "8"));
    availableDefaultDatabases.add(new LabelValueBean(OptionConstants.CRC_Cartridge, "16"));

    return availableDefaultDatabases;
  }

  public Collection getAllDatabaseOptions() {

    List availableDefaultDatabases = new ArrayList();
    Collection dbarray = (new Ev2()).getDatabaseArray();
    Iterator itrDbs = dbarray.iterator();
    while(itrDbs.hasNext())
    {
      Database db = (Database) itrDbs.next();
      availableDefaultDatabases.add(new LabelValueBean(db.getName(),String.valueOf(db.getMask())));
    }

    availableDefaultDatabases.add(new LabelValueBean(OptionConstants.UPO_Label, "8"));
    availableDefaultDatabases.add(new LabelValueBean(OptionConstants.CRC_Label, "16"));

    return availableDefaultDatabases;
  }

//  public Collection getAllDatabaseOptions() { return OptionConstants.getDefaultDatabaseOptions( getOptions().getProduct() ); }


  // --------------------------------------------------------- Public Methods

  /**
   * Reset all properties to their default values.
   *
   * @param mapping The mapping used to select this instance
   * @param request The servlet request we are processing
   */
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    setAction(Constants.ACTION_CREATE);
    setOptions(new Ev2());
  }

  /**
   * Validate the properties that have been set from this HTTP request,
   * and return an <code>ActionErrors</code> object that encapsulates any
   * validation errors that have been found.  If no errors are found, return
   * <code>null</code> or an <code>ActionErrors</code> object with no
   * recorded error messages.
   *
   * @param mapping The mapping used to select this instance
   * @param request The servlet request we are processing
   */
  public ActionErrors validate(
    ActionMapping mapping,
    HttpServletRequest request) {

    MessageResources resources = (MessageResources) request.getAttribute( Globals.MESSAGES_KEY );
    ActionErrors errors = super.validate(mapping, request);

    Village opt = (Ev2) getOptions();

    if(opt.getSelectedOptions() == null) {
      errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.cartridge"));
    }
    else if(opt.getSelectedOptions().length == 0) {
      errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.cartridge"));
    }
    List optionslist = Arrays.asList(opt.getSelectedOptions());

    // Check to make sure standalone backfiles are not selected along with frontfiles or integrated backfiles
    if(optionslist.contains(OptionConstants.CPX_Cartridge) && optionslist.contains(OptionConstants.ZBF_Cartridge))
    {
      errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.standaloneconflict", OptionConstants.ZBF_Label, OptionConstants.CPX_Label));
    }
    if(optionslist.contains(OptionConstants.C84_Cartridge) && optionslist.contains(OptionConstants.ZBF_Cartridge))
    {
      errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.standaloneconflict", OptionConstants.ZBF_Label, OptionConstants.C84_Label));
    }
    if(optionslist.contains(OptionConstants.INS_Cartridge) && optionslist.contains(OptionConstants.IBS_Cartridge))
    {
      errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.standaloneconflict", OptionConstants.IBS_Label, OptionConstants.INS_Label));
    }
    if(optionslist.contains(OptionConstants.IBF_Cartridge) && optionslist.contains(OptionConstants.IBS_Cartridge))
    {
      errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.standaloneconflict", OptionConstants.IBS_Label, OptionConstants.IBF_Label));
    }

    // Check to make sure integrated backfiles are selected along with frontfiles
    if(optionslist.contains(OptionConstants.C84_Cartridge) && !optionslist.contains(OptionConstants.CPX_Cartridge))
    {
      errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.backfileconflict", OptionConstants.C84_Label, OptionConstants.CPX_Label, OptionConstants.ZBF_Label));
    }
    if(optionslist.contains(OptionConstants.IBF_Cartridge) && !optionslist.contains(OptionConstants.INS_Cartridge))
    {
      errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.backfileconflict", OptionConstants.IBF_Label, OptionConstants.INS_Label, OptionConstants.IBS_Label));
    }

    /*
      This code found conflicts between non-selected DBs and set default DBs
      New JS code prevents this from happening so it is commented out

    Collection alldbs = ((Ev2) getOptions()).getDatabaseArray();

    List defaultlist = Arrays.asList(opt.getSelectedDefaultDatabases());
    Iterator itrdefs = defaultlist.iterator();
    while(itrdefs.hasNext())
    {

      String intmask = (String) itrdefs.next();
      String cart = "";
      // convert string to int and get corresponding cartridge
      Iterator itralldbs = alldbs.iterator();
      Database db = null;
      while(itralldbs.hasNext())
      {
        db = (Database) itralldbs.next();
        if(intmask.equals(String.valueOf(db.getMask()))) {
          break;
        }
      }
      if(db != null) {
        cart = db.getCartridge();

        // check to make sure each database set as a default is also selected as available an database option
        // also this will not work for Books since PAG is not actually a cartridge so skip PAG
        if(!Constants.EMPTY_STRING.equals(cart) && !OptionConstants.PAG_Cartridge.equals(cart) && !optionslist.contains(cart))
        {
          errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.defaultdatabase", db.getName()));
        }
      }
    }
    */
    return errors;
  }
}