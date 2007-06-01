package org.ei.data.encompasslit.loadtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Hashtable;
import org.apache.oro.text.perl.Perl5Util;

public class ExtractCTEFltThes {
	private Perl5Util perl = new Perl5Util();

	public void extract(String thesfile, String outfile) throws Exception {
		PrintWriter writerCT = null;
		Hashtable ctHash = new Hashtable();
		String line = null;
		BufferedReader buf = null;
		FileReader in = null;
		File file = null;
		String display_name = null;
		int cvorder = 0;

		try {
			file = new File(thesfile);
			writerCT = new PrintWriter(new FileWriter(outfile));
			in = new FileReader(file);
			buf = new BufferedReader(in);
			while ((line = buf.readLine()) != null) {
				cvorder++;
				if (perl.match("/^trm:/i", line) && !perl.match("/^\\-/i", line)) {
					int endoffset = perl.endOffset(0);
					display_name = line.substring(endoffset);
					writerCT.println(display_name + "\t" + cvorder + "t" + "elt");
				}
			}
		} finally {
			if (writerCT != null) {
				try {
					writerCT.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (in != null)
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}

	}
	public static void main(String[] args) {
		String thesfile = "C:/cvPat/2004_EnCompass_Thesaurus.txt";
		String outfile = "elt_ct.lkp";
		ExtractCTEFltThes test = new ExtractCTEFltThes();
		try {
			test.extract(thesfile, outfile);
	//		System.out.println("ELT CV extract completed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
