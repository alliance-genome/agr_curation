package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.PhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.AffectedGenomicModelDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.constructAssociations.ConstructGenomicEntityAssociationService;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.validation.AffectedGenomicModelValidator;
import org.alliancegenome.curation_api.services.validation.dto.AffectedGenomicModelDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AffectedGenomicModelService extends SubmittedObjectCrudService<AffectedGenomicModel, AffectedGenomicModelDTO, AffectedGenomicModelDAO> {

	@Inject AffectedGenomicModelDAO agmDAO;
	@Inject AlleleDAO alleleDAO;
	@Inject AffectedGenomicModelValidator agmValidator;
	@Inject AffectedGenomicModelDTOValidator agmDtoValidator;
	@Inject DiseaseAnnotationService diseaseAnnotationService;
	@Inject PhenotypeAnnotationService phenotypeAnnotationService;
	@Inject PersonService personService;
	@Inject ConstructGenomicEntityAssociationService constructGenomicEntityAssociationService;

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

		if (agm == null) {
			return null;
		}

		return agmDAO.persist(agm);
	}

	@Transactional
	public void removeOrDeprecateNonUpdated(Long id, String loadDescription) {
		AffectedGenomicModel agm = agmDAO.find(id);
		if (agm != null) {
			if (agmDAO.hasReferencingDiseaseAnnotations(id) || agmDAO.hasReferencingPhenotypeAnnotations(id) ||
					CollectionUtils.isNotEmpty(agm.getConstructGenomicEntityAssociations())) {
				if (!agm.getObsolete()) {
					agm.setUpdatedBy(personService.fetchByUniqueIdOrCreate(loadDescription));
					agm.setDateUpdated(OffsetDateTime.now());
					agm.setObsolete(true);
					agmDAO.persist(agm);
				}
			} else {
				agmDAO.remove(id);
			}
		} else {
			log.error("Failed getting AGM: " + id);
		}
	}

	public List<Long> getIdsByDataProvider(String dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider);
		List<Long> ids = agmDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);
		return ids;
	}

}
