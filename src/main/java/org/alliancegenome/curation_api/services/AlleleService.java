package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.base.BasePopularityInterface;
import org.alliancegenome.curation_api.model.document.es.AlleleSummaryDocument;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.validation.AlleleValidator;
import org.alliancegenome.curation_api.services.validation.dto.AlleleDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AlleleService extends SubmittedObjectCrudService<Allele, AlleleDTO, AlleleDAO> implements BasePopularityInterface {

	@Inject
	AlleleDAO alleleDAO;
	@Inject
	AlleleValidator alleleValidator;
	@Inject
	AlleleDTOValidator alleleDtoValidator;
	@Inject
	PersonService personService;
	@Inject
	NoteService noteService;
	@Inject
	CrossReferenceService crossReferenceService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<Allele> update(Allele uiEntity) {
		// AlleleView carries crossReferences but not the associations, so this path manages the former only.
		Allele dbEntity = alleleValidator.validateAlleleUpdate(uiEntity, false, true);
		return new ObjectResponse<>(dbEntity);
	}

	@Transactional
	public ObjectResponse<Allele> updateDetail(Allele uiEntity) {
		// AlleleDetailView carries the associations but not crossReferences, so this path manages the associations
		// only; cross references are written through the allele's cross-references sub-resource. Both flags are set
		// explicitly rather than derived from one another - they are complementary for these two views by
		// coincidence, not by rule.
		Allele dbEntity = alleleValidator.validateAlleleUpdate(uiEntity, true, false);
		return new ObjectResponse<>(dbEntity);
	}

	public ObjectListResponse<CrossReference> getCrossReferences(Long id) {
		Allele allele = findAlleleOrThrow(id);
		List<CrossReference> crossReferences = allele.getCrossReferences();
		return new ObjectListResponse<>(crossReferences == null ? new ArrayList<>() : new ArrayList<>(crossReferences));
	}

	@Transactional
	public ObjectListResponse<CrossReference> updateCrossReferences(Long id, List<CrossReference> crossReferences) {
		return crossReferenceService.replaceForOwner(findAlleleOrThrow(id), crossReferences);
	}

	private Allele findAlleleOrThrow(Long id) {
		Allele allele = alleleDAO.find(id);
		if (allele == null) {
			ObjectResponse<Allele> response = new ObjectResponse<>();
			response.addErrorMessage("id", ValidationConstants.INVALID_MESSAGE);
			throw new ApiErrorException(response);
		}
		return allele;
	}

	@Override
	@Transactional
	public ObjectResponse<Allele> create(Allele uiEntity) {
		Allele dbEntity = alleleValidator.validateAlleleCreate(uiEntity);
		return new ObjectResponse<>(dbEntity);
	}

	@Override
	public ObjectResponse<Allele> upsert(AlleleDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return alleleDtoValidator.validateAlleleDTO(dto, dataProvider);
	}

	@Override
	@Transactional
	public ObjectResponse<Allele> deleteById(Long id) {
		deprecateOrDelete(id, true, "Allele DELETE API call", false);
		ObjectResponse<Allele> ret = new ObjectResponse<>();
		return ret;
	}

	/**
	 * Hard-deletes an allele on a curator's request. The allele's own slot annotations, notes, cross references
	 * and gene, variant and construct associations go with it. Disease, phenotype and HTP sample annotations,
	 * AGM associations, constructs listing it as a component and genetic interactions that reference it block
	 * the deletion.
	 *
	 * @param identifierString curie, primary external ID or MOD internal ID of the allele
	 * @return response holding the deleted allele
	 * @throws ApiErrorException when no allele matches or the allele is still referenced
	 */
	@Override
	@Transactional
	public ObjectResponse<Allele> deleteByIdentifier(String identifierString) {
		Allele allele = findByIdentifierString(identifierString);
		if (allele == null) {
			ObjectResponse<Allele> response = new ObjectResponse<>();
			response.setErrorMessage("Could not find Allele with identifier: " + identifierString);
			throw new ApiErrorException(response);
		}

		List<String> referencingReasons = getReferencingAnnotationAndAgmReasons(allele.getId());
		if (CollectionUtils.isNotEmpty(allele.getConstructGenomicEntityAssociations())) {
			referencingReasons.add("Allele is a component of construct(s)");
		}
		if (alleleDAO.hasReferencingGeneGeneticInteractions(allele.getId())) {
			referencingReasons.add("Allele is referenced by genetic interaction(s)");
		}
		if (CollectionUtils.isNotEmpty(referencingReasons)) {
			ObjectResponse<Allele> response = new ObjectResponse<>();
			response.setErrorMessage("Allele " + allele.getIdentifier() + " is in use and cannot be deleted: " + String.join("; ", referencingReasons));
			throw new ApiErrorException(response);
		}

		alleleDAO.remove(allele.getId());
		return new ObjectResponse<>(allele);
	}

	private List<String> getReferencingAnnotationAndAgmReasons(Long alleleId) {
		List<String> reasons = new ArrayList<>();
		if (alleleDAO.hasReferencingDiseaseAnnotations(alleleId)) {
			reasons.add("Allele is referenced by disease annotation(s)");
		}
		if (alleleDAO.hasReferencingPhenotypeAnnotations(alleleId)) {
			reasons.add("Allele is referenced by phenotype annotation(s)");
		}
		if (alleleDAO.hasReferencingHTPExpressionDatasetSampleAnnotation(alleleId)) {
			reasons.add("Allele is referenced by HTP expression dataset annotation(s)");
		}
		if (alleleDAO.hasReferencingAgmAlleleAssociations(alleleId)) {
			reasons.add("Allele has AGM association(s)");
		}
		return reasons;
	}

	@Override
	@Transactional
	public Allele deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean forceDeprecate) {
		Allele allele = alleleDAO.find(id);
		List<String> deprecationReasons = new ArrayList<>();
		if (allele != null) {
			if (forceDeprecate) {
				deprecationReasons.add("Deprecation instead of deletion rule applied");
			}
			deprecationReasons.addAll(getReferencingAnnotationAndAgmReasons(id));
			if (CollectionUtils.isNotEmpty(allele.getAlleleGeneAssociations())) {
				deprecationReasons.add("Allele has gene association(s)");
			}
			if (CollectionUtils.isNotEmpty(allele.getAlleleVariantAssociations())) {
				deprecationReasons.add("Allele has variant association(s)");
			}
			if (CollectionUtils.isNotEmpty(allele.getConstructGenomicEntityAssociations())) {
				deprecationReasons.add("Allele has construct association(s)");
			}
			if (CollectionUtils.isNotEmpty(deprecationReasons)) {
				if (!allele.getObsolete()) {
					allele.setUpdatedBy(personService.fetchByUniqueIdOrCreate(requestSource));
					allele.setDateUpdated(OffsetDateTime.now());
					allele.setObsolete(true);
					Note deprecationNote = noteService.createDeprecationNote(allele.getIdentifier(), requestSource, deprecationReasons);
					if (allele.getRelatedNotes() == null) {
						allele.setRelatedNotes(new ArrayList<>());
					}
					allele.getRelatedNotes().add(deprecationNote);

					return alleleDAO.persist(allele);
				} else {
					return allele;
				}
			} else {
				alleleDAO.remove(id);
			}
		} else {
			String errorMessage = "Could not find Allele with id: " + id;
			if (throwApiError) {
				ObjectResponse<Allele> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			Log.error(errorMessage);
		}
		return null;
	}

	public List<Long> getIdsByDataProvider(String dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider);
		List<Long> ids = alleleDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);
		return ids;
	}

	@Override
	@Transactional
	public void updatePopularity(String curie, Double popularity) {
		SearchResponse<Allele> searchResponse = findByField("primaryExternalId", curie);

		if (searchResponse != null) {
			Allele allele = searchResponse.getSingleResult();
			allele.setPopularity(popularity);
		}
	}

	public List<Long> getAllAlleleSummaryIds() {
		return alleleDAO.getAllAlleleSummaryIds();
	}

	public SearchResponse<AlleleSummaryDocument> findAllelesForSummaryByIds(List<Long> ids) {
		return alleleDAO.findAllelesForSummaryByIds(ids);
	}

}
