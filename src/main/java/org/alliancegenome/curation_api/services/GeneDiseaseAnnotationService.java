package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.dao.GeneDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseDTOCrudService;
import org.alliancegenome.curation_api.services.validation.GeneDiseaseAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.dto.GeneDiseaseAnnotationDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class GeneDiseaseAnnotationService extends BaseDTOCrudService<GeneDiseaseAnnotation, GeneDiseaseAnnotationDTO, GeneDiseaseAnnotationDAO> {

	@Inject
	GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
	@Inject
	GeneDiseaseAnnotationValidator geneDiseaseValidator;
	@Inject
	GeneDiseaseAnnotationDTOValidator geneDiseaseAnnotationDtoValidator;
	@Inject
	DiseaseAnnotationService diseaseAnnotationService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneDiseaseAnnotationDAO);
	}

	@Override
	public ObjectResponse<GeneDiseaseAnnotation> get(String identifier) {
		SearchResponse<GeneDiseaseAnnotation> ret = findByField("modEntityId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<GeneDiseaseAnnotation>(ret.getResults().get(0));
		
		ret = findByField("modInternalId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<GeneDiseaseAnnotation>(ret.getResults().get(0));
		
		ret = findByField("uniqueId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<GeneDiseaseAnnotation>(ret.getResults().get(0));
				
		return new ObjectResponse<GeneDiseaseAnnotation>();
	}

	@Override
	@Transactional
	public ObjectResponse<GeneDiseaseAnnotation> update(GeneDiseaseAnnotation uiEntity) {
		GeneDiseaseAnnotation dbEntity = geneDiseaseValidator.validateAnnotationUpdate(uiEntity);
		return new ObjectResponse<>(geneDiseaseAnnotationDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<GeneDiseaseAnnotation> create(GeneDiseaseAnnotation uiEntity) {
		GeneDiseaseAnnotation dbEntity = geneDiseaseValidator.validateAnnotationCreate(uiEntity);
		return new ObjectResponse<>(geneDiseaseAnnotationDAO.persist(dbEntity));
	}

	@Transactional
	public GeneDiseaseAnnotation upsert(GeneDiseaseAnnotationDTO dto) throws ObjectUpdateException {
		GeneDiseaseAnnotation annotation = geneDiseaseAnnotationDtoValidator.validateGeneDiseaseAnnotationDTO(dto);

		return geneDiseaseAnnotationDAO.persist(annotation);
	}

	@Override
	@Transactional
	public ObjectResponse<GeneDiseaseAnnotation> delete(Long id) {
		diseaseAnnotationService.deprecateOrDeleteAnnotationAndNotes(id, true, "Gene disease annotation DELETE API call", false);
		ObjectResponse<GeneDiseaseAnnotation> ret = new ObjectResponse<>();
		return ret;
	}

	@Override
	public void removeOrDeprecateNonUpdated(String curie, String dataProvider, String md5sum) { }
}
