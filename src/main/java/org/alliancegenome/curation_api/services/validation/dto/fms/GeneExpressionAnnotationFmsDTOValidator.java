package org.alliancegenome.curation_api.services.validation.dto.fms;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.ingest.dto.fms.GeneExpressionFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;
import org.alliancegenome.curation_api.services.ResourceDescriptorService;
import org.alliancegenome.curation_api.services.ontology.MmoTermService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

@RequestScoped
public class GeneExpressionAnnotationFmsDTOValidator {

	@Inject	GeneService geneService;

	@Inject	MmoTermService mmoTermService;

	@Inject ReferenceService referenceService;

	@Inject	ResourceDescriptorService resourceDescriptorService;

	@Inject
	ResourceDescriptorPageService resourceDescriptorPageService;

	public boolean isExpressionAnnotationValid(GeneExpressionFmsDTO geneExpressionFmsDTO) throws ObjectUpdateException {
		ObjectResponse<GeneExpressionAnnotation> response = new ObjectResponse<GeneExpressionAnnotation>();

		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getGeneId())) {
			response.addErrorMessage("geneId - ", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getGeneId() + ")");
		}
		if (geneService.findByIdentifierString(geneExpressionFmsDTO.getGeneId()) == null) {
			response.addErrorMessage("geneId - ", ValidationConstants.INVALID_MESSAGE + " (" + geneExpressionFmsDTO.getGeneId() + ")");
		}
		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getDateAssigned())) {
			response.addErrorMessage("dateAssigned - ", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getDateAssigned() + ")");
		}
		try {
			OffsetDateTime.parse(geneExpressionFmsDTO.getDateAssigned());
		} catch (DateTimeParseException e) {
			response.addErrorMessage("dateAssigned", ValidationConstants.INVALID_MESSAGE + " (" + geneExpressionFmsDTO.getDateAssigned() + ")");
		}
		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getAssay())) {
			response.addErrorMessage("assay - ",  ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getAssay() + ")");
		}
		if (mmoTermService.findByCurie(geneExpressionFmsDTO.getAssay()) == null) {
			response.addErrorMessage("assay - ", ValidationConstants.INVALID_MESSAGE + " (" + geneExpressionFmsDTO.getAssay() + ")");
		}
		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getEvidence())) {
			response.addErrorMessage("evidence - ", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getEvidence() + ")");
		}
		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getEvidence().getCrossReference())) {
			response.addErrorMessage("evidence - crossreference", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getEvidence().getCrossReference() + ")");
		}
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
		if (StringUtils.isEmpty(geneExpressionFmsDTO.getEvidence().getPublicationId()) || StringUtils.isBlank(geneExpressionFmsDTO.getEvidence().getPublicationId())) {
			response.addErrorMessage("evidence - publicationId", ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getEvidence().getPublicationId() + ")");
		}
		if (referenceService.retrieveFromDbOrLiteratureService(geneExpressionFmsDTO.getEvidence().getPublicationId()) == null) {
			response.addErrorMessage("evidence - publicationId", ValidationConstants.INVALID_MESSAGE + " (" + geneExpressionFmsDTO.getEvidence().getPublicationId() + ")");
		}
		if (response.hasErrors()) {
			throw new ObjectValidationException(geneExpressionFmsDTO, response.errorMessagesString());
		}
		return true;
	}
}
