package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.VariantDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.ingest.dto.VariantDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseDTOCrudService;
import org.alliancegenome.curation_api.services.validation.VariantValidator;
import org.alliancegenome.curation_api.services.validation.dto.VariantDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class VariantService extends BaseDTOCrudService<Variant, VariantDTO, VariantDAO> {

	@Inject VariantDAO variantDAO;
	@Inject NoteService noteService;
	@Inject VariantValidator variantValidator;
	@Inject VariantDTOValidator variantDtoValidator;

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
	public void removeOrDeprecateNonUpdated(String curie, String loadDescription) {
		Variant variant = variantDAO.find(curie);
		if (variant != null) {
			List<Note> notesToDelete = variant.getRelatedNotes();
			variantDAO.remove(curie);
			if (CollectionUtils.isNotEmpty(notesToDelete))
				notesToDelete.forEach(note -> noteService.delete(note.getId()));
		} else {
			log.error("Failed getting variant: " + curie);
		}
	}
	
	public List<String> getCuriesByDataProvider(String dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider);
		List<String> curies = variantDAO.findFilteredIds(params);
		curies.removeIf(Objects::isNull);
		return curies;
	}

}
