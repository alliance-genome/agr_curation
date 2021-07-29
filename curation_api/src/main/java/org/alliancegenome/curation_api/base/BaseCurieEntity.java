package org.alliancegenome.curation_api.base;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.*;
import javax.persistence.Index;

import org.alliancegenome.curation_api.view.View;
import org.hibernate.annotations.*;
import org.hibernate.search.annotations.*;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@MappedSuperclass
public class BaseCurieEntity extends BaseEntity implements Serializable {

	@Id
	@JsonView({View.FieldsOnly.class})
	private String curie;

	@Field
	@CreationTimestamp
	@JsonView({View.FieldsOnly.class})
	private LocalDateTime created;

	@Field
	@UpdateTimestamp
	@JsonView({View.FieldsOnly.class})
	private LocalDateTime lastUpdated;

}
