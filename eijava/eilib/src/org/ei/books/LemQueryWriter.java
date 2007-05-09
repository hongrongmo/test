package org.ei.books;

import java.util.Hashtable;
import org.ei.domain.LemClient;
import org.ei.domain.SearchException;
import org.ei.parser.base.BooleanQuery;

public class LemQueryWriter extends BookQueryWriter {

	public String getQuery(BooleanQuery bQuery) {
		BufferStream lemBuffer = new BufferStream();

		try {
			String bookQueryBuffer = super.getQuery(bQuery);
			String lemQueryBuffer = super.getLemBuffer(bQuery);
			LemClient client = new LemClient();
			client.setBaseURL("http://rei-testnode.bos3.fastsearch.net:29568");
			// System.out.println("BOOKQUERYBUFFER: " + bookQueryBuffer);\
			String[] inWords = bookQueryBuffer.split(";");
			String[] nonExactTerms = lemQueryBuffer.split(";");
			client.setInWords(nonExactTerms);
			client.search();

			Hashtable owh = client.getLems();

			for (int i = 0; i < inWords.length; i++) {
				// add unlemmitized words
				lemBuffer.write(inWords[i] + ";");

				if (nonExactTerms.length > i
						&& !(nonExactTerms[i].matches(".*?\\s+.*?"))) // skip
				// lemmitization
				// if
				// phrase
				{
					// add lemmitized words from LemClient
					String[] outWords = (String[]) owh.get(nonExactTerms[i]);

					for (int j = 0; outWords != null && j < outWords.length; j++) {
						lemBuffer.write(outWords[j] + ";");
					}
				}
			}
		} catch (SearchException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lemBuffer.toString();
	}
}
