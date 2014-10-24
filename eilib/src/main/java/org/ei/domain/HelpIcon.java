package org.ei.domain;

public class HelpIcon {

	private static String helpCID = "pophelp";

	private static String blue_help = "<img src=\"/static/images/blue_help.gif\" width=\"12\" height=\"12\" border=\"0\"/>";
	private static String blue_help1 = "<img src=\"/static/images/blue_help1.gif\" border=\"0\"/>";

	private static String window_attribs = "width=700,height=500,toolbar=no,location=no,scrollbars,resizable";
	public static String getHelpLink()
	{

		String helplink = "";
		helplink = "<a href=\"javascript:makeUrl('Content_Resources_Introduction.htm')\"/>";
		helplink = helplink.concat(HelpIcon.blue_help1);
		helplink = helplink.concat("</a>");

		return helplink;
	}
}
