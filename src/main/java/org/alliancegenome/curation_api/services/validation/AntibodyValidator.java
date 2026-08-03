package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AntibodyDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Antibody;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.AntibodyUniqueIdHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AntibodyValidator extends ReagentValidator {

	@Inject AntibodyDAO antibodyDAO;
	@Inject GeneDAO geneDAO;
	@Inject ReferenceValidator referenceValidator;
	@Inject CrossReferenceValidator crossReferenceValidator;

	private String errorMessage;

	public Antibody validateAntibodyUpdate(Antibody uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update Antibody: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No Antibody ID provided");
			throw new ApiErrorException(response);
		}
		Antibody dbEntity = antibodyDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find Antibody with ID: [" + id + "]");
			throw new ApiErrorException(response);
		}

		return validateAntibody(uiEntity, dbEntity);
	}

	public Antibody validateAntibodyCreate(Antibody uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create Antibody";

		Antibody dbEntity = new Antibody();

		return validateAntibody(uiEntity, dbEntity);
	}

	public Antibody validateAntibody(Antibody uiEntity, Antibody dbEntity) {

		dbEntity = (Antibody) validateCommonReagentFields(uiEntity, dbEntity, VocabularyConstants.ANTIBODY_NOTE_TYPES_VOCABULARY_TERM_SET);

		if (StringUtils.isBlank(uiEntity.getName())) {
			addMessageResponse("name", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			dbEntity.setName(uiEntity.getName());
		}

		VocabularyTerm clonality = validateRequiredTermInVocabulary("clonality", VocabularyConstants.ANTIBODY_CLONALITY_VOCABULARY, uiEntity.getClonality(), dbEntity.getClonality());
		dbEntity.setClonality(clonality);

		VocabularyTerm heavyChainIsotype = validateTermInVocabulary("heavyChainIsotype", VocabularyConstants.ANTIBODY_HEAVY_CHAIN_ISOTYPE_VOCABULARY, uiEntity.getHeavyChainIsotype(), dbEntity.getHeavyChainIsotype());
		dbEntity.setHeavyChainIsotype(heavyChainIsotype);

		VocabularyTerm lightChainIsotype = validateTermInVocabulary("lightChainIsotype", VocabularyConstants.ANTIBODY_LIGHT_CHAIN_ISOTYPE_VOCABULARY, uiEntity.getLightChainIsotype(), dbEntity.getLightChainIsotype());
		dbEntity.setLightChainIsotype(lightChainIsotype);

		NCBITaxonTerm antigenTaxon = validateTaxon(uiEntity.getAntigenTaxon(), dbEntity.getAntigenTaxon(), "antigenTaxon");
		dbEntity.setAntigenTaxon(antigenTaxon);

		NCBITaxonTerm taxon = validateTaxon(uiEntity.getTaxon(), dbEntity.getTaxon(), "taxon");
		dbEntity.setTaxon(taxon);

		List<Gene> targetGenes = validateEntities(geneDAO, "antibodyTargetGenes", uiEntity.getAntibodyTargetGenes(), dbEntity.getAntibodyTargetGenes());
		dbEntity.setAntibodyTargetGenes(targetGenes);

		dbEntity.setReferences(validateReferences(uiEntity, dbEntity));
		dbEntity.setOriginalReference(validateOriginalReference(uiEntity, dbEntity));
		dbEntity.setCrossReferences(validateCrossReferences(uiEntity, dbEntity));

		String uniqueId = validateUniqueId(uiEntity, dbEntity);
		dbEntity.setUniqueId(uniqueId);

		response.convertErrorMessagesToMap();

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		dbEntity = antibodyDAO.persist(dbEntity);

		return dbEntity;
	}

	public String validateUniqueId(Antibody uiEntity, Antibody dbEntity) {

		if (dbEntity.getDataProvider() == null) {
			return null;
		}

		String uniqueId = AntibodyUniqueIdHelper.getAntibodyUniqueId(uiEntity);

		if (dbEntity.getUniqueId() == null || !uniqueId.equals(dbEntity.getUniqueId())) {
			SearchResponse<Antibody> dbResponse = antibodyDAO.findByField("uniqueId", uniqueId);
			if (dbResponse != null) {
				addMessageResponse("uniqueId", ValidationConstants.NON_UNIQUE_MESSAGE);
				return null;
			}
		}

		return uniqueId;
	}

	private List<Reference> validateReferences(Antibody uiEntity, Antibody dbEntity) {
		List<String> previousReferenceCuries = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(dbEntity.getReferences())) {
			previousReferenceCuries = dbEntity.getReferences().stream().map(Reference::getCurie).collect(Collectors.toList());
		}
		if (CollectionUtils.isEmpty(uiEntity.getReferences())) {
			return null;
		}
		List<Reference> references = new ArrayList<Reference>();
		for (Reference uiReference : uiEntity.getReferences()) {
			Reference reference = validateReference("references", uiReference, previousReferenceCuries);
			if (reference != null) {
				references.add(reference);
			}
		}
		return references;
	}

	private Reference validateOriginalReference(Antibody uiEntity, Antibody dbEntity) {
		if (uiEntity.getOriginalReference() == null) {
			return null;
		}
		List<String> previousCuries = new ArrayList<String>();
		if (dbEntity.getOriginalReference() != null) {
			previousCuries.add(dbEntity.getOriginalReference().getCurie());
		}
		return validateReference("originalReference", uiEntity.getOriginalReference(), previousCuries);
	}

	private Reference validateReference(String field, Reference uiEntity, List<String> previousCuries) {
		ObjectResponse<Reference> singleRefResponse = referenceValidator.validateReference(uiEntity);
		if (singleRefResponse.getEntity() == null) {
			addMessageResponse(field, singleRefResponse.errorMessagesString());
			return null;
		}

		if (singleRefResponse.getEntity().getObsolete() && (CollectionUtils.isEmpty(previousCuries) || !previousCuries.contains(singleRefResponse.getEntity().getCurie()))) {
			addMessageResponse(field, "curie - " + ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return singleRefResponse.getEntity();
	}

	private List<CrossReference> validateCrossReferences(Antibody uiEntity, Antibody dbEntity) {
		String field = "crossReferences";

		List<CrossReference> validatedXrefs = new ArrayList<CrossReference>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getCrossReferences())) {
			for (int ix = 0; ix < uiEntity.getCrossReferences().size(); ix++) {
				CrossReference xref = uiEntity.getCrossReferences().get(ix);
				ObjectResponse<CrossReference> xrefResponse = crossReferenceValidator.validateCrossReference(xref, false);
				if (xrefResponse.hasErrors()) {
					allValid = false;
					response.addErrorMessages(field, ix, xrefResponse.getErrorMessages());
				} else {
					validatedXrefs.add(xrefResponse.getEntity());
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedXrefs)) {
			return null;
		}

		return validatedXrefs;
	}
}
