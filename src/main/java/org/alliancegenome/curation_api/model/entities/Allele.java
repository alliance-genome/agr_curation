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
@ToString(exclude = {"genomicLocations"})
public class Allele extends GenomicEntity {

	@Field
	@JsonView({View.FieldsOnly.class})
	private String symbol;
	
	@Field
	@JsonView({View.FieldsOnly.class})
	private String feature_type;
	
	@Field
	@Column(columnDefinition="TEXT")
	@JsonView({View.FieldsOnly.class})
	private String description;

	@ManyToMany
	private List<GeneGenomicLocation> genomicLocations;
	
}

