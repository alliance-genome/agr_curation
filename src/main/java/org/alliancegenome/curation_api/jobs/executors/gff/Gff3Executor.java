package org.alliancegenome.curation_api.jobs.executors.gff;

import java.util.List;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.jobs.executors.LoadFileExecutor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.services.Gff3Service;
import org.alliancegenome.curation_api.services.GenomeAssemblyService;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Gff3Executor extends LoadFileExecutor {

	@Inject Gff3Service gff3Service;
	@Inject GenomeAssemblyService genomeAssemblyService;

	public String loadGenomeAssemblyFromGFF(List<String> gffHeaderData) throws ValidationException {
		for (String header : gffHeaderData) {
			if (header.startsWith("#!assembly")) {
				String assemblyName = header.split(" ")[1];
				return assemblyName;
			}
		}
		return null;
	}

	/**
	 * SCRUM-6080: a GFF load may only proceed if a GenomeAssembly with the name
	 * declared in the file header already exists for the load's data provider.
	 * If the header carries no assembly, or no matching assembly exists, the whole
	 * load is failed and no records are imported (assemblies are no longer
	 * auto-created during a load).
	 *
	 * @return true if the load may proceed; false if the load was failed (the caller
	 *         must return without importing).
	 */
	protected boolean validateGffAssembly(BulkLoadFileHistory history, BackendBulkDataProvider dataProvider, String headerAssembly) {
		if (StringUtils.isBlank(headerAssembly)) {
			failLoad(history, new ObjectValidationException(null,
				"GFF header contains no assembly (expected a '#!assembly <id>' line) for "
				+ dataProvider.name() + " - load aborted"));
			return false;
		}

		if (genomeAssemblyService.findByName(headerAssembly, dataProvider) == null) {
			failLoad(history, new ObjectValidationException(null,
				"No assembly with name " + headerAssembly + " found"));
			return false;
		}

		return true;
	}

}
