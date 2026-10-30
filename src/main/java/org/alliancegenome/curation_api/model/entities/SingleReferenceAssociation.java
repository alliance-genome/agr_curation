package org.alliancegenome.curation_api.model.entities;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;

@MappedSuperclass
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Schema(name = "singleReferenceAssociation", description = "SingleReferenceAssociation: a single reference association")
@AGRCurationSchemaVersion(min = "2.10.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { Association.class })
public class SingleReferenceAssociation extends Association {

	@IndexedEmbedded(includePaths = {
		"id", "curie", "curie_keyword",
		"primaryCrossReferenceCurie", "primaryCrossReferenceCurie_keyword", "crossReferenceCuries", "crossReferenceCuries_keyword",
		"createdBy.uniqueId", "createdBy.uniqueId_keyword", "updatedBy.uniqueId", "updatedBy.uniqueId_keyword",
		"dateCreated", "dateCreated_keyword", "dateUpdated", "dateUpdated_keyword", "dbDateCreated", "dbDateCreated_keyword",
		"dbDateUpdated", "dbDateUpdated_keyword", "internal", "internal_keyword", "obsolete", "obsolete_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ CurationView.FieldsOnly.class, CurationView.ForPublic.class, CurationView.GeneExpressionDocument.class })
	private Reference evidenceItem;

}
