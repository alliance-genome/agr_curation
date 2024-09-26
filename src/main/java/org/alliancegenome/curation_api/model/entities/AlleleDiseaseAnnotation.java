package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
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
@AGRCurationSchemaVersion(min = "2.2.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { DiseaseAnnotation.class })

@Table(indexes = {
	@Index(name = "AlleleDiseaseAnnotation_internal_index", columnList = "internal"),
	@Index(name = "AlleleDiseaseAnnotation_obsolete_index", columnList = "obsolete"),
	@Index(name = "AlleleDiseaseAnnotation_curie_index", columnList = "curie"),
	@Index(name = "AlleleDiseaseAnnotation_modEntityId_index", columnList = "modEntityId"),
	@Index(name = "AlleleDiseaseAnnotation_modInternalId_index", columnList = "modInternalId"),
	@Index(name = "AlleleDiseaseAnnotation_uniqueId_index", columnList = "uniqueId"),
	@Index(name = "AlleleDiseaseAnnotation_negated_index", columnList = "negated"),
	@Index(name = "AlleleDiseaseAnnotation_createdBy_index", columnList = "createdBy_id"),
	@Index(name = "AlleleDiseaseAnnotation_updatedBy_index", columnList = "updatedBy_id"),
	@Index(name = "AlleleDiseaseAnnotation_singleReference_index", columnList = "singleReference_id"),
	@Index(name = "AlleleDiseaseAnnotation_dataProvider_index", columnList = "dataProvider_id"),
	@Index(name = "AlleleDiseaseAnnotation_annotationType_index", columnList = "annotationType_id"),
	@Index(name = "AlleleDiseaseAnnotation_diseaseAnnotationObject_index", columnList = "diseaseAnnotationObject_id"),
	@Index(name = "AlleleDiseaseAnnotation_diseaseGeneticModifierRelation_index", columnList = "diseaseGeneticModifierRelation_id"),
	@Index(name = "AlleleDiseaseAnnotation_geneticSex_index", columnList = "geneticSex_id"),
	@Index(name = "AlleleDiseaseAnnotation_relation_index", columnList = "relation_id"),
	@Index(name = "AlleleDiseaseAnnotation_secondaryDataProvider_index", columnList = "secondaryDataProvider_id"),
	@Index(name = "AlleleDiseaseAnnotation_diseaseAnnotationSubject_index", columnList = "diseaseAnnotationSubject_id"),
	@Index(name = "AlleleDiseaseAnnotation_inferredGene_index", columnList = "inferredGene_id")
})

public class AlleleDiseaseAnnotation extends DiseaseAnnotation {

	@IndexedEmbedded(includePaths = {
			"curie", "modEntityId", "modInternalId", "curie_keyword", "modEntityId_keyword", "modInternalId_keyword",
			"alleleSymbol.formatText", "alleleSymbol.displayText", "alleleSymbol.formatText_keyword", "alleleSymbol.displayText_keyword",
			"alleleFullName.formatText", "alleleFullName.displayText", "alleleFullName.formatText_keyword", "alleleFullName.displayText_keyword",
			"alleleSynonyms.formatText", "alleleSynonyms.displayText", "alleleSynonyms.formatText_keyword", "alleleSynonyms.displayText_keyword",
			"alleleSecondaryIds.secondaryId", "alleleSecondaryIds.secondaryId_keyword", "name", "name_keyword", "symbol", "symbol_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private Allele diseaseAnnotationSubject;

	@IndexedEmbedded(includePaths = {
			"curie", "modEntityId", "modInternalId", "curie_keyword", "modEntityId_keyword", "modInternalId_keyword",
			"geneSymbol.formatText", "geneSymbol.displayText", "geneSymbol.formatText_keyword", "geneSymbol.displayText_keyword",
			"geneFullName.formatText", "geneFullName.displayText", "geneFullName.formatText_keyword", "geneFullName.displayText_keyword",
			"geneSystematicName.formatText", "geneSystematicName.displayText", "geneSystematicName.formatText_keyword", "geneSystematicName.displayText_keyword",
			"geneSynonyms.formatText", "geneSynonyms.displayText", "geneSynonyms.formatText_keyword", "geneSynonyms.displayText_keyword",
			"geneSecondaryIds.secondaryId", "geneSecondaryIds.secondaryId_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private Gene inferredGene;

	@IndexedEmbedded(includePaths = {
			"curie", "modEntityId", "modInternalId", "curie_keyword", "modEntityId_keyword", "modInternalId_keyword",
			"geneSymbol.formatText", "geneSymbol.displayText", "geneSymbol.formatText_keyword", "geneSymbol.displayText_keyword",
			"geneFullName.formatText", "geneFullName.displayText", "geneFullName.formatText_keyword", "geneFullName.displayText_keyword",
			"geneSystematicName.formatText", "geneSystematicName.displayText", "geneSystematicName.formatText_keyword", "geneSystematicName.displayText_keyword",
			"geneSynonyms.formatText", "geneSynonyms.displayText", "geneSynonyms.formatText_keyword", "geneSynonyms.displayText_keyword",
			"geneSecondaryIds.secondaryId", "geneSecondaryIds.secondaryId_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@Fetch(FetchMode.SELECT)
	@JsonView({ View.FieldsAndLists.class, View.DiseaseAnnotation.class, View.ForPublic.class })
	@JoinTable(
		joinColumns = @JoinColumn(name = "association_id"),
		inverseJoinColumns = @JoinColumn(name = "assertedgenes_id"),
		indexes = {
			@Index(columnList = "association_id"),
			@Index(columnList = "assertedgenes_id")
		}
	)
	private List<Gene> assertedGenes;

	@Transient
	@Override
	@JsonIgnore
	public String getSubjectCurie() {
		if (diseaseAnnotationSubject == null) {
			return null;
		}
		return diseaseAnnotationSubject.getCurie();
	}

	@Transient
	@Override
	@JsonIgnore
	public String getSubjectTaxonCurie() {
		if (diseaseAnnotationSubject == null) {
			return null;
		}
		if (diseaseAnnotationSubject.getTaxon() == null) {
			return null;
		}
		return diseaseAnnotationSubject.getTaxon().getCurie();
	}
	
	@Transient
	@Override
	@JsonIgnore
	public String getSubjectIdentifier() {
		if (diseaseAnnotationSubject == null) {
			return null;
		}
		return diseaseAnnotationSubject.getIdentifier();
	}

	@Transient
	@Override
	@JsonIgnore
	public String getSubjectSpeciesName() {
		if (diseaseAnnotationSubject == null) {
			return null;
		}
		if (diseaseAnnotationSubject.getTaxon() == null) {
			return null;
		}
		return diseaseAnnotationSubject.getTaxon().getGenusSpecies();
	}
}