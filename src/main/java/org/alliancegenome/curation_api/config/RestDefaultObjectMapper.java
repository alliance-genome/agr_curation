package org.alliancegenome.curation_api.config;

import javax.ws.rs.ext.*;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Provider
public class RestDefaultObjectMapper implements ContextResolver<ObjectMapper> {

	private final ObjectMapper mapper;

	public RestDefaultObjectMapper() {
		
		mapper = JsonMapper.builder() // or different mapper for other format
				   //.addModule(new ParameterNamesModule())
				   //.addModule(new Jdk8Module())
				   .addModule(new JavaTimeModule())
				   // and possibly other configuration, modules, then:
				   .build();

		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
		mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
		mapper.setSerializationInclusion(Include.NON_NULL);

		//if (!ConfigHelper.isProduction())
		//	mapper.enable(SerializationFeature.INDENT_OUTPUT);
		//mapper.setSerializerFactory(mapper.getSerializerFactory().withSerializerModifier(new APIBeanSerializerModifier()));
	}

	@Override
	public ObjectMapper getContext(Class<?> type) {
		return mapper;
	}

	public ObjectMapper getMapper() {
		return mapper;
	}
}