package com.a.eye.common.web.common.service;

import java.util.List;

import com.a.eye.common.web.common.trace.AnnotationKey;
import com.a.eye.common.web.common.trace.ServiceTypeInfo;

/**
 * @author emeroad
 */
public interface TraceMetadataLoaderService {
    List<ServiceTypeInfo> getServiceTypeInfos();

    List<AnnotationKey> getAnnotationKeys();
}
