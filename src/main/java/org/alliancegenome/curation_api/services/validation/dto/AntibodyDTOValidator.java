package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AntibodyDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Antibody;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.dto.AntibodyDTO;
import org.alliancegenome.curation_api.model.ingest.dto.CrossReferenceDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.helpers.AntibodyUniqueIdHelper;
import org.alliancegenome.curation_api.services.helpers.UniqueIdentifierHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AntibodyDTOValidator extends ReagentDTOValidator<Antibody, AntibodyDTO> {

	@Inject
	AntibodyDAO antibodyDAO;
	@Inject
	GeneService geneService;
	@Inject
	ReferenceService referenceService;
	@Inject
	CrossReferenceDTOValidator crossReferenceDtoValidator;
	@Inject
	CrossReferenceService crossReferenceService;

	@Transactional
	public ObjectResponse<Antibody> validateAntibodyDTO(AntibodyDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {

		response = new ObjectResponse<Antibody>();

		Antibody antibody = new Antibody();

		String uniqueId = AntibodyUniqueIdHelper.getAntibodyUniqueId(dto);
		String antibodyId = UniqueIdentifierHelper.setSubmittedObjectIdentifiers(dto, antibody, uniqueId);
		String identifyingField = UniqueIdentifierHelper.getIdentifyingField(dto);

		boolean existing = false;

		SearchResponse<Antibody> resp = antibodyDAO.findByField(identifyingField, antibodyId);
		if (resp != null) {
			Antibody dbAntibody = resp.getSingleResult();
			if (dbAntibody != null) {
				antibody = dbAntibody;
				existing = true;
			}
		}

		antibody.setUniqueId(uniqueId);
		UniqueIdentifierHelper.setObsoleteAndInternal(dto, antibody);

		antibody = validateReagentDTO(antibody, dto, VocabularyConstants.ANTIBODY_NOTE_TYPES_VOCABULARY_TERM_SET);

		if (StringUtils.isBlank(dto.getName())) {
			response.addErrorMessage("name", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			antibody.setName(dto.getName());
		}

		VocabularyTerm clonality = validateRequiredTermInVocabulary("clonality_name", dto.getClonalityName(), VocabularyConstants.ANTIBODY_CLONALITY_VOCABULARY);
		antibody.setClonality(clonality);

		VocabularyTerm heavyChainIsotype = validateTermInVocabulary("heavy_chain_isotype_name", dto.getHeavyChainIsotypeName(), VocabularyConstants.ANTIBODY_HEAVY_CHAIN_ISOTYPE_VOCABULARY);
		antibody.setHeavyChainIsotype(heavyChainIsotype);

		VocabularyTerm lightChainIsotype = validateTermInVocabulary("light_chain_isotype_name", dto.getLightChainIsotypeName(), VocabularyConstants.ANTIBODY_LIGHT_CHAIN_ISOTYPE_VOCABULARY);
		antibody.setLightChainIsotype(lightChainIsotype);

		NCBITaxonTerm antigenTaxon = validateTaxon("antigen_taxon_curie", dto.getAntigenTaxonCurie());
		antibody.setAntigenTaxon(antigenTaxon);

		NCBITaxonTerm taxon = validateTaxon("taxon_curie", dto.getTaxonCurie());
		antibody.setTaxon(taxon);

		List<Gene> targetGenes = validateOptionalEntities("antibody_target_gene_identifiers", dto.getAntibodyTargetGeneIdentifiers(), geneService::findByIdentifierString);
		antibody.setAntibodyTargetGenes(targetGenes);

		List<Reference> refs = validateOptionalEntities("reference_curies", dto.getReferenceCuries(), referenceService::retrieveFromDbOrLiteratureService);
		antibody.setReferences(refs);

		if (StringUtils.isNotBlank(dto.getOriginalReferenceCurie())) {
			Reference originalReference = referenceService.retrieveFromDbOrLiteratureService(dto.getOriginalReferenceCurie());
			if (originalReference == null) {
				response.addWarningMessage("original_reference_curie", ValidationConstants.WARNING_MISSING_MESSAGE + " (" + dto.getOriginalReferenceCurie() + ")");
			}
			antibody.setOriginalReference(originalReference);
		} else {
			antibody.setOriginalReference(null);
		}

		antibody.setCrossReferences(validateCrossReferences(dto, antibody));

		response.convertWarningMessagesToMap();
		response.convertErrorMessagesToMap();

		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}

		if (!existing) {
			antibody = antibodyDAO.persist(antibody);
		}

		response.setEntity(antibody);

		return response;
	}

	private List<CrossReference> validateCrossReferences(AntibodyDTO dto, Antibody antibody) {
		List<CrossReference> validatedXrefs = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getCrossReferenceDtos())) {
			for (CrossReferenceDTO xrefDto : dto.getCrossReferenceDtos()) {
				ObjectResponse<CrossReference> xrefResponse = crossReferenceDtoValidator.validateCrossReferenceDTO(xrefDto, null);
				if (xrefResponse.hasErrors()) {
					response.addErrorMessage("cross_reference_dtos", xrefResponse.errorMessagesString());
					break;
				} else {
					validatedXrefs.add(xrefResponse.getEntity());
				}
			}
		}

		return crossReferenceService.getUpdatedXrefList(validatedXrefs, antibody.getCrossReferences());
	}
}
