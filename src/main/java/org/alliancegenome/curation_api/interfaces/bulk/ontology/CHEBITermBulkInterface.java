package org.alliancegenome.curation_api.interfaces.bulk.ontology;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.BaseOntologyTermBulkRESTInterface;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/chebiterm/bulk")
@Tag(name = "Ontology - ChEBI")
@Tag(name = "Ontology - Bulk Import")
@Produces(MediaType.APPLICATION_JSON)
public interface CHEBITermBulkInterface extends BaseOntologyTermBulkRESTInterface {
}

