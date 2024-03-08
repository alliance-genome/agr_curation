package org.alliancegenome.curation_api.model.entities.associations.geneAssociations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.EvidenceAssociation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "2.2.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { EvidenceAssociation.class })
@Schema(name = "GeneGeneAssociation", description = "POJO representing an association between a gene and a gene")
@Table(indexes = {
	@Index(name = "genegeneassociation_geneassociationsubject_index", columnList = "geneassociationsubject_id"),
	@Index(name = "genegeneassociation_relation_index", columnList = "relation_id"),
	@Index(name = "genegeneassociation_genegeneassociationobject_index", columnList = "genegeneassociationobject_id")
})
public class GeneGeneAssociation extends EvidenceAssociation {

	@IndexedEmbedded(includePaths = {"curie", "geneSymbol.displayText", "geneSymbol.formatText", "geneFullName.displayText", "geneFullName.formatText",
			"curie_keyword", "geneSymbol.displayText_keyword", "geneSymbol.formatText_keyword", "geneFullName.displayText_keyword", "geneFullName.formatText_keyword",
			"modEntityId", "modEntityId_keyword", "modInternalId", "modInternalId_keyword"})
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private Gene geneAssociationSubject;
	
	@IndexedEmbedded(includePaths = {"curie", "geneSymbol.displayText", "geneSymbol.formatText", "geneFullName.displayText", "geneFullName.formatText",
			"curie_keyword", "geneSymbol.displayText_keyword", "geneSymbol.formatText_keyword", "geneFullName.displayText_keyword", "geneFullName.formatText_keyword",
			"modEntityId", "modEntityId_keyword", "modInternalId", "modInternalId_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private Gene geneGeneAssociationObject;
	
	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private VocabularyTerm relation;
}
