package test.ei.org.common.transformation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamSource;

import org.ei.service.cars.rest.util.XMLUtil;
import org.ei.service.cars.rest.util.XSLTTransformationException;


public class XslTranformationTest {

	/**
	 * @param args
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerConfigurationException
	 * @throws XSLTTransformationException
	 * @throws IOException
	 */
	public static void main(String[] args) throws TransformerConfigurationException, TransformerFactoryConfigurationError, XSLTTransformationException, IOException {

		InputStream stream = XslTranformationTest.class.getResourceAsStream("template.xsl");
		Templates templates = TransformerFactory.newInstance().newTemplates(new StreamSource(stream));
		InputStream data = XslTranformationTest.class.getResourceAsStream("input.xml");

		Writer writer = new StringWriter();

        char[] buffer = new char[1024];
        try
        {
            Reader reader = new BufferedReader(
                    new InputStreamReader(data, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1)
            {
                writer.write(buffer, 0, n);
            }
        }
        finally
        {
            data.close();
        }

        ;
		String output = transform(writer.toString(), templates);

       System.out.println(output);

	}


	 private static String transform(String xmlInputToTransform, Templates latestTemplate) throws XSLTTransformationException {
	        String transformedHtml = null;

	        try {
	            if (null != xmlInputToTransform && null != latestTemplate) {
	                transformedHtml = XMLUtil.transformXML(xmlInputToTransform, latestTemplate);
	            }
	        } catch (TransformerConfigurationException exp) {
	            throw new XSLTTransformationException(exp.getMessage(), exp);
	        } catch (TransformerException exp) {
	            throw new XSLTTransformationException(exp.getMessage(), exp);
	        }
	        return transformedHtml;
	    }

}
