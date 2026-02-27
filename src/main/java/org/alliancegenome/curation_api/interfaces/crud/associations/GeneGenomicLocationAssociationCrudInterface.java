package org.alliancegenome.curation_api.interfaces.crud.associations;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.associations.GeneGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/genegenomiclocation")
@Tag(name = "CRUD - GeneGenomicLocationAssociation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneGenomicLocationAssociationCrudInterface extends BaseIdCrudInterface<GeneGenomicLocationAssociation> {

	@Operation(summary = "Bulk load gene genomic location association data", description = "Bulk load gene genomic location association records from a data provider submission")
	@POST
	@Path("/bulk/{dataProvider}_{assemblyName}/geneLocations")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateGeneLocations(@PathParam("dataProvider") String dataProvider, @PathParam("assemblyName") String assemblyName, List<Gff3DTO> gff3Data);
	
}
