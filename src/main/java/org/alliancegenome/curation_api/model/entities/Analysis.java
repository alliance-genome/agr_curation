package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.ontology.FBCVTerm;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
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

/**
 * SCRUM-6714: an analysis of a dataset, currently used to carry FB scRNA-seq analysis metadata on
 * an HTPExpressionDatasetAnnotation. Cascade-owned by its parent, not an independently curatable
 * entity - same pattern as BioSampleAge/MicroarraySampleDetails on HTPExpressionDatasetSampleAnnotation.
 */
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Schema(name = "Analysis", description = "Analysis: an analysis of a dataset")
@AGRCurationSchemaVersion(min = "2.19.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
@Table(indexes = {
	@Index(name = "analysis_analysistype_index", columnList = "analysistype_id"),
	@Index(name = "analysis_dataprovider_index", columnList = "dataprovider_id"),
	@Index(name = "analysis_dataprovidercrossreference_index", columnList = "dataprovidercrossreference_id")
})
public class Analysis extends AuditedObject {

	@IndexedEmbedded(includePaths = { "name", "curie", "name_keyword", "curie_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ CurationView.FieldsOnly.class })
	private FBCVTerm analysisType;

	@IndexedEmbedded(includePaths = { "abbreviation", "fullName", "shortName", "abbreviation_keyword", "fullName_keyword", "shortName_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView({ CurationView.FieldsOnly.class })
	private Organization dataProvider;

	@IndexedEmbedded(includePaths = { "displayName", "referencedCurie", "displayName_keyword", "referencedCurie_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne(orphanRemoval = true)
	@Fetch(FetchMode.SELECT)
	@JsonView({ CurationView.FieldsOnly.class })
	private CrossReference dataProviderCrossReference;

	@IndexedEmbedded(includePaths = { "freeText", "freeText_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ CurationView.FieldsAndLists.class })
	@JoinTable(indexes = {
		@Index(name = "analysis_note_analysis_index", columnList = "analysis_id"),
		@Index(name = "analysis_note_relatednotes_index", columnList = "relatednotes_id")
	})
	private List<Note> relatedNotes;

}
