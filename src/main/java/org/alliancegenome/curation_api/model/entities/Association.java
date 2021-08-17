package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.BaseGeneratedEntity;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

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

