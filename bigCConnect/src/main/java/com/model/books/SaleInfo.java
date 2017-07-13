package com.model.books;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "country", "saleability", "isEbook", "listPrice",
		"retailPrice", "buyLink" })
public class SaleInfo {

	@JsonProperty("country")
	private String country;
	@JsonProperty("saleability")
	private String saleability;
	@JsonProperty("isEbook")
	private Boolean isEbook;
	@JsonProperty("listPrice")
	private ListPrice listPrice;
	@JsonProperty("retailPrice")
	private RetailPrice retailPrice;
	@JsonProperty("buyLink")
	private String buyLink;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * 
	 * @return The country
	 */
	@JsonProperty("country")
	public String getCountry() {
		return country;
	}

	/**
	 * 
	 * @param country
	 *            The country
	 */
	@JsonProperty("country")
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * 
	 * @return The saleability
	 */
	@JsonProperty("saleability")
	public String getSaleability() {
		return saleability == null ? "" : saleability;
	}

	/**
	 * 
	 * @param saleability
	 *            The saleability
	 */
	@JsonProperty("saleability")
	public void setSaleability(String saleability) {
		this.saleability = saleability;
	}

	/**
	 * 
	 * @return The isEbook
	 */
	@JsonProperty("isEbook")
	public Boolean getIsEbook() {
		return isEbook;
	}

	/**
	 * 
	 * @param isEbook
	 *            The isEbook
	 */
	@JsonProperty("isEbook")
	public void setIsEbook(Boolean isEbook) {
		this.isEbook = isEbook;
	}

	/**
	 * 
	 * @return The listPrice
	 */
	@JsonProperty("listPrice")
	public ListPrice getListPrice() {
		return listPrice;
	}

	/**
	 * 
	 * @param listPrice
	 *            The listPrice
	 */
	@JsonProperty("listPrice")
	public void setListPrice(ListPrice listPrice) {
		this.listPrice = listPrice;
	}

	/**
	 * 
	 * @return The retailPrice
	 */
	@JsonProperty("retailPrice")
	public RetailPrice getRetailPrice() {
		return retailPrice;
	}

	/**
	 * 
	 * @param retailPrice
	 *            The retailPrice
	 */
	@JsonProperty("retailPrice")
	public void setRetailPrice(RetailPrice retailPrice) {
		this.retailPrice = retailPrice;
	}

	/**
	 * 
	 * @return The buyLink
	 */
	@JsonProperty("buyLink")
	public String getBuyLink() {
		return buyLink;
	}

	/**
	 * 
	 * @param buyLink
	 *            The buyLink
	 */
	@JsonProperty("buyLink")
	public void setBuyLink(String buyLink) {
		this.buyLink = buyLink;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}
