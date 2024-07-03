package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.CodingSequenceDAO;
import org.alliancegenome.curation_api.interfaces.crud.CodingSequenceCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.Gff3Executor;
import org.alliancegenome.curation_api.model.entities.CodingSequence;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.CodingSequenceService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CodingSequenceCrudController extends BaseEntityCrudController<CodingSequenceService, CodingSequence, CodingSequenceDAO> implements CodingSequenceCrudInterface {

	@Inject
	CodingSequenceService codingSequenceService;
	@Inject
	Gff3Executor gff3Executor;

	@Override
	@PostConstruct
	protected void init() {
		setService(codingSequenceService);
	}

	public APIResponse updateCodingSequences(String dataProvider, String assembly, List<Gff3DTO> gffData) {
		return gff3Executor.runLoadApi(dataProvider, assembly, gffData);
	}

	@Override
	public ObjectResponse<CodingSequence> getByIdentifier(String identifierString) {
		return codingSequenceService.getByIdentifier(identifierString);
	}

	@Override
	public ObjectResponse<CodingSequence> deleteByIdentifier(String identifierString) {
		return codingSequenceService.deleteByIdentifier(identifierString);
	}

}
