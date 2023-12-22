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

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonView;

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
	@JsonView({ View.FieldsAndLists.class, View.ForPublic.class })
	private List<CrossReference> crossReferences;
	
	@IndexedEmbedded(includePaths = {"subject.curie", "subject.constructSymbol.displayText", "subject.constructSymbol.formatText",
			"subject.constructFullName.displayText", "subject.constructFullName.formatText", "subject.modEntityId",
			"subject.curie_keyword", "subject.constructSymbol.displayText_keyword", "subject.constructSymbol.formatText_keyword",
			"subject.constructFullName.displayText_keyword", "subject.constructFullName.formatText_keyword", "subject.modEntityId_keyword",})
	@OneToMany(mappedBy = "object", cascade = CascadeType.ALL)
	@JsonView({ View.FieldsAndLists.class, View.GeneDetailView.class })
	private List<ConstructGenomicEntityAssociation> constructGenomicEntityAssociations;

}
