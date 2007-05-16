
package org.ei.data.encompasslit.runtime;
import org.apache.oro.text.perl.Perl5Util;
import org.ei.domain.DataDictionary;
import java.util.Hashtable;

public class EltDataDictionary implements DataDictionary
{
    
        private Hashtable classCodes;

        public String getClassCodeTitle(String classCode)
        {
            Perl5Util p5 = new Perl5Util();
            String title = p5.substitute("s/(\\w+)/\\u$1/g",classCode.toLowerCase());
            return title;
    	}

        public Hashtable getClassCodes()
        {
            return this.classCodes;
        }
        public Hashtable getTreatments()
        {
            return null;
        }
        public EltDataDictionary()
        {

        }
    
}
