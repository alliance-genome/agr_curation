package org.alliancegenome.curation_api.model.entities.associations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.AssemblyComponent;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@MappedSuperclass
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "2.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { VariantLocationAssociation.class })
@Schema(name = "VariantGenomicLocationAssociation", description = "POJO representing an association between a variant and a genomic location")
public abstract class VariantGenomicLocationAssociation extends VariantLocationAssociation {

	@IndexedEmbedded(includePaths = {
		"curie", "curie_keyword", "primaryExternalId", "primaryExternalId_keyword",
		"modInternalId", "modInternalId_keyword", "name", "name_keyword"
	})
	@ManyToOne
	@JsonView({ CurationView.FieldsOnly.class, CurationView.AlleleSummaryDocument.class, CurationView.VariantDocument.class })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@Fetch(FetchMode.JOIN)
	private AssemblyComponent variantGenomicLocationAssociationObject;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "variationStrand_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ CurationView.FieldsOnly.class })
	@Column(length = 1)
	private String variationStrand;

	@GenericField(projectable = Projectable.YES, sortable = Sortable.YES)
	@JsonView({ CurationView.FieldsOnly.class })
	private Integer numberAdditionalDnaBasePairs;

	@GenericField(projectable = Projectable.YES, sortable = Sortable.YES)
	@JsonView({ CurationView.FieldsOnly.class })
	private Integer numberRemovedDnaBasePairs;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "paddedBase_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ CurationView.FieldsOnly.class })
	@Column(length = 1)
	private String paddedBase;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "insertedSequence_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ CurationView.FieldsOnly.class })
	@Column(columnDefinition = "TEXT")
	private String insertedSequence;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "deletedSequence_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ CurationView.FieldsOnly.class })
	@Column(columnDefinition = "TEXT")
	private String deletedSequence;

	@IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name", "namespace",
			"curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword", "namespace_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ CurationView.FieldsOnly.class })
	private SOTerm dnaMutationType;

	@IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name", "namespace",
			"curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword", "namespace_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ CurationView.FieldsOnly.class })
	private SOTerm geneLocalizationType;


	@Transient
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonView({CurationView.FieldsOnly.class, CurationView.VariantView.class})
	public String getNucleotideChange() {
		if (getVariantAssociationSubject() != null && getVariantAssociationSubject().getVariantType() != null) {
			String variantTypeCurie = getVariantAssociationSubject().getVariantType().getCurie();
			if ("SO:0000667".equals(variantTypeCurie)) {
				// Insertion
				return "c>c" + getVariantSequence();
			} else if ("SO:1000008".equals(variantTypeCurie)) {
				// Point mutation
				return getReferenceSequence() + ">" + getVariantSequence();
			} else if ("SO:0000159".equals(variantTypeCurie)) {
				// Deletion
				return "t" + getReferenceSequence() + ">t";
			}
			if (getReferenceSequence() != null && getVariantSequence() != null) {
				return getReferenceSequence() + ">" + getVariantSequence();
			}
		}
		return null;
	}

}
