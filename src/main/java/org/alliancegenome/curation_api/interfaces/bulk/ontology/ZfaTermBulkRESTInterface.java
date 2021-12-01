package org.alliancegenome.curation_api.interfaces.bulk.ontology;

import org.alliancegenome.curation_api.base.BaseOntologyTermBulkRESTInterface;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/zfaTerm/bulk")
@Tag(name = "Ontology - ZFA")
@Tag(name = "Ontology - Bulk Import")
@Produces(MediaType.APPLICATION_JSON)
public interface ZfaTermBulkRESTInterface extends BaseOntologyTermBulkRESTInterface {


}