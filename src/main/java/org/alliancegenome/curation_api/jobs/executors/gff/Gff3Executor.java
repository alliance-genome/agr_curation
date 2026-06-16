package org.alliancegenome.curation_api.jobs.executors.gff;

import java.util.List;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.jobs.executors.LoadFileExecutor;
import org.alliancegenome.curation_api.model.entities.GenomeAssembly;
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

	/**
	 * SCRUM-6080: parse the assembly declared in the GFF header ({@code #!assembly}) and
	 * validate it against the official assembly designated for the species in the Species
	 * table ({@link Species#getGenomeAssembly()}). Returns the header assembly name only
	 * when it matches the official assembly; otherwise fails the whole load (via
	 * {@code failLoad}) and returns {@code null} so the caller imports nothing. The load is
	 * failed when the header carries no assembly, when no official assembly is designated
	 * for the provider's taxon, or when the header assembly does not match it.
	 */
	public String loadGenomeAssemblyFromGFF(BulkLoadFileHistory history, List<String> gffHeaderData, BackendBulkDataProvider dataProvider) throws ValidationException {
		String headerAssembly = null;
		for (String header : gffHeaderData) {
			if (header.startsWith("#!assembly")) {
				headerAssembly = header.split(" ")[1];
				break;
			}
		}

		if (StringUtils.isBlank(headerAssembly)) {
			failLoad(history, new ObjectValidationException(null,
				"GFF header contains no assembly (expected a '#!assembly <id>' line) for "
				+ dataProvider.name() + " - load aborted"));
			return null;
		}

		Species species = speciesService.getByTaxonCurie(dataProvider.canonicalTaxonCurie);
		GenomeAssembly officialAssembly = (species != null) ? species.getGenomeAssembly() : null;

		if (officialAssembly == null) {
			failLoad(history, new ObjectValidationException(null,
				"No official assembly is designated in the Species table for " + dataProvider.name()
				+ " (taxon " + dataProvider.canonicalTaxonCurie + "); cannot load GFF with header assembly '"
				+ headerAssembly + "' - load aborted"));
			return null;
		}

		if (!headerAssembly.equals(officialAssembly.getPrimaryExternalId())) {
			failLoad(history, new ObjectValidationException(null,
				"GFF header assembly '" + headerAssembly + "' does not match the official assembly '"
				+ officialAssembly.getPrimaryExternalId() + "' designated for " + dataProvider.name()
				+ " in the Species table - load aborted"));
			return null;
		}

		return headerAssembly;
	}

}
