package org.alliancegenome.curation_api.interfaces.bulk;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.model.ingest.json.dto.GeneMetaDataDTO;
import org.alliancegenome.curation_api.model.ingest.json.dto.MoleculeMetaDataDTO;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/molecule/bulk")
@Tag(name = "Molecules")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface MoleculeBulkRESTInterface {
    
    @POST
    @Path("/moleculefile")
    public String updateMolecules(
        MoleculeMetaDataDTO moleculeData);

}