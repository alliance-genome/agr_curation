package org.alliancegenome.curation_api.model.entities.associations;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.EvidenceAssociation;
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

/**
 * SCRUM-6535: a cassette that is part of a construct.
 *
 * LinkML puts this under an abstract ConstructAssociation that holds the subject, but no such base
 * exists in Java: ConstructGenomicEntityAssociation extends EvidenceAssociation directly and
 * declares constructAssociationSubject itself. This class follows its sibling rather than
 * introducing the base, which would mean reworking a class with a wide shipped surface. Hoisting
 * both onto a ConstructAssociation @MappedSuperclass later would be schema-neutral, exactly as
 * CassetteAssociation is for the three cassette associations.
 */
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "2.18.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = {EvidenceAssociation.class})
@Schema(name = "ConstructCassetteAssociation", description = "ConstructCassetteAssociation: a construct cassette association")

@Table(indexes = {
	@Index(columnList = "internal"),
	@Index(columnList = "obsolete"),
	@Index(columnList = "createdBy_id"),
	@Index(columnList = "updatedBy_id"),
	@Index(columnList = "constructassociationsubject_id"),
	@Index(columnList = "constructcassetteassociationobject_id"),
	@Index(columnList = "relation_id")
})

public class ConstructCassetteAssociation extends EvidenceAssociation {

	@IndexedEmbedded(includePaths = {
		"curie", "constructSymbol.displayText", "constructSymbol.formatText",
		"constructFullName.displayText", "constructFullName.formatText", "primaryExternalId", "modInternalId",
		"curie_keyword", "constructSymbol.displayText_keyword", "constructSymbol.formatText_keyword",
		"constructFullName.displayText_keyword", "constructFullName.formatText_keyword", "primaryExternalId_keyword", "modInternalId_keyword"})
	@ManyToOne
	@JsonView({CurationView.FieldsOnly.class})
	@JsonIgnoreProperties({"constructGenomicEntityAssociations", "constructCassetteAssociations"})
	@Fetch(FetchMode.JOIN)
	private Construct constructAssociationSubject;

	/**
	 * Expected to be has_part (BFO:0000051), from the 'Construct Cassette Association Relation'
	 * vocabulary. Left unconstrained here and enforced in the validator, as for the other
	 * construct and cassette associations.
	 */
	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({CurationView.FieldsOnly.class})
	private VocabularyTerm relation;

	@IndexedEmbedded(includePaths = {
		"curie", "cassetteSymbol.displayText", "cassetteSymbol.formatText",
		"cassetteFullName.displayText", "cassetteFullName.formatText", "primaryExternalId", "modInternalId",
		"curie_keyword", "cassetteSymbol.displayText_keyword", "cassetteSymbol.formatText_keyword",
		"cassetteFullName.displayText_keyword", "cassetteFullName.formatText_keyword", "primaryExternalId_keyword", "modInternalId_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({CurationView.FieldsOnly.class})
	@JsonIgnoreProperties({"constructCassetteAssociations", "cassetteGenomicEntityAssociations", "cassetteTransgenicToolAssociations", "cassetteStrAssociations"})
	private Cassette constructCassetteAssociationObject;

	@IndexedEmbedded(includePaths = {"freeText", "noteType.name", "references.curie",
		"references.primaryCrossReferenceCurie", "freeText_keyword", "noteType.name_keyword", "references.curie_keyword",
		"references.primaryCrossReferenceCurie_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({CurationView.FieldsAndLists.class, CurationView.ConstructView.class})
	@JoinTable(
		name = "constructcassetteassociation_note",
		joinColumns = @JoinColumn(name = "constructcassetteassociation_id"),
		inverseJoinColumns = @JoinColumn(name = "relatednotes_id"),
		indexes = {
			@Index(name = "constructcassetteassoc_note_cca_index", columnList = "constructcassetteassociation_id"),
			@Index(name = "constructcassetteassoc_note_relatednotes_index", columnList = "relatednotes_id")
		}
	)
	private List<Note> relatedNotes;
}
