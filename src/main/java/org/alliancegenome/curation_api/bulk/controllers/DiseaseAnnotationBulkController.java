package org.alliancegenome.curation_api.bulk.controllers;

import javax.inject.Inject;

import org.alliancegenome.curation_api.interfaces.bulk.DiseaseAnnotationBulkInterface;
import org.alliancegenome.curation_api.model.dto.json.DiseaseMetaDataDefinitionDTO;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class DiseaseAnnotationBulkController implements DiseaseAnnotationBulkInterface {

	@Inject DiseaseAnnotationService diseaseAnnotationService;
	
	@Override
	public String updateDAF(DiseaseMetaDataDefinitionDTO diseaseMetaDataDefinitionData) {
		
		log.info("Annotations Recived: " + diseaseMetaDataDefinitionData.getData().size());
		
		diseaseAnnotationService.bulkUpdate(diseaseMetaDataDefinitionData);

		return "OK";
	}

}
