package org.alliancegenome.curation_api.interfaces.rest;


import org.alliancegenome.curation_api.base.BaseCrudRESTInterface;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/disease-annotations")
@Tag(name = "Disease Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DiseaseAnnotationRESTInterface extends BaseCrudRESTInterface<DiseaseAnnotation> {

}
