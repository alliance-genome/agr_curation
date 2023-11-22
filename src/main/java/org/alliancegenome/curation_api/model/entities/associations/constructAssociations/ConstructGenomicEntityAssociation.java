package org.alliancegenome.curation_api.model.entities.associations.constructAssociations;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.EvidenceAssociation;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "1.11.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { EvidenceAssociation.class })
@Schema(name = "ConstructGenomicEntityAssociation", description = "POJO representing an association between a construct and a genomic entity")
@Table(indexes = {
	@Index(name = "constructgenomicentityassociation_subject_index", columnList = "subjectConstruct_id"),
	@Index(name = "constructgenomicentityassociation_object_index", columnList = "objectGenomicEntity_curie"),
	@Index(name = "constructgenomicentityassociation_relation_index", columnList = "relation_id")
})
public class ConstructGenomicEntityAssociation extends EvidenceAssociation {

	@IndexedEmbedded(includePaths = {"curie", "constructSymbol.displayText", "constructSymbol.formatText",
			"constructFullName.displayText", "constructFullName.formatText", "modEntityId",
			"curie_keyword", "constructSymbol.displayText_keyword", "constructSymbol.formatText_keyword",
			"constructFullName.displayText_keyword", "constructFullName.formatText_keyword", "modEntityId_keyword",})
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@JsonIgnoreProperties("constructGenomicEntityAssociations")
	@Fetch(FetchMode.JOIN)
	private Construct subjectConstruct;
	
	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private VocabularyTerm relation;
	
	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne
	@JsonView({ View.FieldsOnly.class })
	@JsonIgnoreProperties({"alleleGeneAssociations", "constructGenomicEntityAssociations"})
	private GenomicEntity objectGenomicEntity;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToMany
	@JsonView({ View.FieldsAndLists.class, View.ConstructView.class })
	@JoinTable(indexes = {
			@Index(name = "cgeassociation_note_cgeassociation_id_index", columnList = "constructgenomicentityassociation_id"),
			@Index(name = "cgeassociation_note_relatednotes_id_index", columnList = "relatedNotes_id")
		})
	private List<Note> relatedNotes;
}
