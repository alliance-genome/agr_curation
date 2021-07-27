package org.alliancegenome.curation_api.model.entities;

import javax.persistence.*;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Audited
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"gene"})
public class GeneGenomicLocation extends BaseEntity {
	
	private Subject subject;
	private Predicate predicate;
	//private AGRObject object;
	private String assembly;
	private Integer startPos;
	private Integer endPos;
	
	@ManyToOne
	@JsonIgnore
	private Gene gene;
	
}
