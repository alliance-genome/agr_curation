package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.GeneExpressionAnnotationDAO;
import org.alliancegenome.curation_api.dao.GeneExpressionExperimentDAO;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneExpressionExperiment;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.ingest.dto.fms.CrossReferenceFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.ontology.MmoTermService;
import org.alliancegenome.curation_api.services.validation.dto.fms.CrossReferenceFmsDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class GeneExpressionExperimentService extends BaseEntityCrudService<GeneExpressionExperiment, GeneExpressionExperimentDAO> {

	@Inject GeneExpressionExperimentDAO geneExpressionExperimentDAO;
	@Inject GeneExpressionAnnotationDAO geneExpressionAnnotationDAO;
	@Inject GeneService geneService;
	@Inject MmoTermService mmoTermService;
	@Inject ReferenceService referenceService;
	@Inject OrganizationService organizationService;
	@Inject CrossReferenceFmsDTOValidator crossReferenceFmsDTOValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneExpressionExperimentDAO);
	}

	public List<Long> getExperimentIdsBySpecies(Species species) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, species.getDataProvider().getAbbreviation());
		if (StringUtils.equals(species.getDataProvider().getAbbreviation(), "RGD") || StringUtils.equals(species.getDataProvider().getAbbreviation(), "XB")) {
			params.put(EntityFieldConstants.EXP_EXPERIMENT_TAXON, species.getTaxon().getCurie());
		}
		return geneExpressionExperimentDAO.findIdsByParams(params);
	}

	@Transactional
	public GeneExpressionExperiment upsert(String experimentId, Set<String> geneExpressionAnnotationIds, Species species, Set<CrossReferenceFmsDTO> crossReferences) throws ValidationException {
		GeneExpressionExperiment geneExpressionExperiment;
		Set<GeneExpressionAnnotation> annotations;

		//	example of experimentId: Xenbase:XB-GENE-972235|AGRKB:101000000874667|MMO:0000658
		String[] definingFields = experimentId.split("\\|", 3);
		String geneId = definingFields[0];
		String referenceId = definingFields[1];
		String assayId = definingFields[2];
		SearchResponse<GeneExpressionExperiment> response = geneExpressionExperimentDAO.findByField("uniqueId", experimentId);
		if (response != null && response.getSingleResult() != null) {
			geneExpressionExperiment = response.getSingleResult();
		} else {
			geneExpressionExperiment = new GeneExpressionExperiment();
			geneExpressionExperiment.setUniqueId(experimentId);
		}
		Organization organization = organizationService.getByAbbr(species.getDataProvider().getAbbreviation()).getEntity();
		geneExpressionExperiment.setDataProvider(organization);
		geneExpressionExperiment.setEntityAssayed(geneService.findByIdentifierString(geneId));
		geneExpressionExperiment.setSingleReference(referenceService.getByCurie(referenceId).getEntity());
		geneExpressionExperiment.setExpressionAssayUsed(mmoTermService.findByCurie(assayId));
		geneExpressionExperiment.setInternal(false);
		geneExpressionExperiment.setObsolete(false);

		annotations = geneExpressionExperiment.getExpressionAnnotations();
		if (annotations == null) {
			annotations = new HashSet<>();
		}

		if (species.getDisplayName().equals("MGI") || species.getDisplayName().equals("WB")) {
			if (geneExpressionExperiment.getCrossReferences() != null) {
				geneExpressionExperiment.getCrossReferences().clear();
			} else {
				geneExpressionExperiment.setCrossReferences(new ArrayList<>());
			}
			Set<CrossReference> validatedCrossRefs = new HashSet<>();
			for (CrossReferenceFmsDTO crossRefDto : crossReferences) {
				ObjectResponse<List<CrossReference>> crossRefResponse = crossReferenceFmsDTOValidator.validateCrossReferenceFmsDTO(crossRefDto);
				if (crossRefResponse.hasErrors()) {
					response.addErrorMessage("cross_references", crossRefResponse.errorMessagesString());
					break;
				} else {
					validatedCrossRefs.addAll(crossRefResponse.getEntity());
				}
			}
			geneExpressionExperiment.getCrossReferences().addAll(validatedCrossRefs);
		}

		if (geneExpressionExperiment.getId() == null) {
			geneExpressionExperimentDAO.persist(geneExpressionExperiment);
		}
		for (String geneExpressionAnnotationId: geneExpressionAnnotationIds) {
			GeneExpressionAnnotation geneExpressionAnnotation = geneExpressionAnnotationDAO.findByField("uniqueId", geneExpressionAnnotationId).getSingleResult();
			geneExpressionAnnotation.setExpressionExperiment(geneExpressionExperiment);
			annotations.add(geneExpressionAnnotation);
		}
		geneExpressionExperiment.setExpressionAnnotations(annotations);

		return geneExpressionExperiment;
	}
}
