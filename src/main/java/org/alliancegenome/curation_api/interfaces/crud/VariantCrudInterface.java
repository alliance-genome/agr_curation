package org.alliancegenome.curation_api.interfaces.crud;

import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseSubmittedObjectCrudInterface;
import org.alliancegenome.curation_api.interfaces.base.BaseUpsertControllerInterface;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.ingest.dto.VariantDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.VariantFmsDTO;
import org.alliancegenome.curation_api.model.output.AllModVariants;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/variant")
@Tag(name = "CRUD - Variants")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface VariantCrudInterface extends BaseSubmittedObjectCrudInterface<Variant>, BaseUpsertControllerInterface<Variant, VariantDTO> {

	@Operation(summary = "Bulk load variant data", description = "Bulk load variant records from a data provider submission")
	@POST
	@Path("/bulk/{dataProvider}/variants")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateVariants(@PathParam("dataProvider") String dataProvider, List<VariantDTO> alleleData);

	@Operation(summary = "Bulk load variant data", description = "Bulk load variant records from a data provider submission")
	@POST
	@Path("/bulk/{dataProvider}/fmsvariants")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateFmsVariants(@PathParam("dataProvider") String dataProvider, List<VariantFmsDTO> alleleData);

	@Operation(summary = "Export the HGVS of a data provider's variants", description = "A page of the data provider's allele - variant pairs, by allele: the allele's ID, the variant's AGRKB curie, the genomic HGVS (HGVS.g) of its curated location, the coding (HGVS.c) and protein (HGVS.p) HGVS of its predicted consequences, one per transcript, and its predicted variant consequences, the VEP result for each transcript. Obsolete alleles, variants and links are left out. page=0&limit=0 returns only totalResults, the number of pairs; page through them with page and limit (at most 10000).")
	@GET
	@Path("/export/{dataProvider}")
	@JsonView(CurationView.AllModVariantsView.class)
	SearchResponse<AllModVariants> getAllModVariants(@PathParam("dataProvider") String dataProvider, @DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("1000") @QueryParam("limit") Integer limit);

	@Override
	@GET
	@JsonView(CurationView.VariantDetailView.class)
	@Path("/{identifierString}")
	ObjectResponse<Variant> getByIdentifier(@PathParam("identifierString") String identifierString);

	@Override
	@PUT
	@Path("/")
	@JsonView(CurationView.VariantView.class)
	ObjectResponse<Variant> update(Variant entity);

	@Override
	@POST
	@Path("/")
	@JsonView(CurationView.VariantView.class)
	ObjectResponse<Variant> create(Variant entity);

	@Override
	@POST
	@Path("/find")
	@Tag(name = "Relational Database Browsing Endpoints")
	@JsonView(CurationView.VariantView.class)
	SearchResponse<Variant> find(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, HashMap<String, Object> params);

	@Override
	@POST
	@Path("/search")
	@Tag(name = "Elastic Search Browsing Endpoints")
	@JsonView({ CurationView.VariantView.class })
	SearchResponse<Variant> search(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, HashMap<String, Object> params);

}
