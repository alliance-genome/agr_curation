package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.associations.TransgenicToolTransgenicToolAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolUseSlotAnnotation;
import org.alliancegenome.curation_api.view.CurationView;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * SCRUM-6535: a commonly used sequence forming part of a Construct or Transgene, with properties
 * exploited to study another gene product or a biological process.
 *
 * No @CurieSubdomain: MaTI has no subdomain for transgenic tools, so AGRKB minting does not apply
 * to this class yet. See CurieSubdomain for the registry.
 */
@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Schema(name = "TransgenicTool", description = "TransgenicTool: a transgenic tool")
@ToString(exclude = {"transgenicToolSymbol", "transgenicToolFullName", "transgenicToolSynonyms", "transgenicToolUses", "transgenicToolTransgenicToolAssociations"}, callSuper = true)
@AGRCurationSchemaVersion(min = "2.19.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { Reagent.class })
public class TransgenicTool extends Reagent {

	/** A compatible tool as the search results show it: relation, tool symbol and tool identifier. */
	public record AssociatedTransgenicTool(
		@JsonView({ CurationView.TransgenicToolView.class }) String relation,
		@JsonView({ CurationView.TransgenicToolView.class }) String symbol,
		@JsonView({ CurationView.TransgenicToolView.class }) String identifier) {
	}

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToOne(mappedBy = "singleTransgenicTool", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ CurationView.FieldsOnly.class })
	private TransgenicToolSymbolSlotAnnotation transgenicToolSymbol;

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToOne(mappedBy = "singleTransgenicTool", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ CurationView.FieldsOnly.class })
	private TransgenicToolFullNameSlotAnnotation transgenicToolFullName;

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToMany(mappedBy = "singleTransgenicTool", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.TransgenicToolView.class })
	private List<TransgenicToolSynonymSlotAnnotation> transgenicToolSynonyms;

	@IndexedEmbedded(includePaths = { "uses.curie", "uses.name", "uses.curie_keyword", "uses.name_keyword" })
	@OneToMany(mappedBy = "singleTransgenicTool", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.TransgenicToolView.class })
	private List<TransgenicToolUseSlotAnnotation> transgenicToolUses;

	@IndexedEmbedded(includePaths = {
		"primaryCrossReferenceCurie", "crossReferences.referencedCurie", "crossReferences.displayName", "curie",
		"primaryCrossReferenceCurie_keyword", "crossReferences.referencedCurie_keyword", "crossReferences.displayName_keyword", "curie_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.TransgenicToolView.class })
	@JoinTable(indexes = {
		@Index(name = "transgenictool_reference_transgenictool_index", columnList = "transgenictool_id"),
		@Index(name = "transgenictool_reference_references_index", columnList = "references_id")
	})
	private List<Reference> references;

	@IndexedEmbedded(includePaths = { "referencedCurie", "displayName", "referencedCurie_keyword", "displayName_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.TransgenicToolView.class })
	@JoinTable(indexes = {
		@Index(name = "transgenictool_crossreference_transgenictool_index", columnList = "transgenictool_id"),
		@Index(name = "transgenictool_crossreference_crossreferences_index", columnList = "crossreferences_id")
	})
	private List<CrossReference> crossReferences;

	/**
	 * SCRUM-6543: the compatible tools this tool was submitted as the subject of. The relation is
	 * symmetrical but associations are stored as submitted, so a tool can also be the object of
	 * associations listed on the other tool.
	 */
	@IndexedEmbedded(includePaths = {
		"transgenicToolTransgenicToolAssociationObject.curie", "transgenicToolTransgenicToolAssociationObject.primaryExternalId",
		"transgenicToolTransgenicToolAssociationObject.modInternalId", "transgenicToolTransgenicToolAssociationObject.transgenicToolSymbol.displayText",
		"transgenicToolTransgenicToolAssociationObject.transgenicToolSymbol.formatText", "relation.name",
		"transgenicToolTransgenicToolAssociationObject.curie_keyword", "transgenicToolTransgenicToolAssociationObject.primaryExternalId_keyword",
		"transgenicToolTransgenicToolAssociationObject.modInternalId_keyword", "transgenicToolTransgenicToolAssociationObject.transgenicToolSymbol.displayText_keyword",
		"transgenicToolTransgenicToolAssociationObject.transgenicToolSymbol.formatText_keyword", "relation.name_keyword"
	})
	@OneToMany(mappedBy = "transgenicToolAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	// In no view: an association is only useful with the tool it points to, and a tool nested in a
	// tool view would carry its associations again and loop (A -> B -> A). The tool views have
	// associatedTransgenicTools instead, and the associations themselves, with both tools, come from
	// /transgenictooltransgenictoolassociation/find.
	private List<TransgenicToolTransgenicToolAssociation> transgenicToolTransgenicToolAssociations;

	// The tools this one is submitted as compatible with, reduced to what the table shows (a tool has
	// a dozen at most). READ_ONLY: computed, so a client sending a tool back need not drop it first.
	@Transient
	@JsonView({ CurationView.TransgenicToolView.class })
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	public List<AssociatedTransgenicTool> getAssociatedTransgenicTools() {
		if (transgenicToolTransgenicToolAssociations == null) {
			return List.of();
		}
		return transgenicToolTransgenicToolAssociations.stream()
			.map(association -> {
				TransgenicTool tool = association.getTransgenicToolTransgenicToolAssociationObject();
				return new AssociatedTransgenicTool(
					association.getRelation() == null ? null : association.getRelation().getName(),
					tool == null || tool.getTransgenicToolSymbol() == null ? null : tool.getTransgenicToolSymbol().getDisplayText(),
					tool == null ? null : modIdentifier(tool));
			})
			.toList();
	}

	// The MOD identifier, which curators recognise, rather than the AGRKB curie getIdentifier() prefers.
	private static String modIdentifier(TransgenicTool tool) {
		if (StringUtils.isNotBlank(tool.getPrimaryExternalId())) {
			return tool.getPrimaryExternalId();
		}
		if (StringUtils.isNotBlank(tool.getModInternalId())) {
			return tool.getModInternalId();
		}
		return tool.getCurie();
	}
}
