package org.alliancegenome.curation_api.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RestDefaultObjectMapper implements ContextResolver<ObjectMapper> {

	private final ObjectMapper mapper;

	public RestDefaultObjectMapper() {
		// log.info("Setting up Default Object Mapper");
		mapper = new ObjectMapper();

		mapper.registerModule(new JavaTimeModule());

		// Hibernate5Module hm = new Hibernate5Module();
		// hm.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, false);
		// mapper.registerModule(new Hibernate5Module());

		// .addModule(new ParameterNamesModule())
		// .addModule(new Jdk8Module())
		// and possibly other configuration, modules, then:
		// .build();

		// mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setSerializationInclusion(Include.NON_EMPTY);

		// if (!ConfigHelper.isProduction())
		// mapper.enable(SerializationFeature.INDENT_OUTPUT);
		// mapper.setSerializerFactory(mapper.getSerializerFactory().withSerializerModifier(new
		// APIBeanSerializerModifier()));
	}

	@Override
	public ObjectMapper getContext(Class<?> type) {
		return mapper;
	}

	public ObjectMapper getMapper() {
		return mapper;
	}
}
