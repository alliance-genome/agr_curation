package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.alleleAssociations.AlleleGeneAssociationService;
import org.alliancegenome.curation_api.services.base.BaseDTOCrudService;
import org.alliancegenome.curation_api.services.validation.AlleleValidator;
import org.alliancegenome.curation_api.services.validation.dto.AlleleDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AlleleService extends BaseDTOCrudService<Allele, AlleleDTO, AlleleDAO> {

	@Inject AlleleDAO alleleDAO;
	@Inject AlleleMutationTypeSlotAnnotationDAO alleleMutationTypeDAO;
	@Inject AlleleInheritanceModeSlotAnnotationDAO alleleInheritanceModeDAO;
	@Inject AlleleSymbolSlotAnnotationDAO alleleSymbolDAO;
	@Inject AlleleFullNameSlotAnnotationDAO alleleFullNameDAO;
	@Inject AlleleSynonymSlotAnnotationDAO alleleSynonymDAO;
	@Inject AlleleValidator alleleValidator;
	@Inject AlleleDTOValidator alleleDtoValidator;
	@Inject DiseaseAnnotationService diseaseAnnotationService;
	@Inject PersonService personService;
	@Inject NoteService noteService;
	@Inject AlleleGeneAssociationService alleleGeneAssociationService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<Allele> update(Allele uiEntity) {
		Allele dbEntity = alleleValidator.validateAlleleUpdate(uiEntity);
		return new ObjectResponse<Allele>(dbEntity);
	}
	
	@Override
	@Transactional
	public ObjectResponse<Allele> create(Allele uiEntity) {
		Allele dbEntity = alleleValidator.validateAlleleCreate(uiEntity);
		return new ObjectResponse<Allele>(dbEntity);
	}

	public Allele upsert(AlleleDTO dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		return alleleDtoValidator.validateAlleleDTO(dto, dataProvider);
	}
	
	@Transactional
	public void removeOrDeprecateNonUpdated(String curie, String dataProviderName, String md5sum) {
		Allele allele = alleleDAO.find(curie);
		String loadDescription = dataProviderName + " Allele bulk load (" + md5sum + ")";
		if (allele != null) {
			List<Long> referencingDAIds = alleleDAO.findReferencingDiseaseAnnotationIds(curie);
			Boolean anyReferencingEntities = false;
			for (Long daId : referencingDAIds) {
				DiseaseAnnotation referencingDA = diseaseAnnotationService.deprecateOrDeleteAnnotationAndNotes(daId, false, loadDescription, true);
				if (referencingDA != null)
					anyReferencingEntities = true;
			}
			if (CollectionUtils.isNotEmpty(allele.getAlleleGeneAssociations())) {
				for (AlleleGeneAssociation association : allele.getAlleleGeneAssociations()) {
					association = alleleGeneAssociationService.deprecateOrDeleteAssociation(association.getId(), false, loadDescription, true);
					if (association != null)
						anyReferencingEntities = true;
				}
			}
			if (anyReferencingEntities) {
				if (!allele.getObsolete()) {
					allele.setUpdatedBy(personService.fetchByUniqueIdOrCreate(loadDescription));
					allele.setDateUpdated(OffsetDateTime.now());
					allele.setObsolete(true);
					alleleDAO.persist(allele);
				}
			} else {
				deleteAlleleSlotAnnotations(allele);
				List<Note> notesToDelete = allele.getRelatedNotes();
				alleleDAO.remove(curie);
				if (CollectionUtils.isNotEmpty(notesToDelete))
					notesToDelete.forEach(note -> noteService.delete(note.getId()));
			}
		} else {
			log.error("Failed getting allele: " + curie);
		}
	}
	
	public List<String> getCuriesByDataProvider(String dataProvider) {
		List<String> curies = alleleDAO.findAllCuriesByDataProvider(dataProvider);
		curies.removeIf(Objects::isNull);
		return curies;
	}
	
	private void deleteAlleleSlotAnnotations(Allele allele) {
		if (CollectionUtils.isNotEmpty(allele.getAlleleMutationTypes()))
			allele.getAlleleMutationTypes().forEach(amt -> {alleleMutationTypeDAO.remove(amt.getId());});
		
		if (CollectionUtils.isNotEmpty(allele.getAlleleInheritanceModes()))
			allele.getAlleleInheritanceModes().forEach(aim -> {alleleInheritanceModeDAO.remove(aim.getId());});
		
		if (allele.getAlleleSymbol() != null)
			alleleSymbolDAO.remove(allele.getAlleleSymbol().getId());
		
		if (allele.getAlleleFullName() != null)
			alleleFullNameDAO.remove(allele.getAlleleFullName().getId());
		
		if (CollectionUtils.isNotEmpty(allele.getAlleleSynonyms()))
			allele.getAlleleSynonyms().forEach(as -> {alleleSynonymDAO.remove(as.getId());});
	}

}
