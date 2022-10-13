package org.alliancegenome.curation_api.services.validation.dto;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class GeneDTOValidator extends BaseDTOValidator {

	@Inject GeneDAO geneDAO;
	
	public Gene validateGeneDTO (GeneDTO dto) throws ObjectValidationException {
		
		ObjectResponse<Gene> geneResponse = new ObjectResponse<Gene>();
		
		Gene gene = null;
		if (StringUtils.isBlank(dto.getCurie())) {
			geneResponse.addErrorMessage("curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			gene = geneDAO.find(dto.getCurie());
		}
		
		if (gene == null)
			gene = new Gene();
		
		gene.setCurie(dto.getCurie());

		ObjectResponse<Gene> geResponse = validateGenomicEntityDTO(gene, dto);
		geneResponse.addErrorMessages(geResponse.getErrorMessages());
		gene = geResponse.getEntity();
		
		String symbol = dto.getSymbol();
		if (StringUtils.isBlank(symbol)) 
			geneResponse.addErrorMessage("symbol", ValidationConstants.REQUIRED_MESSAGE);
		gene.setSymbol(symbol);
		
		if (geneResponse.hasErrors()) {
			throw new ObjectValidationException(dto, geneResponse.errorMessagesString());
		}
		
		return gene;
	}
}
