package org.alliancegenome.curation_api.interfaces.crud;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.BaseCrudRESTInterface;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/vocabularyterm")
@Tag(name = "VocabularyTerm")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface VocabularyTermRESTInterface extends BaseCrudRESTInterface<VocabularyTerm> {


}
