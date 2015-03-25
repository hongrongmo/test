package org.ei.tags;

import java.io.*;
import java.util.*;

public class Color
{

	public  final String COLOR_BLACK = "1";
	public  final String COLOR_BROWN = "2";
	public  final String COLOR_RED = "3";
	public  final String COLOR_PURPLE = "4";
	public  final String COLOR_ORANGE = "5";
	public  final String COLOR_LIME_GREEN = "6";
	public  final String COLOR_GREEN = "7";
	public  final String COLOR_VIOLET = "8";
	public  final String COLOR_BLUE = "9";
	public  final String COLOR_SKY_BLUE = "10";
	private  Map<String, IColor> nameMap = new HashMap<String, IColor>();
	private  IColor[] colors = new IColor[10];

	private static Color instance = null;

	public static Color getInstance()
	{
		if(instance == null)
		{
			instance = new Color();
		}

		return instance;
	}


	private Color()
	{
		colors[0] = new IColor(COLOR_BLACK, "Black", "#000000");
		colors[1] = new IColor(COLOR_BROWN, "Brown/Moca", "#6c5635");
		colors[2] = new IColor(COLOR_RED, "Ruby Red", "#bf272d");
		colors[3] = new IColor(COLOR_PURPLE, "Purple/Raspberry", "#bc1a8c");
		colors[4] = new IColor(COLOR_ORANGE, "Orange", "#f6921d");
		colors[5] = new IColor(COLOR_LIME_GREEN, "Lime Green", "#8bc53f");
		colors[6] = new IColor(COLOR_GREEN, "Green", "#268f3a");
		colors[7] = new IColor(COLOR_VIOLET, "Violet", "#EE82EE");
		colors[8] = new IColor(COLOR_BLUE, "Blue", "#0071bb");
		colors[9] = new IColor(COLOR_SKY_BLUE, "Sky Blue", "#00acee");

		for(int i=0; i<colors.length; i++)
		{
			nameMap.put(colors[i].getID(), colors[i]);
		}
	}

	public IColor[] getColors() {
		return colors;
	}
	
	public void toXML(Writer out)
		throws IOException
	{
		out.write("<COLORS>");
		for(int i=0; i<colors.length; i++)
		{
			out.write("<COLOR>");
			out.write("<ID>");
			out.write(colors[i].getID());
			out.write("</ID>");
			out.write("<NAME><![CDATA[");
			out.write(colors[i].getName());
			out.write("]]></NAME>");
			out.write("<CODE><![CDATA[");
			out.write(colors[i].getCode());
			out.write("]]></CODE>");
			out.write("</COLOR>");
		}
		out.write("</COLORS>");
	}

	public IColor getColorByID(String colorID) {
		IColor ic = (IColor)nameMap.get(colorID);
		if(ic == null)
		{
			ic = (IColor)nameMap.get(COLOR_SKY_BLUE);
		} 
		return ic;
	}
	
	public void toXML(String colorID, Writer out)
		throws IOException
	{
		IColor ic = (IColor)nameMap.get(colorID);

		if(ic == null)
		{
			ic = (IColor)nameMap.get(COLOR_SKY_BLUE);
		}
		out.write("<COLOR>");
		out.write("<ID>");
		out.write(ic.getID());
		out.write("</ID>");
		out.write("<NAME><![CDATA[");
		out.write(ic.getName());
		out.write("]]></NAME>");
		out.write("<CODE><![CDATA[");
		out.write(ic.getCode());
		out.write("]]></CODE>");
		out.write("</COLOR>");
	}


	public class IColor
	{
		private String id;
		private String name;
		private String code;

		public IColor(String id, String name, String code)
		{
			this.id = id;
			this.name = name;
			this.code = code;
		}

		public String getID()
		{
			return this.id;
		}

		public String getName()
		{
			return this.name;
		}

		public String getCode()
		{
			return this.code;
		}
	}

}


