package com.a.eye.common.web.common.service;

import java.util.List;

import com.a.eye.common.web.common.trace.ServiceType;

/**
 * @author emeroad
 */
public interface ServiceTypeRegistryService {
	ServiceType findServiceType(short serviceType);

	ServiceType findServiceTypeByName(String typeName);

	@Deprecated
	List<ServiceType> findDesc(String desc);
}
