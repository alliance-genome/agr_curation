package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.GeneToGeneParalogy;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ParalogyFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ParalogyIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.GeneToGeneParalogyService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ParalogyExecutor extends LoadFileExecutor {

	@Inject GeneToGeneParalogyService geneToGeneParalogyService;

	public void execLoad(BulkLoadFile bulkLoadFile) {
		try {
			BulkFMSLoad fms = (BulkFMSLoad) bulkLoadFile.getBulkLoad();

			ParalogyIngestFmsDTO paralogyData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), ParalogyIngestFmsDTO.class);
			bulkLoadFile.setRecordCount(paralogyData.getData().size());

			AGRCurationSchemaVersion version = GeneToGeneParalogy.class.getAnnotation(AGRCurationSchemaVersion.class);
			bulkLoadFile.setLinkMLSchemaVersion(version.max());
			if (paralogyData.getMetaData() != null && StringUtils.isNotBlank(paralogyData.getMetaData().getRelease())) {
				bulkLoadFile.setAllianceMemberReleaseVersion(paralogyData.getMetaData().getRelease());
			}

			List<Pair<String, String>> paralogyPairsLoaded = new ArrayList<>();
			// String dataProviderAbbreviation = fms.getFmsDataSubType();
			// List<Object[]> orthoPairsBefore =
			// generatedOrthologyService.getAllOrthologyPairsBySubjectGeneDataProvider(dataProviderAbbreviation);
			// log.debug("runLoad: Before: total " + orthoPairsBefore.size());

			bulkLoadFileDAO.merge(bulkLoadFile);

			BulkLoadFileHistory history = new BulkLoadFileHistory(paralogyData.getData().size());

			createHistory(history, bulkLoadFile);
			runLoad(history, fms.getFmsDataSubType(), paralogyData, paralogyPairsLoaded);

			// runCleanup(history, fms.getFmsDataSubType());

			history.finishLoad();

			finalSaveHistory(history);

		} catch (Exception e) {
			failLoad(bulkLoadFile, e);
			e.printStackTrace();
		}
	}

	private void runLoad(BulkLoadFileHistory history, String dataProvider, ParalogyIngestFmsDTO paralogyData, List<Pair<String, String>> paralogyPairsAdded) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess(dataProvider + " Paralogy DTO Update", paralogyData.getData().size());

		for (ParalogyFmsDTO paralogyPairDTO : paralogyData.getData()) {
			try {
				GeneToGeneParalogy paralogyPair = geneToGeneParalogyService.upsert(paralogyPairDTO);
				history.incrementCompleted();
				// if (paralogyPairsAdded != null)
				// paralogyPairsAdded.add(Pair.of(paralogyPair.getSubjectGene().getCurie(),
				// paralogyPair.getObjectGene().getCurie()));
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(paralogyPairDTO, e.getMessage(), e.getStackTrace()));
			}
			updateHistory(history);
			ph.progressProcess();
		}
		ph.finishProcess();

	}

	public APIResponse runLoad(String dataProvider, ParalogyIngestFmsDTO paralogyData) {
		List<Pair<String, String>> paralogyPairsAdded = new ArrayList<>();

		BulkLoadFileHistory history = new BulkLoadFileHistory(paralogyData.getData().size());
		runLoad(history, dataProvider, paralogyData, paralogyPairsAdded);
		history.finishLoad();

		return new LoadHistoryResponce(history);
	}

}
