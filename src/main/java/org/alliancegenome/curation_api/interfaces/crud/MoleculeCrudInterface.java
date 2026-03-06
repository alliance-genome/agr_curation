package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.Molecule;
import org.alliancegenome.curation_api.model.ingest.dto.fms.MoleculeIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/molecule")
@Tag(name = "CRUD - Molecules")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface MoleculeCrudInterface extends BaseIdCrudInterface<Molecule> {

	@Operation(summary = "Bulk load molecule data", description = "Bulk load molecule records from a data provider submission")
	@POST
	@Path("/bulk/moleculefile")
	@JsonView(CurationView.FieldsAndLists.class)
	APIResponse updateMolecules(MoleculeIngestFmsDTO moleculeData);

}

