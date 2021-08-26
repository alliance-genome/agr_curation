package org.alliancegenome.curation_api.model.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.alliancegenome.curation_api.base.BaseGeneratedEntity;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Audited
@Entity(name = "genesynonym")
@Data @EqualsAndHashCode(callSuper = true)
public class GeneSynonym extends Synonym {

    @ManyToOne
    private Gene gene;

    public GeneSynonym(String name) {
        super(name);
    }

    public GeneSynonym() {
    }
}
