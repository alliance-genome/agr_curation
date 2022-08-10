package org.alliancegenome.curation_api.controllers;

import static org.reflections.scanners.Scanners.TypesAnnotated;

import java.util.*;

import javax.enterprise.context.RequestScoped;

import org.alliancegenome.curation_api.interfaces.APIVersionInterface;
import org.alliancegenome.curation_api.model.entities.AGRSchemaVersion;
import org.alliancegenome.curation_api.model.output.APIVersionInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.reflections.Reflections;

@RequestScoped
public class APIVersionInfoController implements APIVersionInterface {

	@ConfigProperty(name = "quarkus.application.version")
	String version;
	
	@ConfigProperty(name = "quarkus.application.name")
	String name;
	
	@ConfigProperty(name = "quarkus.hibernate-search-orm.elasticsearch.hosts")
	String es_host;
	
	@ConfigProperty(name = "NET")
	String env;
	

	@Override
	public APIVersionInfo get() {

		Reflections reflections = new Reflections("org.alliancegenome.curation_api");
		Set<Class<?>> annotatedClasses = reflections.get(TypesAnnotated.with(AGRSchemaVersion.class).asClass(reflections.getConfiguration().getClassLoaders()));
		HashMap<String, String> linkMLClassVersions = new HashMap<String, String>();
		for(Class<?> clazz: annotatedClasses) {
			AGRSchemaVersion version = clazz.getAnnotation(AGRSchemaVersion.class);
			linkMLClassVersions.put(clazz.getSimpleName(), version.value());
		}

		APIVersionInfo info = new APIVersionInfo();
		info.setVersion(version);
		info.setName(name);
		info.setLinkMLClassVersions(linkMLClassVersions);
		info.setEsHost(es_host);
		info.setEnv(env);
		return info;
	}

}
