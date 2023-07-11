package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Molecule;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.MoleculeFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.MoleculeIngestFmsDTO;
import org.alliancegenome.curation_api.services.MoleculeService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

@ApplicationScoped
public class MoleculeExecutor extends LoadFileExecutor {

	@Inject
	MoleculeService moleculeService;

	public void runLoad(BulkLoadFile bulkLoadFile) {
		try {
			MoleculeIngestFmsDTO moleculeData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), MoleculeIngestFmsDTO.class);
			bulkLoadFile.setRecordCount(moleculeData.getData().size());
			if (bulkLoadFile.getLinkMLSchemaVersion() == null) {
				AGRCurationSchemaVersion version = Molecule.class.getAnnotation(AGRCurationSchemaVersion.class);
				bulkLoadFile.setLinkMLSchemaVersion(version.max());
			}
			bulkLoadFileDAO.merge(bulkLoadFile);

			BulkLoadFileHistory history = new BulkLoadFileHistory(moleculeData.getData().size());
			
			runLoad(history, moleculeData);

			history.finishLoad();
			
			trackHistory(history, bulkLoadFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Gets called from the API directly
	public void runLoad(BulkLoadFileHistory history, MoleculeIngestFmsDTO moleculeData) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(processDisplayService);
		ph.startProcess("Molecule DTO Update", moleculeData.getData().size());

		for (MoleculeFmsDTO molecule : moleculeData.getData()) {
			try {
				moleculeService.processUpdate(molecule);
				history.incrementCompleted();
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			}
			ph.progressProcess();
		}
		ph.finishProcess();

	}

}
