package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseDTOCrudService;
import org.alliancegenome.curation_api.services.validation.GeneValidator;
import org.alliancegenome.curation_api.services.validation.dto.GeneDTOValidator;
import org.apache.commons.collections4.ListUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class GeneService extends BaseDTOCrudService<Gene, GeneDTO, GeneDAO> {

	@Inject GeneDAO geneDAO;
	@Inject GeneValidator geneValidator;
	@Inject GeneDTOValidator geneDtoValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<Gene> update(Gene uiEntity) {
		Gene dbEntity = geneValidator.validateAnnotation(uiEntity);
		return new ObjectResponse<Gene>(geneDAO.persist(dbEntity));
	}

	@Transactional
	public Gene getByIdOrCurie(String id) {
		Gene gene = geneDAO.getByIdOrCurie(id);
		if (gene != null) {
			gene.getSynonyms().size();
			gene.getSecondaryIdentifiers().size();
		}
		return gene;
	}

	@Transactional
	public Gene upsert(GeneDTO dto) throws ObjectUpdateException {
		Gene gene = geneDtoValidator.validateGeneDTO(dto);
		if (gene == null) return null;
		
		return geneDAO.persist(gene);
	}
	
	public void removeNonUpdatedGenes(String taxonIds, List<String> geneCuriesBefore, List<String> geneCuriesAfter) {
		log.debug("runLoad: After: " + taxonIds + " " + geneCuriesAfter.size());

		List<String> distinctAfter = geneCuriesAfter.stream().distinct().collect(Collectors.toList());
		log.debug("runLoad: Distinct: " + taxonIds + " " + distinctAfter.size());

		List<String> curiesToRemove = ListUtils.subtract(geneCuriesBefore, distinctAfter);
		log.debug("runLoad: Remove: " + taxonIds + " " + curiesToRemove.size());

		log.info("Deleting disease annotations linked to " + curiesToRemove.size() + " unloaded genes");
		List<String> foundGeneCuries = new ArrayList<String>();
		for (String curie : curiesToRemove) {
			Gene gene = geneDAO.find(curie);
			if (gene != null) {
				foundGeneCuries.add(curie);
			} else {
				log.error("Failed getting gene: " + curie);
			}
		}
		geneDAO.deleteReferencingDiseaseAnnotations(foundGeneCuries);
		foundGeneCuries.forEach(curie -> {delete(curie);});
		log.info("Deletion of disease annotations linked to unloaded genes finished");
	}
	
	public List<String> getCuriesByTaxonId(String taxonId) {
		List<String> curies = geneDAO.findAllCuriesByTaxon(taxonId);
		curies.removeIf(Objects::isNull);
		return curies;
	}

}
