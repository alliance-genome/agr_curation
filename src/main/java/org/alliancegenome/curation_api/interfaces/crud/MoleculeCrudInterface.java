package org.alliancegenome.curation_api.interfaces.crud;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.interfaces.BaseCurieCrudInterface;
import org.alliancegenome.curation_api.model.entities.Molecule;
import org.alliancegenome.curation_api.model.ingest.fms.dto.MoleculeMetaDataFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/molecule")
@Tag(name = "CRUD - Molecules")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface MoleculeCrudInterface extends BaseCurieCrudInterface<Molecule> {

	@POST
	@Path("/bulk/moleculefile")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateMolecules(MoleculeMetaDataFmsDTO moleculeData);
	
}
