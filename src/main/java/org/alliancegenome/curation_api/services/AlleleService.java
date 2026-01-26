package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.base.BasePopularityInterface;
import org.alliancegenome.curation_api.model.document.es.AlleleSummaryDTO;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.validation.AlleleValidator;
import org.alliancegenome.curation_api.services.validation.dto.AlleleDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.alliancegenome.curation_api.model.input.Pagination;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AlleleService extends SubmittedObjectCrudService<Allele, AlleleDTO, AlleleDAO> implements BasePopularityInterface {

	@Inject AlleleDAO alleleDAO;
	@Inject AlleleValidator alleleValidator;
	@Inject AlleleDTOValidator alleleDtoValidator;
	@Inject PersonService personService;
	@Inject NoteService noteService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<Allele> update(Allele uiEntity) {
		Allele dbEntity = alleleValidator.validateAlleleUpdate(uiEntity, false);
		return new ObjectResponse<>(dbEntity);
	}

	@Transactional
	public ObjectResponse<Allele> updateDetail(Allele uiEntity) {
		Allele dbEntity = alleleValidator.validateAlleleUpdate(uiEntity, true);
		return new ObjectResponse<>(dbEntity);
	}

	@Override
	@Transactional
	public ObjectResponse<Allele> create(Allele uiEntity) {
		Allele dbEntity = alleleValidator.validateAlleleCreate(uiEntity);
		return new ObjectResponse<>(dbEntity);
	}

	@Override
	public Allele upsert(AlleleDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return alleleDtoValidator.validateAlleleDTO(dto, dataProvider);
	}

	@Override
	@Transactional
	public ObjectResponse<Allele> deleteById(Long id) {
		deprecateOrDelete(id, true, "Allele DELETE API call", false);
		ObjectResponse<Allele> ret = new ObjectResponse<>();
		return ret;
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
			if (alleleDAO.hasReferencingDiseaseAnnotations(id)) {
				deprecationReasons.add("Allele is referenced by disease annotation(s)");
			}
			if (alleleDAO.hasReferencingPhenotypeAnnotations(id)) {
				deprecationReasons.add("Allele is referenced by phenotype annotation(s)");
			}
			if (alleleDAO.hasReferencingHTPExpressionDatasetSampleAnnotation(id)) {
				deprecationReasons.add("Allele is referenced by HTP expression dataset annotation(s)");
			}
			if (alleleDAO.hasReferencingAgmAlleleAssociations(id)) {
				deprecationReasons.add("Allele has AGM association(s)");
			}
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

	public SearchResponse<AlleleSummaryDTO> findAllelesForSummary(Pagination pagination, Map<String, Object> params) {
		return alleleDAO.findAllelesForSummary(pagination, params);
	}
}
