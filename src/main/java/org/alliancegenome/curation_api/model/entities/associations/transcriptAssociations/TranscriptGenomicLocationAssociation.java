package org.alliancegenome.curation_api.model.entities.associations.transcriptAssociations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.AssemblyComponent;
import org.alliancegenome.curation_api.model.entities.LocationAssociation;
import org.alliancegenome.curation_api.model.entities.Transcript;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
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
@AGRCurationSchemaVersion(min = "2.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { LocationAssociation.class })
@Schema(name = "TranscriptGenomicLocationAssociation", description = "POJO representing an association between a transcript and a genomic location")
@Table(
	indexes = {
		@Index(name = "transcriptlocationassociation_relation_index", columnList = "relation_id"),
		@Index(name = "transcriptlocationassociation_subject_index", columnList = "transcriptassociationsubject_id"),
		@Index(name = "transcriptlocationassociation_object_index", columnList = "transcriptgenomiclocationassociationobject_id")
	}
)
public class TranscriptGenomicLocationAssociation extends LocationAssociation {

	@IndexedEmbedded(includePaths = {
		"curie", "curie_keyword", "modEntityId", "modEntityId_keyword",
		"modInternalId", "modInternalId_keyword", "name", "name_keyword"
	})
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@JsonIgnoreProperties({
		"transcriptCodingSequenceAssociations",
		"transcriptExonAssociations",
		"transcriptGeneAssociations",
		"transcriptGenomicLocationAssociations"
	})
	@Fetch(FetchMode.JOIN)
	private Transcript transcriptAssociationSubject;

	@IndexedEmbedded(includePaths = {
		"curie", "curie_keyword", "modEntityId", "modEntityId_keyword",
		"modInternalId", "modInternalId_keyword", "name", "name_keyword"
	})
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@JsonIgnoreProperties({
		"codingSequenceGenomicLocationAssociations",
		"exonGenomicLocationAssociations",
		"transcriptGenomicLocationAssociations"
	})
	@Fetch(FetchMode.JOIN)
	private AssemblyComponent transcriptGenomicLocationAssociationObject;
	
	@GenericField(projectable = Projectable.YES, sortable = Sortable.YES)
	@JsonView({ View.FieldsOnly.class })
	private Integer phase;
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "phenotypeAnnotationObject_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	@Column(length = 1)
	private String strand;
}
