package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.associations.ExonGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.entities.associations.TranscriptExonAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

//@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"exonGenomicLocationAssociations", "transcriptExonAssociations"}, callSuper = true)
@Schema(name = "Exon", description = "Exon: an exon")
@AGRCurationSchemaVersion(min = "2.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { GenomicEntity.class })
@Table(indexes = {
	@Index(name = "exon_uniqueid_index", columnList = "uniqueid"),
	@Index(name = "exon_exonType_index", columnList = "exonType_id")
})
public class Exon extends GenomicEntity {

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "uniqueId_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ CurationView.FieldsOnly.class })
	@EqualsAndHashCode.Include
	protected String uniqueId;
	
	@JsonView({ CurationView.FieldsOnly.class })
	private String name;

	@IndexedEmbedded(includePaths = {"curie", "name", "curie_keyword", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ CurationView.FieldsOnly.class })
	private SOTerm exonType;

	@IndexedEmbedded(
		includePaths = {
			"exonGenomicLocationAssociationObject.curie", "exonGenomicLocationAssociationObject.curie_keyword",
			"exonGenomicLocationAssociationObject.primaryExternalId", "exonGenomicLocationAssociationObject.primaryExternalId_keyword",
			"exonGenomicLocationAssociationObject.modInternalId", "exonGenomicLocationAssociationObject.modInternalId_keyword",
			"start", "end"
		}
	)
	@OneToMany(mappedBy = "exonAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ CurationView.FieldsAndLists.class })
	private List<ExonGenomicLocationAssociation> exonGenomicLocationAssociations;
	
	@OneToMany(mappedBy = "transcriptExonAssociationObject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ CurationView.FieldsAndLists.class })
	private List<TranscriptExonAssociation> transcriptExonAssociations;

}
