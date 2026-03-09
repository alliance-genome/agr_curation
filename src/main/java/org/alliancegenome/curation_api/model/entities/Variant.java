package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.associations.AlleleVariantAssociation;
import org.alliancegenome.curation_api.model.entities.associations.CuratedVariantGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.view.CurationView;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"curatedVariantGenomicLocations", "alleleVariantAssociations"}, callSuper = true)
@AGRCurationSchemaVersion(min = "2.9.1", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = {GenomicEntity.class})
@Table(indexes = {
	@Index(name = "variant_varianttype_index", columnList = "varianttype_id"),
	@Index(name = "variant_variantstatus_index", columnList = "variantstatus_id"),
	@Index(name = "variant_sourcegeneralconsequence_index", columnList = "sourcegeneralconsequence_id")
})
public class Variant extends GenomicEntity {

	@IndexedEmbedded(includePaths = {"curie", "name", "curie_keyword", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({CurationView.FieldsOnly.class, CurationView.AlleleSummaryDocument.class, CurationView.VariantSummaryDocument.class, CurationView.SequenceSummaryDocument.class})
	private SOTerm variantType;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({CurationView.FieldsOnly.class})
	@Fetch(FetchMode.JOIN)
	private VocabularyTerm variantStatus;

	@IndexedEmbedded(includePaths = {"curie", "name", "curie_keyword", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({CurationView.FieldsOnly.class})
	private SOTerm sourceGeneralConsequence;

	@IndexedEmbedded(
		includePaths = {
			"variantGenomicLocationAssociationObject.curie", "variantGenomicLocationAssociationObject.curie_keyword",
			"variantGenomicLocationAssociationObject.primaryExternalId", "variantGenomicLocationAssociationObject.primaryExternalId_keyword",
			"variantGenomicLocationAssociationObject.modInternalId", "variantGenomicLocationAssociationObject.modInternalId_keyword",
			"start", "end"
		}
	)
	@OneToMany(mappedBy = "variantAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({CurationView.FieldsAndLists.class, CurationView.VariantDetailView.class, CurationView.AlleleSummaryDocument.class, CurationView.VariantSummaryDocument.class, CurationView.SequenceSummaryDocument.class})
	private List<CuratedVariantGenomicLocationAssociation> curatedVariantGenomicLocations;

	@OneToMany(mappedBy = "alleleVariantAssociationObject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({CurationView.FieldsAndLists.class, CurationView.VariantDetailView.class})
	private List<AlleleVariantAssociation> alleleVariantAssociations;

	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ElementCollection
	@JoinTable(indexes = @Index(name = "variant_synonyms_variant_index", columnList = "variant_id"))
	@JsonView({CurationView.FieldsAndLists.class, CurationView.VariantView.class})
	@Column(columnDefinition = "TEXT")
	private List<String> synonyms;

	@IndexedEmbedded(
		includePaths = {
			"primaryCrossReferenceCurie", "crossReferences.referencedCurie", "crossReferences.displayName", "curie", "primaryCrossReferenceCurie_keyword",
			"crossReferences.referencedCurie_keyword", "crossReferences.displayName_keyword", "curie_keyword"
		}
	)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@Fetch(FetchMode.JOIN)
	@JoinTable(indexes = {
		@Index(name = "variant_reference_variant_index", columnList = "variant_id"),
		@Index(name = "variant_reference_references_index", columnList = "references_id")
	})
	@JsonView({CurationView.FieldsAndLists.class, CurationView.VariantView.class, CurationView.VariantSummaryDocument.class, CurationView.SequenceSummaryDocument.class})
	private List<Reference> references;

	@Transient
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonView({CurationView.VariantSummaryDocument.class, CurationView.SequenceSummaryDocument.class})
	public String getNucleotideChange() {
		if (getVariantType() == null) {
			return null;
		}
		String variantTypeCurie = getVariantType().getCurie();
		String paddedBase = "";
		if (CollectionUtils.isNotEmpty(curatedVariantGenomicLocations)) {
			CuratedVariantGenomicLocationAssociation first = curatedVariantGenomicLocations.getFirst();
			String paddedBase1 = first.getPaddedBase();
			if (paddedBase1 != null && paddedBase1.length() == 1) {
				paddedBase = paddedBase1.toLowerCase();
			}
			// Insertion
			String variantSequence = first.getVariantSequence();
			String referenceSequence = first.getReferenceSequence();
			if ("SO:0000667".equals(variantTypeCurie)) {
				return paddedBase + ">" + paddedBase + variantSequence;
			} else if ("SO:0000159".equals(variantTypeCurie)) {
				// Deletion
				return paddedBase + referenceSequence + ">" + paddedBase;
			}
			if (referenceSequence != null && variantSequence != null) {
				return referenceSequence + ">" + variantSequence;
			}
		}
		return null;
	}
}
