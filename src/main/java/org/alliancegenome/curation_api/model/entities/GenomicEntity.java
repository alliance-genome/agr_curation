package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = { "crossReferences", "constructGenomicEntityAssociations" }, callSuper = true)
@AGRCurationSchemaVersion(min = "1.5.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { BiologicalEntity.class })
public class GenomicEntity extends BiologicalEntity {

	@IndexedEmbedded(includePaths = {"referencedCurie", "displayName", "referencedCurie_keyword", "displayName_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JoinTable(indexes = { 
		@Index(columnList = "genomicentity_curie, crossreferences_id", name = "genomicentity_crossreference_ge_curie_xref_id_index"),
		@Index(columnList = "genomicentity_curie", name = "genomicentity_crossreference_genomicentity_curie_index"),
		@Index(columnList = "crossreferences_id", name = "genomicentity_crossreference_crossreferences_id_index")
	})
	@JsonView({ View.FieldsAndLists.class })
	private List<CrossReference> crossReferences;
	
	@IndexedEmbedded(includePaths = {"constructSubject.curie", "constructSubject.constructSymbol.displayText", "constructSubject.constructSymbol.formatText",
			"constructSubject.constructFullName.displayText", "constructSubject.constructFullName.formatText", "constructSubject.modEntityId",
			"constructSubject.curie_keyword", "constructSubject.constructSymbol.displayText_keyword", "constructSubject.constructSymbol.formatText_keyword",
			"constructSubject.constructFullName.displayText_keyword", "constructSubject.constructFullName.formatText_keyword", "constructSubject.modEntityId_keyword",})
	@OneToMany(mappedBy = "objectGenomicEntity", cascade = CascadeType.ALL)
	@JsonView({ View.FieldsAndLists.class, View.GeneDetailView.class })
	private List<ConstructGenomicEntityAssociation> constructGenomicEntityAssociations;

}
