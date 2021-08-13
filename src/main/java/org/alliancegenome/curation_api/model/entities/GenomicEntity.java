package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"synonyms", "crossReferences", "secondaryIdentifiers"})
public class GenomicEntity extends BiologicalEntity {

	@KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
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
