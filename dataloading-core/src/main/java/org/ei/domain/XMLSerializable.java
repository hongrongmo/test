package org.ei.domain;

import java.io.IOException;
import java.io.Writer;

public interface XMLSerializable
{
	public void toXML(Writer out) throws IOException;

}
