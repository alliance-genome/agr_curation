package org.alliancegenome.curation_api.model.entities.orthology;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.base.GeneratedAuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
@ToString(callSuper = true)
@Schema(name = "GeneToGeneOrthology", description = "POJO that represents orthology between two genes")
@AGRCurationSchemaVersion(min = "1.7.4", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
@Table(indexes = {
	@Index(name = "genetogeneorthology_createdby_index", columnList = "createdBy_id"),
	@Index(name = "genetogeneorthology_updatedby_index", columnList = "updatedBy_id"),
	@Index(name = "genetogeneorthology_subjectgene_index", columnList = "subjectgene_curie"),
	@Index(name = "genetogeneorthology_objectgene_index", columnList = "objectgene_curie")
})
public class GeneToGeneOrthology extends GeneratedAuditedObject {

	@IndexedEmbedded(includePaths = {"geneSymbol.displayText", "geneSymbol.formatText", "geneSymbol.displayText_keyword", "geneSymbol.formatText_keyword", "curie", "curie_keyword", "taxon.curie", "taxon.name", "taxon.curie_keyword", "taxon.name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private Gene subjectGene;
	
	@IndexedEmbedded(includePaths = {"geneSymbol.displayText", "geneSymbol.formatText", "geneSymbol.displayText_keyword", "geneSymbol.formatText_keyword", "curie", "curie_keyword", "taxon.curie", "taxon.name", "taxon.curie_keyword", "taxon.name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private Gene objectGene;

}
