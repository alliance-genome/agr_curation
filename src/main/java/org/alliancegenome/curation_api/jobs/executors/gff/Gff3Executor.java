package org.alliancegenome.curation_api.jobs.executors.gff;

import java.util.List;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.jobs.executors.LoadFileExecutor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.services.Gff3Service;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Gff3Executor extends LoadFileExecutor {

	@Inject Gff3Service gff3Service;

	protected String loadGenomeAssembly(String assemblyName, BulkLoadFileHistory history, List<String> gffHeaderData, BackendBulkDataProvider dataProvider, ProcessDisplayHelper ph) {
		try {
			assemblyName = gff3Service.loadGenomeAssembly(assemblyName, gffHeaderData, dataProvider);
			history.incrementCompleted();
		} catch (ObjectUpdateException e) {
			//e.printStackTrace();
			history.incrementFailed();
			addException(history, e.getData());
		} catch (Exception e) {
			e.printStackTrace();
			history.incrementFailed();
			addException(history, new ObjectUpdateExceptionData(gffHeaderData, e.getMessage(), e.getStackTrace()));
		}
		updateHistory(history);
		ph.progressProcess();
		
		return assemblyName;
	}

}
