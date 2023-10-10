package org.alliancegenome.curation_api.jobs;

import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.AGM;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.AGM_DISEASE_ANNOTATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.ALLELE;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.ALLELE_ASSOCIATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.ALLELE_DISEASE_ANNOTATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.CONSTRUCT;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.DISEASE_ANNOTATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.FULL_INGEST;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.GENE;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.GENE_DISEASE_ANNOTATION;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.enums.BackendBulkLoadType;
import org.alliancegenome.curation_api.jobs.executors.AgmDiseaseAnnotationExecutor;
import org.alliancegenome.curation_api.jobs.executors.AgmExecutor;
import org.alliancegenome.curation_api.jobs.executors.AlleleDiseaseAnnotationExecutor;
import org.alliancegenome.curation_api.jobs.executors.AlleleExecutor;
import org.alliancegenome.curation_api.jobs.executors.AlleleGeneAssociationExecutor;
import org.alliancegenome.curation_api.jobs.executors.ConstructExecutor;
import org.alliancegenome.curation_api.jobs.executors.GeneDiseaseAnnotationExecutor;
import org.alliancegenome.curation_api.jobs.executors.GeneExecutor;
import org.alliancegenome.curation_api.jobs.executors.MoleculeExecutor;
import org.alliancegenome.curation_api.jobs.executors.OntologyExecutor;
import org.alliancegenome.curation_api.jobs.executors.OrthologyExecutor;
import org.alliancegenome.curation_api.jobs.executors.ResourceDescriptorExecutor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;

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
	@Inject AlleleGeneAssociationExecutor alleleGeneAssociationExecutor;

	public void process(BulkLoadFile bulkLoadFile, Boolean cleanUp) throws Exception {

		BackendBulkLoadType loadType = bulkLoadFile.getBulkLoad().getBackendBulkLoadType();

		List<BackendBulkLoadType> ingestTypes = List.of(AGM_DISEASE_ANNOTATION, ALLELE_DISEASE_ANNOTATION, GENE_DISEASE_ANNOTATION, DISEASE_ANNOTATION, AGM, ALLELE, GENE, CONSTRUCT, FULL_INGEST, ALLELE_ASSOCIATION);

		if (ingestTypes.contains(loadType)) {

			bulkLoadFile.setRecordCount(0);
			bulkLoadFileDAO.merge(bulkLoadFile);

			if (loadType == AGM || loadType == FULL_INGEST) {
				agmExecutor.runLoad(bulkLoadFile, cleanUp);
			}
			if (loadType == ALLELE || loadType == FULL_INGEST) {
				alleleExecutor.runLoad(bulkLoadFile, cleanUp);
			}
			if (loadType == GENE || loadType == FULL_INGEST) {
				geneExecutor.runLoad(bulkLoadFile, cleanUp);
			}
			if (loadType == CONSTRUCT || loadType == FULL_INGEST) {
				constructExecutor.runLoad(bulkLoadFile, cleanUp);
			}
			if (loadType == ALLELE_DISEASE_ANNOTATION || loadType == DISEASE_ANNOTATION || loadType == FULL_INGEST) {
				alleleDiseaseAnnotationExecutor.runLoad(bulkLoadFile, cleanUp);
			}
			if (loadType == AGM_DISEASE_ANNOTATION || loadType == DISEASE_ANNOTATION || loadType == FULL_INGEST) {
				agmDiseaseAnnotationExecutor.runLoad(bulkLoadFile, cleanUp);
			}
			if (loadType == GENE_DISEASE_ANNOTATION || loadType == DISEASE_ANNOTATION || loadType == FULL_INGEST) {
				geneDiseaseAnnotationExecutor.runLoad(bulkLoadFile, cleanUp);
			}
			if (loadType == ALLELE_ASSOCIATION || loadType == FULL_INGEST) {
				alleleGeneAssociationExecutor.runLoad(bulkLoadFile, cleanUp);
			}

		} else if (bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.MOLECULE) {
			moleculeExecutor.runLoad(bulkLoadFile);
		} else if (bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.ORTHOLOGY) {
			orthologyExecutor.runLoad(bulkLoadFile);
		} else if (bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.ONTOLOGY) {
			ontologyExecutor.runLoad(bulkLoadFile);
		} else if (bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.RESOURCE_DESCRIPTOR) {
			resourceDescriptorExecutor.runLoad(bulkLoadFile);
		} else {
			log.info("Load: " + bulkLoadFile.getBulkLoad().getName() + " not implemented");
			throw new Exception("Load: " + bulkLoadFile.getBulkLoad().getName() + " not implemented");
		}
		log.info("Process Finished for: " + bulkLoadFile.getBulkLoad().getName());
	}

}
