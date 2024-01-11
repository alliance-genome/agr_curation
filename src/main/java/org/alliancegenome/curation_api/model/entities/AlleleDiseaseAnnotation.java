package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Schema(name = "Allele_Disease_Annotation", description = "Annotation class representing a allele disease annotation")
@JsonTypeName("AlleleDiseaseAnnotation")
@OnDelete(action = OnDeleteAction.CASCADE)
@AGRCurationSchemaVersion(min = "1.3.2", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { DiseaseAnnotation.class })
@Table(indexes = { @Index(name = "AlleleDiseaseAnnotation_inferredGene_index", columnList = "inferredGene_id"), @Index(name = "AlleleDiseaseAnnotation_Subject_index", columnList = "subject_id")})
public class AlleleDiseaseAnnotation extends DiseaseAnnotation {

	@IndexedEmbedded(includeDepth = 2)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
	@JsonView({ View.FieldsOnly.class })
	private Allele subject;

	@IndexedEmbedded(includeDepth = 2)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView({ View.FieldsOnly.class })
	private Gene inferredGene;

	@IndexedEmbedded(includeDepth = 2)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@Fetch(FetchMode.SELECT)
	@JoinTable(indexes = {
		@Index(name = "allelediseaseannotationgene_diseaseannotation_index", columnList = "allelediseaseannotation_id"),
		@Index(name = "allelediseaseannotationgene_assertedgenes_index", columnList = "assertedgenes_id")
	})
	@JsonView({ View.FieldsAndLists.class, View.DiseaseAnnotation.class })
	private List<Gene> assertedGenes;

	@Transient
	@Override
	@JsonIgnore
	public String getSubjectCurie() {
		if (subject == null)
			return null;
		return subject.getCurie();
	}

	@Transient
	@Override
	@JsonIgnore
	public String getSubjectTaxonCurie() {
		if (subject == null)
			return null;
		if (subject.getTaxon() == null)
			return null;
		return subject.getTaxon().getCurie();
	}
	
	@Transient
	@Override
	@JsonIgnore
	public String getSubjectIdentifier() {
		if (subject == null)
			return null;
		return subject.getIdentifier();
	}

	@Transient
	@Override
	@JsonIgnore
	public String getSubjectSpeciesName() {
		if (subject == null)
			return null;
		if (subject.getTaxon() == null)
			return null;
		return subject.getTaxon().getGenusSpecies();
	}
}
