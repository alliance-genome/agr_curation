package org.alliancegenome.curation_api.jobs.executors.gff;

import java.util.List;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.jobs.executors.LoadFileExecutor;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.services.Gff3Service;
import org.alliancegenome.curation_api.services.SpeciesService;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Gff3Executor extends LoadFileExecutor {

	@Inject Gff3Service gff3Service;
	@Inject SpeciesService speciesService;

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
	 * SCRUM-6080: a GFF load may only proceed if the assembly declared in the file
	 * header matches the official assembly designated for the species in the Species
	 * table ({@link Species#getGenomeAssembly()}). On any mismatch the whole load is
	 * failed and no records are imported.
	 *
	 * @return true if the load may proceed; false if the load was failed (the caller
	 *         must return without importing).
	 */
	protected boolean validateGffAssemblyMatchesSpecies(BulkLoadFileHistory history, BackendBulkDataProvider dataProvider, String headerAssembly) {
		if (StringUtils.isBlank(headerAssembly)) {
			failLoad(history, new ObjectValidationException(null,
				"GFF header contains no assembly (expected a '#!assembly <id>' line); cannot validate against the official assembly for "
				+ dataProvider.name() + " - load aborted"));
			return false;
		}

		Species species = speciesService.getByTaxonCurie(dataProvider.canonicalTaxonCurie);
		String officialAssembly = (species != null && species.getGenomeAssembly() != null)
			? species.getGenomeAssembly().getPrimaryExternalId()
			: null;

		if (officialAssembly == null) {
			failLoad(history, new ObjectValidationException(null,
				"No official assembly is designated in the Species table for " + dataProvider.name()
				+ " (taxon " + dataProvider.canonicalTaxonCurie + "); cannot load GFF with header assembly '"
				+ headerAssembly + "' - load aborted"));
			return false;
		}

		if (!officialAssembly.equals(headerAssembly)) {
			failLoad(history, new ObjectValidationException(null,
				"GFF header assembly '" + headerAssembly + "' does not match the official assembly '" + officialAssembly
				+ "' designated for " + dataProvider.name() + " in the Species table - load aborted"));
			return false;
		}

		return true;
	}

}
