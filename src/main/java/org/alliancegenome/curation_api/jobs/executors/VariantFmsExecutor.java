package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.VariantFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.VariantIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.SpeciesService;
import org.alliancegenome.curation_api.services.VariantService;
import org.alliancegenome.curation_api.services.associations.AlleleVariantAssociationService;
import org.alliancegenome.curation_api.services.associations.CuratedVariantGenomicLocationAssociationService;
import org.alliancegenome.curation_api.services.validation.dto.fms.VariantFmsDTOValidator;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VariantFmsExecutor extends LoadFileExecutor {

	@Inject VariantService variantService;
	@Inject CuratedVariantGenomicLocationAssociationService curatedVariantGenomicLocationAssociationService;
	@Inject AlleleVariantAssociationService alleleVariantAssociationService;
	@Inject VariantFmsDTOValidator variantFmsDtoValidator;
	@Inject SpeciesService speciesService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) {
		try {
			VariantIngestFmsDTO variantData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())), VariantIngestFmsDTO.class);

			if (variantData.getMetaData() != null && StringUtils.isNotBlank(variantData.getMetaData().getRelease())) {
				bulkLoadFileHistory.getBulkLoadFile().setAllianceMemberReleaseVersion(variantData.getMetaData().getRelease());
			}

			Species species = bulkLoadFileHistory.getBulkLoad().getSpecies();

			List<Long> entityIdsAdded = new ArrayList<>();
			List<Long> locationIdsAdded = new ArrayList<>();
			List<Long> associationIdsAdded = new ArrayList<>();

			bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

			updateHistory(bulkLoadFileHistory);

			boolean success = runLoad(bulkLoadFileHistory, variantData.getData(), entityIdsAdded, locationIdsAdded, associationIdsAdded, species);
			if (success) {
				runCleanup(alleleVariantAssociationService, bulkLoadFileHistory, species.getDisplayName(), alleleVariantAssociationService.getAssociationsByDataProvider(species), associationIdsAdded, "Allele variant association");
				runCleanup(curatedVariantGenomicLocationAssociationService, bulkLoadFileHistory, species.getDisplayName(), curatedVariantGenomicLocationAssociationService.getIdsByDataProvider(species), locationIdsAdded, "Curated variant genomic location association");
				runCleanup(variantService, bulkLoadFileHistory, species.getDisplayName(), variantService.getIdsByDataProvider(species.getDisplayName()), entityIdsAdded, "Variant");
			}
			bulkLoadFileHistory.finishLoad();
			updateHistory(bulkLoadFileHistory);
			updateExceptions(bulkLoadFileHistory);
		} catch (Exception e) {
			failLoad(bulkLoadFileHistory, e);
			e.printStackTrace();
		}
	}

	private boolean runLoad(
			BulkLoadFileHistory history,
			List<VariantFmsDTO> data,
			List<Long> entityIdsAdded,
			List<Long> locationIdsAdded,
			List<Long> associationIdsAdded,
			Species species) {

		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("Variant update for " + species.getDisplayName(), data.size());
		
		history.setCount("Entities", data.size());
		history.setCount("Locations", data.size());
		history.setCount("Associations", data.size());
		updateHistory(history);
		
		String countType = null;
		for (VariantFmsDTO dto : data) {
			countType = "Entities";
			Long variantId = null;
			try {
				variantId = variantFmsDtoValidator.validateVariant(dto, entityIdsAdded, species);
				history.incrementCompleted(countType);
			} catch (ObjectUpdateException e) {
				history.incrementFailed(countType);
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed(countType);
				addException(history, new ObjectUpdateExceptionData(dto, e.getMessage(), e.getStackTrace()));
			}
			countType = "Locations";
			try {
				variantFmsDtoValidator.validateCuratedVariantGenomicLocationAssociation(dto, locationIdsAdded, variantId);
				history.incrementCompleted(countType);
			} catch (ObjectUpdateException e) {
				history.incrementFailed(countType);
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed(countType);
				addException(history, new ObjectUpdateExceptionData(dto, e.getMessage(), e.getStackTrace()));
			}
			countType = "Associations";
			try {
				variantFmsDtoValidator.validateAlleleVariantAssociation(dto, associationIdsAdded, variantId);
				history.incrementCompleted(countType);
			} catch (ObjectUpdateException e) {
				history.incrementFailed(countType);
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed(countType);
				addException(history, new ObjectUpdateExceptionData(dto, e.getMessage(), e.getStackTrace()));
			}
			ph.progressProcess();
			if (Thread.currentThread().isInterrupted()) {
				history.setErrorMessage(ApiErrorException.INTERRUPTED_MESSAGE);
				throw new RuntimeException(ApiErrorException.INTERRUPTED_MESSAGE);
			}
		}
		
		updateHistory(history);
		ph.finishProcess();
		
		return true;
	}

	public APIResponse runLoadApi(String dataProviderName, List<VariantFmsDTO> gffData) {
		List<Long> idsAdded = new ArrayList<>();
		Species species = speciesService.getByDisplayName(dataProviderName);
		BulkLoadFileHistory history = new BulkLoadFileHistory();
		history = bulkLoadFileHistoryDAO.persist(history);
		runLoad(history, gffData, idsAdded, idsAdded, idsAdded, species);
		history.finishLoad();
		
		return new LoadHistoryResponce(history);
	}
}
