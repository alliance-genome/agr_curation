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
import org.alliancegenome.curation_api.model.entities.associations.transcriptAssociations.TranscriptCodingSequenceAssociation;
import org.alliancegenome.curation_api.model.entities.associations.transcriptAssociations.TranscriptExonAssociation;
import org.alliancegenome.curation_api.model.entities.associations.transcriptAssociations.TranscriptGeneAssociation;
import org.alliancegenome.curation_api.model.entities.associations.transcriptAssociations.TranscriptGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.associations.codingSequenceAssociations.CodingSequenceGenomicLocationAssociationService;
import org.alliancegenome.curation_api.services.associations.exonAssociations.ExonGenomicLocationAssociationService;
import org.alliancegenome.curation_api.services.associations.transcriptAssociations.TranscriptCodingSequenceAssociationService;
import org.alliancegenome.curation_api.services.associations.transcriptAssociations.TranscriptExonAssociationService;
import org.alliancegenome.curation_api.services.associations.transcriptAssociations.TranscriptGeneAssociationService;
import org.alliancegenome.curation_api.services.associations.transcriptAssociations.TranscriptGenomicLocationAssociationService;
import org.alliancegenome.curation_api.services.helpers.gff3.Gff3UniqueIdHelper;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.services.validation.dto.Gff3DtoValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

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
	@Inject TranscriptGeneAssociationService transcriptGeneService;
	@Inject TranscriptCodingSequenceAssociationService transcriptCdsService;
	@Inject TranscriptExonAssociationService transcriptExonService;
	@Inject DataProviderService dataProviderService;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject Gff3DtoValidator gff3DtoValidator;

	@Transactional
	public String loadGenomeAssembly(String assemblyName, List<String> gffHeaderData, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {

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
				assembly.setDataProvider(dataProviderService.getDefaultDataProvider(dataProvider.sourceOrganization));
				assembly.setTaxon(ncbiTaxonTermService.getByCurie(dataProvider.canonicalTaxonCurie).getEntity());

				genomeAssemblyDAO.persist(assembly);
			}

			return assemblyName;
		} else {
			throw new ObjectValidationException(gffHeaderData, "#!assembly - " + ValidationConstants.REQUIRED_MESSAGE);
		}
	}

	@Transactional
	public void loadExonLocationAssociations(BulkLoadFileHistory history, ImmutablePair<Gff3DTO, Map<String, String>> gffEntryPair, List<Long> idsAdded, BackendBulkDataProvider dataProvider, String assemblyId, Map<String, String> geneIdCurieMap) throws ObjectUpdateException {
		Gff3DTO gffEntry = gffEntryPair.getKey();

		if (StringUtils.isBlank(assemblyId)) {
			throw new ObjectValidationException(gffEntry, "Cannot load associations without assembly");
		}

		if (!StringUtils.equals(gffEntry.getType(), "exon") && !StringUtils.equals(gffEntry.getType(), "noncoding_exon")) {
			throw new ObjectValidationException(gffEntry, "Invalid Type: " + gffEntry.getType() + " for Exon Location");
		}

		String uniqueId = Gff3UniqueIdHelper.getExonOrCodingSequenceUniqueId(gffEntry, gffEntryPair.getValue(), dataProvider);
		SearchResponse<Exon> response = exonDAO.findByField("uniqueId", uniqueId);
		if (response == null || response.getSingleResult() == null) {
			throw new ObjectValidationException(gffEntry, "uniqueId - " + ValidationConstants.INVALID_MESSAGE + " (" + uniqueId + ")");
		}
		Exon exon = response.getSingleResult();

		ExonGenomicLocationAssociation exonLocation = gff3DtoValidator.validateExonLocation(gffEntry, exon, assemblyId, dataProvider);
		if (exonLocation != null) {
			idsAdded.add(exonLocation.getId());
			exonLocationService.addAssociationToSubject(exonLocation);
		}

	}

	@Transactional
	public void loadCDSLocationAssociations(BulkLoadFileHistory history, ImmutablePair<Gff3DTO, Map<String, String>> gffEntryPair, List<Long> idsAdded, BackendBulkDataProvider dataProvider, String assemblyId, Map<String, String> geneIdCurieMap) throws ObjectUpdateException {
		Gff3DTO gffEntry = gffEntryPair.getKey();
		Map<String, String> attributes = gffEntryPair.getValue();
		if (StringUtils.isBlank(assemblyId)) {
			throw new ObjectValidationException(gffEntry, "Cannot load associations without assembly");
		}

		if (!StringUtils.equals(gffEntry.getType(), "CDS")) {
			throw new ObjectValidationException(gffEntry, "Invalid Type: " + gffEntry.getType() + " for CDS Location");
		}

		String uniqueId = Gff3UniqueIdHelper.getExonOrCodingSequenceUniqueId(gffEntry, attributes, dataProvider);
		SearchResponse<CodingSequence> response = cdsDAO.findByField("uniqueId", uniqueId);
		if (response == null || response.getSingleResult() == null) {
			throw new ObjectValidationException(gffEntry, "uniqueId - " + ValidationConstants.INVALID_MESSAGE + " (" + uniqueId + ")");
		}
		CodingSequence cds = response.getSingleResult();

		CodingSequenceGenomicLocationAssociation cdsLocation = gff3DtoValidator.validateCdsLocation(gffEntry, cds, assemblyId, dataProvider);
		if (cdsLocation != null) {
			idsAdded.add(cdsLocation.getId());
			cdsLocationService.addAssociationToSubject(cdsLocation);
		}

	}

	@Transactional
	public void loadTranscriptLocationAssociations(BulkLoadFileHistory history, ImmutablePair<Gff3DTO, Map<String, String>> gffEntryPair, List<Long> idsAdded, BackendBulkDataProvider dataProvider, String assemblyId, Map<String, String> geneIdCurieMap) throws ObjectUpdateException {
		Gff3DTO gffEntry = gffEntryPair.getKey();
		Map<String, String> attributes = gffEntryPair.getValue();
		if (StringUtils.isBlank(assemblyId)) {
			throw new ObjectValidationException(gffEntry, "Cannot load associations without assembly");
		}

		if (!Gff3Constants.TRANSCRIPT_TYPES.contains(gffEntry.getType())) {
			throw new ObjectValidationException(gffEntry, "Invalid Type: " + gffEntry.getType() + " for Transcript Location");
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
			idsAdded.add(transcriptLocation.getId());
			transcriptLocationService.addAssociationToSubject(transcriptLocation);
		}
	}

	@Transactional
	public void loadExonParentChildAssociations(BulkLoadFileHistory history, ImmutablePair<Gff3DTO, Map<String, String>> gffEntryPair, List<Long> idsAdded, BackendBulkDataProvider dataProvider, Map<String, String> geneIdCurieMap) throws ObjectUpdateException {
		Gff3DTO gffEntry = gffEntryPair.getKey();

		if (!StringUtils.equals(gffEntry.getType(), "exon") && !StringUtils.equals(gffEntry.getType(), "noncoding_exon")) {
			throw new ObjectValidationException(gffEntry, "Invalid Type: " + gffEntry.getType() + " for Exon Transcript Associations");
		}

		Map<String, String> attributes = gffEntryPair.getValue();
		String uniqueId = Gff3UniqueIdHelper.getExonOrCodingSequenceUniqueId(gffEntry, attributes, dataProvider);
		SearchResponse<Exon> response = exonDAO.findByField("uniqueId", uniqueId);
		if (response == null || response.getSingleResult() == null) {
			throw new ObjectValidationException(gffEntry, "uniqueId - " + ValidationConstants.INVALID_MESSAGE + " (" + uniqueId + ")");
		}
		Exon exon = response.getSingleResult();

		TranscriptExonAssociation transcriptAssociation = gff3DtoValidator.validateTranscriptExonAssociation(gffEntry, exon, attributes);
		if (transcriptAssociation != null) {
			idsAdded.add(transcriptAssociation.getId());
			transcriptExonService.addAssociationToSubjectAndObject(transcriptAssociation);
		}

	}

	@Transactional
	public void loadCDSParentChildAssociations(BulkLoadFileHistory history, ImmutablePair<Gff3DTO, Map<String, String>> gffEntryPair, List<Long> idsAdded, BackendBulkDataProvider dataProvider, Map<String, String> geneIdCurieMap) throws ObjectUpdateException {
		Gff3DTO gffEntry = gffEntryPair.getKey();
		Map<String, String> attributes = gffEntryPair.getValue();

		if (!StringUtils.equals(gffEntry.getType(), "CDS")) {
			throw new ObjectValidationException(gffEntry, "Invalid Type: " + gffEntry.getType() + " for CDS Transcript Associations");
		}

		String uniqueId = Gff3UniqueIdHelper.getExonOrCodingSequenceUniqueId(gffEntry, attributes, dataProvider);
		SearchResponse<CodingSequence> response = cdsDAO.findByField("uniqueId", uniqueId);
		if (response == null || response.getSingleResult() == null) {
			throw new ObjectValidationException(gffEntry, "uniqueId - " + ValidationConstants.INVALID_MESSAGE + " (" + uniqueId + ")");
		}
		CodingSequence cds = response.getSingleResult();

		TranscriptCodingSequenceAssociation transcriptAssociation = gff3DtoValidator.validateTranscriptCodingSequenceAssociation(gffEntry, cds, attributes);
		if (transcriptAssociation != null) {
			idsAdded.add(transcriptAssociation.getId());
			transcriptCdsService.addAssociationToSubjectAndObject(transcriptAssociation);
		}
	}

	@Transactional
	public void loadGeneParentChildAssociations(BulkLoadFileHistory history, ImmutablePair<Gff3DTO, Map<String, String>> gffEntryPair, List<Long> idsAdded, BackendBulkDataProvider dataProvider, Map<String, String> geneIdCurieMap) throws ObjectUpdateException {
		Gff3DTO gffEntry = gffEntryPair.getKey();
		if (!Gff3Constants.TRANSCRIPT_TYPES.contains(gffEntry.getType())) {
			throw new ObjectValidationException(gffEntry, "Invalid Type: " + gffEntry.getType() + " for Gene Transcript Associations");
		}

		Map<String, String> attributes = gffEntryPair.getValue();
		if (!attributes.containsKey("ID")) {
			throw new ObjectValidationException(gffEntry, "attributes - ID - " + ValidationConstants.REQUIRED_MESSAGE);
		}

		SearchResponse<Transcript> response = transcriptDAO.findByField("modInternalId", attributes.get("ID"));
		if (response == null || response.getSingleResult() == null) {
			throw new ObjectValidationException(gffEntry, "attributes - ID - " + ValidationConstants.INVALID_MESSAGE + " (" + attributes.get("ID") + ")");
		}
		Transcript transcript = response.getSingleResult();

		TranscriptGeneAssociation geneAssociation = gff3DtoValidator.validateTranscriptGeneAssociation(gffEntry, transcript, attributes, geneIdCurieMap);
		if (geneAssociation != null) {
			idsAdded.add(geneAssociation.getId());
			transcriptGeneService.addAssociationToSubjectAndObject(geneAssociation);
		}
	}

	public Map<String, String> getIdCurieMap(List<ImmutablePair<Gff3DTO, Map<String, String>>> gffData) {
		Map<String, String> geneIdCurieMap = new HashMap<>();

		for (ImmutablePair<Gff3DTO, Map<String, String>> gffEntryPair : gffData) {
			Map<String, String> attributes = gffEntryPair.getValue();
			if (attributes.containsKey("ID") && attributes.containsKey("gene_id")) {
				geneIdCurieMap.put(attributes.get("ID"), attributes.get("gene_id"));
			}
		}

		return geneIdCurieMap;
	}

}
