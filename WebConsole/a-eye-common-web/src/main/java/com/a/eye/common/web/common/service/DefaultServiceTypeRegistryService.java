package com.a.eye.common.web.common.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.a.eye.common.web.common.trace.ServiceType;
import com.a.eye.common.web.common.trace.ServiceTypeInfo;
import com.a.eye.common.web.common.trace.ServiceTypeRegistry;
import com.a.eye.common.web.common.util.StaticFieldLookUp;

/**
 * @author emeroad
 */
public class DefaultServiceTypeRegistryService implements ServiceTypeRegistryService {
	private final Logger logger = Logger.getLogger(DefaultServiceTypeRegistryService.class.getName());

	private final TraceMetadataLoaderService typeLoaderService;
	private final ServiceTypeRegistry registry;

	public DefaultServiceTypeRegistryService() {
		this(new DefaultTraceMetadataLoaderService());
	}

	public DefaultServiceTypeRegistryService(TraceMetadataLoaderService typeLoaderService) {
		if (typeLoaderService == null) {
			throw new NullPointerException("typeLoaderService must not be null");
		}
		this.typeLoaderService = typeLoaderService;
		this.registry = buildServiceTypeRegistry();
	}

	private ServiceTypeRegistry buildServiceTypeRegistry() {
		ServiceTypeRegistry.Builder builder = new ServiceTypeRegistry.Builder();

		StaticFieldLookUp<ServiceType> staticFieldLookUp = new StaticFieldLookUp<ServiceType>(ServiceType.class, ServiceType.class);
		List<ServiceType> lookup = staticFieldLookUp.lookup();
		for (ServiceType serviceType : lookup) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("add Default ServiceType:" + serviceType);
			}
			builder.addServiceType(serviceType);
		}

		final List<ServiceTypeInfo> types = loadType();
		for (ServiceTypeInfo type : types) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("add Plugin ServiceType:" + type.getServiceType());
			}
			builder.addServiceType(type.getServiceType());
		}

		return builder.build();
	}

	private List<ServiceTypeInfo> loadType() {
		return typeLoaderService.getServiceTypeInfos();
	}

	@Override
	public ServiceType findServiceType(short serviceType) {
		return registry.findServiceType(serviceType);
	}

	public ServiceType findServiceTypeByName(String typeName) {
		return registry.findServiceTypeByName(typeName);
	}

	@Override
	public List<ServiceType> findDesc(String desc) {
		return registry.findDesc(desc);
	}

}
