package org.alliancegenome.curation_api.services.fms;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.interfaces.fms.DataFileRESTInterface;
import org.alliancegenome.curation_api.model.fms.DataFile;

import si.mazi.rescu.RestProxyFactory;

@ApplicationScoped
public class DataFileService {

	private DataFileRESTInterface api = RestProxyFactory.createProxy(DataFileRESTInterface.class, "https://fms.alliancegenome.org/api");

	public List<DataFile> getDataFiles(String dataType, String dataSubType) {
		return api.getDataTypeSubTypeFiles(dataType, dataSubType, true);
	}

}
