package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.*;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Audited
@Indexed(index = "search_index")
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"genomicLocations", "synonyms", "crossReferences", "secondaryIdentifiers"})
public class Allele extends BaseEntity {

	@Field
	private String name;
	@Field
	private String symbol;
	@Field
	private String taxon;

	@Field
	private String feature_type;

	@Field
	private String curie;


	//@Field
	//@Column(columnDefinition="TEXT")


	@ElementCollection
	private List<String> secondaryIdentifiers;
	
	@Field
	@Column(columnDefinition="TEXT")
	private String automatedGeneDescription;

	@OneToMany(mappedBy = "genomicEntity")
	@JsonIgnore
	private List<Synonym> synonyms;
	

	
	@OneToMany(mappedBy = "gene")
	@JsonIgnore
	private List<GeneGenomicLocation> genomicLocations;
	
	
	
	@OneToMany(mappedBy = "genomicEntity")
	@JsonIgnore
	private List<CrossReference> crossReferences;
	
}

