package org.alliancegenome.curation_api.model.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.*;

import org.alliancegenome.curation_api.view.View;
import org.hibernate.annotations.*;
import org.hibernate.search.annotations.Field;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@MappedSuperclass
public class BaseEntity implements Serializable {
	
	@Id
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
