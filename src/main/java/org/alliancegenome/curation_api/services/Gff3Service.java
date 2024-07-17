package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.constants.Gff3Constants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CodingSequenceDAO;
import org.alliancegenome.curation_api.dao.ExonDAO;
import org.alliancegenome.curation_api.dao.GenomeAssemblyDAO;
import org.alliancegenome.curation_api.dao.TranscriptDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.CodingSequence;
import org.alliancegenome.curation_api.model.entities.Exon;
import org.alliancegenome.curation_api.model.entities.GenomeAssembly;
import org.alliancegenome.curation_api.model.entities.Transcript;
import org.alliancegenome.curation_api.model.entities.associations.codingSequenceAssociations.CodingSequenceGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.entities.associations.exonAssociations.ExonGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.entities.associations.transcriptAssociations.TranscriptGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.associations.codingSequenceAssociations.CodingSequenceGenomicLocationAssociationService;
import org.alliancegenome.curation_api.services.associations.exonAssociations.ExonGenomicLocationAssociationService;
import org.alliancegenome.curation_api.services.associations.transcriptAssociations.TranscriptGenomicLocationAssociationService;
import org.alliancegenome.curation_api.services.helpers.gff3.Gff3AttributesHelper;
import org.alliancegenome.curation_api.services.helpers.gff3.Gff3UniqueIdHelper;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.services.validation.dto.Gff3DtoValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class Gff3Service {

	@Inject GenomeAssemblyDAO genomeAssemblyDAO;
	@Inject ExonDAO exonDAO;
	@Inject CodingSequenceDAO cdsDAO;
	@Inject TranscriptDAO transcriptDAO;
	@Inject ExonGenomicLocationAssociationService exonLocationService;
	@Inject CodingSequenceGenomicLocationAssociationService cdsLocationService;
	@Inject TranscriptGenomicLocationAssociationService transcriptLocationService;
	@Inject DataProviderService dataProviderService;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject Gff3DtoValidator gff3DtoValidator;
	
	@Transactional
	public void loadGenomeAssembly(String assemblyName, List<String> gffHeaderData, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		
		if (StringUtils.isBlank(assemblyName)) {
			for (String header : gffHeaderData) {
				if (header.startsWith("#!assembly")) {
					assemblyName = header.split(" ")[1];
				}
			}
		}
		if (StringUtils.isNotBlank(assemblyName)) {
			Map<String, Object> params = new HashMap<>();
			params.put("modEntityId", assemblyName);
			params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
			params.put(EntityFieldConstants.TAXON, dataProvider.canonicalTaxonCurie);
			
			SearchResponse<GenomeAssembly> resp = genomeAssemblyDAO.findByParams(params);
			if (resp == null || resp.getSingleResult() == null) {
				GenomeAssembly assembly = new GenomeAssembly();
				assembly.setModEntityId(assemblyName);
				assembly.setDataProvider(dataProviderService.createOrganizationDataProvider(dataProvider.sourceOrganization));
				assembly.setTaxon(ncbiTaxonTermService.getByCurie(dataProvider.canonicalTaxonCurie).getEntity());
				
				genomeAssemblyDAO.persist(assembly);
			}
		} else {
			throw new ObjectValidationException(gffHeaderData, "#!assembly - " + ValidationConstants.REQUIRED_MESSAGE);
		}
	}
	
	public Map<String, List<Long>> loadEntity(BulkLoadFileHistory history, Gff3DTO gffEntry, Map<String, List<Long>> idsAdded, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		if (StringUtils.equals(gffEntry.getType(), "exon") || StringUtils.equals(gffEntry.getType(), "noncoding_exon")) {
			Exon exon = gff3DtoValidator.validateExonEntry(gffEntry, dataProvider);
			if (exon != null) {
				idsAdded.get("Exon").add(exon.getId());
			}
		} else if (StringUtils.equals(gffEntry.getType(), "CDS")) {
			CodingSequence cds = gff3DtoValidator.validateCdsEntry(gffEntry, dataProvider);
			if (cds != null) {
				idsAdded.get("CodingSequence").add(cds.getId());
			}
		} else if (Gff3Constants.TRANSCRIPT_TYPES.contains(gffEntry.getType())) {
			if (StringUtils.equals(gffEntry.getType(), "lnc_RNA")) {
				gffEntry.setType("lncRNA");
			}
			Transcript transcript = gff3DtoValidator.validateTranscriptEntry(gffEntry, dataProvider);
			if (transcript != null) {
				idsAdded.get("Transcript").add(transcript.getId());
			}
		}
		return idsAdded;
	}
	
	@Transactional
	public Map<String, List<Long>> loadAssociations(BulkLoadFileHistory history, Gff3DTO gffEntry, Map<String, List<Long>> idsAdded, BackendBulkDataProvider dataProvider, String assemblyId) throws ObjectUpdateException {
		if (StringUtils.isEmpty(assemblyId)) {
			throw new ObjectValidationException(gffEntry, "Cannot load associations without assembly");
		}
		
		Map<String, String> attributes = Gff3AttributesHelper.getAttributes(gffEntry, dataProvider);
		if (StringUtils.equals(gffEntry.getType(), "exon") || StringUtils.equals(gffEntry.getType(), "noncoding_exon")) {
			String uniqueId = Gff3UniqueIdHelper.getExonOrCodingSequenceUniqueId(gffEntry, attributes, dataProvider);
			SearchResponse<Exon> response = exonDAO.findByField("uniqueId", uniqueId);
			if (response == null || response.getSingleResult() == null) {
				throw new ObjectValidationException(gffEntry, "uniqueId - " + ValidationConstants.INVALID_MESSAGE + " (" + uniqueId + ")");
			}
			Exon exon = response.getSingleResult();
			
			ExonGenomicLocationAssociation exonLocation = gff3DtoValidator.validateExonLocation(gffEntry, exon, assemblyId, dataProvider);
			if (exonLocation != null) {
				idsAdded.get("ExonGenomicLocationAssociation").add(exonLocation.getId());
				exonLocationService.addAssociationToSubjectAndObject(exonLocation);
			}
		} else if (StringUtils.equals(gffEntry.getType(), "CDS")) {
			String uniqueId = Gff3UniqueIdHelper.getExonOrCodingSequenceUniqueId(gffEntry, attributes, dataProvider);
			SearchResponse<CodingSequence> response = cdsDAO.findByField("uniqueId", uniqueId);
			if (response == null || response.getSingleResult() == null) {
				throw new ObjectValidationException(gffEntry, "uniqueId - " + ValidationConstants.INVALID_MESSAGE + " (" + uniqueId + ")");
			}
			CodingSequence cds = response.getSingleResult();
			CodingSequenceGenomicLocationAssociation cdsLocation = gff3DtoValidator.validateCdsLocation(gffEntry, cds, assemblyId, dataProvider);
			if (cdsLocation != null) {
				idsAdded.get("CodingSequenceGenomicLocationAssociation").add(cdsLocation.getId());
				cdsLocationService.addAssociationToSubjectAndObject(cdsLocation);
			}
		} else if (Gff3Constants.TRANSCRIPT_TYPES.contains(gffEntry.getType())) {
			if (StringUtils.equals(gffEntry.getType(), "lnc_RNA")) {
				gffEntry.setType("lncRNA");
			}
			if (!attributes.containsKey("ID")) {
				throw new ObjectValidationException(gffEntry, "attributes - ID - " + ValidationConstants.REQUIRED_MESSAGE);
			}
			SearchResponse<Transcript> response = transcriptDAO.findByField("modInternalId", attributes.get("ID"));
			if (response == null || response.getSingleResult() == null) {
				throw new ObjectValidationException(gffEntry, "attributes - ID - " + ValidationConstants.INVALID_MESSAGE + " (" + attributes.get("ID") + ")");
			}
			Transcript transcript = response.getSingleResult();
			TranscriptGenomicLocationAssociation transcriptLocation = gff3DtoValidator.validateTranscriptLocation(gffEntry, transcript, assemblyId, dataProvider);
			if (transcriptLocation != null) {
				idsAdded.get("TranscriptGenomicLocationAssociation").add(transcriptLocation.getId());
				transcriptLocationService.addAssociationToSubjectAndObject(transcriptLocation);
			}
		}
		
		return idsAdded;
	}
	
}
