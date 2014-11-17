package org.ei.gui;

public abstract class Component implements IComponent {

	String width;
	String height;
	String sBgImage;
	String halign;
	String valign;
	String sBgColor;
	String border;
	String colSpan;
	protected String name;
	protected String value;
	boolean isLastElement;
	boolean isFirstElement;
	
	public void setIsFirstElement(boolean isFirstElement){
			this.isFirstElement = isFirstElement;
		}
	public void setIsLastElement(boolean isLastElement){
		this.isLastElement = isLastElement;
	}
	public boolean isLastElement(){
		return isLastElement;
	}
	public boolean isFirstElement(){
			return isFirstElement;
		}
    public String getLabel() {
        return name;
    }
    public String getName() {
        return name;
    }
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void setName(String name) {
		this.name = name;
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
		this.halign = align;
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
}
