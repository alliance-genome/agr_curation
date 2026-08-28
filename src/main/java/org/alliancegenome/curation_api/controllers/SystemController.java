package org.alliancegenome.curation_api.controllers;

import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.dao.base.SystemSQLDAO;
import org.alliancegenome.curation_api.interfaces.SystemControllerInterface;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ConditionRelationService;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;
import org.alliancegenome.curation_api.services.ExperimentalConditionService;
import org.alliancegenome.curation_api.services.PhenotypeAnnotationService;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.VariantDAO;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.dao.AntibodyDAO;
import org.alliancegenome.curation_api.dao.SequenceTargetingReagentDAO;
import org.alliancegenome.curation_api.dao.AssemblyComponentDAO;
import org.alliancegenome.curation_api.dao.GenomeAssemblyDAO;
import org.alliancegenome.curation_api.dao.PhenotypeAnnotationDAO;
import org.alliancegenome.curation_api.enums.MatiSubdomain;
import org.alliancegenome.curation_api.services.CurieMintService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class SystemController implements SystemControllerInterface {

	@Inject
	SystemSQLDAO systemSQLDAO;
	@Inject
	CurieMintService curieMintService;
	@Inject
	AlleleDAO alleleDAO;
	@Inject
	GeneDAO geneDAO;
	@Inject
	VariantDAO variantDAO;
	@Inject
	AffectedGenomicModelDAO affectedGenomicModelDAO;
	@Inject
	ConstructDAO constructDAO;
	@Inject
	AntibodyDAO antibodyDAO;
	@Inject
	SequenceTargetingReagentDAO sequenceTargetingReagentDAO;
	@Inject
	AssemblyComponentDAO assemblyComponentDAO;
	@Inject
	GenomeAssemblyDAO genomeAssemblyDAO;
	@Inject
	PhenotypeAnnotationDAO phenotypeAnnotationDAO;
	@Inject
	DiseaseAnnotationDAO diseaseAnnotationDAO;
	@Inject
	DiseaseAnnotationService diseaseAnnotationService;
	@Inject
	PhenotypeAnnotationService phenotypeAnnotationService;
	@Inject
	ConditionRelationService conditionRelationService;
	@Inject
	ExperimentalConditionService experimentalConditionService;
	
	@Override
	public void reindexEverything(Integer threadsToLoadObjects, Integer typesToIndexInParallel, Integer limitIndexedObjectsTo, Integer batchSizeToLoadObjects, Integer idFetchSize,
		Integer transactionTimeout) {
		systemSQLDAO.reindexEverything(threadsToLoadObjects, typesToIndexInParallel, limitIndexedObjectsTo, batchSizeToLoadObjects, idFetchSize, transactionTimeout);
	}

	@Override
	public ObjectResponse<Map<String, Object>> getSiteSummary() {
		return systemSQLDAO.getSiteSummary();
	}
	
	public void updateDiseaseAnnotationUniqueIds() {
		diseaseAnnotationService.updateUniqueIds();
	}

	@Override
	public void mintMissingDiseaseAnnotationCuries(Integer batchSize, Integer maxToMint) {
		curieMintService.mintMissingCuries(diseaseAnnotationDAO, MatiSubdomain.DISEASE_ANNOTATION, batchSize, maxToMint);
	}

	@Override
	public void mintMissingAlleleCuries(Integer batchSize, Integer maxToMint) {
		curieMintService.mintMissingCuries(alleleDAO, MatiSubdomain.ALLELE, batchSize, maxToMint);
	}

	@Override
	public void mintMissingGeneCuries(Integer batchSize, Integer maxToMint) {
		curieMintService.mintMissingCuries(geneDAO, MatiSubdomain.GENE, batchSize, maxToMint);
	}

	@Override
	public void mintMissingVariantCuries(Integer batchSize, Integer maxToMint) {
		curieMintService.mintMissingCuries(variantDAO, MatiSubdomain.VARIANT, batchSize, maxToMint);
	}

	@Override
	public void mintMissingAgmCuries(Integer batchSize, Integer maxToMint) {
		curieMintService.mintMissingCuries(affectedGenomicModelDAO, MatiSubdomain.AGM, batchSize, maxToMint);
	}

	@Override
	public void mintMissingConstructCuries(Integer batchSize, Integer maxToMint) {
		curieMintService.mintMissingCuries(constructDAO, MatiSubdomain.CONSTRUCT, batchSize, maxToMint);
	}

	@Override
	public void mintMissingAntibodyCuries(Integer batchSize, Integer maxToMint) {
		curieMintService.mintMissingCuries(antibodyDAO, MatiSubdomain.ANTIBODY, batchSize, maxToMint);
	}

	@Override
	public void mintMissingStrCuries(Integer batchSize, Integer maxToMint) {
		curieMintService.mintMissingCuries(sequenceTargetingReagentDAO, MatiSubdomain.SEQUENCE_TARGETING_REAGENT, batchSize, maxToMint);
	}

	@Override
	public void mintMissingAssemblyComponentCuries(Integer batchSize, Integer maxToMint) {
		curieMintService.mintMissingCuries(assemblyComponentDAO, MatiSubdomain.ASSEMBLY_COMPONENT, batchSize, maxToMint);
	}

	@Override
	public void mintMissingGenomeAssemblyCuries(Integer batchSize, Integer maxToMint) {
		curieMintService.mintMissingCuries(genomeAssemblyDAO, MatiSubdomain.GENOME_ASSEMBLY, batchSize, maxToMint);
	}

	@Override
	public void mintMissingPhenotypeAnnotationCuries(Integer batchSize, Integer maxToMint) {
		curieMintService.mintMissingCuries(phenotypeAnnotationDAO, MatiSubdomain.PHENOTYPE_ANNOTATION, batchSize, maxToMint);
	}

	@Override
	public void deleteUnusedConditionsAndExperiments() {
		List<Long> inUseCrIds = diseaseAnnotationService.getAllReferencedConditionRelationIds();
		inUseCrIds.addAll(phenotypeAnnotationService.getAllReferencedConditionRelationIds());
		conditionRelationService.deleteUnusedConditions(inUseCrIds);
		experimentalConditionService.deleteUnusedExperiments();
	}
}
