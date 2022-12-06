package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class AlleleExecutor extends LoadFileExecutor {

	@Inject
	AlleleDAO alleleDAO;

	@Inject
	AlleleService alleleService;

	public void runLoad(BulkLoadFile bulkLoadFile) {

		try {
			BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
			log.info("Running with: " + manual.getDataType().name() + " " + manual.getDataType().getTaxonId());

			IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);
			bulkLoadFile.setLinkMLSchemaVersion(getVersionNumber(ingestDto.getLinkMLVersion()));
			List<AlleleDTO> alleles = ingestDto.getAlleleIngestSet();
			String taxonId = manual.getDataType().getTaxonId();
			String dataType = manual.getDataType().name();

			if (alleles != null) {
				bulkLoadFile.setRecordCount(alleles.size() + bulkLoadFile.getRecordCount());
				bulkLoadFileDAO.merge(bulkLoadFile);
				trackHistory(runLoad(taxonId, alleles, dataType), bulkLoadFile);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Gets called from the API directly
	public APIResponse runLoad(List<AlleleDTO> alleles) {
		List<String> taxonIds = alleles.stream().map(alleleDTO -> alleleDTO.getTaxonCurie()).distinct().collect(Collectors.toList());
		return runLoad(taxonIds, alleles, "API");
	}

	public APIResponse runLoad(String taxonId, List<AlleleDTO> alleles, String dataType) {
		List<String> taxonIds = new ArrayList<String>();
		taxonIds.add(taxonId);
		return runLoad(taxonIds, alleles, dataType);
	}

	public APIResponse runLoad(List<String> taxonIds, List<AlleleDTO> alleles, String dataType) {

		List<String> alleleCuriesBefore = new ArrayList<String>();
		for (String taxonId : taxonIds) {
			List<String> alleleCuries = alleleService.getCuriesByTaxonId(taxonId);
			log.debug("runLoad: Before: " + taxonId + " " + alleleCuries.size());
			alleleCuriesBefore.addAll(alleleCuries);
		}
		if (taxonIds.size() > 1)
			log.debug("runLoad: Before: total " + alleleCuriesBefore.size());

		List<String> alleleCuriesAfter = new ArrayList<>();
		BulkLoadFileHistory history = new BulkLoadFileHistory(alleles.size());
		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(processDisplayService);
		ph.startProcess("Allele Update " + taxonIds.toString(), alleles.size());
		alleles.forEach(alleleDTO -> {

			try {
				Allele allele = alleleService.upsert(alleleDTO);
				history.incrementCompleted();
				alleleCuriesAfter.add(allele.getCurie());
			} catch (ObjectUpdateException e) {
				addException(history, e.getData());
			} catch (Exception e) {
				addException(history, new ObjectUpdateExceptionData(alleleDTO, e.getMessage(), e.getStackTrace()));
			}

			ph.progressProcess();
		});
		ph.finishProcess();

		alleleService.removeOrDeprecateNonUpdatedAlleles(taxonIds.toString(), alleleCuriesBefore, alleleCuriesAfter, dataType);

		return new LoadHistoryResponce(history);
	}

}
