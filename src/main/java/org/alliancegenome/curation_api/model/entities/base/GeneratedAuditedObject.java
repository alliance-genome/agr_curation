package org.alliancegenome.curation_api.model.entities.base;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.alliancegenome.curation_api.view.View;
import org.alliancegenome.curation_api.view.View.VocabularyTermSetView;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@MappedSuperclass
@ToString()
public class GeneratedAuditedObject extends AuditedObject {

	@Id
	@DocumentId
	@GenericField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@JsonView({ View.FieldsOnly.class, View.PersonSettingView.class, VocabularyTermSetView.class })
	@EqualsAndHashCode.Include
	protected Long id;

}
