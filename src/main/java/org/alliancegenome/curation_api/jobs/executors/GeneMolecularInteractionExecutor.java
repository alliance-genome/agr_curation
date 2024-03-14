package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.dao.GeneMolecularInteractionDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.jobs.util.CsvSchemaBuilder;
import org.alliancegenome.curation_api.model.entities.GeneMolecularInteraction;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.GeneMolecularInteractionService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GeneMolecularInteractionExecutor extends LoadFileExecutor {

	@Inject
	GeneMolecularInteractionDAO geneMolecularInteractionDAO;
	@Inject
	GeneMolecularInteractionService geneMolecularInteractionService;
	
	public void runLoad(BulkLoadFile bulkLoadFile) {
		try {
			
			BulkFMSLoad fmsLoad = (BulkFMSLoad) bulkLoadFile.getBulkLoad();
			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fmsLoad.getFmsDataSubType());
			
			CsvSchema psiMiTabSchema = CsvSchemaBuilder.psiMiTabSchema();
			MappingIterator<PsiMiTabDTO> it = csvMapper.enable(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS)
					.readerFor(PsiMiTabDTO.class).with(psiMiTabSchema)
					.readValues(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())));
			List<PsiMiTabDTO> interactionData = it.readAll();
			
			BulkLoadFileHistory history = new BulkLoadFileHistory(interactionData.size());
			
			List<Long> interactionIdsLoaded = new ArrayList<>();
			List<Long> interactionIdsBefore = geneMolecularInteractionDAO.findAllIds().getResults();
			
			//runLoad(history, interactionData), annotationIdsLoaded, dataProvider);
			
			//runCleanup(phenotypeAnnotationService, history, interactionIdsBefore, interactionIdsLoaded, "phenotype annotation", bulkLoadFile.getMd5Sum());

			history.finishLoad();
			
			trackHistory(history, bulkLoadFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Gets called from the API directly
	public APIResponse runLoad(List<PsiMiTabDTO> interactions) {
		List<Long> interactionIdsLoaded = new ArrayList<>();
		
		BulkLoadFileHistory history = new BulkLoadFileHistory(interactions.size());
		runLoad(history, interactions, interactionIdsLoaded);
		history.finishLoad();
		
		return new LoadHistoryResponce(history);
	}

	
	private void runLoad(BulkLoadFileHistory history, List<PsiMiTabDTO> interactions, List<Long> idsAdded) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("Gene Molecular Interaction DTO Update", interactions.size());
		for (PsiMiTabDTO dto : interactions) {
			try {
				GeneMolecularInteraction interaction = geneMolecularInteractionService.upsert(dto);
				if (interaction != null) {
					history.incrementCompleted();
					if (idsAdded != null)
						idsAdded.add(interaction.getId());
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
		}
		ph.finishProcess();

	}

}
