package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.CodingSequenceDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.CodingSequence;
import org.alliancegenome.curation_api.model.entities.Exon;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.Gff3DtoValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class CodingSequenceService extends BaseEntityCrudService<CodingSequence, CodingSequenceDAO> {

	@Inject CodingSequenceDAO codingSequenceDAO;
	@Inject PersonService personService;
	@Inject Gff3DtoValidator gff3DtoValidator;
	@Inject NoteService noteService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(codingSequenceDAO);
	}

	public List<Long> getIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD") || StringUtils.equals(dataProvider.sourceOrganization, "XB")) {
			params.put(EntityFieldConstants.TAXON, dataProvider.canonicalTaxonCurie);
		}
		List<Long> ids = codingSequenceDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);
		return ids;
	}

	@Override
	public ObjectResponse<CodingSequence> getByIdentifier(String identifier) {
		CodingSequence object = findByAlternativeFields(List.of("curie", "primaryExternalId", "modInternalId", "uniqueId"), identifier);
		ObjectResponse<CodingSequence> ret = new ObjectResponse<CodingSequence>(object);
		return ret;
	}

	public ObjectResponse<CodingSequence> deleteByIdentifier(String identifierString) {
		CodingSequence codingSequence = findByAlternativeFields(List.of("curie", "primaryExternalId", "modInternalId", "uniqueId"), identifierString);
		if (codingSequence != null) {
			codingSequenceDAO.remove(codingSequence.getId());
		}
		ObjectResponse<CodingSequence> ret = new ObjectResponse<>(codingSequence);
		return ret;
	}

	@Override
	@Transactional
	public CodingSequence deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean forceDeprecate) {
		CodingSequence cds = codingSequenceDAO.find(id);
		List<String> deprecationReasons = new ArrayList<>();
		if (cds != null) {
			if (forceDeprecate) {
				deprecationReasons.add("Deprecation instead of deletion rule applied");
			}
			if (CollectionUtils.isNotEmpty(cds.getCodingSequenceGenomicLocationAssociations())) {
				deprecationReasons.add("CDS has genomic location association(s)");
			}
			if (CollectionUtils.isNotEmpty(cds.getTranscriptCodingSequenceAssociations())) {
				deprecationReasons.add("CDS has transcript association(s)");
			}
			if (CollectionUtils.isNotEmpty(deprecationReasons)) {
				if (!cds.getObsolete()) {
					cds.setUpdatedBy(personService.fetchByUniqueIdOrCreate(requestSource));
					cds.setDateUpdated(OffsetDateTime.now());
					cds.setObsolete(true);
					
					Note deprecationNote = noteService.createDeprecationNote(cds.getIdentifier(), requestSource, deprecationReasons);
					if (cds.getRelatedNotes() == null) {
						cds.setRelatedNotes(new ArrayList<>());
					}
					cds.getRelatedNotes().add(deprecationNote);
					
					return codingSequenceDAO.persist(cds);
				} else {
					return cds;
				}
			} else {
				codingSequenceDAO.remove(id);
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

}
