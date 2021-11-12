package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.BaseCurieEntity;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Indexed
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"synonyms", "crossReferences"})
@Schema(name="Molecule", description="POJO that represents the Molecule")

public class Molecule extends BaseCurieEntity {

	@KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
	@JsonView({View.FieldsOnly.class})
	private String name;
	
	@KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
	@JsonView({View.FieldsOnly.class})
	private String inchi;
	
	@KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
	@JsonView({View.FieldsOnly.class})
	private String inchiKey;
	
	@KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
	@JsonView({View.FieldsOnly.class})
	private String iupac;
	
	@KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
	@JsonView({View.FieldsOnly.class})
	private String formula;
	
	@KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
	@JsonView({View.FieldsOnly.class})
	private String smiles;
	
	@ManyToMany
	@JoinTable(indexes = @Index( columnList = "molecule_curie"))
    @JsonView({View.FieldsOnly.class})
	private List<Synonym> synonyms;
	
	@ManyToMany
	@JoinTable(indexes = @Index( columnList = "molecule_curie"))
    @JsonView({View.FieldsOnly.class})
	private List<CrossReference> crossReferences;
}
