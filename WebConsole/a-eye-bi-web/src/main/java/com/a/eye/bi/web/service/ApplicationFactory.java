package com.a.eye.bi.web.service;

import com.a.eye.bi.web.view.Application;
import com.a.eye.common.web.common.trace.ServiceType;

/**
 * @author emeroad
 */
public interface ApplicationFactory {

	Application createApplication(String applicationName, short serviceTypeCode);

	Application createApplication(String applicationName, ServiceType serviceType);

	Application createApplicationByTypeName(String applicationName, String serviceTypeName);
}
