package org.alliancegenome.curation_api.model.entities;

import javax.persistence.Entity;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import lombok.*;


@Audited
@Indexed
@Entity
@Data @EqualsAndHashCode(callSuper = true)
//@ToString(exclude = {"genomicLocations"})
@Schema(name = "Disease_Annotation", description = "Annotation class representing a disease annotation")
public class DiseaseAnnotation extends Association {

}

