package org.alliancegenome.curation_api.model.entities.associations;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
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

/** SCRUM-6535: a cassette component that is a known transgenic tool. */
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "2.18.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = {CassetteAssociation.class})
@Schema(name = "CassetteTransgenicToolAssociation", description = "CassetteTransgenicToolAssociation: a cassette component that is a known transgenic tool.")

@Table(indexes = {
	@Index(columnList = "internal"),
	@Index(columnList = "obsolete"),
	@Index(columnList = "createdBy_id"),
	@Index(columnList = "updatedBy_id"),
	@Index(columnList = "cassetteassociationsubject_id"),
	@Index(columnList = "cassettetransgenictoolassociationobject_id"),
	@Index(columnList = "relation_id")
})

public class CassetteTransgenicToolAssociation extends CassetteAssociation {

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ CurationView.FieldsOnly.class })
	@JsonIgnoreProperties({"cassetteTransgenicToolAssociations"})
	private TransgenicTool cassetteTransgenicToolAssociationObject;

	@IndexedEmbedded(includePaths = {"freeText", "noteType.name", "references.curie",
		"references.primaryCrossReferenceCurie", "freeText_keyword", "noteType.name_keyword", "references.curie_keyword",
		"references.primaryCrossReferenceCurie_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ CurationView.FieldsAndLists.class , CurationView.CassetteView.class})
	@JoinTable(
		name = "cassettetransgenictoolassociation_note",
		joinColumns = @JoinColumn(name = "cassettetransgenictoolassociation_id"),
		inverseJoinColumns = @JoinColumn(name = "relatednotes_id"),
		indexes = {
			@Index(name = "cassettettassoc_note_assoc_index", columnList = "cassettetransgenictoolassociation_id"),
			@Index(name = "cassettettassoc_note_relatednotes_index", columnList = "relatednotes_id")
		}
	)
	private List<Note> relatedNotes;
}
