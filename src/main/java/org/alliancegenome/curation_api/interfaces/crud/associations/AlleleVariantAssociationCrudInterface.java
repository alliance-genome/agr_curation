package org.alliancegenome.curation_api.interfaces.crud.associations;

import java.util.Map;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.associations.AlleleVariantAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/allelevariantassociation")
@Tag(name = "CRUD - Allele Variant Associations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AlleleVariantAssociationCrudInterface extends BaseIdCrudInterface<AlleleVariantAssociation> {

	@GET
	@Path("/findBy")
	@JsonView(View.FieldsAndLists.class)
	ObjectResponse<AlleleVariantAssociation> getAssociation(@QueryParam("alleleId") Long alleleId, @QueryParam("relationName") String relationName, @QueryParam("variantId") Long variantId);

	@GET
	@Path("/alleleVariantAssociationMap")
	@JsonView(View.FieldsAndLists.class)
	ObjectResponse<Map<String, Long>> alleleVariantAssociationMap();
}
