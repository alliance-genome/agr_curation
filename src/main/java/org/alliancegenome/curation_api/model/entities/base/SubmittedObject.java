package org.alliancegenome.curation_api.model.entities.base;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.view.View;
import org.apache.commons.lang3.StringUtils;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "2.0.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { CurieObject.class })
@Schema(name = "SubmittedObject", description = "POJO that represents the SubmittedObject")
public class SubmittedObject extends CurieObject {

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "modEntityId_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private String modEntityId;
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "modInternalId_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	private String modInternalId;

	@IndexedEmbedded(includePaths = {"sourceOrganization.abbreviation", "sourceOrganization.fullName", "sourceOrganization.shortName", "crossReference.displayName", "crossReference.referencedCurie",
			"sourceOrganization.abbreviation_keyword", "sourceOrganization.fullName_keyword", "sourceOrganization.shortName_keyword", "crossReference.displayName_keyword", "crossReference.referencedCurie_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView({ View.FieldsOnly.class })
	private DataProvider dataProvider;

	
	@Transient
	@JsonIgnore
	public String getIdentifier() {
		if (StringUtils.isNotBlank(curie))
			return curie;
		if (StringUtils.isNotBlank(modEntityId))
			return modEntityId;
		if (StringUtils.isNotBlank(modInternalId))
			return modInternalId;
		return null;
	}
	
	@Transient
	@JsonIgnore
	public String getSubmittedIdentifier() {
		if (StringUtils.isNotBlank(modEntityId))
			return modEntityId;
		if (StringUtils.isNotBlank(modInternalId))
			return modInternalId;
		return null;
	}
	
}
