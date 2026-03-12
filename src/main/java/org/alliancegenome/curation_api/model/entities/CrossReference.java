package org.alliancegenome.curation_api.model.entities;

import java.beans.Transient;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(callSuper = true)
@Schema(name = "CrossReference", description = "CrossReference: a cross reference")
@AGRCurationSchemaVersion(min = "1.6.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = {AuditedObject.class})
@Table(indexes = {
	@Index(name = "crossreference_createdby_index", columnList = "createdBy_id"),
	@Index(name = "crossreference_updatedby_index", columnList = "updatedBy_id"),
	@Index(name = "crossreference_resourcedescriptorpage_index", columnList = "resourcedescriptorpage_id"),
	@Index(name = "crossreference_referencedcurie_index", columnList = "referencedcurie")
})
public class CrossReference extends AuditedObject {

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "referencedCurie_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({CurationView.FieldsOnly.class, CurationView.ForPublic.class, CurationView.GeneSummaryDocument.class, CurationView.AlleleSummaryDocument.class, CurationView.GeneExpressionDocument.class, CurationView.ModelDocument.class, CurationView.VariantSummaryDocument.class, CurationView.SequenceSummaryDocument.class})
	@EqualsAndHashCode.Include
	private String referencedCurie;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "displayName_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({CurationView.FieldsOnly.class, CurationView.ForPublic.class, CurationView.GeneSummaryDocument.class, CurationView.AlleleSummaryDocument.class, CurationView.GeneExpressionDocument.class, CurationView.VariantSummaryDocument.class, CurationView.SequenceSummaryDocument.class})
	@EqualsAndHashCode.Include
	private String displayName;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({CurationView.FieldsOnly.class, CurationView.ForPublic.class, CurationView.DiseaseSummaryDocument.class, CurationView.GeneSummaryDocument.class, CurationView.AlleleSummaryDocument.class, CurationView.GeneExpressionDocument.class, CurationView.ModelDocument.class, CurationView.VariantSummaryDocument.class, CurationView.SequenceSummaryDocument.class})
	@Fetch(FetchMode.SELECT)
	private ResourceDescriptorPage resourceDescriptorPage;

	@Transient
	public String getPrefix() {
		if (!referencedCurie.contains(":")) {
			return referencedCurie;
		}

		return referencedCurie.substring(0, referencedCurie.indexOf(":"));
	}

	@Transient
	public String getUrlFromResourceDescriptorPage(String curie) {
		if (resourceDescriptorPage != null) {
			String[] array = curie.split(":");
			String localId = array[1];
			return resourceDescriptorPage.getUrlTemplate().replace("[%s]", localId);
		}
		return null;
	}
}
