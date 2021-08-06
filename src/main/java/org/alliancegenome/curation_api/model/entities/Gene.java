package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.*;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Indexed(index = "search_index")
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"genomicLocations"})
public class Gene extends GenomicEntity {

	@Field
	@JsonView({View.FieldsOnly.class})
	private String symbol;

	@Field
	@Column(columnDefinition="TEXT")
	@JsonView({View.FieldsOnly.class})
	private String geneSynopsis;

	@Field
	@JsonView({View.FieldsOnly.class})
	private String geneSynopsisURL;

	@Field
	@JsonView({View.FieldsOnly.class})
	private String type;
	
	@Field
	@Column(columnDefinition="TEXT")
	@JsonView({View.FieldsOnly.class})
	private String automatedGeneDescription;

	@ManyToMany
	private List<GeneGenomicLocation> genomicLocations;
	
}

