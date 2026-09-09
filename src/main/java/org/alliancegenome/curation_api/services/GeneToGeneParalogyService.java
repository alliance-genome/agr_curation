package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.GeneToGeneParalogyDAO;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.GeneToGeneParalogy;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ParalogyFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.ParalogyFmsDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneToGeneParalogyService extends BaseEntityCrudService<GeneToGeneParalogy, GeneToGeneParalogyDAO> implements BaseUpsertServiceInterface<GeneToGeneParalogy, ParalogyFmsDTO> {

	@Inject GeneToGeneParalogyDAO geneToGeneParalogyDAO;
	@Inject ParalogyFmsDTOValidator paralogyFmsDtoValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneToGeneParalogyDAO);
	}

	@Override
	public ObjectResponse<GeneToGeneParalogy> upsert(ParalogyFmsDTO paralogyData, Species species) throws ValidationException {
		return paralogyFmsDtoValidator.validateParalogyFmsDTO(paralogyData);
	}

	public List<Long> getAllParalogyPairIdsBySubjectGeneDataProvider(Species species) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.SUBJECT_GENE_DATA_PROVIDER, species.getDataProvider().getAbbreviation());

		if (StringUtils.equals(species.getDataProvider().getAbbreviation(), "RGD") || StringUtils.equals(species.getDataProvider().getAbbreviation(), "XB")) {
			params.put(EntityFieldConstants.SUBJECT_GENE_TAXON, species.getTaxon().getCurie());
		}

		List<Long> annotationIds = geneToGeneParalogyDAO.findIdsByParams(params);
		return annotationIds;
	}

}
