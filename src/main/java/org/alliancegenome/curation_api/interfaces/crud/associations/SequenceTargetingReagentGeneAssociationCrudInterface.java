package org.alliancegenome.curation_api.interfaces.crud.associations;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.associations.sequenceTargetingReagentAssociations.SequenceTargetingReagentGeneAssociation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;


import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/sqtrgeneassociation")
@Tag(name = "CRUD - SQTR Gene Associations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface SequenceTargetingReagentGeneAssociationCrudInterface extends BaseIdCrudInterface<SequenceTargetingReagentGeneAssociation> { }
