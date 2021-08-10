package org.alliancegenome.curation_api.model.entities;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.BaseGeneratedEntity;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"object"})
public class DiseaseAnnotation extends BaseGeneratedEntity {

	@OneToOne
	@JsonView(View.FieldsOnly.class)
	private BiologicalEntity object;
}
