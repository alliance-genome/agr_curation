package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.associations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.associations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.entities.associations.GeneGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.entities.associations.SequenceTargetingReagentGeneAssociation;
import org.alliancegenome.curation_api.model.entities.associations.TranscriptGeneAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSystematicNameSlotAnnotation;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {
		"geneDiseaseAnnotations",
		"geneExpressionAnnotations",
		"genePhenotypeAnnotations",
		"allelePhenotypeInferredGeneAnnotations",
		"agmPhenotypeInferredGeneAnnotations",
		"allelePhenotypeAssertedGeneAnnotations",
		"agmPhenotypeAssertedGeneAnnotations",
		"geneOntologyAnnotations",
		"geneToGeneOrthologyGenerateds",
		"geneSymbol",
		"geneFullName",
		"geneSystematicName",
		"geneSynonyms",
		"geneSecondaryIds",
		"geneGenomicLocationAssociations",
		"alleleGeneAssociations",
		"sequenceTargetingReagentGeneAssociations",
		"transcriptGeneAssociations",
		"constructGenomicEntityAssociations" }, callSuper = true)
@Schema(name = "Gene", description = "POJO that represents the Gene")
@AGRCurationSchemaVersion(min = "1.5.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { GenomicEntity.class }, partial = true)
@Table(indexes = {
		@Index(name = "gene_genetype_index", columnList = "geneType_id"),
		@Index(name = "gene_gcrpcrossreference_index", columnList = "gcrpcrossreference_id")
	}
)
public class Gene extends GenomicEntity {

	@GenericField(projectable = Projectable.YES, sortable = Sortable.YES)
	private Double popularity;

	@IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name", "namespace",
			"curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword", "namespace_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ CurationView.FieldsOnly.class, CurationView.GeneSummaryDocument.class })
	private SOTerm geneType;

	@OneToMany(mappedBy = "diseaseAnnotationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<GeneDiseaseAnnotation> geneDiseaseAnnotations;

	@OneToMany(mappedBy = "expressionAnnotationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<GeneExpressionAnnotation> geneExpressionAnnotations;

	// Back references to all classes for ES documents
	@OneToMany(mappedBy = "phenotypeAnnotationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<GenePhenotypeAnnotation> genePhenotypeAnnotations;
	@OneToMany(mappedBy = "inferredGene", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<AllelePhenotypeAnnotation> allelePhenotypeInferredGeneAnnotations;
	@OneToMany(mappedBy = "inferredGene", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<AGMPhenotypeAnnotation> agmPhenotypeInferredGeneAnnotations;
	@ManyToMany(mappedBy = "assertedGenes", cascade = CascadeType.ALL)
	private List<AllelePhenotypeAnnotation> allelePhenotypeAssertedGeneAnnotations;
	@ManyToMany(mappedBy = "assertedGenes", cascade = CascadeType.ALL)
	private List<AGMPhenotypeAnnotation> agmPhenotypeAssertedGeneAnnotations;

	@OneToMany(mappedBy = "singleGene", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<GeneOntologyAnnotation> geneOntologyAnnotations;
	@OneToMany(mappedBy = "subjectGene", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<GeneToGeneOrthologyGenerated> geneToGeneOrthologyGenerateds;

	//@OneToMany(mappedBy = "geneAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	//private List<GeneGeneAssociation> geneGeneAssociations;

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToOne(mappedBy = "singleGene", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ CurationView.FieldsOnly.class, CurationView.ForPublic.class, CurationView.GeneToGeneOrthologyDocument.class, CurationView.GeneSummaryDocument.class, CurationView.ModelDocument.class, CurationView.TransgenicAllelesDocument.class, CurationView.AlleleSummaryDocument.class, CurationView.GeneExpressionDocument.class, CurationView.VariantDocument.class })
	private GeneSymbolSlotAnnotation geneSymbol;

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToOne(mappedBy = "singleGene", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ CurationView.FieldsOnly.class, CurationView.ForPublic.class, CurationView.GeneSummaryDocument.class })
	private GeneFullNameSlotAnnotation geneFullName;

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToOne(mappedBy = "singleGene", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ CurationView.FieldsOnly.class, CurationView.GeneSummaryDocument.class })
	private GeneSystematicNameSlotAnnotation geneSystematicName;

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToMany(mappedBy = "singleGene", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.GeneView.class, CurationView.GeneSummaryDocument.class })
	private List<GeneSynonymSlotAnnotation> geneSynonyms;

	@IndexedEmbedded(includePaths = { "secondaryId", "evidence.curie", "secondaryId_keyword", "evidence.curie_keyword"})
	@OneToMany(mappedBy = "singleGene", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.GeneView.class, CurationView.GeneSummaryDocument.class })
	private List<GeneSecondaryIdSlotAnnotation> geneSecondaryIds;

	@OneToMany(mappedBy = "alleleGeneAssociationObject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.GeneDetailView.class })
	private List<AlleleGeneAssociation> alleleGeneAssociations;

	@OneToMany(mappedBy = "sequenceTargetingReagentGeneAssociationObject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.GeneDetailView.class })
	private List<SequenceTargetingReagentGeneAssociation> sequenceTargetingReagentGeneAssociations;

	@OneToMany(mappedBy = "transcriptGeneAssociationObject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.GeneDetailView.class })
	private List<TranscriptGeneAssociation> transcriptGeneAssociations;

	@IndexedEmbedded(
		includePaths = {
			"geneGenomicLocationAssociationObject.curie", "geneGenomicLocationAssociationObject.curie_keyword",
			"geneGenomicLocationAssociationObject.primaryExternalId", "geneGenomicLocationAssociationObject.primaryExternalId_keyword",
			"geneGenomicLocationAssociationObject.modInternalId", "geneGenomicLocationAssociationObject.modInternalId_keyword",
			"start", "end"
		}
	)
	@OneToMany(mappedBy = "geneAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.GeneDetailView.class, CurationView.GeneSummaryDocument.class, CurationView.VariantDocument.class })
	private List<GeneGenomicLocationAssociation> geneGenomicLocationAssociations;


	//@IndexedEmbedded(includePaths = {
	//	"constructAssociationSubject.curie", "constructAssociationSubject.constructSymbol.displayText", "constructAssociationSubject.constructSymbol.formatText",
	//	"constructAssociationSubject.constructFullName.displayText", "constructAssociationSubject.constructFullName.formatText", "constructAssociationSubject.primaryExternalId",
	//	"constructAssociationSubject.curie_keyword", "constructAssociationSubject.constructSymbol.displayText_keyword", "constructAssociationSubject.constructSymbol.formatText_keyword",
	//	"constructAssociationSubject.constructFullName.displayText_keyword", "constructAssociationSubject.constructFullName.formatText_keyword", "constructAssociationSubject.primaryExternalId_keyword"
	//})
	@OneToMany(mappedBy = "constructGenomicEntityAssociationObject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ CurationView.FieldsAndLists.class, CurationView.GeneDetailView.class })
	private List<ConstructGenomicEntityAssociation> constructGenomicEntityAssociations;

	@IndexedEmbedded(includePaths = {"displayName", "referencedCurie", "displayName_keyword", "referencedCurie_keyword", "resourceDescriptorPage.name", "resourceDescriptorPage.name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({CurationView.FieldsOnly.class})
	private CrossReference gcrpCrossReference;
}
