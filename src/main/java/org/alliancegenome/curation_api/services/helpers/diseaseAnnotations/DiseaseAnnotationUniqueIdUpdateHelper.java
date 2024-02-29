package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class DiseaseAnnotationUniqueIdUpdateHelper {

	@Inject 
	DiseaseAnnotationDAO diseaseAnnotationDAO;
	
	public void updateDiseaseAnnotationUniqueIds() {
		ProcessDisplayHelper pdh = new ProcessDisplayHelper();
		
		SearchResponse<Long> response = diseaseAnnotationDAO.findAllIds();
		pdh.startProcess("DiseaseAnnotation uniqueId update", response.getTotalResults());
		for (Long daId : response.getResults()) {
			updateDiseaseAnnotationUniqueId(daId);
			pdh.progressProcess();
		}
		pdh.finishProcess();
	}

	@Transactional
	public void updateDiseaseAnnotationUniqueId(Long id) {
		DiseaseAnnotation annotation = diseaseAnnotationDAO.find(id);
		if (annotation == null)
			return;
		
		annotation.setUniqueId(AnnotationUniqueIdHelper.getDiseaseAnnotationUniqueId(annotation));
		diseaseAnnotationDAO.merge(annotation);
	}
	
}
