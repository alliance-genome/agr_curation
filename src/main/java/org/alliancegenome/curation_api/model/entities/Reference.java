package org.alliancegenome.curation_api.model.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.alliancegenome.curation_api.base.BaseCurieEntity;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.List;

@Audited
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
@Schema(name="Reference", description="POJO that represents the Cross Reference")
public class Reference extends BaseCurieEntity {

}
