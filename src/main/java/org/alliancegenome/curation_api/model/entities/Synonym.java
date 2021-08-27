package org.alliancegenome.curation_api.model.entities;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.alliancegenome.curation_api.base.BaseGeneratedEntity;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.List;

@Audited
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class Synonym extends BaseGeneratedEntity {

    @JsonView({View.FieldsOnly.class})
    private String name;

    public Synonym(String name) {
        super();
        this.name = name;
    }

    @ManyToMany
    private List<GenomicEntity> genomicEntityList;

    public Synonym() {
    }
}
