package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.TranscriptDAO;
import org.alliancegenome.curation_api.interfaces.crud.TranscriptCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.Gff3TranscriptCDSExecutor;
import org.alliancegenome.curation_api.jobs.executors.Gff3TranscriptExecutor;
import org.alliancegenome.curation_api.jobs.executors.Gff3TranscriptExonExecutor;
import org.alliancegenome.curation_api.jobs.executors.Gff3TranscriptGeneExecutor;
import org.alliancegenome.curation_api.jobs.executors.Gff3TranscriptLocationExecutor;
import org.alliancegenome.curation_api.model.entities.Transcript;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.TranscriptService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class TranscriptCrudController extends BaseEntityCrudController<TranscriptService, Transcript, TranscriptDAO> implements TranscriptCrudInterface {

	@Inject TranscriptService transcriptService;

	@Inject Gff3TranscriptExecutor gff3TranscriptExecutor;
	@Inject Gff3TranscriptLocationExecutor gff3TranscriptLocationExecutor;
	@Inject Gff3TranscriptGeneExecutor gff3TranscriptGeneExecutor;
	@Inject Gff3TranscriptCDSExecutor gff3TranscriptCDSExecutor;
	@Inject Gff3TranscriptExonExecutor gff3TranscriptExonExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(transcriptService);
	}

	@Override
	public APIResponse updateTranscripts(String dataProvider, String assembly, List<Gff3DTO> gffData) {
		BulkLoadFileHistory history = new BulkLoadFileHistory();
		LoadHistoryResponce resp = (LoadHistoryResponce) gff3TranscriptExecutor.runLoadApi(dataProvider, assembly, gffData);
		history.setFailedRecords(history.getFailedRecords() + resp.getHistory().getFailedRecords());
		history.setCompletedRecords(history.getCompletedRecords() + resp.getHistory().getCompletedRecords());
		history.setTotalRecords(history.getTotalRecords() + resp.getHistory().getTotalRecords());
		resp = (LoadHistoryResponce) gff3TranscriptLocationExecutor.runLoadApi(dataProvider, assembly, gffData);
		history.setFailedRecords(history.getFailedRecords() + resp.getHistory().getFailedRecords());
		history.setCompletedRecords(history.getCompletedRecords() + resp.getHistory().getCompletedRecords());
		history.setTotalRecords(history.getTotalRecords() + resp.getHistory().getTotalRecords());
		resp = (LoadHistoryResponce) gff3TranscriptGeneExecutor.runLoadApi(dataProvider, assembly, gffData);
		history.setFailedRecords(history.getFailedRecords() + resp.getHistory().getFailedRecords());
		history.setCompletedRecords(history.getCompletedRecords() + resp.getHistory().getCompletedRecords());
		history.setTotalRecords(history.getTotalRecords() + resp.getHistory().getTotalRecords());
		resp = (LoadHistoryResponce) gff3TranscriptCDSExecutor.runLoadApi(dataProvider, assembly, gffData);
		history.setFailedRecords(history.getFailedRecords() + resp.getHistory().getFailedRecords());
		history.setCompletedRecords(history.getCompletedRecords() + resp.getHistory().getCompletedRecords());
		history.setTotalRecords(history.getTotalRecords() + resp.getHistory().getTotalRecords());
		resp = (LoadHistoryResponce) gff3TranscriptExonExecutor.runLoadApi(dataProvider, assembly, gffData);
		history.setFailedRecords(history.getFailedRecords() + resp.getHistory().getFailedRecords());
		history.setCompletedRecords(history.getCompletedRecords() + resp.getHistory().getCompletedRecords());
		history.setTotalRecords(history.getTotalRecords() + resp.getHistory().getTotalRecords());
		return new LoadHistoryResponce(history);
	}

	@Override
	public ObjectResponse<Transcript> getByIdentifier(String identifierString) {
		return transcriptService.getByIdentifier(identifierString);
	}

	@Override
	public ObjectResponse<Transcript> deleteByIdentifier(String identifierString) {
		return transcriptService.deleteByIdentifier(identifierString);
	}

}
