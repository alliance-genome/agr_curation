package org.alliancegenome.curation_api.model.entities.ontology;

import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.view.CurationView;
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
@ToString(callSuper = true, exclude = {"species"})
@AGRCurationSchemaVersion(min = LinkMLSchemaConstants.MIN_ONTOLOGY_RELEASE, max = LinkMLSchemaConstants.MAX_ONTOLOGY_RELEASE, dependencies = {OntologyTerm.class})
public class NCBITaxonTerm extends OntologyTerm {

	@OneToMany(mappedBy = "taxon", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView(CurationView.GeneSummaryDocument.class)
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

	@Transient
	private static final Map<String, Integer> phylogeneticOrder = Map.of(
			"NCBITaxon:9606", 0,
			"NCBITaxon:10116", 100,
			"NCBITaxon:10090", 200,
			"NCBITaxon:8355", 300,
			"NCBITaxon:8364", 400,
			"NCBITaxon:7955", 500,
			"NCBITaxon:7227", 600,
			"NCBITaxon:6239", 700,
			"NCBITaxon:559292", 800,
			"NCBITaxon:2697049", 900
	);

	@Transient
	public int getPhylogeneticSortOrder() {
		if (phylogeneticOrder.get(curie) != null) {
			return phylogeneticOrder.get(curie);
		} else {
			return 10000;
		}
	}
}
