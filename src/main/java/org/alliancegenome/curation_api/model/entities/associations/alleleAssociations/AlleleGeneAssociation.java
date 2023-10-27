package org.alliancegenome.curation_api.model.entities.associations.alleleAssociations;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "1.9.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AlleleGenomicEntityAssociation.class })
@Schema(name = "AlleleGeneAssociation", description = "POJO representing an association between an allele and a gene")
@Table(indexes = {
		@Index(name = "allelegeneassociation_subject_index", columnList = "subject_curie"),
		@Index(name = "allelegeneassociation_object_curie_index", columnList = "object_curie")
		})
public class AlleleGeneAssociation extends AlleleGenomicEntityAssociation {

	@IndexedEmbedded(includePaths = {"curie", "alleleSymbol.displayText", "alleleSymbol.formatText", "alleleFullName.displayText", "alleleFullName.formatText",
			"curie_keyword", "alleleSymbol.displayText_keyword", "alleleSymbol.formatText_keyword", "alleleFullName.displayText_keyword", "alleleFullName.formatText_keyword"})
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@JsonIgnoreProperties("alleleGeneAssociations")
	@Fetch(FetchMode.JOIN)
	private Allele subject;
	
	@IndexedEmbedded(includePaths = {"curie", "geneSymbol.displayText", "geneSymbol.formatText", "geneFullName.displayText", "geneFullName.formatText",
			"curie_keyword", "geneSymbol.displayText_keyword", "geneSymbol.formatText_keyword", "geneFullName.displayText_keyword", "geneFullName.formatText_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne
	@JsonView({ View.FieldsOnly.class })
	@JsonIgnoreProperties({"alleleGeneAssociations", "constructGenomicEntityAssociations"})
	private Gene object;
}
