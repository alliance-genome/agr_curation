package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Molecule;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.PhenotypeAnnotationService;
import org.alliancegenome.curation_api.services.SpeciesService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PhenotypeAnnotationExecutor extends LoadFileExecutor {

	@Inject PhenotypeAnnotationService phenotypeAnnotationService;
	@Inject SpeciesService speciesService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) {
		try {

			BulkFMSLoad fmsLoad = (BulkFMSLoad) bulkLoadFileHistory.getBulkLoad();
			Species species = speciesService.getByDisplayName(fmsLoad.getFmsDataSubType());

			PhenotypeIngestFmsDTO phenotypeData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())), PhenotypeIngestFmsDTO.class);
			bulkLoadFileHistory.getBulkLoadFile().setRecordCount(phenotypeData.getData().size());
			if (bulkLoadFileHistory.getBulkLoadFile().getLinkMLSchemaVersion() == null) {
				AGRCurationSchemaVersion version = Molecule.class.getAnnotation(AGRCurationSchemaVersion.class);
				bulkLoadFileHistory.getBulkLoadFile().setLinkMLSchemaVersion(version.max());
			}
			if (phenotypeData.getMetaData() != null && StringUtils.isNotBlank(phenotypeData.getMetaData().getRelease())) {
				bulkLoadFileHistory.getBulkLoadFile().setAllianceMemberReleaseVersion(phenotypeData.getMetaData().getRelease());
			}
			bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

			bulkLoadFileHistory.setCount(phenotypeData.getData().size());
			updateHistory(bulkLoadFileHistory);

			Set<Long> annotationIdsLoaded = new HashSet<>();
			List<Long> annotationIdsBefore = phenotypeAnnotationService.getAnnotationIdsByDataProvider(species);

			phenotypeAnnotationService.preloadUniqueIds(species);

			runLoad(bulkLoadFileHistory, phenotypeData.getData(), annotationIdsLoaded, species);

			runCleanup(phenotypeAnnotationService, bulkLoadFileHistory, species.getDisplayName(), annotationIdsBefore, annotationIdsLoaded.stream().collect(Collectors.toList()), "phenotype annotation");

			bulkLoadFileHistory.finishLoad();
			updateHistory(bulkLoadFileHistory);
			updateExceptions(bulkLoadFileHistory);
		} catch (Exception e) {
			failLoad(bulkLoadFileHistory, e);
			e.printStackTrace();
		}
	}

	// Gets called from the API directly
	public APIResponse runLoadApi(String dataProviderName, List<PhenotypeFmsDTO> annotations) {
		Set<Long> annotationIdsLoaded = new HashSet<>();
		BulkLoadFileHistory history = new BulkLoadFileHistory(annotations.size());
		history = bulkLoadFileHistoryDAO.persist(history);
		Species species = speciesService.getByDisplayName(dataProviderName);
		runLoad(history, annotations, annotationIdsLoaded, species);
		history.finishLoad();

		return new LoadHistoryResponce(history);
	}

	private void runLoad(BulkLoadFileHistory history, List<PhenotypeFmsDTO> annotations, Set<Long> idsAdded, Species species) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("PhenotypeAnnotation update for " + species.getDisplayName(), annotations.size());

		loadPrimaryAnnotations(history, annotations, idsAdded, species, ph);
		loadSecondaryAnnotations(history, annotations, idsAdded, species, ph);

		ph.finishProcess();

	}
	
	private void loadSecondaryAnnotations(BulkLoadFileHistory history, List<PhenotypeFmsDTO> annotations, Set<Long> idsAdded, Species species, ProcessDisplayHelper ph) {
		for (PhenotypeFmsDTO dto : annotations) {
			if (CollectionUtils.isEmpty(dto.getPrimaryGeneticEntityIds())) {
				continue;
			}

			try {
				phenotypeAnnotationService.addInferredOrAssertedEntities(dto, species, idsAdded);
				history.incrementCompleted();
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(dto, e.getMessage(), e.getStackTrace()));
			}
			
			ph.progressProcess();
			if (Thread.currentThread().isInterrupted()) {
				history.setErrorMessage(ApiErrorException.INTERRUPTED_MESSAGE);
				throw new RuntimeException(ApiErrorException.INTERRUPTED_MESSAGE);
			}
		}
		updateHistory(history);
	}

	private void loadPrimaryAnnotations(BulkLoadFileHistory history, List<PhenotypeFmsDTO> annotations, Set<Long> idsAdded, Species species, ProcessDisplayHelper ph) {
		for (PhenotypeFmsDTO dto : annotations) {
			if (CollectionUtils.isNotEmpty(dto.getPrimaryGeneticEntityIds())) {
				continue;
			}

			try {
				Long primaryAnnotationId = phenotypeAnnotationService.upsertPrimaryAnnotation(dto, species);
				if (primaryAnnotationId != null) {
					history.incrementCompleted();
					if (idsAdded != null) {
						idsAdded.add(primaryAnnotationId);
					}
				}
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(dto, e.getMessage(), e.getStackTrace()));
			}
			
			ph.progressProcess();
			if (Thread.currentThread().isInterrupted()) {
				history.setErrorMessage(ApiErrorException.INTERRUPTED_MESSAGE);
				throw new RuntimeException(ApiErrorException.INTERRUPTED_MESSAGE);
			}
		}
		updateHistory(history);
	}

}
