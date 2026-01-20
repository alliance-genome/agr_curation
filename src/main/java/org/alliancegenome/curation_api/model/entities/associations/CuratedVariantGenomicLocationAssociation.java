package org.alliancegenome.curation_api.model.entities.associations;

import static org.alliancegenome.curation_api.services.associations.CuratedVariantGenomicLocationAssociationService.SORTED_VARIANT_CONSEQUENCE_MAP;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.PredictedVariantConsequence;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = "predictedVariantConsequences", callSuper = true)
@AGRCurationSchemaVersion(min = "2.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = {VariantGenomicLocationAssociation.class})
@Schema(name = "CuratedVariantGenomicLocationAssociation", description = "POJO representing an association between a variant and a curated genomic location")

@Table(indexes = {
		@Index(name = "cvgla_internal_index", columnList = "internal"),
		@Index(name = "cvgla_obsolete_index", columnList = "obsolete"),
		@Index(name = "cvgla_hgvs_index", columnList = "hgvs"),
		@Index(name = "cvgla_createdby_index", columnList = "createdBy_id"),
		@Index(name = "cvgla_updatedby_index", columnList = "updatedBy_id"),
		@Index(name = "cvgla_relation_index", columnList = "relation_id"),
		@Index(name = "cvgla_dnamutationtype_index", columnList = "dnaMutationType_id"),
		@Index(name = "cvgla_genelocalizationtype_index", columnList = "geneLocalizationType_id"),
		@Index(name = "cvgla_consequence_index", columnList = "consequence_id"),
		@Index(name = "cvgla_curatedconsequence_index", columnList = "curatedConsequence_id"),
		@Index(name = "cvgla_variantassociationsubject_index", columnList = "variantassociationsubject_id"),
		@Index(name = "cvgla_vglaobject_index", columnList = "variantgenomiclocationassociationobject_id")
}, name = "CuratedVariantGenomicLocation"
)

public class CuratedVariantGenomicLocationAssociation extends VariantGenomicLocationAssociation {

	@IndexedEmbedded(
			includePaths = {
					"variantTranscript.name", "variantTranscript.primaryExternalId",
					"variantTranscript.modInternalId", "variantTranscript.curie",
					"vepConsequence.name", "variantTranscript.name_keyword",
					"variantTranscript.primaryExternalId_keyword", "variantTranscript.modInternalId_keyword",
					"variantTranscript.curie_keyword", "vepConsequence.name_keyword",
					"variantTranscript.transcriptId", "variantTranscript.transcriptId_keyword"
			}
	)
	@OneToMany(mappedBy = "variantGenomicLocation", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({CurationView.FieldsAndLists.class, CurationView.VariantView.class})
	private List<PredictedVariantConsequence> predictedVariantConsequences;

	@Transient
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonView({CurationView.FieldsOnly.class, CurationView.VariantView.class})
	public PredictedVariantConsequence getMostSevereConsequence() {
		if (predictedVariantConsequences == null || predictedVariantConsequences.isEmpty()) {
			return null;
		}
		return predictedVariantConsequences.stream()
				.min(Comparator.comparingInt(pvc -> {
					if (pvc.getVepConsequences() == null || pvc.getVepConsequences().isEmpty()) {
						return Integer.MAX_VALUE;
					}
					return pvc.getVepConsequences().stream()
							.map(soTerm -> SORTED_VARIANT_CONSEQUENCE_MAP.getOrDefault(soTerm.getName(), Integer.MAX_VALUE))
							.min(Integer::compareTo)
							.orElse(Integer.MAX_VALUE);
				}))
				.orElse(null);
	}

	@Transient
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonView({CurationView.VariantView.class})
	public List<String> getHgvsC() {
		if (predictedVariantConsequences == null) {
			return Collections.emptyList();
		}
		return predictedVariantConsequences.stream()
				.map(PredictedVariantConsequence::getHgvsCodingNomenclature)
				.filter(Objects::nonNull)
				.distinct()
				.sorted()
				.collect(Collectors.toList());
	}

	@Transient
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonView({CurationView.VariantView.class})
	public List<String> getHgvsP() {
		if (predictedVariantConsequences == null) {
			return Collections.emptyList();
		}
		return predictedVariantConsequences.stream()
				.map(PredictedVariantConsequence::getHgvsProteinNomenclature)
				.filter(Objects::nonNull)
				.distinct()
				.sorted()
				.collect(Collectors.toList());
	}

	@Transient
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonView({CurationView.VariantView.class})
	public List<Gene> getOverlapGenes() {
		if (predictedVariantConsequences == null) {
			return Collections.emptyList();
		}
		return predictedVariantConsequences.stream()
				.map(predictedVariantConsequence -> {
					var transcript = predictedVariantConsequence.getVariantTranscript();
					if (transcript == null || transcript.getTranscriptGeneAssociations() == null) {
						return Collections.<Gene>emptyList();
					}
					return transcript
							.getTranscriptGeneAssociations()
							.stream().map(TranscriptGeneAssociation::getTranscriptGeneAssociationObject)
							.toList();
				})
				.flatMap(Collection::stream)
				.distinct()
				.sorted(Comparator.comparing(gene -> gene.getGeneSymbol().getDisplayText()))
				.collect(Collectors.toList());
	}
}
