package org.ei.domain.personalization;

import java.io.Serializable;



public class ProductInfo implements Serializable{

   private static final long serialVersionUID = -5096541773445077427L;

   private String productId; 
   private String allListId;
   private String allListUpdateDate;
   private boolean ehrEnabled;

   
   public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getAllListId() {
		return allListId;
	}
	public void setAllListId(String allListId) {
		this.allListId = allListId;
	}
	public String getAllListUpdateDate() {
		return allListUpdateDate;
	}
	public void setAllListUpdateDate(String allListUpdateDate) {
		this.allListUpdateDate = allListUpdateDate;
	}
	public boolean isEhrEnabled() {
		return ehrEnabled;
	}
	public void setEhrEnabled(boolean ehrEnabled) {
		this.ehrEnabled = ehrEnabled;
	} 
   
	
}
