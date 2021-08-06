package org.alliancegenome.curation_api.bulk.controllers;

import java.util.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.interfaces.bulk.AlleleBulkRESTInterface;
import org.alliancegenome.curation_api.model.dto.json.*;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AlleleBulkController implements AlleleBulkRESTInterface {

	@Inject AlleleService alleleSerice;
	
	@Override
	public String updateAlleles(AlleleMetaDataDTO alleleData) {

		Map<String, Object> params = new HashMap<String, Object>();
		ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
		ph.startProcess("Allele Update", alleleData.getData().size());
		for(AlleleDTO allele: alleleData.getData()) {
			params.put("curie", allele.getPrimaryId());
			Allele dbAllele = alleleSerice.get(allele.getPrimaryId());
			if(dbAllele == null) {
				dbAllele = new Allele();
				dbAllele.setCurie(allele.getPrimaryId());
				dbAllele.setSymbol(allele.getSymbol());
				dbAllele.setDescription(allele.getDescription());
				dbAllele.setTaxon(allele.getTaxonId());
				alleleSerice.create(dbAllele);
			} else {
				if(dbAllele.getCurie().equals(allele.getPrimaryId())) {
					dbAllele.setSymbol(allele.getSymbol());
					dbAllele.setDescription(allele.getDescription());
					dbAllele.setTaxon(allele.getTaxonId());
					alleleSerice.update(dbAllele);
				}
			}
			
			ph.progressProcess();
		}
		ph.finishProcess();
		return "OK";

	}


}
