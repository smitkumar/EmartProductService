package com.emart.service.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ProductInfo {
	
	
	/*
	 * 
	 * "id": "SP-101",
  "name": "Mi4i Mobile",
  "price": "10000",
  "model": "mi4i model",
  "stock": "inStock",
  "category": "electronics",
  "website": "No link",
  "type": "brewery",
  "updated": "2010-10-24 13:54:07",
  "description": "This is Mi4i Mobile !!!"
	 * 
	 * */

	@XmlElement private String id;
	@XmlElement private String name;
	@XmlElement private String price;
	@XmlElement private String model;
	@XmlElement private String stock;
	@XmlElement private String category;
	@XmlElement private String website;
	@XmlElement private String type;	
	@XmlElement private String updated;	
	@XmlElement private String description;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getStock() {
		return stock;
	}
	public void setStock(String stock) {
		this.stock = stock;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUpdated() {
		return updated;
	}
	public void setUpdated(String updated) {
		this.updated = updated;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
	
	
}
