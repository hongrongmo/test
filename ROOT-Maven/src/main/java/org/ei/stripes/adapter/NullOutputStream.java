package org.ei.stripes.adapter;

import java.io.IOException;
import java.io.OutputStream;

public class NullOutputStream extends OutputStream {

	@Override
	public void write(int b) throws IOException {
		// Do nothing!!  This is meant to produce no output...
	}

}
