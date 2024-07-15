package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Molecule;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.MoleculeIngestFmsDTO;
import org.alliancegenome.curation_api.services.MoleculeService;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MoleculeExecutor extends LoadFileExecutor {

	@Inject MoleculeService moleculeService;

	public void execLoad(BulkLoadFile bulkLoadFile) {
		try {
			MoleculeIngestFmsDTO moleculeData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), MoleculeIngestFmsDTO.class);
			bulkLoadFile.setRecordCount(moleculeData.getData().size());
			if (bulkLoadFile.getLinkMLSchemaVersion() == null) {
				AGRCurationSchemaVersion version = Molecule.class.getAnnotation(AGRCurationSchemaVersion.class);
				bulkLoadFile.setLinkMLSchemaVersion(version.max());
			}
			if (moleculeData.getMetaData() != null && StringUtils.isNotBlank(moleculeData.getMetaData().getRelease())) {
				bulkLoadFile.setAllianceMemberReleaseVersion(moleculeData.getMetaData().getRelease());
			}
			bulkLoadFileDAO.merge(bulkLoadFile);

			BulkLoadFileHistory history = new BulkLoadFileHistory(moleculeData.getData().size());
			createHistory(history, bulkLoadFile);
			runLoad(moleculeService, history, null, moleculeData.getData(), null);
			history.finishLoad();
			finalSaveHistory(history);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
