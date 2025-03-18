package org.alliancegenome.curation_api.model.entities.ontology;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = LinkMLSchemaConstants.MIN_ONTOLOGY_RELEASE, max = LinkMLSchemaConstants.MAX_ONTOLOGY_RELEASE, dependencies = { OntologyTerm.class })
public class NCBITaxonTerm extends OntologyTerm {

	@OneToMany(mappedBy = "taxon", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView(View.GeneSummaryDocument.class)
	private List<Species> species;
	
	@Transient
	@JsonIgnore
	public String getGenusSpecies() {
		if (name == null) {
			return null;
		}
		String[] nameParts = name.split(" ");
		return nameParts[0] + " " + nameParts[1];
	}

}
