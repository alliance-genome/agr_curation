package org.alliancegenome.curation_api.model.entities;

import javax.persistence.*;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Audited
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"genomicEntity"})
public class Synonym extends BaseEntity {

	@ManyToOne
	@JsonIgnore
	private GenomicEntity genomicEntity;
}
