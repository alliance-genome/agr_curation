package org.alliancegenome.curation_api.model.fms;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SnapShot {
	private List<DataFile> dataFiles;
}
