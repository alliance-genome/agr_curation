package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.ResourceDescriptorPageDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.base.AuditedObjectValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CrossReferenceValidator extends AuditedObjectValidator<CrossReference> {

	@Inject CrossReferenceDAO crossReferenceDAO;
	@Inject ResourceDescriptorPageDAO resourceDescriptorPageDAO;

	// Each descriptor idPattern compiled once, keyed by its text. An empty entry marks one that does not
	// compile, so it is reported once rather than on every cross reference checked against it.
	private static final Map<String, Optional<Pattern>> COMPILED_ID_PATTERNS = new ConcurrentHashMap<>();

	public ObjectResponse<CrossReference> validateCrossReference(CrossReference uiEntity, Boolean throwError) {
		return validateCrossReference(uiEntity, throwError, true);
	}

	public List<CrossReference> validateCrossReferences(List<CrossReference> uiXrefs, String fieldName, ObjectResponse<?> targetResponse) {
		return validateCrossReferences(uiXrefs, fieldName, targetResponse, false);
	}

	/**
	 * Validates each cross reference in turn, recording an entry's errors against targetResponse under
	 * fieldName and that entry's index.
	 *
	 * @param requireCompleteCrossReference see {@link #validateCrossReference(CrossReference, Boolean, Boolean, boolean)}
	 * @return the validated cross references, or null when any entry failed
	 */
	public List<CrossReference> validateCrossReferences(List<CrossReference> uiXrefs, String fieldName, ObjectResponse<?> targetResponse, boolean requireCompleteCrossReference) {
		List<CrossReference> validatedXrefs = new ArrayList<>();
		boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiXrefs)) {
			for (int ix = 0; ix < uiXrefs.size(); ix++) {
				ObjectResponse<CrossReference> xrefResponse = validateCrossReference(uiXrefs.get(ix), false, true, requireCompleteCrossReference);
				if (xrefResponse.hasErrors()) {
					allValid = false;
					targetResponse.addErrorMessages(fieldName, ix, xrefResponse.getErrorMessages());
				} else {
					validatedXrefs.add(xrefResponse.getEntity());
				}
			}
		}

		if (!allValid) {
			targetResponse.convertMapToErrorMessages(fieldName);
			return null;
		}

		return validatedXrefs;
	}

	public ObjectResponse<CrossReference> validateCrossReference(CrossReference uiEntity, Boolean throwError, Boolean persist) {
		return validateCrossReference(uiEntity, throwError, persist, false);
	}

	/**
	 * @param persist whether to write the validated cross reference. Pass false to check a cross reference
	 *        without storing it. A payload with no id is then returned unmanaged and without one; a payload
	 *        carrying an id returns the managed row it names, with the payload's values applied but not
	 *        written, so the caller must not run inside a transaction that would flush them.
	 * @param requireCompleteCrossReference whether the cross reference must have a display name and name a
	 *        page, and its curie match that page's resource descriptor: its idPattern, or for a descriptor
	 *        without a pattern that compiles, its prefix followed by an identifier. Off for the entity types
	 *        whose stored cross references do not all meet it, so that saving an unrelated field on one of
	 *        them is not refused. Without it, a display name is required only when there is no curie.
	 */
	public ObjectResponse<CrossReference> validateCrossReference(CrossReference uiEntity, Boolean throwError, Boolean persist, boolean requireCompleteCrossReference) {
		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update CrossReference: [" + uiEntity.getReferencedCurie() + "]";

		CrossReference dbEntity;

		Boolean newEntity = true;
		if (uiEntity.getId() != null) {
			dbEntity = crossReferenceDAO.find(uiEntity.getId());
			if (dbEntity == null) {
				addMessageResponse("id", ValidationConstants.INVALID_MESSAGE);
				if (throwError) {
					response.setErrorMessage(errorTitle);
					throw new ApiErrorException(response);
				}
				return response;
			}
			newEntity = false;
		} else {
			dbEntity = new CrossReference();
		}

		dbEntity = (CrossReference) validateAuditedObjectFields(uiEntity, dbEntity, newEntity);

		if (StringUtils.isEmpty(uiEntity.getReferencedCurie())) {
			addMessageResponse("referencedCurie", ValidationConstants.REQUIRED_MESSAGE);
		}
		dbEntity.setReferencedCurie(uiEntity.getReferencedCurie());

		if (StringUtils.isEmpty(uiEntity.getDisplayName())) {
			if (requireCompleteCrossReference || StringUtils.isEmpty(uiEntity.getReferencedCurie())) {
				addMessageResponse("displayName", ValidationConstants.REQUIRED_MESSAGE);
			}
		}
		dbEntity.setDisplayName(uiEntity.getDisplayName());

		// Applied whether or not the payload names one, as the other fields are, so a cleared page is
		// stored as cleared. Resolved rather than taken from the payload when it does name one: the
		// association has no cascade, so an unresolved page would only fail once the transaction flushed,
		// past the point an error response can be built.
		ResourceDescriptorPage resourceDescriptorPage = null;
		if (uiEntity.getResourceDescriptorPage() == null) {
			dbEntity.setResourceDescriptorPage(null);
			if (requireCompleteCrossReference) {
				addMessageResponse("resourceDescriptorPage", ValidationConstants.REQUIRED_MESSAGE);
			}
		} else {
			resourceDescriptorPage = validateEntity(resourceDescriptorPageDAO, "resourceDescriptorPage",
				uiEntity.getResourceDescriptorPage(), dbEntity.getResourceDescriptorPage(), false);
			if (resourceDescriptorPage != null) {
				dbEntity.setResourceDescriptorPage(resourceDescriptorPage);
			}
		}

		if (requireCompleteCrossReference) {
			// A page that resolved without a descriptor names no resource for the curie to belong to.
			if (resourceDescriptorPage != null && resourceDescriptorPage.getResourceDescriptor() == null) {
				addMessageResponse("resourceDescriptorPage", ValidationConstants.INVALID_MESSAGE);
			}
			if (resourceDescriptorPage != null) {
				validateReferencedCurieAgainstDescriptor(uiEntity.getReferencedCurie(), resourceDescriptorPage.getResourceDescriptor());
			}
		}

		if (response.hasErrors()) {
			if (throwError) {
				response.setErrorMessage(errorTitle);
				throw new ApiErrorException(response);
			}
			return response;
		}

		response.setEntity(persist ? crossReferenceDAO.persist(dbEntity) : dbEntity);

		return response;
	}

	// The page builds the curie's link, so the curie has to belong to the resource the page's descriptor
	// names. A missing curie, page or descriptor has already been reported, so there is nothing to compare.
	private void validateReferencedCurieAgainstDescriptor(String referencedCurie, ResourceDescriptor resourceDescriptor) {
		if (StringUtils.isEmpty(referencedCurie) || resourceDescriptor == null) {
			return;
		}

		Pattern idPattern = compiledIdPattern(resourceDescriptor);
		boolean matchesDescriptor = idPattern != null
			? idPattern.matcher(referencedCurie).matches()
			: carriesPrefix(referencedCurie, resourceDescriptor.getPrefix());

		if (!matchesDescriptor) {
			addMessageResponse("referencedCurie", ValidationConstants.CURIE_PATTERN_MISMATCH_MESSAGE);
		}
	}

	// The descriptor's idPattern, or null when it has none or it does not compile.
	private static Pattern compiledIdPattern(ResourceDescriptor resourceDescriptor) {
		String idPattern = resourceDescriptor.getIdPattern();
		if (StringUtils.isBlank(idPattern)) {
			return null;
		}

		return COMPILED_ID_PATTERNS.computeIfAbsent(idPattern, patternText -> {
			try {
				return Optional.of(Pattern.compile(patternText));
			} catch (PatternSyntaxException error) {
				Log.warn("Resource descriptor " + resourceDescriptor.getPrefix() + " has an idPattern that does not compile, so its curies are checked by prefix: " + error.getDescription());
				return Optional.empty();
			}
		}).orElse(null);
	}

	// Whether the curie is the prefix, a colon and a non-blank identifier.
	private static boolean carriesPrefix(String referencedCurie, String prefix) {
		int separatorIndex = referencedCurie.indexOf(':');
		return separatorIndex > 0
			&& referencedCurie.substring(0, separatorIndex).equals(prefix)
			&& StringUtils.isNotBlank(referencedCurie.substring(separatorIndex + 1));
	}
}