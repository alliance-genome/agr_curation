package org.alliancegenome.curation_api.model.entities;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonView;
import org.alliancegenome.curation_api.model.entities.ontology.EcoTerm;
import org.alliancegenome.curation_api.model.ingest.json.dto.DiseaseModelAnnotationDTO;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@Audited
@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
//@ToString(exclude = {"genomicLocations"})
@Schema(name = "Disease_Annotation", description = "Annotation class representing a disease annotation")
public class DiseaseAnnotation extends Association {

    @GenericField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
    @JsonView({View.FieldsOnly.class})
    @Column(columnDefinition = "boolean default false", nullable = false)
    private Boolean negated = false;


    @KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
    @JsonView({View.FieldsOnly.class})
    @Enumerated(EnumType.STRING)
    private DiseaseRelation diseaseRelation;

    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @ManyToMany
    @JsonView({View.FieldsOnly.class})
    private List<EcoTerm> evidenceCodes;

    public enum DiseaseRelation {
        is_model_of,
        is_implicated_in,
        is_marker_for;
    }

}

