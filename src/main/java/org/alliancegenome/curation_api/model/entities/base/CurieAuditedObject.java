package org.alliancegenome.curation_api.model.entities.base;

import javax.persistence.*;

import org.alliancegenome.curation_api.view.View;
import org.hibernate.search.engine.backend.types.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@MappedSuperclass
@ToString(callSuper = true)
public class CurieAuditedObject extends AuditedObject {

	@Id @DocumentId
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "curie_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({View.FieldsOnly.class})
	@EqualsAndHashCode.Include
	private String curie;

}
