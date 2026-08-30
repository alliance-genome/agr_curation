package org.alliancegenome.curation_api.interfaces;

import java.util.Map;

import org.alliancegenome.curation_api.response.ObjectResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/system")
@Tag(name = "System Endpoints")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface SystemControllerInterface {

	@GET
	@Path("/reindexeverything")
	void reindexEverything(
		@DefaultValue("1000") @QueryParam("batchSizeToLoadObjects") Integer batchSizeToLoadObjects,
		@DefaultValue("0") @QueryParam("idFetchSize") Integer idFetchSize,
		@DefaultValue("0") @QueryParam("limitIndexedObjectsTo") Integer limitIndexedObjectsTo,
		@DefaultValue("8") @QueryParam("threadsToLoadObjects") Integer threadsToLoadObjects,
		@DefaultValue("14400") @QueryParam("transactionTimeout") Integer transactionTimeout,
		@DefaultValue("4") @QueryParam("typesToIndexInParallel") Integer typesToIndexInParallel);

	@GET
	@Path("/sitesummary")
	ObjectResponse<Map<String, Object>> getSiteSummary();
	
	@GET
	@Path("/updatedauniqueids")
	void updateDiseaseAnnotationUniqueIds();

	// SCRUM-6078 backfill endpoint. Mints AGRKB curies for every
	// DiseaseAnnotation whose curie is currently NULL, in batches.
	// Idempotent. Remove after rollout on alpha/beta/prod.
	//
	// maxToMint caps the TOTAL number of annotations minted in a single call
	// so a cold full-table run cannot overwhelm the environment. 0 = no cap
	// (mint every NULL-curie annotation). Because the backfill is idempotent,
	// the endpoint can be called repeatedly with a bounded maxToMint to work
	// through the table in safe chunks.
	@GET
	@Path("/mintdacuries")
	void mintMissingDiseaseAnnotationCuries(
		@DefaultValue("1000") @QueryParam("batchSize") Integer batchSize,
		@DefaultValue("0") @QueryParam("maxToMint") Integer maxToMint);

	// SCRUM-6173 backfill endpoint. Mints AGRKB curies (MaTI subdomain "allele", code 106)
	// for every Allele whose curie is currently NULL. Idempotent.
	//
	// Every allele is in scope regardless of obsolete/internal, so this targets ~3.7M rows —
	// roughly 38x the disease-annotation backfill. No supporting index is needed (the batch fetch
	// is driven from allele by a forward id cursor on the primary key), but work through the table
	// with a bounded maxToMint rather than in one pass.
	@GET
	@Path("/mintallelecuries")
	void mintMissingAlleleCuries(
		@DefaultValue("1000") @QueryParam("batchSize") Integer batchSize,
		@DefaultValue("0") @QueryParam("maxToMint") Integer maxToMint);

	// SCRUM-6359 / SCRUM-6360 backfill endpoints. Each mints AGRKB curies for every row of its
	// class whose curie is currently NULL, from the MaTI subdomain registered for that class under
	// SCRUM-6358. All of them share one implementation (CurieMintService + BaseCurieSQLDAO), so the
	// semantics are identical everywhere: idempotent, resumable, batched, and capped by maxToMint
	// (0 = no cap). Work large classes through the table in bounded chunks rather than one pass.
	//
	// Not covered here: molecular and genetic interactions and the two HTP expression classes,
	// which have no curie column yet — see SCRUM-6463. Expression experiments and expression
	// annotations are on hold pending direct submissions.

	// Gene — 2.4M rows, subdomain GENE.
	@GET
	@Path("/mintgenecuries")
	void mintMissingGeneCuries(
		@DefaultValue("1000") @QueryParam("batchSize") Integer batchSize,
		@DefaultValue("0") @QueryParam("maxToMint") Integer maxToMint);

	// Variant — 101K rows, subdomain VARIANT.
	@GET
	@Path("/mintvariantcuries")
	void mintMissingVariantCuries(
		@DefaultValue("1000") @QueryParam("batchSize") Integer batchSize,
		@DefaultValue("0") @QueryParam("maxToMint") Integer maxToMint);

	// AffectedGenomicModel — 795K rows, subdomain AGM.
	@GET
	@Path("/mintagmcuries")
	void mintMissingAgmCuries(
		@DefaultValue("1000") @QueryParam("batchSize") Integer batchSize,
		@DefaultValue("0") @QueryParam("maxToMint") Integer maxToMint);

	// Construct — 237K rows, subdomain CONSTRUCT.
	@GET
	@Path("/mintconstructcuries")
	void mintMissingConstructCuries(
		@DefaultValue("1000") @QueryParam("batchSize") Integer batchSize,
		@DefaultValue("0") @QueryParam("maxToMint") Integer maxToMint);

	// Antibody — 27K rows, subdomain ANTIBODY.
	@GET
	@Path("/mintantibodycuries")
	void mintMissingAntibodyCuries(
		@DefaultValue("1000") @QueryParam("batchSize") Integer batchSize,
		@DefaultValue("0") @QueryParam("maxToMint") Integer maxToMint);

	// SequenceTargetingReagent — 31K rows, subdomain SEQUENCE_TARGETING_REAGENT.
	@GET
	@Path("/mintstrcuries")
	void mintMissingStrCuries(
		@DefaultValue("1000") @QueryParam("batchSize") Integer batchSize,
		@DefaultValue("0") @QueryParam("maxToMint") Integer maxToMint);

	// AssemblyComponent — 4K rows, subdomain ASSEMBLY_COMPONENT.
	@GET
	@Path("/mintassemblycomponentcuries")
	void mintMissingAssemblyComponentCuries(
		@DefaultValue("1000") @QueryParam("batchSize") Integer batchSize,
		@DefaultValue("0") @QueryParam("maxToMint") Integer maxToMint);

	// GenomeAssembly — 14 rows, subdomain GENOME_ASSEMBLY.
	@GET
	@Path("/mintgenomeassemblycuries")
	void mintMissingGenomeAssemblyCuries(
		@DefaultValue("1000") @QueryParam("batchSize") Integer batchSize,
		@DefaultValue("0") @QueryParam("maxToMint") Integer maxToMint);

	// PhenotypeAnnotation — 1.8M rows, subdomain PHENOTYPE_ANNOTATION.
	@GET
	@Path("/mintphenotypeannotationcuries")
	void mintMissingPhenotypeAnnotationCuries(
		@DefaultValue("1000") @QueryParam("batchSize") Integer batchSize,
		@DefaultValue("0") @QueryParam("maxToMint") Integer maxToMint);

	@DELETE
	@Path("/deletedUnusedConditionsAndExperiments")
	void deleteUnusedConditionsAndExperiments();

}
