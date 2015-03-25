package org.ei.gui;

public interface IComponent {

	public void setWidth(String width);
	public void setHeight(String height);
	public String getHeight();
	public void setBgColor(String bgColor);
	public String getBgColor();
	public String getWidth();
	public void setValign(String align);
	public void setHalign(String align);
	public String getValign();
	public String getHalign();
	public String getBorder();
	public void setBorder(String border) ;
	public String getColSpan();
	public void setColSpan(String colSpan);
	public boolean isLastElement();
	public boolean isFirstElement();
	public abstract String render();

}
