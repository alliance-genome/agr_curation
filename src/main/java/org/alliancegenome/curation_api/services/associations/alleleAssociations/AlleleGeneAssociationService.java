package org.alliancegenome.curation_api.services.associations.alleleAssociations;

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

import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.associations.alleleAssociations.AlleleGeneAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.alleleAssociations.AlleleGeneAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.associations.alleleAssociations.AlleleGeneAssociationValidator;
import org.alliancegenome.curation_api.services.validation.dto.associations.alleleAssociations.AlleleGeneAssociationDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AlleleGeneAssociationService extends BaseEntityCrudService<AlleleGeneAssociation, AlleleGeneAssociationDAO> {

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
		return new ObjectResponse<AlleleGeneAssociation>(dbEntity);
	}

	public ObjectResponse<AlleleGeneAssociation> validate(AlleleGeneAssociation uiEntity) {
		AlleleGeneAssociation aga = alleleGeneAssociationValidator.validateAlleleGeneAssociation(uiEntity, true, false);
		return new ObjectResponse<AlleleGeneAssociation>(aga);
	}

	@Transactional
	public AlleleGeneAssociation upsert(AlleleGeneAssociationDTO dto) throws ObjectUpdateException {
		AlleleGeneAssociation association = alleleGeneAssociationDtoValidator.validateAlleleGeneAssociationDTO(dto);
		if (association != null)
			addAssociationToAllele(association);
		
		return association;
	}

	public List<Long> getAlleleGeneAssociationsByDataProvider(BackendBulkDataProvider dataProvider) {
		List<Long> ids = new ArrayList<>();
		
		List<String> alleleCuries = alleleDAO.findAllCuriesByDataProvider(dataProvider.sourceOrganization);
		alleleCuries.removeIf(Objects::isNull);
		
		alleleCuries.forEach(curie -> {
			Allele allele = alleleDAO.find(curie);
			if (allele != null) {
				if (CollectionUtils.isNotEmpty(allele.getAlleleGeneAssociations())) {
					ids.addAll(allele.getAlleleGeneAssociations().stream().map(AlleleGeneAssociation::getId).collect(Collectors.toList()));
				}
			} else {
				log.error("Failed getting allele " + curie + " for cleanup of allele gene associations");
			}
		});
		
		return ids;
	}

	public void removeAssociation(Long id, String dataProviderName, String md5sum) {
		AlleleGeneAssociation association = alleleGeneAssociationDAO.find(id);
		if (association != null) {
			association.setSubject(null);
			Long noteId = null;
			if (association.getRelatedNote() != null)
				noteId = association.getRelatedNote().getId();
			alleleGeneAssociationDAO.remove(id);
			if (noteId != null)
				noteDAO.remove(noteId);
		} else {
			log.error("Failed getting allele-gene association: " + id);
		}
		
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
}
