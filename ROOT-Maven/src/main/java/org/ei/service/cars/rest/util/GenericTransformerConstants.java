package org.ei.service.cars.rest.util;

import org.apache.xalan.xsltc.trax.TransformerFactoryImpl;



/**
 * This class contains constants used by the XSLT transformation process.  These
 * constants are available to other classes that have need of the standard XSLT
 * transformer processing.
 */
public class GenericTransformerConstants {

    /** Transformer Factory system property key */
    public static final String TRANSFORMER_FACTORY =
        "javax.xml.transform.TransformerFactory";

    /** Transformer Factory system property value */
    public static final String XSLTC_COMPILED_TRANSFORMER_FACTORY_IMPL =
        "org.apache.xalan.xsltc.trax.TransformerFactoryImpl";

    /** Smart Transformer Factory system property value */
    public static final String XSLTC_SMART_TRANSFORMER_FACTORY_IMPL =
        "org.apache.xalan.xsltc.trax.SmartTransformerFactoryImpl";

    /** XSLTC TransformerFactory Attribute: package-name */
    public static final String FACTORY_ATTR_PACKAGE_NAME =
        TransformerFactoryImpl.PACKAGE_NAME;

    /** XSLTC TransformerFactory Attribute: translet-name */
    public static final String FACTORY_ATTR_TRANSLET_NAME =
        TransformerFactoryImpl.TRANSLET_NAME;

    /** XSLTC TransformerFactory Attribute: generate-translet */
    public static final String FACTORY_ATTR_GENERATE_TRANSLET =
        TransformerFactoryImpl.GENERATE_TRANSLET;

    /** XSLTC TransformerFactory Attribute: auto-translet */
    public static final String FACTORY_ATTR_AUTO_TRANSLET =
        TransformerFactoryImpl.AUTO_TRANSLET;

    /** XSLTC TransformerFactory Attribute: destination-directory */
    public static final String FACTORY_ATTR_DESTINATION_DIRECTORY =
        TransformerFactoryImpl.DESTINATION_DIRECTORY;

    /** XSLTC TransformerFactory Attribute: debug */
    public static final String FACTORY_ATTR_DEBUG =
        TransformerFactoryImpl.DEBUG;

    /** XSLTC TransformerFactory Attribute: use-classpath */
    public static final String FACTORY_ATTR_USE_CLASSPATH =
        TransformerFactoryImpl.USE_CLASSPATH;

    /** XSLTC TransformerFactory Attribute: enable-inlining */
    public static final String FACTORY_ATTR_ENABLE_INLINING =
        TransformerFactoryImpl.ENABLE_INLINING;

}

/*******************************************************************************


                                   ELSEVIER

                                 CONFIDENTIAL

    This document is the property of Elsevier, and its contents are proprietary
    to Elsevier.   Reproduction in any form by anyone of the materials
    contained  herein  without  the  permission  of Elsevier is prohibited.
    Finders are  asked  to  return  this  document  to the following Elsevier
    Science location.

        Elsevier
        360 Park Avenue South
        New York, NY 10010-1710

    Copyright (c) 2005 by Elsevier, A member of the Reed Elsevier plc
    group.

    All Rights Reserved.

 ******************************************************************************/