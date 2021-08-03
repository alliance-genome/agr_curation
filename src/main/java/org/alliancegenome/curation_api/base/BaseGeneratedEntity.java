package org.alliancegenome.curation_api.base;

import java.time.LocalDateTime;

import javax.persistence.*;

import org.alliancegenome.curation_api.view.View;
import org.hibernate.annotations.*;
import org.hibernate.search.annotations.*;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@MappedSuperclass
public class BaseGeneratedEntity extends BaseEntity {
	
	@Id @DocumentId
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@JsonView({View.FieldsOnly.class})
	private Long id;
	
	@Field
	@CreationTimestamp
	@JsonView({View.FieldsOnly.class})
	private LocalDateTime created;
	
	@Field
	@UpdateTimestamp
	@JsonView({View.FieldsOnly.class})
	private LocalDateTime lastUpdated;

}
