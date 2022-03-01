package org.alliancegenome.curation_api.model.entities;

import javax.persistence.*;

import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Indexed
@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Schema(name = "Allele_Disease_Annotation", description = "Annotation class representing a allele disease annotation")
@JsonTypeName("AlleleDiseaseAnnotation")
public class AlleleDiseaseAnnotation extends DiseaseAnnotation {

    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @ManyToOne
    @JsonView({View.FieldsOnly.class})
    private Allele subject;
    
    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @ManyToOne
    @JsonView({View.FieldsOnly.class})
    private Gene inferredGene;
    
}
