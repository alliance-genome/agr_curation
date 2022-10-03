package org.alliancegenome.curation_api.model.entities;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.*;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Indexed
@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Schema(name = "AGM_Disease_Annotation", description = "Annotation class representing a agm disease annotation")
@JsonTypeName("AGMDiseaseAnnotation")
@OnDelete(action = OnDeleteAction.CASCADE)
@AGRCurationSchemaVersion(min="1.2.4", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={"DiseaseAnnotation"}, submitted=true)
public class AGMDiseaseAnnotation extends DiseaseAnnotation {

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
	@JoinColumn(foreignKey = @ForeignKey(name="fk_agmdasubject"))
	@JsonView({View.FieldsOnly.class})
	private AffectedGenomicModel subject;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private Gene inferredGene;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private Allele inferredAllele;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private Gene assertedGene;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private Allele assertedAllele;

	@Transient
	@Override
	@JsonIgnore
	public String getSubjectCurie() {
		if(subject == null) return null;
		return subject.getCurie();
	}

	@Transient
	@Override
	@JsonIgnore
	public String getSubjectTaxonCurie() {
		if(subject == null) return null;
		if(subject.getTaxon() == null) return null;
		return subject.getTaxon().getCurie();
	}
}
