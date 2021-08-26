package org.alliancegenome.curation_api.model.entities;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonView;
import org.alliancegenome.curation_api.base.BaseGeneratedEntity;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(callSuper = false)
public class Synonym extends BaseGeneratedEntity {

    @JsonView({View.FieldsOnly.class})
    private String name;

    public Synonym(String name) {
        super();
        this.name = name;
    }

    public Synonym() {
    }
}
