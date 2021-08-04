package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.Entity;

import org.alliancegenome.curation_api.base.BaseGeneratedEntity;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Indexed;

import lombok.*;

@Audited
@Indexed(index = "search_index")
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class DiseaseAnnotationCurated extends BaseGeneratedEntity {

	private String objectId;
	private String doid;
	private Reference reference;
	private List<String> evidenceCode;

}