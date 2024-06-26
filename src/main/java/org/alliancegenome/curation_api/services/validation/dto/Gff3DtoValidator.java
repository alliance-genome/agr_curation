package org.alliancegenome.curation_api.services.validation.dto;

import java.util.HashMap;
import java.util.Map;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CodingSequenceDAO;
import org.alliancegenome.curation_api.dao.ExonDAO;
import org.alliancegenome.curation_api.dao.TranscriptDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Exon;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.DataProviderService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class Gff3DtoValidator extends BaseDTOValidator {

	@Inject ExonDAO exonDAO;
	@Inject TranscriptDAO transcriptDAO;
	@Inject CodingSequenceDAO codingSequenceDAO;
	@Inject DataProviderService dataProviderService;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	
	private ObjectResponse<Exon> exonResponse;

	@Transactional
	public Exon validateExonEntry(Gff3DTO dto, BackendBulkDataProvider dataProvider) throws ObjectValidationException {

		exonResponse = new ObjectResponse<>();

		Exon exon = new Exon();
		if (StringUtils.isNotBlank(dto.getSeqId())) {
			SearchResponse<Exon> exonResponse = exonDAO.findByField("modEntityId", dto.getSeqId());
			if (exonResponse != null && exonResponse.getSingleResult() != null) {
				exon = exonResponse.getSingleResult();
			}
		}
		
		ObjectResponse<Exon> geResponse = validateGffEntry(exon, dto, dataProvider);
		

		
		return exonDAO.persist(exon);
	}
	
	private <E extends GenomicEntity> ObjectResponse<E> validateGffEntry(E entity, Gff3DTO dto, BackendBulkDataProvider dataProvider) {
		ObjectResponse<E> geResponse = new ObjectResponse<E>();
		
		if (StringUtils.isBlank(dto.getSeqId())) {
			geResponse.addErrorMessage("seqId", ValidationConstants.REQUIRED_MESSAGE);
		}
		entity.setModEntityId(dto.getSeqId());
		
		entity.setDataProvider(dataProviderService.createOrganizationDataProvider(dataProvider.sourceOrganization));
		entity.setTaxon(ncbiTaxonTermService.getByCurie(dataProvider.canonicalTaxonCurie).getEntity());
		
		
		Map<String, String> attributes = getAttributes(dto);
		
		return geResponse;
	}
	
	private Map<String, String> getAttributes (Gff3DTO dto) {
		Map<String, String> attributes = new HashMap<String, String>();
		if (CollectionUtils.isNotEmpty(dto.getAttributes())) {
			for (String keyValue : dto.getAttributes()) {
				String[] parts = keyValue.split("=");
				if (parts.length == 2) {
					attributes.put(parts[0], parts[1]);
				}
			}
		}
		return attributes;
	}

}
