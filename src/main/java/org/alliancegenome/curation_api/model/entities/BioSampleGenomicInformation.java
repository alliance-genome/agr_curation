package org.alliancegenome.curation_api.model.entities;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

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
@Schema(name = "BioSampleGenomicInformation", description = "BioSampleGenomicInformation: a bio sample genomic information")
@AGRCurationSchemaVersion(min = "2.7.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
@Indexed
@Table(indexes = {
	@Index(name = "biosamplegenomicinfo_bioSampleAllele_index", columnList = "bioSampleAllele_id"),
	@Index(name = "biosamplegenomicinfo_bioSampleAgm_index", columnList = "bioSampleAgm_id"),
	@Index(name = "biosamplegenomicinfo_bioSampleAgmType_index", columnList = "bioSampleAgmType_id")
})
public class BioSampleGenomicInformation extends AuditedObject {

	@IndexedEmbedded(includePaths = {
		"curie", "primaryExternalId", "modInternalId", "curie_keyword", "primaryExternalId_keyword", "modInternalId_keyword",
		"alleleSymbol.formatText", "alleleSymbol.displayText", "alleleSymbol.formatText_keyword", "alleleSymbol.displayText_keyword",
		"alleleFullName.formatText", "alleleFullName.displayText", "alleleFullName.formatText_keyword", "alleleFullName.displayText_keyword",
		"alleleSynonyms.formatText", "alleleSynonyms.displayText", "alleleSynonyms.formatText_keyword", "alleleSynonyms.displayText_keyword",
		"alleleSecondaryIds.secondaryId", "alleleSecondaryIds.secondaryId_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView(CurationView.FieldsOnly.class)
	private Allele bioSampleAllele;

	@IndexedEmbedded(includePaths = {"name", "name_keyword", "curie", "curie_keyword", "primaryExternalId", "primaryExternalId_keyword", "modInternalId", "modInternalId_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView(CurationView.FieldsOnly.class)
	private AffectedGenomicModel bioSampleAgm;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ CurationView.FieldsOnly.class })
	private VocabularyTerm bioSampleAgmType;

	@JsonView(CurationView.FieldsOnly.class)
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "biosampletext_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	private String bioSampleText;

}
