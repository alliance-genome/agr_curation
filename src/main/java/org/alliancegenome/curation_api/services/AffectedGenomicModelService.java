package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.ingest.dto.AffectedGenomicModelDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.ConstructGenomicEntityAssociationService;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.validation.AffectedGenomicModelValidator;
import org.alliancegenome.curation_api.services.validation.dto.AffectedGenomicModelDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AffectedGenomicModelService extends SubmittedObjectCrudService<AffectedGenomicModel, AffectedGenomicModelDTO, AffectedGenomicModelDAO> {

	@Inject AffectedGenomicModelDAO agmDAO;
	@Inject AffectedGenomicModelValidator agmValidator;
	@Inject AffectedGenomicModelDTOValidator agmDtoValidator;
	@Inject DiseaseAnnotationService diseaseAnnotationService;
	@Inject PhenotypeAnnotationService phenotypeAnnotationService;
	@Inject PersonService personService;
	@Inject ConstructGenomicEntityAssociationService constructGenomicEntityAssociationService;
	@Inject NoteService noteService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(agmDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<AffectedGenomicModel> update(AffectedGenomicModel uiEntity) {
		AffectedGenomicModel dbEntity = agmValidator.validateAffectedGenomicModelUpdate(uiEntity);
		return new ObjectResponse<>(dbEntity);
	}

	@Override
	@Transactional
	public ObjectResponse<AffectedGenomicModel> create(AffectedGenomicModel uiEntity) {
		AffectedGenomicModel dbEntity = agmValidator.validateAffectedGenomicModelCreate(uiEntity);
		return new ObjectResponse<>(dbEntity);
	}

	@Override
	public ObjectResponse<AffectedGenomicModel> upsert(AffectedGenomicModelDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return agmDtoValidator.validateAffectedGenomicModelDTO(dto, dataProvider);
	}

	@Override
	@Transactional
	public ObjectResponse<AffectedGenomicModel> deleteById(Long id) {
		deprecateOrDelete(id, true, "AGM DELETE API call", false);
		ObjectResponse<AffectedGenomicModel> ret = new ObjectResponse<>();
		return ret;
	}

	@Override
	@Transactional
	public AffectedGenomicModel deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean forceDeprecate) {
		AffectedGenomicModel agm = agmDAO.find(id);
		List<String> deprecationReasons = new ArrayList<>();
		if (agm != null) {
			if (forceDeprecate) {
				deprecationReasons.add("Deprecation instead of deletion rule applied");
			}
			if (agmDAO.hasReferencingDiseaseAnnotations(id)) {
				deprecationReasons.add("AGM is referenced by disease annotation(s)");
			}
			if (agmDAO.hasReferencingPhenotypeAnnotations(id)) {
				deprecationReasons.add("AGM is referenced by phenotype annotation(s)");
			}
			if (agmDAO.hasReferencingHTPExpressionDatasetSampleAnnotation(id)) {
				deprecationReasons.add("AGM is referenced by HTP expression dataset annotation(s)");
			}
			if (CollectionUtils.isNotEmpty(agm.getConstructGenomicEntityAssociations())) {
				deprecationReasons.add("AGM has construct association(s)");
			}
			if (CollectionUtils.isNotEmpty(agm.getAgmSequenceTargetingReagentAssociations())) {
				deprecationReasons.add("AGM has STR association(s)");
			}
			if (CollectionUtils.isNotEmpty(agm.getComponents())) {
				deprecationReasons.add("AGM has allele association(s)");
			}
			if (CollectionUtils.isNotEmpty(agm.getParentalPopulations())) {
				deprecationReasons.add("AGM has AGM association(s)");
			}
			if (CollectionUtils.isNotEmpty(deprecationReasons)) {
				if (!agm.getObsolete()) {
					agm.setUpdatedBy(personService.fetchByUniqueIdOrCreate(requestSource));
					agm.setDateUpdated(OffsetDateTime.now());
					agm.setObsolete(true);
					
					Note deprecationNote = noteService.createDeprecationNote(agm.getIdentifier(), requestSource, deprecationReasons);
					if (agm.getRelatedNotes() == null) {
						agm.setRelatedNotes(new ArrayList<>());
					}
					agm.getRelatedNotes().add(deprecationNote);
					
					return agmDAO.persist(agm);
				} else {
					return agm;
				}
			} else {
				agmDAO.remove(id);
			}
		} else {
			String errorMessage = "Could not find AGM with id: " + id;
			if (throwApiError) {
				ObjectResponse<AffectedGenomicModel> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			Log.error(errorMessage);
		}
		return null;
	}

	public List<Long> getAllIds() {
		return agmDAO.getAllIds();
	}

	public List<AffectedGenomicModel> findByIds(List<Long> ids) {
		return agmDAO.findByIds(ids);
	}

	public List<Long> getIdsByDataProvider(String dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider);
		List<Long> ids = agmDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);
		return ids;
	}

}
