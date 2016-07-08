package com.a.eye.common.web.common.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.a.eye.common.web.common.trace.AnnotationKey;
import com.a.eye.common.web.common.trace.AnnotationKeyRegistry;
import com.a.eye.common.web.common.util.StaticFieldLookUp;

/**
 * @author emeroad
 */
public class DefaultAnnotationKeyRegistryService implements AnnotationKeyRegistryService {
	private final Logger logger = Logger.getLogger(DefaultServiceTypeRegistryService.class.getName());

	private final TraceMetadataLoaderService typeLoaderService;
	private final AnnotationKeyRegistry registry;

	public DefaultAnnotationKeyRegistryService() {
		this(new DefaultTraceMetadataLoaderService());
	}

	public DefaultAnnotationKeyRegistryService(TraceMetadataLoaderService typeLoaderService) {
		if (typeLoaderService == null) {
			throw new NullPointerException("typeLoaderService must not be null");
		}
		this.typeLoaderService = typeLoaderService;
		this.registry = buildAnnotationKeyRegistry();
	}

	private AnnotationKeyRegistry buildAnnotationKeyRegistry() {
		AnnotationKeyRegistry.Builder builder = new AnnotationKeyRegistry.Builder();

		StaticFieldLookUp<AnnotationKey> staticFieldLookUp = new StaticFieldLookUp<AnnotationKey>(AnnotationKey.class, AnnotationKey.class);
		List<AnnotationKey> lookup = staticFieldLookUp.lookup();
		for (AnnotationKey serviceType : lookup) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("add Default AnnotationKey:" + serviceType);
			}
			builder.addAnnotationKey(serviceType);
		}

		final List<AnnotationKey> types = typeLoaderService.getAnnotationKeys();
		for (AnnotationKey type : types) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("add Plugin AnnotationKey:" + type);
			}
			builder.addAnnotationKey(type);
		}

		return builder.build();
	}

	@Override
	public AnnotationKey findAnnotationKey(int annotationCode) {
		return this.registry.findAnnotationKey(annotationCode);
	}

	@Override
	public AnnotationKey findAnnotationKeyByName(String keyName) {
		return this.registry.findAnnotationKeyByName(keyName);

	}

	@Override
	public AnnotationKey findApiErrorCode(int annotationCode) {
		return this.registry.findApiErrorCode(annotationCode);
	}
}
