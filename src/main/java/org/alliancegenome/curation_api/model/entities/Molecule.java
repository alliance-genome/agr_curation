package org.alliancegenome.curation_api.model.entities;


import javax.persistence.*;

import org.alliancegenome.curation_api.model.entities.ontology.ChemicalTerm;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import lombok.*;

@Audited
@Indexed
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Schema(name="Molecule", description="POJO that represents the Molecule")

public class Molecule extends ChemicalTerm {

}
