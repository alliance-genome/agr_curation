package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.dao.PredictedVariantConsequenceDAO;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.jobs.util.CsvSchemaBuilder;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.VepTxtDTO;
import org.alliancegenome.curation_api.services.PredictedVariantConsequenceService;
import org.alliancegenome.curation_api.services.SpeciesService;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VepTranscriptExecutor extends LoadFileExecutor {

	@Inject PredictedVariantConsequenceDAO predictedVariantConsequenceDAO;
	@Inject PredictedVariantConsequenceService predictedVariantConsequenceService;
	@Inject SpeciesService speciesService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) {
		try {

			CsvSchema vepTxtSchema = CsvSchemaBuilder.vepTxtSchema();
			CsvMapper csvMapper = new CsvMapper();
			MappingIterator<VepTxtDTO> it = csvMapper.enable(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS).readerFor(VepTxtDTO.class).with(vepTxtSchema).readValues(new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())));
			List<VepTxtDTO> vepData = it.readAll();
			

			BulkFMSLoad fmsLoad = (BulkFMSLoad) bulkLoadFileHistory.getBulkLoad();
			Species species = speciesService.getByDisplayName(fmsLoad.getFmsDataSubType());

			List<Long> consequenceIdsLoaded = new ArrayList<>();
			List<Long> consequenceIdsBefore = predictedVariantConsequenceService.getIdsByDataProvider(species);
			
			bulkLoadFileHistory.setCount(vepData.size());
			updateHistory(bulkLoadFileHistory);
			
			boolean success = runLoad(predictedVariantConsequenceService, bulkLoadFileHistory, species, vepData, consequenceIdsLoaded);
			if (success) {
				runCleanup(predictedVariantConsequenceService, bulkLoadFileHistory, species.getDisplayName(), consequenceIdsBefore, consequenceIdsLoaded, "predicted variant consequences");
			}
			bulkLoadFileHistory.finishLoad();
			updateHistory(bulkLoadFileHistory);
			updateExceptions(bulkLoadFileHistory);

		} catch (Exception e) {
			failLoad(bulkLoadFileHistory, e);
			e.printStackTrace();
		}
	}

}
