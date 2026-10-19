package org.alliancegenome.curation_api.model.entities.associations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Association;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * SCRUM-6543: two transgenic tools that are compatible for use together, e.g. the parts of a binary
 * system or an enzyme and its target site (FLP/FRT).
 *
 * The relation is symmetrical, but associations are stored as submitted, so a tool's compatible
 * tools are those on either side. LinkML derives this from an EvidenceAssociation yet describes it
 * as "without evidence" and gives the DTO no evidence or notes, so like AgmAgmAssociation it extends
 * Association.
 */
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "2.18.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = {Association.class})
@Schema(name = "TransgenicToolTransgenicToolAssociation", description = "TransgenicToolTransgenicToolAssociation: two transgenic tools that are compatible for use together.")

@Table(indexes = {
	@Index(name = "tttoolassoc_internal_index", columnList = "internal"),
	@Index(name = "tttoolassoc_obsolete_index", columnList = "obsolete"),
	@Index(name = "tttoolassoc_createdby_index", columnList = "createdBy_id"),
	@Index(name = "tttoolassoc_updatedby_index", columnList = "updatedBy_id"),
	@Index(name = "tttoolassoc_subject_index", columnList = "transgenictoolassociationsubject_id"),
	@Index(name = "tttoolassoc_object_index", columnList = "transgenictooltransgenictoolassociationobject_id"),
	@Index(name = "tttoolassoc_relation_index", columnList = "relation_id")
})

public class TransgenicToolTransgenicToolAssociation extends Association {

	@IndexedEmbedded(includePaths = {
		"curie", "transgenicToolSymbol.displayText", "transgenicToolSymbol.formatText", "primaryExternalId", "modInternalId",
		"curie_keyword", "transgenicToolSymbol.displayText_keyword", "transgenicToolSymbol.formatText_keyword", "primaryExternalId_keyword", "modInternalId_keyword"})
	// The tools are in the association view only (/find and /findBy): a tool's own associations
	// (FieldsAndLists) have neither, so they cannot loop through the tool they point to
	@ManyToOne
	@JsonView({ CurationView.TransgenicToolTransgenicToolAssociationView.class })
	@Fetch(FetchMode.JOIN)
	private TransgenicTool transgenicToolAssociationSubject;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ CurationView.FieldsOnly.class })
	private VocabularyTerm relation;

	@IndexedEmbedded(includePaths = {
		"curie", "transgenicToolSymbol.displayText", "transgenicToolSymbol.formatText", "primaryExternalId", "modInternalId",
		"curie_keyword", "transgenicToolSymbol.displayText_keyword", "transgenicToolSymbol.formatText_keyword", "primaryExternalId_keyword", "modInternalId_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ CurationView.TransgenicToolTransgenicToolAssociationView.class })
	private TransgenicTool transgenicToolTransgenicToolAssociationObject;
}
