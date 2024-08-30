package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.ExonDAO;
import org.alliancegenome.curation_api.interfaces.crud.ExonCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.Gff3ExonExecutor;
import org.alliancegenome.curation_api.jobs.executors.Gff3ExonLocationExecutor;
import org.alliancegenome.curation_api.jobs.executors.Gff3TranscriptExonExecutor;
import org.alliancegenome.curation_api.model.entities.Exon;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ExonService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ExonCrudController extends BaseEntityCrudController<ExonService, Exon, ExonDAO> implements ExonCrudInterface {

	@Inject ExonService exonService;
	@Inject Gff3ExonExecutor gff3ExonExecutor;
	@Inject Gff3ExonLocationExecutor gff3ExonLocationExecutor;
	@Inject Gff3TranscriptExonExecutor gff3TranscriptExonExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(exonService);
	}

	@Override
	public APIResponse updateExons(String dataProvider, String assembly, List<Gff3DTO> gffData) {
		BulkLoadFileHistory history = new BulkLoadFileHistory();
		LoadHistoryResponce resp = (LoadHistoryResponce) gff3ExonExecutor.runLoadApi(dataProvider, assembly, gffData);
		history.setFailedRecords(history.getFailedRecords() + resp.getHistory().getFailedRecords());
		history.setCompletedRecords(history.getCompletedRecords() + resp.getHistory().getCompletedRecords());
		history.setTotalRecords(history.getTotalRecords() + resp.getHistory().getTotalRecords());
		resp = (LoadHistoryResponce) gff3ExonLocationExecutor.runLoadApi(dataProvider, assembly, gffData);
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
	public ObjectResponse<Exon> getByIdentifier(String identifierString) {
		return exonService.getByIdentifier(identifierString);
	}

	@Override
	public ObjectResponse<Exon> deleteByIdentifier(String identifierString) {
		return exonService.deleteByIdentifier(identifierString);
	}

}
