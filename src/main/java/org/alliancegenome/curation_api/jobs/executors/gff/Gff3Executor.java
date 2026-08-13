package org.alliancegenome.curation_api.jobs.executors.gff;

import java.util.List;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.jobs.executors.LoadFileExecutor;
import org.alliancegenome.curation_api.model.entities.GenomeAssembly;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.services.Gff3Service;
import org.alliancegenome.curation_api.services.SpeciesService;
import org.apache.commons.lang3.StringUtils;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Gff3Executor extends LoadFileExecutor {

	@Inject Gff3Service gff3Service;
	@Inject SpeciesService speciesService;

	/** Matches the cutoff {@code LoadFileExecutor.runLoad} applies to non-GFF loads. */
	protected static final double ERROR_RATE_CUTOFF = 0.25;

	private static final List<String> GFF_COUNT_TYPES = List.of("Entities", "Locations", "Associations");

	/**
	 * SCRUM-6258: the GFF executors each drive their own batch loop rather than going through
	 * {@code LoadFileExecutor.runLoad}, so until now nothing checked the failure rate and no GFF
	 * load could ever abort on one. The ZFIN GFF Transcript load on beta reported
	 * {@code FINISHED} with zero errorMessage while failing all 131,099 gene associations and
	 * creating zero locations, then ran cleanup and deleted 49,089 transcripts.
	 *
	 * Returns true when any of the GFF count types is failing above the cutoff, so the caller
	 * can stop and mark the load FAILED instead of carrying on into cleanup.
	 */
	protected boolean aboveErrorRateCutoff(BulkLoadFileHistory history) {
		for (String countType : GFF_COUNT_TYPES) {
			double errorRate = history.getErrorRate(countType);
			if (errorRate > ERROR_RATE_CUTOFF) {
				Log.error("GFF load failure rate for " + countType + " is " + errorRate
					+ " (cutoff " + ERROR_RATE_CUTOFF + ") - aborting load");
				return true;
			}
		}
		return false;
	}

	/**
	 * SCRUM-6080: parse the assembly declared in the GFF header ({@code #!assembly}) and
	 * validate it against the official assembly designated for the species in the Species
	 * table ({@link Species#getGenomeAssembly()}). Returns the header assembly name only
	 * when it matches the official assembly; otherwise fails the whole load (via
	 * {@code failLoad}) and returns {@code null} so the caller imports nothing. The load is
	 * failed when the header carries no assembly, when no official assembly is designated
	 * for the provider's taxon, or when the header assembly does not match it.
	 */
	public String loadGenomeAssemblyFromGFF(BulkLoadFileHistory history, List<String> gffHeaderData, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		String headerAssembly = null;
		for (String header : gffHeaderData) {
			if (header.startsWith("#!assembly")) {
				headerAssembly = header.split(" ")[1];
				break;
			}
		}

		if (StringUtils.isBlank(headerAssembly)) {
			throw new ObjectUpdateException(null,
				"GFF header contains no assembly (expected a '#!assembly <id>' line) for "
					+ dataProvider.name() + " - load aborted");
		}

		Species species = speciesService.getByTaxonCurie(dataProvider.canonicalTaxonCurie);
		GenomeAssembly officialAssembly = (species != null) ? species.getGenomeAssembly() : null;

		if (officialAssembly == null) {
			throw new ObjectUpdateException(null,
				"No official assembly is designated in the Species table for " + dataProvider.name()
					+ " (taxon " + dataProvider.canonicalTaxonCurie + "); cannot load GFF with header assembly '"
					+ headerAssembly + "' - load aborted");
		}

		if (!headerAssembly.equals(officialAssembly.getPrimaryExternalId())) {
			throw new ObjectUpdateException(null,
				"GFF header assembly '" + headerAssembly + "' does not match the official assembly '"
					+ officialAssembly.getPrimaryExternalId() + "' designated for " + dataProvider.name()
					+ " in the Species table - load aborted");
		}

		return headerAssembly;
	}

}
