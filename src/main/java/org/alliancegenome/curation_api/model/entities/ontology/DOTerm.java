package org.alliancegenome.curation_api.model.entities.ontology;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = LinkMLSchemaConstants.MIN_ONTOLOGY_RELEASE, max = LinkMLSchemaConstants.MAX_ONTOLOGY_RELEASE, dependencies = {OntologyTerm.class})
public class DOTerm extends OntologyTerm {

	@ToString.Exclude
	@OneToMany(mappedBy = "diseaseAnnotationObject")
	private List<GeneDiseaseAnnotation> geneDiseaseAnnotations;
	
	@ToString.Exclude
	@OneToMany(mappedBy = "diseaseAnnotationObject")
	private List<AlleleDiseaseAnnotation> alleleDiseaseAnnotations;
	
	@ToString.Exclude
	@OneToMany(mappedBy = "diseaseAnnotationObject")
	private List<AGMDiseaseAnnotation> agmDiseaseAnnotations;

	@GenericField(projectable = Projectable.YES, sortable = Sortable.YES)
	private Double popularity;

	public List<GeneDiseaseAnnotation> getPublicGeneDiseaseAnnotations() {
		if (CollectionUtils.isEmpty(geneDiseaseAnnotations)) {
			return null;
		}
		return geneDiseaseAnnotations.stream().filter(AuditedObject::isNotInternalOrObsolete).toList();
	}

	public List<AlleleDiseaseAnnotation> getPublicAlleleDiseaseAnnotations() {
		if (CollectionUtils.isEmpty(alleleDiseaseAnnotations)) {
			return null;
		}
		return alleleDiseaseAnnotations.stream().filter(AuditedObject::isNotInternalOrObsolete).toList();
	}

	public List<AGMDiseaseAnnotation> getPublicAGMDiseaseAnnotations() {
		if (CollectionUtils.isEmpty(agmDiseaseAnnotations)) {
			return null;
		}
		return agmDiseaseAnnotations.stream().filter(AuditedObject::isNotInternalOrObsolete).toList();
	}
}
