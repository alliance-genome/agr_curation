package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.model.ingest.dto.SynonymDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AlleleDTOValidator extends BaseDTOValidator {

	@Inject AlleleDAO alleleDAO;
	@Inject VocabularyTermDAO vocabularyTermDAO;
	@Inject ReferenceDAO referenceDAO;
	@Inject ReferenceService referenceService;

	public Allele validateAlleleDTO(AlleleDTO dto) throws ObjectValidationException {
		
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
		
		ObjectResponse<Allele> geResponse = validateGenomicEntityDTO(allele, dto);
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

		allele.setIsExtinct(dto.getIsExtinct());
		
		if (CollectionUtils.isNotEmpty(dto.getReferences())) {
			List<Reference> references = new ArrayList<>();
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
			allele.setReferences(references);
		} else {
			allele.setReferences(null);
		}
		
		if (alleleResponse.hasErrors()) {
			throw new ObjectValidationException(dto, alleleResponse.errorMessagesString());
		}
		
		return allele;
	}

}
