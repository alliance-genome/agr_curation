package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@AGRCurationSchemaVersion(min = "1.3.2", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { Agent.class })
@Table(indexes = { 
	@Index(name = "person_createdby_index", columnList = "createdBy_id"), 
	@Index(name = "person_updatedby_index", columnList = "updatedBy_id"),
	@Index(name = "person_uniqueid_index", columnList = "uniqueid"),
	@Index(name = "person_allianceMember_index", columnList = "allianceMember_id"),
})
public class Person extends Agent {

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "firstName_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class, View.PersonSettingView.class })
	private String firstName;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "middleName_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class, View.PersonSettingView.class })
	private String middleName;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "lastName_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class, View.PersonSettingView.class })
	private String lastName;

	@KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
	@ElementCollection
	@JsonView({ View.FieldsAndLists.class, View.PersonSettingView.class })
	@JoinTable(indexes = @Index(columnList = "person_id"))
	private List<String> emails;

	@KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
	@ElementCollection
	@JsonView({ View.FieldsAndLists.class, View.PersonSettingView.class })
	@JoinTable(indexes = @Index(columnList = "person_id"))
	private List<String> oldEmails;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "orcid_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class, View.PersonSettingView.class })
	@Column(unique = true)
	private String orcid;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "modEntityId_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class, View.PersonSettingView.class })
	@Column(unique = true)
	private String modEntityId;

	@OneToMany(mappedBy = "person")
	@JsonView({ View.PersonSettingView.class })
	private List<PersonSetting> settings;

	@ManyToOne
	@JsonView({ View.FieldsOnly.class, View.PersonSettingView.class })
	@Fetch(FetchMode.JOIN)
	private AllianceMember allianceMember;

}
