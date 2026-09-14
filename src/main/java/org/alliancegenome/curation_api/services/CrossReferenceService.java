package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.ingest.dto.fms.CrossReferenceFmsDTO;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.CrossReferenceValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class CrossReferenceService extends BaseEntityCrudService<CrossReference, CrossReferenceDAO> {
	HashMap<String, CrossReference> crossReferenceCache = new HashMap<>();

	@Inject
	CrossReferenceDAO crossReferenceDAO;
	@Inject
	ResourceDescriptorPageService resourceDescriptorPageService;
	@Inject
	CrossReferenceValidator crossReferenceValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(crossReferenceDAO);
	}

	// Validates without writing the cross reference, and stays non-@Transactional like the other validate
	// endpoints. Auditing still reaches PersonService, which commits a Person of its own when the payload
	// names one that is not stored yet.
	public ObjectResponse<CrossReference> validate(CrossReference uiEntity) {
		return crossReferenceValidator.validateCrossReference(uiEntity, true, false);
	}

	/**
	 * Replaces the owner's cross references with the given list, deleting any that are no longer present.
	 * An empty list is a valid payload and clears them all; a null list is rejected, so a malformed request
	 * cannot be read as a deletion.
	 *
	 * <p>An entry carrying an id must already belong to this owner. Cross reference rows are addressed by a
	 * join table shared with references, ontology terms and vocabulary terms, and nothing on the row records
	 * its owner, so an unchecked id would re-parent another entity's row onto this one and leave the previous
	 * owner's next save to orphan-delete it.
	 */
	@Transactional
	public ObjectListResponse<CrossReference> replaceForOwner(GenomicEntity owner, List<CrossReference> incomingXrefs) {
		ObjectResponse<List<CrossReference>> validationResponse = new ObjectResponse<>();
		String errorTitle = "Could not update CrossReferences for: [" + owner.getId() + "]";

		if (incomingXrefs == null) {
			validationResponse.addErrorMessage("crossReferences", ValidationConstants.REQUIRED_MESSAGE);
			validationResponse.setErrorMessage(errorTitle);
			throw new ApiErrorException(validationResponse);
		}

		if (owner.getCrossReferences() == null) {
			owner.setCrossReferences(new ArrayList<>());
		}

		Set<Long> ownedIds = owner.getCrossReferences().stream().map(CrossReference::getId).collect(Collectors.toSet());
		boolean allOwned = true;
		for (int ix = 0; ix < incomingXrefs.size(); ix++) {
			Long incomingId = incomingXrefs.get(ix).getId();
			if (incomingId != null && !ownedIds.contains(incomingId)) {
				allOwned = false;
				validationResponse.addErrorMessages("crossReferences", ix, Map.of("id", ValidationConstants.INVALID_MESSAGE));
			}
		}
		if (!allOwned) {
			validationResponse.convertMapToErrorMessages("crossReferences");
			validationResponse.setErrorMessage(errorTitle);
			throw new ApiErrorException(validationResponse);
		}

		List<CrossReference> validatedXrefs = crossReferenceValidator.validateCrossReferences(incomingXrefs, "crossReferences", validationResponse);
		if (validatedXrefs == null) {
			validationResponse.setErrorMessage(errorTitle);
			throw new ApiErrorException(validationResponse);
		}

		owner.getCrossReferences().clear();
		owner.getCrossReferences().addAll(validatedXrefs);

		return new ObjectListResponse<>(new ArrayList<>(owner.getCrossReferences()));
	}

	public List<CrossReference> getMergedFmsXrefList(List<CrossReferenceFmsDTO> fmsCrossReferences, List<CrossReference> existingXrefs) {
		List<CrossReference> incomingXrefs = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(fmsCrossReferences)) {
			for (CrossReferenceFmsDTO fmsXref : fmsCrossReferences) {
				String xrefCurie = fmsXref.getCurie();
				if (CollectionUtils.isEmpty(fmsXref.getPages())) {
					fmsXref.getPages().add("default");
				}
				for (String xrefPage : fmsXref.getPages()) {
					CrossReference xref = new CrossReference();
					xref.setReferencedCurie(xrefCurie);
					xref.setDisplayName(xrefCurie);
					String prefix = xrefCurie.indexOf(":") == -1 ? xrefCurie : xrefCurie.substring(0, xrefCurie.indexOf(":"));
					ResourceDescriptorPage rdp = resourceDescriptorPageService.getPageForResourceDescriptor(prefix, xrefPage);
					if (rdp != null) {
						xref.setResourceDescriptorPage(rdp);
						incomingXrefs.add(xref);
					}
				}
			}
		}

		return getUpdatedXrefList(incomingXrefs, existingXrefs);
	}

	@Transactional
	public List<CrossReference> getUpdatedXrefList(List<CrossReference> incomingXrefs, List<CrossReference> existingXrefs) {
		return getUpdatedXrefList(incomingXrefs, existingXrefs, false);
	}
	
	@Transactional
	public List<CrossReference> getUpdatedXrefList(List<CrossReference> incomingXrefs, List<CrossReference> existingXrefs, Boolean keepAllExisting) {
		Map<String, CrossReference> existingXrefMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(existingXrefs)) {
			for (CrossReference existingXref : existingXrefs) {
				existingXrefMap.put(getCrossReferenceUniqueId(existingXref), existingXref);
			}
		}

		List<CrossReference> finalXrefs = new ArrayList<>();
		List<String> addedXrefUniqueIds = new ArrayList<>();
		
		List<CrossReference> combinedXrefs = new ArrayList<>();
		combinedXrefs.addAll(incomingXrefs);
		if (keepAllExisting && CollectionUtils.isNotEmpty(existingXrefs)) {
			combinedXrefs.addAll(existingXrefs);
		}
		
		if (CollectionUtils.isNotEmpty(combinedXrefs)) {
			for (CrossReference xref : combinedXrefs) {
				String incomingXrefUniqueId = getCrossReferenceUniqueId(xref);
				if (!addedXrefUniqueIds.contains(incomingXrefUniqueId)) {
					if (existingXrefMap.containsKey(incomingXrefUniqueId)) {
						finalXrefs.add(updateCrossReference(existingXrefMap.get(incomingXrefUniqueId), xref));
					} else {
						finalXrefs.add(crossReferenceDAO.persist(xref));
					}
					addedXrefUniqueIds.add(incomingXrefUniqueId);
				}
			}
		}

		return finalXrefs;
	}

	public CrossReference updateCrossReference(CrossReference oldXref, CrossReference newXref) {
		oldXref.setDisplayName(newXref.getDisplayName());
		oldXref.setCreatedBy(newXref.getCreatedBy());
		oldXref.setUpdatedBy(newXref.getUpdatedBy());
		oldXref.setDateCreated(newXref.getDateCreated());
		oldXref.setDateUpdated(newXref.getDateUpdated());
		oldXref.setInternal(newXref.getInternal());
		oldXref.setObsolete(newXref.getObsolete());

		return oldXref;
	}

	public String getCrossReferenceUniqueId(CrossReference xref) {
		if (xref == null) {
			return null;
		}
		if (xref.getResourceDescriptorPage() == null) {
			return xref.getReferencedCurie();
		}
		return StringUtils.join(List.of(xref.getReferencedCurie(), xref.getResourceDescriptorPage().getId()), "|");
	}
}
