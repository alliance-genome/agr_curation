package org.alliancegenome.curation_api.interfaces.search;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.BaseCrudInterface;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/disease-annotation")
@Tag(name = "Search - Disease Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DiseaseAnnotationInterface extends BaseCrudInterface<DiseaseAnnotation> {

}
