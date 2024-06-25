package org.alliancegenome.curation_api.services.validation.dto.fms;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.GeneExpressionAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.ontology.MMOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.GeneExpressionFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.*;
import org.alliancegenome.curation_api.services.helpers.annotations.GeneExpressionAnnotationUniqueIdHelper;
import org.alliancegenome.curation_api.services.ontology.MmoTermService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

@RequestScoped
public class GeneExpressionAnnotationFmsDTOValidator {

	@Inject GeneService geneService;

	@Inject MmoTermService mmoTermService;

	@Inject ReferenceService referenceService;

	@Inject ResourceDescriptorService resourceDescriptorService;

	@Inject ResourceDescriptorPageService resourceDescriptorPageService;

	@Inject GeneExpressionAnnotationUniqueIdHelper geneExpressionAnnotationUniqueIdHelper;

	@Inject GeneExpressionAnnotationDAO geneExpressionAnnotationDAO;

	@Inject DataProviderService dataProviderService;

	@Inject VocabularyTermService vocabularyTermService;

	public GeneExpressionAnnotation validateAnnotation(GeneExpressionFmsDTO geneExpressionFmsDTO, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		ObjectResponse<GeneExpressionAnnotation> response = new ObjectResponse<GeneExpressionAnnotation>();
		GeneExpressionAnnotation geneExpressionAnnotation;

		ObjectResponse<Reference> singleReferenceResponse = validateEvidence(geneExpressionFmsDTO);
		if (singleReferenceResponse.hasErrors()) {
			response.addErrorMessages("singleReference", singleReferenceResponse.getErrorMessages());
			throw new ObjectValidationException(geneExpressionFmsDTO, response.errorMessagesString());
		} else {
			String curie = singleReferenceResponse.getEntity().getCurie();
			String uniqueId = geneExpressionAnnotationUniqueIdHelper.generateUniqueId(geneExpressionFmsDTO, curie);
			SearchResponse<GeneExpressionAnnotation> annotationDB = geneExpressionAnnotationDAO.findByField("uniqueId", uniqueId);
			if (annotationDB != null && annotationDB.getSingleResult() != null) {
				geneExpressionAnnotation = annotationDB.getSingleResult();
			} else {
				geneExpressionAnnotation = new GeneExpressionAnnotation();
				geneExpressionAnnotation.setUniqueId(uniqueId);
			}
			geneExpressionAnnotation.setSingleReference(singleReferenceResponse.getEntity());
		}

		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getGeneId())) {
			response.addErrorMessage("geneId - ", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getGeneId() + ")");
		} else {
			Gene expressionAnnotationSubject = geneService.findByIdentifierString(geneExpressionFmsDTO.getGeneId());
			if (expressionAnnotationSubject == null) {
				response.addErrorMessage("geneId - ", ValidationConstants.INVALID_MESSAGE + " (" + geneExpressionFmsDTO.getGeneId() + ")");
			} else {
				geneExpressionAnnotation.setExpressionAnnotationSubject(expressionAnnotationSubject);
			}
		}

		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getDateAssigned())) {
			response.addErrorMessage("dateAssigned - ", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getDateAssigned() + ")");
		} else {
			OffsetDateTime creationDate = null;
			try {
				creationDate = OffsetDateTime.parse(geneExpressionFmsDTO.getDateAssigned());
			} catch (DateTimeParseException e) {
				response.addErrorMessage("dateAssigned", ValidationConstants.INVALID_MESSAGE + " (" + geneExpressionFmsDTO.getDateAssigned() + ")");
			}
			geneExpressionAnnotation.setDateCreated(creationDate);
		}

		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getAssay())) {
			response.addErrorMessage("assay - ", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getAssay() + ")");
		} else {
			MMOTerm expressionAssayUsed = mmoTermService.findByCurie(geneExpressionFmsDTO.getAssay());
			if (expressionAssayUsed == null) {
				response.addErrorMessage("assay - ", ValidationConstants.INVALID_MESSAGE + " (" + geneExpressionFmsDTO.getAssay() + ")");
			} else {
				geneExpressionAnnotation.setExpressionAssayUsed(expressionAssayUsed);
			}
		}

		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getWhereExpressed())) {
			response.addErrorMessage("whereExpressed - ", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getWhereExpressed() + ")");
		} else {
			String whereExpressedStatement = geneExpressionFmsDTO.getWhereExpressed().getWhereExpressedStatement();
			if (ObjectUtils.isEmpty(whereExpressedStatement)) {
				response.addErrorMessage("whereExpressed - whereExpressedStatement", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getWhereExpressed().getWhereExpressedStatement() + ")");
			} else {
				geneExpressionAnnotation.setWhereExpressedStatement(whereExpressedStatement);
			}
		}

		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getWhenExpressed())) {
			response.addErrorMessage("whenExpressed - ", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getWhenExpressed() + ")");
		} else {
			String stageName = geneExpressionFmsDTO.getWhenExpressed().getStageName();
			if (ObjectUtils.isEmpty(stageName)) {
				response.addErrorMessage("whenExpressed - whenExpressedStageName", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getWhenExpressed().getStageName() + ")");
			} else {
				geneExpressionAnnotation.setWhenExpressedStageName(stageName);
			}
		}

		geneExpressionAnnotation.setDataProvider(dataProviderService.createOrganizationDataProvider(dataProvider.sourceOrganization));
		geneExpressionAnnotation.setRelation(vocabularyTermService.getTermInVocabulary(VocabularyConstants.GENE_EXPRESSION_VOCABULARY, VocabularyConstants.GENE_EXPRESSION_RELATION_TERM).getEntity());
		geneExpressionAnnotation.setObsolete(false);
		geneExpressionAnnotation.setInternal(false);

		if (response.hasErrors()) {
			throw new ObjectValidationException(geneExpressionFmsDTO, response.errorMessagesString());
		}
		return geneExpressionAnnotation;
	}


	private ObjectResponse<Reference> validateEvidence(GeneExpressionFmsDTO geneExpressionFmsDTO) throws ObjectUpdateException {
		ObjectResponse<Reference> response = new ObjectResponse<>();

		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getEvidence())) {
			response.addErrorMessage("evidence - ", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getEvidence() + ")");
		}
		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getEvidence().getCrossReference())) {
			response.addErrorMessage("evidence - crossreference", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getEvidence().getCrossReference() + ")");
		}
		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getEvidence().getCrossReference().getId())) {
			response.addErrorMessage("evidence - crossreference - id", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getEvidence().getCrossReference() + ")");
		} else {
			String[] splittedId = geneExpressionFmsDTO.getEvidence().getCrossReference().getId().split(":");
			if (splittedId.length != 2) {
				response.addErrorMessage("evidence - crossreference - id - prefix", ValidationConstants.INVALID_MESSAGE + " (" + geneExpressionFmsDTO.getEvidence().getCrossReference().getId() + ")");
			} else {
				String prefix = splittedId[0];
				if (resourceDescriptorService.getByPrefixOrSynonym(prefix) == null) {
					response.addErrorMessage("evidence - crossreference - id - prefix", ValidationConstants.INVALID_MESSAGE + " (" + prefix + ")");
				}
				if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getEvidence().getCrossReference().getPages())) {
					response.addErrorMessage("evidence - crossreference - pages", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getEvidence().getCrossReference().getPages() + ")");
				} else {
					for (String page : geneExpressionFmsDTO.getEvidence().getCrossReference().getPages()) {
						if (resourceDescriptorPageService.getPageForResourceDescriptor(prefix, page) == null) {
							response.addErrorMessage("evidence - crossreference - pages - page_area", ValidationConstants.INVALID_MESSAGE + " (" + page + ")");
						}
					}
				}
			}
		}
		if (StringUtils.isEmpty(geneExpressionFmsDTO.getEvidence().getPublicationId()) || StringUtils.isBlank(geneExpressionFmsDTO.getEvidence().getPublicationId())) {
			response.addErrorMessage("evidence - publicationId", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getEvidence().getPublicationId() + ")");
		} else {
			Reference reference = referenceService.retrieveFromDbOrLiteratureService(geneExpressionFmsDTO.getEvidence().getPublicationId());
			if (reference == null) {
				response.addErrorMessage("evidence - publicationId", ValidationConstants.INVALID_MESSAGE + " (" + geneExpressionFmsDTO.getEvidence().getPublicationId() + ")");
			} else {
				response.setEntity(reference);
			}
		}
		return response;
	}
}
