package org.alliancegenome.curation_api.model.entities.ontology;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.GeneOntologyAnnotation;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = LinkMLSchemaConstants.MIN_ONTOLOGY_RELEASE, max = LinkMLSchemaConstants.MAX_ONTOLOGY_RELEASE, dependencies = { OntologyTerm.class })
public class GOTerm extends OntologyTerm {

	@OneToMany(mappedBy = "goTerm")
	private List<GeneOntologyAnnotation> geneOntologyAnnotations;

}
