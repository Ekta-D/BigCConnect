package com.bigc.models;

import java.io.Serializable;

import com.bigc.adapters.NewsAdapter.SOURCE;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "source", "title", "description", "link", "image" })
public class Feed implements Serializable {

	private static final long serialVersionUID = 123452L;

	@JsonProperty("source")
	public SOURCE source;

	@JsonProperty("title")
	public String title;

	@JsonProperty("description")
	public String description;

	@JsonProperty("link")
	public String link;

	@JsonProperty("image")
	public String image;

	public Feed() {

	}

	@JsonProperty("source")
	public Integer getSource() {
		return source.ordinal();
	}

	@JsonProperty("source")
	public void setSource(Integer source) {
		this.source = SOURCE.values()[source];
	}

	@JsonProperty("title")
	public String getTitle() {
		return title;
	}

	@JsonProperty("title")
	public void setTitle(String title) {
		this.title = title;
	}

	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	@JsonProperty("description")
	public void setDescription(String description) {
		this.description = description;
	}

	@JsonProperty("link")
	public String getLink() {
		return link;
	}

	@JsonProperty("link")
	public void setLink(String link) {
		this.link = link;
	}

	@JsonProperty("image")
	public String getImage() {
		return image;
	}

	@JsonProperty("image")
	public void setImage(String image) {
		this.image = image;
	}

	public Feed(SOURCE source) {
		this.source = source;
	}

	@Override
	public boolean equals(Object another) {
		try {
			return title.equals(((Feed) another).title);
		} catch (Exception e) {
			return false;
		}
	}
}
