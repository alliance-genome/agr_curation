package org.alliancegenome.curation_api.interfaces.document;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.interfaces.base.BaseIdDocumentInterface;
import org.alliancegenome.curation_api.model.document.LiteratureReference;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/literature-reference")
@Tag(name = "Document - Literature Reference")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface LiteratureReferenceInterface extends BaseIdDocumentInterface<LiteratureReference> {

}
