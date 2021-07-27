package org.alliancegenome.curation_api.model.entities;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import lombok.*;

@Audited
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class InformationContentEntity extends BaseEntity {
	private String curie;
}
