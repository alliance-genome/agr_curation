package org.alliancegenome.curation_api.model.entities.associations;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.EvidenceAssociation;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "2.2.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = {EvidenceAssociation.class})
@Schema(name = "CassetteGenomicEntityAssociation", description = "CassetteGenomicEntityAssociation: a cassette genomic entity association")

@Table(indexes = {
	@Index(columnList = "internal"),
	@Index(columnList = "obsolete"),
	@Index(columnList = "createdBy_id"),
	@Index(columnList = "updatedBy_id"),
	@Index(columnList = "cassetteassociationsubject_id"),
	@Index(columnList = "cassettegenomicentityassociationobject_id"),
	@Index(columnList = "relation_id")
})

public class CassetteGenomicEntityAssociation extends EvidenceAssociation {

	@IndexedEmbedded(includePaths = {
		"curie", "cassetteSymbol.displayText", "cassetteSymbol.formatText",
		"cassetteFullName.displayText", "cassetteFullName.formatText", "primaryExternalId", "modInternalId",
		"curie_keyword", "cassetteSymbol.displayText_keyword", "cassetteSymbol.formatText_keyword",
		"cassetteFullName.displayText_keyword", "cassetteFullName.formatText_keyword", "primaryExternalId_keyword", "modInternalId_keyword"})
	@ManyToOne
	@JsonView({CurationView.FieldsOnly.class})
	@JsonIgnoreProperties("cassetteGenomicEntityAssociations")
	@Fetch(FetchMode.JOIN)
	private Cassette cassetteAssociationSubject;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({CurationView.FieldsOnly.class})
	private VocabularyTerm relation;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({CurationView.FieldsOnly.class})
	@JsonIgnoreProperties({
		"alleleGeneAssociations", "constructGenomicEntityAssociations", "cassetteGenomicEntityAssociations", "sequenceTargetingReagentGeneAssociations",
		"transcriptGenomicLocationAssociations", "exonGenomicLocationAssociations", "codingSequenceGenomicLocationAssociations",
		"transcriptGeneAssociations", "geneGenomicLocationAssociations", "transcriptExonAssociations", "transcriptCodingSequenceAssociations"
	})
	private GenomicEntity cassetteGenomicEntityAssociationObject;

	@IndexedEmbedded(includePaths = {"freeText", "noteType.name", "references.curie",
		"references.primaryCrossReferenceCurie", "freeText_keyword", "noteType.name_keyword", "references.curie_keyword",
		"references.primaryCrossReferenceCurie_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({CurationView.FieldsAndLists.class})
	@JoinTable(
		joinColumns = @JoinColumn(name = "cassettegenomicentityassociation_id"),
		inverseJoinColumns = @JoinColumn(name = "relatedNotes_id"),
		indexes = {
			@Index(name = "cassettegeassociation_note_cgea_index", columnList = "cassettegenomicentityassociation_id"),
			@Index(name = "cassettegeassociation_note_relatednotes_index", columnList = "relatedNotes_id")
		}
	)
	private List<Note> relatedNotes;
}
