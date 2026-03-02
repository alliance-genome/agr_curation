package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.associations.AgmAgmAssociation;
import org.alliancegenome.curation_api.model.entities.associations.AgmAlleleAssociation;
import org.alliancegenome.curation_api.model.entities.associations.AgmSequenceTargetingReagentAssociation;
import org.alliancegenome.curation_api.model.entities.associations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmSynonymSlotAnnotation;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {
		"agmDiseaseAnnotations",
		"agmPhenotypeAnnotations",
		"constructGenomicEntityAssociations",
		"agmFullName",
		"agmSynonyms",
		"agmSecondaryIds",
		"agmSequenceTargetingReagentAssociations",
		"components",
		"parentalPopulations" }, callSuper = true)
@Schema(name = "AffectedGenomicModel", description = "AffectedGenomicModel: an affected genomic model")
@AGRCurationSchemaVersion(min = "2.12.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = {GenomicEntity.class}, partial = true)
public class AffectedGenomicModel extends GenomicEntity {

	@OneToMany(mappedBy = "diseaseAnnotationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<AGMDiseaseAnnotation> agmDiseaseAnnotations;

	@OneToMany(mappedBy = "phenotypeAnnotationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<AGMPhenotypeAnnotation> agmPhenotypeAnnotations;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({CurationView.FieldsOnly.class, CurationView.ForPublic.class, CurationView.ModelDocument.class})
	private VocabularyTerm subtype;

	@IndexedEmbedded(includePaths = {"secondaryId", "evidence.curie", "secondaryId_keyword", "evidence.curie_keyword"})
	@OneToMany(mappedBy = "singleAgm", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({CurationView.FieldsAndLists.class, CurationView.AffectedGenomicModelView.class})
	private List<AgmSecondaryIdSlotAnnotation> agmSecondaryIds;


	@IndexedEmbedded(includePaths = {
		"constructAssociationSubject.curie", "constructAssociationSubject.constructSymbol.displayText", "constructAssociationSubject.constructSymbol.formatText",
		"constructAssociationSubject.constructFullName.displayText", "constructAssociationSubject.constructFullName.formatText", "constructAssociationSubject.primaryExternalId",
		"constructAssociationSubject.curie_keyword", "constructAssociationSubject.constructSymbol.displayText_keyword", "constructAssociationSubject.constructSymbol.formatText_keyword",
		"constructAssociationSubject.constructFullName.displayText_keyword", "constructAssociationSubject.constructFullName.formatText_keyword", "constructAssociationSubject.primaryExternalId_keyword"
	})
	@OneToMany(mappedBy = "constructGenomicEntityAssociationObject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({CurationView.FieldsAndLists.class, CurationView.GeneDetailView.class})
	private List<ConstructGenomicEntityAssociation> constructGenomicEntityAssociations;

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToOne(mappedBy = "singleAgm", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ CurationView.FieldsOnly.class, CurationView.AffectedGenomicModelView.class, CurationView.ForPublic.class, CurationView.ModelDocument.class })
	private AgmFullNameSlotAnnotation agmFullName;

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToMany(mappedBy = "singleAgm", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.AffectedGenomicModelView.class })
	private List<AgmSynonymSlotAnnotation> agmSynonyms;

	@IndexedEmbedded(includePaths = {
		"agmSequenceTargetingReagentAssociationObject.name",
		"agmSequenceTargetingReagentAssociationObject.name_keyword",
		"agmSequenceTargetingReagentAssociationObject.synonyms",
		"agmSequenceTargetingReagentAssociationObject.synonyms_keyword",
		"agmSequenceTargetingReagentAssociationObject.secondaryIdentifiers",
		"agmSequenceTargetingReagentAssociationObject.secondaryIdentifiers_keyword"
	})
	@OneToMany(mappedBy = "agmAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({CurationView.FieldsAndLists.class, CurationView.AffectedGenomicModelDetailView.class})
	private List<AgmSequenceTargetingReagentAssociation> agmSequenceTargetingReagentAssociations;

	@OneToMany(mappedBy = "agmAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({CurationView.FieldsAndLists.class, CurationView.AffectedGenomicModelDetailView.class})
	private List<AgmAlleleAssociation> components;

	@IndexedEmbedded(includePaths = {
		"agmAgmAssociationObject.name",
		"agmAgmAssociationObject.name_keyword",
		"agmAgmAssociationObject.synonyms",
		"agmAgmAssociationObject.synonyms_keyword",
		"agmAgmAssociationObject.secondaryIdentifiers",
		"agmAgmAssociationObject.secondaryIdentifiers_keyword"
	})
	@OneToMany(mappedBy = "agmAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({CurationView.FieldsAndLists.class, CurationView.AffectedGenomicModelDetailView.class})
	private List<AgmAgmAssociation> parentalPopulations;
}
