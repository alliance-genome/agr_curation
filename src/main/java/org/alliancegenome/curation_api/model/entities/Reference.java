package org.alliancegenome.curation_api.model.entities;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.constants.ReferenceConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.CurationView;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Schema(name = "Reference", description = "Reference: a reference")
@AGRCurationSchemaVersion(min = "1.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = {InformationContentEntity.class}, partial = true)
public class Reference extends InformationContentEntity {

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@Fetch(FetchMode.SUBSELECT)
	@JsonView({CurationView.FieldsOnly.class, CurationView.ForPublic.class, CurationView.AlleleSummaryDocument.class, CurationView.VariantSummaryDocument.class})
	@JoinTable(
		indexes = {
			@Index(name = "reference_crossreference_reference_index", columnList = "Reference_id"),
			@Index(name = "reference_crossreference_crossreferences_index", columnList = "crossReferences_id")
		}
	)
	private Set<CrossReference> crossReferences;

	@JsonView({CurationView.FieldsOnly.class, CurationView.ForPublic.class})
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "shortCitation_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@Column(columnDefinition = "TEXT")
	private String shortCitation;

	/**
	 * Display priority: PMID if present in crossReferences, else PubMod ID (MOD-prefixed),
	 * else the reference's own AGRKB curie.
	 */
	@Transient
	@JsonView({CurationView.ForPublic.class, CurationView.GeneExpressionDocument.class, CurationView.AlleleSummaryDocument.class, CurationView.VariantSummaryDocument.class, CurationView.SequenceSummaryDocument.class})
	public String getReferenceID() {
		return getReferenceID(true);
	}

	/**
	 * Display priority: PubMod ID (MOD-prefixed) if present in crossReferences, else the reference's own AGRKB curie.
	 */
	@Transient
	@JsonView({CurationView.ForPublic.class, CurationView.GeneExpressionDocument.class})
	public String getPubModID() {
		return getReferenceID(false);
	}

	@Transient
	private String getReferenceID(boolean pubmedIdFirst) {
		List<String> primaryXrefOrder = ReferenceConstants.primaryXrefOrder;
		if (!pubmedIdFirst) {
			String pmid = "PMID";
			primaryXrefOrder = ReferenceConstants.primaryXrefOrder.stream()
				.filter(s -> !s.equals(pmid)).toList();
			boolean success = primaryXrefOrder.size() < ReferenceConstants.primaryXrefOrder.size();
			if (!success) {
				throw new RuntimeException("Could not find " + pmid + " in ReferenceConstants.primaryXrefOrder");
			}
		}

		if (CollectionUtils.isNotEmpty(getCrossReferences())) {
			for (String prefix : primaryXrefOrder) {
				Optional<CrossReference> opt = getCrossReferences().stream().filter(reference -> reference.getReferencedCurie().startsWith(prefix + ":")).findFirst();
				if (opt.isPresent()) {
					return opt.map(CrossReference::getReferencedCurie).orElse(null);
				}
			}
		}

		return getCurie();
	}
}
