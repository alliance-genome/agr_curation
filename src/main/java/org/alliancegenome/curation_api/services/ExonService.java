package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.ExonDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Exon;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class ExonService extends BaseEntityCrudService<Exon, ExonDAO> {

	@Inject ExonDAO exonDAO;
	@Inject PersonService personService;
	@Inject NoteService noteService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(exonDAO);
	}

	@Override
	@Transactional
	public Exon deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean forceDeprecate) {
		Exon exon = exonDAO.find(id);
		List<String> deprecationReasons = new ArrayList<>();
		if (exon != null) {
			if (forceDeprecate) {
				deprecationReasons.add("Deprecation instead of deletion rule applied");
			}
			if (CollectionUtils.isNotEmpty(exon.getExonGenomicLocationAssociations())) {
				deprecationReasons.add("Exon has genomic location association(s)");
			}
			if (CollectionUtils.isNotEmpty(exon.getTranscriptExonAssociations())) {
				deprecationReasons.add("Exon has transcript association(s)");
			}
			if (CollectionUtils.isNotEmpty(deprecationReasons)) {
				if (!exon.getObsolete()) {
					exon.setUpdatedBy(personService.fetchByUniqueIdOrCreate(requestSource));
					exon.setDateUpdated(OffsetDateTime.now());
					exon.setObsolete(true);
					
					Note deprecationNote = noteService.createDeprecationNote(exon.getIdentifier(), requestSource, deprecationReasons);
					if (exon.getRelatedNotes() == null) {
						exon.setRelatedNotes(new ArrayList<>());
					}
					exon.getRelatedNotes().add(deprecationNote);
					
					return exonDAO.persist(exon);
				} else {
					return exon;
				}
			} else {
				exonDAO.remove(id);
			}
		} else {
			String errorMessage = "Could not find Exon with id: " + id;
			if (throwApiError) {
				ObjectResponse<Variant> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			Log.error(errorMessage);
		}
		return null;
	}

	public List<Long> getIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD") || StringUtils.equals(dataProvider.sourceOrganization, "XB")) {
			params.put(EntityFieldConstants.TAXON, dataProvider.canonicalTaxonCurie);
		}
		List<Long> ids = exonDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);
		return ids;
	}

	@Override
	public ObjectResponse<Exon> getByIdentifier(String identifier) {
		Exon object = findByAlternativeFields(List.of("curie", "primaryExternalId", "modInternalId", "uniqueId"), identifier);
		ObjectResponse<Exon> ret = new ObjectResponse<Exon>(object);
		return ret;
	}

	public ObjectResponse<Exon> deleteByIdentifier(String identifierString) {
		Exon exon = findByAlternativeFields(List.of("primaryExternalId", "modInternalId", "uniqueId"), identifierString);
		if (exon != null) {
			exonDAO.remove(exon.getId());
		}
		ObjectResponse<Exon> ret = new ObjectResponse<>(exon);
		return ret;
	}
}
