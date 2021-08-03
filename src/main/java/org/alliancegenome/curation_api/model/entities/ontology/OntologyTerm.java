package org.alliancegenome.curation_api.model.entities.ontology;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.BaseCurieEntity;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.*;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"parent", "children", "crossReferences", "synonyms", "secondaryIdentifiers", "subsets", "definitionUrls"})
public class OntologyTerm extends BaseCurieEntity {

	@Field
	@JsonView(View.FieldsOnly.class)
	private String name;
	@Field
	@JsonView(View.FieldsOnly.class)
	private String type;
	@Field
	@JsonView(View.FieldsOnly.class)
	private Boolean obsolete;
	@Field
	@JsonView(View.FieldsOnly.class)
	private String namespace;
	
//	@ManyToOne
//	private OntologyTerm parent;
//	
//	@OneToMany
//	private List<OntologyTerm> children;
	
	@Field
	@Column(columnDefinition="TEXT")
	@JsonView(View.FieldsOnly.class)
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
