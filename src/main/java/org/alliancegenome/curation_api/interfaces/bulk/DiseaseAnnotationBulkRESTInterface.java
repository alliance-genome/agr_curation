package org.alliancegenome.curation_api.interfaces.bulk;

import org.alliancegenome.curation_api.model.dto.json.DiseaseAnnotationMetaDataDTO;
import org.alliancegenome.curation_api.model.dto.json.GeneMetaDataDTO;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/disease/bulk")
@Tag(name = "DiseaseAnnotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DiseaseAnnotationBulkRESTInterface {
	
	@POST
	@Path("/annotationFile")
	public String updateDiseaseAnnotation(DiseaseAnnotationMetaDataDTO geneData);

}