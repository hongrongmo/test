package org.ei.query.base;

import java.util.LinkedList;
import java.util.Stack;

public class HitHighlightFinisher {

	// private static String eTag ="</b></font>";
	// private static String bTag = "<font color=\"red\"><b>";
	private static String eTag = "</span>";
	private static String bTag = "<span class='hit'>";
	private static String eTagOff = "</span>";
	private static String bTagOff = "<span class='nohit'>";

	public HitHighlightFinisher() {

	}

	public static String addMarkup(String data) {
		return finishHighlightedString(bTag, eTag, data, false);
	}

	public static String addMarkupCheckTagging(String data) {
		return finishHighlightedString(bTag, eTag, data, true);
	}

	public static String removeMarkup(String data) {
		return finishHighlightedString("", "", data, false);
	}

	public static String removeMarkupWithTag(String data) {
		return finishHighlightedString(bTagOff, eTagOff, data, false);
	}

	public static String removeMarkupWithTagCheckTagging(String data) {
		return finishHighlightedString(bTagOff, eTagOff, data, true);
	}

	public static String finishHighlightedString(String beginTag,
			String endTag, String data, boolean checkTagging)

	{

		boolean inTag = false;
		boolean inEntity = false;

		if (data == null) {
			return null;
		}

		Stack charPool = new Stack();
		for (int i = 0; i < 4; ++i) {
			charPool.push(new CharWrapper());
		}

		int numOpen = 0;
		StringBuffer buf = new StringBuffer();
		LinkedList charList = new LinkedList();
		char[] databuf = new char[data.length()];
		data.getChars(0, data.length(), databuf, 0);

		for (int i = 0; i < databuf.length; ++i) {
			// System.out.println(charPool.size());
			CharWrapper cw = (CharWrapper) charPool.pop();
			cw.c = databuf[i];
			if (databuf[i] == '<') {
				inTag = true;
			} else if (databuf[i] == '>') {
				inTag = false;
			} else if (databuf[i] == '&') {
				inEntity = true;
			} else if (databuf[i] == ';') {
				inEntity = false;
			}

			// System.out.println("Char:"+ databuf[i]);
			charList.addLast(cw);

			if (charList.size() == 4) {
				// System.out.println("Char lis is 4");

				if (isOpenHit(charList)) {
					++numOpen;
					if (numOpen == 1) {
						if (!checkTagging
								|| (checkTagging && !inTag && !inEntity)) {
							buf.append(beginTag);
						}

					}

					while (charList.size() > 0) {
						charPool.push(charList.removeFirst());
					}
				} else if (isCloseHit(charList)) {
					if (numOpen == 1) {
						if (!checkTagging
								|| (checkTagging && !inTag && !inEntity)) {
							buf.append(endTag);
						}
					}

					--numOpen;

					while (charList.size() > 0) {
						charPool.push(charList.removeFirst());
					}
				} else {
					CharWrapper w = (CharWrapper) charList.removeFirst();
					buf.append(w.c);
					// System.out.println("Size:"+charPool.size()+":"+buf.toString());
					charPool.push(w);
				}
			}
		}

		while (charList.size() > 0) {
			CharWrapper cwr = (CharWrapper) charList.removeFirst();
			buf.append(cwr.c);
		}

		return buf.toString();
	}

	private static boolean isCloseHit(LinkedList l) {
		CharWrapper a = (CharWrapper) l.get(0);

		if (a.c != ':') {
			return false;
		}

		a = (CharWrapper) l.get(1);

		if (a.c != 'H') {
			return false;
		}

		a = (CharWrapper) l.get(2);

		if (a.c != ':') {
			return false;
		}

		a = (CharWrapper) l.get(3);

		if (a.c != ':') {
			return false;
		}

		return true;
	}

	private static boolean isOpenHit(LinkedList l) {
		CharWrapper a = (CharWrapper) l.get(0);
		if (a.c != ':') {
			return false;
		}

		a = (CharWrapper) l.get(1);

		if (a.c != ':') {
			return false;
		}

		a = (CharWrapper) l.get(2);

		if (a.c != 'H') {
			return false;
		}

		a = (CharWrapper) l.get(3);

		if (a.c != ':') {
			return false;
		}

		return true;

	}

}