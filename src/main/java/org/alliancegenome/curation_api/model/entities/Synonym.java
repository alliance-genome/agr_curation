package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.BaseGeneratedEntity;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"genomicEntities", "molecules"})
public class Synonym extends BaseGeneratedEntity {

    @JsonView({View.FieldsOnly.class})
    private String name;

    @ManyToMany(mappedBy="synonyms")
    private List<GenomicEntity> genomicEntities;

}
