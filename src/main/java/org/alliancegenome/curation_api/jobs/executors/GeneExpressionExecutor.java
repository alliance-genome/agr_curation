package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneExpressionExperiment;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ConsolidatedGeneExpressionFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.CrossReferenceFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.GeneExpressionFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.GeneExpressionIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.GeneExpressionAnnotationService;
import org.alliancegenome.curation_api.services.SpeciesService;
import org.alliancegenome.curation_api.services.GeneExpressionExperimentService;
import org.alliancegenome.curation_api.services.helpers.annotations.GeneExpressionAnnotationUniqueIdHelper;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GeneExpressionExecutor extends LoadFileExecutor {
	@Inject GeneExpressionAnnotationService geneExpressionAnnotationService;
	@Inject GeneExpressionExperimentService geneExpressionExperimentService;
	@Inject GeneExpressionAnnotationUniqueIdHelper geneExpressionAnnotationUniqueIdHelper;
	@Inject SpeciesService speciesService;
	static final String ANNOTATIONS = "Annotations";
	static final String EXPERIMENTS = "Experiments";


	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) {
		try {
			Species species = bulkLoadFileHistory.getBulkLoad().getSpecies();

			GeneExpressionIngestFmsDTO geneExpressionIngestFmsDTO = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())), GeneExpressionIngestFmsDTO.class);
			bulkLoadFileHistory.getBulkLoadFile().setRecordCount(geneExpressionIngestFmsDTO.getData().size());

			AGRCurationSchemaVersion version = GeneExpressionAnnotation.class.getAnnotation(AGRCurationSchemaVersion.class);
			bulkLoadFileHistory.getBulkLoadFile().setLinkMLSchemaVersion(version.max());
			if (geneExpressionIngestFmsDTO.getMetaData() != null && StringUtils.isNotBlank(geneExpressionIngestFmsDTO.getMetaData().getRelease())) {
				bulkLoadFileHistory.getBulkLoadFile().setAllianceMemberReleaseVersion(geneExpressionIngestFmsDTO.getMetaData().getRelease());
			}
			bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

			List<Long> annotationIdsLoaded = new ArrayList<>();
			List<Long> annotationIdsBefore = geneExpressionAnnotationService.getAnnotationIdsBySpecies(species);

			List<Long> experimentIdsLoaded = new ArrayList<>();
			List<Long> experimentIdsBefore = geneExpressionExperimentService.getExperimentIdsBySpecies(species);

			boolean success = runLoad(geneExpressionAnnotationService, bulkLoadFileHistory, species, consolidateFMSDTOs(geneExpressionIngestFmsDTO.getData()), annotationIdsLoaded, ANNOTATIONS);

			if (success) {
				runCleanup(geneExpressionAnnotationService, bulkLoadFileHistory, species.getDisplayName(), annotationIdsBefore, annotationIdsLoaded, ANNOTATIONS);
				loadExperiments(bulkLoadFileHistory, species, experimentIdsLoaded);
				runCleanup(geneExpressionExperimentService, bulkLoadFileHistory, species.getDisplayName(), experimentIdsBefore, experimentIdsLoaded, EXPERIMENTS);
			}

			bulkLoadFileHistory.finishLoad();
			updateHistory(bulkLoadFileHistory);
			updateExceptions(bulkLoadFileHistory);

		} catch (Exception e) {
			failLoad(bulkLoadFileHistory, e);
			e.printStackTrace();
		}
	}

	public APIResponse runLoadAPI(GeneExpressionAnnotationService service, String dataProviderName, List<GeneExpressionFmsDTO> objectList) {
		List<Long> idsLoaded = new ArrayList<>();
		BulkLoadFileHistory history = new BulkLoadFileHistory(objectList.size());
		history = bulkLoadFileHistoryDAO.persist(history);
		Species species = null;
		if (dataProviderName != null) {
			species = speciesService.getByDisplayName(dataProviderName);
		}
		boolean success = runLoad(service, history, species, consolidateFMSDTOs(objectList), idsLoaded, true, ANNOTATIONS);
		if (success) {
			loadExperiments(history, species, new ArrayList<>());
		}
		history.finishLoad();
		return new LoadHistoryResponce(history);
	}

	private void loadExperiments(BulkLoadFileHistory history, Species species, List<Long> experimentIdsLoaded) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		Map<String, Set<String>> experiments = geneExpressionAnnotationService.getExperiments();
		Map<String, Set<CrossReferenceFmsDTO>> crossReferences = geneExpressionAnnotationService.getCrossReferences();
		ph.startProcess("Saving gene expression experiments: ", experiments.size());
		history.setCount(EXPERIMENTS, geneExpressionAnnotationService.getExperiments().size());
		for (String experimentId: experiments.keySet()) {
			try {
				GeneExpressionExperiment experiment = geneExpressionExperimentService.upsert(experimentId, experiments.get(experimentId), species, crossReferences.get(experimentId));
				if (experiment != null) {
					experimentIdsLoaded.add(experiment.getId());
					history.incrementCompleted(EXPERIMENTS);
				}
			} catch (ObjectUpdateException e) {
				history.incrementFailed(EXPERIMENTS);
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed(EXPERIMENTS);
				addException(history, new ObjectUpdateException.ObjectUpdateExceptionData(experimentId, e.getMessage(), e.getStackTrace()));
			}
			ph.progressProcess();
			if (Thread.currentThread().isInterrupted()) {
				history.setErrorMessage(ApiErrorException.INTERRUPTED_MESSAGE);
				throw new RuntimeException(ApiErrorException.INTERRUPTED_MESSAGE);
			}
		}
		updateHistory(history);
		ph.finishProcess();
	}

	private List<ConsolidatedGeneExpressionFmsDTO> consolidateFMSDTOs(List<GeneExpressionFmsDTO> geneExpressionFmsDTOs) {
		Map<String, ConsolidatedGeneExpressionFmsDTO> consolidationDictionary = new HashMap<>();

		for (GeneExpressionFmsDTO geneExpressionFmsDTO : geneExpressionFmsDTOs) {
			String key = geneExpressionAnnotationUniqueIdHelper.generateHash(geneExpressionFmsDTO);
			if (consolidationDictionary.containsKey(key)) {
				if (geneExpressionFmsDTO.getCrossReference() != null) {
					consolidationDictionary.get(key).getCrossReferences().add(geneExpressionFmsDTO.getCrossReference());
				}
			} else {
				consolidationDictionary.put(key, adaptDTO(geneExpressionFmsDTO));
			}
		}

		return new ArrayList<ConsolidatedGeneExpressionFmsDTO>(consolidationDictionary.values());
	}

	private ConsolidatedGeneExpressionFmsDTO adaptDTO(GeneExpressionFmsDTO geneExpressionFmsDTO) {
		ConsolidatedGeneExpressionFmsDTO consolidatedGeneExpressionFmsDTO = new ConsolidatedGeneExpressionFmsDTO();

		consolidatedGeneExpressionFmsDTO.setGeneId(geneExpressionFmsDTO.getGeneId());
		consolidatedGeneExpressionFmsDTO.setAssay(geneExpressionFmsDTO.getAssay());
		consolidatedGeneExpressionFmsDTO.setDateAssigned(geneExpressionFmsDTO.getDateAssigned());
		consolidatedGeneExpressionFmsDTO.setEvidence(geneExpressionFmsDTO.getEvidence());
		consolidatedGeneExpressionFmsDTO.setWhenExpressed(geneExpressionFmsDTO.getWhenExpressed());
		consolidatedGeneExpressionFmsDTO.setWhereExpressed(geneExpressionFmsDTO.getWhereExpressed());
		if (ObjectUtils.isNotEmpty(geneExpressionFmsDTO.getCrossReference())) {
			consolidatedGeneExpressionFmsDTO.setCrossReferences(new ArrayList<>(List.of(geneExpressionFmsDTO.getCrossReference())));
		} else {
			consolidatedGeneExpressionFmsDTO.setCrossReferences(new ArrayList<>());
		}
		return consolidatedGeneExpressionFmsDTO;
	}

}

