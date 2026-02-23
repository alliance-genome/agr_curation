package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.associations.AlleleConstructAssociation;
import org.alliancegenome.curation_api.model.entities.associations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.ConstructComponentSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.ConstructFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.ConstructSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.ConstructSynonymSlotAnnotation;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Schema(name = "construct", description = "Construct: a construct")
@ToString(exclude = {"constructGenomicEntityAssociations", "alleleConstructAssociations", "constructComponents", "constructSymbol", "constructFullName", "constructSynonyms"}, callSuper = true)
@AGRCurationSchemaVersion(min = "2.1.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { Reagent.class })

public class Construct extends Reagent {

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToOne(mappedBy = "singleConstruct", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ CurationView.FieldsOnly.class, CurationView.TransgenicAllelesDocument.class, CurationView.AlleleSummaryDocument.class, CurationView.ForPublic.class })
	private ConstructSymbolSlotAnnotation constructSymbol;

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToOne(mappedBy = "singleConstruct", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ CurationView.FieldsOnly.class })
	private ConstructFullNameSlotAnnotation constructFullName;

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToMany(mappedBy = "singleConstruct", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.ConstructView.class })
	private List<ConstructSynonymSlotAnnotation> constructSynonyms;

	@IndexedEmbedded(
		includePaths = {
			"primaryCrossReferenceCurie", "crossReferences.referencedCurie", "crossReferences.displayName", "curie", "primaryCrossReferenceCurie_keyword",
			"crossReferences.referencedCurie_keyword", "crossReferences.displayName_keyword", "curie_keyword"
		}
	)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.ConstructView.class })
	@JoinTable(indexes = {
		@Index(name = "construct_reference_construct_index", columnList = "construct_id"),
		@Index(name = "construct_reference_references_index", columnList = "references_id")
	})
	private List<Reference> references;

	@IndexedEmbedded(includePaths = { "relation.name", "relation.name_keyword", "componentSymbol", "taxon.curie", "taxonText", "componentSymbol_keyword", "taxon.curie_keyword", "taxonText_keyword"})
	@OneToMany(mappedBy = "singleConstruct", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.ConstructView.class, CurationView.TransgenicAllelesDocument.class })
	private List<ConstructComponentSlotAnnotation> constructComponents;

	@IndexedEmbedded(includePaths = {
		"constructGenomicEntityAssociationObject.curie", "constructGenomicEntityAssociationObject.primaryExternalId", "constructGenomicEntityAssociationObject.modInternalId",
		"constructGenomicEntityAssociationObject.name", "constructGenomicEntityAssociationObject.symbol", "relation.name", "constructGenomicEntityAssociationObject.curie_keyword",
		"constructGenomicEntityAssociationObject.primaryExternalId_keyword", "constructGenomicEntityAssociationObject.modInternalId_keyword", "constructGenomicEntityAssociationObject.name_keyword",
		"constructGenomicEntityAssociationObject.symbol_keyword", "relation.name_keyword"
	})
	@OneToMany(mappedBy = "constructAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.ConstructView.class, CurationView.TransgenicAllelesDocument.class })
	private List<ConstructGenomicEntityAssociation> constructGenomicEntityAssociations;
	
	@OneToMany(mappedBy = "alleleConstructAssociationObject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.ConstructDetailView.class })
	private List<AlleleConstructAssociation> alleleConstructAssociations;
}
