package org.alliancegenome.curation_api.base;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Data
public class BaseAnnotation {

	@Id
	@GeneratedValue
	@JsonView({View.FieldsOnly.class})
	private Long id;

	@GenericField
	@CreationTimestamp
	@JsonView({View.FieldsOnly.class})
	private LocalDateTime created;

	@GenericField
	@UpdateTimestamp
	@JsonView({View.FieldsOnly.class})
	private LocalDateTime lastUpdated;

}
