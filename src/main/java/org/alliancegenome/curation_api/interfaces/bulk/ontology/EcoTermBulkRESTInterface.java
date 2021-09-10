package org.alliancegenome.curation_api.interfaces.bulk.ontology;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.BaseOntologyTermBulkRESTInterface;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/ecoterm/bulk")
@Tag(name = "Ontology - ECO")
@Produces(MediaType.APPLICATION_JSON)
public interface EcoTermBulkRESTInterface extends BaseOntologyTermBulkRESTInterface {

    
}