package org.ei.stripes.view;

public class LocalHoldingLink {

	private String url;
	private String position;
	private String imageUrl;
	private String label;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public LocalHoldingLink(String url,String position, String imageUrl, String label) {
		super();
		this.url = url;
		this.position = position;
		this.imageUrl = imageUrl;
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
}
