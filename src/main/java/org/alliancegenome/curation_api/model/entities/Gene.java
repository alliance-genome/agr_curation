package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Indexed
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"genomicLocations"})
public class Gene extends GenomicEntity {

	@FullTextField
	@JsonView({View.FieldsOnly.class})
	private String symbol;

	@FullTextField
	@Column(columnDefinition="TEXT")
	@JsonView({View.FieldsOnly.class})
	private String geneSynopsis;

	@FullTextField
	@JsonView({View.FieldsOnly.class})
	private String geneSynopsisURL;

	@FullTextField
	@JsonView({View.FieldsOnly.class})
	private String type;
	
	@FullTextField
	@Column(columnDefinition="TEXT")
	@JsonView({View.FieldsOnly.class})
	private String automatedGeneDescription;

	@ManyToMany
	private List<GeneGenomicLocation> genomicLocations;
	
}

