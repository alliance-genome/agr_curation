package org.alliancegenome.curation_api.jobs.executors;

import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.AGM;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.AGM_AGM_ASSOCIATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.AGM_ASSOCIATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.AGM_DISEASE_ANNOTATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.ALLELE;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.ALLELE_ASSOCIATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.ALLELE_DISEASE_ANNOTATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.ANTIBODY;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.CASSETTE;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.CASSETTE_GENOMIC_ENTITY_ASSOCIATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.CASSETTE_STR_ASSOCIATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.CASSETTE_TRANSGENIC_TOOL_ASSOCIATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.CONSTRUCT;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.CONSTRUCT_ASSOCIATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.CONSTRUCT_CASSETTE_ASSOCIATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.TRANSGENIC_TOOL;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.DISEASE_ANNOTATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.FULL_INGEST;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.GENE;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.GENE_DISEASE_ANNOTATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.VARIANT;

import java.util.List;
import java.util.Set;

import org.alliancegenome.curation_api.enums.BackendBulkLoadType;
import org.alliancegenome.curation_api.jobs.executors.associations.AgmAgmAssociationExecutor;
import org.alliancegenome.curation_api.jobs.executors.associations.CassetteGenomicEntityAssociationExecutor;
import org.alliancegenome.curation_api.jobs.executors.associations.CassetteStrAssociationExecutor;
import org.alliancegenome.curation_api.jobs.executors.associations.CassetteTransgenicToolAssociationExecutor;
import org.alliancegenome.curation_api.jobs.executors.associations.ConstructCassetteAssociationExecutor;
import org.alliancegenome.curation_api.jobs.executors.associations.AgmAlleleAssociationExecutor;
import org.alliancegenome.curation_api.jobs.executors.associations.AgmStrAssociationExecutor;
import org.alliancegenome.curation_api.jobs.executors.associations.AlleleConstructAssociationExecutor;
import org.alliancegenome.curation_api.jobs.executors.associations.AlleleGeneAssociationExecutor;
import org.alliancegenome.curation_api.jobs.executors.associations.ConstructGenomicEntityAssociationExecutor;
import org.alliancegenome.curation_api.jobs.executors.gff.Gff3CDSExecutor;
import org.alliancegenome.curation_api.jobs.executors.gff.Gff3ExonExecutor;
import org.alliancegenome.curation_api.jobs.executors.gff.Gff3GeneExecutor;
import org.alliancegenome.curation_api.jobs.executors.gff.Gff3TranscriptExecutor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class BulkLoadJobExecutor {

	@Inject IngestFileReader ingestFileReader;

	@Inject AlleleDiseaseAnnotationExecutor alleleDiseaseAnnotationExecutor;
	@Inject AgmDiseaseAnnotationExecutor agmDiseaseAnnotationExecutor;
	@Inject GeneDiseaseAnnotationExecutor geneDiseaseAnnotationExecutor;
	@Inject GeneExecutor geneExecutor;
	@Inject AlleleExecutor alleleExecutor;
	@Inject AgmExecutor agmExecutor;
	@Inject MoleculeExecutor moleculeExecutor;
	@Inject ResourceDescriptorExecutor resourceDescriptorExecutor;
	@Inject OrthologyExecutor orthologyExecutor;
	@Inject OntologyExecutor ontologyExecutor;
	@Inject ConstructExecutor constructExecutor;
	@Inject CassetteExecutor cassetteExecutor;
	@Inject TransgenicToolExecutor transgenicToolExecutor;
	@Inject ConstructCassetteAssociationExecutor constructCassetteAssociationExecutor;
	@Inject CassetteGenomicEntityAssociationExecutor cassetteGenomicEntityAssociationExecutor;
	@Inject CassetteTransgenicToolAssociationExecutor cassetteTransgenicToolAssociationExecutor;
	@Inject CassetteStrAssociationExecutor cassetteStrAssociationExecutor;
	@Inject AntibodyExecutor antibodyExecutor;
	@Inject AlleleGeneAssociationExecutor alleleGeneAssociationExecutor;
	@Inject AlleleConstructAssociationExecutor alleleConstructAssociationExecutor;
	@Inject ConstructGenomicEntityAssociationExecutor constructGenomicEntityAssociationExecutor;
	@Inject AgmStrAssociationExecutor agmStrAssociationExecutor;
	@Inject AgmAlleleAssociationExecutor agmAlleleAssociationExecutor;
	@Inject
	AgmAgmAssociationExecutor agmAgmAssociationExecutor;
	@Inject PhenotypeAnnotationExecutor phenotypeAnnotationExecutor;
	@Inject GeneMolecularInteractionExecutor geneMolecularInteractionExecutor;
	@Inject GeneGeneticInteractionExecutor geneGeneticInteractionExecutor;
	@Inject ParalogyExecutor paralogyExecutor;
	@Inject GeneExpressionExecutor geneExpressionExecutor;
	@Inject SequenceTargetingReagentExecutor sqtrExecutor;
	@Inject VariantFmsExecutor variantFmsExecutor;
	@Inject HTPExpressionDatasetAnnotationExecutor htpExpressionDatasetAnnotationExecutor;
	@Inject HTPExpressionDatasetSampleAnnotationExecutor htpExpressionDatasetSampleAnnotationExecutor;
	@Inject GeoXrefExecutor geoXrefExecutor;

	@Inject Gff3ExonExecutor gff3ExonExecutor;
	@Inject Gff3CDSExecutor gff3CDSExecutor;
	@Inject Gff3GeneExecutor gff3GeneExecutor;
	@Inject Gff3TranscriptExecutor gff3TranscriptExecutor;
	@Inject VepTranscriptExecutor vepTranscriptExecutor;
	@Inject VepGeneExecutor vepGeneExecutor;

	@Inject ExpressionAtlasExecutor expressionAtlasExecutor;
	@Inject
	GeneOntologyAnnotationExecutor gafExecutor;

	@Inject BiogridOrcExecutor biogridOrcExecutor;
	@Inject GoogleAnalyticsExecutor googleAnalyticsExecutor;

	public void process(BulkLoadFileHistory bulkLoadFileHistory, Boolean cleanUp) throws Exception {

		BackendBulkLoadType loadType = bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType();

		List<BackendBulkLoadType> ingestTypes = List.of(AGM_DISEASE_ANNOTATION, ALLELE_DISEASE_ANNOTATION, GENE_DISEASE_ANNOTATION, DISEASE_ANNOTATION, AGM, ALLELE, GENE, VARIANT, CONSTRUCT, ANTIBODY, CASSETTE, TRANSGENIC_TOOL, FULL_INGEST, ALLELE_ASSOCIATION, AGM_ASSOCIATION, AGM_AGM_ASSOCIATION, CONSTRUCT_ASSOCIATION, CONSTRUCT_CASSETTE_ASSOCIATION, CASSETTE_GENOMIC_ENTITY_ASSOCIATION, CASSETTE_TRANSGENIC_TOOL_ASSOCIATION, CASSETTE_STR_ASSOCIATION);

		if (ingestTypes.contains(loadType)) {

			// Parsed once and shared, so a FULL_INGEST or CONSTRUCT load that fans out to several
			// executors reads the file once instead of once per executor.
			IngestDTO ingestDto = ingestFileReader.read(bulkLoadFileHistory);
			if (ingestDto == null) {
				// readIngestFile has already failed the load with the parse error
				return;
			}

			Set<String> ingestSets = IngestFileReader.presentIngestSets(ingestDto);
			if (fansOut(loadType)) {
				log.info("Ingest sets found in " + bulkLoadFileHistory.getBulkLoad().getName() + ": " + ingestSets);
				if (ingestSets.isEmpty()) {
					// Previously this ran all eighteen executors, each returning early, and
					// reported success having loaded nothing.
					log.info("Load: " + bulkLoadFileHistory.getBulkLoad().getName() + " contains no recognised ingest set");
					throw new Exception("Load: " + bulkLoadFileHistory.getBulkLoad().getName() + " contains no recognised ingest set");
				}
			}

			if (loadType == AGM || fileCarriesIngestSet(loadType, ingestSets, "agm_ingest_set")) {
				agmExecutor.execLoad(bulkLoadFileHistory, ingestDto, cleanUp);
			}
			if (loadType == ALLELE || fileCarriesIngestSet(loadType, ingestSets, "allele_ingest_set")) {
				alleleExecutor.execLoad(bulkLoadFileHistory, ingestDto, cleanUp);
			}
			if (loadType == GENE || fileCarriesIngestSet(loadType, ingestSets, "gene_ingest_set")) {
				geneExecutor.execLoad(bulkLoadFileHistory, ingestDto, cleanUp);
			}
			if (fileCarriesIngestSet(loadType, ingestSets, "construct_ingest_set")) {
				constructExecutor.execLoad(bulkLoadFileHistory, ingestDto, cleanUp);
			}
			if (loadType == TRANSGENIC_TOOL || fileCarriesIngestSet(loadType, ingestSets, "transgenic_tool_ingest_set")) {
				transgenicToolExecutor.execLoad(bulkLoadFileHistory, ingestDto, cleanUp);
			}
			if (loadType == CASSETTE || fileCarriesIngestSet(loadType, ingestSets, "cassette_ingest_set")) {
				cassetteExecutor.execLoad(bulkLoadFileHistory, ingestDto, cleanUp);
			}
			if (loadType == ANTIBODY || fileCarriesIngestSet(loadType, ingestSets, "antibody_ingest_set")) {
				antibodyExecutor.execLoad(bulkLoadFileHistory, ingestDto, cleanUp);
			}
			if (loadType == VARIANT || fileCarriesIngestSet(loadType, ingestSets, "variant_ingest_set")) {
				// TODO: re-enable once accepting direct submissions of variants by DQMs again and FMS load turned off
				// variantExecutor.execLoad(bulkLoadFileHistory, ingestDto, cleanUp);
			}
			if (loadType == ALLELE_DISEASE_ANNOTATION || loadType == DISEASE_ANNOTATION || fileCarriesIngestSet(loadType, ingestSets, "disease_allele_ingest_set")) {
				alleleDiseaseAnnotationExecutor.execLoad(bulkLoadFileHistory, ingestDto, cleanUp);
			}
			if (loadType == AGM_DISEASE_ANNOTATION || loadType == DISEASE_ANNOTATION || fileCarriesIngestSet(loadType, ingestSets, "disease_agm_ingest_set")) {
				agmDiseaseAnnotationExecutor.execLoad(bulkLoadFileHistory, ingestDto, cleanUp);
			}
			if (loadType == GENE_DISEASE_ANNOTATION || loadType == DISEASE_ANNOTATION || fileCarriesIngestSet(loadType, ingestSets, "disease_gene_ingest_set")) {
				geneDiseaseAnnotationExecutor.execLoad(bulkLoadFileHistory, ingestDto, cleanUp);
			}
			// Two executors behind one load type, so each is gated on its own ingest set.
			if (loadType == ALLELE_ASSOCIATION || fileCarriesIngestSet(loadType, ingestSets, "allele_gene_association_ingest_set")) {
				alleleGeneAssociationExecutor.execLoad(bulkLoadFileHistory, ingestDto, cleanUp);
			}
			if (loadType == ALLELE_ASSOCIATION || fileCarriesIngestSet(loadType, ingestSets, "allele_construct_association_ingest_set")) {
				alleleConstructAssociationExecutor.execLoad(bulkLoadFileHistory, ingestDto, cleanUp);
			}
			if (loadType == CONSTRUCT_ASSOCIATION || fileCarriesIngestSet(loadType, ingestSets, "construct_genomic_entity_association_ingest_set")) {
				constructGenomicEntityAssociationExecutor.execLoad(bulkLoadFileHistory, ingestDto, cleanUp);
			}
			if (loadType == CONSTRUCT_CASSETTE_ASSOCIATION || fileCarriesIngestSet(loadType, ingestSets, "construct_cassette_association_ingest_set")) {
				constructCassetteAssociationExecutor.execLoad(bulkLoadFileHistory, ingestDto, cleanUp);
			}
			if (loadType == CASSETTE_GENOMIC_ENTITY_ASSOCIATION || fileCarriesIngestSet(loadType, ingestSets, "cassette_genomic_entity_association_ingest_set")) {
				cassetteGenomicEntityAssociationExecutor.execLoad(bulkLoadFileHistory, ingestDto, cleanUp);
			}
			if (loadType == CASSETTE_TRANSGENIC_TOOL_ASSOCIATION || fileCarriesIngestSet(loadType, ingestSets, "cassette_transgenic_tool_association_ingest_set")) {
				cassetteTransgenicToolAssociationExecutor.execLoad(bulkLoadFileHistory, ingestDto, cleanUp);
			}
			if (loadType == CASSETTE_STR_ASSOCIATION || fileCarriesIngestSet(loadType, ingestSets, "cassette_str_association_ingest_set")) {
				cassetteStrAssociationExecutor.execLoad(bulkLoadFileHistory, ingestDto, cleanUp);
			}
			// The AGM/STR set is named for sequence_targeting_reagent in the schema, not for
			// the agmStr abbreviation the Java field uses.
			if (loadType == AGM_ASSOCIATION || fileCarriesIngestSet(loadType, ingestSets, "agm_sequence_targeting_reagent_association_ingest_set")) {
				agmStrAssociationExecutor.execLoad(bulkLoadFileHistory, ingestDto, cleanUp);
			}
			if (loadType == AGM_ASSOCIATION || fileCarriesIngestSet(loadType, ingestSets, "agm_allele_association_ingest_set")) {
				agmAlleleAssociationExecutor.execLoad(bulkLoadFileHistory, ingestDto, cleanUp);
			}
			if (loadType == AGM_ASSOCIATION || fileCarriesIngestSet(loadType, ingestSets, "agm_agm_association_ingest_set")) {
				agmAgmAssociationExecutor.execLoad(bulkLoadFileHistory, ingestDto, cleanUp);
			}

		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.MOLECULE) {
			moleculeExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.SEQUENCE_TARGETING_REAGENT) {
			sqtrExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.INTERACTION_MOL) {
			geneMolecularInteractionExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.INTERACTION_GEN) {
			geneGeneticInteractionExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.PHENOTYPE) {
			phenotypeAnnotationExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.ORTHOLOGY) {
			orthologyExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.PARALOGY) {
			paralogyExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.ONTOLOGY) {
			ontologyExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.RESOURCE_DESCRIPTOR) {
			resourceDescriptorExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.EXPRESSION) {
			geneExpressionExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.VARIATION) {
			variantFmsExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.GFF_EXON) {
			gff3ExonExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.GFF_CDS) {
			gff3CDSExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.GFF_TRANSCRIPT) {
			gff3TranscriptExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.GFF_GENE) {
			gff3GeneExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.HTPDATASET) {
			htpExpressionDatasetAnnotationExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.EXPRESSION_ATLAS) {
			expressionAtlasExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.GEOXREF) {
			geoXrefExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.BIOGRID_ORCS) {
			biogridOrcExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.VEPTRANSCRIPT) {
			vepTranscriptExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.VEPGENE) {
			vepGeneExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.HTPDATASAMPLE) {
			htpExpressionDatasetSampleAnnotationExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.GAF) {
			gafExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.GOOGLE_ANALYTICS) {
			googleAnalyticsExecutor.execLoad(bulkLoadFileHistory);
		} else {
			log.info("Load: " + bulkLoadFileHistory.getBulkLoad().getName() + " for type " + bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() + " not implemented");
			throw new Exception("Load: " + bulkLoadFileHistory.getBulkLoad().getName() + " for type " + bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() + " not implemented");
		}
		log.info("Process Finished for: " + bulkLoadFileHistory.getBulkLoad().getName());
	}

	/**
	 * The constructs model is submitted as a family of files that share one load type: a
	 * CONSTRUCT_&lt;MOD&gt; submission may carry constructs, cassettes, transgenic tools or any of
	 * their associations. Listing the sets it owns keeps CONSTRUCT from straying into the rest
	 * of a FULL_INGEST when the two overlap in a single file.
	 *
	 * Two sets the MODs ship are deliberately absent because nothing can read them yet:
	 * str_ingest_set, which has no LinkML DTO (only the FMS SequenceTargetingReagent path),
	 * and transgenic_tool_transgenic_tool_association_ingest_set, which is still unimplemented.
	 */
	private static final Set<String> CONSTRUCT_MODEL_INGEST_SETS = Set.of(
		"construct_ingest_set",
		"cassette_ingest_set",
		"transgenic_tool_ingest_set",
		"construct_cassette_association_ingest_set",
		"construct_genomic_entity_association_ingest_set",
		"cassette_genomic_entity_association_ingest_set",
		"cassette_transgenic_tool_association_ingest_set",
		"cassette_str_association_ingest_set");

	/** Load types that carry more than one kind of entity and so are dispatched by file content. */
	boolean fansOut(BackendBulkLoadType loadType) {
		return loadType == FULL_INGEST || loadType == CONSTRUCT;
	}

	/**
	 * Whether the file of a content dispatched load carries {@code ingestSetName}, and the load
	 * type owns that set, so its executor should run. The single type loads keep their own
	 * explicit check alongside this, so an operator submitting CASSETTE still gets the cassette
	 * executor either way.
	 */
	boolean fileCarriesIngestSet(BackendBulkLoadType loadType, Set<String> ingestSets, String ingestSetName) {
		if (!fansOut(loadType)) {
			return false;
		}
		if (loadType == CONSTRUCT && !CONSTRUCT_MODEL_INGEST_SETS.contains(ingestSetName)) {
			return false;
		}
		return ingestSets.contains(ingestSetName);
	}
}
