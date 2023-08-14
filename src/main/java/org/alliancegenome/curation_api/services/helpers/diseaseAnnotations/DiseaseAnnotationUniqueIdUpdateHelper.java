package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

@RequestScoped
public class DiseaseAnnotationUniqueIdUpdateHelper {

	@Inject 
	DiseaseAnnotationDAO diseaseAnnotationDAO;
	
	public void updateDiseaseAnnotationUniqueIds() {
		ProcessDisplayHelper pdh = new ProcessDisplayHelper();
		
		SearchResponse<String> response = diseaseAnnotationDAO.findAllIds();
		pdh.startProcess("DiseaseAnnotation uniqueId update", response.getTotalResults());
		for (String daId : response.getResults()) {
			updateDiseaseAnnotationUniqueId(Long.parseLong(daId));
			pdh.progressProcess();
		}
		pdh.finishProcess();
	}

	@Transactional
	public void updateDiseaseAnnotationUniqueId(Long id) {
		DiseaseAnnotation annotation = diseaseAnnotationDAO.find(id);
		if (annotation == null)
			return;
		
		annotation.setUniqueId(DiseaseAnnotationUniqueIdHelper.getDiseaseAnnotationUniqueId(annotation));
		diseaseAnnotationDAO.merge(annotation);
	}
	
}
