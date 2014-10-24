package org.ei.service.cars.rest.util;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;

/**
 * This is the ITransformer Interface.
 */
public interface ITransformer {

    /**
     * This method performs an XSLT transformation and streams the output directly via the writer.
     * 
     * @param xmlReader
     *            The reader for the XML to be transformed.
     * @param uri
     *            The path to the stylesheet source file.
     * @param writer
     *            Destination of the transformation output.
     * @param params
     *            Properties map of parameters passed to the transformer.
     * 
     * @throws XSLTTransformationException
     *             is generated if the transformation fails for a reason contained in a published exception.
     * @throws TransformerConfigurationException
     */
    public void transform(Reader xmlReader, String uri, Writer writer, Map<String, Object> params) throws XSLTTransformationException;

    /**
     * This method performs an XSLT transformation and streams the output directly via the writer.
     * 
     * @param xmlReader
     *            The reader for the XML to be transformed.
     * @param templates
     *            Translet object that contains the transformation instructions.
     * @param writer
     *            Destination of the transformation output.
     * 
     * @throws XSLTTransformationException
     *             is generated if the transformation fails for a reason contained in a published exception.
     */
    public void transform(Reader xmlReader, Templates templates, Writer writer) throws XSLTTransformationException;

    /**
     * This method performs an XSLT transformation and streams the output directly via the writer.
     * 
     * @param xmlReader
     *            The reader for the XML to be transformed.
     * @param templates
     *            Translet object that contains the transformation instructions.
     * @param writer
     *            Destination of the transformation output.
     * @param map
     *            Properties map of parameters passed to the transformer.
     * 
     * @throws XSLTTransformationException
     *             is generated if the transformation fails for a reason contained in a published exception.
     */
    public void transform(Reader xmlReader, Templates templates, Writer writer, Map<String, Object> map) throws XSLTTransformationException;

}

/*******************************************************************************
 * ELSEVIER
 * 
 * CONFIDENTIAL
 * 
 * This document is the property of Elsevier, and its contents are proprietary to Elsevier. Reproduction in any form by anyone of the materials contained herein
 * without the permission of Elsevier is prohibited. Finders are asked to return this document to the following Elsevier Science location.
 * 
 * Elsevier 360 Park Avenue South New York, NY 10010-1710
 * 
 * Copyright (c) 2005 by Elsevier, A member of the Reed Elsevier plc group.
 * 
 * All Rights Reserved.
 ******************************************************************************/
