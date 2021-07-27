package org.alliancegenome.curation_api.model.entities;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.*;

import lombok.*;

@Audited
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class BiologicalEntity extends BaseEntity {


}

