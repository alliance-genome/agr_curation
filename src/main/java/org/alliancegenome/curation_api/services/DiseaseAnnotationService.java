package org.alliancegenome.curation_api.services;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.model.dto.json.*;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotationCurated;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class DiseaseAnnotationService extends BaseService<DiseaseAnnotationCurated, DiseaseAnnotationDAO> {

	@Inject
	private DiseaseAnnotationDAO diseaseAnnotationDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(diseaseAnnotationDAO);
	}

	public void bulkUpdate(DiseaseMetaDataDefinitionDTO diseaseMetaDataDefinitionData) {
		
//		for(DiseaseModelAnnotationDTO dto: diseaseMetaDataDefinitionData.getData()) {
//
//			HashMap<String, Object> params = new HashMap<String, Object>();
//			
//			params.put("objectId", dto.getObjectId());
//			params.put("doid", dto.getDoid());
//			params.put("reference.curie", dto.getEvidence().getPublication().getPublicationId());
//			params.put("evidenceCode.curie", dto.evidenceCode());
//			
//			List<DiseaseAnnotationCurated> list = findByParams(params);
//			
//			if(list.size() == 0) {
//
//				// Insert
//				annot = new DiseaseAnnotationCurated();
//				//annot.setCurie(dto.getObjectId());
//				annot.setDoid(dto.getDoid());
//				create(annot);
//			} else {
//				// Update
//
//			}
//		}
		
		

	}

}
