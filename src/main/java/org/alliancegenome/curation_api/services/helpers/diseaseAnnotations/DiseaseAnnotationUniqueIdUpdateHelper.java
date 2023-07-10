package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;

@RequestScoped
public class DiseaseAnnotationUniqueIdUpdateHelper {

	@Inject 
	DiseaseAnnotationDAO diseaseAnnotationDAO;
	
	public void updateDiseaseAnnotationUniqueIds() {
		ProcessDisplayHelper pdh = new ProcessDisplayHelper();
		int page = 0;
		int limit = 500;
		Pagination pagination = new Pagination(page, limit);

		Boolean allSynced = false;
		while (!allSynced) {
			pagination.setPage(page);
			SearchResponse<String> response = diseaseAnnotationDAO.findAllIds(pagination);
			if (page == 0)
				pdh.startProcess("DiseaseAnnotation uniqueId update", response.getTotalResults());
			for (String daId : response.getResults()) {
				updateDiseaseAnnotationUniqueId(Long.parseLong(daId));
				pdh.progressProcess();
			}
			page = page + 1;
			int nrSynced = limit * page;
			if (nrSynced > response.getTotalResults().intValue()) {
				nrSynced = response.getTotalResults().intValue();
				allSynced = true;
			}
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
