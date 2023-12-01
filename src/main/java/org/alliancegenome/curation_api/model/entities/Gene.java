package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotation;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = { "geneDiseaseAnnotations", "geneSymbol", "geneFullName", "geneSystematicName", "geneSynonyms", "geneSecondaryIds", "alleleGeneAssociations" }, callSuper = true)
@Schema(name = "Gene", description = "POJO that represents the Gene")
@AGRCurationSchemaVersion(min = "1.5.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { GenomicEntity.class }, partial = true)
@Table(indexes = { @Index(name = "gene_genetype_index", columnList = "geneType_curie"), })
public class Gene extends GenomicEntity {

	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private SOTerm geneType;

	@OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<GeneDiseaseAnnotation> geneDiseaseAnnotations;

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToOne(mappedBy = "singleGene", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ View.FieldsOnly.class })
	private GeneSymbolSlotAnnotation geneSymbol;

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToOne(mappedBy = "singleGene", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ View.FieldsOnly.class })
	private GeneFullNameSlotAnnotation geneFullName;

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToOne(mappedBy = "singleGene", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ View.FieldsOnly.class })
	private GeneSystematicNameSlotAnnotation geneSystematicName;

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToMany(mappedBy = "singleGene", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ View.FieldsAndLists.class, View.GeneView.class })
	private List<GeneSynonymSlotAnnotation> geneSynonyms;
	
	@IndexedEmbedded(includePaths = { "secondaryId", "evidence.curie", "secondaryId_keyword", "evidence.curie_keyword"})
	@OneToMany(mappedBy = "singleGene", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ View.FieldsAndLists.class, View.GeneView.class })
	private List<GeneSecondaryIdSlotAnnotation> geneSecondaryIds;
	
	@IndexedEmbedded(includePaths = {"subject.curie", "subject.alleleSymbol.displayText", "subject.alleleSymbol.formatText", "subject.alleleFullName.displayText", "subject.alleleFullName.formatText",
			"subject.curie_keyword", "subject.alleleSymbol.displayText_keyword", "subject.alleleSymbol.formatText_keyword", "subject.alleleFullName.displayText_keyword", "subject.alleleFullName.formatText_keyword"})
	@OneToMany(mappedBy = "object", cascade = CascadeType.ALL)
	@JsonView({ View.FieldsAndLists.class, View.GeneDetailView.class })
	private List<AlleleGeneAssociation> alleleGeneAssociations;
}
