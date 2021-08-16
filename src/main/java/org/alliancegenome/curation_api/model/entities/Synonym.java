package org.alliancegenome.curation_api.model.entities;

import javax.persistence.Entity;

import org.alliancegenome.curation_api.base.BaseGeneratedEntity;
import org.hibernate.envers.Audited;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(callSuper = false)
public class Synonym extends BaseGeneratedEntity {

    private String name;

}
