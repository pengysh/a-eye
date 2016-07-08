package com.a.eye.bi.web.view;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author emeroad
 */
public class ServerTime {

	private final long currentServerTime;

	public ServerTime() {
		this.currentServerTime = System.currentTimeMillis();
	}

	@JsonProperty("currentServerTime")
	public long getCurrentServerTime() {
		return currentServerTime;
	}
}
