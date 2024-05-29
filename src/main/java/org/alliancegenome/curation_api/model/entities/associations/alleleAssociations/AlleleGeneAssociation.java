package org.alliancegenome.curation_api.model.entities.associations.alleleAssociations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@AGRCurationSchemaVersion(min = "2.2.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AlleleGenomicEntityAssociation.class })
@Schema(name = "AlleleGeneAssociation", description = "POJO representing an association between an allele and a gene")
@Table(
	indexes = {
		@Index(name = "allelegeneassociation_alleleassociationsubject_index", columnList = "alleleassociationsubject_id"),
		@Index(name = "allelegeneassociation_allelegeneassociationobject_index", columnList = "allelegeneassociationobject_id")
	}
)
public class AlleleGeneAssociation extends AlleleGenomicEntityAssociation {

	@IndexedEmbedded(includePaths = {
		"curie", "alleleSymbol.displayText", "alleleSymbol.formatText", "alleleFullName.displayText", "alleleFullName.formatText",
		"curie_keyword", "alleleSymbol.displayText_keyword", "alleleSymbol.formatText_keyword", "alleleFullName.displayText_keyword",
		"alleleFullName.formatText_keyword", "modEntityId", "modEntityId_keyword", "modInternalId", "modInternalId_keyword" })
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@JsonIgnoreProperties("alleleGeneAssociations")
	@Fetch(FetchMode.JOIN)
	private Allele alleleAssociationSubject;

	@IndexedEmbedded(includePaths = { "curie", "geneSymbol.displayText", "geneSymbol.formatText", "geneFullName.displayText",
		"geneFullName.formatText", "curie_keyword", "geneSymbol.displayText_keyword", "geneSymbol.formatText_keyword",
		"geneFullName.displayText_keyword", "geneFullName.formatText_keyword", "modEntityId", "modEntityId_keyword",
		"modInternalId", "modInternalId_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class, View.AlleleView.class })
	@JsonIgnoreProperties({ "alleleGeneAssociations", "constructGenomicEntityAssociations" })
	private Gene alleleGeneAssociationObject;
}
