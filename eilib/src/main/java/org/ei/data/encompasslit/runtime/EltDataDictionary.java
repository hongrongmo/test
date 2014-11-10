
package org.ei.data.encompasslit.runtime;
import java.util.Hashtable;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.domain.DataDictionary;

public class EltDataDictionary implements DataDictionary
{
    
        private Hashtable<String, String> classCodes;

        public String getClassCodeTitle(String classCode)
        {
            Perl5Util p5 = new Perl5Util();
            String title = p5.substitute("s/(\\w+)/\\u$1/g",classCode.toLowerCase());
            return title;
    	}
        public Hashtable<String, String> getAuthorityCodes()
        {
        	return null;
        }

        public Hashtable<String, String> getClassCodes()
        {
            return this.classCodes;
        }
        public Hashtable<String, String> getTreatments()
        {
            return null;
        }
        public EltDataDictionary()
        {

        }
        public String getTreatmentTitle(String mTreatmentCode)
        {
        	return null;
        }
    
}
