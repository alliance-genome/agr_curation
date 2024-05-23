package org.alliancegenome.curation_api.services.validation.dto.fms;

import org.alliancegenome.curation_api.dao.GeneToGeneParalogyDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.GeneToGeneParalogy;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ParalogyFmsDTO;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class ParalogyFmsDTOValidator {

	@Inject
	GeneToGeneParalogyDAO genetoGeneParalogyDAO;
	@Inject
	GeneService geneService;
	@Inject
	NcbiTaxonTermService ncbiTaxonTermService;

	@Transactional
	public GeneToGeneParalogy validateParalogyFmsDTO(ParalogyFmsDTO dto) throws ObjectValidationException {

		// ObjectResponse<GeneToGeneParalogy> paralogyResponse = new
		// ObjectResponse<GeneToGeneParalogy>();

		// String subjectGeneIdentifier = null;
		// String objectGeneIdentifier = null;

		// GeneToGeneParalogy paralogyData = null;

		// if (StringUtils.isBlank(dto.getGene1())) {
		// paralogyResponse.addErrorMessage("gene1",
		// ValidationConstants.REQUIRED_MESSAGE);
		// } else {
		// subjectGeneIdentifier = convertToModCurie(dto.getGene1(), dto.getSpecies());
		// }
		// if (StringUtils.isBlank(dto.getGene2())) {
		// paralogyResponse.addErrorMessage("gene2",
		// ValidationConstants.REQUIRED_MESSAGE);
		// } else {
		// objectGeneIdentifier = convertToModCurie(dto.getGene2(), dto.getSpecies());
		// }

		// Gene subjectGene = null;
		// if (StringUtils.isNotBlank(dto.getGene1())) {
		// subjectGene = geneService.findByIdentifierString(subjectGeneIdentifier);
		// if (subjectGene == null) {
		// paralogyResponse.addErrorMessage("gene1", ValidationConstants.INVALID_MESSAGE
		// + " (" + subjectGeneIdentifier + ")");
		// } else {
		// if (dto.getSpecies() == null) {
		// paralogyResponse.addErrorMessage("Species",
		// ValidationConstants.REQUIRED_MESSAGE);
		// } else {
		// ObjectResponse<NCBITaxonTerm> taxonResponse =
		// ncbiTaxonTermService.getByCurie("NCBITaxon:" + dto.getSpecies());
		// NCBITaxonTerm subjectTaxon = taxonResponse.getEntity();
		// if (subjectTaxon == null) {
		// paralogyResponse.addErrorMessage("Species",
		// ValidationConstants.INVALID_MESSAGE + " (" + dto.getSpecies() + ")");
		// } else if (!sameGenus(subjectTaxon, subjectGene.getTaxon())) {
		// paralogyResponse.addErrorMessage("Species",
		// ValidationConstants.INVALID_MESSAGE + " (" + dto.getSpecies() + ") for gene "
		// + subjectGene.getCurie());
		// }
		// }
		// }
		// }
		return null;
	}

	private String convertToModCurie(String curie, Integer taxonId) {
		curie = curie.replaceFirst("^DRSC:", "");
		if (curie.indexOf(":") == -1) {
			String prefix = BackendBulkDataProvider.getCuriePrefixFromTaxonId(taxonId);
			if (prefix != null)
				curie = prefix + curie;
		}

		return curie;
	}

	private boolean sameGenus(NCBITaxonTerm taxon, NCBITaxonTerm geneTaxon) {
		if (StringUtils.equals(taxon.getCurie(), "NCBITaxon:8355")
				|| StringUtils.equals(taxon.getCurie(), "NCBITaxon:8364")) {
			// Must be same species for Xenopus as cleanup uses taxon curie
			if (StringUtils.equals(taxon.getCurie(), geneTaxon.getCurie()))
				return true;
			return false;
		}
		String genus = taxon.getName().substring(0, taxon.getName().indexOf(" "));
		String geneGenus = geneTaxon.getName().substring(0, geneTaxon.getName().indexOf(" "));
		if (StringUtils.equals(genus, geneGenus))
			return true;
		return false;
	}

}
