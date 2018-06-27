package org.ei.gui;

import java.util.Vector;

public class Table implements IComponent {

	protected String tableName;
	protected int cellPadding = 0;
	protected int cellSpacing = 0;
	protected int borderSize = 0;
	protected int columns = -1;
	protected int maxRows = 5;
	protected String width;
	protected String height;
	protected int startIndex = 0;
	protected String arrRowHeights[];
	protected String arrRowWidths[];
	protected int arrColumnsAtRow[];
	protected Vector vComponents = new Vector();
	String sBgImage;
	String halign;
	String valign;
	String sBgColor;
	String border;
	String colSpan;
	boolean isLastElement = false;
	boolean isFirstElement = false;

	public Table(int rows, String arrRowHeights[], String[] arrRowWidths, String sHeight, String sWidth) {
		maxRows = rows;
		this.arrRowHeights = arrRowHeights;
		this.arrRowWidths = arrRowWidths;
		width = sWidth;
		height = sHeight;
	}
	public Table(int rows, String height, String width) {

		this(rows, null, null, height, width);
	}
	public void setIsLastElement(boolean isLastElement) {
		this.isLastElement = isLastElement;
	}
	public boolean isLastElement() {
		return isLastElement;
	}
	public boolean isFirstElement() {
		return isFirstElement;
	}
	public void setMaxRows(int rows) {
		maxRows = rows;
	}
	public void setMaxCols(int cols) {
		columns = cols;
	}
	public void setTableName(String name) {
		tableName = name;
	}
	public void setCellPadding(int padding) {
		this.cellPadding = padding;
	}
	public void setCellSpacing(int spacing) {
		this.cellSpacing = spacing;
	}
	public void setRowHeights(String heights[]) {
		this.arrRowHeights = heights;
	}
	public void setRowWidths(String widths[]) {
		this.arrRowWidths = widths;
	}
	public void setBorderSize(int size) {
		borderSize = size;
	}
	public void setWidth(String width) {
		this.width = width;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getHeight() {
		return height;
	}
	public void setBgColor(String bgColor) {
		sBgColor = bgColor;
	}
	public String getBgColor() {
		return sBgColor;
	}
	public String getWidth() {
		return width;
	}

	public void setValign(String align) {
		valign = align;
	}

	public void setHalign(String align) {
		halign = align;
	}
	public String getValign() {
		return valign;
	}
	public String getHalign() {
		return halign;
	}
	public String getBorder() {
		return border;
	}
	public void setBorder(String border) {
		this.border = border;
	}
	public String getColSpan() {
		return colSpan;
	}
	public void setColSpan(String colSpan) {
		this.colSpan = colSpan;
	}
	public void add(IComponent c) {
		vComponents.addElement(c);
	}
	public void remove(IComponent c) {
		vComponents.removeElement(c);
	}
	public void removeAllElements() {
		vComponents.removeAllElements();
	}
	public Vector getComponents() {
		return vComponents;
	}
	public String render() {

		StringBuffer toHtmlBuffer = new StringBuffer();

		renderHead(toHtmlBuffer);
		renderBody(toHtmlBuffer);
		renderTail(toHtmlBuffer);

		return toHtmlBuffer.toString();
	}

	protected String renderHead(StringBuffer toHtmlBuffer) {

		toHtmlBuffer.append("<TABLE");

		if (borderSize != -1)
			toHtmlBuffer.append(" BORDER=\"").append(borderSize).append("\"");
		if (width != null)
			toHtmlBuffer.append(" WIDTH=\"").append(width).append("\"");
		if (height != null)
			toHtmlBuffer.append(" HEIGHT=\"").append(height).append("\"");
		if (cellSpacing != -1)
			toHtmlBuffer.append(" CELLSPACING=\"").append(cellSpacing).append("\"");
		if (cellPadding != -1)
			toHtmlBuffer.append(" CELLPADDING=\"").append(cellPadding).append("\"");
		if (tableName != null)
			toHtmlBuffer.append(" NAME=\"" + tableName + "\"");

		toHtmlBuffer.append(">");

		return toHtmlBuffer.toString();

	}

	protected String renderBody(StringBuffer toHtmlBuffer) {

		int vSize = vComponents.size();

		IComponent c = null;

		try {

			for (int i = 0; i < vSize; i++) {
				
				c = (IComponent) vComponents.elementAt(i);

				if (c != null && c.isFirstElement()) {
					toHtmlBuffer.append("<TR>");
				}

				toHtmlBuffer.append("<TD");

				if (c.getWidth() != null)
					toHtmlBuffer.append(" WIDTH=\"").append(c.getWidth()).append("\"");
				if (c.getHeight() != null)
					toHtmlBuffer.append(" HEIGHT=\"").append(c.getHeight()).append("\"");
				if (c.getHalign() != null)
					toHtmlBuffer.append(" ALIGN=\"").append(c.getHalign()).append("\"");
				if (c.getValign() != null)
					toHtmlBuffer.append(" VALIGN=\"").append(c.getValign()).append("\"");
				if (c.getBgColor() != null)
					toHtmlBuffer.append(" BGCOLOR=\"" + c.getBgColor()).append("\"");
				if (c.getColSpan() != null)
					toHtmlBuffer.append(" COLSPAN=\"" + c.getColSpan()).append("\"");

				toHtmlBuffer.append(" >");

				toHtmlBuffer.append(c.render());
				toHtmlBuffer.append("</TD>");
				
				if (c != null && c.isLastElement()) {
					toHtmlBuffer.append("</TR>");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return toHtmlBuffer.toString();
	}
	protected String renderTail(StringBuffer toHtmlBuffer) {
		toHtmlBuffer.append("</TABLE>");

		return toHtmlBuffer.toString();
	}
}
