package org.alliancegenome.curation_api.interfaces.bulk.ontology;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.alliancegenome.curation_api.base.BaseOntologyTermBulkRESTInterface;
import org.alliancegenome.curation_api.model.ingest.xml.dto.RDF;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/mpterm/bulk")
@Tag(name = "Ontology - MP")
@Produces(MediaType.APPLICATION_JSON)
public interface MpTermBulkRESTInterface extends BaseOntologyTermBulkRESTInterface {

    
}