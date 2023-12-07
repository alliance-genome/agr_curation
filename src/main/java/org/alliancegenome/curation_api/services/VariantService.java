package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.VariantDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.ingest.dto.VariantDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.validation.VariantValidator;
import org.alliancegenome.curation_api.services.validation.dto.VariantDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class VariantService extends SubmittedObjectCrudService<Variant, VariantDTO, VariantDAO> {

	@Inject VariantDAO variantDAO;
	@Inject NoteService noteService;
	@Inject VariantValidator variantValidator;
	@Inject VariantDTOValidator variantDtoValidator;
	@Inject DiseaseAnnotationService diseaseAnnotationService;
	@Inject PersonService personService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(variantDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<Variant> update(Variant uiEntity) {
		Variant dbEntity = variantValidator.validateVariantUpdate(uiEntity);
		return new ObjectResponse<Variant>(dbEntity);
	}
	
	@Override
	@Transactional
	public ObjectResponse<Variant> create(Variant uiEntity) {
		Variant dbEntity = variantValidator.validateVariantCreate(uiEntity);
		return new ObjectResponse<Variant>(dbEntity);
	}

	public Variant upsert(VariantDTO dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		return variantDtoValidator.validateVariantDTO(dto, dataProvider);
	}
	
	@Transactional
	public void removeOrDeprecateNonUpdated(Long id, String loadDescription) {
		Variant variant = variantDAO.find(id);
		if (variant != null) {
			List<Long> referencingDAIds = variantDAO.findReferencingDiseaseAnnotationIds(id);
			Boolean anyReferencingEntities = false;
			for (Long daId : referencingDAIds) {
				DiseaseAnnotation referencingDA = diseaseAnnotationService.deprecateOrDeleteAnnotationAndNotes(daId, false, loadDescription, true);
				if (referencingDA != null)
					anyReferencingEntities = true;
			}
			if (anyReferencingEntities) {
				if (!variant.getObsolete()) {
					variant.setUpdatedBy(personService.fetchByUniqueIdOrCreate(loadDescription));
					variant.setDateUpdated(OffsetDateTime.now());
					variant.setObsolete(true);
					variantDAO.persist(variant);
				}
			} else {
				variantDAO.remove(id);
			}
		} else {
			log.error("Failed getting variant: " + id);
		}
	}
	
	public List<Long> getIdsByDataProvider(String dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider);
		List<Long> ids = variantDAO.findFilteredIds(params);
		ids.removeIf(Objects::isNull);
		return ids;
	}

}
