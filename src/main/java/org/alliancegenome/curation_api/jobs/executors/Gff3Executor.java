package org.alliancegenome.curation_api.jobs.executors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.services.Gff3Service;
import org.alliancegenome.curation_api.services.helpers.gff3.Gff3AttributesHelper;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Gff3Executor extends LoadFileExecutor {

	@Inject Gff3Service gff3Service;

	protected String loadGenomeAssembly(String assemblyName, BulkLoadFileHistory history, List<String> gffHeaderData, BackendBulkDataProvider dataProvider, ProcessDisplayHelper ph) {
		try {
			assemblyName = gff3Service.loadGenomeAssembly(assemblyName, gffHeaderData, dataProvider);
			history.incrementCompleted();
		} catch (ObjectUpdateException e) {
			//e.printStackTrace();
			history.incrementFailed();
			addException(history, e.getData());
		} catch (Exception e) {
			e.printStackTrace();
			history.incrementFailed();
			addException(history, new ObjectUpdateExceptionData(gffHeaderData, e.getMessage(), e.getStackTrace()));
		}
		updateHistory(history);
		ph.progressProcess();
		
		return assemblyName;
	}
	
	protected List<ImmutablePair<Gff3DTO, Map<String, String>>> preProcessGffData(List<Gff3DTO> gffData, BackendBulkDataProvider dataProvider) {
		List<ImmutablePair<Gff3DTO, Map<String, String>>> processedGffData = new ArrayList<>();
		
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("GFF pre-processing for " + dataProvider.name(), gffData.size());
		
		for (Gff3DTO originalGffEntry : gffData) {
			Map<String, String> attributes = Gff3AttributesHelper.getAttributes(originalGffEntry, dataProvider);
			if (attributes.containsKey("Parent") && attributes.get("Parent").indexOf(",") > -1) {
				for (String parent : attributes.get("Parent").split(",")) {
					HashMap<String, String> attributesCopy = new HashMap<>();
					attributesCopy.putAll(attributes);
					String[] parentIdParts = parent.split(":");
					if (parentIdParts.length == 1) {
						parent = dataProvider.name() + ':' + parentIdParts[0];
					}
					attributesCopy.put("Parent", parent);
					processedGffData.add(new ImmutablePair<>(originalGffEntry, attributesCopy));
				}
			} else {
				processedGffData.add(new ImmutablePair<>(originalGffEntry, attributes));
			}
			ph.progressProcess();
		}
		ph.finishProcess();
		
		return processedGffData;
	}
}
