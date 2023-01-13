package org.alliancegenome.curation_api.services.helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.ResourceDescriptorDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.quarkus.logging.Log;

public class ResourceDescriptorLoadHelper {

	public List<ResourceDescriptorDTO> load(File rdFile) throws Exception {

		Log.info("Loading ResourceDescriptor File");
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, ResourceDescriptorDTO.class);
		List<ResourceDescriptorDTO> dtos = mapper.readValue(rdFile, listType);
		Log.info("Loading ResourceDescriptorFileFinished");

		return dtos;
	}
}
