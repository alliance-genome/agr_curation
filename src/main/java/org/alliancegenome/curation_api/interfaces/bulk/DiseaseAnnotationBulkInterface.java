package org.alliancegenome.curation_api.interfaces.bulk;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.model.dto.json.*;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/disease_annotations/bulk")
@Tag(name = "Disease Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DiseaseAnnotationBulkInterface {
	
	@POST
	@Path("/daf")
	public String updateDAF(DiseaseMetaDataDefinitionDTO diseaseMetaDataDefinitionData);
	
}

