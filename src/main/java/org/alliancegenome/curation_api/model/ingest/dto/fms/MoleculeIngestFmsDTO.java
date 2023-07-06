package org.alliancegenome.curation_api.model.ingest.dto.fms;

import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;

@Data
public class MoleculeIngestFmsDTO extends BaseDTO {

	private MetaDataFmsDTO metaData;
	private List<MoleculeFmsDTO> data;
}
