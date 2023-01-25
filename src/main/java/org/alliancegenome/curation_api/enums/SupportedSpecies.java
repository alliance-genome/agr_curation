package org.alliancegenome.curation_api.enums;

import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurie;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.FlyDiseaseAnnotationCurie;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.MGIDiseaseAnnotationCurie;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.RGDDiseaseAnnotationCurie;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.SGDDiseaseAnnotationCurie;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.WormDiseaseAnnotationCurie;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.ZFINDiseaseAnnotationCurie;

public enum SupportedSpecies {
	RATTUS_NORVEGICUS(new RGDDiseaseAnnotationCurie(), "NCBITaxon:10116"),
	MUS_MUSCULUS(new MGIDiseaseAnnotationCurie(), "NCBITaxon:10090"),
	SACCHAROMYCES_CEREVISIAE(new SGDDiseaseAnnotationCurie(), "NCBITaxon:559292"),
	HOMO_SAPIENS(new RGDDiseaseAnnotationCurie(), "NCBITaxon:9606"),
	DANIO_RERIO(new ZFINDiseaseAnnotationCurie(), "NCBITaxon:7955"),
	DROSOPHILA_MELANOGASTER(new FlyDiseaseAnnotationCurie(), "NCBITaxon:7227"),
	CAENORHABDITIS_ELEGANS(new WormDiseaseAnnotationCurie(), "NCBITaxon:6239");

	public DiseaseAnnotationCurie annotationCurie;
	public final String canonicalTaxon;

	private SupportedSpecies(DiseaseAnnotationCurie annotationCurie, String canonicalTaxon) {
		this.annotationCurie = annotationCurie;
		this.canonicalTaxon = canonicalTaxon;
	}

	public static DiseaseAnnotationCurie getAnnotationCurie(String speciesName) {
		SupportedSpecies species = getSpeciesFromName(speciesName);
		if (species == null)
			return null;
		return species.annotationCurie;
	}
	
	public static Boolean isSupported(String speciesName) {
		SupportedSpecies species = getSpeciesFromName(speciesName);
		if (species == null)
			return false;
		return true;
	}
	
	private static SupportedSpecies getSpeciesFromName(String speciesName) {
		speciesName = speciesName.replace(" ", "_");
		SupportedSpecies result = null;
		for (SupportedSpecies species : values()) {
			if (species.name().equalsIgnoreCase(speciesName)) {
				result = species;
				break;
			}
		}
		return result;
	}
}