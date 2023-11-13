package org.alliancegenome.curation_api.interfaces.document;

import org.alliancegenome.curation_api.interfaces.base.BaseIdDocumentInterface;
import org.alliancegenome.curation_api.model.document.LiteratureReference;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/literature-reference")
@Tag(name = "Document - Literature Reference")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface LiteratureReferenceInterface extends BaseIdDocumentInterface<LiteratureReference> {

}
