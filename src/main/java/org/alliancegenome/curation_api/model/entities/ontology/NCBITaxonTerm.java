package org.alliancegenome.curation_api.model.entities.ontology;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Indexed
@Schema(name = "NCBITaxonTerm", description = "NCBITaxonTerm: a NCBI taxon term")
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true, exclude = {"species"})
@AGRCurationSchemaVersion(min = LinkMLSchemaConstants.MIN_ONTOLOGY_RELEASE, max = LinkMLSchemaConstants.MAX_ONTOLOGY_RELEASE, dependencies = {OntologyTerm.class})
public class NCBITaxonTerm extends OntologyTerm {

	@OneToOne(mappedBy = "taxon", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({CurationView.GeneSummaryDocument.class, CurationView.AlleleSummaryDocument.class})
	private Species species;

}
