package com.a.eye.common.web.common.trace;


/**
 * @author emeroad
 */
public class DefaultAnnotationKeyFactory extends AnnotationKeyFactory {

    public AnnotationKey createAnnotationKey(int code, String name, AnnotationKeyProperty... properties) {
        return new DefaultAnnotationKey(code, name, properties);
    }

}