package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotation;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

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
@Inheritance(strategy = InheritanceType.JOINED)
@Schema(name = "construct", description = "POJO that represents a construct")
@ToString(exclude = {"constructComponents", "constructSymbol", "constructFullName", "constructSynonyms"}, callSuper = true)
@AGRCurationSchemaVersion(min = "1.10.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { Reagent.class })

public class Construct extends Reagent {

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToOne(mappedBy = "singleConstruct", cascade = CascadeType.ALL)
	@JsonManagedReference
	@JsonView({ View.FieldsOnly.class })
	private ConstructSymbolSlotAnnotation constructSymbol;

	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToOne(mappedBy = "singleConstruct", cascade = CascadeType.ALL)
	@JsonManagedReference
	@JsonView({ View.FieldsOnly.class })
	private ConstructFullNameSlotAnnotation constructFullName;
	
	@IndexedEmbedded(includePaths = { "displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword", "formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"})
	@OneToMany(mappedBy = "singleConstruct", cascade = CascadeType.ALL)
	@JsonManagedReference
	@JsonView({ View.FieldsAndLists.class, View.ConstructView.class })
	private List<ConstructSynonymSlotAnnotation> constructSynonyms;

	@IndexedEmbedded(includeDepth = 2)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ View.FieldsAndLists.class, View.ConstructView.class })
	@JoinTable(indexes = {
		@Index(name = "construct_reference_construct_id_index", columnList = "construct_id"),
		@Index(name = "construct_reference_references_curie_index", columnList = "references_curie")
	})
	private List<Reference> references;
	
	@IndexedEmbedded(includePaths = { "componentSymbol", "taxon.curie", "taxonText", "componentSymbol_keyword", "taxon.curie_keyword", "taxonText_keyword"})
	@OneToMany(mappedBy = "singleConstruct", cascade = CascadeType.ALL)
	@JsonManagedReference
	@JsonView({ View.FieldsAndLists.class, View.ConstructView.class })
	private List<ConstructComponentSlotAnnotation> constructComponents;
	
	@IndexedEmbedded(includeDepth = 2)
	@OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
	@JsonView({ View.FieldsAndLists.class, View.AlleleDetailView.class })
	private List<ConstructGenomicEntityAssociation> constructGenomicEntityAssociations;
}
