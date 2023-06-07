package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Indexed
@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Schema(name = "Allele_Disease_Annotation", description = "Annotation class representing a allele disease annotation")
@JsonTypeName("AlleleDiseaseAnnotation")
@OnDelete(action = OnDeleteAction.CASCADE)
@AGRCurationSchemaVersion(min = "1.3.2", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { DiseaseAnnotation.class })
@Table(indexes = { @Index(name = "AlleleDiseaseAnnotation_inferredGene_index", columnList = "inferredGene_curie"), @Index(name = "AlleleDiseaseAnnotation_Subject_index", columnList = "subject_curie")})
public class AlleleDiseaseAnnotation extends DiseaseAnnotation {

	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
	@JoinColumn(foreignKey = @ForeignKey(name = "fk_alleledasubject"))
	@JsonView({ View.FieldsOnly.class })
	@Fetch(FetchMode.JOIN)
	private Allele subject;

	@IndexedEmbedded(includePaths = { "curie", "geneSymbol.displayText", "geneFullName.displayText", "geneSystematicName.displayText", "geneSynonyms.displayText", "curie_keyword", "geneSymbol.displayText_keyword", "geneFullName.displayText_keyword", "geneSystematicName.displayText_keyword", "geneSynonyms.displayText_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@Fetch(FetchMode.JOIN)
	private Gene inferredGene;

	@IndexedEmbedded(includePaths = { "curie", "geneSymbol.displayText", "geneFullName.displayText", "geneSystematicName.displayText", "geneSynonyms.displayText", "curie_keyword", "geneSymbol.displayText_keyword", "geneFullName.displayText_keyword", "geneSystematicName.displayText_keyword", "geneSynonyms.displayText_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JoinTable(indexes = { @Index(columnList = "allelediseaseannotation_id"), @Index(columnList = "assertedgenes_curie")})
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
	public String getSubjectSpeciesName() {
		if (subject == null)
			return null;
		if (subject.getTaxon() == null)
			return null;
		return subject.getTaxon().getGenusSpecies();
	}
}
