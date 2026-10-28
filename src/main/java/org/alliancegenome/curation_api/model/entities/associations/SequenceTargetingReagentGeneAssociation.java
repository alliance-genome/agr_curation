package org.alliancegenome.curation_api.model.entities.associations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.EvidenceAssociation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
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


@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "2.3.1", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { EvidenceAssociation.class })
@Schema(name = "SequenceTargetingReagentGeneAssociation", description = "SequenceTargetingReagentGeneAssociation: a sequence targeting reagent gene association")

@Table(
	indexes = {
		@Index(name = "SQTRGeneAssociation_internal_index", columnList = "internal"),
		@Index(name = "SQTRGeneAssociation_obsolete_index", columnList = "obsolete"),
		@Index(name = "SQTRGeneAssociation_createdBy_index", columnList = "createdBy_id"),
		@Index(name = "SQTRGeneAssociation_updatedBy_index", columnList = "updatedBy_id"),
		@Index(name = "SQTRGeneAssociation_relation_index", columnList = "relation_id"),
		@Index(name = "SQTRGeneAssociation_sqtrAssociationSubject_index", columnList = "sequencetargetingreagentassociationsubject_id"),
		@Index(name = "SQTRGeneAssociation_sqtrGeneAssociationObject_index", columnList = "sequencetargetingreagentgeneassociationobject_id")
	}, name = "SQTRGeneAssociation"
	)

public class SequenceTargetingReagentGeneAssociation extends EvidenceAssociation {
	
	// Not in the STR detail view, where the STR is the parent of the association, nor in the generic
	// views, where a nested entity has its fields only
	@IndexedEmbedded(includePaths = {"name", "synonyms", "secondaryIdentifiers"})
	@ManyToOne
	@JsonView({ CurationView.SequenceTargetingReagentGeneAssociationView.class })
	@Fetch(FetchMode.JOIN)
	private SequenceTargetingReagent sequenceTargetingReagentAssociationSubject;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ CurationView.FieldsOnly.class })
	private VocabularyTerm relation;

	@IndexedEmbedded(includePaths = { "curie", "geneSymbol.displayText", "geneSymbol.formatText", "geneFullName.displayText",
		"geneFullName.formatText", "curie_keyword", "geneSymbol.displayText_keyword", "geneSymbol.formatText_keyword",
		"geneFullName.displayText_keyword", "geneFullName.formatText_keyword", "primaryExternalId", "primaryExternalId_keyword",
		"modInternalId", "modInternalId_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	// In the STR views only, so a gene's own associations do not repeat the gene
	@ManyToOne
	@JsonView({ CurationView.SequenceTargetingReagentView.class })
	private Gene sequenceTargetingReagentGeneAssociationObject;
}
