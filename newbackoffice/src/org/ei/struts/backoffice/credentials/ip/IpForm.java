package org.ei.struts.backoffice.credentials.ip;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.credentials.access.AccessForm;

public class IpForm extends AccessForm {

  public IpForm() {
    setAction(Constants.ACTION_CREATE);
    setCredentials(new Ip());
  }

  private static Log log = LogFactory.getLog("IpForm");

  /**
   * The maintenance action we are performing (Create or Edit).
   */
//  private String action = Constants.ACTION_CREATE;
  /**
   * Return the maintenance action.
   * Set the maintenance action.
   */
//  public String getAction() { return (this.action); }
//  public void setAction(String action) { this.action = action; }

//  private Credentials m_ip = new Ip();
//  public Credentials getCredentials() { return (this.m_ip); }
//  public void setCredentials(Credentials ip) { this.m_ip = ip; }

  // -- ValidatorForm methods
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    setAction(Constants.ACTION_CREATE);
    setCredentials(new Ip());
  }
  private static final Pattern IPWILDCARD_PATTERN = Pattern.compile("\\.\\*$");

  public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {

    MessageResources resources = (MessageResources) request.getAttribute( Globals.MESSAGES_KEY );
    ActionErrors errors = super.validate(mapping, request);

    Perl5Util perl = new Perl5Util();
    //Ip ip = (Ip) getAccess().getCredentials();
    Ip ip = (Ip) getCredentials();
    String lowIP = ip.getLowIp();
    String highIP = ip.getHighIp();
    String blockIP = ip.getIpBlock();

    if(
        (blockIP != null) && !blockIP.equals(Constants.EMPTY_STRING)
        &&
        (
          ((lowIP != null) && !lowIP.equals(Constants.EMPTY_STRING)
          ||
          (highIP != null) && !highIP.equals(Constants.EMPTY_STRING))
        )
      )
    {
      errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.ipfieldconflict", resources.getMessage("prompt.lowip")));
      return errors;
    }

    if((blockIP != null) && !blockIP.equals(Constants.EMPTY_STRING))
    {

      // remove everything that is not a number, dot, dash or semicolon
      blockIP = blockIP.replaceAll("(\\r\\n|\\r|\\n)",";");
      blockIP = blockIP.replaceAll("[^\\d\\.\\-;]",Constants.EMPTY_STRING);
      String[] ips = blockIP.split(";");
      for (int i = 0; i < ips.length; i++)
      {
        if(ips[i] != null)
        {
          if(ips[i].equals(Constants.EMPTY_STRING))
          {
            continue;
          }
          else
          {
            IpRange iprange = new IpRange();
            String[] ipnums = ips[i].split("-");
            if(ipnums.length == 1)
            {
              iprange.setLowIp(ipnums[0]);
              iprange.setHighIp(ipnums[0]);
            }
            if(ipnums.length == 2)
            {
              iprange.setLowIp(ipnums[0]);
              // handle high IP from entry like 130.192.1.0-127
              String strHighIP = ipnums[1];
              if(strHighIP != null && strHighIP.length() <= 3)
              {
                String strFirstThreeBytes = ipnums[0].substring(0,ipnums[0].lastIndexOf("."));
                strHighIP = strFirstThreeBytes.concat(".").concat(strHighIP);
                log.info(" strFirstThreeBytes: " + strFirstThreeBytes);
              }
              log.info(" strHighIP: " + strHighIP);
              iprange.setHighIp(strHighIP);
            }
            if(iprange.getLowIp() != null && iprange.getHighIp() != null )
            {
              log.info("adding Block IpRange" + iprange.toString());
              ip.addIpRange(iprange);
            }
          }
        }
      }
    }
    else
    {

      if((lowIP == null) || lowIP.equals(Constants.EMPTY_STRING)) {
        errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.required", resources.getMessage("prompt.lowip")));
      }
      else if(lowIP != null) {
        // replace wildcards on high and/or low ip addresses
        if(IPWILDCARD_PATTERN.matcher(lowIP).find()) {
          if((highIP == null) || highIP.equals(Constants.EMPTY_STRING))
          {
            highIP = lowIP;
            lowIP = IPWILDCARD_PATTERN.matcher(lowIP).replaceAll("\\.1");
            highIP = IPWILDCARD_PATTERN.matcher(highIP).replaceAll("\\.255");

            ip.setLowIp(lowIP);
            ip.setHighIp(highIP);
          }
          else // high IP is not null
          {
            errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.required", resources.getMessage("prompt.highip")));
          }
        }
      }

      // if high IP is STILL null - then treat single IP as a range of one IP, i.e. 10.10.0.1-10.10.0.1
      if((highIP == null) || highIP.equals(Constants.EMPTY_STRING)) {
        highIP = lowIP;
        ip.setHighIp(lowIP);
      }

      if((highIP == null) || highIP.equals(Constants.EMPTY_STRING)) {
        errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.required", resources.getMessage("prompt.highip")));
      }

      IpRange iprange = new IpRange();
      iprange.setLowIp(lowIP);
      iprange.setHighIp(highIP);

      log.info("adding Single IpRange" + iprange.toString());
      ip.addIpRange(iprange);
    }

    Iterator ipitr = ip.getIpRanges().iterator();
    while(ipitr.hasNext())
    {
      IpRange iprange = (IpRange) ipitr.next();
      highIP = iprange.getHighIp();
      lowIP = iprange.getLowIp();

      // test for valid IP addresses
      if((highIP != null) && (lowIP != null))
      {

        // only [0-9.] allowed
        if(!perl.match("/^[0-9.]*$/",highIP)) {
          errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.ip", resources.getMessage("prompt.highip"),highIP));
        }
        if(!perl.match("/^[0-9.]*$/",lowIP)) {
          errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.ip", resources.getMessage("prompt.lowip"),lowIP));
        }

        NumberFormat nf = NumberFormat.getNumberInstance();
        DecimalFormat df = (DecimalFormat)nf;
        // format bytes to three digits for storage
        df.applyPattern("000");

        // each 'byte' should be btwn 0-255
        if(perl.match("/^(\\d+).(\\d+).(\\d+).(\\d+)$/",lowIP) && (perl.groups()==5)) {
          String formattedIP = Constants.EMPTY_STRING;
          try {
            for(int x = 1; x <= 4; x++) {
              long aByte = Long.parseLong(perl.group(x));
              if(aByte > 255 || aByte < 0) {
                errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.ip.byte", new Integer(x), resources.getMessage("prompt.lowip"), lowIP));
              }
              formattedIP = formattedIP.concat(df.format(aByte));
            }
          }
          catch(NumberFormatException nfe) {
            errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.ip", resources.getMessage("prompt.lowip"),lowIP));
          }
          lowIP = formattedIP;
        }
        else {
          errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.ip", resources.getMessage("prompt.lowip"),lowIP));
        }

        // each 'byte' should be btwn 0-255
        if(perl.match("/^(\\d+).(\\d+).(\\d+).(\\d+)$/",highIP) && (perl.groups()==5)) {
          String formattedIP = Constants.EMPTY_STRING;
          try {
            for(int x = 1; x <= 4; x++) {
              long aByte = Long.parseLong(perl.group(x));
              if((aByte > 255) || (aByte < 0)) {
                errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.ip.byte", new Integer(x), resources.getMessage("prompt.highip"), highIP));
              }
              formattedIP = formattedIP.concat(df.format(aByte));
            }
          }
          catch(NumberFormatException nfe) {
            errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.ip", resources.getMessage("prompt.highip"),highIP));
          }
          highIP = formattedIP;
        }
        else {
          errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.ip", resources.getMessage("prompt.highip"),highIP));
        }

        // check high and low
        if((lowIP != null) && (highIP != null)) {
          try {
            if(Long.parseLong(lowIP) > Long.parseLong(highIP)) {
              errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.invalidiprange", Constants.formatIpAddress(highIP), Constants.formatIpAddress(lowIP)));
            }
          }
          catch(NumberFormatException nfe) {
            // eat the exception, lower on we test for non numeric input
          }
        }

        // Test the first two bytes to make sure this address is not too broad
        // i.e. 10.10.x.x-10.11.x.x is too broad
        // i.e. 10.x.x.x-11.x.x.x is WAY too broad
        // NOTE: the IP addresses here have been 'cleaned' - that is there are no '.' separating the bytes
        if(highIP.length() > 6 && lowIP.length() > 6 && !highIP.substring(0,6).equals(lowIP.substring(0,6)))
        {
          log.info(highIP.substring(0,6));
          log.info(lowIP.substring(0,6));

          errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.toobroad", Constants.formatIpAddress(lowIP), Constants.formatIpAddress(highIP)));
        }

      } // if((highIP != null) && (lowIP != null))

      if(errors.size() > 5) {
        errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.toomany"));
        break;
      }

    } // while

    return errors;
  }
}
