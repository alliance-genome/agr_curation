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
@ToString(exclude = {"genomicLocations"})
public class Allele extends GenomicEntity {

	@Field
	private String symbol;
	@Field
	private String feature_type;

	@ManyToMany
	private List<GeneGenomicLocation> genomicLocations;
	
}

