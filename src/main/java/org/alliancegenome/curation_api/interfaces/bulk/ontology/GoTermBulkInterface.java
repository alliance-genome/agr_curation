package org.alliancegenome.curation_api.interfaces.bulk.ontology;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.BaseOntologyTermBulkRESTInterface;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/goterm/bulk")
@Tag(name = "Ontology - GO")
@Tag(name = "Ontology - Bulk Import")
@Produces(MediaType.APPLICATION_JSON)
public interface GoTermBulkInterface extends BaseOntologyTermBulkRESTInterface {


}