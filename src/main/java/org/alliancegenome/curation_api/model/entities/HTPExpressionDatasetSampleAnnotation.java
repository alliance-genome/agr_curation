package org.alliancegenome.curation_api.model.entities;

import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.alliancegenome.curation_api.model.entities.ontology.MMOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.OBITerm;
import org.alliancegenome.curation_api.view.View;
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

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Schema(name = "HTPExpressionDatasetSampleAnnotation", description = "POJO that represents the HighThroughputExpressionDatasetSampleAnnotation")
@AGRCurationSchemaVersion(min = "2.6.2", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
public class HTPExpressionDatasetSampleAnnotation extends AuditedObject {

    @IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsOnly.class })
    private ExternalDataBaseEntity htpExpressionSample;

    @JsonView({ View.FieldsOnly.class })
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "name_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
    private String htpExpressionSampleTitle;

    @IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name", "abbreviation",
    "curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword", "abbreviation_keyword" })
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @ManyToOne
    @JsonView(View.FieldsAndLists.class)
    private OBITerm htpExpressionSampleType;

    @IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name", "abbreviation",
    "curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword", "abbreviation_keyword" })
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @ManyToOne
    @JsonView(View.FieldsAndLists.class)
    private MMOTerm expressionAssayUsed;

    @IndexedEmbedded(includePaths = {
		"sourceOrganization.abbreviation", "sourceOrganization.fullName", "sourceOrganization.shortName", "crossReference.displayName", "crossReference.referencedCurie",
		"sourceOrganization.abbreviation_keyword", "sourceOrganization.fullName_keyword", "sourceOrganization.shortName_keyword", "crossReference.displayName_keyword", "crossReference.referencedCurie_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	DataProvider dataProvider;

    private VocabularyTerm geneticSex;

    private List<AnatomicalSite> htpExpressionSampleLocations;

    private Note relatedNotes;

    private VocabularyTerm sequencingFormat;

    private NCBITaxonTerm taxon;

    private String abundance;

    private String assemblyVersions;

    private List<ExternalDataBaseEntity> datasetIds;
    
}
