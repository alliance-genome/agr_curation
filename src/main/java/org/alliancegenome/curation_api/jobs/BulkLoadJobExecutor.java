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
import org.alliancegenome.curation_api.jobs.executors.OrthologyExecutor;
import org.alliancegenome.curation_api.jobs.executors.ResourceDescriptorExecutor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.services.MoleculeService;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadHelper;
import org.alliancegenome.curation_api.services.ontology.ApoTermService;
import org.alliancegenome.curation_api.services.ontology.AtpTermService;
import org.alliancegenome.curation_api.services.ontology.BspoTermService;
import org.alliancegenome.curation_api.services.ontology.CHEBITermService;
import org.alliancegenome.curation_api.services.ontology.ClTermService;
import org.alliancegenome.curation_api.services.ontology.CmoTermService;
import org.alliancegenome.curation_api.services.ontology.DaoTermService;
import org.alliancegenome.curation_api.services.ontology.DoTermService;
import org.alliancegenome.curation_api.services.ontology.DpoTermService;
import org.alliancegenome.curation_api.services.ontology.EcoTermService;
import org.alliancegenome.curation_api.services.ontology.EmapaTermService;
import org.alliancegenome.curation_api.services.ontology.FbdvTermService;
import org.alliancegenome.curation_api.services.ontology.GoTermService;
import org.alliancegenome.curation_api.services.ontology.HpTermService;
import org.alliancegenome.curation_api.services.ontology.MaTermService;
import org.alliancegenome.curation_api.services.ontology.MiTermService;
import org.alliancegenome.curation_api.services.ontology.MmoTermService;
import org.alliancegenome.curation_api.services.ontology.MmusdvTermService;
import org.alliancegenome.curation_api.services.ontology.ModTermService;
import org.alliancegenome.curation_api.services.ontology.MpTermService;
import org.alliancegenome.curation_api.services.ontology.MpathTermService;
import org.alliancegenome.curation_api.services.ontology.ObiTermService;
import org.alliancegenome.curation_api.services.ontology.PatoTermService;
import org.alliancegenome.curation_api.services.ontology.PwTermService;
import org.alliancegenome.curation_api.services.ontology.RoTermService;
import org.alliancegenome.curation_api.services.ontology.RsTermService;
import org.alliancegenome.curation_api.services.ontology.SoTermService;
import org.alliancegenome.curation_api.services.ontology.UberonTermService;
import org.alliancegenome.curation_api.services.ontology.VtTermService;
import org.alliancegenome.curation_api.services.ontology.WbPhenotypeTermService;
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

	@Inject XcoTermService xcoTermService;
	@Inject GoTermService goTermService;
	@Inject SoTermService soTermService;
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
	HpTermService hpTermService;
	@Inject
	WbbtTermService wbbtTermService;
	@Inject
	MpTermService mpTermService;
	@Inject
	MaTermService maTermService;
	@Inject
	VtTermService vtTermService;
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
	ObiTermService obiTermService;
	@Inject
	PatoTermService patoTermService;
	@Inject
	WbPhenotypeTermService wbPhenotypeTermService;
	@Inject
	DpoTermService dpoTermService;
	@Inject
	MmoTermService mmoTermService;
	@Inject
	ApoTermService apoTermService;
	@Inject
	MiTermService miTermService;
	@Inject
	MpathTermService mpathTermService;
	@Inject
	ModTermService modTermService;
	@Inject
	UberonTermService uberonTermService;
	@Inject
	RsTermService rsTermService;
	@Inject
	PwTermService pwTermService;
	@Inject
	ClTermService clTermService;
	@Inject
	CmoTermService cmoTermService;
	@Inject
	BspoTermService bspoTermService;

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
	@Inject
	ResourceDescriptorExecutor resourceDescriptorExecutor;
	@Inject
	OrthologyExecutor orthologyExecutor;

	public void process(BulkLoadFile bulkLoadFile, Boolean cleanUp) throws Exception {

		BackendBulkLoadType loadType = bulkLoadFile.getBulkLoad().getBackendBulkLoadType();

		List<BackendBulkLoadType> ingestTypes = List.of(AGM_DISEASE_ANNOTATION, ALLELE_DISEASE_ANNOTATION, GENE_DISEASE_ANNOTATION, DISEASE_ANNOTATION, AGM, ALLELE, GENE, FULL_INGEST);

		if (ingestTypes.contains(loadType)) {

			bulkLoadFile.setRecordCount(0);
			bulkLoadFileDAO.merge(bulkLoadFile);

			if (loadType == ALLELE_DISEASE_ANNOTATION || loadType == DISEASE_ANNOTATION || loadType == FULL_INGEST) {
				alleleDiseaseAnnotationExecutor.runLoad(bulkLoadFile, cleanUp);
			}
			if (loadType == AGM_DISEASE_ANNOTATION || loadType == DISEASE_ANNOTATION || loadType == FULL_INGEST) {
				agmDiseaseAnnotationExecutor.runLoad(bulkLoadFile, cleanUp);
			}
			if (loadType == GENE_DISEASE_ANNOTATION || loadType == DISEASE_ANNOTATION || loadType == FULL_INGEST) {
				geneDiseaseAnnotationExecutor.runLoad(bulkLoadFile, cleanUp);
			}
			if (loadType == AGM || loadType == FULL_INGEST) {
				agmExecutor.runLoad(bulkLoadFile, cleanUp);
			}
			if (loadType == ALLELE || loadType == FULL_INGEST) {
				alleleExecutor.runLoad(bulkLoadFile, cleanUp);
			}
			if (loadType == GENE || loadType == FULL_INGEST) {
				geneExecutor.runLoad(bulkLoadFile, cleanUp);
			}

		} else if (bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.MOLECULE) {
			moleculeExecutor.runLoad(bulkLoadFile);
		} else if (bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.ORTHOLOGY) {
			orthologyExecutor.runLoad(bulkLoadFile);
		} else if (bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.ONTOLOGY) {
			bulkLoadFile.setRecordCount(0);
			GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
			BaseOntologyTermService service = null;
			OntologyBulkLoadType ontologyType = bulkLoadFile.getBulkLoad().getOntologyType();

			switch (ontologyType) {
				case ZECO -> {
					config.setLoadOnlyIRIPrefix("ZECO");
					service = zecoTermService;
					processTerms(bulkLoadFile, zecoTermService, config);
				}
				case EMAPA -> {
					config.getAltNameSpaces().add("anatomical_structure");
					service = emapaTermService;
					processTerms(bulkLoadFile, emapaTermService, config);
				}
				case GO -> {
					config.getAltNameSpaces().add("biological_process");
					config.getAltNameSpaces().add("molecular_function");
					config.getAltNameSpaces().add("cellular_component");
					service = goTermService;
					processTerms(bulkLoadFile, goTermService, config);
				}
				case SO -> processTerms(bulkLoadFile, soTermService, config);
				case XCO -> processTerms(bulkLoadFile, xcoTermService, config);
				case ECO -> {
					processTerms(bulkLoadFile, ecoTermService, config);
					ecoTermService.updateAbbreviations();
				}
				case CHEBI -> processTerms(bulkLoadFile, chebiTermService, config);
				case ZFA -> {
					config.getAltNameSpaces().add("zebrafish_anatomy");
					processTerms(bulkLoadFile, zfaTermService, config);
				}
				case DO -> processTerms(bulkLoadFile, doTermService, config);
				case MP -> {
					config.setLoadOnlyIRIPrefix("MP");
					processTerms(bulkLoadFile, mpTermService, config);
				}
				case RO -> {
					config.setLoadObjectProperties(true);
					config.setLoadOnlyIRIPrefix("RO");
					processTerms(bulkLoadFile, roTermService, config);
				}
				case MA -> processTerms(bulkLoadFile, maTermService, config);
				case WBBT -> processTerms(bulkLoadFile, wbbtTermService, config);
				case DAO -> {
					config.setLoadOnlyIRIPrefix("FBbt");
					processTerms(bulkLoadFile, daoTermService, config);
				}
				case WBLS -> processTerms(bulkLoadFile, wblsTermService, config);
				case FBDV -> processTerms(bulkLoadFile, fbdvTermService, config);
				case MMUSDV -> {
					config.getAltNameSpaces().add("mouse_developmental_stage");
					config.getAltNameSpaces().add("mouse_stages_ontology");
					processTerms(bulkLoadFile, mmusdvTermService, config);
				}
				case ZFS -> processTerms(bulkLoadFile, zfsTermService, config);
				case XBA_XBS -> {
					config.getAltNameSpaces().add("xenopus_anatomy");
					config.getAltNameSpaces().add("xenopus_anatomy_in_vitro");
					processTerms(bulkLoadFile, OntologyBulkLoadType.XBA, xbaTermService, config);
					GenericOntologyLoadConfig config2 = new GenericOntologyLoadConfig();
					config2.getAltNameSpaces().add("xenopus_developmental_stage");
					processTerms(bulkLoadFile, OntologyBulkLoadType.XBS, xbsTermService, config2);
				}
				case XPO -> {
					config.setLoadOnlyIRIPrefix("XPO");
					processTerms(bulkLoadFile, xpoTermService, config);
				}
				case ATP -> {
					config.setLoadOnlyIRIPrefix("ATP");
					processTerms(bulkLoadFile, atpTermService, config);
				}
				case XBED -> processTerms(bulkLoadFile, xbedTermService, config);
				case VT -> processTerms(bulkLoadFile, vtTermService, config);
				case XSMO -> processTerms(bulkLoadFile, xsmoTermService, config);
				case OBI -> {
					config.setLoadOnlyIRIPrefix("OBI");
					processTerms(bulkLoadFile, obiTermService, config);
				}
				case WBPheno -> processTerms(bulkLoadFile, wbPhenotypeTermService, config);
				case PATO -> processTerms(bulkLoadFile, patoTermService, config);
				case HP -> {
					config.setLoadOnlyIRIPrefix("HP");
					processTerms(bulkLoadFile, hpTermService, config);
				}
				case DPO -> {
					config.getAltNameSpaces().add("phenotypic_class");
					processTerms(bulkLoadFile, dpoTermService, config);
				}
				case MMO -> processTerms(bulkLoadFile, mmoTermService, config);
				case APO -> {
					config.getAltNameSpaces().add("experiment_type");
					config.getAltNameSpaces().add("mutant_type");
					config.getAltNameSpaces().add("observable");
					config.getAltNameSpaces().add("qualifier");
					processTerms(bulkLoadFile, apoTermService, config);
				}
				case MI -> processTerms(bulkLoadFile, miTermService, config);
				case MPATH -> processTerms(bulkLoadFile, mpathTermService, config);
				case MOD -> processTerms(bulkLoadFile, modTermService, config);
				case UBERON -> {
					config.setLoadOnlyIRIPrefix("UBERON");
					processTerms(bulkLoadFile, uberonTermService, config);
				}
				case RS -> processTerms(bulkLoadFile, rsTermService, config);
				case PW -> processTerms(bulkLoadFile, pwTermService, config);
				case CL -> {
					config.setLoadOnlyIRIPrefix("CL");
					processTerms(bulkLoadFile, clTermService, config);
				}
				case CMO -> {
					config.setLoadOnlyIRIPrefix("CMO");
					processTerms(bulkLoadFile, cmoTermService, config);
				}
				case BSPO -> {
					config.setLoadOnlyIRIPrefix("BSPO");
					processTerms(bulkLoadFile, bspoTermService, config);
				}
				default -> {
					log.info("Ontology Load: " + bulkLoadFile.getBulkLoad().getName() + " for OT: " + ontologyType + " not implemented");
					throw new Exception("Ontology Load: " + bulkLoadFile.getBulkLoad().getName() + " for OT: " + ontologyType + " not implemented");
				}
			}
		} else if (bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.RESOURCE_DESCRIPTOR) {
			resourceDescriptorExecutor.runLoad(bulkLoadFile);
		} else {
			log.info("Load: " + bulkLoadFile.getBulkLoad().getName() + " not implemented");
			throw new Exception("Load: " + bulkLoadFile.getBulkLoad().getName() + " not implemented");
		}
		log.info("Process Finished for: " + bulkLoadFile.getBulkLoad().getName());
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
		for (Entry<String, ? extends OntologyTerm> entry : termMap.entrySet()) {
			service.processUpdate(entry.getValue());
			ph.progressProcess();
		}
		ph.finishProcess();

		ProcessDisplayHelper ph1 = new ProcessDisplayHelper(10000);
		ph1.startProcess(bulkLoadFile.getBulkLoad().getName() + ": " + ontologyType.getClazz().getSimpleName() + " Closure", termMap.size());
		for (Entry<String, ? extends OntologyTerm> entry : termMap.entrySet()) {
			service.processUpdateRelationships(entry.getValue());
			ph1.progressProcess();
		}
		ph1.finishProcess();
	}

}
