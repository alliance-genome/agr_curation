package org.alliancegenome.curation_api.model.entities.associations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.EvidenceAssociation;
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

import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SCRUM-6535: the subject and relation shared by the three cassette association classes, matching
 * the abstract CassetteAssociation in LinkML.
 *
 * A {@code @MappedSuperclass} rather than an entity, so it adds no table of its own and each
 * subclass simply carries the two columns - the same arrangement {@link EvidenceAssociation} uses.
 * relatedNotes is deliberately NOT hoisted here: it is a collection, so each subclass needs its own
 * join table, and a shared declaration would put all three subclasses' notes in one.
 */
@MappedSuperclass
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Schema(name = "cassetteAssociation", description = "CassetteAssociation: base class for cassette associations")
@AGRCurationSchemaVersion(min = "2.18.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { EvidenceAssociation.class })
public class CassetteAssociation extends EvidenceAssociation {

	@IndexedEmbedded(includePaths = {
		"curie", "cassetteSymbol.displayText", "cassetteSymbol.formatText",
		"cassetteFullName.displayText", "cassetteFullName.formatText", "primaryExternalId", "modInternalId",
		"curie_keyword", "cassetteSymbol.displayText_keyword", "cassetteSymbol.formatText_keyword",
		"cassetteFullName.displayText_keyword", "cassetteFullName.formatText_keyword", "primaryExternalId_keyword", "modInternalId_keyword"})
	@ManyToOne
	@JsonView({ CurationView.FieldsOnly.class })
	@JsonIgnoreProperties({"cassetteGenomicEntityAssociations", "cassetteTransgenicToolAssociations", "cassetteStrAssociations"})
	@Fetch(FetchMode.JOIN)
	private Cassette cassetteAssociationSubject;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ CurationView.FieldsOnly.class })
	private VocabularyTerm relation;

}
