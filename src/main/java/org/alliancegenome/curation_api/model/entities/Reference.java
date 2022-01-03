package org.alliancegenome.curation_api.model.entities;

import javax.persistence.Entity;

import org.alliancegenome.curation_api.base.entity.BaseCurieEntity;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;

import lombok.*;

@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString
@Schema(name="Reference", description="POJO that represents the Cross Reference")
public class Reference extends BaseCurieEntity {

}
