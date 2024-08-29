package org.alliancegenome.curation_api.jobs.executors;

import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.AGM;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.AGM_DISEASE_ANNOTATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.ALLELE;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.ALLELE_ASSOCIATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.ALLELE_DISEASE_ANNOTATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.CONSTRUCT;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.CONSTRUCT_ASSOCIATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.DISEASE_ANNOTATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.FULL_INGEST;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.GENE;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.GENE_DISEASE_ANNOTATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.VARIANT;

import java.util.List;

import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.enums.BackendBulkLoadType;
import org.alliancegenome.curation_api.jobs.executors.associations.alleleAssociations.AlleleGeneAssociationExecutor;
import org.alliancegenome.curation_api.jobs.executors.associations.constructAssociations.ConstructGenomicEntityAssociationExecutor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class BulkLoadJobExecutor {

	@Inject BulkLoadFileDAO bulkLoadFileDAO;

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
	@Inject VariantExecutor variantExecutor;
	@Inject AlleleGeneAssociationExecutor alleleGeneAssociationExecutor;
	@Inject ConstructGenomicEntityAssociationExecutor constructGenomicEntityAssociationExecutor;
	@Inject PhenotypeAnnotationExecutor phenotypeAnnotationExecutor;
	@Inject GeneMolecularInteractionExecutor geneMolecularInteractionExecutor;
	@Inject GeneGeneticInteractionExecutor geneGeneticInteractionExecutor;
	@Inject ParalogyExecutor paralogyExecutor;
	@Inject GeneExpressionExecutor geneExpressionExecutor;
	@Inject SequenceTargetingReagentExecutor sqtrExecutor;
	
	@Inject Gff3ExonExecutor gff3ExonExecutor;
	@Inject Gff3CDSExecutor gff3CDSExecutor;
	@Inject Gff3TranscriptExecutor gff3TranscriptExecutor;
	
	@Inject HTPExpressionDatasetAnnotationExecutor htpExpressionDatasetAnnotationExecutor;

	public void process(BulkLoadFileHistory bulkLoadFileHistory, Boolean cleanUp) throws Exception {

		BackendBulkLoadType loadType = bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType();

		List<BackendBulkLoadType> ingestTypes = List.of(AGM_DISEASE_ANNOTATION, ALLELE_DISEASE_ANNOTATION, GENE_DISEASE_ANNOTATION, DISEASE_ANNOTATION, AGM, ALLELE, GENE, VARIANT, CONSTRUCT, FULL_INGEST, ALLELE_ASSOCIATION, CONSTRUCT_ASSOCIATION);

		if (ingestTypes.contains(loadType)) {

			bulkLoadFileHistory.getBulkLoadFile().setRecordCount(0);
			bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

			if (loadType == AGM || loadType == FULL_INGEST) {
				agmExecutor.execLoad(bulkLoadFileHistory, cleanUp);
			}
			if (loadType == ALLELE || loadType == FULL_INGEST) {
				alleleExecutor.execLoad(bulkLoadFileHistory, cleanUp);
			}
			if (loadType == GENE || loadType == FULL_INGEST) {
				geneExecutor.execLoad(bulkLoadFileHistory, cleanUp);
			}
			if (loadType == CONSTRUCT || loadType == FULL_INGEST) {
				constructExecutor.execLoad(bulkLoadFileHistory, cleanUp);
			}
			if (loadType == VARIANT || loadType == FULL_INGEST) {
				variantExecutor.execLoad(bulkLoadFileHistory, cleanUp);
			}
			if (loadType == ALLELE_DISEASE_ANNOTATION || loadType == DISEASE_ANNOTATION || loadType == FULL_INGEST) {
				alleleDiseaseAnnotationExecutor.execLoad(bulkLoadFileHistory, cleanUp);
			}
			if (loadType == AGM_DISEASE_ANNOTATION || loadType == DISEASE_ANNOTATION || loadType == FULL_INGEST) {
				agmDiseaseAnnotationExecutor.execLoad(bulkLoadFileHistory, cleanUp);
			}
			if (loadType == GENE_DISEASE_ANNOTATION || loadType == DISEASE_ANNOTATION || loadType == FULL_INGEST) {
				geneDiseaseAnnotationExecutor.execLoad(bulkLoadFileHistory, cleanUp);
			}
			if (loadType == ALLELE_ASSOCIATION || loadType == FULL_INGEST) {
				alleleGeneAssociationExecutor.execLoad(bulkLoadFileHistory, cleanUp);
			}
			if (loadType == CONSTRUCT_ASSOCIATION || loadType == FULL_INGEST) {
				constructGenomicEntityAssociationExecutor.execLoad(bulkLoadFileHistory, cleanUp);
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
			
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.GFF_EXON) {
			gff3ExonExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.GFF_CDS) {
			gff3CDSExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.GFF_TRANSCRIPT) {
			gff3TranscriptExecutor.execLoad(bulkLoadFileHistory);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.GFF_EXON_LOCATION) {
			//gff3Executor.execLoad(bulkLoadFile);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.GFF_CDS_LOCATION) {
			//gff3Executor.execLoad(bulkLoadFile);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.GFF_TRANSCRIPT_LOCATION) {
			//gff3Executor.execLoad(bulkLoadFile);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.GFF_TRANSCRIPT_GENE) {
			//gff3Executor.execLoad(bulkLoadFile);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.GFF_TRANSCRIPT_EXON) {
			//gff3Executor.execLoad(bulkLoadFile);
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.GFF_TRANSCRIPT_CDS) {
			//gff3Executor.execLoad(bulkLoadFile);
			
		} else if (bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.HTPDATASET) {
			htpExpressionDatasetAnnotationExecutor.execLoad(bulkLoadFileHistory);
		} else {
			log.info("Load: " + bulkLoadFileHistory.getBulkLoad().getName() + " for type " + bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() + " not implemented");
			throw new Exception("Load: " + bulkLoadFileHistory.getBulkLoad().getName() + " for type " + bulkLoadFileHistory.getBulkLoad().getBackendBulkLoadType() + " not implemented");
		}
		log.info("Process Finished for: " + bulkLoadFileHistory.getBulkLoad().getName());
	}
}
