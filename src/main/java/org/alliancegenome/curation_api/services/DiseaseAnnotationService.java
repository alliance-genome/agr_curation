package org.alliancegenome.curation_api.services;

import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.base.SearchResults;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.model.dto.Pagination;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@JBossLog
@RequestScoped
public class DiseaseAnnotationService extends BaseService<DiseaseAnnotation, DiseaseAnnotationDAO> {

	@Inject DiseaseAnnotationDAO geneDAO;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneDAO);
	}
	
	public SearchResults<DiseaseAnnotation> getAllDiseaseAnnotation(Pagination pagination) {
		return getAll(pagination);
	}

	@Transactional
	public DiseaseAnnotation createAnnotation(DiseaseAnnotation annotation) {
		return geneDAO.persist(annotation);
	}


}
