package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.dao.GeneMolecularInteractionDAO;
import org.alliancegenome.curation_api.jobs.util.CsvSchemaBuilder;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.services.GeneInteractionService;
import org.alliancegenome.curation_api.services.GeneMolecularInteractionService;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GeneMolecularInteractionExecutor extends LoadFileExecutor {

	@Inject GeneMolecularInteractionDAO geneMolecularInteractionDAO;
	@Inject GeneMolecularInteractionService geneMolecularInteractionService;
	@Inject GeneInteractionService geneInteractionService;

	public void execLoad(BulkLoadFile bulkLoadFile) {
		try {

			CsvSchema psiMiTabSchema = CsvSchemaBuilder.psiMiTabSchema();
			CsvMapper csvMapper = new CsvMapper();
			MappingIterator<PsiMiTabDTO> it = csvMapper.enable(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS).readerFor(PsiMiTabDTO.class).with(psiMiTabSchema).readValues(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())));
			List<PsiMiTabDTO> interactionData = it.readAll();

			List<Long> interactionIdsLoaded = new ArrayList<>();
			List<Long> interactionIdsBefore = geneMolecularInteractionDAO.findAllIds().getResults();
			
			BulkLoadFileHistory history = new BulkLoadFileHistory(interactionData.size());
			createHistory(history, bulkLoadFile);
			boolean success = runLoad(geneMolecularInteractionService, history, null, interactionData, interactionIdsLoaded, false);
			if (success) {
				runCleanup(geneInteractionService, history, "COMBINED", interactionIdsBefore, interactionIdsLoaded, "gene molecular interaction", bulkLoadFile.getMd5Sum());
			}
			history.finishLoad();
			finalSaveHistory(history);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
