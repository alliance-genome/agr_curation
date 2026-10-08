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
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
		"transgenicToolTransgenicToolAssociationObject.modInternalId", "relation.name",
		"transgenicToolTransgenicToolAssociationObject.curie_keyword", "transgenicToolTransgenicToolAssociationObject.primaryExternalId_keyword",
		"transgenicToolTransgenicToolAssociationObject.modInternalId_keyword", "relation.name_keyword"
	})
	@OneToMany(mappedBy = "transgenicToolAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.TransgenicToolView.class })
	private List<TransgenicToolTransgenicToolAssociation> transgenicToolTransgenicToolAssociations;

}
