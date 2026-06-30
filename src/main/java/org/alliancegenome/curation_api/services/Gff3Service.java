package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alliancegenome.curation_api.constants.Gff3Constants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CodingSequenceDAO;
import org.alliancegenome.curation_api.dao.ExonDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.GenomeAssemblyDAO;
import org.alliancegenome.curation_api.dao.TranscriptDAO;
import org.alliancegenome.curation_api.dao.associations.CodingSequenceGenomicLocationAssociationDAO;
import org.alliancegenome.curation_api.dao.associations.ExonGenomicLocationAssociationDAO;
import org.alliancegenome.curation_api.dao.associations.TranscriptCodingSequenceAssociationDAO;
import org.alliancegenome.curation_api.dao.associations.TranscriptExonAssociationDAO;
import org.alliancegenome.curation_api.dao.associations.TranscriptGeneAssociationDAO;
import org.alliancegenome.curation_api.dao.associations.TranscriptGenomicLocationAssociationDAO;
import org.alliancegenome.curation_api.dao.ontology.SoTermDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileExceptionDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.KnownIssueValidationException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.CodingSequence;
import org.alliancegenome.curation_api.model.entities.Exon;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Transcript;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.associations.CodingSequenceGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.entities.associations.ExonGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.entities.associations.GeneGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.entities.associations.TranscriptCodingSequenceAssociation;
import org.alliancegenome.curation_api.model.entities.associations.TranscriptExonAssociation;
import org.alliancegenome.curation_api.model.entities.associations.TranscriptGeneAssociation;
import org.alliancegenome.curation_api.model.entities.associations.TranscriptGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileException;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.associations.CodingSequenceGenomicLocationAssociationService;
import org.alliancegenome.curation_api.services.associations.ExonGenomicLocationAssociationService;
import org.alliancegenome.curation_api.services.associations.GeneGenomicLocationAssociationService;
import org.alliancegenome.curation_api.services.associations.TranscriptCodingSequenceAssociationService;
import org.alliancegenome.curation_api.services.associations.TranscriptExonAssociationService;
import org.alliancegenome.curation_api.services.associations.TranscriptGeneAssociationService;
import org.alliancegenome.curation_api.services.associations.TranscriptGenomicLocationAssociationService;
import org.alliancegenome.curation_api.services.helpers.Gff3AttributesHelper;
import org.alliancegenome.curation_api.services.helpers.Gff3UniqueIdHelper;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.services.validation.dto.Gff3DtoValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.transaction.Transactional;

@RequestScoped
public class Gff3Service {

	@Inject GenomeAssemblyDAO genomeAssemblyDAO;
	@Inject ExonDAO exonDAO;
	@Inject CodingSequenceDAO cdsDAO;
	@Inject TranscriptDAO transcriptDAO;
	@Inject GeneDAO geneDAO;
	@Inject BulkLoadFileExceptionDAO bulkLoadFileExceptionDAO;
	@Inject ExonGenomicLocationAssociationDAO exonLocationDAO;
	@Inject TranscriptExonAssociationDAO transcriptExonDAO;
	@Inject CodingSequenceGenomicLocationAssociationDAO cdsLocationDAO;
	@Inject TranscriptCodingSequenceAssociationDAO transcriptCdsDAO;
	@Inject TranscriptGenomicLocationAssociationDAO transcriptLocationDAO;
	@Inject TranscriptGeneAssociationDAO transcriptGeneDAO;
	@Inject SoTermDAO soTermDAO;
	@Inject ExonGenomicLocationAssociationService exonLocationService;
	@Inject CodingSequenceGenomicLocationAssociationService cdsLocationService;
	@Inject TranscriptGenomicLocationAssociationService transcriptLocationService;
	@Inject GeneGenomicLocationAssociationService geneLocationService;
	@Inject TranscriptGeneAssociationService transcriptGeneService;
	@Inject TranscriptCodingSequenceAssociationService transcriptCdsService;
	@Inject TranscriptExonAssociationService transcriptExonService;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject OrganizationService organizationService;
	@Inject Gff3DtoValidator gff3DtoValidator;
	@Inject GeneService geneService;
	@Inject EntityManager entityManager;

