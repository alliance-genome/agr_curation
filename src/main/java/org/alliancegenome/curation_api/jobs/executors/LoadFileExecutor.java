package org.alliancegenome.curation_api.jobs.executors;

import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LoadFileExecutor {

    @Inject ObjectMapper mapper;
    @Inject BulkLoadFileDAO bulkLoadFileDAO;
}
