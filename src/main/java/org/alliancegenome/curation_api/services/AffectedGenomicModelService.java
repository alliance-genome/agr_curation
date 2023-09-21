package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.AffectedGenomicModelDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.constructAssociations.ConstructGenomicEntityAssociationService;
import org.alliancegenome.curation_api.services.base.BaseDTOCrudService;
import org.alliancegenome.curation_api.services.validation.AffectedGenomicModelValidator;
import org.alliancegenome.curation_api.services.validation.dto.AffectedGenomicModelDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AffectedGenomicModelService extends BaseDTOCrudService<AffectedGenomicModel, AffectedGenomicModelDTO, AffectedGenomicModelDAO> {

	@Inject
	AffectedGenomicModelDAO agmDAO;
	@Inject
	AlleleDAO alleleDAO;
	@Inject
	AffectedGenomicModelValidator agmValidator;
	@Inject
	AffectedGenomicModelDTOValidator agmDtoValidator;
	@Inject
	DiseaseAnnotationService diseaseAnnotationService;
	@Inject
	PersonService personService;
	@Inject
	ConstructGenomicEntityAssociationService constructGenomicEntityAssociationService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(agmDAO);
	}
	
	@Override
	@Transactional
	public ObjectResponse<AffectedGenomicModel> update(AffectedGenomicModel uiEntity) {
		AffectedGenomicModel dbEntity = agmDAO.persist(agmValidator.validateAffectedGenomicModelUpdate(uiEntity));
		return new ObjectResponse<AffectedGenomicModel>(dbEntity);
	}
	
	@Override
	@Transactional
	public ObjectResponse<AffectedGenomicModel> create(AffectedGenomicModel uiEntity) {
		AffectedGenomicModel dbEntity = agmDAO.persist(agmValidator.validateAffectedGenomicModelCreate(uiEntity));
		return new ObjectResponse<AffectedGenomicModel>(dbEntity);
	}

	@Transactional
	public AffectedGenomicModel upsert(AffectedGenomicModelDTO dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		AffectedGenomicModel agm = agmDtoValidator.validateAffectedGenomicModelDTO(dto, dataProvider);

		if (agm == null)
			return null;

		return agmDAO.persist(agm);
	}

	@Transactional
	public void removeOrDeprecateNonUpdated(String curie, String loadDescription) {
		AffectedGenomicModel agm = agmDAO.find(curie);
		if (agm != null) {
			List<Long> referencingDAIds = agmDAO.findReferencingDiseaseAnnotations(curie);
			Boolean anyReferencingEntities = false;
			for (Long daId : referencingDAIds) {
				DiseaseAnnotation referencingDA = diseaseAnnotationService.deprecateOrDeleteAnnotationAndNotes(daId, false, loadDescription, true);
				if (referencingDA != null)
					anyReferencingEntities = true;
			}
			if (CollectionUtils.isNotEmpty(agm.getConstructGenomicEntityAssociations())) {
				for (ConstructGenomicEntityAssociation association : agm.getConstructGenomicEntityAssociations()) {
					association = constructGenomicEntityAssociationService.deprecateOrDeleteAssociation(association.getId(), false, loadDescription, true);
					if (association != null)
						anyReferencingEntities = true;
				}
			}

			if (anyReferencingEntities) {
				if (!agm.getObsolete()) {
					agm.setUpdatedBy(personService.fetchByUniqueIdOrCreate(loadDescription));
					agm.setDateUpdated(OffsetDateTime.now());
					agm.setObsolete(true);
					agmDAO.persist(agm);
				}
			} else {
				agmDAO.remove(curie);
			}
		} else {
			log.error("Failed getting AGM: " + curie);
		}
	}
	
	public List<String> getCuriesByDataProvider(String dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider);
		List<String> curies = agmDAO.findFilteredIds(params);
		curies.removeIf(Objects::isNull);
		return curies;
	}

}
