package org.alliancegenome.curation_api.services.validation.dto;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.AnatomicalSite;
import org.alliancegenome.curation_api.model.ingest.dto.AnatomicalSiteDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ontology.AnatomicalTermService;
import org.alliancegenome.curation_api.services.ontology.GoTermService;
import org.alliancegenome.curation_api.services.ontology.OntologyTermService;
import org.alliancegenome.curation_api.services.ontology.UberonTermService;
import org.alliancegenome.curation_api.services.validation.dto.base.AuditedObjectDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/**
 * SCRUM-6715: direct-submission validator for AnatomicalSite, a cascade-owned sub-object (no
 * independent CRUD) currently only reachable from HTPExpressionDatasetSampleAnnotationDTO's
 * htp_expression_sample_locations. AnatomicalSite is also used by GeneExpressionAnnotation's own
 * where-expressed data, but that entity has no direct submission path to share this validator with
 * yet - nothing to reuse, the FMS validator's validateAnatomicalSite is a different DTO shape.
 */
@RequestScoped
public class AnatomicalSiteDTOValidator extends AuditedObjectDTOValidator<AnatomicalSite, AnatomicalSiteDTO> {

	@Inject AnatomicalTermService anatomicalTermService;
	@Inject GoTermService goTermService;
	@Inject OntologyTermService ontologyTermService;
	@Inject UberonTermService uberonTermService;

	public ObjectResponse<AnatomicalSite> validateAnatomicalSiteDTO(AnatomicalSiteDTO dto) {
		response = new ObjectResponse<>();

		AnatomicalSite anatomicalSite = new AnatomicalSite();

		anatomicalSite = validateAuditedObjectDTO(anatomicalSite, dto);

		if (StringUtils.isBlank(dto.getAnatomicalStructureCurie()) && StringUtils.isBlank(dto.getCellularComponentCurie())) {
			response.addErrorMessage("anatomical_structure_curie", ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + " cellular_component_curie");
		}

		anatomicalSite.setAnatomicalStructure(validateOntologyTerm(anatomicalTermService, "anatomical_structure_curie", dto.getAnatomicalStructureCurie()));
		anatomicalSite.setAnatomicalStructureQualifiers(validateOntologyTerms(ontologyTermService, "anatomical_structure_qualifier_curies", dto.getAnatomicalStructureQualifierCuries()));
		anatomicalSite.setAnatomicalStructureUberonTerms(validateOntologyTerms(uberonTermService, "anatomical_structure_uberon_term_curies", dto.getAnatomicalStructureUberonTermCuries()));
		// These three booleans are all "boolean DEFAULT false NOT NULL" columns - an explicit null
		// (as opposed to omitting the column) still violates that constraint, so coalesce rather than
		// pass the DTO value straight through.
		anatomicalSite.setAnatomicalStructureUberonTermOther(Boolean.TRUE.equals(dto.getAnatomicalStructureUberonTermOther()));

		anatomicalSite.setAnatomicalSubstructure(validateOntologyTerm(anatomicalTermService, "anatomical_substructure_curie", dto.getAnatomicalSubstructureCurie()));
		anatomicalSite.setAnatomicalSubstructureQualifiers(validateOntologyTerms(ontologyTermService, "anatomical_substructure_qualifier_curies", dto.getAnatomicalSubstructureQualifierCuries()));
		anatomicalSite.setAnatomicalSubstructureUberonTerms(validateOntologyTerms(uberonTermService, "anatomical_substructure_uberon_term_curies", dto.getAnatomicalSubstructureUberonTermCuries()));
		anatomicalSite.setAnatomicalSubStructureUberonTermOther(Boolean.TRUE.equals(dto.getAnatomicalSubStructureUberonTermOther()));

		anatomicalSite.setCellularComponentTerm(validateOntologyTerm(goTermService, "cellular_component_curie", dto.getCellularComponentCurie()));
		anatomicalSite.setCellularComponentRibbonTerms(validateOntologyTerms(goTermService, "cellular_component_ribbon_term_curies", dto.getCellularComponentRibbonTermCuries()));
		anatomicalSite.setCellularComponentQualifiers(validateOntologyTerms(ontologyTermService, "cellular_component_qualifier_curies", dto.getCellularComponentQualifierCuries()));
		anatomicalSite.setCellularComponentOther(Boolean.TRUE.equals(dto.getCellularComponentOther()));

		response.setEntity(anatomicalSite);
		return response;
	}

}
