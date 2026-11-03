package org.alliancegenome.curation_api.model.entities;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.constants.ReferenceConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.bridges.ReferenceTypeBridge;
import org.alliancegenome.curation_api.model.entities.base.CurieObject;
import org.alliancegenome.curation_api.view.CurationView;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.TypeBinderRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.TypeBinding;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@TypeBinding(binder = @TypeBinderRef(type = ReferenceTypeBridge.class))
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
	@JsonSubTypes.Type(value = Reference.class, name = "Reference"),
	@JsonSubTypes.Type(value = Reference.class, name = "LiteratureReference")
})
@Schema(name = "Reference", description = "Reference: a reference")
@AGRCurationSchemaVersion(min = "1.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = {CurieObject.class}, partial = true)
@Table(
	indexes = {
		@Index(name = "reference_curie_index", columnList = "curie"),
		@Index(name = "reference_createdby_index", columnList = "createdBy_id"),
		@Index(name = "reference_updatedby_index", columnList = "updatedBy_id")
	},
	uniqueConstraints = {
		@UniqueConstraint(name = "reference_curie_uk", columnNames = "curie")
	}
)
public class Reference extends CurieObject {

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToMany(cascade = CascadeType.MERGE, orphanRemoval = true)
	@Fetch(FetchMode.SUBSELECT)
	@JsonView({ CurationView.FieldsOnly.class, CurationView.ForPublic.class, CurationView.AlleleSummaryDocument.class, CurationView.VariantSummaryDocument.class, CurationView.TransgenicAllelesDocument.class, CurationView.AlleleDetailView.class })
	@JoinTable(
		indexes = {
			@Index(name = "reference_crossreference_reference_index", columnList = "Reference_id"),
			@Index(name = "reference_crossreference_crossreferences_index", columnList = "crossReferences_id")
		}
	)
	private Set<CrossReference> crossReferences;

	@JsonView({ CurationView.FieldsOnly.class, CurationView.ForPublic.class, CurationView.AlleleSummaryDocument.class, CurationView.TransgenicAllelesDocument.class, CurationView.AlleleDetailView.class })
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "shortCitation_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@Column(columnDefinition = "TEXT")
	private String shortCitation;

	/**
	 * Display priority: PMID if present in crossReferences, else PubMod ID (MOD-prefixed),
	 * else the reference's own AGRKB curie.
	 */
	@Transient
	@JsonView({CurationView.ForPublic.class, CurationView.GeneExpressionDocument.class, CurationView.AlleleSummaryDocument.class, CurationView.VariantSummaryDocument.class, CurationView.SequenceSummaryDocument.class, CurationView.TransgenicAllelesDocument.class})
	public String getReferenceID() {
		return getReferenceID(true);
	}

	/**
	 * Display priority: PubMod ID (MOD-prefixed) if present in crossReferences, else the reference's own AGRKB curie.
	 */
	@Transient
	@JsonView({CurationView.ForPublic.class, CurationView.GeneExpressionDocument.class, CurationView.AlleleSummaryDocument.class, CurationView.TransgenicAllelesDocument.class})
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
