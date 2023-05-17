package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.bridges.BooleanAndNullValueBridge;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotation;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = { "alleleDiseaseAnnotations", "alleleMutationTypes", "alleleSymbol", "alleleFullName", "alleleSynonyms", "alleleSecondaryIds", "alleleInheritanceModes", "alleleGermlineTransmissionStatus" }, callSuper = true)
@AGRCurationSchemaVersion(min = "1.5.1", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { GenomicEntity.class }, partial = true)
@Table(indexes = { @Index(name = "allele_inCollection_index", columnList = "inCollection_id"), })
public class Allele extends GenomicEntity {

	@IndexedEmbedded(includeDepth = 2)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JoinTable(indexes = { @Index(columnList = "Allele_curie"), @Index(columnList = "references_curie") })
	@JsonView({ View.FieldsAndLists.class, View.AlleleView.class })
	private List<Reference> references;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private VocabularyTerm inCollection;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = BooleanAndNullValueBridge.class))
	@KeywordField(name = "isExtinct_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, valueBridge = @ValueBridgeRef(type = BooleanAndNullValueBridge.class))
	@JsonView({ View.FieldsOnly.class })
	private Boolean isExtinct;

	@OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
	private List<AlleleDiseaseAnnotation> alleleDiseaseAnnotations;

	@IndexedEmbedded(includePaths = { "mutationTypes.curie", "mutationTypes.name", "evidence.curie",
			"mutationTypes.curie_keyword", "mutationTypes.name_keyword", "evidence.curie_keyword"})
	@OneToMany(mappedBy = "singleAllele", cascade = CascadeType.ALL)
	@JsonManagedReference
	@JsonView({ View.FieldsAndLists.class, View.AlleleView.class })
	private List<AlleleMutationTypeSlotAnnotation> alleleMutationTypes;
	
	@IndexedEmbedded(includePaths = { "inheritanceMode.name", "phenotypeTerm.curie", "phenotypeTerm.name", "phenotypeStatement", "evidence.curie",
			"inheritanceMode.name_keyword", "phenotypeTerm.curie_keyword", "phenotypeTerm.name_keyword", "phenotypeStatement_keyword", "evidence.curie_keyword"})
	@OneToMany(mappedBy = "singleAllele", cascade = CascadeType.ALL)
	@JsonManagedReference
	@JsonView({ View.FieldsAndLists.class, View.AlleleView.class })
	private List<AlleleInheritanceModeSlotAnnotation> alleleInheritanceModes;

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie",
			"displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToOne(mappedBy = "singleAllele", cascade = CascadeType.ALL)
	@JsonManagedReference
	@JsonView({ View.FieldsOnly.class })
	private AlleleSymbolSlotAnnotation alleleSymbol;

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie",
			"displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToOne(mappedBy = "singleAllele", cascade = CascadeType.ALL)
	@JsonManagedReference
	@JsonView({ View.FieldsOnly.class })
	private AlleleFullNameSlotAnnotation alleleFullName;

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie",
			"displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToMany(mappedBy = "singleAllele", cascade = CascadeType.ALL)
	@JsonManagedReference
	@JsonView({ View.FieldsAndLists.class, View.AlleleView.class })
	private List<AlleleSynonymSlotAnnotation> alleleSynonyms;

	@IndexedEmbedded(includePaths = { "secondaryId", "evidence.curie",
			"secondaryId_keyword", "evidence.curie_keyword"})
	@OneToMany(mappedBy = "singleAllele", cascade = CascadeType.ALL)
	@JsonManagedReference
	@JsonView({ View.FieldsAndLists.class, View.AlleleView.class })
	private List<AlleleSecondaryIdSlotAnnotation> alleleSecondaryIds;
	
	@IndexedEmbedded(includePaths = { "germlineTransmissionStatus.name", "evidence.curie",
			"germlineTransmissionStatus.name_keyword", "evidence.curie_keyword"})
	@OneToOne(mappedBy = "singleAllele", cascade = CascadeType.ALL)
	@JsonManagedReference
	@JsonView({ View.FieldsOnly.class })
	private AlleleGermlineTransmissionStatusSlotAnnotation alleleGermlineTransmissionStatus;
}
