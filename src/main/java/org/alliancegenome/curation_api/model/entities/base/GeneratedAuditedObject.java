package org.alliancegenome.curation_api.model.entities.base;

import javax.persistence.*;

import org.alliancegenome.curation_api.view.View;
import org.hibernate.search.engine.backend.types.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@MappedSuperclass
@ToString(callSuper = true)
public class GeneratedAuditedObject extends AuditedObject {

	@Id @DocumentId
	@GenericField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@JsonView({View.FieldsOnly.class, View.PersonSettingView.class})
	@EqualsAndHashCode.Include
	protected Long id;

}