	private Map<String, Transcript> transcriptCache = new HashMap<>();

	@Transactional
	public void loadExonLocationAssociations(ImmutablePair<Gff3DTO, Map<String, String>> gffEntryPair, List<Long> idsAdded, BackendBulkDataProvider dataProvider, String assemblyId) throws ValidationException {
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
	public void loadCDSLocationAssociations(ImmutablePair<Gff3DTO, Map<String, String>> gffEntryPair, List<Long> idsAdded, BackendBulkDataProvider dataProvider, String assemblyId) throws ValidationException {
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
	public void loadTranscriptLocationAssociations(ImmutablePair<Gff3DTO, Map<String, String>> gffEntryPair, List<Long> idsAdded, BackendBulkDataProvider dataProvider, String assemblyId) throws ValidationException {
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
	public void loadGeneLocationAssociations(ImmutablePair<Gff3DTO, Map<String, String>> gffEntryPair, List<Long> idsAdded, BackendBulkDataProvider dataProvider, String assemblyId) throws ValidationException {
		Gff3DTO gffEntry = gffEntryPair.getKey();
		Map<String, String> attributes = gffEntryPair.getValue();
		if (StringUtils.isBlank(assemblyId)) {
			throw new ObjectValidationException(gffEntry, "Cannot load associations without assembly");
		}

		if (!Gff3Constants.GENE_TYPES.contains(gffEntry.getType())) {
			throw new ObjectValidationException(gffEntry, "Invalid Type: " + gffEntry.getType() + " for Gene Location");
		}

		String geneCurie;
		String identifyingAttribute;
		if (attributes.containsKey("gene_id")) {
			geneCurie = attributes.get("gene_id");
			identifyingAttribute = "gene_id";
		} else if (attributes.containsKey("ID")) {
			geneCurie = attributes.get("ID");
			identifyingAttribute = "ID";
		} else {
			throw new ObjectValidationException(gffEntry, "attributes - ID - " + ValidationConstants.REQUIRED_MESSAGE);
		}
		
		Gene gene = geneService.findByIdentifierString(geneCurie);
		if (gene == null) {
			// SCRUM-6205: RefSeq-shaped GFFs (e.g. ZFIN GRCz12tu) carry the MOD curie only in
			// Dbxref; the prefixed gene_id/ID match nothing. Fall back to the provider-scoped
			// Dbxref curie before skipping. Existing files resolve above and never reach here.
			String dbxrefCurie = Gff3AttributesHelper.getModCurieFromDbxref(attributes, dataProvider);
			if (dbxrefCurie != null) {
				gene = geneService.findByIdentifierString(dbxrefCurie);
			}
		}
		if (gene == null) {
			throw new KnownIssueValidationException(ValidationConstants.UNRECOGNIZED_MESSAGE + " (" + attributes.get(identifyingAttribute) + ")");
		}

		GeneGenomicLocationAssociation geneLocation = gff3DtoValidator.validateGeneLocation(gffEntry, gene, assemblyId, dataProvider);
		if (geneLocation != null) {
			idsAdded.add(geneLocation.getId());
			geneLocationService.addAssociationToSubject(geneLocation);
		}
	}

	@Transactional
	public void loadExonParentChildAssociations(ImmutablePair<Gff3DTO, Map<String, String>> gffEntryPair, List<Long> idsAdded, BackendBulkDataProvider dataProvider) throws ValidationException {
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
	public void loadCDSParentChildAssociations(ImmutablePair<Gff3DTO, Map<String, String>> gffEntryPair, List<Long> idsAdded, BackendBulkDataProvider dataProvider) throws ValidationException {
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
	public void loadGeneParentChildAssociations(ImmutablePair<Gff3DTO, Map<String, String>> gffEntryPair, List<Long> idsAdded, BackendBulkDataProvider dataProvider, Map<String, String> geneIdCurieMap) throws ValidationException {
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

	@Transactional
	public void loadExonBatch(
			List<ImmutablePair<Gff3DTO, Map<String, String>>> batch,
			List<Long> entityIdsAdded,
			List<Long> locationIdsAdded,
			List<Long> associationIdsAdded,
			BackendBulkDataProvider dataProvider,
			String assemblyId,
			BulkLoadFileHistory history,
			ProcessDisplayHelper ph) {

		entityManager.setFlushMode(FlushModeType.COMMIT);

		// Batch-load existing exons by uniqueId
		Set<String> uniqueIds = new HashSet<>();
		Set<String> parentIds = new HashSet<>();
		for (ImmutablePair<Gff3DTO, Map<String, String>> pair : batch) {
			uniqueIds.add(Gff3UniqueIdHelper.getExonOrCodingSequenceUniqueId(pair.getKey(), pair.getValue(), dataProvider));
			String parent = pair.getValue().get("Parent");
			if (parent != null && !transcriptCache.containsKey(parent)) {
				parentIds.add(parent);
			}
		}
		Map<String, Exon> exonMap = exonDAO.findByUniqueIds(uniqueIds);

		// Batch-load transcripts not yet cached
		if (!parentIds.isEmpty()) {
			transcriptCache.putAll(transcriptDAO.findByModInternalIds(parentIds));
		}

		// Batch-load existing associations for existing exons
		Set<Long> existingExonIds = new HashSet<>();
		for (Exon exon : exonMap.values()) {
			if (exon.getId() != null) {
				existingExonIds.add(exon.getId());
			}
		}
		Map<Long, ExonGenomicLocationAssociation> locationAssocMap = assemblyId != null
			? exonLocationDAO.findByExonIdsAndAssembly(existingExonIds, assemblyId)
			: new HashMap<>();
		Map<Long, TranscriptExonAssociation> transcriptExonAssocMap = transcriptExonDAO.findByExonIds(existingExonIds);

		for (ImmutablePair<Gff3DTO, Map<String, String>> pair : batch) {
			Gff3DTO gffEntry = pair.getKey();
			Map<String, String> attributes = pair.getValue();
			String uniqueId = Gff3UniqueIdHelper.getExonOrCodingSequenceUniqueId(gffEntry, attributes, dataProvider);

			// Phase 1: Entity
			Exon exon = null;
			try {
				if (!StringUtils.equals(gffEntry.getType(), "exon") && !StringUtils.equals(gffEntry.getType(), "noncoding_exon")) {
					throw new ObjectValidationException(gffEntry, "Invalid Type: " + gffEntry.getType() + " for Exon Entity");
				}

				exon = exonMap.get(uniqueId);
				if (exon == null) {
					exon = new Exon();
					exon.setUniqueId(uniqueId);
				}

				if (attributes.containsKey("Name")) {
					exon.setName(attributes.get("Name"));
				}

				exon.setDataProvider(organizationService.getByAbbr(dataProvider.sourceOrganization).getEntity());
				exon.setTaxon(ncbiTaxonTermService.getByCurie(dataProvider.canonicalTaxonCurie).getEntity());

				exon = exonDAO.persist(exon);
				entityIdsAdded.add(exon.getId());
				history.incrementCompleted("Entities");
			} catch (ObjectUpdateException e) {
				history.incrementFailed("Entities");
				addBatchException(history, e.getData());
				exon = null;
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed("Entities");
				addBatchException(history, new ObjectUpdateExceptionData(gffEntry, e.getMessage(), e.getStackTrace()));
				exon = null;
			}

			// Phase 2: Location
			if (exon != null && assemblyId != null) {
				try {
					ExonGenomicLocationAssociation existingLocation = locationAssocMap.get(exon.getId());
					ExonGenomicLocationAssociation exonLocation = gff3DtoValidator.validateExonLocation(gffEntry, exon, assemblyId, dataProvider, existingLocation);
					if (exonLocation != null) {
						locationIdsAdded.add(exonLocation.getId());
					}
					history.incrementCompleted("Locations");
				} catch (ObjectUpdateException e) {
					history.incrementFailed("Locations");
					addBatchException(history, e.getData());
				} catch (Exception e) {
					e.printStackTrace();
					history.incrementFailed("Locations");
					addBatchException(history, new ObjectUpdateExceptionData(gffEntry, e.getMessage(), e.getStackTrace()));
				}
			}

			// Phase 3: Transcript-Exon Association
			if (exon != null) {
				try {
					if (!attributes.containsKey("Parent")) {
						throw new ObjectValidationException(gffEntry, "Attributes - Parent - " + ValidationConstants.REQUIRED_MESSAGE);
					}
					Transcript parentTranscript = transcriptCache.get(attributes.get("Parent"));
					if (parentTranscript == null) {
						throw new ObjectValidationException(gffEntry, "Attributes - Parent - " + ValidationConstants.INVALID_MESSAGE + " (" + attributes.get("Parent") + ")");
					}

					TranscriptExonAssociation existingAssoc = transcriptExonAssocMap.get(exon.getId());
					TranscriptExonAssociation transcriptAssociation = gff3DtoValidator.validateTranscriptExonAssociation(gffEntry, exon, parentTranscript, existingAssoc);
					if (transcriptAssociation != null) {
						associationIdsAdded.add(transcriptAssociation.getId());
					}
					history.incrementCompleted("Associations");
				} catch (ObjectUpdateException e) {
					history.incrementFailed("Associations");
					addBatchException(history, e.getData());
				} catch (Exception e) {
					e.printStackTrace();
					history.incrementFailed("Associations");
					addBatchException(history, new ObjectUpdateExceptionData(gffEntry, e.getMessage(), e.getStackTrace()));
				}
			}

			ph.progressProcess();
		}
	}

	@Transactional
	public void loadCdsBatch(
			List<ImmutablePair<Gff3DTO, Map<String, String>>> batch,
			List<Long> entityIdsAdded,
			List<Long> locationIdsAdded,
			List<Long> associationIdsAdded,
			BackendBulkDataProvider dataProvider,
			String assemblyId,
			BulkLoadFileHistory history,
			ProcessDisplayHelper ph) {

		entityManager.setFlushMode(FlushModeType.COMMIT);

		// Batch-load existing CDS by uniqueId
		Set<String> uniqueIds = new HashSet<>();
		Set<String> parentIds = new HashSet<>();
		for (ImmutablePair<Gff3DTO, Map<String, String>> pair : batch) {
			uniqueIds.add(Gff3UniqueIdHelper.getExonOrCodingSequenceUniqueId(pair.getKey(), pair.getValue(), dataProvider));
			String parent = pair.getValue().get("Parent");
			if (parent != null && !transcriptCache.containsKey(parent)) {
				parentIds.add(parent);
			}
		}
		Map<String, CodingSequence> cdsMap = cdsDAO.findByUniqueIds(uniqueIds);

		// Batch-load transcripts not yet cached
		if (!parentIds.isEmpty()) {
			transcriptCache.putAll(transcriptDAO.findByModInternalIds(parentIds));
		}

		// Batch-load existing associations for existing CDS
		Set<Long> existingCdsIds = new HashSet<>();
		for (CodingSequence cds : cdsMap.values()) {
			if (cds.getId() != null) {
				existingCdsIds.add(cds.getId());
			}
		}
		Map<Long, CodingSequenceGenomicLocationAssociation> locationAssocMap = assemblyId != null
			? cdsLocationDAO.findByCdsIdsAndAssembly(existingCdsIds, assemblyId)
			: new HashMap<>();
		Map<Long, TranscriptCodingSequenceAssociation> transcriptCdsAssocMap = transcriptCdsDAO.findByCdsIds(existingCdsIds);

		for (ImmutablePair<Gff3DTO, Map<String, String>> pair : batch) {
			Gff3DTO gffEntry = pair.getKey();
			Map<String, String> attributes = pair.getValue();
			String uniqueId = Gff3UniqueIdHelper.getExonOrCodingSequenceUniqueId(gffEntry, attributes, dataProvider);

			// Phase 1: Entity
			CodingSequence cds = null;
			try {
				if (!StringUtils.equals(gffEntry.getType(), "CDS")) {
					throw new ObjectValidationException(gffEntry, "Invalid Type: " + gffEntry.getType() + " for CDS Entity");
				}

				cds = cdsMap.get(uniqueId);
				if (cds == null) {
					cds = new CodingSequence();
					cds.setUniqueId(uniqueId);
				}

				if (attributes.containsKey("Name")) {
					cds.setName(attributes.get("Name"));
				}

				cds.setDataProvider(organizationService.getByAbbr(dataProvider.sourceOrganization).getEntity());
				cds.setTaxon(ncbiTaxonTermService.getByCurie(dataProvider.canonicalTaxonCurie).getEntity());

				cds = cdsDAO.persist(cds);
				entityIdsAdded.add(cds.getId());
				history.incrementCompleted("Entities");
			} catch (ObjectUpdateException e) {
				history.incrementFailed("Entities");
				addBatchException(history, e.getData());
				cds = null;
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed("Entities");
				addBatchException(history, new ObjectUpdateExceptionData(gffEntry, e.getMessage(), e.getStackTrace()));
				cds = null;
			}

			// Phase 2: Location
			if (cds != null && assemblyId != null) {
				try {
					CodingSequenceGenomicLocationAssociation existingLocation = locationAssocMap.get(cds.getId());
					CodingSequenceGenomicLocationAssociation cdsLocation = gff3DtoValidator.validateCdsLocation(gffEntry, cds, assemblyId, dataProvider, existingLocation);
					if (cdsLocation != null) {
						locationIdsAdded.add(cdsLocation.getId());
					}
					history.incrementCompleted("Locations");
				} catch (ObjectUpdateException e) {
					history.incrementFailed("Locations");
					addBatchException(history, e.getData());
				} catch (Exception e) {
					e.printStackTrace();
					history.incrementFailed("Locations");
					addBatchException(history, new ObjectUpdateExceptionData(gffEntry, e.getMessage(), e.getStackTrace()));
				}
			}

			// Phase 3: Transcript-CDS Association
			if (cds != null) {
				try {
					if (!attributes.containsKey("Parent")) {
						throw new ObjectValidationException(gffEntry, "Attributes - Parent - " + ValidationConstants.REQUIRED_MESSAGE);
					}
					Transcript parentTranscript = transcriptCache.get(attributes.get("Parent"));
					if (parentTranscript == null) {
						throw new ObjectValidationException(gffEntry, "Attributes - Parent - " + ValidationConstants.INVALID_MESSAGE + " (" + attributes.get("Parent") + ")");
					}

					TranscriptCodingSequenceAssociation existingAssoc = transcriptCdsAssocMap.get(cds.getId());
					TranscriptCodingSequenceAssociation transcriptAssociation = gff3DtoValidator.validateTranscriptCodingSequenceAssociation(gffEntry, cds, parentTranscript, existingAssoc);
					if (transcriptAssociation != null) {
						associationIdsAdded.add(transcriptAssociation.getId());
					}
					history.incrementCompleted("Associations");
				} catch (ObjectUpdateException e) {
					history.incrementFailed("Associations");
					addBatchException(history, e.getData());
				} catch (Exception e) {
					e.printStackTrace();
					history.incrementFailed("Associations");
					addBatchException(history, new ObjectUpdateExceptionData(gffEntry, e.getMessage(), e.getStackTrace()));
				}
			}

			ph.progressProcess();
		}
	}

	private void addBatchException(BulkLoadFileHistory history, ObjectUpdateExceptionData data) {
		BulkLoadFileException exception = new BulkLoadFileException();
		exception.setException(data);
		exception.setBulkLoadFileHistory(history);
		bulkLoadFileExceptionDAO.persist(exception);
	}

	@Transactional
	public void loadTranscriptBatch(
			List<ImmutablePair<Gff3DTO, Map<String, String>>> batch,
			List<Long> entityIdsAdded,
			List<Long> locationIdsAdded,
			List<Long> associationIdsAdded,
			BackendBulkDataProvider dataProvider,
			String assemblyId,
			Map<String, String> geneIdCurieMap,
			BulkLoadFileHistory history,
			ProcessDisplayHelper ph) {

		entityManager.setFlushMode(FlushModeType.COMMIT);

		// Collect modInternalIds for batch transcript lookup
		Set<String> modInternalIds = new HashSet<>();
		Set<String> geneCuries = new HashSet<>();
		for (ImmutablePair<Gff3DTO, Map<String, String>> pair : batch) {
			Map<String, String> attributes = pair.getValue();
			if (attributes.containsKey("ID")) {
				modInternalIds.add(attributes.get("ID"));
			}
			if (attributes.containsKey("Parent")) {
				String geneCurie = geneIdCurieMap.containsKey(attributes.get("Parent"))
					? geneIdCurieMap.get(attributes.get("Parent"))
					: attributes.get("Parent");
				geneCuries.add(geneCurie);
			}
		}

		// Batch-load existing transcripts
		Map<String, Transcript> batchTranscriptMap = transcriptDAO.findByModInternalIds(modInternalIds);
		transcriptCache.putAll(batchTranscriptMap);

		// Batch-load genes
		Map<String, Gene> geneMap = geneDAO.findByIdentifiers(geneCuries);

		// Batch-load existing associations for existing transcripts
		Set<Long> existingTranscriptIds = new HashSet<>();
		for (Transcript t : batchTranscriptMap.values()) {
			if (t.getId() != null) {
				existingTranscriptIds.add(t.getId());
			}
		}
		Map<Long, TranscriptGenomicLocationAssociation> locationAssocMap = assemblyId != null
			? transcriptLocationDAO.findByTranscriptIdsAndAssembly(existingTranscriptIds, assemblyId)
			: new HashMap<>();
		Map<Long, TranscriptGeneAssociation> geneAssocMap = transcriptGeneDAO.findByTranscriptIds(existingTranscriptIds);

		// SOTerm cache for this batch
		Map<String, SOTerm> soTermCache = new HashMap<>();

		for (ImmutablePair<Gff3DTO, Map<String, String>> pair : batch) {
			Gff3DTO gffEntry = pair.getKey();
			Map<String, String> attributes = pair.getValue();

			// Phase 1: Entity
			Transcript transcript = null;
			try {
				if (!Gff3Constants.TRANSCRIPT_TYPES.contains(gffEntry.getType())) {
					throw new ObjectValidationException(gffEntry, "Invalid Type: " + gffEntry.getType() + " for Transcript Entity");
				}

				if (!attributes.containsKey("ID")) {
					throw new ObjectValidationException(gffEntry, "attributes - ID - " + ValidationConstants.REQUIRED_MESSAGE);
				}

				transcript = transcriptCache.get(attributes.get("ID"));
				if (transcript == null) {
					transcript = new Transcript();
					transcript.setModInternalId(attributes.get("ID"));
				}

				SOTerm soTerm = soTermCache.computeIfAbsent(gffEntry.getType(), type -> {
					SearchResponse<SOTerm> soResponse = soTermDAO.findByField("name", type);
					return soResponse != null ? soResponse.getSingleResult() : null;
				});
				transcript.setTranscriptType(soTerm);

				if (attributes.containsKey("Name")) {
					transcript.setName(attributes.get("Name"));
				} else {
					transcript.setName(null);
				}

				if (attributes.containsKey("transcript_id")) {
					transcript.setTranscriptId(attributes.get("transcript_id"));
				} else {
					transcript.setTranscriptId(null);
				}

				transcript.setDataProvider(organizationService.getByAbbr(dataProvider.sourceOrganization).getEntity());
				transcript.setTaxon(ncbiTaxonTermService.getByCurie(dataProvider.canonicalTaxonCurie).getEntity());

				transcript = transcriptDAO.persist(transcript);
				transcriptCache.put(attributes.get("ID"), transcript);
				entityIdsAdded.add(transcript.getId());
				history.incrementCompleted("Entities");
			} catch (ObjectUpdateException e) {
				history.incrementFailed("Entities");
				addBatchException(history, e.getData());
				transcript = null;
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed("Entities");
				addBatchException(history, new ObjectUpdateExceptionData(gffEntry, e.getMessage(), e.getStackTrace()));
				transcript = null;
			}

			// Phase 2: Location
			if (transcript != null && assemblyId != null) {
				try {
					TranscriptGenomicLocationAssociation existingLocation = locationAssocMap.get(transcript.getId());
					TranscriptGenomicLocationAssociation transcriptLocation = gff3DtoValidator.validateTranscriptLocation(gffEntry, transcript, assemblyId, dataProvider, existingLocation);
					if (transcriptLocation != null) {
						locationIdsAdded.add(transcriptLocation.getId());
					}
					history.incrementCompleted("Locations");
				} catch (ObjectUpdateException e) {
					history.incrementFailed("Locations");
					addBatchException(history, e.getData());
				} catch (Exception e) {
					e.printStackTrace();
					history.incrementFailed("Locations");
					addBatchException(history, new ObjectUpdateExceptionData(gffEntry, e.getMessage(), e.getStackTrace()));
				}
			}

			// Phase 3: Gene Association
			if (transcript != null) {
				try {
					if (!attributes.containsKey("Parent")) {
						throw new ObjectValidationException(gffEntry, "Attributes - Parent - " + ValidationConstants.REQUIRED_MESSAGE);
					}
					String geneCurie = geneIdCurieMap.containsKey(attributes.get("Parent"))
						? geneIdCurieMap.get(attributes.get("Parent"))
						: attributes.get("Parent");
					Gene parentGene = geneMap.get(geneCurie);
					if (parentGene == null) {
						throw new ObjectValidationException(gffEntry, "Attributes - Parent - " + ValidationConstants.INVALID_MESSAGE + " (" + attributes.get("Parent") + ")");
					}

					TranscriptGeneAssociation existingAssoc = geneAssocMap.get(transcript.getId());
					TranscriptGeneAssociation geneAssociation = gff3DtoValidator.validateTranscriptGeneAssociation(gffEntry, transcript, parentGene, existingAssoc);
					if (geneAssociation != null) {
						associationIdsAdded.add(geneAssociation.getId());
					}
					history.incrementCompleted("Associations");
				} catch (ObjectUpdateException e) {
					history.incrementFailed("Associations");
					addBatchException(history, e.getData());
				} catch (Exception e) {
					e.printStackTrace();
					history.incrementFailed("Associations");
					addBatchException(history, new ObjectUpdateExceptionData(gffEntry, e.getMessage(), e.getStackTrace()));
				}
			}

			ph.progressProcess();
		}
	}

	public Map<String, String> getGeneIdCurieMap(List<Gff3DTO> gffData, BackendBulkDataProvider dataProvider) {
		Map<String, String> geneIdCurieMap = new HashMap<>();

		for (Gff3DTO gffEntry : gffData) {
			if (Gff3Constants.GENE_TYPES.contains(gffEntry.getType())) {
				Map<String, String> attributes = Gff3AttributesHelper.getAttributes(gffEntry, dataProvider);
				if (attributes.containsKey("gene_id") && attributes.containsKey("ID")) {
					geneIdCurieMap.put(attributes.get("ID"), attributes.get("gene_id"));
				} else if (attributes.containsKey("ID")) {
					// SCRUM-6205: RefSeq genes have no gene_id; map the gene's ID (e.g. gene-rpl24)
					// to the MOD curie from Dbxref so a transcript's Parent resolves to the gene.
					String dbxrefCurie = Gff3AttributesHelper.getModCurieFromDbxref(attributes, dataProvider);
					if (dbxrefCurie != null) {
						geneIdCurieMap.put(attributes.get("ID"), dbxrefCurie);
					}
				}
			}
		}

		return geneIdCurieMap;
	}

}
