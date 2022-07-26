package org.alliancegenome.curation_api.interfaces.crud;


import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.interfaces.*;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.ingest.dto.*;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/allele")
@Tag(name = "CRUD - Alleles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AlleleCrudInterface extends BaseCurieCrudInterface<Allele>, BaseDTOCrudControllerInterface<Allele, AlleleDTO> {

	@POST
	@Path("/bulk/alleles")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateAlleles(List<AlleleDTO> alleleData);

	@Override
	@JsonView(View.FieldsAndLists.class)
	public ObjectResponse<Allele> get(@PathParam("curie") String curie);

	@Override
	@GET
	@Path("/reindex")
	@Tag(name = "Reindex Endpoints")
	public void reindex(
		@DefaultValue("50") @QueryParam("batchSizeToLoadObjects") Integer batchSizeToLoadObjects,
		@DefaultValue("0") @QueryParam("idFetchSize") Integer idFetchSize,
		@DefaultValue("0") @QueryParam("limitIndexedObjectsTo") Integer limitIndexedObjectsTo,
		@DefaultValue("1") @QueryParam("threadsToLoadObjects") Integer threadsToLoadObjects,
		@DefaultValue("7200") @QueryParam("transactionTimeout") Integer transactionTimeout,
		@DefaultValue("1") @QueryParam("typesToIndexInParallel") Integer typesToIndexInParallel
	);

}
