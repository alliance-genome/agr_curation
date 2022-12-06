package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.AlleleMutationTypeSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseDTOCrudService;
import org.alliancegenome.curation_api.services.validation.AlleleValidator;
import org.alliancegenome.curation_api.services.validation.dto.AlleleDTOValidator;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AlleleService extends BaseDTOCrudService<Allele, AlleleDTO, AlleleDAO> {

	@Inject AlleleDAO alleleDAO;
	@Inject AlleleMutationTypeSlotAnnotationDAO alleleMutationTypeDAO;
	@Inject AlleleSymbolSlotAnnotationDAO alleleSymbolDAO;
	@Inject AlleleFullNameSlotAnnotationDAO alleleFullNameDAO;
	@Inject AlleleSynonymSlotAnnotationDAO alleleSynonymDAO;
	@Inject AlleleValidator alleleValidator;
	@Inject AlleleDTOValidator alleleDtoValidator;
	@Inject DiseaseAnnotationService diseaseAnnotationService;
	@Inject PersonService personService;

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

	public Allele upsert(AlleleDTO dto) throws ObjectUpdateException {
		return alleleDtoValidator.validateAlleleDTO(dto);
	}
	
	@Transactional
	public void removeOrDeprecateNonUpdatedAlleles(String taxonIds, List<String> alleleCuriesBefore, List<String> alleleCuriesAfter, String dataType) {
		log.debug("runLoad: After: " + taxonIds + " " + alleleCuriesAfter.size());

		List<String> distinctAfter = alleleCuriesAfter.stream().distinct().collect(Collectors.toList());
		log.debug("runLoad: Distinct: " + taxonIds + " " + distinctAfter.size());

		List<String> curiesToRemove = ListUtils.subtract(alleleCuriesBefore, distinctAfter);
		log.debug("runLoad: Remove: " + taxonIds + " " + curiesToRemove.size());

		ProcessDisplayHelper ph = new ProcessDisplayHelper(1000);
		ph.startProcess("Deletion/deprecation of disease annotations linked to unloaded " + taxonIds + " alleles", curiesToRemove.size());
		for (String curie : curiesToRemove) {
			Allele allele = alleleDAO.find(curie);
			if (allele != null) {
				List<Long> referencingDAIds = alleleDAO.findReferencingDiseaseAnnotationIds(curie);
				Boolean anyPublicReferencingDAs = false;
				for (Long daId : referencingDAIds) {
					Boolean daMadePublic = diseaseAnnotationService.deprecateOrDeleteAnnotationAndNotes(daId, false, "allele");
					if (daMadePublic)
						anyPublicReferencingDAs = true;
				}
				
				if (anyPublicReferencingDAs) {
					allele.setUpdatedBy(personService.fetchByUniqueIdOrCreate(dataType + " allele bulk upload"));
					allele.setDateUpdated(OffsetDateTime.now());
					allele.setObsolete(true);
					alleleDAO.persist(allele);
				} else {
					deleteAlleleSlotAnnotations(allele);
					alleleDAO.remove(curie);
				}
			} else {
				log.error("Failed getting allele: " + curie);
			}
			ph.progressProcess();
		}
		ph.finishProcess();
	}
	
	public List<String> getCuriesByTaxonId(String taxonId) {
		List<String> curies = alleleDAO.findAllCuriesByTaxon(taxonId);
		curies.removeIf(Objects::isNull);
		return curies;
	}
	
	private void deleteAlleleSlotAnnotations(Allele allele) {
		if (CollectionUtils.isNotEmpty(allele.getAlleleMutationTypes()))
			allele.getAlleleMutationTypes().forEach(amt -> {alleleMutationTypeDAO.remove(amt.getId());});
		
		if (allele.getAlleleSymbol() != null)
			alleleSymbolDAO.remove(allele.getAlleleSymbol().getId());
		
		if (allele.getAlleleFullName() != null)
			alleleFullNameDAO.remove(allele.getAlleleFullName().getId());
		
		if (CollectionUtils.isNotEmpty(allele.getAlleleSynonyms()))
			allele.getAlleleSynonyms().forEach(as -> {alleleSynonymDAO.remove(as.getId());});
	}
}
