package org.alliancegenome.curation_api.model.entities;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.alliancegenome.curation_api.base.BaseGeneratedEntity;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import javax.persistence.*;
import java.util.List;

@Audited
@Entity
@Data
@ToString(exclude = {"subject","object","referenceList"})
@Inheritance(strategy = InheritanceType.JOINED)
@Schema(name = "association", description = "Annotation class representing a disease annotation")
public class Association extends BaseGeneratedEntity {

    @ManyToOne
    @JsonView({View.FieldsOnly.class})
    private BiologicalEntity subject;
    @ManyToOne
    @JsonView({View.FieldsOnly.class})
    private DOTerm object;

    @ManyToMany
    @JsonView({View.FieldsOnly.class})
    private List<Reference> referenceList;

}

