package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.model.ingest.fms.dto.*;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.MoleculeService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class MoleculeExecutor extends LoadFileExecutor {

	@Inject MoleculeService moleculeService;
	
	public void runLoad(BulkLoadFile bulkLoadFile) {
		try {
			MoleculeMetaDataFmsDTO moleculeData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), MoleculeMetaDataFmsDTO.class);
			bulkLoadFile.setRecordCount(moleculeData.getData().size());
			bulkLoadFileDAO.merge(bulkLoadFile);
			
			trackHistory(runLoad(moleculeData), bulkLoadFile);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Gets called from the API directly
	public APIResponse runLoad(MoleculeMetaDataFmsDTO moleculeData) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
		ph.startProcess("Molecule DTO Update", moleculeData.getData().size());

		BulkLoadFileHistory history = new BulkLoadFileHistory(moleculeData.getData().size());
		for(MoleculeFmsDTO molecule: moleculeData.getData()) {
			try {
				moleculeService.processUpdate(molecule);
				history.incrementCompleted();
			} catch (ObjectUpdateException e) {
				addException(history, e.getData());
			}
			ph.progressProcess();
		}
		ph.finishProcess();
		
		return new LoadHistoryResponce(history);
	}

}
