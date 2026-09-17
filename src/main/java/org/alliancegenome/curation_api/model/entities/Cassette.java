package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.associations.CassetteGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.entities.associations.CassetteStrAssociation;
import org.alliancegenome.curation_api.model.entities.associations.CassetteTransgenicToolAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteComponentSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteUseSlotAnnotation;
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
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * SCRUM-6535: a Cassette, the second entity of the new Constructs model (epic SCRUM-6382).
 *
 * A Cassette is a segment of a Construct. Its components come in two forms that sit side by side:
 * the three association lists below, for components that are themselves curated entities with a
 * curie, and cassetteComponents, for components named only by symbol. LinkML requires at least one
 * of cassetteGenomicEntityAssociations or cassetteComponents to be populated.
 *
 * No {@code @CurieSubdomain}: MaTI has no subdomain for cassettes, so AGRKB minting does not apply
 * to this class, as it does not to {@link TransgenicTool}.
 */
@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Schema(name = "cassette", description = "Cassette: a segment of a construct")
@ToString(exclude = {"cassetteSymbol", "cassetteFullName", "cassetteSynonyms", "cassetteComponents", "cassetteUses", "cassetteGenomicEntityAssociations", "cassetteTransgenicToolAssociations", "cassetteStrAssociations"}, callSuper = true)
@AGRCurationSchemaVersion(min = "2.18.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { Reagent.class })
public class Cassette extends Reagent {

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToOne(mappedBy = "singleCassette", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ CurationView.FieldsOnly.class })
	private CassetteSymbolSlotAnnotation cassetteSymbol;

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToOne(mappedBy = "singleCassette", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ CurationView.FieldsOnly.class })
	private CassetteFullNameSlotAnnotation cassetteFullName;

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToMany(mappedBy = "singleCassette", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.CassetteView.class })
	private List<CassetteSynonymSlotAnnotation> cassetteSynonyms;

	@IndexedEmbedded(
		includePaths = {
			"primaryCrossReferenceCurie", "crossReferences.referencedCurie", "crossReferences.displayName", "curie", "primaryCrossReferenceCurie_keyword",
			"crossReferences.referencedCurie_keyword", "crossReferences.displayName_keyword", "curie_keyword"
		}
	)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.CassetteView.class })
	@JoinTable(indexes = {
		@Index(name = "cassette_reference_cassette_index", columnList = "cassette_id"),
		@Index(name = "cassette_reference_references_index", columnList = "references_id")
	})
	private List<Reference> references;

	@IndexedEmbedded(includePaths = { "relation.name", "relation.name_keyword", "componentSymbol", "taxon.curie", "taxonText", "componentSymbol_keyword", "taxon.curie_keyword", "taxonText_keyword"})
	@OneToMany(mappedBy = "singleCassette", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.CassetteView.class })
	private List<CassetteComponentSlotAnnotation> cassetteComponents;

	@IndexedEmbedded(includePaths = { "uses.curie", "uses.name", "uses.curie_keyword", "uses.name_keyword" })
	@OneToMany(mappedBy = "singleCassette", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.CassetteView.class })
	private List<CassetteUseSlotAnnotation> cassetteUses;

	@IndexedEmbedded(includePaths = {
		"cassetteGenomicEntityAssociationObject.curie", "cassetteGenomicEntityAssociationObject.primaryExternalId", "cassetteGenomicEntityAssociationObject.modInternalId",
		"cassetteGenomicEntityAssociationObject.name", "cassetteGenomicEntityAssociationObject.symbol", "relation.name", "cassetteGenomicEntityAssociationObject.curie_keyword",
		"cassetteGenomicEntityAssociationObject.primaryExternalId_keyword", "cassetteGenomicEntityAssociationObject.modInternalId_keyword", "cassetteGenomicEntityAssociationObject.name_keyword",
		"cassetteGenomicEntityAssociationObject.symbol_keyword", "relation.name_keyword"
	})
	@OneToMany(mappedBy = "cassetteAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.CassetteView.class })
	private List<CassetteGenomicEntityAssociation> cassetteGenomicEntityAssociations;

	@IndexedEmbedded(includePaths = {
		"cassetteTransgenicToolAssociationObject.curie", "cassetteTransgenicToolAssociationObject.primaryExternalId", "cassetteTransgenicToolAssociationObject.modInternalId",
		"relation.name", "cassetteTransgenicToolAssociationObject.curie_keyword", "cassetteTransgenicToolAssociationObject.primaryExternalId_keyword",
		"cassetteTransgenicToolAssociationObject.modInternalId_keyword", "relation.name_keyword"
	})
	@OneToMany(mappedBy = "cassetteAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.CassetteView.class })
	private List<CassetteTransgenicToolAssociation> cassetteTransgenicToolAssociations;

	@IndexedEmbedded(includePaths = {
		"cassetteStrAssociationObject.curie", "cassetteStrAssociationObject.primaryExternalId", "cassetteStrAssociationObject.modInternalId",
		"relation.name", "cassetteStrAssociationObject.curie_keyword", "cassetteStrAssociationObject.primaryExternalId_keyword",
		"cassetteStrAssociationObject.modInternalId_keyword", "relation.name_keyword"
	})
	@OneToMany(mappedBy = "cassetteAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.CassetteView.class })
	private List<CassetteStrAssociation> cassetteStrAssociations;
}
