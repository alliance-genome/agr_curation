package org.alliancegenome.curation_api.model.entities.orthology;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
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
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(name = "GeneToGeneOrthologyCurated", description = "POJO that represents curated orthology between two genes")
@AGRCurationSchemaVersion(min = "1.7.4", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class, GeneToGeneOrthology.class })
@Table(indexes = {
	@Index(name = "genetogeneorthologycurated_singlereference_index", columnList = "singlereference_curie"),
	@Index(name = "genetogeneorthologycurated_evidencecode_index", columnList = "evidencecode_curie")
})
public class GeneToGeneOrthologyCurated extends GeneToGeneOrthology {

	@IndexedEmbedded(includePaths = {"curie", "crossReferences.referencedCurie", "crossReferences.displayName", "curie_keyword", "crossReferences.referencedCurie_keyword", "crossReferences.displayName_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private Reference singleReference;
	
	@IndexedEmbedded(includePaths = {"curie", "name", "abbreviation", "curie_keyword", "name_keyword", "abbreviation_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private ECOTerm evidenceCode;
}
