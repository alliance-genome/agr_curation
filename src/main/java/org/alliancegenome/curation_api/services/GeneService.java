package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthology;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.alleleAssociations.AlleleGeneAssociationService;
import org.alliancegenome.curation_api.services.base.BaseDTOCrudService;
import org.alliancegenome.curation_api.services.orthology.GeneToGeneOrthologyService;
import org.alliancegenome.curation_api.services.validation.GeneValidator;
import org.alliancegenome.curation_api.services.validation.dto.GeneDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

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
	@Inject
	GeneToGeneOrthologyService orthologyService;
	@Inject
	AlleleGeneAssociationService alleleGeneAssociationService;

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

	public Gene upsert(GeneDTO dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		return geneDtoValidator.validateGeneDTO(dto, dataProvider);
	}
	
	@Transactional
	public void removeOrDeprecateNonUpdated(String curie, String dataProviderName, String md5sum) {
		Gene gene = geneDAO.find(curie);
		String loadDescription = dataProviderName + " Gene bulk load (" + md5sum + ")";
		if (gene != null) {
			List<Long> referencingDAIds = geneDAO.findReferencingDiseaseAnnotations(curie);
			Boolean anyReferencingEntities = false;
			for (Long daId : referencingDAIds) {
				DiseaseAnnotation referencingDA = diseaseAnnotationService.deprecateOrDeleteAnnotationAndNotes(daId, false, loadDescription, true);
				if (referencingDA != null)
					anyReferencingEntities = true;
			}
			List<Long> referencingOrthologyPairs = geneDAO.findReferencingOrthologyPairs(curie);
			for (Long orthId : referencingOrthologyPairs) {
				GeneToGeneOrthology referencingOrthoPair = orthologyService.deprecateOrthologyPair(orthId, loadDescription);
				if (referencingOrthoPair != null)
					anyReferencingEntities = true;
			}
			if (CollectionUtils.isNotEmpty(gene.getAlleleGeneAssociations())) {
				for (AlleleGeneAssociation association : gene.getAlleleGeneAssociations()) {
					association = alleleGeneAssociationService.deprecateOrDeleteAssociation(association.getId(), false, loadDescription, true);
					if (association != null)
						anyReferencingEntities = true;
				}
			}
			
			if (anyReferencingEntities) {
				gene.setUpdatedBy(personService.fetchByUniqueIdOrCreate(loadDescription));
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
	}

	public List<String> getCuriesByDataProvider(BackendBulkDataProvider dataProvider) {
		List<String> curies;

		String sourceOrg = dataProvider.sourceOrganization;

		if( StringUtils.equals(sourceOrg, "RGD") ){
			curies = geneDAO.findAllCuriesByDataProvider(dataProvider.sourceOrganization, dataProvider.canonicalTaxonCurie);
		} else {
			curies = geneDAO.findAllCuriesByDataProvider(dataProvider.sourceOrganization);
		}

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
