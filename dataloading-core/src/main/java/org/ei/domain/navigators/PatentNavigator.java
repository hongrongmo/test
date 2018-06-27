/*
 * Created on Aug 10, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.domain.navigators;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JMoschet
 * 
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class PatentNavigator extends EiNavigator {
    public PatentNavigator(String name) {
        super(name);
    }

    public EiModifier createModifier(int i, String slable, String svalue) {
        slable = svalue.toUpperCase();

        slable = PatentCodeModifier.cleanPatentModifier(slable);
        svalue = PatentCodeModifier.cleanPatentModifier(svalue);

        return createPatentModifier(i, slable, svalue);
    }

    public abstract EiModifier createPatentModifier(int i, String slable, String svalue);

    public List<EiModifier> parseModifiers(String modsstring) {
        List<EiModifier> modifiers = new ArrayList<EiModifier>();

        String[] mods = modsstring.split(EiModifier.MODS_DELIM);
        for (int i = 0; i < mods.length; i++) {
            EiModifier mod = EiModifier.parseModifier(mods[i]);

            String label = PatentCodeModifier.cleanPatentModifier(mod.getLabel());
            String value = PatentCodeModifier.cleanPatentModifier(mod.getValue());

            modifiers.add(createModifier(mod.getCount(), label, value));
        }

        return modifiers;

    }
}
