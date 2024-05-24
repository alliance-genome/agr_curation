package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.List;

import org.alliancegenome.curation_api.dao.SQTRDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.PhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.SQTR;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SQTRFmsDTO;
import org.alliancegenome.curation_api.services.base.BaseDTOCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.SQTRFmsDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

public class SQTRService extends BaseDTOCrudService<SQTR, SQTRFmsDTO, SQTRDAO>{

    @Inject
    SQTRFmsDTOValidator sqtrDtoValidator;
    @Inject
    SQTRDAO sqtrDAO;

    @Override
	@PostConstruct
	protected void init() {
		setSQLDao(sqtrDAO);
	}

    @Override
	public SQTR upsert(SQTRFmsDTO dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		return sqtrDtoValidator.validateSQTRFmsDTO(dto, dataProvider);
	}

    @Transactional
	public void removeOrDeprecateNonUpdated(Long id, String loadDescription) { }
}
