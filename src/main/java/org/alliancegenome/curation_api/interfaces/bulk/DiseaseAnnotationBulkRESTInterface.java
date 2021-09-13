package org.alliancegenome.curation_api.interfaces.bulk;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import io.quarkus.vertx.http.runtime.attribute.RequestPathAttribute;
import org.alliancegenome.curation_api.model.ingest.json.dto.DiseaseAnnotationMetaDataDTO;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/disease/bulk")
@Tag(name = "Disease Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DiseaseAnnotationBulkRESTInterface {
    
    @POST
    @Path("/{taxonID}/annotationFile")
    public String updateDiseaseAnnotation(@PathParam("taxonID") String taxonID, DiseaseAnnotationMetaDataDTO geneData);

}