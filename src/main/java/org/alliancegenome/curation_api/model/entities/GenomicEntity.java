package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.CurationView;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "GenomicEntity", description = "GenomicEntity: a genomic entity")
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"crossReferences"}, callSuper = true)
@AGRCurationSchemaVersion(min = "1.5.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = {BiologicalEntity.class})
public class GenomicEntity extends BiologicalEntity {

	@IndexedEmbedded(includePaths = {"referencedCurie", "displayName", "resourceDescriptorPage.name", "referencedCurie_keyword", "displayName_keyword", "resourceDescriptorPage.name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(indexes = {
		@Index(columnList = "genomicentity_id, crossreferences_id", name = "genomicentity_crossreference_ge_xref_index"),
		@Index(columnList = "genomicentity_id", name = "genomicentity_crossreference_genomicentity_index"),
		@Index(columnList = "crossreferences_id", name = "genomicentity_crossreference_crossreferences_index")
	})
	@EqualsAndHashCode.Include
	@JsonView({CurationView.FieldsAndLists.class, CurationView.AlleleView.class, CurationView.GeneView.class, CurationView.AffectedGenomicModelView.class, CurationView.VariantView.class, CurationView.GeneSummaryDocument.class, CurationView.VariantDocument.class})
	private List<CrossReference> crossReferences;
}
