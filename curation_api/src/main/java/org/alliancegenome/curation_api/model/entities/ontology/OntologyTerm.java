package org.alliancegenome.curation_api.model.entities.ontology;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.model.entities.BaseEntity;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.*;

import lombok.*;

@Audited
@Indexed(index = "search_index")
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"secondaryIdentifiers", "subsets", "termDefinitionUrls"})
public class OntologyTerm extends BaseEntity {

	private String curie;
	private String name;

	@Field
	@Column(columnDefinition="TEXT")
	private String termDefinition;

	private String type;
	private Boolean obsolete;
	private String namespace;
	
	
	@ElementCollection
	private List<String> termDefinitionUrls;
	
	@ElementCollection
	private List<String> subsets;

	@ElementCollection
	private List<String> secondaryIdentifiers;

	//@OneToMany(mappedBy = "genomicEntity")
	//@JsonIgnore
	//private List<Synonym> synonyms;
	
	//@OneToMany(mappedBy = "genomicEntity")
	//@JsonIgnore
	//private List<CrossReference> crossReferences;

}
