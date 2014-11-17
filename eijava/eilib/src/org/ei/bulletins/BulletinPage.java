package org.ei.bulletins;

import java.util.*;

public class BulletinPage {

    List lstBulletins = new Vector();

    /** 
     * @return
     */
    public void add(Bulletin bulletin) {

        lstBulletins.add(bulletin);
        
    }
    /** 
	 * @return
	 */
    public Bulletin get(int index) {

        Bulletin bulletin = (Bulletin) lstBulletins.get(index);

        return bulletin;
    }
	/** 
	 * @return
	 */
    public int size() {

        return lstBulletins.size();
    }
	/** 
	 * @return
	 */
    public List getBulletins() {

        return lstBulletins;
    }
	public void accept(BulletinXMLVisitor v)
			throws Exception
		{
			v.visitWith(this);
		}
}