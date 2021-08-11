package org.alliancegenome.curation_api.base;

import java.time.LocalDateTime;

import javax.persistence.*;

import org.alliancegenome.curation_api.view.View;
import org.hibernate.annotations.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@MappedSuperclass
public class BaseGeneratedEntity extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@JsonView({View.FieldsOnly.class})
	private Long id;
	
	@FullTextField
	@CreationTimestamp
	@JsonView({View.FieldsOnly.class})
	private LocalDateTime created;
	
	@FullTextField
	@UpdateTimestamp
	@JsonView({View.FieldsOnly.class})
	private LocalDateTime lastUpdated;

}
