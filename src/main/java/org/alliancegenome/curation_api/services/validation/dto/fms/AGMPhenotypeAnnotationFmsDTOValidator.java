package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.AGMPhenotypeAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.AGMPhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.GenomicEntityService;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.AnnotationUniqueIdHelper;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AGMPhenotypeAnnotationFmsDTOValidator extends PhenotypeAnnotationFmsDTOValidator {

	@Inject
	AGMPhenotypeAnnotationDAO agmPhenotypeAnnotationDAO;
	@Inject
	GenomicEntityService genomicEntityService;
	
	public AGMPhenotypeAnnotation validatePrimaryAnnotation(AffectedGenomicModel subject, PhenotypeFmsDTO dto, BackendBulkDataProvider dataProvider) throws ObjectValidationException {

		ObjectResponse<AGMPhenotypeAnnotation> apaResponse = new ObjectResponse<AGMPhenotypeAnnotation>();
		AGMPhenotypeAnnotation annotation = new AGMPhenotypeAnnotation();
		
		ObjectResponse<Reference> refResponse = validateReference(dto);
		apaResponse.addErrorMessages(refResponse.getErrorMessages());
			
		Reference reference = refResponse.getEntity();
		String refString = reference == null ? null : reference.getCurie();
		
		String uniqueId = AnnotationUniqueIdHelper.getPhenotypeAnnotationUniqueId(dto, subject.getIdentifier(), refString);
		SearchResponse<AGMPhenotypeAnnotation> annotationSearch = agmPhenotypeAnnotationDAO.findByField("uniqueId", uniqueId);
		if (annotationSearch != null && annotationSearch.getSingleResult() != null)
			annotation = annotationSearch.getSingleResult();

		annotation.setUniqueId(uniqueId);
		annotation.setSingleReference(reference);
		annotation.setPhenotypeAnnotationSubject(subject);
		
		// Reset implied/asserted fields as secondary annotations loaded separately
		annotation.setAssertedAllele(null);
		annotation.setAssertedGenes(null);
		annotation.setInferredAllele(null);
		annotation.setInferredGene(null);
		
		ObjectResponse<AGMPhenotypeAnnotation> paResponse = validatePhenotypeAnnotation(annotation, dto, dataProvider);
		apaResponse.addErrorMessages(paResponse.getErrorMessages());
		annotation = paResponse.getEntity();
		
		if (apaResponse.hasErrors())
			throw new ObjectValidationException(dto, apaResponse.errorMessagesString());
		
		return annotation;

	}

	public AGMPhenotypeAnnotation validateInferredOrAssertedEntities(AffectedGenomicModel primaryAnnotationSubject, PhenotypeFmsDTO dto, List<Long> idsAdded, BackendBulkDataProvider dataProvider) throws ObjectValidationException {
		ObjectResponse<AGMPhenotypeAnnotation> apaResponse = new ObjectResponse<AGMPhenotypeAnnotation>();
		
		ObjectResponse<Reference> refResponse = validateReference(dto);
		apaResponse.addErrorMessages(refResponse.getErrorMessages());
			
		Reference reference = refResponse.getEntity();
		String refString = reference == null ? null : reference.getCurie();
		
		String primaryAnnotationUniqueId = AnnotationUniqueIdHelper.getPhenotypeAnnotationUniqueId(dto, primaryAnnotationSubject.getIdentifier(), refString);
		SearchResponse<AGMPhenotypeAnnotation> annotationSearch = agmPhenotypeAnnotationDAO.findByField("uniqueId", primaryAnnotationUniqueId);
		if (annotationSearch == null || annotationSearch.getSingleResult() == null)
			throw new ObjectValidationException(dto, "Primary annotation not found for " + primaryAnnotationSubject.getIdentifier());
		AGMPhenotypeAnnotation primaryAnnotation = annotationSearch.getSingleResult();
		if (!idsAdded.contains(primaryAnnotation.getId()))
			throw new ObjectValidationException(dto, "Primary annotation not included in submitted file (" + primaryAnnotationUniqueId + ")");
		
		if (StringUtils.isBlank(dto.getObjectId())) {
			apaResponse.addErrorMessage("objectId", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			GenomicEntity inferredOrAssertedEntity = genomicEntityService.findByIdentifierString(dto.getObjectId());
			if (inferredOrAssertedEntity == null) {
				apaResponse.addErrorMessage("objectId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getObjectId() + ")");
			} else if (inferredOrAssertedEntity instanceof Gene) {
				if (dataProvider.hasInferredGenePhenotypeAnnotations) {
					primaryAnnotation.setInferredGene((Gene) inferredOrAssertedEntity);
				} else if (dataProvider.hasAssertedGenePhenotypeAnnotations) {
					List<Gene> assertedGenes = primaryAnnotation.getAssertedGenes();
					if (assertedGenes == null)
						assertedGenes = new ArrayList<>();
					assertedGenes.add((Gene) inferredOrAssertedEntity);
					primaryAnnotation.setAssertedGenes(assertedGenes);
				} else {
					apaResponse.addErrorMessage("objectId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getObjectId() + ")");
				}		
			} else if (inferredOrAssertedEntity instanceof Allele) {
				if (dataProvider.hasInferredAllelePhenotypeAnnotations) {
					primaryAnnotation.setInferredAllele((Allele) inferredOrAssertedEntity);
				} else if (dataProvider.hasAssertedAllelePhenotypeAnnotations) {
					primaryAnnotation.setAssertedAllele((Allele) inferredOrAssertedEntity);
				} else {
					apaResponse.addErrorMessage("objectId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getObjectId() + ")");
				}
			} else {
				apaResponse.addErrorMessage("objectId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getObjectId() + ")");
			}
		}
		
		if (apaResponse.hasErrors())
			throw new ObjectValidationException(dto, apaResponse.errorMessagesString());
		
		return primaryAnnotation;
	}
}
