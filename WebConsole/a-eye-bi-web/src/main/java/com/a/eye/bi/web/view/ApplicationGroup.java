package com.a.eye.bi.web.view;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author emeroad
 */
@JsonSerialize(using = ApplicationGroupSerializer.class)
public class ApplicationGroup {

	private final List<Application> applicationList;

	public ApplicationGroup(List<Application> applicationList) {
		if (applicationList == null) {
			throw new NullPointerException("applicationList must not be null");
		}
		this.applicationList = applicationList;
	}

	public List<Application> getApplicationList() {
		return applicationList;
	}
}
