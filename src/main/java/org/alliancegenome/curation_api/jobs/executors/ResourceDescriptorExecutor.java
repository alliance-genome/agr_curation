package org.alliancegenome.curation_api.jobs.executors;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.ResourceDescriptorDTO;
import org.alliancegenome.curation_api.services.ResourceDescriptorService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class ResourceDescriptorExecutor extends LoadFileExecutor {
	
	@Inject
	ResourceDescriptorService resourceDescriptorService;

	public void runLoad(BulkLoadFile bulkLoadFile) throws Exception {

		log.info("Loading ResourceDescriptor File");
		
		File rdFile = new File(bulkLoadFile.getLocalFilePath());
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, ResourceDescriptorDTO.class);
		List<ResourceDescriptorDTO> dtos = mapper.readValue(new GZIPInputStream(new FileInputStream(rdFile)), listType);
		
		List<String> rdNamesBefore = resourceDescriptorService.getAllNames();
		List<String> rdNamesAfter = new ArrayList<>();
		BulkLoadFileHistory history = new BulkLoadFileHistory(dtos.size());
		
		dtos.forEach(dto -> {
			try {
				ResourceDescriptor rd = resourceDescriptorService.upsert(dto);
				history.incrementCompleted();
				rdNamesAfter.add(rd.getName());
			} catch (ObjectUpdateException e) {
				addException(history, e.getData());
			} catch (Exception e) {
				addException(history, new ObjectUpdateExceptionData(dto, e.getMessage(), e.getStackTrace()));
			}
		});
		
		resourceDescriptorService.removeNonUpdatedResourceDescriptors(rdNamesBefore, rdNamesAfter);
		
		log.info("Loading ResourceDescriptorFileFinished");
	}
}
