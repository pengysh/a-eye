package com.a.eye.common.web.common.trace;


/**
 * @author emeroad
 */
public interface ServiceTypeInfo {

    ServiceType getServiceType();

    AnnotationKeyMatcher getPrimaryAnnotationKeyMatcher();
}
