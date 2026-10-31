package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.PredictedVariantConsequenceDAO;
import org.alliancegenome.curation_api.dao.VariantDAO;
import org.alliancegenome.curation_api.dao.associations.AlleleVariantAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.PredictedVariantConsequence;
import org.alliancegenome.curation_api.model.entities.Transcript;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.associations.AlleleVariantAssociation;
import org.alliancegenome.curation_api.model.entities.associations.CuratedVariantGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.VariantDTO;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.model.output.ModVariant;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.validation.VariantValidator;
import org.alliancegenome.curation_api.services.validation.dto.VariantDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class VariantService extends SubmittedObjectCrudService<Variant, VariantDTO, VariantDAO> {

	@Inject VariantDAO variantDAO;
	@Inject AlleleVariantAssociationDAO alleleVariantAssociationDAO;
	@Inject PredictedVariantConsequenceDAO predictedVariantConsequenceDAO;
	@Inject NoteService noteService;
	@Inject VariantValidator variantValidator;
	@Inject VariantDTOValidator variantDtoValidator;
	@Inject DiseaseAnnotationService diseaseAnnotationService;
	@Inject PersonService personService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(variantDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<Variant> update(Variant uiEntity) {
		Variant dbEntity = variantValidator.validateVariantUpdate(uiEntity);
		return new ObjectResponse<>(dbEntity);
	}

	@Override
	@Transactional
	public ObjectResponse<Variant> create(Variant uiEntity) {
		Variant dbEntity = variantValidator.validateVariantCreate(uiEntity);
		return new ObjectResponse<>(dbEntity);
	}

	@Override
	public ObjectResponse<Variant> upsert(VariantDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return variantDtoValidator.validateVariantDTO(dto, dataProvider);
	}

	@Override
	@Transactional
	public Variant deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean forceDeprecate) {
		Variant variant = variantDAO.find(id);
		List<String> deprecationReasons = new ArrayList<>();
		if (variant != null) {
			if (forceDeprecate) {
				deprecationReasons.add("Deprecation instead of deletion rule applied");
			}
			if (CollectionUtils.isNotEmpty(variant.getAlleleVariantAssociations())) {
				deprecationReasons.add("Variant has allele association(s)");
			}
			if (CollectionUtils.isNotEmpty(variant.getCuratedVariantGenomicLocations())) {
				deprecationReasons.add("Variant has curated genomic location(s)");
			}
			if (CollectionUtils.isNotEmpty(deprecationReasons)) {
				if (!variant.getObsolete()) {
					variant.setUpdatedBy(personService.fetchByUniqueIdOrCreate(requestSource));
					variant.setDateUpdated(OffsetDateTime.now());
					variant.setObsolete(true);
					
					Note deprecationNote = noteService.createDeprecationNote(variant.getIdentifier(), requestSource, deprecationReasons);
					if (variant.getRelatedNotes() == null) {
						variant.setRelatedNotes(new ArrayList<>());
					}
					variant.getRelatedNotes().add(deprecationNote);
					
					return variantDAO.persist(variant);
				} else {
					return variant;
				}
			} else {
				variantDAO.remove(id);
			}
		} else {
			String errorMessage = "Could not find Variant with id: " + id;
			if (throwApiError) {
				ObjectResponse<Variant> response = new ObjectResponse<>();
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
		List<Long> ids = variantDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);
		return ids;
	}

	public ObjectListResponse<String> findAllVariantNames() {
		return variantDAO.getAllVariantNames();
	}

	/** At most this many allele - variant pairs per page of the HGVS export. */
	public static final int MAX_ALL_MOD_VARIANTS_PAGE_SIZE = 10000;

	// SCRUM-6757: a page of a data provider's allele - variant pairs with their HGVS; page 0 with limit 0 returns only the total
	public SearchResponse<ModVariant> getAllModVariants(String dataProvider, Integer page, Integer limit) {
		Map<String, Object> params = new HashMap<>();
		params.put("alleleVariantAssociationObject.dataProvider.abbreviation", dataProvider);
		params.put("obsolete", false);
		params.put("alleleAssociationSubject.obsolete", false);
		params.put("alleleVariantAssociationObject.obsolete", false);

		SearchResponse<AlleleVariantAssociation> associations = alleleVariantAssociationDAO.findByParams(new Pagination(page, Math.min(limit, MAX_ALL_MOD_VARIANTS_PAGE_SIZE)), params);
		// findByParams only counts on the 0, 0 call, and then returns no results
		if (page == 0 && limit == 0) {
			SearchResponse<ModVariant> count = new SearchResponse<>();
			count.setTotalResults(associations.getTotalResults());
			return count;
		}
		List<ModVariant> results = associations.getResults().stream().map(this::toModVariant).toList();
		populateIntronExonLocations(results);
		return new SearchResponse<>(results);
	}

	// The exon or intron each consequence falls in is transient, so it is computed for the whole page in one query
	private void populateIntronExonLocations(List<ModVariant> results) {
		Map<Long, PredictedVariantConsequence> pvcById = new HashMap<>();
		for (ModVariant result : results) {
			for (PredictedVariantConsequence pvc : result.predictedVariantConsequences()) {
				if (pvc.getId() != null && pvc.getVariantTranscript() != null) {
					pvcById.put(pvc.getId(), pvc);
				}
			}
		}
		predictedVariantConsequenceDAO.populateIntronExonLocations(pvcById);
	}

	private ModVariant toModVariant(AlleleVariantAssociation association) {
		Variant variant = association.getAlleleVariantAssociationObject();
		CuratedVariantGenomicLocationAssociation location = variant.getCuratedVariantGenomicLocations().stream()
			.filter(l -> !l.getObsolete())
			.findFirst()
			.orElse(null);
		List<PredictedVariantConsequence> consequences = location == null ? List.of() : location.getPredictedVariantConsequences().stream()
			.filter(c -> !c.getObsolete())
			.toList();
		return new ModVariant(
			association.getAlleleAssociationSubject().getPrimaryExternalId(),
			variant.getCurie(),
			location == null ? null : location.getHgvs(),
			distinctSorted(consequences, PredictedVariantConsequence::getHgvsCodingNomenclature),
			distinctSorted(consequences, PredictedVariantConsequence::getHgvsProteinNomenclature),
			// a sorted copy: the location's own list is managed, with orphan removal
			consequences.stream().sorted(Comparator.comparing(VariantService::transcriptId, Comparator.nullsLast(Comparator.naturalOrder()))).toList());
	}

	// The consequence's transcript accession, e.g. NM_201090.2, or its identifier when it has none
	private static String transcriptId(PredictedVariantConsequence consequence) {
		Transcript transcript = consequence.getVariantTranscript();
		if (transcript == null) {
			return null;
		}
		return transcript.getTranscriptId() != null ? transcript.getTranscriptId() : transcript.getIdentifier();
	}

	private static List<String> distinctSorted(List<PredictedVariantConsequence> consequences, Function<PredictedVariantConsequence, String> hgvs) {
		return consequences.stream().map(hgvs).filter(Objects::nonNull).distinct().sorted().toList();
	}

}
