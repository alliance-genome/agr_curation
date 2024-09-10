package org.alliancegenome.curation_api.model.entities.associations.codingSequenceAssociations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.AssemblyComponent;
import org.alliancegenome.curation_api.model.entities.CodingSequence;
import org.alliancegenome.curation_api.model.entities.LocationAssociation;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "2.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { LocationAssociation.class })
@Schema(name = "CodingSequenceGenomicLocationAssociation", description = "POJO representing an association between a CDS and a genomic location")
public class CodingSequenceGenomicLocationAssociation extends LocationAssociation {

	@IndexedEmbedded(includePaths = {
		"curie", "curie_keyword", "modEntityId", "modEntityId_keyword",
		"modInternalId", "modInternalId_keyword", "name", "name_keyword"
	})
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@JsonIgnoreProperties("codingSequenceGenomicLocationAssociations")
	@Fetch(FetchMode.JOIN)
	private CodingSequence codingSequenceAssociationSubject;

	@IndexedEmbedded(includePaths = {
		"curie", "curie_keyword", "modEntityId", "modEntityId_keyword",
		"modInternalId", "modInternalId_keyword", "name", "name_keyword"
	})
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@Fetch(FetchMode.JOIN)
	private AssemblyComponent codingSequenceGenomicLocationAssociationObject;
	
	@GenericField(projectable = Projectable.YES, sortable = Sortable.YES)
	@JsonView({ View.FieldsOnly.class })
	private Integer phase;
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "phenotypeAnnotationObject_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	@Column(length = 1)
	private String strand;
}
