package com.bigc.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "feeds" })
public class Feeds implements Serializable {

	private static final long serialVersionUID = 123451L;

	@JsonProperty("feeds")
	private List<Feed> feeds = new ArrayList<Feed>();

	@JsonProperty("feeds")
	public List<Feed> getFeeds() {
		return feeds;
	}

	@JsonProperty("feeds")
	public void setFeeds(List<Feed> items) {
		feeds.clear();
		if (items == null)
			return;
		this.feeds.addAll(items);
	}
}
