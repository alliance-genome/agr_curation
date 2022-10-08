package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.dao.ontology.NcbiTaxonTermDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.model.ingest.dto.SynonymDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseDTOCrudService;
import org.alliancegenome.curation_api.services.helpers.validators.AlleleValidator;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AlleleService extends BaseDTOCrudService<Allele, AlleleDTO, AlleleDAO> {

	@Inject AlleleDAO alleleDAO;
	@Inject AlleleValidator alleleValidator;
	@Inject GeneDAO geneDAO;
	@Inject CrossReferenceService crossReferenceService;
	@Inject SynonymService synonymService;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject NcbiTaxonTermDAO ncbiTaxonTermDAO;
	@Inject PersonService personService;
	@Inject VocabularyTermDAO vocabularyTermDAO;
	@Inject ReferenceDAO referenceDAO;
	@Inject ReferenceService referenceService;
	@Inject AuditedObjectService<Allele, AlleleDTO> auditedObjectService;
	@Inject BiologicalEntityService<Allele, AlleleDTO> biologicalEntityService;
	@Inject GenomicEntityService<Allele, AlleleDTO> genomicEntityService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<Allele> update(Allele uiEntity) {
		Allele dbEntity = alleleValidator.validateAlleleUpdate(uiEntity);
		return new ObjectResponse<Allele>(alleleDAO.persist(dbEntity));
	}
	
	@Override
	@Transactional
	public ObjectResponse<Allele> create(Allele uiEntity) {
		Allele dbEntity = alleleValidator.validateAlleleCreate(uiEntity);
		return new ObjectResponse<Allele>(alleleDAO.persist(dbEntity));
	}

	@Transactional
	public Allele upsert(AlleleDTO dto) throws ObjectUpdateException {
		Allele allele = validateAlleleDTO(dto);
		
		if (allele == null) return null;
		
		return alleleDAO.persist(allele);
	}
	
	public void removeNonUpdatedAlleles(String taxonIds, List<String> alleleCuriesBefore, List<String> alleleCuriesAfter) {
		log.debug("runLoad: After: " + taxonIds + " " + alleleCuriesAfter.size());

		List<String> distinctAfter = alleleCuriesAfter.stream().distinct().collect(Collectors.toList());
		log.debug("runLoad: Distinct: " + taxonIds + " " + distinctAfter.size());

		List<String> curiesToRemove = ListUtils.subtract(alleleCuriesBefore, distinctAfter);
		log.debug("runLoad: Remove: " + taxonIds + " " + curiesToRemove.size());

		log.info("Deleting disease annotations linked to " + curiesToRemove.size() + " unloaded alleles");
		List<String> foundAlleleCuries = new ArrayList<String>();
		for (String curie : curiesToRemove) {
			Allele allele = alleleDAO.find(curie);
			if (allele != null) {
				foundAlleleCuries.add(curie);
			} else {
				log.error("Failed getting allele: " + curie);
			}
		}
		alleleDAO.deleteReferencingDiseaseAnnotations(foundAlleleCuries);
		foundAlleleCuries.forEach(curie -> {delete(curie);});
		log.info("Deletion of disease annotations linked to unloaded alleles finished");
	}
	
	public List<String> getCuriesByTaxonId(String taxonId) {
		List<String> curies = alleleDAO.findAllCuriesByTaxon(taxonId);
		curies.removeIf(Objects::isNull);
		return curies;
	}
	
	private Allele validateAlleleDTO(AlleleDTO dto) throws ObjectValidationException {
		ObjectResponse<Allele> alleleResponse = new ObjectResponse<Allele>();
		Allele allele = null;
		if (StringUtils.isBlank(dto.getCurie())) {
			alleleResponse.addErrorMessage("curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			allele = alleleDAO.find(dto.getCurie());
		}
		
		if (allele == null)
			allele = new Allele();
		
		allele.setCurie(dto.getCurie());
		
		ObjectResponse<Allele> aoResponse = auditedObjectService.validateAuditedObjectDTO(allele, dto);
		ObjectResponse<Allele> beResponse = biologicalEntityService.validateBiologicalEntityDTO(aoResponse.getEntity(), dto);
		ObjectResponse<Allele> geResponse = genomicEntityService.validateGenomicEntityDTO(beResponse.getEntity(), dto);
		alleleResponse.addErrorMessages(aoResponse.getErrorMessages());
		alleleResponse.addErrorMessages(beResponse.getErrorMessages());
		alleleResponse.addErrorMessages(geResponse.getErrorMessages());
		
		allele = geResponse.getEntity();
		
		if (StringUtils.isBlank(dto.getName()))
			alleleResponse.addErrorMessage("name", ValidationConstants.REQUIRED_MESSAGE);
		allele.setName(dto.getName());
		
		String symbol = null;
		if (StringUtils.isNotBlank(dto.getSymbol())) 
			symbol = dto.getSymbol();
		allele.setSymbol(symbol);
				
		VocabularyTerm inheritenceMode = null;
		if (StringUtils.isNotBlank(dto.getInheritanceMode())) {
			inheritenceMode = vocabularyTermDAO.getTermInVocabulary(dto.getInheritanceMode(), VocabularyConstants.ALLELE_INHERITANCE_MODE_VOCABULARY);
			if (inheritenceMode == null) {
				alleleResponse.addErrorMessage("inheritanceMode", ValidationConstants.INVALID_MESSAGE);
			}
		}
		allele.setInheritanceMode(inheritenceMode);
		
		VocabularyTerm inCollection = null;
		if (StringUtils.isNotBlank(dto.getInCollection())) {
			inCollection = vocabularyTermDAO.getTermInVocabulary(dto.getInCollection(), VocabularyConstants.ALLELE_COLLECTION_VOCABULARY);
			if (inCollection == null) {
				alleleResponse.addErrorMessage("inCollection", ValidationConstants.INVALID_MESSAGE);
			}
		}
		allele.setInCollection(inCollection);
		
		VocabularyTerm sequencingStatus = null;
		if (StringUtils.isNotBlank(dto.getSequencingStatus())) {
			sequencingStatus = vocabularyTermDAO.getTermInVocabulary(dto.getSequencingStatus(), VocabularyConstants.SEQUENCING_STATUS_VOCABULARY);
			if (sequencingStatus == null) {
				alleleResponse.addErrorMessage("sequencingStatus", ValidationConstants.INVALID_MESSAGE);
			}
		}
		allele.setSequencingStatus(sequencingStatus);

		if (allele.getIsExtinct() != null)
			allele.setIsExtinct(allele.getIsExtinct());
		
		List<Reference> references = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getReferences())) {
			for (String publicationId : dto.getReferences()) {
				Reference reference = referenceDAO.find(publicationId);
				if (reference == null || reference.getObsolete()) {
					reference = referenceService.retrieveFromLiteratureService(publicationId);
					if (reference == null) {
						alleleResponse.addErrorMessage("references", ValidationConstants.INVALID_MESSAGE);
						break;
					}
				}
				references.add(reference);
			}
		}
		allele.setReferences(references);
		
		if (CollectionUtils.isNotEmpty(dto.getSynonyms())) {
			List<Synonym> synonyms= new ArrayList<>();
			for (SynonymDTO synonymDTO : dto.getSynonyms()) {
				ObjectResponse<Synonym> synResponse = synonymService.validateSynonymDTO(synonymDTO);
				if (synResponse.hasErrors()) {
					alleleResponse.addErrorMessage("synonyms", synResponse.getErrorMessagesString());
					break;
				} else {
					synonyms.add(synResponse.getEntity());
				}
			}
			allele.setSynonyms(synonyms);
		}
		
		if (alleleResponse.hasErrors()) {
			throw new ObjectValidationException(dto, alleleResponse.getErrorMessagesString());
		}
		
		return allele;
	}

}
