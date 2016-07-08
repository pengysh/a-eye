package com.a.eye.common.web.common.service;

import com.a.eye.common.web.common.trace.AnnotationKey;

/**
 * @author emeroad
 */
public interface AnnotationKeyRegistryService {
    AnnotationKey findAnnotationKey(int annotationCode);

    AnnotationKey findAnnotationKeyByName(String keyName);

    AnnotationKey findApiErrorCode(int annotationCode);
}
