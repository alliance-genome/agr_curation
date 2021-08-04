package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.Entity;

import org.alliancegenome.curation_api.base.BaseCurieEntity;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Indexed;

import lombok.*;

@Audited
@Indexed(index = "search_index")
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class Reference extends BaseCurieEntity {

	
}
