package org.alliancegenome.curation_api.model.entities.ontology;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.BaseCurieEntity;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.*;

import lombok.*;

@Audited
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"parent", "children", "crossReferences", "synonyms", "secondaryIdentifiers", "subsets", "termDefinitionUrls"})
public class OntologyTerm extends BaseCurieEntity {

	private String name;
	private String type;
	private Boolean obsolete;
	private String namespace;
	
//	@ManyToOne
//	private OntologyTerm parent;
//	
//	@OneToMany
//	private List<OntologyTerm> children;
	
	@Field
	@Column(columnDefinition="TEXT")
	private String definition;
	
//	@ElementCollection
//	private List<String> definitionUrls;
//
//	@ElementCollection
//	private List<String> subsets;
//
//	@ElementCollection
//	private List<String> secondaryIdentifiers;
//
//	@ManyToMany
//	private List<Synonym> synonyms;
//	
//	@ManyToMany
//	private List<CrossReference> crossReferences;

}
