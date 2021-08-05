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
		ph.startProcess("BGI Gene Update", alleleData.getData().size());
		for(AlleleDTO allele: alleleData.getData()) {
			params.put("curie", allele.getPrimaryId());
			List<Allele> alleles = alleleSerice.findByParams(params);
			if(alleles == null || alleles.size() == 0) {
				Allele a = new Allele();
				a.setCurie(allele.getPrimaryId());
				a.setSymbol(allele.getSymbol());
				a.setDescription(allele.getDescription());
				a.setTaxon(allele.getTaxonId());
				alleleSerice.create(a);
			} else {
				Allele a = alleles.get(0);
				if(a.getCurie().equals(allele.getPrimaryId())) {
					a.setSymbol(allele.getSymbol());
					a.setName(allele.getDescription());
					a.setTaxon(allele.getTaxonId());
					alleleSerice.update(a);
				}
			}
			
			ph.progressProcess();
		}
		ph.finishProcess();
		return "OK";

	}


}
