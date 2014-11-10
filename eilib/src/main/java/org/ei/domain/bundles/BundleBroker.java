package org.ei.domain.bundles;

import java.util.Locale;
import java.util.ResourceBundle;

public class BundleBroker {

    public static String getMessage(String key) {
        // Get Japanese resource bundle
        ResourceBundle messages = ResourceBundle.getBundle("org.ei.domain.bundles.EvResources", Locale.JAPAN);
        // Get localized string
        String greeting = messages.getString("s1");
        // Output welcome message

        return greeting;

    }
}
