package org.alliancegenome.curation_api.jobs;

import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.AGM;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.AGM_DISEASE_ANNOTATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.ALLELE;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.ALLELE_DISEASE_ANNOTATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.DISEASE_ANNOTATION;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.FULL_INGEST;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.GENE;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.GENE_DISEASE_ANNOTATION;

import java.io.FileInputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.enums.BackendBulkLoadType;
import org.alliancegenome.curation_api.enums.OntologyBulkLoadType;
import org.alliancegenome.curation_api.jobs.executors.AgmDiseaseAnnotationExecutor;
import org.alliancegenome.curation_api.jobs.executors.AgmExecutor;
import org.alliancegenome.curation_api.jobs.executors.AlleleDiseaseAnnotationExecutor;
import org.alliancegenome.curation_api.jobs.executors.AlleleExecutor;
import org.alliancegenome.curation_api.jobs.executors.GeneDiseaseAnnotationExecutor;
import org.alliancegenome.curation_api.jobs.executors.GeneExecutor;
import org.alliancegenome.curation_api.jobs.executors.MoleculeExecutor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.services.MoleculeService;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadHelper;
import org.alliancegenome.curation_api.services.ontology.AtpTermService;
import org.alliancegenome.curation_api.services.ontology.CHEBITermService;
import org.alliancegenome.curation_api.services.ontology.DaoTermService;
import org.alliancegenome.curation_api.services.ontology.DoTermService;
import org.alliancegenome.curation_api.services.ontology.EcoTermService;
import org.alliancegenome.curation_api.services.ontology.EmapaTermService;
import org.alliancegenome.curation_api.services.ontology.FbdvTermService;
import org.alliancegenome.curation_api.services.ontology.GoTermService;
import org.alliancegenome.curation_api.services.ontology.MaTermService;
import org.alliancegenome.curation_api.services.ontology.MmusdvTermService;
import org.alliancegenome.curation_api.services.ontology.MpTermService;
import org.alliancegenome.curation_api.services.ontology.RoTermService;
import org.alliancegenome.curation_api.services.ontology.SoTermService;
import org.alliancegenome.curation_api.services.ontology.WbbtTermService;
import org.alliancegenome.curation_api.services.ontology.WblsTermService;
import org.alliancegenome.curation_api.services.ontology.XbaTermService;
import org.alliancegenome.curation_api.services.ontology.XbedTermService;
import org.alliancegenome.curation_api.services.ontology.XbsTermService;
import org.alliancegenome.curation_api.services.ontology.XcoTermService;
import org.alliancegenome.curation_api.services.ontology.XpoTermService;
import org.alliancegenome.curation_api.services.ontology.XsmoTermService;
import org.alliancegenome.curation_api.services.ontology.ZecoTermService;
import org.alliancegenome.curation_api.services.ontology.ZfaTermService;
import org.alliancegenome.curation_api.services.ontology.ZfsTermService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class BulkLoadJobExecutor {

	@Inject
	ObjectMapper mapper;


	@Inject
	XcoTermService xcoTermService;
	@Inject
	GoTermService goTermService;
	@Inject
	SoTermService soTermService;
	@Inject
	EcoTermService ecoTermService;
	@Inject
	ZecoTermService zecoTermService;
	@Inject
	EmapaTermService emapaTermService;
	@Inject
	DaoTermService daoTermService;
	@Inject
	CHEBITermService chebiTermService;
	@Inject
	ZfaTermService zfaTermService;
	@Inject
	DoTermService doTermService;
	@Inject
	WbbtTermService wbbtTermService;
	@Inject
	MpTermService mpTermService;
	@Inject
	MaTermService maTermService;
	@Inject
	WblsTermService wblsTermService;
	@Inject
	FbdvTermService fbdvTermService;
	@Inject
	MmusdvTermService mmusdvTermService;
	@Inject
	ZfsTermService zfsTermService;
	@Inject
	XbaTermService xbaTermService;
	@Inject
	XbsTermService xbsTermService;
	@Inject
	XpoTermService xpoTermService;
	@Inject
	AtpTermService atpTermService;
	@Inject
	XbedTermService xbedTermService;
	@Inject
	XsmoTermService xsmoTermService;
	@Inject
	RoTermService roTermService;

	@Inject
	MoleculeService moleculeService;

	@Inject
	BulkLoadFileDAO bulkLoadFileDAO;

	@Inject
	AlleleDiseaseAnnotationExecutor alleleDiseaseAnnotationExecutor;
	@Inject
	AgmDiseaseAnnotationExecutor agmDiseaseAnnotationExecutor;
	@Inject
	GeneDiseaseAnnotationExecutor geneDiseaseAnnotationExecutor;
	@Inject
	GeneExecutor geneExecutor;
	@Inject
	AlleleExecutor alleleExecutor;
	@Inject
	AgmExecutor agmExecutor;
	@Inject
	MoleculeExecutor moleculeExecutor;


	public void process(BulkLoadFile bulkLoadFile) throws Exception {

		BackendBulkLoadType loadType = bulkLoadFile.getBulkLoad().getBackendBulkLoadType();

		List<BackendBulkLoadType> ingestTypes = List.of(
			AGM_DISEASE_ANNOTATION,
			ALLELE_DISEASE_ANNOTATION,
			GENE_DISEASE_ANNOTATION,
			DISEASE_ANNOTATION,
			AGM,
			ALLELE,
			GENE,
			FULL_INGEST
		);

		if (ingestTypes.contains(loadType)) {

			bulkLoadFile.setRecordCount(0);
			bulkLoadFileDAO.merge(bulkLoadFile);

			if (loadType == ALLELE_DISEASE_ANNOTATION || loadType == DISEASE_ANNOTATION || loadType == FULL_INGEST) {
				alleleDiseaseAnnotationExecutor.runLoad(bulkLoadFile);
			}
			if (loadType == AGM_DISEASE_ANNOTATION || loadType == DISEASE_ANNOTATION || loadType == FULL_INGEST) {
				agmDiseaseAnnotationExecutor.runLoad(bulkLoadFile);
			}
			if (loadType == GENE_DISEASE_ANNOTATION || loadType == DISEASE_ANNOTATION || loadType == FULL_INGEST) {
				geneDiseaseAnnotationExecutor.runLoad(bulkLoadFile);
			}
			if (loadType == AGM || loadType == FULL_INGEST) {
				agmExecutor.runLoad(bulkLoadFile);
			}
			if (loadType == ALLELE || loadType == FULL_INGEST) {
				alleleExecutor.runLoad(bulkLoadFile);
			}
			if (loadType == GENE || loadType == FULL_INGEST) {
				geneExecutor.runLoad(bulkLoadFile);
			}

		} else if (bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.MOLECULE) {
			moleculeExecutor.runLoad(bulkLoadFile);
		} else if (bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.ONTOLOGY) {
			bulkLoadFile.setRecordCount(0);
			GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
			BaseOntologyTermService service = null;
			OntologyBulkLoadType ontologyType = bulkLoadFile.getBulkLoad().getOntologyType();

			switch (ontologyType) {
				case ZECO:
					config.setLoadOnlyIRIPrefix("ZECO");
					service = zecoTermService;
					processTerms(bulkLoadFile, zecoTermService, config);
					break;
				case EMAPA:
					config.getAltNameSpaces().add("anatomical_structure");
					service = emapaTermService;
					processTerms(bulkLoadFile, emapaTermService, config);
					break;
				case GO:
					config.getAltNameSpaces().add("biological_process");
					config.getAltNameSpaces().add("molecular_function");
					config.getAltNameSpaces().add("cellular_component");
					service = goTermService;
					processTerms(bulkLoadFile, goTermService, config);
					break;
				case SO:
					processTerms(bulkLoadFile, soTermService, config);
					break;
				case XCO:
					processTerms(bulkLoadFile, xcoTermService, config);
					break;
				case ECO:
					processTerms(bulkLoadFile, ecoTermService, config);
					ecoTermService.updateAbbreviations();
					break;
				case CHEBI:
					processTerms(bulkLoadFile, chebiTermService, config);
					break;
				case ZFA:
					config.getAltNameSpaces().add("zebrafish_anatomy");
					processTerms(bulkLoadFile, zfaTermService, config);
					break;
				case DO:
					processTerms(bulkLoadFile, doTermService, config);
					break;
				case MP:
					config.setLoadOnlyIRIPrefix("MP");
					processTerms(bulkLoadFile, mpTermService, config);
					break;
				case RO:
					//config.setLoadOnlyIRIPrefix("RO");
					processTerms(bulkLoadFile, roTermService, config);
					break;
				case MA:
					processTerms(bulkLoadFile, maTermService, config);
					break;
				case WBBT:
					processTerms(bulkLoadFile, wbbtTermService, config);
					break;
				case DAO:
					config.setLoadOnlyIRIPrefix("FBbt");
					processTerms(bulkLoadFile, daoTermService, config);
					break;
				case WBLS:
					processTerms(bulkLoadFile, wblsTermService, config);
					break;
				case FBDV:
					processTerms(bulkLoadFile, fbdvTermService, config);
					break;
				case MMUSDV:
					config.getAltNameSpaces().add("mouse_developmental_stage");
					config.getAltNameSpaces().add("mouse_stages_ontology");
					processTerms(bulkLoadFile, mmusdvTermService, config);
					break;
				case ZFS:
					processTerms(bulkLoadFile, zfsTermService, config);
					break;
				case XBA_XBS:
					config.getAltNameSpaces().add("xenopus_anatomy");
					config.getAltNameSpaces().add("xenopus_anatomy_in_vitro");
					processTerms(bulkLoadFile, OntologyBulkLoadType.XBA, xbaTermService, config);
					GenericOntologyLoadConfig config2 = new GenericOntologyLoadConfig();
					config2.getAltNameSpaces().add("xenopus_developmental_stage");
					processTerms(bulkLoadFile, OntologyBulkLoadType.XBS, xbsTermService, config2);
					break;
				case XPO:
					config.setLoadOnlyIRIPrefix("XPO");
					processTerms(bulkLoadFile, xpoTermService, config);
					break;
				case ATP:
					config.setLoadOnlyIRIPrefix("ATP");
					processTerms(bulkLoadFile, atpTermService, config);
					break;
				case XBED:
					processTerms(bulkLoadFile, xbedTermService, config);
					break;
				case XSMO:
					processTerms(bulkLoadFile, xsmoTermService, config);
					break;
				default:
					log.info("Ontology Load: " + bulkLoadFile.getBulkLoad().getName() + " for OT: " + ontologyType + " not implemented");
					throw new Exception("Ontolgy Load: " + bulkLoadFile.getBulkLoad().getName() + " for OT: " + ontologyType + " not implemented");
			}
		} else {
			log.info("Load: " + bulkLoadFile.getBulkLoad().getName() + " not implemented");
			throw new Exception("Load: " + bulkLoadFile.getBulkLoad().getName() + " not implemented");
		}
		log.info("Process Finished for: " + bulkLoadFile.getBulkLoad().

			getName());
	}

	private void processTerms(BulkLoadFile bulkLoadFile, BaseOntologyTermService service, GenericOntologyLoadConfig config) throws Exception {
		processTerms(bulkLoadFile, bulkLoadFile.getBulkLoad().getOntologyType(), service, config);
	}

	private void processTerms(BulkLoadFile bulkLoadFile, OntologyBulkLoadType ontologyType, BaseOntologyTermService service, GenericOntologyLoadConfig config) throws Exception {

		GenericOntologyLoadHelper loader = new GenericOntologyLoadHelper(ontologyType.getClazz(), config);

		Map<String, ? extends OntologyTerm> termMap = loader.load(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())));

		bulkLoadFile.setRecordCount(bulkLoadFile.getRecordCount() + termMap.size());

		bulkLoadFile.setDateLastLoaded(OffsetDateTime.now());
		bulkLoadFileDAO.merge(bulkLoadFile);
		ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
		ph.startProcess(bulkLoadFile.getBulkLoad().getName() + ": " + ontologyType.getClazz().getSimpleName() + " Terms", termMap.size());
		for (Entry<String, ? extends OntologyTerm> entry: termMap.entrySet()) {
			service.processUpdate(entry.getValue());
			ph.progressProcess();
		}
		ph.finishProcess();
		
		ProcessDisplayHelper ph1 = new ProcessDisplayHelper(10000);
		ph1.startProcess(bulkLoadFile.getBulkLoad().getName() + ": " + ontologyType.getClazz().getSimpleName() + " Closure", termMap.size());
		for (Entry<String, ? extends OntologyTerm> entry: termMap.entrySet()) {
			service.processUpdateRelationships(entry.getValue());
			ph1.progressProcess();
		}
		ph1.finishProcess();
	}


}
