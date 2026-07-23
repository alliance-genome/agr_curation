package org.alliancegenome.curation_api.interfaces.crud;

import java.util.HashMap;

import org.alliancegenome.curation_api.interfaces.base.BaseSubmittedObjectCrudInterface;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/biologicalentity")
@Tag(name = "CRUD - Biological Entities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BiologicalEntityCrudInterface extends BaseSubmittedObjectCrudInterface<BiologicalEntity> {

	// Override search() to FieldsOnly (instead of the inherited FieldsAndLists),
	// matching the per-type search endpoints (/gene, /allele, /agm, ...). On this
	// cross-subtype index FieldsAndLists serializes association collections whose
	// cycle-guards are incomplete across subtypes, producing a >2GB serialization /
	// OutOfMemoryError. Callers needing associations use get-by-id.
	@Override
	@POST
	@Path("/search")
	@Tag(name = "Elastic Search Browsing Endpoints")
	@JsonView({ CurationView.FieldsOnly.class })
	SearchResponse<BiologicalEntity> search(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, HashMap<String, Object> params);

}
