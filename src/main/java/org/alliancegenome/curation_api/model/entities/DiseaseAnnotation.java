package org.alliancegenome.curation_api.model.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.alliancegenome.curation_api.base.BaseGeneratedEntity;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Audited
@Indexed
@Entity
@Data
//@ToString(exclude = {"genomicLocations"})
@Schema(name = "Disease_Annotation", description = "Annotation class representing a disease annotation")
public class DiseaseAnnotation extends  Association {

}

