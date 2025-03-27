package org.alliancegenome.curation_api.model.entities.ontology;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import java.util.List;

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

	public List<GeneDiseaseAnnotation> getNonObsoletedGeneDiseaseAnnotations() {
		if (CollectionUtils.isEmpty(geneDiseaseAnnotations)) {
			return null;
		}
		return geneDiseaseAnnotations.stream().filter(AuditedObject::isNotInternalOrObsolete).toList();
	}

	public List<AlleleDiseaseAnnotation> getNonObsoletedAlleleDiseaseAnnotations() {
		if (CollectionUtils.isEmpty(alleleDiseaseAnnotations)) {
			return null;
		}
		return alleleDiseaseAnnotations.stream().filter(AuditedObject::isNotInternalOrObsolete).toList();
	}

	public List<AGMDiseaseAnnotation> getNonObsoletedAGMDiseaseAnnotations() {
		if (CollectionUtils.isEmpty(agmDiseaseAnnotations)) {
			return null;
		}
		return agmDiseaseAnnotations.stream().filter(AuditedObject::isNotInternalOrObsolete).toList();
	}
}
