package org.alliancegenome.curation_api.model.entities.ontology;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.CurationView;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Indexed
@Schema(name = "SOTerm", description = "SOTerm: a SO term")
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = LinkMLSchemaConstants.MIN_ONTOLOGY_RELEASE, max = LinkMLSchemaConstants.MAX_ONTOLOGY_RELEASE, dependencies = { OntologyTerm.class })
public class SOTerm extends OntologyTerm {

	@GenericField(projectable = Projectable.YES, sortable = Sortable.YES)
	@JsonView({ CurationView.FieldsOnly.class, CurationView.AlleleSummaryDocument.class, CurationView.VariantSummaryDocument.class, CurationView.AlleleDetailView.class })
	private Integer severityOrder;

}
