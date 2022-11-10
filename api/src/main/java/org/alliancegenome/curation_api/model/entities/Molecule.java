package org.alliancegenome.curation_api.model.entities;


import javax.persistence.Entity;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.ontology.ChemicalTerm;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import lombok.*;

@Audited
@Indexed
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Schema(name="Molecule", description="POJO that represents the Molecule")

@AGRCurationSchemaVersion(min="1.2.0", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={ChemicalTerm.class})
public class Molecule extends ChemicalTerm {

}
