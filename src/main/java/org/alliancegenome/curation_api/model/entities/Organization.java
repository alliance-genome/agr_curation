package org.alliancegenome.curation_api.model.entities;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = AllianceMember.class, name = "AllianceMember") })
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "1.4.1", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { Agent.class })
@Table(indexes = { @Index(name = "organization_homepageresourcedescriptorpage_id_index", columnList = "homepageresourcedescriptorpage_id") })
public class Organization extends Agent {

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "abbreviation_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class, View.PersonSettingView.class })
	@Column(unique = true)
	private String abbreviation;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "fullName_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class, View.PersonSettingView.class })
	private String fullName;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "shortName_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class, View.PersonSettingView.class })
	private String shortName;
	
	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne
	@JsonView({ View.FieldsOnly.class })
	private ResourceDescriptorPage homepageResourceDescriptorPage;
}
