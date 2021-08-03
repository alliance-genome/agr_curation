package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Field;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"synonyms", "crossReferences", "secondaryIdentifiers"})
public class GenomicEntity extends BiologicalEntity {

	@Field
	@JsonView({View.FieldsOnly.class})
	private String name;

	@ManyToMany
	@JsonView({View.FieldsAndLists.class})
	private List<Synonym> synonyms;
	
	@ManyToMany
	@JsonView({View.FieldsAndLists.class})
	private List<CrossReference> crossReferences;
	
	@ElementCollection
	@JsonView({View.FieldsAndLists.class})
	private List<String> secondaryIdentifiers;

}
