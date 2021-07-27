package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.*;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Audited
@Indexed(index = "search_index")
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"genomicLocations", "synonyms", "crossReferences", "secondaryIdentifiers"})
public class Gene extends BaseEntity {

	@Field
	@JsonView({View.FieldsOnly.class})
	private String symbol;
	@Field
	@JsonView({View.FieldsOnly.class})
	private String name;
	@Field
	@JsonView({View.FieldsOnly.class})
	private String type;
	@Field
	@JsonView({View.FieldsOnly.class})
	private String geneSynopsisURL;
	@Field
	@JsonView({View.FieldsOnly.class})
	private String curie;
	@Field
	@JsonView({View.FieldsOnly.class})
	private String taxon;

	@Field
	@Column(columnDefinition="TEXT")
	private String geneSynopsis;

	@ElementCollection
	@JsonView({View.FieldsAndLists.class})
	private List<String> secondaryIdentifiers;
	
	@Field
	@Column(columnDefinition="TEXT")
	private String automatedGeneDescription;

	@OneToMany(mappedBy = "genomicEntity")
	@JsonView({View.FieldsAndLists.class})
	private List<Synonym> synonyms;
	
	@OneToMany(mappedBy = "genomicEntity")
	private List<CrossReference> crossReferences;
	
	@OneToMany(mappedBy = "gene")
	private List<GeneGenomicLocation> genomicLocations;
	
}

