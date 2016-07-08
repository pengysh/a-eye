package com.a.eye.common.web.common.trace;


/**
 * @author Jongho Moon <jongho.moon@navercorp.com>
 *
 */
public interface TraceMetadataProvider {
    void setup(TraceMetadataSetupContext context);
}
