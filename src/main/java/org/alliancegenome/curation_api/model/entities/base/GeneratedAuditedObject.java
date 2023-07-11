package org.alliancegenome.curation_api.model.entities.base;

import org.alliancegenome.curation_api.view.View;
import org.alliancegenome.curation_api.view.View.VocabularyTermSetView;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
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
