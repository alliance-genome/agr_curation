package org.alliancegenome.curation_api.services.associations.constructAssociations;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.dao.GenomicEntityDAO;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.associations.constructAssociations.ConstructGenomicEntityAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.constructAssociations.ConstructGenomicEntityAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.base.BaseAssociationDTOCrudService;
import org.alliancegenome.curation_api.services.validation.associations.constructAssociations.ConstructGenomicEntityAssociationValidator;
import org.alliancegenome.curation_api.services.validation.dto.associations.constructAssociations.ConstructGenomicEntityAssociationDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class ConstructGenomicEntityAssociationService extends BaseAssociationDTOCrudService<ConstructGenomicEntityAssociation, ConstructGenomicEntityAssociationDTO, ConstructGenomicEntityAssociationDAO> {

	@Inject
	ConstructGenomicEntityAssociationDAO constructGenomicEntityAssociationDAO;
	@Inject
	ConstructGenomicEntityAssociationValidator constructGenomicEntityAssociationValidator;
	@Inject
	ConstructGenomicEntityAssociationDTOValidator constructGenomicEntityAssociationDtoValidator;
	@Inject
	ConstructDAO constructDAO;
	@Inject
	GenomicEntityDAO genomicEntityDAO;
	@Inject
	PersonService personService;
	@Inject
	PersonDAO personDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(constructGenomicEntityAssociationDAO);
	}

	@Transactional
	public ObjectResponse<ConstructGenomicEntityAssociation> upsert(ConstructGenomicEntityAssociation uiEntity) {
		ConstructGenomicEntityAssociation dbEntity = constructGenomicEntityAssociationValidator.validateConstructGenomicEntityAssociation(uiEntity, true, true);
		if (dbEntity == null)
			return null;
		dbEntity = constructGenomicEntityAssociationDAO.persist(dbEntity);
		addAssociationToConstruct(dbEntity);
		addAssociationToGenomicEntity(dbEntity);
		return new ObjectResponse<ConstructGenomicEntityAssociation>(dbEntity);
	}

	public ObjectResponse<ConstructGenomicEntityAssociation> validate(ConstructGenomicEntityAssociation uiEntity) {
		ConstructGenomicEntityAssociation aga = constructGenomicEntityAssociationValidator.validateConstructGenomicEntityAssociation(uiEntity, true, false);
		return new ObjectResponse<ConstructGenomicEntityAssociation>(aga);
	}

	@Transactional
	public ConstructGenomicEntityAssociation upsert(ConstructGenomicEntityAssociationDTO dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		ConstructGenomicEntityAssociation association = constructGenomicEntityAssociationDtoValidator.validateConstructGenomicEntityAssociationDTO(dto, dataProvider);
		if (association != null) {
			addAssociationToConstruct(association);
			addAssociationToGenomicEntity(association);
		}
			
		return association;
	}

	public List<Long> getAssociationsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		List<String> associationIdStrings = constructGenomicEntityAssociationDAO.findFilteredIds(params);
		associationIdStrings.removeIf(Objects::isNull);
		List<Long> associationIds = associationIdStrings.stream().map(Long::parseLong).collect(Collectors.toList());
		
		return associationIds;
	}

	@Transactional
	public ConstructGenomicEntityAssociation deprecateOrDeleteAssociation(Long id, Boolean throwApiError, String loadDescription, Boolean deprecate) {
		ConstructGenomicEntityAssociation association = constructGenomicEntityAssociationDAO.find(id);
		
		if (association == null) {
			String errorMessage = "Could not find ConstructGenomicEntityAssociation with id: " + id;
			if (throwApiError) {
				ObjectResponse<ConstructGenomicEntityAssociation> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			log.error(errorMessage);
			return null;
		}
		if (deprecate) {
			if (!association.getObsolete()) {
				association.setObsolete(true);
				if (authenticatedPerson != null) {
					association.setUpdatedBy(personDAO.find(authenticatedPerson.getId()));
				} else {
					association.setUpdatedBy(personService.fetchByUniqueIdOrCreate(loadDescription));
				}
				association.setDateUpdated(OffsetDateTime.now());
				return constructGenomicEntityAssociationDAO.persist(association);
			}
		}
		
		List<Note> notesToDelete = association.getRelatedNotes();
		if (CollectionUtils.isNotEmpty(notesToDelete))
			notesToDelete.forEach(note -> constructGenomicEntityAssociationDAO.deleteAttachedNote(note.getId()));
		constructGenomicEntityAssociationDAO.remove(association.getId());
		
		
		return null;
	}
	
	public ObjectResponse<ConstructGenomicEntityAssociation> getAssociation(Long constructId, String relationName, String genomicEntityCurie) {
		ConstructGenomicEntityAssociation association = null;
		
		Map<String, Object> params = new HashMap<>();
		params.put("subjectConstruct.id", constructId);
		params.put("relation.name", relationName);
		params.put("objectGenomicEntity.curie", genomicEntityCurie);

		SearchResponse<ConstructGenomicEntityAssociation> resp = constructGenomicEntityAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null)
			association = resp.getSingleResult();
		
		ObjectResponse<ConstructGenomicEntityAssociation> response = new ObjectResponse<>();
		response.setEntity(association);
		
		return response;
	}
	
	private void addAssociationToConstruct(ConstructGenomicEntityAssociation association) {
		Construct construct = association.getSubjectConstruct();
		List<ConstructGenomicEntityAssociation> currentAssociations = construct.getConstructGenomicEntityAssociations();
		if (currentAssociations == null)
			currentAssociations = new ArrayList<>();
		List<Long> currentAssociationIds = currentAssociations.stream().map(ConstructGenomicEntityAssociation::getId).collect(Collectors.toList());
		if (!currentAssociationIds.contains(association.getId()));
			currentAssociations.add(association);
		construct.setConstructGenomicEntityAssociations(currentAssociations);
		constructDAO.persist(construct);
	}
	
	private void addAssociationToGenomicEntity(ConstructGenomicEntityAssociation association) {
		GenomicEntity genomicEntity = association.getObjectGenomicEntity();
		List<ConstructGenomicEntityAssociation> currentAssociations = genomicEntity.getConstructGenomicEntityAssociations();
		if (currentAssociations == null)
			currentAssociations = new ArrayList<>();
		List<Long> currentAssociationIds = currentAssociations.stream().map(ConstructGenomicEntityAssociation::getId).collect(Collectors.toList());
		if (!currentAssociationIds.contains(association.getId()));
			currentAssociations.add(association);
		genomicEntity.setConstructGenomicEntityAssociations(currentAssociations);
		genomicEntityDAO.persist(genomicEntity);
	}
}
