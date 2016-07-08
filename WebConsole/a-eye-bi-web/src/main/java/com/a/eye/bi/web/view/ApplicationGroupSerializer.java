package com.a.eye.bi.web.view;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author emeroad
 */
public class ApplicationGroupSerializer extends JsonSerializer<ApplicationGroup> {

	@Override
	public void serialize(ApplicationGroup applicationGroup, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		jgen.writeStartArray();

		List<Application> applicationList = applicationGroup.getApplicationList();
		for (Application application : applicationList) {
			jgen.writeObject(application);
		}
		jgen.writeEndArray();
	}
}
