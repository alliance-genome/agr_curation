package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneInteraction;
import org.alliancegenome.curation_api.model.entities.PhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthology;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.alleleAssociations.AlleleGeneAssociationService;
import org.alliancegenome.curation_api.services.associations.constructAssociations.ConstructGenomicEntityAssociationService;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.orthology.GeneToGeneOrthologyService;
import org.alliancegenome.curation_api.services.validation.GeneValidator;
import org.alliancegenome.curation_api.services.validation.dto.GeneDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class GeneService extends SubmittedObjectCrudService<Gene, GeneDTO, GeneDAO> {

	@Inject GeneDAO geneDAO;
	@Inject GeneValidator geneValidator;
	@Inject GeneDTOValidator geneDtoValidator;
	@Inject DiseaseAnnotationService diseaseAnnotationService;
	@Inject PersonService personService;
	@Inject GeneToGeneOrthologyService orthologyService;
	@Inject AlleleGeneAssociationService alleleGeneAssociationService;
	@Inject ConstructGenomicEntityAssociationService constructGenomicEntityAssociationService;
	@Inject GeneInteractionService geneInteractionService;
	@Inject PhenotypeAnnotationService phenotypeAnnotationService;

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

	@Override
	public Gene upsert(GeneDTO dto) throws ObjectUpdateException {
		return upsert(dto, null);
	}

	@Override
	public Gene upsert(GeneDTO dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		return geneDtoValidator.validateGeneDTO(dto, dataProvider);
	}

	@Override
	@Transactional
	public ObjectResponse<Gene> deleteById(Long id) {
		removeOrDeprecateNonUpdated(id, "Gene DELETE API call");
		ObjectResponse<Gene> ret = new ObjectResponse<>();
		return ret;
	}

	@Override
	@Transactional
	public void removeOrDeprecateNonUpdated(Long id, String loadDescription) {
		Gene gene = geneDAO.find(id);
		if (gene != null) {
			if (geneDAO.hasReferencingDiseaseAnnotations(id) || geneDAO.hasReferencingPhenotypeAnnotations(id) ||
					geneDAO.hasReferencingOrthologyPairs(id) || geneDAO.hasReferencingInteractions(id) ||
					CollectionUtils.isNotEmpty(gene.getAlleleGeneAssociations()) ||
					CollectionUtils.isNotEmpty(gene.getConstructGenomicEntityAssociations())) {
				if (!gene.getObsolete()) {
					gene.setUpdatedBy(personService.fetchByUniqueIdOrCreate(loadDescription));
					gene.setDateUpdated(OffsetDateTime.now());
					gene.setObsolete(true);
					geneDAO.persist(gene);
				}
			} else {
				geneDAO.remove(id);
			}
		} else {
			log.error("Failed getting gene: " + id);
		}
	}

	public List<Long> getIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD")) {
			params.put(EntityFieldConstants.TAXON, dataProvider.canonicalTaxonCurie);
		}
		List<Long> ids = geneDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);

		return ids;
	}

}
