package org.alliancegenome.curation_api.services.validation.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CodingSequenceDAO;
import org.alliancegenome.curation_api.dao.ExonDAO;
import org.alliancegenome.curation_api.dao.TranscriptDAO;
import org.alliancegenome.curation_api.dao.ontology.SoTermDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.CodingSequence;
import org.alliancegenome.curation_api.model.entities.Exon;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.Transcript;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.DataProviderService;
import org.alliancegenome.curation_api.services.helpers.gff3.Gff3UniqueIdHelper;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class Gff3DtoValidator {

	@Inject ExonDAO exonDAO;
	@Inject TranscriptDAO transcriptDAO;
	@Inject CodingSequenceDAO codingSequenceDAO;
	@Inject DataProviderService dataProviderService;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject SoTermDAO soTermDAO;
	
	@Transactional
	public Exon validateExonEntry(Gff3DTO dto, BackendBulkDataProvider dataProvider) throws ObjectValidationException {

		Exon exon = null;
		
		Map<String, String> attributes = getAttributes(dto, dataProvider);
		String uniqueId = Gff3UniqueIdHelper.getExonOrCodingSequenceUniqueId(dto, attributes, dataProvider);
		SearchResponse<Exon> searchResponse = exonDAO.findByField("uniqueId", uniqueId);
		if (searchResponse != null && searchResponse.getSingleResult() != null) {
			exon = searchResponse.getSingleResult();
		} else {
			exon = new Exon();
			exon.setUniqueId(uniqueId);
		}
		
		if (attributes.containsKey("Name")) {
			exon.setName(attributes.get("Name"));
		}
		
		ObjectResponse<Exon> exonResponse = validateGffEntity(exon, dto, attributes, dataProvider);
	
		if (exonResponse.hasErrors()) {
			throw new ObjectValidationException(dto, exonResponse.errorMessagesString());
		}
		
		return exonDAO.persist(exonResponse.getEntity());
	}
	
	@Transactional
	public CodingSequence validateCdsEntry(Gff3DTO dto, BackendBulkDataProvider dataProvider) throws ObjectValidationException {

		CodingSequence cds = null;
		
		Map<String, String> attributes = getAttributes(dto, dataProvider);
		String uniqueId = Gff3UniqueIdHelper.getExonOrCodingSequenceUniqueId(dto, attributes, dataProvider);
		SearchResponse<CodingSequence> searchResponse = codingSequenceDAO.findByField("uniqueId", uniqueId);
		if (searchResponse != null && searchResponse.getSingleResult() != null) {
			cds = searchResponse.getSingleResult();
		} else {
			cds = new CodingSequence();
			cds.setUniqueId(uniqueId);
		}
		
		if (attributes.containsKey("Name")) {
			cds.setName(attributes.get("Name"));
		}
		
		ObjectResponse<CodingSequence> cdsResponse = validateGffEntity(cds, dto, attributes, dataProvider);
	
		if (cdsResponse.hasErrors()) {
			throw new ObjectValidationException(dto, cdsResponse.errorMessagesString());
		}
		
		return codingSequenceDAO.persist(cdsResponse.getEntity());
	}
	
	@Transactional
	public Transcript validateTranscriptEntry(Gff3DTO dto, BackendBulkDataProvider dataProvider) throws ObjectValidationException {

		Transcript transcript = null;
		
		Map<String, String> attributes = getAttributes(dto, dataProvider);
		if (attributes.containsKey("ID")) {
			SearchResponse<Transcript> searchResponse = transcriptDAO.findByField("modInternalId", attributes.get("ID"));
			if (searchResponse != null && searchResponse.getSingleResult() != null) {
				transcript = searchResponse.getSingleResult();
			} else {
				transcript = new Transcript();
				transcript.setModInternalId(attributes.get("ID"));
			}
		}
		
		SearchResponse<SOTerm> soResponse = soTermDAO.findByField("name", dto.getType());
		if (soResponse != null) {
			transcript.setTranscriptType(soResponse.getSingleResult());
		}
		
		if (attributes.containsKey("Name")) {
			transcript.setName(attributes.get("Name"));
		}
		
		ObjectResponse<Transcript> transcriptResponse = validateGffEntity(transcript, dto, attributes, dataProvider);
		if (!attributes.containsKey("ID")) {
			transcriptResponse.addErrorMessage("attributes - ID", ValidationConstants.REQUIRED_MESSAGE);
		}
		
		if (transcriptResponse.hasErrors()) {
			throw new ObjectValidationException(dto, transcriptResponse.errorMessagesString());
		}
		
		return transcriptDAO.persist(transcriptResponse.getEntity());
	}
	
	private <E extends GenomicEntity> ObjectResponse<E> validateGffEntity(E entity, Gff3DTO dto, Map<String, String> attributes, BackendBulkDataProvider dataProvider) {
		ObjectResponse<E> geResponse = new ObjectResponse<E>();
		
		entity.setDataProvider(dataProviderService.createOrganizationDataProvider(dataProvider.sourceOrganization));
		entity.setTaxon(ncbiTaxonTermService.getByCurie(dataProvider.canonicalTaxonCurie).getEntity());
		
		geResponse.setEntity(entity);
		
		return geResponse;
	}
	
	private Map<String, String> getAttributes(Gff3DTO dto, BackendBulkDataProvider dataProvider) {
		Map<String, String> attributes = new HashMap<String, String>();
		if (CollectionUtils.isNotEmpty(dto.getAttributes())) {
			for (String keyValue : dto.getAttributes()) {
				String[] parts = keyValue.split("=");
				if (parts.length == 2) {
					attributes.put(parts[0], parts[1]);
				}
			}
		}

		if (StringUtils.equals(dataProvider.sourceOrganization, "WB")) {
			for (String key : List.of("ID", "Parent")) {
				if (attributes.containsKey(key)) {
					String id = attributes.get(key);
					String[] idParts = id.split(":");
					if (idParts.length > 1) {
						id = idParts[1];
					}
					attributes.put(key, id);
				}
			}
		}
		
		return attributes;
	}

}
