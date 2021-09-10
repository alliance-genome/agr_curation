package org.alliancegenome.curation_api.interfaces.bulk.ontology;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.BaseOntologyTermBulkRESTInterface;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/doterm/bulk")
@Tag(name = "Ontology - DO")
@Produces(MediaType.APPLICATION_JSON)
public interface DoTermBulkRESTInterface extends BaseOntologyTermBulkRESTInterface {


}