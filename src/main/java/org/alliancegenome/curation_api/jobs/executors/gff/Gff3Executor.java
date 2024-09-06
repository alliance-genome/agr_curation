package org.alliancegenome.curation_api.jobs.executors.gff;

import java.util.List;

import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.jobs.executors.LoadFileExecutor;
import org.alliancegenome.curation_api.services.Gff3Service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Gff3Executor extends LoadFileExecutor {

	@Inject Gff3Service gff3Service;
	
	public String loadGenomeAssemblyFromGFF(List<String> gffHeaderData) throws ObjectUpdateException {
		for (String header : gffHeaderData) {
			if (header.startsWith("#!assembly")) {
				String assemblyName = header.split(" ")[1];
				return assemblyName;
			}
		}
		return null;
	}
	
//	
//
//	protected String loadGenomeAssembly(String assemblyName, BulkLoadFileHistory history, List<String> gffHeaderData, BackendBulkDataProvider dataProvider) {
//		try {
//			assemblyName = gff3Service.loadGenomeAssembly(assemblyName, gffHeaderData, dataProvider);
//		} catch (ObjectUpdateException e) {
//			//e.printStackTrace();
//			history.incrementFailed("Assembly");
//			addException(history, e.getData());
//		} catch (Exception e) {
//			e.printStackTrace();
//			history.incrementFailed("Assembly");
//			addException(history, new ObjectUpdateExceptionData(gffHeaderData, e.getMessage(), e.getStackTrace()));
//		}
//		updateHistory(history);
//		
//		return assemblyName;
//	}
	
	

	
}
