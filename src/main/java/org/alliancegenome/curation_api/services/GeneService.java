package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseDTOCrudService;
import org.alliancegenome.curation_api.services.validation.GeneValidator;
import org.alliancegenome.curation_api.services.validation.dto.GeneDTOValidator;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class GeneService extends BaseDTOCrudService<Gene, GeneDTO, GeneDAO> {

	@Inject
	GeneDAO geneDAO;
	@Inject
	GeneValidator geneValidator;
	@Inject
	GeneDTOValidator geneDtoValidator;
	@Inject
	DiseaseAnnotationService diseaseAnnotationService;
	@Inject
	PersonService personService;
	@Inject
	GeneSymbolSlotAnnotationDAO geneSymbolDAO;
	@Inject
	GeneFullNameSlotAnnotationDAO geneFullNameDAO;
	@Inject
	GeneSystematicNameSlotAnnotationDAO geneSystematicNameDAO;
	@Inject
	GeneSynonymSlotAnnotationDAO geneSynonymDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<Gene> update(Gene uiEntity) {
		Gene dbEntity = geneValidator.validateGeneUpdate(uiEntity);
		return new ObjectResponse<Gene>(dbEntity);
	}

	@Override
	@Transactional
	public ObjectResponse<Gene> create(Gene uiEntity) {
		Gene dbEntity = geneValidator.validateGeneCreate(uiEntity);
		return new ObjectResponse<Gene>(dbEntity);
	}

	public Gene upsert(GeneDTO dto) throws ObjectUpdateException {
		return geneDtoValidator.validateGeneDTO(dto);
	}

	@Transactional
	public void removeOrDeprecateNonUpdatedGenes(String speciesNames, List<String> geneCuriesBefore, List<String> geneCuriesAfter, String dataType) {
		log.debug("runLoad: After: " + speciesNames + " " + geneCuriesAfter.size());

		List<String> distinctAfter = geneCuriesAfter.stream().distinct().collect(Collectors.toList());
		log.debug("runLoad: Distinct: " + speciesNames + " " + distinctAfter.size());

		List<String> curiesToRemove = ListUtils.subtract(geneCuriesBefore, distinctAfter);
		log.debug("runLoad: Remove: " + speciesNames + " " + curiesToRemove.size());

		ProcessDisplayHelper ph = new ProcessDisplayHelper(1000);
		ph.startProcess("Deletion/deprecation of disease annotations linked to unloaded " + speciesNames + " genes", curiesToRemove.size());
		for (String curie : curiesToRemove) {
			Gene gene = geneDAO.find(curie);
			if (gene != null) {
				List<Long> referencingDAIds = geneDAO.findReferencingDiseaseAnnotations(curie);
				Boolean anyPublicReferencingDAs = false;
				for (Long daId : referencingDAIds) {
					Boolean daMadePublic = diseaseAnnotationService.deprecateOrDeleteAnnotationAndNotes(daId, false, "gene");
					if (daMadePublic)
						anyPublicReferencingDAs = true;
				}

				if (anyPublicReferencingDAs) {
					gene.setUpdatedBy(personService.fetchByUniqueIdOrCreate(dataType + " gene bulk upload"));
					gene.setDateUpdated(OffsetDateTime.now());
					gene.setObsolete(true);
					geneDAO.persist(gene);
				} else {
					deleteGeneSlotAnnotations(gene);
					geneDAO.remove(curie);
				}
			} else {
				log.error("Failed getting gene: " + curie);
			}
			ph.progressProcess();
		}
		ph.finishProcess();
	}

	public List<String> getCuriesBySpeciesName(String speciesName) {
		List<String> curies = geneDAO.findAllCuriesBySpeciesName(speciesName);
		curies.removeIf(Objects::isNull);
		return curies;
	}

	private void deleteGeneSlotAnnotations(Gene gene) {
		if (gene.getGeneSymbol() != null)
			geneSymbolDAO.remove(gene.getGeneSymbol().getId());

		if (gene.getGeneFullName() != null)
			geneFullNameDAO.remove(gene.getGeneFullName().getId());

		if (gene.getGeneSystematicName() != null)
			geneSystematicNameDAO.remove(gene.getGeneSystematicName().getId());

		if (CollectionUtils.isNotEmpty(gene.getGeneSynonyms()))
			gene.getGeneSynonyms().forEach(gs -> {
				geneSynonymDAO.remove(gs.getId());
			});
	}

}
