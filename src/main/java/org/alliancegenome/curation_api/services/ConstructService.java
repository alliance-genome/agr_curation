package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.ConstructDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.associations.constructAssociations.ConstructGenomicEntityAssociationService;
import org.alliancegenome.curation_api.services.base.BaseDTOCrudService;
import org.alliancegenome.curation_api.services.validation.ConstructValidator;
import org.alliancegenome.curation_api.services.validation.dto.ConstructDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class ConstructService extends BaseDTOCrudService<Construct, ConstructDTO, ConstructDAO> {

	@Inject
	ConstructDAO constructDAO;
	@Inject
	ConstructValidator constructValidator;
	@Inject
	ConstructDTOValidator constructDtoValidator;
	@Inject
	ConstructService constructService;
	@Inject
	PersonService personService;
	@Inject
	ConstructComponentSlotAnnotationDAO constructComponentDAO;
	@Inject
	ConstructGenomicEntityAssociationService constructGenomicEntityAssociationService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(constructDAO);
	}

	@Override
	public ObjectResponse<Construct> get(String identifier) {
		SearchResponse<Construct> ret = findByField("curie", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<Construct>(ret.getResults().get(0));
		
		ret = findByField("modEntityId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<Construct>(ret.getResults().get(0));
		
		ret = findByField("modInternalId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<Construct>(ret.getResults().get(0));
		
		ret = findByField("uniqueId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<Construct>(ret.getResults().get(0));
				
		return new ObjectResponse<Construct>();
	}

	@Override
	@Transactional
	public ObjectResponse<Construct> update(Construct uiEntity) {
		Construct dbEntity = constructValidator.validateConstructUpdate(uiEntity);
		return new ObjectResponse<>(constructDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<Construct> create(Construct uiEntity) {
		Construct dbEntity = constructValidator.validateConstructCreate(uiEntity);
		return new ObjectResponse<>(constructDAO.persist(dbEntity));
	}

	@Transactional
	public Construct upsert(ConstructDTO dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		Construct construct = constructDtoValidator.validateConstructDTO(dto, dataProvider);

		return constructDAO.persist(construct);
	}

	@Override
	@Transactional
	public ObjectResponse<Construct> delete(Long id) {
		constructService.removeOrDeprecateNonUpdated(id, true, "Construct DELETE API call");
		ObjectResponse<Construct> ret = new ObjectResponse<>();
		return ret;
	}
	
	@Transactional
	public Construct removeOrDeprecateNonUpdated(Long id, Boolean throwApiError, String loadDescription) {
		Construct construct = constructDAO.find(id);

		if (construct == null) {
			String errorMessage = "Could not find Construct with id: " + id;
			if (throwApiError) {
				ObjectResponse<Construct> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			log.error(errorMessage);
			return null;
		}
		
		Boolean anyReferencingEntities = false;
		if (CollectionUtils.isNotEmpty(construct.getConstructGenomicEntityAssociations())) {
			for (ConstructGenomicEntityAssociation association : construct.getConstructGenomicEntityAssociations()) {
				association = constructGenomicEntityAssociationService.deprecateOrDeleteAssociation(association.getId(), false, loadDescription, true);
				if (association != null)
					anyReferencingEntities = true;
			}
		}

		if (anyReferencingEntities) {
			if (!construct.getObsolete()) {
				construct.setObsolete(true);
				if (authenticatedPerson.getOktaEmail() != null) {
					construct.setUpdatedBy(personService.findPersonByOktaEmail(authenticatedPerson.getOktaEmail()));
				} else {
					construct.setUpdatedBy(personService.fetchByUniqueIdOrCreate(loadDescription));
				}
				construct.setDateUpdated(OffsetDateTime.now());
				return constructDAO.persist(construct);
			} else {
				return construct;
			}
		} else {
			deleteConstructSlotAnnotations(construct);
			constructDAO.remove(id);
		}

		return null;
	}

	@Override
	public void removeOrDeprecateNonUpdated(String curie, String loadDescription) { }

	public List<Long> getConstructIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		List<String> constructIdStrings = constructDAO.findFilteredIds(params);
		constructIdStrings.removeIf(Objects::isNull);
		List<Long>  constructIds = constructIdStrings.stream().map(Long::parseLong).collect(Collectors.toList());
		
		return constructIds;
	}
	
	private void deleteConstructSlotAnnotations(Construct construct) {
		if (CollectionUtils.isNotEmpty(construct.getConstructComponents())) {
			construct.getConstructComponents().forEach(cc -> {
				List<Note> notesToDelete = cc.getRelatedNotes();
				if (CollectionUtils.isNotEmpty(notesToDelete))
					notesToDelete.forEach(note -> {
						constructComponentDAO.deleteAttachedNote(note.getId());
						
					});
				constructComponentDAO.remove(cc.getId());
			});
		}
	}
}
