package org.alliancegenome.curation_api.model.entities;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.ontology.MMOTerm;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Schema(name = "Gene_Expression_Annotation", description = "Annotation class representing a gene expression annotation")
@JsonTypeName("GeneExpressionAnnotation")
@AGRCurationSchemaVersion(min = "2.2.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { ExpressionAnnotation.class })
@Table(indexes = {
	@Index(name = "geneexpressionannotation_expression_annotation_subject_index", columnList = "expressionannotationsubject_id"),
	@Index(name = "geneexpressionannotation_expression_assay_used_index", columnList = "expressionassayused_id")
})
public class GeneExpressionAnnotation extends ExpressionAnnotation {

	@IndexedEmbedded(includePaths = {"geneSymbol.displayText", "geneSymbol.formatText", "geneSymbol.displayText_keyword", "geneSymbol.formatText_keyword", "curie", "curie_keyword", "taxon.curie", "taxon.name", "taxon.curie_keyword", "taxon.name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class })
	private Gene expressionAnnotationSubject;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private MMOTerm expressionAssayUsed;

}
