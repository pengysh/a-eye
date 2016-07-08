package com.a.eye.bi.web.view;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author HyunGil Jeong
 */
public class ApplicationSerializer extends JsonSerializer<Application> {

    @Override
    public void serialize(Application application, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeStringField("applicationName", application.getName());
        jgen.writeStringField("serviceType", application.getServiceType().getDesc());
        jgen.writeNumberField("code", application.getServiceTypeCode());
        jgen.writeEndObject();
    }
}
