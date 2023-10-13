package org.alliancegenome.curation_api.services.associations.alleleAssociations;

import java.time.OffsetDateTime;
import java.util.ArrayList;
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
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.associations.alleleAssociations.AlleleGeneAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.alleleAssociations.AlleleGeneAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.base.BaseAssociationDTOCrudService;
import org.alliancegenome.curation_api.services.validation.associations.alleleAssociations.AlleleGeneAssociationValidator;
import org.alliancegenome.curation_api.services.validation.dto.associations.alleleAssociations.AlleleGeneAssociationDTOValidator;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AlleleGeneAssociationService extends BaseAssociationDTOCrudService<AlleleGeneAssociation, AlleleGeneAssociationDTO, AlleleGeneAssociationDAO> {

	@Inject
	AlleleGeneAssociationDAO alleleGeneAssociationDAO;
	@Inject
	AlleleGeneAssociationValidator alleleGeneAssociationValidator;
	@Inject
	AlleleGeneAssociationDTOValidator alleleGeneAssociationDtoValidator;
	@Inject
	AlleleDAO alleleDAO;
	@Inject
	NoteDAO noteDAO;
	@Inject
	GeneDAO geneDAO;
	@Inject
	PersonService personService;
	@Inject
	PersonDAO personDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleGeneAssociationDAO);
	}

	@Transactional
	public ObjectResponse<AlleleGeneAssociation> upsert(AlleleGeneAssociation uiEntity) {
		AlleleGeneAssociation dbEntity = alleleGeneAssociationValidator.validateAlleleGeneAssociation(uiEntity, true, true);
		if (dbEntity == null)
			return null;
		dbEntity = alleleGeneAssociationDAO.persist(dbEntity);
		addAssociationToAllele(dbEntity);
		addAssociationToGene(dbEntity);
		return new ObjectResponse<AlleleGeneAssociation>(dbEntity);
	}

	public ObjectResponse<AlleleGeneAssociation> validate(AlleleGeneAssociation uiEntity) {
		AlleleGeneAssociation aga = alleleGeneAssociationValidator.validateAlleleGeneAssociation(uiEntity, true, false);
		return new ObjectResponse<AlleleGeneAssociation>(aga);
	}

	@Transactional
	public AlleleGeneAssociation upsert(AlleleGeneAssociationDTO dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		AlleleGeneAssociation association = alleleGeneAssociationDtoValidator.validateAlleleGeneAssociationDTO(dto, dataProvider);
		if (association != null) {
			addAssociationToAllele(association);
			addAssociationToGene(association);
		}
			
		return association;
	}

	public List<Long> getAssociationsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		List<String> associationIdStrings = alleleGeneAssociationDAO.findFilteredIds(params);
		associationIdStrings.removeIf(Objects::isNull);
		List<Long> associationIds = associationIdStrings.stream().map(Long::parseLong).collect(Collectors.toList());
		
		return associationIds;
	}

	@Transactional
	public AlleleGeneAssociation deprecateOrDeleteAssociation(Long id, Boolean throwApiError, String loadDescription, Boolean deprecate) {
		AlleleGeneAssociation association = alleleGeneAssociationDAO.find(id);
		
		if (association == null) {
			String errorMessage = "Could not find AlleleGeneAssociation with id: " + id;
			if (throwApiError) {
				ObjectResponse<AlleleGeneAssociation> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			log.error(errorMessage);
			return null;
		}
		if (deprecate) {
			if (!association.getObsolete()) {
				association.setObsolete(true);
				if (authenticatedPerson.getId() != null) {
					association.setUpdatedBy(personDAO.find(authenticatedPerson.getId()));
				} else {
					association.setUpdatedBy(personService.fetchByUniqueIdOrCreate(loadDescription));
				}
				association.setDateUpdated(OffsetDateTime.now());
				return alleleGeneAssociationDAO.persist(association);
			}
			return association;
		}
		
		Long noteId = null;
		if (association.getRelatedNote() != null)
			noteId = association.getRelatedNote().getId();
		alleleGeneAssociationDAO.remove(association.getId());
		if (noteId != null)
			noteDAO.remove(noteId);
		
		return null;
	}
	
	public ObjectResponse<AlleleGeneAssociation> getAssociation(String alleleCurie, String relationName, String geneCurie) {
		AlleleGeneAssociation association = null;
		
		Map<String, Object> params = new HashMap<>();
		params.put("subject.curie", alleleCurie);
		params.put("relation.name", relationName);
		params.put("object.curie", geneCurie);

		SearchResponse<AlleleGeneAssociation> resp = alleleGeneAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null)
			association = resp.getSingleResult();
		
		ObjectResponse<AlleleGeneAssociation> response = new ObjectResponse<>();
		response.setEntity(association);
		
		return response;
	}
	
	private void addAssociationToAllele(AlleleGeneAssociation association) {
		Allele allele = association.getSubject();
		List<AlleleGeneAssociation> currentAssociations = allele.getAlleleGeneAssociations();
		if (currentAssociations == null)
			currentAssociations = new ArrayList<>();
		List<Long> currentAssociationIds = currentAssociations.stream().map(AlleleGeneAssociation::getId).collect(Collectors.toList());
		if (!currentAssociationIds.contains(association.getId()));
			currentAssociations.add(association);
		allele.setAlleleGeneAssociations(currentAssociations);
		alleleDAO.persist(allele);
	}
	
	private void addAssociationToGene(AlleleGeneAssociation association) {
		Gene gene = association.getObject();
		List<AlleleGeneAssociation> currentAssociations = gene.getAlleleGeneAssociations();
		if (currentAssociations == null)
			currentAssociations = new ArrayList<>();
		List<Long> currentAssociationIds = currentAssociations.stream().map(AlleleGeneAssociation::getId).collect(Collectors.toList());
		if (!currentAssociationIds.contains(association.getId()));
			currentAssociations.add(association);
		gene.setAlleleGeneAssociations(currentAssociations);
		geneDAO.persist(gene);
	}
}
