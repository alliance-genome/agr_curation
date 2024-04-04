package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.PhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.alleleAssociations.AlleleGeneAssociationService;
import org.alliancegenome.curation_api.services.associations.constructAssociations.ConstructGenomicEntityAssociationService;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.validation.AlleleValidator;
import org.alliancegenome.curation_api.services.validation.dto.AlleleDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AlleleService extends SubmittedObjectCrudService<Allele, AlleleDTO, AlleleDAO> {

	@Inject AlleleDAO alleleDAO;
	@Inject AlleleValidator alleleValidator;
	@Inject AlleleDTOValidator alleleDtoValidator;
	@Inject DiseaseAnnotationService diseaseAnnotationService;
	@Inject PersonService personService;
	@Inject AlleleGeneAssociationService alleleGeneAssociationService;
	@Inject ConstructGenomicEntityAssociationService constructGenomicEntityAssociationService;
	@Inject PhenotypeAnnotationService phenotypeAnnotationService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<Allele> update(Allele uiEntity) {
		Allele dbEntity = alleleValidator.validateAlleleUpdate(uiEntity, false);
		return new ObjectResponse<Allele>(dbEntity);
	}
	
	@Transactional
	public ObjectResponse<Allele> updateDetail(Allele uiEntity) {
		Allele dbEntity = alleleValidator.validateAlleleUpdate(uiEntity, true);
		return new ObjectResponse<Allele>(dbEntity);
	}
	
	@Override
	@Transactional
	public ObjectResponse<Allele> create(Allele uiEntity) {
		Allele dbEntity = alleleValidator.validateAlleleCreate(uiEntity);
		return new ObjectResponse<Allele>(dbEntity);
	}

	public Allele upsert(AlleleDTO dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		return alleleDtoValidator.validateAlleleDTO(dto, dataProvider);
	}
	
	@Override
	@Transactional
	public ObjectResponse<Allele> deleteById(Long id) {
		removeOrDeprecateNonUpdated(id, "Allele DELETE API call");
		ObjectResponse<Allele> ret = new ObjectResponse<>();
		return ret;
	}
	
	@Transactional
	public void removeOrDeprecateNonUpdated(Long id, String loadDescription) {
		Allele allele = alleleDAO.find(id);
		if (allele != null) {
			List<Long> referencingDAIds = alleleDAO.findReferencingDiseaseAnnotationIds(id);
			Boolean anyReferencingEntities = false;
			for (Long daId : referencingDAIds) {
				DiseaseAnnotation referencingDA = diseaseAnnotationService.deprecateOrDeleteAnnotationAndNotes(daId, false, loadDescription, true);
				if (referencingDA != null)
					anyReferencingEntities = true;
			}

			List<Long> referencingPAIds = alleleDAO.findReferencingPhenotypeAnnotations(id);
			for (Long paId : referencingPAIds) {
				PhenotypeAnnotation referencingPA = phenotypeAnnotationService.deprecateOrDeleteAnnotationAndNotes(paId, false, loadDescription, true);
				if (referencingPA != null)
					anyReferencingEntities = true;
			}
			if (CollectionUtils.isNotEmpty(allele.getAlleleGeneAssociations())) {
				for (AlleleGeneAssociation association : allele.getAlleleGeneAssociations()) {
					association = alleleGeneAssociationService.deprecateOrDeleteAssociation(association.getId(), false, loadDescription, true);
					if (association != null)
						anyReferencingEntities = true;
				}
			}
			if (CollectionUtils.isNotEmpty(allele.getConstructGenomicEntityAssociations())) {
				for (ConstructGenomicEntityAssociation association : allele.getConstructGenomicEntityAssociations()) {
					association = constructGenomicEntityAssociationService.deprecateOrDeleteAssociation(association.getId(), false, loadDescription, true);
					if (association != null)
						anyReferencingEntities = true;
				}
			}
			if (anyReferencingEntities) {
				if (!allele.getObsolete()) {
					allele.setUpdatedBy(personService.fetchByUniqueIdOrCreate(loadDescription));
					allele.setDateUpdated(OffsetDateTime.now());
					allele.setObsolete(true);
					alleleDAO.persist(allele);
				}
			} else {
				alleleDAO.remove(id);
			}
		} else {
			log.error("Failed getting allele: " + id);
		}
	}
	
	public List<Long> getIdsByDataProvider(String dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider);
		List<Long> ids = alleleDAO.findFilteredIds(params);
		ids.removeIf(Objects::isNull);
		return ids;
	}

}
