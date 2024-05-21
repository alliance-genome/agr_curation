package org.alliancegenome.curation_api.jobs.executors.associations;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.jobs.executors.LoadFileExecutor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;

@JBossLog
@ApplicationScoped
public class ParalogyExecutor extends LoadFileExecutor {
	public void execLoad(BulkLoadFile bulkLoadFile) {
		try {

		} catch (Exception e) {
			failLoad(bulkLoadFile, e);
			e.printStackTrace();
		}
	}



}
